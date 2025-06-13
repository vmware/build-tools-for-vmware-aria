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
import { FileDescriptor, FileTransformationContext, ScriptTransformationContext, HierarchyFacts, ScriptTransformers } from "../../../types";
import { system } from "../../../system/system";
import { transformSourceFile } from "../scripts/scripts";
import { collectFactsBefore } from "../metaTransformers/facts";
import { collectNamespaces, transformNamespaces } from "../codeTransformers/namespaces";
import { transformShimsBefore, transformShims } from "../codeTransformers/shims";
import { remediateTypeScript } from "../codeTransformers/remediate";
import { transformModuleSystem } from "../codeTransformers/modules";
import { canCreateDeclarationForFile } from "../metaTransformers/declaration";
import { checkActionForMisplacedClassDecorators } from "../helpers/checkActionForMisplacedClassDecorators";
import { addHeaderComment } from "../helpers/source";
import { filePathMatchesGlob, readVroIgnorePatternsFromFile } from "../../../utilities/vroIgnoreUtil";
import { resolve } from "path";

/**
 * Returns Action file transformer
 * @param {FileDescriptor} file
 * @param {FileTransformationContext} context
 * @returns {Function} Function to transform action script
 */
export function getActionTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration)
		.forEach(classNode => checkActionForMisplacedClassDecorators(classNode as ts.ClassDeclaration));
	context.sourceFiles.push(sourceFile);

	return () => transform(file, context, sourceFile);
}

/**
 * Function to transform an Action source file. Excludes test helpers (.helper.ts/js)
 * @param {FileDescriptor} file
 * @param {FileTransformationContext} context
 * @param {ts.SourceFile} sourceFile
 */
function transform(file: FileDescriptor, context: FileTransformationContext, sourceFile: ts.SourceFile) {
	const transformers = buildTransformers(context.emitHeader);
	const [sourceText, typeDefText, mapText] = transformSourceFile(sourceFile, context, transformers);

	// special case of .vroignore (kept as is):
	const isHelper = file.relativeFilePath.match(/\.helper\.[tj]s$/);
	const outputDir = isHelper ? context.outputs.testHelpers : context.outputs.actions;

	const resolvedPath = system.resolvePath(outputDir, file.relativeFilePath);
	let targetFilePath = system.changeFileExt(resolvedPath, ".js", [".js", ".ts"]);

	if (isHelper) {
		targetFilePath = system.changeFileExt(targetFilePath, "_helper.js", [".helper.js"]);
	}

	context.writeFile(targetFilePath, sourceText);

	const ignorePatterns = readVroIgnorePatternsFromFile(resolve(context.vroIgnoreFile), 'TestHelpers', 'Compilation');
    console.log(`vrotsc/action: Ignored patterns: ${JSON.stringify(ignorePatterns)}`);
	const isIgnored = filePathMatchesGlob(resolvedPath, ignorePatterns);
	if (typeDefText && !isIgnored && canCreateDeclarationForFile(file, context.rootDir)) {
		const targetDtsFilePath = system.changeFileExt(system.resolvePath(context.outputs.types, file.relativeFilePath), ".d.ts");
		context.writeFile(targetDtsFilePath, typeDefText);
	}

	if (mapText) {
		const targetMapFilePath = system.changeFileExt(system.resolvePath(context.outputs.maps, file.relativeFilePath), ".js.map");
		context.writeFile(targetMapFilePath, mapText);
	}
}

/**
 * Builds action script transformers
 * @param {boolean} emitHeader - whether to add a header comment
 * @returns {ScriptTransformers}
 */
function buildTransformers(emitHeader: boolean): ScriptTransformers {
	return {
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
			(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext) => handleActionClosure(sourceFile, ctx, emitHeader),
		],
	};
}

/**
 * Wraps an Action in a closure function or, if it already is a closure, updates it.
 * @param {ts.SourceFile} sourceFile action source file
 * @param {ScriptTransformationContext} ctx - script transformation context
 * @param {boolean} emitHeader - whether to add a header comment
 * @returns {ts.SourceFile}
 */
export function handleActionClosure(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext, emitHeader: boolean): ts.SourceFile {
	const statements: ts.Statement[] = (ctx.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure)
		? updateActionClosure(sourceFile.statements, ctx.factory, emitHeader)
		: createActionClosure(sourceFile.statements, ctx.factory, emitHeader);

	let nodeArray = ctx.factory.createNodeArray(statements);
	nodeArray = ts.setTextRange(nodeArray, sourceFile.statements);

	return ctx.factory.updateSourceFile(sourceFile, nodeArray);
}

/**
 * Copies all statements preceeding the action closure
 * @param {ts.Statement[]} sourceFileStatements - statements from the source file
 * @param {ts.NodeFactory} factory - node factory (from the script transformation context)
 * @param {boolean} emitHeader - whether to add a header comment
 * @return {ts.Statement[]} populated array of statements
 */
function updateActionClosure(sourceFileStatements: ts.NodeArray<ts.Statement>, factory: ts.NodeFactory, emitHeader: boolean): ts.Statement[] {
	const actionClosureIndex = sourceFileStatements.length - 1;
	const expStatement = sourceFileStatements[actionClosureIndex] as ts.ExpressionStatement;
	const parenExpression = expStatement.expression as ts.ParenthesizedExpression;
	const funcExpression = parenExpression.expression as ts.FunctionExpression;
	const funcStatements: ts.Statement[] = sourceFileStatements.slice(0, actionClosureIndex).concat(funcExpression.body.statements);
	if (emitHeader) {
		addHeaderComment(funcStatements);
	}
	const block = factory.updateBlock(funcExpression.body, funcStatements);
	const updatedFnExpr = factory.updateFunctionExpression(funcExpression,
		undefined, // ts.Modifier[],
		undefined, // ts.AsteriskToken,
		undefined,// ts.Identifier,
		undefined, // ts.TypeParameterDeclaration[],
		funcExpression.parameters,// ts.ParameterDeclaration[],
		undefined, // ts.TypeNode,
		block // ts.Block
	);
	const parenthesizedExpr = factory.updateParenthesizedExpression(parenExpression, updatedFnExpr);

	return [factory.updateExpressionStatement(expStatement, parenthesizedExpr)];
}

/**
 * Wrap statements in a new action closure
 * @param {ts.Statement[]} sourceFileStatements - statements from the source file
 * @param {ts.NodeFactory} factory - node factory (from the script transformation context)
 * @param {boolean} emitHeader - whether to add a header comment
 * @return {ts.Statement[]} populated array of statements
 */
function createActionClosure(sourceFileStatements: ts.NodeArray<ts.Statement>, factory: ts.NodeFactory, emitHeader: boolean): ts.Statement[] {
	if (emitHeader) {
		addHeaderComment(<ts.Statement[]><unknown>sourceFileStatements);
	}
	const block = factory.createBlock(sourceFileStatements, true);
	const fnExpr = factory.createFunctionExpression(
		undefined, // ts.Modifier[],
		undefined, // ts.AsteriskToken,
		undefined,// ts.Identifier,
		undefined, // ts.TypeParameterDeclaration[],
		undefined,// ts.ParameterDeclaration[],
		undefined, // ts.TypeNode,
		block // ts.Block
	);
	const parentesizedExpr = factory.createParenthesizedExpression(fnExpr);
	let closureStatement = factory.createExpressionStatement(parentesizedExpr);

	return [ts.addSyntheticLeadingComment(closureStatement, ts.SyntaxKind.MultiLineCommentTrivia, "*\n * @return {Any}\n ", true)]; //hasTrailingNewLine
}
