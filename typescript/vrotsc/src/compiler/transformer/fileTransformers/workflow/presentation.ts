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
import { WorkflowDescriptor, WorkflowParameter, WorkflowParameterType } from "../../../decorators";
import { FileTransformationContext, XmlElement, XmlNode } from "../../../../types";
import { StringBuilderClass } from "../../../../utilities/stringBuilder";
import { findItemByName } from "./helpers/findItemByName";
import { Graph } from "./decorators/helpers/graph";

const xmldoc: typeof import("xmldoc") = require("xmldoc");

const LARGEST_INT32 = 2147483647;

/**
 * This will print the workflow in XML format
 *
 * This is called when we are working with a `wf.ts` file
 */
export function printWorkflowXml(workflow: WorkflowDescriptor, context: FileTransformationContext): string {
	const stringBuilder = new StringBuilderClass("", "");
	stringBuilder.append(`<?xml version="1.0" encoding="utf-8" ?>`).appendLine();
	stringBuilder.append(`<workflow`
		+ ` xmlns="http://vmware.com/vco/workflow"`
		+ ` xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
		+ ` xsi:schemaLocation="http://vmware.com/vco/workflow http://vmware.com/vco/workflow/Workflow-v4.xsd"`
		+ ` root-name="item${findItemByName(workflow.items, workflow.rootItem) || "1"}"`
		+ ` object-name="workflow:name=generic"`
		+ ` id="${workflow.id}"`
		+ ` version="${workflow.version}"`
		+ ` api-version="6.0.0"`
		+ ` restartMode="1"`
		+ ` resumeFromFailedMode="0"`
		+ `>`).appendLine();
	stringBuilder.indent();
	stringBuilder.append(`<display-name><![CDATA[${workflow.name}]]></display-name>`).appendLine();
	if (workflow.description) {
		stringBuilder.append(`<description><![CDATA[${workflow.description}]]></description>`).appendLine();
	}

	let graph: Graph;

	try {
		graph = getGraph(workflow);
	} catch (e) {
		throw new Error(`Error while building graph for workflow "${workflow.name}". Please check the workflow for any missing target items.
                        Original Error: ${e.message}`);
	}

	stringBuilder.append(`<position x="${graph.getNodeSpacing() / 2}" y="${graph.getHeight() / 2}" />`).appendLine();
	buildParameters("input", workflow.parameters.filter(p => !p.isAttribute && p.parameterType & WorkflowParameterType.Input));
	buildParameters("output", workflow.parameters.filter(p => !p.isAttribute && p.parameterType & WorkflowParameterType.Output));
	buildAttributes(workflow.parameters.filter(p => p.isAttribute));

	const defaultEndNode = graph.getNode("item0");
	buildEndItem(defaultEndNode.x, defaultEndNode.y);

	workflow.items.forEach((item, i) => {
		const pos = i + 1;
		const node = graph.getNode(`item${pos}`);

		stringBuilder.appendContent(item.strategy.printItem(item, pos, node.x, node.y));
	});

	buildPresentation();
	stringBuilder.unindent();
	stringBuilder.append(`</workflow>`).appendLine();
	return stringBuilder.toString();

	function buildParameters(parentName: string, parameters: WorkflowParameter[]): void {
		if (parameters.length) {
			stringBuilder.append(`<${parentName}>`).appendLine();
			stringBuilder.indent();
			parameters.forEach(param => {
				if (!param.description) {
					stringBuilder.append(`<param name="${param.name}" type="${param.type}" />`).appendLine();
				} else {
					stringBuilder.append(`<param name="${param.name}" type="${param.type}">`).appendLine();
					stringBuilder.indent();
					stringBuilder.append(`<description><![CDATA[${param.description}]]></description>`).appendLine();
					stringBuilder.unindent();
					stringBuilder.append(`</param>`).appendLine();
				}
			});
			stringBuilder.unindent();
			stringBuilder.append(`</${parentName}>`).appendLine();
		}
	}

	function buildAttributes(attributes: WorkflowParameter[]): void {
		attributes.forEach(att => {
			stringBuilder.append(`<attrib name="${att.name}" type="${att.type}" read-only="false"`);
			buildBindAttribute(att);
			stringBuilder.append(` />`).appendLine();
		});
	}

	function buildBindAttribute(att: WorkflowParameter) {
		let value = "" + att.defaultValue;
		if (!att?.bind) {
			return;
		}
		const index = value.lastIndexOf("/");
		if (index == -1) {
			throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that this value is bound to a Configuration (Element) but its value "${value}" `
				+ `cannot be mapped to a Configuration Element path. No / separator in value. Expected format is "/Path/To/Config/variable".`);
		}
		if (index >= value.length - 1) {
			throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that this value is bound to a Configuration (Element) `
				+ `but its value "${value}" ends in / character. Expected format is "/Path/To/Config/variable".`);
		}
		const key = value.substring(index + 1).trim();
		const path = value.substring(0, index).trim();
		const id = context.configIdsMap[path];
		if (id === null) {
			throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that its value is bound to configuration element with path "${path}" and variable "${key}"`
				+ `, but a configuration element with path "${path}" cannot be found in project at that stage. If you believe it is indeed really part of the project, `
				+ `please try moving the file earlier alphabetically so it is processed earlier than the workflow that uses it. Currently available configuration elements are: `
				+ JSON.stringify(context.configIdsMap));
		}
		stringBuilder.append(` conf-id="${id}" `);
		stringBuilder.append(` conf-key="${key}" `);
	}

	function buildEndItem(x: number, y: number) {
		stringBuilder.append(`<workflow-item name="item0" type="end" end-mode="0">`).appendLine();
		stringBuilder.indent();
		stringBuilder.append(`<position x="${x}" y="${y}" />`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();
	}

	function buildPresentation(): void {
		if (workflow.presentation) {
			stringBuilder.append(workflow.presentation);
		}
		else {
			const inputParameters = workflow.parameters.filter(p => p.parameterType === WorkflowParameterType.Input);
			if (inputParameters === null) {
				return;
			}
			stringBuilder.append(`<presentation>`).appendLine();
			stringBuilder.indent();
			inputParameters.forEach(param => {
				stringBuilder.append(`<p-param name="${param.name}">`).appendLine();
				stringBuilder.indent();
				stringBuilder.append(`<desc><![CDATA[${param.title || param.name}]]></desc>`).appendLine();
				if (param.required) {
					stringBuilder.append(`<p-qual kind="static" name="mandatory" type="boolean"><![CDATA[true]]></p-qual>`).appendLine();
				}
				if (param.multiLine) {
					stringBuilder.append(`<p-qual kind="static" name="textInput" type="void" />`).appendLine();
				}
				if (param.minStringLength != null) {
					stringBuilder.append(`<p-qual kind="static" name="minStringLength" type="Number"><![CDATA[${param.minStringLength.toString()}]]></p-qual>`).appendLine();
				}
				if (param.maxStringLength != null) {
					stringBuilder.append(`<p-qual kind="static" name="maxStringLength" type="Number"><![CDATA[${param.maxStringLength.toString()}]]></p-qual>`).appendLine();
				}
				if (param.numberFormat != null) {
					stringBuilder.append(`<p-qual kind="static" name="numberFormat" type="String"><![CDATA[${param.numberFormat}]]></p-qual>`).appendLine();
				}
				if (param.defaultValue != null) {
					stringBuilder.append(`<p-qual kind="static" name="defaultValue" type="${param.type}"><![CDATA[${param.defaultValue}]]></p-qual>`).appendLine();
				}
				if (param.availableValues?.length) {
					const availableValuesToken = `#{${param.availableValues.map(v => `#${param.type}#${v}#`).join(";")}}#`;
					stringBuilder.append(`<p-qual kind="static" name="genericEnumeration" type="Array/${param.type}"><![CDATA[${availableValuesToken}]]></p-qual>`).appendLine();
				}
				stringBuilder.unindent();
				stringBuilder.append(`</p-param>`).appendLine();
			});

			stringBuilder.unindent();
			stringBuilder.append(`</presentation>`).appendLine();
		}
	}
}

export function mergeWorkflowXml(workflow: WorkflowDescriptor, xmlTemplate: string): string {
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
				stringBuilder.append(` ${attName}="${workflow.id}"`);
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
			stringBuilder.append(`<![CDATA[${workflow.items[scriptIndex++].sourceText}]]>`);
		}
		else if (ele.name === "display-name" && xmlLevel === 1) {
			stringBuilder.append(`<![CDATA[${workflow.name}]]>`);
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

export function printPolyglotCode(moduleName: string, methodName: string, input: string[], output: string[]): string {
	let result = "";
	result += `//AUTOGENERATED POLYGLOT INVOCATION ---- DON'T TOUCH THIS SECTION CODE\n`;
	result += `System.log("Starting execution of polyglot decorator")\n`;
	result += `var polyglot_module = System.getModule("${moduleName}");`;
	result += `var polyglot_result = polyglot_module["${methodName}"](${buildPolyglotInputArgs(input)});\n`;
	result += `${buildOutput(output)} = polyglot_result;\n`;
	result += `System.log("Finish execution of polyglot decorator")\n`;
	result += `System.log("Starting execution of action code")\n`;

	return result;
}

export function buildPolyglotInputArgs(input: string[]): string {
	return input.join(",");
}

function buildOutput(output: string[]): string {
	if (output == undefined || output.length == 0) {
		throw new Error("Polyglot decorator require an @Out parameter");
	}
	return output[0];
}

/**
 * Returns the graph for all the nodes.
 *
 * Will insert the `rootItem` by default as a start node an search for other start nodes
 *
 * Will insert the default `end` node as well as the others require it but it's not an actual workflow item
 */
function getGraph(workflow: WorkflowDescriptor) {
	// start node
	const startNodes = [`item${findItemByName(workflow.items, workflow.rootItem) || "1"}`];
	const nodes = workflow.items.map((item, i) => {
		switch (item.strategy.getCanvasType()) {
			case "error-handler":
				startNodes.push(`item${i + 1}`);
				break;
			default:
				break;
		}

		return item.strategy.getGraphNode(item, i + 1);
	});
	// end node
	nodes.push({ name: "item0", targets: [] });

	const nodeSpacing = 100;
	const height = 200;
	const width = LARGEST_INT32;
	return new Graph(nodes, startNodes, nodeSpacing, height, width);
}
