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
		statements.push(ts.factory.createReturnStatement(ts.factory.createIdentifier("exports")));
	}

	return statements;
}
