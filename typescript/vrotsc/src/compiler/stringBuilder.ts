/*
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
namespace vrotsc {
    export function createStringBuilder(newLine: string = "\n", indentToken: string = "\t"): StringBuilder {
        let content = "";
        let indentLevel = 0;
        let needIndentation = false;

        const stringBuilder: StringBuilder = {
            indent,
            unindent,
            append,
            appendLine,
            toString,
        };

        return stringBuilder;

        function indent(): StringBuilder {
            indentLevel++;
            needIndentation = true;
            return stringBuilder;
        }

        function unindent(): StringBuilder {
            indentLevel--;
            needIndentation = true;
            return stringBuilder;
        }

        function append(value: string): StringBuilder {
            applyIndent();
            content += value;
            return stringBuilder;
        }

        function appendLine(): StringBuilder {
            content += newLine;
            needIndentation = true;
            return stringBuilder;
        }

        function toString(): string {
            return content;
        }

        function applyIndent(): void {
            if (needIndentation) {
                for (let i = 0; i < indentLevel; i++) {
                    content += indentToken;
                }
                needIndentation = false;
            }
        }
    }
}