import * as ts from "typescript";
import { HierarchyFacts, ScriptTransformationContext } from "../../../types";
import { SCRIPT_VROES_VAR } from "../helpers/VROES";

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
export function createPrologueStatements(context: ScriptTransformationContext): ts.Statement[] {
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
