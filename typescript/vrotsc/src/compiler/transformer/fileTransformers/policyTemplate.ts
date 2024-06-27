/*-
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import * as ts from "typescript";
import { FileDescriptor, FileTransformationContext, FileType, XmlNode, XmlElement } from "../../../types";
import { system } from "../../../system/system";
import { printElementInfo } from "../../elementInfo";
import { transformSourceFile } from "../scripts/scripts";
import { collectFactsBefore } from "../metaTransformers/facts";
import { transformShimsBefore, transformShims } from "../codeTransformers/shims";
import { remediateTypeScript } from "../codeTransformers/remediate";
import { transformModuleSystem } from "../codeTransformers/modules";
import { addHeaderComment, printSourceFile } from "../helpers/source";
import { generateElementId } from "../../../utilities/utilities";
import { getPropertyName } from "../helpers/node";
import { StringBuilderClass } from "../../../utilities/stringBuilder";
import { PolicyTemplateDescriptor, PolicyTemplateEventDescriptor, PolicyTemplateScheduleDescriptor } from "../../../decorators";
import { prepareHeaderEmitter } from "../codeTransformers/header";

const xmldoc: typeof import("xmldoc") = require("xmldoc");

/**
 * Transforms a policy template file.
 * @param {FileDescriptor} file - The file to transform.
 * @param {FileTransformationContext} context - The transformation context.
 * @returns {Function} The transform function.
 */
export function getPolicyTemplateTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	const policyTemplates: PolicyTemplateDescriptor[] = [];
	const eventSourceFiles: ts.SourceFile[] = [];

	sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration).forEach(classNode => {
		registerPolicyTemplateClass(classNode as ts.ClassDeclaration);
	});
	eventSourceFiles.forEach(sf => context.sourceFiles.push(sf));

	return function transform() {
		transpilePolicyEvents();

		policyTemplates.forEach(policyTemplateInfo => {
			const targetFilePath = system.changeFileExt(
				system.resolvePath(context.outputs.policyTemplates, policyTemplateInfo.path, policyTemplateInfo.name),
				"",
				[".pl.ts"]);

			const xmlTemplateFilePath = system.changeFileExt(file.filePath, ".xml");
			const xmlTemplate = system.fileExists(xmlTemplateFilePath) ? system.readFile(xmlTemplateFilePath).toString() : undefined;

			context.writeFile(`${targetFilePath}.xml`, xmlTemplate ? mergePolicyTemplateXml(policyTemplateInfo, xmlTemplate) : printPolicyTemplateXml(policyTemplateInfo));
			context.writeFile(`${targetFilePath}.element_info.xml`, printElementInfo({
				categoryPath: policyTemplateInfo.path.replace(/(\\|\/)/g, "."),
				name: policyTemplateInfo.name,
				type: "PolicyTemplate",
				id: policyTemplateInfo.id,
			}));
		});
	};

	/**
	 * Transpiles policy events.
	 * This function transforms the source code of each policy event and stores the transformed source text in the event descriptor.
	 */
	function transpilePolicyEvents(): void {
		const events = policyTemplates.reduce((events, pl) => events.concat(pl.events), <PolicyTemplateEventDescriptor[]>[]);
		eventSourceFiles.forEach((eventSourceFile, i) => {
			const [sourceText] = transformSourceFile(
				eventSourceFile,
				context,
				{
					before: [
						collectFactsBefore,
						transformShimsBefore,
					],
					after: [
						transformShims,
						remediateTypeScript,
						transformModuleSystem,
						prepareHeaderEmitter(context),
					],
				},
				file);
			events[i].sourceText = sourceText;
		});
	}


	/**
	 * This function extracts information from the class declaration and its decorators to create a policy template descriptor.
	 *
	 * This will be done for each class declaration that is decorated with the `@PolicyTemplate` decorator.
	 *
	 * It populates the `policyTemplates` array with the policy template descriptors.
	 *
	 * @TODO: This should be refactored to return the policy template descriptor instead of populating the `policyTemplates` array.
	 *
	 * @param {ts.ClassDeclaration} classNode - The class node.
	 */
	function registerPolicyTemplateClass(classNode: ts.ClassDeclaration): void {
		let policyTemplateInfo: PolicyTemplateDescriptor = {
			id: undefined,
			name: classNode.name.text,
			path: undefined,
			tag: undefined,
			type: "AMQP:Subscription",
			version: "1.0.0",
			events: [],
		};
		const decorators = ts.getDecorators(classNode);
		if (decorators?.length) {
			decorators.filter(decoratorNode => {
				const callExpNode = decoratorNode.expression as ts.CallExpression;
				if (callExpNode && callExpNode.expression.kind === ts.SyntaxKind.Identifier) {
					return (<ts.Identifier>callExpNode.expression).text === "PolicyTemplate";
				}
			}).forEach(decoratorNode => {
				populatePolicyTemplateInfoFromDecorator(policyTemplateInfo, <ts.CallExpression>decoratorNode.expression);
			});
		}

		classNode.members
			.filter(member => member.kind === ts.SyntaxKind.MethodDeclaration)
			.forEach((methodNode: ts.MethodDeclaration) => { registerPolicyTemplateItem(policyTemplateInfo, methodNode); });

		policyTemplateInfo.name = policyTemplateInfo.name || classNode.name.text;
		policyTemplateInfo.path = policyTemplateInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
		policyTemplateInfo.id = policyTemplateInfo.id || generateElementId(FileType.PolicyTemplate, `${policyTemplateInfo.path}/${policyTemplateInfo.name}`);

		policyTemplates.push(policyTemplateInfo);
	}

	/**
	 * This function extracts information from the method declaration to create a policy template event descriptor and a corresponding source file.
	 *
	 * @TODO: This should be refactored to return the policy template event descriptor instead of populating the `policyTemplateInfo.events` array and the `eventSourceFiles` array.
	 *
	 * @param {PolicyTemplateDescriptor} policyTemplateInfo - The policy template info.
	 * @param {ts.MethodDeclaration} methodNode - The method node.
	 */
	function registerPolicyTemplateItem(policyTemplateInfo: PolicyTemplateDescriptor, methodNode: ts.MethodDeclaration): void {
		const eventInfo: PolicyTemplateEventDescriptor = {
			type: getEventType(methodNode),
			sourceText: undefined,
		};

		const eventSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${eventInfo.type}.pl.ts`, [".pl.ts"]);
		const eventSourceText = printSourceFile(
			ts.factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== ts.SyntaxKind.ClassDeclaration),
					...createPolicyTemplateItemPrologueStatements(methodNode),
					...methodNode.body.statements
				]));
		const eventSourceFile = ts.createSourceFile(
			eventSourceFilePath,
			eventSourceText,
			ts.ScriptTarget.Latest,
			true);

		policyTemplateInfo.events.push(eventInfo);
		eventSourceFiles.push(eventSourceFile);
	}

	/**
	 * Determines the event type based on the method name.
	 *
	 * @param {ts.MethodDeclaration} methodNode - The method node.
	 * @returns {string} The event type.
	 */
	function getEventType(methodNode: ts.MethodDeclaration): string {
		let eventType = getPropertyName(methodNode.name);
		switch (eventType.toLowerCase()) {
			case "oninit":
				return "OnInit";
			case "onexit":
				return "OnExit";
			case "onexecute":
				return "OnExecute";
			case "onmessage":
				return "OnMessage";
			case "ontrap":
				return "OnTrap";
			case "ontrapall":
				return "OnTrapAll";
			case "onconnect":
				return "OnConnect";
			case "ondisconnect":
				return "OnDisconnect";
			default:
				throw new Error(`PolicyTemplate event type '${eventType}' is not supported.`);
		}
	}

	/**
	 * Creates prologue statements for a policy template item.
	 * This function creates variable declarations for the parameters of the method.
	 * The variable declarations are added to the beginning of the method body.
	 *
	 * @param {ts.MethodDeclaration} methodNode - The method node.
	 * @returns {ts.Statement[]} The statements.
	 */
	function createPolicyTemplateItemPrologueStatements(methodNode: ts.MethodDeclaration): ts.Statement[] {
		const statements: ts.Statement[] = [];

		if (methodNode.parameters.length) {
			const variableDeclarations: ts.VariableDeclaration[] = [];
			methodNode.parameters.forEach(paramNode => {
				const paramName = (<ts.Identifier>paramNode.name).text;
				variableDeclarations.push(ts.factory.createVariableDeclaration(
					paramName,
					undefined,
					paramNode.type
				));
			});

			if (variableDeclarations.length) {
				statements.push(ts.factory.createVariableStatement(
					[ts.factory.createModifier(ts.SyntaxKind.DeclareKeyword)],
					variableDeclarations));
			}
		}

		return statements;
	}

	/**
	 * This function processes a single decorator to fill in the policy template descriptor.
	 *
	 * @param {PolicyTemplateDescriptor} policyTemplateInfo - The policy template info.
	 * @param {ts.CallExpression} decoratorCallExp - The decorator call expression.
	 */
	function populatePolicyTemplateInfoFromDecorator(policyTemplateInfo: PolicyTemplateDescriptor, decoratorCallExp: ts.CallExpression): void {
		const objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
		if (objLiteralNode) {
			objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
				const propName = getPropertyName(property.name);
				switch (propName) {
					case "id": {
						policyTemplateInfo.id = (<ts.StringLiteral>property.initializer).text;
						break;
					}
					case "name": {
						policyTemplateInfo.name = (<ts.StringLiteral>property.initializer).text;
						break;
					}
					case "description": {
						policyTemplateInfo.description = (<ts.StringLiteral>property.initializer).text;
						break;
					}
					case "path": {
						policyTemplateInfo.path = (<ts.StringLiteral>property.initializer).text;
						break;
					}
					case "type": {
						policyTemplateInfo.type = (<ts.StringLiteral>property.initializer).text;
						break;
					}
					case "version": {
						policyTemplateInfo.version = (<ts.StringLiteral>(property.initializer)).text;
						break;
					}
					case "schedule": {
						policyTemplateInfo.schedule = {
							periode: undefined,
							when: undefined,
							timezone: undefined,
						};
						(<ts.ObjectLiteralExpression>property.initializer).properties
							.filter((property: ts.PropertyAssignment) => !!property.initializer && ts.isStringLiteral(property.initializer))
							.forEach((property: ts.PropertyAssignment) => {
								policyTemplateInfo.schedule[getPropertyName(property.name)] = (<ts.StringLiteral>(property.initializer)).text;
							});
						break;
					}
					default:
						throw new Error(`PolicyTemplate attribute '${propName}' is not supported.`);
				}
			});
		}
	}

	/**
	 * This function merges the information from the policy template descriptor into the XML template.
	 *
	 * It will create a new XML document from the XML template and then merge the policy template information into it.
	 *
	 * This is done in case where we have multiple policy templates in the same XML template.
	 *
	 * @param {PolicyTemplateDescriptor} policyTemplate - The policy template.
	 * @param {string} xmlTemplate - The XML template.
	 *
	 * @returns {string} The merged XML.
	 */
	function mergePolicyTemplateXml(policyTemplate: PolicyTemplateDescriptor, xmlTemplate: string): string {
		const xmlDoc = new xmldoc.XmlDocument(xmlTemplate);
		const stringBuilder = new StringBuilderClass();
		let scriptIndex = 0;
		let xmlLevel = 0;

		stringBuilder.append(`<?xml version="1.0" encoding="UTF-8"?>`).appendLine();
		mergeNode(xmlDoc);

		return stringBuilder.toString();

		function mergeNode(node: XmlNode): void {
			switch (node.type) {
				case "element":
					mergeElement(<XmlElement>node);
					break;
				case "text":
				case "cdata":
					stringBuilder.append(node.toString().trim());
					break;
			}
		}

		function mergeElement(ele: XmlElement): void {
			stringBuilder.append(`<${ele.name}`);
			for (let attName in ele.attr || {}) {
				if (xmlLevel === 0 && attName === "id") {
					stringBuilder.append(` ${attName}="${policyTemplate.id}"`);
				}
				else {
					stringBuilder.append(` ${attName}="${ele.attr[attName]}"`);
				}
			}
			stringBuilder.append(">");
			mergeChildren(ele);
			stringBuilder.append(`</${ele.name}>`);
		}

		function mergeChildren(ele: XmlElement): void {
			if (ele.name === "script") {
				stringBuilder.append(`<![CDATA[${policyTemplate.events[scriptIndex++].sourceText}]]>`);
			}
			else if (ele.name === "description" && xmlLevel === 1 && policyTemplate.description != null) {
				stringBuilder.append(`<![CDATA[${policyTemplate.description}]]>`);
			}
			else if (ele.children && ele.children.length) {
				xmlLevel++;
				(ele.children || []).forEach(childNode => {
					mergeNode(childNode);
				});
				xmlLevel--;
			}
			else if (ele.val != null) {
				stringBuilder.append(ele.val);
			}
		}
	}

	/**
	 * This function generates an XML representation of the policy template descriptor.
	 *
	 * This preceeds the merging of the policy template descriptor into the XML template.
	 *
	 * @param {PolicyTemplateDescriptor} policyTemplate - The policy template.
	 * @returns {string} The policy template XML.
	 */
	function printPolicyTemplateXml(policyTemplate: PolicyTemplateDescriptor): string {
		const stringBuilder = new StringBuilderClass("", "");
		stringBuilder.append(`<?xml version="1.0" encoding="utf-8" ?>`).appendLine();
		stringBuilder.append(`<dunes-policy`
			+ ` name="${policyTemplate.name}"`
			+ ` id="${policyTemplate.id}"`
			+ ` version="${policyTemplate.version}"`
			+ ` api-version="6.0.0"`
			+ ` type="0"`
			+ ` allowed-operations="vef"`
			+ `>`).appendLine();
		stringBuilder.indent();
		if (policyTemplate.description) {
			stringBuilder.append(`<description><![CDATA[${policyTemplate.description}]]></description>`).appendLine();
		}
		if (policyTemplate.schedule) {
			buildScheduledItem(policyTemplate.schedule);
		}
		else {
			printSdkItem(policyTemplate.type || "AMQP:Subscription");
		}
		stringBuilder.unindent();
		stringBuilder.append(`</dunes-policy>`).appendLine();
		return stringBuilder.toString();

		function buildScheduledItem(schedule: PolicyTemplateScheduleDescriptor): void {
			stringBuilder.append(`<item`
				+ ` tag="Schedule"`
				+ ` type="0"`
				+ ` periode="${schedule.periode}"`
				+ ` when="${schedule.when}"`
				+ ` timezone="${schedule.timezone}"`
				+ `>`).appendLine();
			stringBuilder.indent();
			printEvents(policyTemplate.events);
			stringBuilder.unindent();
			stringBuilder.append(`</item>`).appendLine();
		}

		function printSdkItem(sdkType: string): void {
			stringBuilder.append(`<item`
				+ ` tag="${sdkType.replace(":", " ")}"`
				+ ` type="0"`
				+ ` sdkType="${sdkType}"`
				+ `>`).appendLine();
			stringBuilder.indent();
			printEvents(policyTemplate.events);
			stringBuilder.unindent();
			stringBuilder.append(`</item>`).appendLine();
		}

		function printEvents(events: PolicyTemplateEventDescriptor[]): void {
			events.forEach(event => {
				stringBuilder.append(`<event type="${event.type}" kind="trigger" active="false">`).appendLine();
				stringBuilder.indent();
				stringBuilder.append(`<script encoded="false"><![CDATA[${event.sourceText}]]></script>`).appendLine();
				stringBuilder.unindent();
				stringBuilder.append(`</event>`).appendLine();
			});
		}
	}
}
