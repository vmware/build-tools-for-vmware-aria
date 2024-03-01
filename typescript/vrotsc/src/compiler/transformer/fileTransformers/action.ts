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
import { FileDescriptor, FileTransformationContext, ScriptTransformationContext, HierarchyFacts } from "../../../types";
import { system } from "../../../system/system";
import { transformSourceFile } from "../scripts/scripts";
import { collectFactsBefore } from "../metaTransformers/facts";
import { collectNamespaces, transformNamespaces } from "../codeTransformers/namespaces";
import { transformShimsBefore, transformShims } from "../codeTransformers/shims";
import { remediateTypeScript } from "../codeTransformers/remediate";
import { transformModuleSystem } from "../codeTransformers/modules";
import { canCreateDeclarationForFile } from "../metaTransformers/declaration";
import { addHeaderComment } from "../helpers/source";

// @TODO: Take a look at this

export function getActionTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	context.sourceFiles.push(sourceFile);

	return function transform() {
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
		const isHelper = file.relativeFilePath.match(/\.helper\.[tj]s$/);
		const outputDir = isHelper ? context.outputs.testHelpers : context.outputs.actions;

		let targetFilePath = system.changeFileExt(
			system.resolvePath(outputDir, file.relativeFilePath),
			".js",
			[".js", ".ts"]);

		if (isHelper) {
			targetFilePath = system.changeFileExt(targetFilePath, "_helper.js", [".helper.js"]);
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
	};

	function createActionClosure(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext): ts.SourceFile {
		const statements: ts.Statement[] = [];
        const { factory } = ctx;

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
			const updatedExpStatement = factory.updateExpressionStatement(expStatement,
				factory.updateParenthesizedExpression(parenExpression,
					factory.updateFunctionExpression(funcExpression,
                            /* modifiers */ undefined,
                            /* asteriskToken */ undefined,
                            /* name */ undefined,
                            /* typeParameters */ undefined,
                            /* parameters */ funcExpression.parameters,
                            /* type */ undefined,
                            /* body */ factory.updateBlock(funcExpression.body, funcStatements),
					)));
			statements.push(updatedExpStatement);
		}
		else {
			if (context.emitHeader) {
				addHeaderComment(<ts.Statement[]><unknown>sourceFile.statements);
			}

			// Wrap statements in an action closure
			let closureStatement = factory.createExpressionStatement(
				factory.createParenthesizedExpression(
					factory.createFunctionExpression(
                            /*modifiers*/ undefined,
                            /*asteriskToken*/ undefined,
                            /*name*/ undefined,
                            /*typeParameters*/ undefined,
                            /*parameters*/ undefined,
                            /*modifiers*/ undefined,
                            /*body*/ factory.createBlock(sourceFile.statements, true))));
			closureStatement = ts.addSyntheticLeadingComment(
				closureStatement,
				ts.SyntaxKind.MultiLineCommentTrivia,
				"*\n * @return {Any}\n ",
                    /*hasTrailingNewLine*/ true);
			statements.push(closureStatement);
		}

		return factory.updateSourceFile(
			sourceFile,
			ts.setTextRange(
				factory.createNodeArray(statements),
				sourceFile.statements));
	}
}
