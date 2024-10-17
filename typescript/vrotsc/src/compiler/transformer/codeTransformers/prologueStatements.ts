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
import { HierarchyFacts, ScriptTransformationContext, FileType } from "../../../types";
import { SCRIPT_VROES_VAR, SCRIPT_VRO_GLOBAL, SCRIPT_VROES_CACHE, SCRIPT_VRO_MODULE_PACKAGE, SCRIPT_VROES_MODULE, SCRIPT_HELPER_MODULE } from "../helpers/VROES";

// Prologue statements are the first statements in a module. They
// are used in our case to add Shim functions to the module.

/**
* Shims for some built-in objects.
*
* Example:
* ```ts
* var Map = VROES.Map;
* var WeakMap = VROES.WeakMap;
* var Set = VROES.Set;
* var WeakSet = VROES.WeakSet;
* var Promise = VROES.Promise;
* ```
* These will be added to the top(prologue) of the file. All calls to these objects will be replaced with the VROES shims.
*/
export function createDeclarationPrologueStatements(context: ScriptTransformationContext): ts.Statement[] {
	const statements: ts.Statement[] = [];
	const variableDeclarations: ts.VariableDeclaration[] = [];

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsMap) {
		// var Map = VROES.Map
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"Map",
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"Map"
				)
			)
		);
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsWeakMap) {
		// var WeakMap = VROES.Map
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"WeakMap",
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"Map"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsSet) {
		// var Set = VROES.Set
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"Set",
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"Set"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsWeakSet) {
		// var WeakSet = VROES.Set
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"WeakSet",
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"Set"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsPromise) {
		// var Promise = VROES.Promise
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"Promise",
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"Promise"
				)
			));
	}

	if (variableDeclarations.length) {
		statements.push(ts.factory.createVariableStatement(/*modifiers*/ undefined, variableDeclarations));
	}

	return statements;
}

/**
* Prologue statements are statements that are added at the beginning of the file.
*
* In this case, we add the global variable __global and the VROES variable.
*
* This also includes the require variable if the file contains a require call.
* This also includes the exports variable if the file is an action.
* Any other variables for the module scope are added as well.
*/
export function createModulePrologueStatements(context: ScriptTransformationContext, tslibVarName: string): ts.Statement[] {
	const statements: ts.Statement[] = [];
	const variableDeclarations: ts.VariableDeclaration[] = [];
	if (context.file.hierarchyFacts & HierarchyFacts.GlobalScope || context.globalIdentifiers.length) {
		// Create the following statement:
		// var __global = (function () {
		//     return this;
		// }).call(null);
		statements.push(ts.factory.createVariableStatement(
                    /*modifiers*/ undefined,
			[
				ts.factory.createVariableDeclaration(
                            /*name*/ SCRIPT_VRO_GLOBAL,
					undefined,
					undefined,
					ts.factory.createBinaryExpression(
						ts.factory.createCallExpression(
							ts.factory.createPropertyAccessExpression(ts.factory.createIdentifier("System"), "getContext"),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/ undefined),
						ts.factory.createToken(ts.SyntaxKind.BarBarToken),
						ts.factory.createCallExpression(
							ts.factory.createPropertyAccessExpression(
								ts.factory.createParenthesizedExpression(
									ts.factory.createFunctionExpression(
                                                /*modifiers*/ undefined,
                                                /*asteriskToken*/ undefined,
                                                /*name*/ undefined,
                                                /*typeParameters*/ undefined,
                                                /*parameters*/ undefined,
                                                /*modifiers*/ undefined,
                                                /*body*/ ts.factory.createBlock([ts.factory.createReturnStatement(ts.factory.createThis())], true))),
								"call"
							),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/[ts.factory.createNull()]
						)
					)
				)
			]));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.VROES) {
		// var VROES = __global.VROES || (__global.VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES())
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
                        /*name*/ SCRIPT_VROES_VAR,
				undefined,
                        /*type*/ undefined,
				ts.factory.createBinaryExpression(
					ts.factory.createPropertyAccessExpression(
						ts.factory.createIdentifier(SCRIPT_VRO_GLOBAL),
						SCRIPT_VROES_CACHE
					),
					ts.factory.createToken(ts.SyntaxKind.BarBarToken),
					ts.factory.createParenthesizedExpression(
						ts.factory.createBinaryExpression(
							ts.factory.createPropertyAccessExpression(
								ts.factory.createIdentifier(SCRIPT_VRO_GLOBAL),
								SCRIPT_VROES_CACHE
							),
							ts.factory.createToken(ts.SyntaxKind.EqualsToken),
							ts.factory.createCallExpression(
								ts.factory.createPropertyAccessExpression(
									ts.factory.createCallExpression(
										ts.factory.createPropertyAccessExpression(ts.factory.createIdentifier("System"), "getModule"),
                                                /* typeArguments */ undefined,
										[
											ts.factory.createStringLiteral(SCRIPT_VRO_MODULE_PACKAGE)
										]),
									SCRIPT_VROES_MODULE
								),
                                        /* typeArguments */ undefined,
                                        /* argumentsArray */ undefined
							)
						)
					)
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsRequire) {
		// var require = VROES.require
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"require",
        /*type*/undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(
					ts.factory.createIdentifier(SCRIPT_VROES_VAR),
					"require"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsTSLib) {
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(tslibVarName,
				undefined,
				undefined,
				ts.factory.createPropertyAccessExpression(ts.factory.createIdentifier(SCRIPT_VROES_VAR), SCRIPT_HELPER_MODULE)
			));
	}

	if (!(context.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) && context.file.type === FileType.Action) {
		variableDeclarations.push(
			ts.factory.createVariableDeclaration(
				"exports",
                        /*type*/ undefined,
				undefined,
				ts.factory.createObjectLiteralExpression([])
			));
	}

	if (variableDeclarations.length) {
		statements.push(ts.factory.createVariableStatement(/*modifiers*/ undefined, variableDeclarations));
	}

	return statements;
}

/**
 * This will create the prologue statements for the workflow item.
 *
 * The prologue statements are the statements that are added at the beginning of the file.
 * In this case, we add the variable declarations for the parameters of the method.
 *
 * @param methodNode - The method node.
 * @returns The prologue statements.
 */
export function createWorkflowItemPrologueStatements(methodNode: ts.MethodDeclaration): ts.Statement[] {
	const statements: ts.Statement[] = [];

	if (methodNode.parameters.length) {
		const variableDeclarations: ts.VariableDeclaration[] = [];
		methodNode.parameters.forEach(paramNode => {
			const paramName = (<ts.Identifier>paramNode.name).text;
			variableDeclarations.push(ts.factory.createVariableDeclaration(
				paramName,
				undefined,
				paramNode.type,
                /* initializer */ undefined
			));
		});

		if (variableDeclarations.length) {
			statements.push(ts.factory.createVariableStatement(
				[ts.factory.createModifier(ts.SyntaxKind.DeclareKeyword)],
				variableDeclarations));
		}
	}

	return statements;
}
