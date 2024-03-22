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

