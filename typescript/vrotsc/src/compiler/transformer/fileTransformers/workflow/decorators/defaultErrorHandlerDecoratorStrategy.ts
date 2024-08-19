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
import { Decorator, MethodDeclaration, SourceFile } from "typescript";
import { StringBuilderClass } from "../../../../../utilities/stringBuilder";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import { findTargetItem } from "../helpers/findTargetItem";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { GraphNode } from "./helpers/graph";
import { formatPosition } from "../helpers/formatPosition";

/**
 * Responsible for printing out a default error handler
 * Important Note: The name of the error handler component should match the name of the end workflow element.
 * @example
 * ```xml
	<error-handler name="item1">
		<position x="415.0" y="65.40"/>
	</error-handler>
	<workflow-item name="item1" type="end" end-mode="0">
		<position x="405.0" y="55.40"/>
	</workflow-item>
 * ```
 */
export default class DefaultErrorHandlerDecoratorStrategy implements CanvasItemDecoratorStrategy {

	/**
	 * Extracts the name of thedefault error handler in an Array to be added to the start node names of a Graph.
	 * @param {WorkflowItemDescriptor[]} items - workflow items
	 * @param {GraphNode[]} nodes - graph nodes mapped to the WF items, with name=item# and targets populated.
	 * @returns {string[]} an array containing the node name (item#) of the default error handler. Empty if there is none.
	 * @throws Error if there are more than 1 Default Error Handler items
	 * @throws Error if the Default Error Handler item is a target of another node
	 * @throws Error if the Default Error Handler has more than 1 target or the target node cannot be found
	 * (e.g. the default "end" node)
	 */
	public static getDefaultErrorHandlerNodes(items: WorkflowItemDescriptor[], nodes: GraphNode[]): string[] {
		const errorHandlerItems = items
			.map((item, i) => item.strategy.getCanvasType() !== "error-handler" ? null : `item${i + 1}`)
			.filter(item => !!item);
		if (errorHandlerItems.length > 1) {
			throw new Error(`Cannot have more than 1 Default Error Handler element: `
				+ `[${nodes.filter(n => errorHandlerItems.indexOf(n.name) > -1).map(n => `${n.name}(${n.origName})`)}]!`);
		}
		if (!errorHandlerItems.length) {
			return [];
		}
		const errorHandlerItem = errorHandlerItems[0];
		const nodesTargetingEh = nodes
			.filter(n => n.targets.indexOf(errorHandlerItem) > -1 && n.name !== errorHandlerItem) // self-targeting handled in graph
			.map(n => `${n.name}(${n.origName})`);
		if (nodesTargetingEh.length) {
			throw new Error(`Default error handler element cannot be targeted by others: ${nodesTargetingEh}!`);
		}
		const ehNodeTargets = nodes.find(n => n.name === errorHandlerItem)?.targets || [];
		const nodesTargetedByEh = nodes.filter(n => ehNodeTargets.indexOf(n.name) > -1).map(n => `${n.name}(${n.origName})`);
		if (nodesTargetedByEh.length !== 1) {
			throw new Error(`Default error handler element must have exactly one target element: [${nodesTargetedByEh}]!`);
		}
		return [errorHandlerItem]
	}

	/** Marks the element type as not targetable by other elements (see in {@link findTargetItem}) */
	public readonly isNotTargetable = true;

	/**
	 * Return XML tag for the error handler workflow item.
	 *
	 * @returns XML tag name.
	 */
	public getCanvasType(): string {
		return "error-handler";
	}

	/**
	 * Return the workflow item type supported by this decorator.
	 *
	 * @returns type of the workflow element.
	 */
	public getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.DefaultErrorHandler;
	}

	/**
	 * Register the canvas item arguments. For the default error handler only "target" and "exception" are supported.
	 *
	 * @param itemInfo item info for that properties should be fetched.
	 * @param decoratorNode decorator node handle.
	 */
	public registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
		const decoratorProperties = getDecoratorProps(decoratorNode);
		if (!decoratorProperties?.length) {
			return;
		}
		decoratorProperties.forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target":
					itemInfo.target = propValue;
					break;

				default:
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
			}
		});
	}

	/**
	 * @see CanvasItemDecoratorStrategy.getGraphNode
	 */
	getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number): GraphNode {
		return {
			name: `item${pos}`,
			origName: itemInfo.name,
			targets: [findTargetItem(itemInfo.target, pos, itemInfo)],
			offset: [40, -10]
		};
	}

	/**
	 * There is no need to print the source file for the default error handler.
	 */
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string { return ""; }

	/**
	 * Prints out the default handler item. Note that it needs to be connected with an end item and
	 * both must have identical name.
	 *
	 * @param itemInfo The item to print.
	 * @param pos The position of the item in the workflow.
	 * @param x position on X axis that will be used for UI display
	 * @param y position on Y axis that will be used for UI display
	 *
	 * @returns The string representation of the item.
	 */
	printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string {
		const stringBuilder = new StringBuilderClass("", "");

		const targetItemName = findTargetItem(itemInfo.target, pos, itemInfo);
		if (targetItemName === null) {
			throw new Error(`Unable to find target item for ${this.getDecoratorType()} item`);
		}
		//
		// it is important that the name of the error handler should be the same as the pointing target item name
		stringBuilder.append(`<error-handler name="${targetItemName}" `);

		if (itemInfo.canvasItemPolymorphicBag.exceptionBinding) {
			stringBuilder.append(` throw-bind-name="${itemInfo.canvasItemPolymorphicBag.exceptionBinding}" `);
		}

		stringBuilder.append(">").appendLine();
		stringBuilder.indent();
		stringBuilder.append(formatPosition([x, y])).appendLine();
		stringBuilder.unindent();
		stringBuilder.append("</error-handler>").appendLine();
		stringBuilder.unindent();

		return stringBuilder.toString();
	}
}
