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
import { Decorator, MethodDeclaration, SourceFile, isIdentifier, isPrivateIdentifier } from "typescript";
import { WorkflowItemDescriptor, WorkflowItemType } from "../../../../decorators";
import CanvasItemDecoratorStrategy from "./canvasItemDecoratorStrategy";

export default class RootItemDecoratorStrategy implements CanvasItemDecoratorStrategy {
	getDecoratorType(): WorkflowItemType {
		return WorkflowItemType.RootItem;
	}

	/**
	 * This function should NOT register the itemType in the itemInfo object, as it's not a real item type.
	 */
	registerItemArguments(itemInfo: WorkflowItemDescriptor, decoratorNode: Decorator): void {
		const methodNode = decoratorNode.parent as MethodDeclaration;

		if (!isIdentifier(methodNode.name) && !isPrivateIdentifier(methodNode.name)) {
			throw new Error(`RootItem decorator must be applied to a method with an identifier name.`);
		}

		itemInfo.parent.rootItem = methodNode.name.escapedText as string;
	}

	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string { return this.throwDoNotCallError(); }
	getCanvasType(): string { return this.throwDoNotCallError(); }
	printItem(itemInfo: WorkflowItemDescriptor, pos: number): string { return this.throwDoNotCallError(); }

	private throwDoNotCallError(): never {
		throw new Error("Method should not be called. RootItem is a meta decorator.");
	}
}
