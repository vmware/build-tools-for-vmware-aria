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
import { FileDescriptor, FileTransformationContext, ScriptTransformationContext } from "../../../types";
import { system } from "../../../system/system";
import { transformSourceFile } from "../scripts/scripts";
import { collectFactsBefore } from "../metaTransformers/facts";
import { collectNamespaces, transformNamespaces } from "../codeTransformers/namespaces";
import { transformShimsBefore, transformShims } from "../codeTransformers/shims";
import { remediateTypeScript } from "../codeTransformers/remediate";
import { transformModuleSystem } from "../codeTransformers/modules";
import { canCreateDeclarationForFile } from "../metaTransformers/declaration";
import { checkActionForMisplacedClassDecorators, handleActionClosure } from "../helpers/actionTransformHelper";

// @TODO: Take a look at this

export function getActionTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration)
		.forEach(classNode => checkActionForMisplacedClassDecorators(classNode as ts.ClassDeclaration));
	context.sourceFiles.push(sourceFile);

	return function transform() {
		const transformers = {
			before: [
				collectFactsBefore,
				collectNamespaces,
				transformShimsBefore,
			],
			after: [
				transformShims,
				transformNamespaces,
				remediateTypeScript,
				transformModuleSystem,
				(sourceFile: ts.SourceFile, ctx: ScriptTransformationContext) => handleActionClosure(sourceFile, ctx, context.emitHeader),
			],
		};
		const [sourceText, typeDefText, mapText] = transformSourceFile(sourceFile, context, transformers);

		// Test helpers should be excluded
		const isHelper = file.relativeFilePath.match(/\.helper\.[tj]s$/);
		const outputDir = isHelper ? context.outputs.testHelpers : context.outputs.actions;

		const resolvedPath = system.resolvePath(outputDir, file.relativeFilePath);
		let targetFilePath = system.changeFileExt(resolvedPath, ".js", [".js", ".ts"]);

		if (isHelper) {
			targetFilePath = system.changeFileExt(targetFilePath, "_helper.js", [".helper.js"]);
		}

		context.writeFile(targetFilePath, sourceText);

		if (typeDefText && canCreateDeclarationForFile(file, context.rootDir)) {
			const targetDtsFilePath = system.changeFileExt(system.resolvePath(context.outputs.types, file.relativeFilePath), ".d.ts");
			context.writeFile(targetDtsFilePath, typeDefText);
		}

		if (mapText) {
			const targetMapFilePath = system.changeFileExt(system.resolvePath(context.outputs.maps, file.relativeFilePath), ".js.map");
			context.writeFile(targetMapFilePath, mapText);
		}
	};
}
