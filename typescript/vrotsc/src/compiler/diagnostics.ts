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
    const ts: typeof import("typescript") = require("typescript");

    export interface DiagnosticCollection {
        add(diagnostic: Diagnostic): void;
        addAtNode(file: ts.SourceFile, node: ts.Node, message: string, category: DiagnosticCategory): void;
        addNative(diagnostic: ts.Diagnostic): void;
        toArray(): Diagnostic[];
    }

    export function createDiagnosticCollection(): DiagnosticCollection {
        const items: Diagnostic[] = [];
        return {
            add: (diagnostic: Diagnostic) => {
                items.push(diagnostic);
            },
            addAtNode: (file: ts.SourceFile, node: ts.Node, message: string, category: DiagnosticCategory) => {
                const lineAndChar = file.getLineAndCharacterOfPosition(node.getStart());
                items.push({
                    file: system.relativePath(system.getCurrentDirectory(), file.fileName),
                    line: lineAndChar.line + 1,
                    col: lineAndChar.character + 1,
                    messageText: message,
                    category: category,
                });
            },
            addNative: (d: ts.Diagnostic) => {
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
                items.push(diagnostic);
            },
            toArray(): Diagnostic[] {
                return items;
            }
        };
    }
}