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
import { CanvasItemPolymorphicBagForDecision, ITEM_ENUM_TO_TYPE, WorkflowDescriptor, WorkflowItemDescriptor, WorkflowItemType, WorkflowParameter, WorkflowParameterType } from "../../../../decorators";
import { FileTransformationContext, XmlElement, XmlNode } from "../../../../types";
import { StringBuilderClass } from "../../../../utilities/stringBuilder";

const xmldoc: typeof import("xmldoc") = require("xmldoc");

/**
 * This function will find the index of the item with the given name, or return 0 if not found
 *
 * The index is incremented by 1, as the first item in the workflow is always the end item
 *
 * @param items The list of items to search
 * @param name The name of the item to find
 * @returns The index of the item with the given name, or 0 if not found
 */
function findItemByName(items: WorkflowItemDescriptor[], name: string): number {
	const index = items.findIndex(item => item.name === name);
	return index === -1 ? 0 : index + 1;;
}

/**
 * This will print the workflow in XML format
 *
 * This is called when we are working with a `wf.ts` file
 */
export function printWorkflowXml(workflow: WorkflowDescriptor, context: FileTransformationContext): string {
	console.log(`Printing workflow ${workflow.name}...`);
	console.log(workflow);
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
	stringBuilder.append(`<position x="105" y="45.90909090909091" />`).appendLine();
	buildParameters("input", workflow.parameters.filter(p => !p.isAttribute && p.parameterType & WorkflowParameterType.Input));
	buildParameters("output", workflow.parameters.filter(p => !p.isAttribute && p.parameterType & WorkflowParameterType.Output));
	buildAttributes(workflow.parameters.filter(p => p.isAttribute));
	buildEndItem();
	workflow.items.forEach((item, i, items) => buildItem(item, i + 1, items));
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
		if (att.bind) {
			const index = value.lastIndexOf("/");
			if (index == -1) {
				throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that this value is bound to a Configuration (Elemene) but its value "${value}" `
					+ `cannot be mapped to a Configuraiton Element path. No / separator in value. Expected formaat is "/Path/To/Confif/variable".`);
			}
			if (index >= value.length - 1) {
				throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that this value is bound to a Configuration (Elemene) `
					+ `but its value "${value}" ends in / character. Expected formaat is "/Path/To/Confif/variable".`);
			}
			const key = value.substring(index + 1).trim();
			const path = value.substring(0, index).trim();
			const id = context.configIdsMap[path];
			if (id === null) {
				throw new Error(`Invalid syntax for attribute "${att.name}" in workflow "${workflow.name}". It is specified that its value is bound to configuration element with path "${path}" and variable "${key}"`
					+ `, but a configuration element with path "${path}" cannot be found in project at that stage. If you belive it is indeed really part of the project, `
					+ `please try moving the file earlier alphabetically so it is processed earlier than the workflow that uses it. Currently available configuration elements are: `
					+ JSON.stringify(context.configIdsMap));
			}
			stringBuilder.append(` conf-id="${id}" `);
			stringBuilder.append(` conf-key="${key}" `);
			value = "";
		}
	}

	function buildEndItem() {
		stringBuilder.append(`<workflow-item name="item0" type="end" end-mode="0">`).appendLine();
		stringBuilder.indent();
		stringBuilder.append(`<position x="${265 + 160 * workflow.items.length}.0" y="45.40909090909091" />`).appendLine();
		stringBuilder.unindent();
		stringBuilder.append(`</workflow-item>`).appendLine();
	}

	/**
	 * Builds a single Workflow Item (canvas element)
	 *
	 * The `out-name` points to the next item in the workflow
	 *
	 * @TODO: If we want breaking changes, if target is missing / null -> assume end
	 *
	 * @param item The item to build
	 * @param pos The position of the item in the workflow. Cannot be 0, as that is reserved for the end item
	 * @param items The list of all items in the workflow
	 */
	function buildItem(item: WorkflowItemDescriptor, pos: number, items: WorkflowItemDescriptor[]): void {
		const targetItem =
			item.target != null
				? item.target === "end"
					? "item0"
					: `item${findItemByName(items, item.target) || 0}`
				: pos < workflow.items.length ? `item${pos + 1}` : "item0";

		const type = ITEM_ENUM_TO_TYPE[item.itemType] || "task";

		switch (item.itemType) {

			case WorkflowItemType.Decision:
				stringBuilder.append(`<workflow-item`
					+ ` name="item${pos}"`
					+ ` out-name="${targetItem}"`
					+ ` type="${type}"`
					+ ` alt-out-name="item${findItemByName(items, (item.canvasItemPolymorphicBag as CanvasItemPolymorphicBagForDecision).else) || 0}"`
					//@TODO: figure out what this is
					+ ` comparator="0"`
					+ ">").appendLine();
				stringBuilder.indent();
				stringBuilder.append(`<display-name><![CDATA[${item.name}]]></display-name>`).appendLine();
				stringBuilder.append(`<script encoded="false"><![CDATA[${item.sourceText}]]></script>`).appendLine();
				buildItemParameterBindings("in-binding", item.input);
				buildItemParameterBindings("out-binding", item.output);
				// @TODO: figure out what this is
				stringBuilder.append(`<condition name="waitingTimer" type="Date" comparator="4" label="null" />`).appendLine();

				stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
				stringBuilder.unindent();
				stringBuilder.append(`</workflow-item>`).appendLine();

			case WorkflowItemType.WaitingTimer:
			case WorkflowItemType.Item:
			default:
				stringBuilder.append(`<workflow-item`
					+ ` name="item${pos}"`
					+ ` out-name="${targetItem}"`
					+ ` type="${type}"`
					+ ">").appendLine();
				stringBuilder.indent();
				stringBuilder.append(`<display-name><![CDATA[${item.name}]]></display-name>`).appendLine();
				stringBuilder.append(`<script encoded="false"><![CDATA[${item.sourceText}]]></script>`).appendLine();
				buildItemParameterBindings("in-binding", item.input);
				buildItemParameterBindings("out-binding", item.output);
				stringBuilder.append(`<position x="${225 + 160 * (pos - 1)}.0" y="55.40909090909091" />`).appendLine();
				stringBuilder.unindent();
				stringBuilder.append(`</workflow-item>`).appendLine();
		}
	}

	function buildItemParameterBindings(parentName: string, parameters: string[]): void {
		if (parameters.length) {
			stringBuilder.append(`<${parentName}>`).appendLine();
			stringBuilder.indent();
			parameters.forEach(paramName => {
				const param = workflow.parameters.find(p => p.name === paramName);
				if (param) {
					stringBuilder.append(`<bind name="${param.name}" type="${param.type}" export-name="${param.name}" />`).appendLine();
				}
			});
			stringBuilder.unindent();
			stringBuilder.append(`</${parentName}>`).appendLine();
		}
	}

	function buildPresentation(): void {
		if (workflow.presentation) {
			stringBuilder.append(workflow.presentation);
		}
		else {
			const inputParameters = workflow.parameters.filter(p => p.parameterType === WorkflowParameterType.Input);
			if (inputParameters.length) {
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

					if (param.availableValues != null && param.availableValues.length) {
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
}

export function mergeWorkflowXml(workflow: WorkflowDescriptor, xmlTemplate: string): string {
	console.log(`Merging ${workflow.name}`);
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
