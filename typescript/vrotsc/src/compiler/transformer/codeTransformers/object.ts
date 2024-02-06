import * as ts from "typescript";
import { Visitor } from "../../../types";

/**
* Transforms a spread assignment in an object literal into a call to `VROES.Shims.objectAssign`.
* Example:
* ```ts
* const obj = { a: 1, b: 2 };
* const obj2 = { ...obj, c: 3, d: 4 }; // Transformed to VROES.Shims.objectAssign(obj, { c: 3, d: 4 });
* ```
*/
export function transformSpreadAssignment(visitor: Visitor, node: ts.ObjectLiteralExpression): ts.Node {
	const objects: ts.Expression[] = [];
	let currentProps: ts.ObjectLiteralElementLike[] = [];

	const addCurrentProps = () => {
		if (currentProps.length) {
			objects.push(ts.createObjectLiteral(currentProps));
			currentProps = [];
		}
	};

	objects.push(ts.createObjectLiteral([]));

	for (const property of node.properties) {
		if (property.kind === ts.SyntaxKind.SpreadAssignment) {
			addCurrentProps();
			objects.push((<ts.SpreadAssignment>property).expression);
		}
		else {
			currentProps.push(property);
		}
	}

	addCurrentProps();

	return ts.createCall(
		ts.createPropertyAccess(
			ts.createPropertyAccess(
				ts.createIdentifier("VROES"),
				"Shims"),
			"objectAssign"),
		undefined,
		objects.map(node => visitor.visitNode(node) as ts.Expression));
}
