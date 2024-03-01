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
