import * as ts from "typescript";

/**
* Maps a TypeScript type to a VRO type.
* @TODO: What is it used for?
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
