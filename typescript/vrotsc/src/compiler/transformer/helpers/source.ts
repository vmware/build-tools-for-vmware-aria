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
import { system } from "../../../system/system";

/**
* Adds a synthetic leading comment to the first statement in the list.
*
* @param statements The list of statements.
*/
export function addHeaderComment(statements: ts.Statement[]): void {
	if (statements.length) {
		const AUTO_GENERATED_COMMENT =
			"----------------------------------------------------------*" + system.newLine +
			" * CHANGES MADE DIRECTLY IN VRO CLIENT MIGHT BE OVERWRITTEN *" + system.newLine +
			" *----------------------------------------------------------";
		statements[0] = ts.addSyntheticLeadingComment(
			statements[0],
			ts.SyntaxKind.MultiLineCommentTrivia,
			AUTO_GENERATED_COMMENT,
			true);
	}
}


/**
* Prints a source file to a string.
*/
export function printSourceFile(sourceFile: ts.SourceFile): string {
	const printer = ts.createPrinter({
		newLine: ts.NewLineKind.LineFeed,
		omitTrailingSemicolon: false
	});
	return printer.printFile(sourceFile);
}

