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

    export function getTestTransformer(file: FileDescriptor, context: FileTransformationContext) {
        const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
        context.sourceFiles.push(sourceFile);
        return transform;

        function transform() {
            const [sourceFileText] = transformSourceFile(
                sourceFile,
                context,
                {
                    before: [
                        collectFactsBefore,
                        transformShimsBefore,
                    ],
                    after: [
                        transformShims,
                        remediateTypeScript,
                        transformModuleSystem,
                        validateJasmineTest,
                    ],
                });

            let targetFilePath = system.changeFileExt(
                system.resolvePath(context.outputs.tests, file.relativeFilePath),
                "",
                [".test.js", ".test.ts"]);

            if (!targetFilePath.endsWith("Test")) {
                targetFilePath += "Test";
            }

            targetFilePath += ".js";

            context.writeFile(targetFilePath, sourceFileText);
        }

        function validateJasmineTest(sourceFile: ts.SourceFile): ts.SourceFile {
            if (!sourceFile.statements.some(isDescribeCall)) {
                context.diagnostics.add({
                    file: system.relativePath(system.getCurrentDirectory(), sourceFile.fileName),
                    messageText: `Jasmine test file should have describe function`,
                    category: DiagnosticCategory.Error,
                });
            }
            return sourceFile;
        }

        function isDescribeCall(statement: ts.Statement): boolean {
            if (statement.kind !== ts.SyntaxKind.ExpressionStatement) {
                return false;
            }

            const { expression } = statement as ts.ExpressionStatement;

            if (expression.kind !== ts.SyntaxKind.CallExpression) {
                return false;
            }

            const { expression: callExp, arguments: callArgs } = expression as ts.CallExpression;

            if (!isIdentifier(callExp, "describe")) {
                return false;
            }

            if (callArgs.length !== 2) {
                return false;
            }

            if (callArgs[0].kind !== ts.SyntaxKind.StringLiteral) {
                return false;
            }

            if (callArgs[1].kind !== ts.SyntaxKind.ArrowFunction && callArgs[1].kind !== ts.SyntaxKind.FunctionExpression) {
                return false;
            }

            return true;
        }
    }
}
