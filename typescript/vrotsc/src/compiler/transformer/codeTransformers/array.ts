import * as ts from "typescript";
import { Visitor } from "../../../types";

/**
* Transforms a spread element in an array literal into a call to `VROES.Shims.spreadArrays`.
* Example:
* ```ts
* const arr = [1, 2, 3];
* const arr2 = [...arr, 4, 5, 6]; // Transformed to VROES.Shims.spreadArrays(arr, 4, 5, 6);
* ```
* @param visitor - The visitor to use.
* @param node - The node to transform.
*/
export function transformSpreadElement(visitor: Visitor, node: ts.ArrayLiteralExpression): ts.Node {
	const arrays: ts.Expression[] = [];
	let currentElems: ts.Expression[] = [];

	const addCurrentProps = () => {
		if (currentElems.length) {
			arrays.push(ts.createArrayLiteral(currentElems));
			currentElems = [];
		}
	};

	for (const elem of node.elements) {
		if (elem.kind === ts.SyntaxKind.SpreadElement) {
			addCurrentProps();
			arrays.push((<ts.SpreadElement>elem).expression);
		}
		else {
			currentElems.push(elem);
		}
	}

	addCurrentProps();

	return ts.createCall(
		ts.createPropertyAccess(
			ts.createPropertyAccess(
				ts.createIdentifier("VROES"),
				"Shims"),
			"spreadArrays"),
		undefined,
		arrays.map(node => visitor.visitNode(node) as ts.Expression));
}
