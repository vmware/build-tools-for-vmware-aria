import * as ts from 'typescript';
import { ScriptTransformationContext, HierarchyFacts } from '../../../types';
import { NodeVisitor } from '../../visitor';
import { hasModifier, getPropertyName, getIdentifierTextOrNull } from '../helpers/node';
import { SCRIPT_VRO_GLOBAL } from '../helpers/VROES';


/**
* Traverses the nodes of the source file and collects the names of the namespaces.
*
* This is used to build the AST.
*
* @param sourceFile - The source file to transform.
* @param context - The context for the transformation.
* @returns The transformed source file.
*/
export function collectNamespaces(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
	const visitor = new NodeVisitor(visitNode, context);

	visitor.visitNode(sourceFile);

	return sourceFile;

	function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
		switch (node.kind) {
			case ts.SyntaxKind.ModuleDeclaration:
				visitModuleDeclaration(<ts.ModuleDeclaration>node);
				break;
		}
		return undefined;
	}

	function visitModuleDeclaration(node: ts.ModuleDeclaration): void {
		if (!hasModifier(node.modifiers, ts.SyntaxKind.ExportKeyword)) {
			const name = getPropertyName(node.name);
			if (name) {
				if (context.globalIdentifiers.indexOf(name) < 0) {
					context.file.hierarchyFacts |= HierarchyFacts.ContainsGlobalNamespace;
					context.globalIdentifiers.push(name);
				}
			}
		}
	}
}

/**
* Transforms the namespaces in the source file to use the VROES global object.
*
* Example:
* ```ts
namespace MyNamespace {
	export interface BufferLike {
		[offset: number]: number;
		length: number;
	}
	interface BufferLike2 {}

	export type MemberDecorator = void;
	type MemberDecorator2 = void;

	export function decorate(decorators: ClassDecorator[], target: Function) {}
	function decorate2(decorators: ClassDecorator[], target: Function) {}

	export const hasOwn = "val1";
	const hasOwn2 = "val2";

	export class Mirror {
		reflect(params: number) {}
	}
	class Mirror2 {
		reflect(params: number) {}
	}
}
* ```
* Transformed to:
* ```ts
(function () {
	var __global = System.getContext() || (function () {
		return this;
	}).call(null);
	var exports = {};

	var MyNamespace = __global.MyNamespace || (__global.MyNamespace = {});
	(function (MyNamespace) {
		function decorate(decorators, target) { }
		MyNamespace.decorate = decorate;
		function decorate2(decorators, target) { }
		MyNamespace.hasOwn = "val1";
		var hasOwn2 = "val2";
		var Mirror =  (function() {
	function Mirror() {}
	Mirror.prototype.reflect = function(params) { };
		return Mirror;
	}());
	MyNamespace.Mirror = Mirror;
	var Mirror2 = (function() {
		function Mirror2() {
		}
		Mirror2.prototype.reflect = function(params) { };
		return Mirror2;
	}());
		}) (MyNamespace);
	return exports;
	});
* ```
*
*/
export function transformNamespaces(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
	if (context.globalIdentifiers.length) {
		const statements = sourceFile.statements.map(node => {
			switch (node.kind) {
				case ts.SyntaxKind.VariableStatement:
					return transformNamespaceVariable(node as ts.VariableStatement);
				case ts.SyntaxKind.ExpressionStatement:
					return transformExpressionStatement(node as ts.ExpressionStatement);
			}
			return node;
		});

		return ts.updateSourceFileNode(
			sourceFile,
			ts.setTextRange(
				ts.createNodeArray([
					...statements
				]),
				sourceFile.statements));
	}

	return sourceFile;

	function transformNamespaceVariable(node: ts.VariableStatement): ts.VariableStatement {
		if (node.declarationList && node.declarationList.declarations && node.declarationList.declarations.length === 1) {
			const varNode = node.declarationList.declarations[0];
			if (!varNode.initializer) {
				const varName = getIdentifierTextOrNull(varNode.name);
				if (varName && context.globalIdentifiers.indexOf(varName) > -1) {
					return ts.updateVariableStatement(
						node,
						node.modifiers,
						ts.updateVariableDeclarationList(
							node.declarationList,
							[
								ts.updateVariableDeclaration(
									varNode,
									varNode.name,
									varNode.type,
									/* initializer */
									ts.createBinary(
										/* left */
										ts.createPropertyAccess(
											/* expression */
											ts.createIdentifier(SCRIPT_VRO_GLOBAL),
											/* name */
											ts.createIdentifier(varName),
										),
										/* operator */
										ts.createToken(ts.SyntaxKind.BarBarToken),
										/* right */
										ts.createParen(
											ts.createBinary(
												/* left */
												ts.createPropertyAccess(
													/* expression */
													ts.createIdentifier(SCRIPT_VRO_GLOBAL),
													/* name */
													ts.createIdentifier(varName),
												),
												/* operator */
												ts.createToken(ts.SyntaxKind.FirstAssignment),
												/* right */
												ts.createObjectLiteral([], false),
											)
										),
									)
								),
							]
						)
					);
				}
			}
		}
		return node;
	}

	function transformExpressionStatement(node: ts.ExpressionStatement): ts.ExpressionStatement {
		if (node.expression.kind === ts.SyntaxKind.CallExpression) {
			const callNode = node.expression as ts.CallExpression;
			if (callNode.arguments.length === 1 && callNode.arguments[0].kind === ts.SyntaxKind.BinaryExpression) {
				const firstCallArg = callNode.arguments[0] as ts.BinaryExpression;
				const ns = getIdentifierTextOrNull(firstCallArg.left);
				if (ns && context.globalIdentifiers.indexOf(ns) > -1) {
					(callNode.arguments[0] as any) = firstCallArg.left;
				}
			}
		}
		return node;
	}
};
