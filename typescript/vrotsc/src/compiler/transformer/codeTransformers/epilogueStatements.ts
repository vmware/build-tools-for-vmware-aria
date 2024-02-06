import * as ts from "typescript";
import { HierarchyFacts, FileType, ScriptTransformationContext } from "../../../types";

/**
* Epilogue statements are statements that are added at the end of the file.
*
* In this case, we add the return statement for the exports variable if the file is an action.
*
* @returns The epilogue statements.
*/
export function createEpilogueStatements(context: ScriptTransformationContext): ts.Statement[] {
	const statements: ts.Statement[] = [];

	if (!(context.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) && context.file.type === FileType.Action) {
		statements.push(ts.createReturn(ts.createIdentifier("exports")));
	}

	return statements;
}
