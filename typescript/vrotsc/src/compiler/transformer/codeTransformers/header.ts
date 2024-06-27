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
