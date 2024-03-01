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
import * as ts from 'typescript';
import { Comment } from '../../../types';

/**
* Helper function to get the text of an identifier or return null.
*/
export function getIdentifierTextOrNull(node: ts.Node): string | undefined {
	if (node && node.kind === ts.SyntaxKind.Identifier) {
		return (<ts.Identifier>node).text;
	}
}

/**
* Helper function to get the text of a string literal or return null.
*/
export function getStringTextOrNull(node: ts.Node): string | undefined {
	if (node && node.kind === ts.SyntaxKind.StringLiteral) {
		return (<ts.StringLiteral>node).text;
	}
}

export function isIdentifier(node: ts.Node, text: string): boolean {
	return getIdentifierTextOrNull(node) === text;
}

export function isStringLiteral(node: ts.Node, text: string): boolean {
	return getStringTextOrNull(node) === text;
}

/**
* A type reference is a reference to a type, such as a class, interface, or type alias.
*/
export function isTypeReference(node: ts.Node, typeName: string): boolean {
	if (node.kind !== ts.SyntaxKind.TypeReference) {
		return false;
	}

	let typeRef = node as ts.TypeReferenceNode;
	if (!isIdentifier(typeRef.typeName, typeName)) {
		return false;
	}

	return true;
}


/**
* An action closure is a function expression that is wrapped in parentheses and is an expression statement.
* Example:
* ```
* (function() {
*  System.log("Hello, world!");
*  })
* ```
*/
export function isActionClosure(statement: ts.Statement): boolean {
	if (statement.kind !== ts.SyntaxKind.ExpressionStatement) {
		return false;
	}

	const { expression } = statement as ts.ExpressionStatement;

	if (expression.kind !== ts.SyntaxKind.ParenthesizedExpression) {
		return false;
	}

	const { expression: funcExpression } = expression as ts.ParenthesizedExpression;

	if (funcExpression.kind !== ts.SyntaxKind.FunctionExpression) {
		return false;
	}

	return true;
}

/**
* A require call is a call to the `require` function with a single argument.
*/
export function isRequireCall(callExpression: ts.Node): boolean {
	if (callExpression.kind !== ts.SyntaxKind.CallExpression) {
		return false;
	}

	const { expression, arguments: args } = callExpression as ts.CallExpression;

	if (expression.kind !== ts.SyntaxKind.Identifier || (expression as ts.Identifier).escapedText !== "require") {
		return false;
	}

	if (args.length !== 1) {
		return false;
	}

	return true;
}

export function getPropertyName(node: ts.PropertyName): string {
	switch (node.kind) {
		case ts.SyntaxKind.Identifier:
			return (<ts.Identifier>node).text;
		case ts.SyntaxKind.StringLiteral:
			return (<ts.StringLiteral>node).text;
		case ts.SyntaxKind.ComputedPropertyName:
			return (<ts.ComputedPropertyName>node).getFullText();
	}
}


/**
* Helper for vrotsc-annotations decorators
*/
export function getDecoratorNames(decorators: readonly ts.Decorator[]): string[] {
	if (decorators && decorators.length) {
		return decorators
			.filter(decoratorNode => decoratorNode.expression.kind === ts.SyntaxKind.Identifier)
			.map(decoratorNode => (<ts.Identifier>decoratorNode.expression).text);
	}
	return [];
}

export function hasModifier(modifiers: ts.NodeArray<ts.ModifierLike>, kind: ts.SyntaxKind): boolean {
	return modifiers != null && modifiers.some(x => x.kind === kind);
}

export function hasAnyModifier(modifiers: ts.NodeArray<ts.ModifierLike>, ...kinds: ts.SyntaxKind[]): boolean {
	return modifiers != null && modifiers.some(x => kinds.some(k => k === x.kind));
}

/**
* Fetches all leading comments for a node.
*/
export function getLeadingComments(sourceFile: ts.SourceFile, node: ts.Node): Comment[] {
	let text = sourceFile.text.substring(node.pos, node.end);
	let comments = ts.getLeadingCommentRanges(text, 0) || [];
	return comments.map(c => <Comment>{
		text: text.substring(c.pos, c.end),
		hasTrailingNewLine: c.hasTrailingNewLine,
		kind: c.kind,
		pos: c.pos,
		end: c.end,
	});
}

