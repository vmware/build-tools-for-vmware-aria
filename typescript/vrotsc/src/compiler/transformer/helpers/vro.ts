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

/**
* Maps a TypeScript type to a VRO type.
*
* This is used in the config and workflow file transformers when generating the
* XML representation of the Resources.
*/
export function getVroType(typeNode: ts.TypeNode): string {
	switch (typeNode.kind) {
		case ts.SyntaxKind.StringKeyword:
			return "string";
		case ts.SyntaxKind.NumberKeyword:
			return "number";
		case ts.SyntaxKind.BooleanKeyword:
			return "boolean";
		case ts.SyntaxKind.ArrayType:
			return "Array/" + getVroType((<ts.ArrayTypeNode>typeNode).elementType);
		default:
			return "Any";
	}
}
