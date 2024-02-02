import * as ts from 'typescript';
import { createVisitor } from '../../visitor';
import { ScriptTransformationContext, HierarchyFacts } from '../../../types';
import { isActionClosure } from '../helpers/node';
import { SCRIPT_VROES_VAR } from '../helpers/VROES';

/**
* Collects facts about the source file before transforming it.
* Facts are used to determine if the source file contains certain constructs.
* Facts collected:
* - ContainsActionClosure
*   - If the source file contains an action closure.
*     Example: var foo = function() { ... };
* - ContainsEcmaScriptExport
*   - If the source file contains an ECMAScript export.
*     Example: export { foo };
* - ContainsVroesReference
*   - If the source file contains a reference to the VROES variable.
*     Example: VROES...
*
* NOTE: This function is called before transforming the source file,
*   it could speed up the process if we don't do this.
*/
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
