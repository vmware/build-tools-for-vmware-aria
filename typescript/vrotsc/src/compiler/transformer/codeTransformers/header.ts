import * as ts from "typescript";
import { addHeaderComment } from "../helpers/source";
import { FileTransformationContext } from "../../../types";

/**
 * Use this to inject the context of the transformer and return a function to be called later by the transformers
 */
export function prepareHeaderEmitter(context: FileTransformationContext): (sourceFile: ts.SourceFile) => ts.SourceFile {
	return (sourceFile: ts.SourceFile) => emitHeaderComment(sourceFile, context);
}

/**
 * Adds a header comment to the source file if the context allows it.
 *
 * The header just warns that changes made directly in VRO Client might be overwritten.
 *
 * @param {ts.SourceFile} sourceFile - The source file.
 * @returns {ts.SourceFile} The source file with a header comment.
 */
function emitHeaderComment(sourceFile: ts.SourceFile, context: FileTransformationContext): ts.SourceFile {
	if (context.emitHeader) {
		addHeaderComment(<ts.Statement[]><unknown>sourceFile.statements);
	}
	return sourceFile;
}
