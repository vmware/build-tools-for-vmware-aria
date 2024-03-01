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
			objects.push(ts.factory.createObjectLiteralExpression(currentProps));
			currentProps = [];
		}
	};

	objects.push(ts.factory.createObjectLiteralExpression([]));

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

	return ts.factory.createCallExpression(
		ts.factory.createPropertyAccessExpression(
			ts.factory.createPropertyAccessExpression(
				ts.factory.createIdentifier("VROES"),
				"Shims"),
			"objectAssign"),
		undefined,
		objects.map(node => visitor.visitNode(node) as ts.Expression));
}
