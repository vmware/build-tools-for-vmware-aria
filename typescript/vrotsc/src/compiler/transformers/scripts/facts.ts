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

    export function collectFactsBefore(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
        const visitor = createVisitor(visitNode, context);
        const actionClosureIndex = sourceFile.statements.findIndex(isActionClosure);

        if (actionClosureIndex > -1 && actionClosureIndex === sourceFile.statements.length - 1) {
            context.file.hierarchyFacts |= HierarchyFacts.ContainsActionClosure;
        }

        visitor.visitNode(sourceFile);

        return sourceFile;

        function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
            switch (node.kind) {
                case ts.SyntaxKind.Identifier:
                    visitIdentifier(<ts.Identifier>node);
                    break;

                case ts.SyntaxKind.ExportKeyword:
                case ts.SyntaxKind.ExportDeclaration:
                case ts.SyntaxKind.ExportAssignment:
                case ts.SyntaxKind.ExportSpecifier:
                    context.file.hierarchyFacts |= HierarchyFacts.ContainsEcmaScriptExport;
                    break;
            }
            return undefined;
        }

        function visitIdentifier(node: ts.Identifier): void {
            if (node.text === SCRIPT_VROES_VAR) {
                context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference;
            }
        }
    }
}
