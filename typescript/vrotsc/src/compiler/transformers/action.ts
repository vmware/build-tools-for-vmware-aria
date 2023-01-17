namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");

    export function getActionTransformer(file: FileDescriptor, context: FileTransformationContext) {
        const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
        context.sourceFiles.push(sourceFile);
        return transform;

        function transform() {
            const [sourceText, typeDefText, mapText] = transformSourceFile(
                sourceFile,
                context,
                {
                    before: [
                        collectFactsBefore,
                        collectNamespaces,
                        transformShimsBefore,
                    ],
                    after: [
                        transformShims,
                        transformNamespaces,
                        remediateTypeScript,
                        transformModuleSystem,
                        createActionClosure,
                    ],
                });

            // Test helpers should be excluded
            const isHelper = file.relativeFilePath.match( /\.helper\.[tj]s$/ );
            const outputDir = isHelper ? context.outputs.testHelpers : context.outputs.actions;

            let targetFilePath = system.changeFileExt(
                system.resolvePath(outputDir, file.relativeFilePath),
                ".js",
                [".js", ".ts"]);

            if ( isHelper ) {
                targetFilePath = system.changeFileExt( targetFilePath, "_helper.js", [".helper.js"] );
            }

            context.writeFile(targetFilePath, sourceText);

            if (typeDefText && canCreateDeclarationForFile(file, context.rootDir)) {
                const targetDtsFilePath = system.changeFileExt(system.resolvePath(context.outputs.types, file.relativeFilePath), ".d.ts");
                context.writeFile(targetDtsFilePath, typeDefText);
            }

            if (mapText) {
				const targetMapFilePath = system.changeFileExt(system.resolvePath(context.outputs.maps, file.relativeFilePath), ".js.map");
				context.writeFile(targetMapFilePath, mapText);
			}
        }

        function createActionClosure(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext): ts.SourceFile {
            const statements: ts.Statement[] = [];

            if (ctx.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) {
                // Copy all statements preceeding the action closure
                const actionClosureIndex = sourceFile.statements.length - 1;
                const expStatement = sourceFile.statements[actionClosureIndex] as ts.ExpressionStatement;
                const parenExpression = expStatement.expression as ts.ParenthesizedExpression;
                const funcExpression = parenExpression.expression as ts.FunctionExpression;
                const funcStatements: ts.Statement[] = [];
                funcStatements.push(...sourceFile.statements.slice(0, actionClosureIndex));
                funcStatements.push(...funcExpression.body.statements);
                if (context.emitHeader) {
                    addHeaderComment(funcStatements);
                }
                const updatedExpStatement = ts.updateExpressionStatement(expStatement,
                    ts.updateParen(parenExpression,
                        ts.updateFunctionExpression(funcExpression,
                            /* modifiers */ undefined,
                            /* asteriskToken */ undefined,
                            /* name */ undefined,
                            /* typeParameters */ undefined,
                            /* parameters */ funcExpression.parameters,
                            /* type */ undefined,
                            /* body */ ts.updateBlock(funcExpression.body, funcStatements),
                        )));
                statements.push(updatedExpStatement);
            }
            else {
                if (context.emitHeader) {
                    addHeaderComment(<ts.Statement[]><unknown>sourceFile.statements);
                }

                // Wrap statements in an action closure
                let closureStatement = ts.createStatement(
                    ts.createParen(
                        ts.createFunctionExpression(
                            /*modifiers*/ undefined,
                            /*asteriskToken*/ undefined,
                            /*name*/ undefined,
                            /*typeParameters*/ undefined,
                            /*parameters*/ undefined,
                            /*modifiers*/ undefined,
                            /*body*/ ts.createBlock(sourceFile.statements, true))));
                closureStatement = ts.addSyntheticLeadingComment(
                    closureStatement,
                    ts.SyntaxKind.MultiLineCommentTrivia,
                    "*\n * @return {Any}\n ",
                    /*hasTrailingNewLine*/ true);
                statements.push(closureStatement);
            }

            return ts.updateSourceFileNode(
                sourceFile,
                ts.setTextRange(
                    ts.createNodeArray(statements),
                    sourceFile.statements));
        }
    }
}
