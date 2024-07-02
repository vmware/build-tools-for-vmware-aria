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
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import { getDecoratorProps } from "../../../helpers/node";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";
import { SourceFilePrinter, WrapperSourceFilePrinter } from "./helpers/sourceFile";

export default class DecisionItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(
		private readonly itemInfo: WorkflowItemDescriptor,
		private readonly sourceFilePrinter: SourceFilePrinter = new WrapperSourceFilePrinter()
	) {
		this.itemInfo.item = this;
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.Decision;
	}

	getCanvasType(): string {
		return "custom-condition";
	}

	registerItemArguments(decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTuple) => {
			const [propName, propValue] = propTuple;
			switch (propName) {
				case "target": {
					this.itemInfo.target = propValue;
					break;
				}
				case "else": {
					this.itemInfo.canvasItemPolymorphicBag.else = propValue;
					break;
				}
				default: {
					throw new Error(`Item attribute '${propName}' is not supported for ${this.getDecoratorType()} item`);
				}
			}
		});
	}

	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		return this.sourceFilePrinter.printSourceFile(methodNode, sourceFile);
	}

	printItem(pos: number): string {
		throw new Error("Method not implemented.");
	}
}
