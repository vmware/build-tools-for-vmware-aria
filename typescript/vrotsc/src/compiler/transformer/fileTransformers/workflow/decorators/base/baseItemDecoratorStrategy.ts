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
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../../decorators";
import { getDecoratorProps } from "../../../../helpers/node";
import { findTargetItem } from "../../helpers/findTargetItem";
import { formatPosition } from "../../helpers/formatPosition";
import { GraphNode } from "../helpers/graph";
import { InputOutputBindings, buildItemParameterBindings } from "../helpers/presentation";
import { DefaultSourceFilePrinter, SourceFilePrinter } from "../helpers/sourceFile";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";

const DEFAULT_OFFSET = 3;

/**
 *
 * Abstract canvas item decorator item strategy.
 */
export default abstract class BaseItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
    /**
    * When true, the Workflow element cannot be targeted by other items.
    * Being targeted explicitly should result in an Error,
    * while being targeted implicitly (by a previous element without a specified target)
    * should instead redirect the flow towards the default End item.
    */
    public isNotTargetable?: boolean;

    // source file printer handle
    protected sourceFilePrinter: SourceFilePrinter;

    public constructor() {
        this.sourceFilePrinter = new DefaultSourceFilePrinter();
    }

    /**
     * Return XML tag for the end workflow item.
     *
     * @returns XML tag name.
     */
    public abstract getCanvasType(): string;

    /**
     * Return the workflow item type supported by this decorator.
     *
     * @returns type of the workflow element.
     */
    public abstract getDecoratorType(): WorkflowItemType;

    /**
     * Prints the source code of the item.
     * @param itemInfo The item to print
     * @param pos The position of the item in the workflow
     * @param x position on X axis that will be used for UI display
     * @param y position on Y axis that will be used for UI display
     *
     * @returns an empty string (no source code is printed). Descendent classes should print source code if component supports it.
     */
    public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
        return "";
    };

    /**
     * Registers the item arguments
     *
     * - `target` is the name of the item to call after the item is executed
     * - `exception` is the exception to throw if the item fails
     * @param itemInfo The item to register
     * @param decoratorNode The decorator node
     * @returns void
     * @throws Error if an unsupported attribute is found
     */
    public registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
        const decoratorProperties = getDecoratorProps(decoratorNode);
        if (!decoratorProperties?.length) {
            return;
        }
        decoratorProperties.forEach((propTuple) => {
            const [propName, propValue] = propTuple;
            switch (propName) {
                case "target": {
                    itemInfo.target = propValue;
                    break;
                }
                case "exception": {
                    itemInfo.canvasItemPolymorphicBag.exception = propValue;
                    break;
                }
                default: {
                    throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
                }
            }
        });
    }

    /**
     * @see CanvasItemDecoratorStrategy.getGraphNode
     */
    public getGraphNode(itemInfo: WorkflowItemDescriptor, pos: number, offset?: [number, number]): GraphNode {
        const node: GraphNode = {
            name: `item${pos}`,
            origName: itemInfo.name,
            targets: [
                this.findTargetItem(itemInfo.target, pos, itemInfo),
            ]
        };
        if (offset) {
            node.offset = offset;
        }
        if (itemInfo.canvasItemPolymorphicBag?.exception) {
            node.targets.push(this.findTargetItem(itemInfo.canvasItemPolymorphicBag?.exception, pos, itemInfo));
        }

        return node;
    }

    /**
     * Prints out the item
     * @param itemInfo The item to print
     * @param pos The position of the item in the workflow
     * @param x position on X axis that will be used for UI display
     * @param y position on Y axis that will be used for UI display
     *
     * @returns The string representation of the item
     */
    public abstract printItem(itemInfo: WorkflowItemDescriptor, pos: number, x: number, y: number): string;

    protected buildParameterBindings(itemInfo: WorkflowItemDescriptor, bindingsType: InputOutputBindings): string {
        return buildItemParameterBindings(itemInfo, bindingsType);
    }

    protected findTargetItem(target: any, pos: number, item: WorkflowItemDescriptor): string {
        return findTargetItem(target, pos, item);
    }

    protected formatItemPosition(position: [number, number]): string {
        return formatPosition(position);
    }
}
