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
import { ScriptTransformationContext, HierarchyFacts } from "../../../types";
import { addHeaderComment } from "./source";

/** Parameters used when creating/updating an Action function expression */
type FunctionExpressionSpec = [
	modifiers: ts.Modifier[],
	asteriskToken: ts.AsteriskToken,
	name: ts.Identifier,
	typeParameters: ts.TypeParameterDeclaration[],
	parameters: ts.ParameterDeclaration[],
	type: ts.TypeNode,
	body: ts.Block
];

/**
 * Checks an action for class-level decorators, associated with an unintentionally omitted prefix to the ".ts" extension:
 * - Configuration (.conf.ts)
 * - PolicyTemplate (.pl.ts)
 * - Workflow (.wf.ts)
 * @param {ClassDeclaration} classNode - class node in the action
 * @throws Error if such decorator is found to indicate possibly omitted extension prefix
 * (otherwise Actions as functions are not expected to have class-level decorators).
 */
export function checkActionForMisplacedClassDecorators(classNode: ts.ClassDeclaration) {
	const decoratorToFileSuffixMap = {
		"PolicyTemplate": "pl",
		"Configuration": "conf",
		"Workflow": "wf"
	};
	const misplacedDecorator = ts.getDecorators(classNode)
		?.map(decoratorNode => (decoratorNode.expression as ts.CallExpression).expression as { kind?: ts.SyntaxKind; text?: string; })
		.find(expression => expression?.kind === ts.SyntaxKind.Identifier && expression?.text in decoratorToFileSuffixMap)?.text;
	if (misplacedDecorator) {
		throw new Error(`Typescript ${misplacedDecorator} file names need to be in the format `
			+ `'<fileName>.${decoratorToFileSuffixMap[misplacedDecorator]}.ts'!`
		);
	}
}

/**
 * Wraps an Action in a closure function or, if it already is a closure, updates it.
 * @param {ts.SourceFile} sourceFile action source file
 * @param {ScriptTransformationContext} ctx - script transformation context
 * @param {boolean} emitHeader - whether to add a header comment
 * @returns {ts.SourceFile}
 */
export function handleActionClosure(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext, emitHeader: boolean): ts.SourceFile {
	const statements: ts.Statement[] = [];
	const sourceFileStatements = sourceFile.statements;
	const factory = ctx.factory;

	if (ctx.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) {
		updateActionClosure(sourceFileStatements, factory, statements, emitHeader);
	}
	else {
		createActionClosure(sourceFileStatements, factory, statements, emitHeader);
	}

	let nodeArray = factory.createNodeArray(statements);
	nodeArray = ts.setTextRange(nodeArray, sourceFileStatements);
	return factory.updateSourceFile(sourceFile, nodeArray);
}

/**
 * Copies all statements preceeding the action closure
 * @param {ts.Statement[]} sourceFileStatements - statements from the source file
 * @param {ts.NodeFactory} factory - node factory (from the script transformation context)
 * @param {ts.Statement[]} statements - array of statements to populate
 * @param {boolean} emitHeader - whether to add a header comment
 */
function updateActionClosure(sourceFileStatements: ts.NodeArray<ts.Statement>, factory: ts.NodeFactory, statements: ts.Statement[], emitHeader: boolean) {
	const actionClosureIndex = sourceFileStatements.length - 1;
	const expStatement = sourceFileStatements[actionClosureIndex] as ts.ExpressionStatement;
	const parenExpression = expStatement.expression as ts.ParenthesizedExpression;
	const funcExpression = parenExpression.expression as ts.FunctionExpression;
	const funcStatements: ts.Statement[] = sourceFileStatements.slice(0, actionClosureIndex).concat(funcExpression.body.statements);
	if (emitHeader) {
		addHeaderComment(funcStatements);
	}
	const block = factory.updateBlock(funcExpression.body, funcStatements);
	const fnExprSpec: FunctionExpressionSpec = [undefined, undefined, undefined, undefined, funcExpression.parameters as any, undefined, block];
	const updatedFnExpr = factory.updateFunctionExpression(funcExpression, ...fnExprSpec);
	const parenthesizedExpr = factory.updateParenthesizedExpression(parenExpression, updatedFnExpr);
	const updatedExpr = factory.updateExpressionStatement(expStatement, parenthesizedExpr);
	statements.push(updatedExpr);
}

/**
 * Wrap statements in a new action closure
 * @param {ts.Statement[]} sourceFileStatements - statements from the source file
 * @param {ts.NodeFactory} factory - node factory (from the script transformation context)
 * @param {ts.Statement[]} statements - array of statements to populate
 * @param {boolean} emitHeader - whether to add a header comment
 */
function createActionClosure(sourceFileStatements: ts.NodeArray<ts.Statement>, factory: ts.NodeFactory, statements: ts.Statement[], emitHeader: boolean) {
	if (emitHeader) {
		addHeaderComment(<ts.Statement[]><unknown>sourceFileStatements);
	}
	const block = factory.createBlock(sourceFileStatements, true);
	const fnExprSpec: FunctionExpressionSpec = [undefined, undefined, undefined, undefined, undefined, undefined, block];
	const fnExpr = factory.createFunctionExpression(...fnExprSpec);
	const parentesizedExpr = factory.createParenthesizedExpression(fnExpr);
	let closureStatement = factory.createExpressionStatement(parentesizedExpr);
	const stmt = ts.addSyntheticLeadingComment(closureStatement, ts.SyntaxKind.MultiLineCommentTrivia, "*\n * @return {Any}\n ", true); //hasTrailingNewLine
	statements.push(stmt);
}
