import * as ts from 'typescript';
import { createVisitor } from '../../visitor';

export function collectFactsBefore(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
	const visitor = createVisitor(visitNode, context);
	const actionClosureIndex = sourceFile.statements.findIndex(isActionClosure);

	if (actionClosureIndex > -1 && actionClosureIndex === sourceFile.statements.length - 1) {
		context.file.hierarchyFacts |= HierarchyFacts.ContainsActionClosure;
	}

	visitor.visitNode(sourceFile);

	return sourceFile;

	function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
		switch (node.kind) {
			case ts.SyntaxKind.Identifier:
				visitIdentifier(<ts.Identifier>node);
				break;

			case ts.SyntaxKind.ExportKeyword:
			case ts.SyntaxKind.ExportDeclaration:
			case ts.SyntaxKind.ExportAssignment:
			case ts.SyntaxKind.ExportSpecifier:
				context.file.hierarchyFacts |= HierarchyFacts.ContainsEcmaScriptExport;
				break;
		}
		return undefined;
	}

	function visitIdentifier(node: ts.Identifier): void {
		if (node.text === SCRIPT_VROES_VAR) {
			context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference;
		}
	}
}
