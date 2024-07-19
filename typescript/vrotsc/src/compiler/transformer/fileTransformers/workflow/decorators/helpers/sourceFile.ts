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
import { MethodDeclaration, SourceFile, SyntaxKind, factory } from "typescript";
import { printSourceFile } from "../../../../helpers/source";
import { createWorkflowItemPrologueStatements } from "../../../../codeTransformers/prologueStatements";

export interface SourceFilePrinter {
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string;
}

/**
 * Default source file printer will directly print the source file with the method node body statements.
 */
export class DefaultSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					...methodNode.body.statements
				]
			)
		);
	}
}

/**
 * This source file printer will wrap the method node body statements with a function declaration.
 *
 *  This wrapping is necessary because "return"s are not allowed in the root of a typescript file, but are allowed in the case of vRO.
 *  The "wrapper" function will later be removed in the transpilation process.
 *  @NOTE: This is 100% due to a typescript limitation, and not a vRO limitation.
 */
export class WrapperSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		const wrapperFunction = factory.createFunctionDeclaration(
			undefined,
			undefined,
			"wrapper",
			undefined,
			[],
			undefined,
			methodNode.body
		);
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					wrapperFunction
				]
			)
		);
	}
}

export class ScheduledWorkflowItemSourceFilePrinter extends DefaultSourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile): string {
		// return printSourceFile(
		// );
		return "";
	}
}
