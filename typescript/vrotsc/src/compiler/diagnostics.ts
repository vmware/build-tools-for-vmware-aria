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
import { Diagnostic, DiagnosticCategory } from "../types";
import { system } from "../system/system";

/**
 * Class representing a collection of diagnostics.
 * Diagnostics in TypeScript are objects that hold information about errors, warnings, or informational messages that are part of the compilation process.
 */
export class DiagnosticCollection {
	private items: Diagnostic[] = [];

	/**
	 * Adds a `Diagnostic` object to the collection.
	 * @param {Diagnostic} diagnostic - The diagnostic to add.
	 */
	add(diagnostic: Diagnostic) {
		this.items.push(diagnostic);
	}

	/**
	 * Creates a new `Diagnostic` object based on a TypeScript `Node`, a message string, and a `DiagnosticCategory`.
	 * It calculates the line and character position of the node in its source file and adds the new diagnostic to the collection.
	 * @param {ts.SourceFile} file - The source file.
	 * @param {ts.Node} node - The node.
	 * @param {string} message - The message.
	 * @param {DiagnosticCategory} category - The category.
	 */
	addAtNode(file: ts.SourceFile, node: ts.Node, message: string, category: DiagnosticCategory) {
		const lineAndChar = file.getLineAndCharacterOfPosition(node.getStart());
		this.items.push({
			file: system.relativePath(system.getCurrentDirectory(), file.fileName),
			line: lineAndChar.line + 1,
			col: lineAndChar.character + 1,
			messageText: message,
			category: category,
		});
	}

	/**
	 * Converts a native TypeScript `Diagnostic` into the custom `Diagnostic` format used in this collection, and adds it to the collection.
	 * @param {ts.Diagnostic} d - The native TypeScript diagnostic.
	 */
	addNative(d: ts.Diagnostic) {
		const diagnostic: Diagnostic = {
			file: undefined,
			line: undefined,
			col: undefined,
			messageText: typeof d.messageText === "string" ? d.messageText : d.messageText.messageText,
			category: <number>d.category,
		};
		if (d.file) {
			diagnostic.file = system.relativePath(system.getCurrentDirectory(), d.file.fileName);
			const pos = d.file.getLineAndCharacterOfPosition(d.start);
			diagnostic.line = pos.line + 1;
			diagnostic.col = pos.character + 1;
		}
		this.items.push(diagnostic);
	}

	/**
	 * Returns the current collection of diagnostics as an array.
	 * @returns {Diagnostic[]} The current collection of diagnostics.
	 */
	toArray(): Diagnostic[] {
		return this.items;
	}
}
