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

import { GraphNode } from "../helpers/graph";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../../decorators";

export default interface CanvasItemDecoratorStrategy {
    /**
     * When true, the Workflow element cannot be targeted by other items.
     * Being targeted explicitly should result in an Error,
     * while being targeted implicitly (by a previous element without a specified target)
     * should instead redirect the flow towards the default End item.
     */
    readonly isNotTargetable?: boolean;

    /**
     * Returns the type of the decorator.
     */
    getDecoratorType(): WorkflowItemType;

    /**
     * This will be the type of the canvas item.
     */
    getCanvasType(): string;

    /**
     * Registers the arguments from the decorator to the workflowInfo.
     */
    registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: ts.Decorator): void;

    /**
     * Only items that have scripts should return something here.
     *
     * The rest can return an empty string.
     */
    printSourceFile(methodNode: ts.MethodDeclaration, sourceFile: ts.SourceFile, itemInfo: WorkflowItemDescriptor): string;

    /**
     * Print out the XML representation of the canvas item.
     */
    printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string;

    /**
     * Returns the Node representation of the item.
     */
    getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number): GraphNode;
}