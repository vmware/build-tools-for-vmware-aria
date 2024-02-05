import * as ts from "typescript";
import { HierarchyFacts, ScriptTransformationContext } from "../../../types";
import { NodeVisitor } from "../../visitor";
import { getIdentifierTextOrNull } from "../helpers/node";
import { transformSpreadAssignment } from "../codeTransformers//object";
import { transformSpreadElement } from "../codeTransformers/array";
import { createDeclarationPrologueStatements } from "./prologueStatements";

// Shims are used to replace the built-in objects with the VROES shims.
// This is done to ensure that the built-in objects work as expected in the Aria Orchestrator environment.

/**
* Containst the names of the shims that are used to replace the built-in objects.
*/
const ShimReferences: Record<string, ShimReference> = {
	"Map": {
		typeName: "MapConstructor",
		facts: HierarchyFacts.ContainsMap,
	},
	"WeakMap": {
		typeName: "WeakMapConstructor",
		facts: HierarchyFacts.ContainsWeakMap,
	},
	"Set": {
		typeName: "SetConstructor",
		facts: HierarchyFacts.ContainsSet,
	},
	"WeakSet": {
		typeName: "WeakSetConstructor",
		facts: HierarchyFacts.ContainsWeakSet,
	},
	"Promise": {
		typeName: "PromiseConstructor",
		facts: HierarchyFacts.ContainsPromise,
	},
};

interface ShimReference {
	typeName: string;
	facts: HierarchyFacts;
}

/**
* Visits the entire source file and replaces the built-in objects with the VROES shims.
*
* Example:
* ```ts
* var Map = VROES.Map;
* var WeakMap = VROES.WeakMap;
* var Set = VROES.Set;
* var WeakSet = VROES.WeakSet;
* var Promise = VROES.Promise;
* // and
* "foo".startsWith("f"); // Transformed to VROES.Shims.stringStartsWith("foo", "f");
* "foo".endsWith("o"); // Transformed to VROES.Shims.stringEndsWith("foo", "o");
* "foo".includes("o"); // Transformed to VROES.Shims.stringIncludes("foo", "o");
* // etc...
* ````
*
* @param sourceFile - The source file to transform.
* @param context - The transformation context.
* @returns - The transformed source file.
*/
export function transformShims(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
	const visitor = new NodeVisitor(visitNode, context);

	return visitSourceFile(sourceFile);

	function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
		switch (node.kind) {
			case ts.SyntaxKind.CallExpression:
				return visitCallExpression(<ts.CallExpression>node);

			case ts.SyntaxKind.Identifier:
				return visitIdentifier(<ts.Identifier>node);
		}
	}

	/**
	* Visits the entire source file and replaces variables declarations with the VROES shims.
	*/
	function visitIdentifier(node: ts.Identifier): ts.Identifier {
		const shimRef = ShimReferences[node.text];
		if (shimRef) {
			const symbol = context.typeChecker.getSymbolAtLocation(node);
			if (symbol && symbol.valueDeclaration && symbol.valueDeclaration.kind === ts.SyntaxKind.VariableDeclaration) {
				const symbolVarDecl = symbol.valueDeclaration as ts.VariableDeclaration;
				if (symbolVarDecl.type && symbolVarDecl.type.kind === ts.SyntaxKind.TypeReference) {
					const typeRef = symbolVarDecl.type as ts.TypeReferenceNode;
					if (getIdentifierTextOrNull(typeRef.typeName) === shimRef.typeName) {
						context.file.hierarchyFacts |= shimRef.facts;
					}
				}
			}
		}

		return visitor.visitEachChild(node);
	}

	/**
	* Will place the prologue statements at the top of the file and replace the built-in objects with the VROES shims.
	*/
	function visitSourceFile(node: ts.SourceFile): ts.SourceFile {
		const statements = visitor.visitNodes(node.statements);
		const prologue = createDeclarationPrologueStatements(context);

		return ts.updateSourceFileNode(
			node,
			ts.setTextRange(
				ts.createNodeArray([
					...prologue,
					...statements,
				]),
				node.statements));
	}

	/**
	* Visits the entire source file and replaces some built-in methods with the VROES shims.
	*
	* Example:
	* ```ts
	* "foo".startsWith("f"); // Transformed to VROES.Shims.stringStartsWith("foo", "f");
	* "foo".endsWith("o"); // Transformed to VROES.Shims.stringEndsWith("foo", "o");
	* "foo".includes("o"); // Transformed to VROES.Shims.stringIncludes("foo", "o");
	* // etc...
	* ```
	*
	* @param node - The node to visit.
	* @returns - The result of visiting the node.
	*/
	function visitCallExpression(node: ts.CallExpression): ts.Expression {
		const symbol = context.typeChecker.getSymbolAtLocation(node.expression);
		if (symbol) {
			switch (context.typeChecker.getFullyQualifiedName(symbol)) {
				case "String.startsWith":
					return shimInstanceCall("stringStartsWith", node);
				case "String.endsWith":
					return shimInstanceCall("stringEndsWith", node);
				case "String.includes":
					return shimInstanceCall("stringIncludes", node);
				case "String.repeat":
					return shimInstanceCall("stringRepeat", node);
				case "String.padStart":
					return shimInstanceCall("stringPadStart", node);
				case "String.padEnd":
					return shimInstanceCall("stringPadEnd", node);
				case "Array.find":
					return shimInstanceCall("arrayFind", node);
				case "Array.findIndex":
					return shimInstanceCall("arrayFindIndex", node);
				case "Array.fill":
					return shimInstanceCall("arrayFill", node);
				case "ArrayConstructor.from":
					return shimStaticCall("arrayFrom", node);
				case "ArrayConstructor.of":
					return shimStaticCall("arrayOf", node);
				case "ObjectConstructor.assign":
					return shimStaticCall("objectAssign", node);
			}
		}

		// Visit the children of the node.
		return visitor.visitEachChild(node);
	}


	/**
	* Helper function to shim instance calls.
	*
	* if the node is a call expression and the expression is a property access expression,
	* then it will be replaced with the VROES shims.
	*/
	function shimInstanceCall(methodName: string, node: ts.CallExpression): ts.CallExpression {
		if (node.expression.kind !== ts.SyntaxKind.PropertyAccessExpression) {
			return visitor.visitEachChild(node);
		}

		context.file.hierarchyFacts |= HierarchyFacts.ContainsPolyfills;

		let args: ts.Expression[] = [];
		args.push((<ts.PropertyAccessExpression>node.expression).expression);
		node.arguments.forEach(n => args.push(n));

		return ts.createCall(
			ts.createPropertyAccess(
				ts.createPropertyAccess(
					ts.createIdentifier("VROES"),
					"Shims"),
				methodName),
			undefined,
			visitor.visitNodes(ts.createNodeArray(args, false)));
	}

	/**
	* Same as shimInstanceCall but for static calls.
	*/
	function shimStaticCall(methodName: string, node: ts.CallExpression): ts.CallExpression {
		context.file.hierarchyFacts |= HierarchyFacts.ContainsPolyfills;

		return ts.createCall(
			ts.createPropertyAccess(
				ts.createPropertyAccess(
					ts.createIdentifier("VROES"),
					"Shims"),
				methodName),
			undefined,
			visitor.visitNodes(node.arguments));
	}
}

/**
* transformShimsBefore function transforms the source file to shim array and object spread.
*
* This function is used to transform the source file to shim array and object spread.
*
* Example:
* ```ts
* const arr = [1, 2, 3];
* const arr2 = [...arr, 4, 5, 6]; // Transformed to VROES.Shims.spreadArrays(arr, 4, 5, 6);
* const obj = { a: 1, b: 2 };
* const obj2 = { ...obj, c: 3, d: 4 }; // Transformed to VROES.Shims.objectAssign(obj, { c: 3, d: 4 });
* ```
*
* NOTE: This must be called before the transformShims function.
*
* @param sourceFile - The source file to transform.
* @param context - The transformation context.
* @returns - The transformed source file.
*/
export function transformShimsBefore(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
	const visitor = new NodeVisitor(visitNode, context);

	return visitSourceFile(sourceFile);

	function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
		switch (node.kind) {
			case ts.SyntaxKind.ArrayLiteralExpression:
				return visitArrayLiteralExpression(<ts.ArrayLiteralExpression>node);
			case ts.SyntaxKind.ObjectLiteralExpression:
				return visitObjectLiteralExpression(<ts.ObjectLiteralExpression>node);
		}
	}

	/**
	* Visits the entire source file and replaces the spread elements with the VROES shims.
	*/
	function visitSourceFile(node: ts.SourceFile): ts.SourceFile {
		const statements = visitor.visitNodes(node.statements);

		return ts.updateSourceFileNode(
			node,
			ts.setTextRange(
				ts.createNodeArray([
					...statements,
				]),
				node.statements));
	}

	function visitArrayLiteralExpression(node: ts.ArrayLiteralExpression): ts.Node {
		const hasSpreadElement = node.elements.some(e => e.kind === ts.SyntaxKind.SpreadElement);
		if (hasSpreadElement) {
			context.file.hierarchyFacts |= HierarchyFacts.ContainsSpreadArray;
			return transformSpreadElement(visitor, node);
		}
		return node;
	}

	function visitObjectLiteralExpression(node: ts.ObjectLiteralExpression): ts.Node {
		const hasSpreadAssignment = node.properties.some(e => e.kind === ts.SyntaxKind.SpreadAssignment);
		if (hasSpreadAssignment) {
			context.file.hierarchyFacts |= HierarchyFacts.ContainsSpreadOperator;
			return transformSpreadAssignment(visitor, node);
		}
		return node;
	}
}
