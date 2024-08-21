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
import { printSourceFile } from "../helpers/source";
import { generateElementId } from "../../../utilities/utilities";
import { getPropertyName } from "../helpers/node";
import { StringBuilderClass } from "../../../utilities/stringBuilder";
import { PolicyTemplateDescriptor, PolicyTemplateEventDescriptor, PolicyTemplateScheduleDescriptor, PolicyWorkflowInfo, PolicyAttribute, PolicyElement } from "../../decorators";
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
			templateVersion: "v1",
			variables: {},
			elements: {}
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
			type: getEventType(getPropertyName(methodNode.name)),
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
	 * @param {string} eventType - the event type / method node name.
	 * @returns {string} The event type with proper casing.
	 */
	function getEventType(eventType: string): string {
		switch (eventType?.toLowerCase()) {
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
					case "templateVersion": {
						policyTemplateInfo.templateVersion = (<ts.StringLiteral>(property.initializer)).text;
						break;
					}
					case "variables": {
						buildPolicyVariables(policyTemplateInfo, <ts.ObjectLiteralExpression>property.initializer);
						break;
					}
					case "elements": {
						buildPolicyElements(policyTemplateInfo, <ts.ObjectLiteralExpression>property.initializer);
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
					default: {
						throw new Error(`PolicyTemplate attribute '${propName}' is not supported.`);
					}
				}
			});
		}
	}

	function buildPolicyElements(policyInfo: PolicyTemplateDescriptor, objLiteralNode: ts.ObjectLiteralExpression): void {
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			policyInfo.elements[name] = buildPolicyElement(property.initializer as ts.ObjectLiteralExpression);
		});
	}

	function buildPolicyElement(objLiteralNode: ts.ObjectLiteralExpression): PolicyElement {
		let elementInfo: PolicyElement = { type: "", events: undefined, schedule: undefined };

		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			switch (name) {
				case "type": {
					elementInfo.type = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "events": {
					buildPolicyElementEvents(elementInfo, property.initializer as ts.ObjectLiteralExpression);
					break;
				}
				case "schedule": {
					buildPolicyElementSchedule(elementInfo, property.initializer as ts.ObjectLiteralExpression);
					break;
				}
			}
		});

		return elementInfo;
	}

	function buildPolicyElementSchedule(elementInfo: PolicyElement, objLiteralNode: ts.ObjectLiteralExpression) {
		elementInfo.schedule = { periode: "", when: "", timezone: "" };
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			switch (name) {
				case "periode": {
					elementInfo.schedule.periode = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "when": {
					elementInfo.schedule.when = (<ts.StringLiteral>property.initializer).text;
					break;
				}
				case "timezone": {
					elementInfo.schedule.timezone = (<ts.StringLiteral>property.initializer).text;
					break;
				}
			}
		})
	}

	function buildPolicyElementEvents(elementInfo: PolicyElement, objLiteralNode: ts.ObjectLiteralExpression) {
		let events = {};
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);

			if (property.initializer.kind === ts.SyntaxKind.StringLiteral) {
				events[name] = (<ts.StringLiteral>property.initializer).text;
			} else if (property.initializer.kind === ts.SyntaxKind.ObjectLiteralExpression) {
				events[name] = buildWorkflowInfo(property.initializer as ts.ObjectLiteralExpression);
			}
		});
		elementInfo.events = events;
	}

	function buildWorkflowInfo(objLiteralNode: ts.ObjectLiteralExpression): PolicyWorkflowInfo {
		let workflowInfo: PolicyWorkflowInfo = { workflowId: "", bindings: {} };
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			if (name === "workflowId") {
				workflowInfo.workflowId = (<ts.StringLiteral>property.initializer).text;
			} else if (name === "bindings") {
				(property.initializer as ts.ObjectLiteralExpression).properties.forEach((bindings: ts.PropertyAssignment) => {
					let bindingName = getPropertyName(bindings.name);
					workflowInfo.bindings[bindingName] = { type: "", variable: "" };
					(bindings.initializer as ts.ObjectLiteralExpression).properties.forEach(((binding: ts.PropertyAssignment) => {
						let key = getPropertyName(binding.name);
						workflowInfo.bindings[bindingName][key] = (<ts.StringLiteral>binding.initializer).text;
					}))
				});
			}
		})
		return workflowInfo;
	}


	function buildPolicyVariables(policyInfo: PolicyTemplateDescriptor, objLiteralNode: ts.ObjectLiteralExpression): void {
		objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			let variableInfo: PolicyAttribute = { type: "Any", value: null, description: null, configId: null, configKey: null };
			switch (property.initializer.kind) {
				case ts.SyntaxKind.StringLiteral: {
					variableInfo.value = (<ts.StringLiteral>property.initializer).text;
					variableInfo.type = "string";
					break;
				}
				case ts.SyntaxKind.ObjectLiteralExpression: {
					variableInfo = getVariableInfo(property.initializer as ts.ObjectLiteralExpression);
					break;
				}
			};
			let configAtt = policyInfo.variables[name] as PolicyAttribute;
			if (configAtt) {
				configAtt.type = variableInfo.type != null ? variableInfo.type : configAtt.type;
				configAtt.value = variableInfo.value != null ? variableInfo.value : configAtt.value;
				configAtt.description = variableInfo.description != null ? variableInfo.description : configAtt.description;
				configAtt.configId = variableInfo.configId != null ? variableInfo.configId : configAtt.configId;
				configAtt.configKey = variableInfo.configKey != null ? variableInfo.configKey : configAtt.configKey;
			}
			else {
				policyInfo.variables[name] = variableInfo;
			}
		});
	}

	function getVariableInfo(exp: ts.ObjectLiteralExpression): PolicyAttribute {
		let result: PolicyAttribute = { type: "Any", value: null, description: null, configId: null, configKey: null };
		exp.properties.forEach((property: ts.PropertyAssignment) => {
			let name = getPropertyName(property.name);
			if (name == "type") {
				result.type = (<ts.StringLiteral>property.initializer).text;
			} else if (name == "configId") {
				result.configId = (<ts.StringLiteral>property.initializer).text;
			} else if (name == "configKey") {
				result.configKey = (<ts.StringLiteral>property.initializer).text;
			} else if (name == "value") {
				result.value = getValue(property.initializer);
			} else if (name == "description") {
				result.description = (<ts.StringLiteral>property.initializer).text;
			}
		});
		return result;
	}

	/**
	 * @brief   Returns the value of a given Node.
	 *
	 * @details This function is called when we want to retrieve the value of a variable. We need to first
	 *          extract the data in a way that can be processed later on in `printConfigXml`
	 *          Types of data we can process:
	 *              1. Strings: NoSubstitutionTemplateLiteral, StringLiteral
	 *                  * Examples:  'example', "example", `example`
	 *                  * Limitations: `test${i}` or similar do not work
	 *              2. Numeric: NumericLiteral
	 *                  * Examples:  1
	 *              3. Booleans: TrueKeyword, FalseKeyword
	 *                  * Examples: true, false
	 *              4. Arrays: ArrayLiteralExpression
	 *                  * An array of the other types only with the same limitations
	 *              5. Objects: ObjectLiteralExpression
	 *                  * Object with values of the other type only
	 *                  * Limitations: Does not support adding a CompositeType inside of another CompositeType
	 * @param literal
	 */
	function getValue(literal: ts.Node) {
		if (literal) {
			switch (literal.kind) {
				case ts.SyntaxKind.NoSubstitutionTemplateLiteral:
				case ts.SyntaxKind.StringLiteral: {
					return (<ts.StringLiteral>(literal)).text;
				}
				case ts.SyntaxKind.NumericLiteral: {
					return parseInt((<ts.NumericLiteral>literal).text);
				}
				case ts.SyntaxKind.TrueKeyword: {
					return true;
				}
				case ts.SyntaxKind.FalseKeyword: {
					return false;
				}
				case ts.SyntaxKind.ArrayLiteralExpression: {
					return (<ts.ArrayLiteralExpression>literal).elements.map(element => getValue(element)).filter(element => element !== null);
				}
				case ts.SyntaxKind.ObjectLiteralExpression: {
					const resultingObjectLiteral = {};
					(<ts.ObjectLiteralExpression>literal).properties.forEach((property: ts.PropertyAssignment) => {
						const key = property.getChildAt(0).getText()
						const value = getValue(property.initializer);
						if (value !== null)
							resultingObjectLiteral[key] = value;
					});

					return resultingObjectLiteral;
				}
				default: {
					return null;
				}
			}
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
				case "element": {
					mergeElement(node);
					break;
				}
				case "text":
				case "cdata": {
					stringBuilder.append(node.toString().trim());
					break;
				}
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
			else if (ele?.children?.length) {
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
	 * This precedes the merging of the policy template descriptor into the XML template.
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
		if (policyTemplate.templateVersion === "v1") {
			if (policyTemplate.schedule) {
				buildScheduledItem(policyTemplate.schedule);
			}
			else {
				printSdkItem(policyTemplate.type || "AMQP:Subscription");
			}
		} else {
			if (policyTemplate.variables) {
				buildVariables(policyTemplate.variables);
			}
			if (policyTemplate.elements) {
				printElements(policyTemplate.elements);
			}
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
				stringBuilder.append(`<event type="${getEventType(event.type)}" kind="trigger" active="false">`).appendLine();
				stringBuilder.indent();
				stringBuilder.append(`<script encoded="false"><![CDATA[${event.sourceText}]]></script>`).appendLine();
				stringBuilder.unindent();
				stringBuilder.append(`</event>`).appendLine();
			});
		}

		function printElements(elements: Record<string, PolicyElement>) {
			Object.keys(elements).forEach(elementName => {
				printElement(elementName, elements[elementName]);
			})
		}

		function printElement(name: string, element: PolicyElement) {
			if (element.type === "Periodic Event") {
				printPeriodicEvent(name, element.events, element.schedule);
			} else {
				printOtherEvents(name, element.type, element.events);
			}
		}

		function printPeriodicEvent(name: string, events: Record<string, string | PolicyWorkflowInfo>, schedule: PolicyTemplateScheduleDescriptor) {
			stringBuilder.append(`<item`
				+ ` tag="${name}"`
				+ ` type="0"`
				+ ` periode="${schedule.periode}"`
				+ ` when="${schedule.when}"`
				+ ` timezone="${schedule.timezone}"`
				+ `>`).appendLine();
			if (events) {
				stringBuilder.indent();
				printEventsForV2(events);
				stringBuilder.unindent();
			}
			stringBuilder.append(`</item>`).appendLine();
		}

		function printOtherEvents(name: string, type: string, events: Record<string, string | PolicyWorkflowInfo>) {
			stringBuilder.append(`<item`
				+ ` tag="${name}"`
				+ ` type="0"`
				+ ` sdkType="${type}"`
				+ `>`).appendLine();
			if (events) {
				stringBuilder.indent();
				printEventsForV2(events);
				stringBuilder.unindent();
			}
			stringBuilder.append(`</item>`).appendLine();
		}

		function printEventsForV2(events: Record<string, string | PolicyWorkflowInfo>) {
			Object.keys(events).forEach(event => {
				stringBuilder.append(`<event type="${event}" kind="trigger" active="false">`).appendLine();
				stringBuilder.indent();
				if (typeof events[event] === "string") {
					const eventType = getEventType(events[event] as string);
					const foundEventForType = policyTemplate.events.find(e => getEventType(e.type) === eventType);
					if (!foundEventForType) {
						console.warn(`Could not find event with type '${events[event]}' in: ${JSON.stringify(policyTemplate.events, null, 4)}`)
					}
					stringBuilder.append(`<script encoded="false"><![CDATA[${foundEventForType?.sourceText || ''}]]></script>`).appendLine();
				} else {
					printWorkflowInfo(events[event] as PolicyWorkflowInfo);
				}
				stringBuilder.unindent();
				stringBuilder.append(`</event>`).appendLine();
			})
		}

		function printWorkflowInfo(workflowInfo: PolicyWorkflowInfo) {
			stringBuilder.append(`<workflow id="${workflowInfo.workflowId}" >`);
			if (workflowInfo.bindings) {
				stringBuilder.indent();
				stringBuilder.append(`<in-bindings >`);
				stringBuilder.indent();
				Object.keys(workflowInfo.bindings).forEach(name => {
					stringBuilder.append(`<bind name="${name}"`
						+ ` type = "${workflowInfo.bindings[name].type}"`
						+ ` export-name="${workflowInfo.bindings[name].variable}" />`);
				})
				stringBuilder.unindent();
				stringBuilder.append(`</in-bindings>`);
				stringBuilder.unindent();
			}
			stringBuilder.append(`</workflow>`);
		}

		function buildVariables(variables: Record<string, PolicyAttribute>) {
			Object.keys(variables).forEach(name => {
				buildVariable(name, variables[name]);
			});
		}

		function buildVariable(name: string, variable: PolicyAttribute) {
			stringBuilder.append(`<attribute`
				+ ` type="${variable.type}"`
				+ ` name="${name}"`
				+ ` read-only="false"`);
			if (variable.configId !== null) {
				if (variable.configKey === null || !variable.configKey) {
					throw new Error(`Configuration Key is not set for variable: ${name}`);
				}
				stringBuilder.append(` conf-id="${variable.configId}"`
					+ ` conf-key="${variable.configKey}"`);
			}

			if ((variable.configId === null && variable.value !== null) || variable.description !== null) {
				stringBuilder.append(`>`).appendLine();
				stringBuilder.indent();
				if (variable.description !== null) {
					stringBuilder.append(`<description><![CDATA[${variable.description}]]></description>`);
				}
				if (variable.configId === null && variable.value !== null) {
					if (variable.value != null && variable.type.indexOf("Array/") === 0) {
						variable.value = printAttributeArrayValue(variable.value, variable.type);
					}

					if (variable.value != null && variable.type.indexOf("CompositeType(") === 0) {
						variable.value = printAttributeCompositeValue(variable.value, variable.type);
					}
					else {
						stringBuilder.append(`<value encoded="n"><![CDATA[${variable.value}]]></value>`).appendLine();
					}
				}
				stringBuilder.appendLine();
				stringBuilder.unindent();
				stringBuilder.append(`</attribute>`).appendLine();
			} else {
				stringBuilder.append(` />`).appendLine();
			}
		}

		/**
		 * @brief Prints out arrays
		 *
		 * @details THIS PRINTS THE ARRAYS IN vRO7 FORMAT
		 *          vRO8 is backwards compatible, but future versions may not be.
		 *          Array in vRO8
		 *          [16:string#123312321,16:string#123132312,16:string#qffwfqwfw]
		 *
		 *          Array in vRO7
		 *          #{#field4:string#test#;#field4:string#test2#;#field4:string#test4#}#
		 *
		 * @param value
		 * @param type
		 */
		function printAttributeArrayValue(value: Array<any>, type: string) {
			type = type.replace("Array/", "");
			let output = "#{";

			value.forEach(element => {
				output += "#" + type + "#" + element + "#;";
			});

			output = output.slice(0, -1) + "}#";
			return output;
		}

		/**
		 * @brief   Prints out composite type values.
		 *
		 * @details THIS PRINTS THE COMPOSITE TYPE IN vRO7 FORMAT
		 *          vRO8 is backwards compatible, but future versions may not be.
		 *          Composite type in vRO8: {5:13:field=string#Stefan
		 *              7:12:field_1=number#123.0
		 *          }
		 *
		 *          Composite type in vRO7:
		 *          #[#field#=#string#Stefan#+#field_1#=#number#123.0#]#
		 *          #[#field1#=#string#rabbit#+#field2#=#Array##{#string#Rebecca#;#string#Pedro#;#string#Peppa#}##]#
		 *
		 * @param   {any} compositeValue
		 * @param   {String} compositeType
		 * @private
		 */
		function printAttributeCompositeValue(compositeValue: any, compositeType: string) {
			let output = "#[";

			for (let [key, value] of Object.entries(compositeValue)) {
				const isArray = Array.isArray(value);

				if (isArray) {
					const valueType = getArrayTypeFromCompositeType(compositeType, key);

					if (valueType === null) {
						throw new Error(`Composite Type Array is in invalid format for ${key}!`);
					}

					value = `Array#${printAttributeArrayValue(value as [], valueType)}`;
				}
				else {
					value = `${typeof value}#${value}`;
				}

				output += `#${key}#=#${value}#+`;
			}

			// Cut the last +
			output = output.slice(0, -1) + "]#";

			return output;
		}

		/**
		 * @brief   Extracts the Array Type from the Composite Type
		 *
		 * @details Example CompositeType: CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest
		 *          If the key passed is field4 will extract `Array/string`
		 *
		 * @param   {String} compositeType
		 * @param   {String} key
		 *
		 * @private
		 */
		function getArrayTypeFromCompositeType(compositeType: string, key: string): string | null {
			const result = compositeType.match(new RegExp(`${key}:(Array\\/[^,)]+)`));

			return result === null ? null : result[1];
		}
	}
}
