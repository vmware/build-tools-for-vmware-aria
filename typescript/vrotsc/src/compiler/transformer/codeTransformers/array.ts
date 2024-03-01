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
import { ScriptTransformationContext, Visitor } from "../../../types";

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
export function transformSpreadElement(visitor: Visitor, node: ts.ArrayLiteralExpression, context: ScriptTransformationContext): ts.Node {
	const arrays: ts.Expression[] = [];
	let currentElems: ts.Expression[] = [];

	const addCurrentProps = () => {
		if (currentElems.length) {
			arrays.push(ts.factory.createArrayLiteralExpression(currentElems));
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

	return ts.factory.createCallExpression(
		ts.factory.createPropertyAccessExpression(
			ts.factory.createPropertyAccessExpression(
				ts.factory.createIdentifier("VROES"),
				"Shims"),
			"spreadArrays"),
		undefined,
		arrays.map(node => visitor.visitNode(node) as ts.Expression));
}
