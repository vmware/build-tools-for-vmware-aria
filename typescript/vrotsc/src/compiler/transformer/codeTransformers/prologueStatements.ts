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
			ts.createVariableDeclaration(
				"Map",
                    /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"Map"
				)
			)
		);
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsWeakMap) {
		// var WeakMap = VROES.Map
		variableDeclarations.push(
			ts.createVariableDeclaration(
				"WeakMap",
                        /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"Map"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsSet) {
		// var Set = VROES.Set
		variableDeclarations.push(
			ts.createVariableDeclaration(
				"Set",
                        /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"Set"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsWeakSet) {
		// var WeakSet = VROES.Set
		variableDeclarations.push(
			ts.createVariableDeclaration(
				"WeakSet",
                        /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"Set"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsPromise) {
		// var Promise = VROES.Promise
		variableDeclarations.push(
			ts.createVariableDeclaration(
				"Promise",
                        /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"Promise"
				)
			));
	}

	if (variableDeclarations.length) {
		statements.push(ts.createVariableStatement(/*modifiers*/ undefined, variableDeclarations));
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
		statements.push(ts.createVariableStatement(
                    /*modifiers*/ undefined,
			[
				ts.createVariableDeclaration(
                            /*name*/ SCRIPT_VRO_GLOBAL,
                            /*type*/ undefined,
					ts.createBinary(
						ts.createCall(
							ts.createPropertyAccess(ts.createIdentifier("System"), "getContext"),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/ undefined),
						ts.createToken(ts.SyntaxKind.BarBarToken),
						ts.createCall(
							ts.createPropertyAccess(
								ts.createParen(
									ts.createFunctionExpression(
                                                /*modifiers*/ undefined,
                                                /*asteriskToken*/ undefined,
                                                /*name*/ undefined,
                                                /*typeParameters*/ undefined,
                                                /*parameters*/ undefined,
                                                /*modifiers*/ undefined,
                                                /*body*/ ts.createBlock([ts.createReturn(ts.createThis())], true))),
								"call"
							),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/[ts.createNull()]
						)
					)
				)
			]));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.VROES) {
		// var VROES = __global.VROES || (__global.VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES())
		variableDeclarations.push(
			ts.createVariableDeclaration(
                        /*name*/ SCRIPT_VROES_VAR,
                        /*type*/ undefined,
				ts.createBinary(
					ts.createPropertyAccess(
						ts.createIdentifier(SCRIPT_VRO_GLOBAL),
						SCRIPT_VROES_CACHE
					),
					ts.createToken(ts.SyntaxKind.BarBarToken),
					ts.createParen(
						ts.createBinary(
							ts.createPropertyAccess(
								ts.createIdentifier(SCRIPT_VRO_GLOBAL),
								SCRIPT_VROES_CACHE
							),
							ts.createToken(ts.SyntaxKind.EqualsToken),
							ts.createCall(
								ts.createPropertyAccess(
									ts.createCall(
										ts.createPropertyAccess(ts.createIdentifier("System"), "getModule"),
                                                /* typeArguments */ undefined,
										[
											ts.createLiteral(SCRIPT_VRO_MODULE_PACKAGE)
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
			ts.createVariableDeclaration(
				"require",
                        /*type*/ undefined,
				ts.createPropertyAccess(
					ts.createIdentifier(SCRIPT_VROES_VAR),
					"require"
				)
			));
	}

	if (context.file.hierarchyFacts & HierarchyFacts.ContainsTSLib) {
		variableDeclarations.push(
			ts.createVariableDeclaration(tslibVarName,
				undefined,
				ts.createPropertyAccess(ts.createIdentifier(SCRIPT_VROES_VAR), SCRIPT_HELPER_MODULE)
			));
	}

	if (!(context.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) && context.file.type === FileType.Action) {
		// var exports = {}
		variableDeclarations.push(
			ts.createVariableDeclaration(
				"exports",
                        /*type*/ undefined,
				ts.createObjectLiteral([])
			));
	}

	if (variableDeclarations.length) {
		statements.push(ts.createVariableStatement(/*modifiers*/ undefined, variableDeclarations));
	}

	return statements;
}
