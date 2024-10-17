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
import { ClassDeclaration, getDecorators, CallExpression, SyntaxKind } from "typescript";

/**
 * Checks an action for class-level decorators, associated with an unintentionally omitted prefix to the ".ts" extension:
 * - Configuration (.conf.ts)
 * - PolicyTemplate (.pl.ts)
 * - Workflow (.wf.ts)
 * @param {ClassDeclaration} classNode - class node in the action
 * @throws Error if such decorator is found to indicate possibly omitted extension prefix
 * (otherwise Actions as functions are not expected to have class-level decorators).
 */
export function checkActionForMisplacedClassDecorators(classNode: ClassDeclaration) {
	const decoratorToFileSuffixMap = {
		"PolicyTemplate": "pl",
		"Configuration": "conf",
		"Workflow": "wf"
	};
	const misplacedDecorator = getDecorators(classNode)
		?.map(decoratorNode => (decoratorNode.expression as CallExpression).expression as { kind?: SyntaxKind; text?: string; })
		.find(expression => expression?.kind === SyntaxKind.Identifier && expression?.text in decoratorToFileSuffixMap)?.text;
	if (misplacedDecorator) {
		throw new Error(`Typescript ${misplacedDecorator} file names need to be in the format `
			+ `'<fileName>.${decoratorToFileSuffixMap[misplacedDecorator]}.ts'!`
		);
	}
}
