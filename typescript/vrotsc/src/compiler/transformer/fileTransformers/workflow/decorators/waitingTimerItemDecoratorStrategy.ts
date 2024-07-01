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
import { DefaultSourceFilePrinter, SourceFilePrinter } from "./helpers/sourceFile";

export default class WaitingTimerItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	constructor(
		private readonly itemInfo: WorkflowItemDescriptor,
		private readonly sourceFilePrinter: SourceFilePrinter = new DefaultSourceFilePrinter()
	) {
		this.itemInfo.item = this;
	}

	getCanvasType(): string {
		return "waiting-timer";
	}

	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.WaitingTimer;
	}

	registerItemArguments(decoratorNode: Decorator): void {
		getDecoratorProps(decoratorNode).forEach((propTouple) => {
			const [propName, propValue] = propTouple;

			switch (propName) {
				case "target":
					this.itemInfo.target = propValue;
					break;
				default:
					throw new Error(`Item attribute '${propName}' is not suported for ${this.getDecoratorType()} item`);
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
