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
export interface StringBuilder {
	indent(): StringBuilder;
	unindent(): StringBuilder;
	append(value: string): StringBuilder;
	appendLine(): StringBuilder;
	toString(): string;
}

/**
* A class to help build strings with indentation.
*/
export class StringBuilderClass implements StringBuilder {
	private content = "";
	private indentLevel = 0;
	private needIndentation = false;

	constructor(private newLine: string = "\n", private indentToken: string = "\t") { }

	indent(): this {
		this.indentLevel++;
		this.needIndentation = true;
		return this;
	}
	unindent(): this {
		this.indentLevel--;
		this.needIndentation = true;
		return this;
	}
	append(value: string): this {
		this.applyIndent();
		this.content += value;
		return this;
	}
	appendLine(): this {
		this.content += this.newLine;
		this.needIndentation = true;
		return this;
	}

	toString(): string {
		return this.content;
	}

	applyIndent(): void {
		if (this.needIndentation) {
			for (let i = 0; i < this.indentLevel; i++) {
				this.content += this.indentToken;
			}
			this.needIndentation = false;
		}
	}
}
