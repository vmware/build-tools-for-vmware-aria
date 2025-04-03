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

/**
* Verify if a jest method that requires module name is being called,
*   e.g. jest.methodName("path/name"[, args])
*/
export function isJestMethodCallWithModuleName(callExpression: ts.CallExpression): boolean {
	if (callExpression.kind !== ts.SyntaxKind.CallExpression) {
		return false;
	}
	if (callExpression.expression.kind !== ts.SyntaxKind.PropertyAccessExpression) {
		return false;
	}
	if (callExpression.arguments.length === 0 || callExpression.arguments[0].kind !== ts.SyntaxKind.StringLiteral) {
		return false;
	}

	const methodExpression = callExpression.expression as ts.PropertyAccessExpression;
	if (methodExpression.expression.kind !== ts.SyntaxKind.Identifier || methodExpression.name.kind !== ts.SyntaxKind.Identifier) {
		return false;
	}
	if ((methodExpression.expression as ts.Identifier).escapedText !== "jest") {
		return false;
	}

	const methodName = (methodExpression.name as ts.Identifier).escapedText;
	switch(methodName) {
		case "createMockFromModule":
		case "mock":
		case "unmock":
		case "deepUnmock":
		case "doMock":
		case "dontmock":
		case "setMock":
		case "requireActual":
		case "requireMock":
			return true;
	}
	return false;
}

export function getPropertyName(node: ts.PropertyName): string {
	switch (node.kind) {
		case ts.SyntaxKind.Identifier:
			return (node).text;
		case ts.SyntaxKind.StringLiteral:
			return (node).text;
		case ts.SyntaxKind.ComputedPropertyName:
			return (node).getFullText();
	}
}


/**
* Helper for vrotsc-annotations decorators
*/
export function getDecoratorNames(decorators: readonly ts.Decorator[]): string[] {
	if (!decorators?.length) {
		return [];
	}

	return decorators
		.filter(decoratorNode => decoratorNode.expression.kind === ts.SyntaxKind.Identifier)
		.map(decoratorNode => (<ts.Identifier>decoratorNode.expression).text);
}

/**
 * Wrapper for getDecoratorNames that returns the first decorator name or null if none are found.
 */
export function getDecoratorName(decorator: ts.Decorator): string {
	const result = getDecoratorNames([decorator]);

	return result.length > 0 ? result[0] : null;
}

export function hasModifier(modifiers: ts.NodeArray<ts.ModifierLike>, kind: ts.SyntaxKind): boolean {
	return modifiers?.some(x => x.kind === kind);
}

export function hasAnyModifier(modifiers: ts.NodeArray<ts.ModifierLike>, ...kinds: ts.SyntaxKind[]): boolean {
	return modifiers?.some(x => kinds.some(k => k === x.kind));
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

/**
 * This will fetch all the props of a decoratorNode
 *
 * @param decoratorNode The decorator node to get the properties from.
 * @returns An array of key-value touple arrays.
 */
export function getDecoratorProps(decoratorNode: ts.Decorator): [string, any][] {
	const decoratorValues = [];
	const objLiteralNode = (decoratorNode.expression as ts.CallExpression)?.arguments?.[0] as ts.ObjectLiteralExpression;

	if (!objLiteralNode) {
		return decoratorValues;
	}
	objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
		const propName = getPropertyName(property.name);
		const propValue = (<ts.StringLiteral>property.initializer).text;

		decoratorValues.push([propName, propValue]);
	});

	return decoratorValues;
}

