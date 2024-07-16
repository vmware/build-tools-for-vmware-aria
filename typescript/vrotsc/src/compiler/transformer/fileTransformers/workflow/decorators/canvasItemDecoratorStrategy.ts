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
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";

export default interface CanvasItemDecoratorStrategy {
	/**
	 * Returns the type of the decorator
	 */
	getDecoratorType(): WorkflowItemType;

	/**
	 * Returns the item info associated with the decorator
	 *
	 * The WorkflowItemDescriptor is passed in the constructor of the strategy and is used internally
	 *
	 * @returns {WorkflowItemDescriptor}
	 */
	getItemInfo(): WorkflowItemDescriptor;

	/**
	 * This will be the type of the canvas item.
	 */
	getCanvasType(): string;

	/**
	 * Registers the arguments from the decorator to the workflowInfo
	 */
	registerItemArguments(decoratorNode: ts.Decorator): void;

	/**
	 * Only items that have scripts should return something here.
	 *
	 * The rest can return an empty string.
	 */
	printSourceFile(methodNode: ts.MethodDeclaration, sourceFile: ts.SourceFile): string;

	printItem(pos: number): string;
}

