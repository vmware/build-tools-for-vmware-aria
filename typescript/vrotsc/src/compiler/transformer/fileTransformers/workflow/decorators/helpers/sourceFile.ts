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
import { MethodDeclaration, SourceFile, SyntaxKind, factory } from "typescript";
import { printSourceFile } from "../../../../helpers/source";
import { createWorkflowItemPrologueStatements } from "../../../../codeTransformers/prologueStatements";
import { WorkflowItemDescriptor } from "../../../../../decorators";

export interface SourceFilePrinter {
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string;
}

/**
 * Default source file printer will directly print the source file with the method node body statements.
 */
export class DefaultSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					...methodNode.body.statements
				]
			)
		);
	}
}

/**
 * This source file printer will wrap the method node body statements with a function declaration.
 *
 *  This wrapping is necessary because "return"s are not allowed in the root of a typescript file, but are allowed in the case of vRO.
 *  The "wrapper" function will later be removed in the transpilation process.
 *  @NOTE: This is 100% due to a typescript limitation, and not a vRO limitation.
 */
export class WrapperSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		const wrapperFunction = factory.createFunctionDeclaration(
			undefined,
			undefined,
			"wrapper",
			undefined,
			[],
			undefined,
			methodNode.body
		);
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					wrapperFunction
				]
			)
		);
	}
}

/**
 * This is used to print the source file for a scheduled workflow item.
 *
 * The Scheduled workflow is essentially just a normal task with special representation.
 *
 * @Example of what is printed:
// var workflowToLaunch = Server.getWorkflowWithId("9e4503db-cbaa-435a-9fad-144409c08df0");
// if (workflowToLaunch == null) {
// 	throw "Workflow not found";
// }
//
// var workflowParameters = new Properties();
// workflowParameters.put("first",first);
// workflowParameters.put("second",second);
// scheduledTask = workflowToLaunch.schedule(workflowParameters, workflowScheduleDate);
 *
 */
export class ScheduledWorkflowItemSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					// Variable declarations are on top
					factory.createVariableStatement(
						undefined,
						// A list of declarations
						factory.createVariableDeclarationList(
							[
								// `var workflowParameters = new Properties();`
								factory.createVariableDeclaration(
									"workflowParameters",
									undefined,
									undefined,
									factory.createNewExpression(
										factory.createIdentifier("Properties"),
										undefined,
										[]
									)
								),
								// `, workflowToLaunch = Server.getWorkflowWithId("some id here");`
								factory.createVariableDeclaration(
									"workflowToLaunch",
									undefined,
									undefined,
									factory.createCallExpression(
										factory.createPropertyAccessExpression(
											factory.createIdentifier("Server"),
											factory.createIdentifier("getWorkflowWithId")
										),
										undefined,
										[factory.createStringLiteral(itemInfo.canvasItemPolymorphicBag.linkedItem)]
									)
								)
							],
							undefined
						)
					),

					// `if (workflowToLaunch == null) { throw "Workflow not found"; }`
					factory.createIfStatement(
						factory.createBinaryExpression(
							factory.createIdentifier("workflowToLaunch"),
							factory.createToken(SyntaxKind.EqualsEqualsToken),
							factory.createNull()
						),
						factory.createBlock(
							[
								factory.createThrowStatement(
									factory.createStringLiteral("Workflow not found")
								)
							],
							true
						)
					),

					// `workflowParameters.put("first",first);`
					// `workflowParameters.put("second",second);`
					// ...... etc
					...itemInfo.input.filter(i => i !== "workflowScheduleDate").map((input) => {
						return factory.createExpressionStatement(
							factory.createCallExpression(
								factory.createPropertyAccessExpression(
									factory.createIdentifier("workflowParameters"),
									factory.createIdentifier("put")
								),
								undefined,
								[
									factory.createStringLiteral(input),
									factory.createIdentifier(input)
								]
							)
						);
					}),

					// `scheduledTask = workflowToLaunch.schedule(workflowParameters, workflowScheduleDate);`
					factory.createExpressionStatement(
						factory.createAssignment(
							factory.createIdentifier("scheduledTask"),
							factory.createCallExpression(
								factory.createPropertyAccessExpression(
									factory.createIdentifier("workflowToLaunch"),
									factory.createIdentifier("schedule")
								),
								undefined,
								[
									factory.createIdentifier("workflowParameters"),
									factory.createIdentifier("workflowScheduleDate"),
									factory.createIdentifier("undefined"),
									factory.createIdentifier("undefined")
								]
							)
						)
					)
				]
			)
		);
	}
}

/**
 * This is used to print the source file for an async workflow item.
 *
 * The Async workflow is essentially just a normal task with special representation.
 *
 * @Example of what is printed:
 * ```js
var workflowToLaunch = Server.getWorkflowWithId("9e4503db-cbaa-435a-9fad-144409c08df0");
if (workflowToLaunch == null) {
	throw "Workflow not found";
}

var workflowParameters = new Properties();
workflowParameters.put("first",first);
workflowParameters.put("second",second);
wfToken = workflowToLaunch.execute(workflowParameters);
 * ```
 */
export class AsyncWorkflowItemSourceFilePrinter implements SourceFilePrinter {
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					// Variable declarations are on top
					factory.createVariableStatement(
						undefined,
						// A list of declarations
						factory.createVariableDeclarationList(
							[
								// `var workflowParameters = new Properties();`
								factory.createVariableDeclaration(
									"workflowParameters",
									undefined,
									undefined,
									factory.createNewExpression(
										factory.createIdentifier("Properties"),
										undefined,
										[]
									)
								),
								// `, workflowToLaunch = Server.getWorkflowWithId("some id here");`
								factory.createVariableDeclaration(
									"workflowToLaunch",
									undefined,
									undefined,
									factory.createCallExpression(
										factory.createPropertyAccessExpression(
											factory.createIdentifier("Server"),
											factory.createIdentifier("getWorkflowWithId")
										),
										undefined,
										[factory.createStringLiteral(itemInfo.canvasItemPolymorphicBag.linkedItem)]
									)
								)
							],
							undefined
						)
					),

					// `if (workflowToLaunch == null) { throw "Workflow not found"; }`
					factory.createIfStatement(
						factory.createBinaryExpression(
							factory.createIdentifier("workflowToLaunch"),
							factory.createToken(SyntaxKind.EqualsEqualsToken),
							factory.createNull()
						),
						factory.createBlock(
							[
								factory.createThrowStatement(
									factory.createStringLiteral("Workflow not found")
								)
							],
							true
						)
					),

					// `workflowParameters.put("first",first);`
					// `workflowParameters.put("second",second);`
					// ...... etc
					...itemInfo.input.map((input) => {
						return factory.createExpressionStatement(
							factory.createCallExpression(
								factory.createPropertyAccessExpression(
									factory.createIdentifier("workflowParameters"),
									factory.createIdentifier("put")
								),
								undefined,
								[
									factory.createStringLiteral(input),
									factory.createIdentifier(input)
								]
							)
						);
					}),

					// `wfToken = workflowToLaunch.execute(workflowParameters);`
					factory.createExpressionStatement(
						factory.createAssignment(
							factory.createIdentifier("wfToken"),
							factory.createCallExpression(
								factory.createPropertyAccessExpression(
									factory.createIdentifier("workflowToLaunch"),
									factory.createIdentifier("execute")
								),
								undefined,
								[
									factory.createIdentifier("workflowParameters")
								]
							)
						)
					)
				]
			)
		);
	}
}

/**
 * This is used to print the source file for an action item.
 *
 * @Example of what is printed:
 * ```js
	actionResult = System.getModule("com.vmware.stef").PrintStef(a,b);
 * ```
 */
export class ActionItemSourceFilePrinter implements SourceFilePrinter {
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					// `actionResult = System.getModule("com.vmware.stef").PrintStef(a,b);`
					factory.createExpressionStatement(
						factory.createAssignment(
							factory.createIdentifier(itemInfo.output[0]),
							factory.createCallExpression(
								factory.createPropertyAccessExpression(
									factory.createCallExpression(
										factory.createPropertyAccessExpression(
											factory.createIdentifier("System"),
											factory.createIdentifier("getModule")
										),
										undefined,
										[factory.createStringLiteral(itemInfo.canvasItemPolymorphicBag.scriptModule.split("/")[0])]
									),
									factory.createIdentifier(itemInfo.canvasItemPolymorphicBag.scriptModule.split("/")[1])
								),
								undefined,
								itemInfo.input.map((input) => factory.createIdentifier(input))
							)
						)
					)
				]
			)
		);
	}
}
