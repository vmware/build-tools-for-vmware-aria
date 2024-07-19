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
	printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo?: WorkflowItemDescriptor): string;
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


//   <workflow-item name="item1" out-name="item0" type="task" launched-workflow-id="9e4503db-cbaa-435a-9fad-144409c08df0">
// 	<display-name><![CDATA[Other Workflow]]></display-name>
// 	<script encoded="false"><![CDATA[//Auto generated script, cannot be modified !
// var workflowToLaunch = Server.getWorkflowWithId("9e4503db-cbaa-435a-9fad-144409c08df0");
// if (workflowToLaunch == null) {
// 	throw "Workflow not found";
// }
//
// var workflowParameters = new Properties();
// workflowParameters.put("first",first);
// workflowParameters.put("second",second);
// scheduledTask = workflowToLaunch.schedule(workflowParameters, workflowScheduleDate);
// ]]></script>
// 	<in-binding>
// 	  <bind name="workflowScheduleDate" type="Date" export-name="workflowScheduleDate"/>
// 	  <bind name="first" type="number" export-name="first"/>
// 	  <bind name="second" type="number" export-name="second"/>
// 	</in-binding>
// 	<out-binding>
// 	  <bind name="scheduledTask" type="Task"/>
// 	</out-binding>
// 	<description><![CDATA[Schedule a workflow and create a task.]]></description>
// 	<position y="60.0" x="260.0"/>
//   </workflow-item>
export class ScheduledWorkflowItemSourceFilePrinter implements SourceFilePrinter {
	public printSourceFile(methodNode: MethodDeclaration, sourceFile: SourceFile, itemInfo: WorkflowItemDescriptor): string {
		const segments = [];

		segments.push(
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
		);

		segments.push(
			factory.createIfStatement(
				factory.createBinaryExpression(
					factory.createIdentifier("workflowToLaunch"),
					factory.createToken(SyntaxKind.ExclamationEqualsToken),
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
			)
		);

		return printSourceFile(
			factory.updateSourceFile(
				sourceFile,
				[
					...sourceFile.statements.filter(n => n.kind !== SyntaxKind.ClassDeclaration),
					...createWorkflowItemPrologueStatements(methodNode),
					...segments,
				]
			)
		);
	}
}
