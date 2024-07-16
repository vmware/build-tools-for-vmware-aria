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
import { ProgramOptions, WriteFileCallback, EmitResult, TransformerFactory, FileDescriptor, FileTransformationContext, FileType } from "../types";
import { DiagnosticCollection } from "./diagnostics";
import { createCompilerOptions } from "./config";

import * as ts from "typescript";
import { system } from "../system/system";
import { noop } from "../utilities/ops";
import { getActionTransformer } from "./transformer/fileTransformers/action";
import { getConfigTypeScriptTransformer, getConfigYamlTransformer } from "./transformer/fileTransformers/config";
import { getDeclarationTransformer, generateIndexTypes } from "./transformer/metaTransformers/declaration";
import { getNativeContentTransformer } from "./transformer/fileTransformers/nativeContent";
import { getPolicyTemplateTransformer } from "./transformer/fileTransformers/policyTemplate";
import { getResourceTransformer } from "./transformer/fileTransformers/resource";
import { getTestTransformer } from "./transformer/fileTransformers/test";
import { getSagaTransformer } from "./transformer/saga/saga";
import { getWorkflowTransformer } from "./transformer/fileTransformers/workflow/workflow";

export interface Program {
	getFiles(): readonly FileDescriptor[];
	emit(writeFileCallback: WriteFileCallback): EmitResult;
}

export function createProgram(options: ProgramOptions): Program {
	const { rootDir, outputs } = options;
	const files = getFiles();
	const fileByPath = files.reduce((obj, f) => {
		obj[f.filePath] = f;
		return obj;
	}, {});

	return {
		getFiles: () => files,
		emit,
	};

	function emit(writeFile: WriteFileCallback): EmitResult {
		const diagnostics = new DiagnosticCollection();
		const emittedFiles: string[] = [];
		const transformerFactoryMap: Record<number, TransformerFactory> = {
			[FileType.Action]: getActionTransformer,
			[FileType.ConfigurationTS]: getConfigTypeScriptTransformer,
			[FileType.ConfigurationYAML]: getConfigYamlTransformer,
			[FileType.TypeDef]: getDeclarationTransformer,
			[FileType.NativeContent]: getNativeContentTransformer,
			[FileType.PolicyTemplate]: getPolicyTemplateTransformer,
			[FileType.Resource]: getResourceTransformer,
			[FileType.JasmineTest]: getTestTransformer,
			[FileType.Workflow]: getWorkflowTransformer,
			[FileType.Saga]: getSagaTransformer,
		};
		const compilerOptions = createCompilerOptions(rootDir, options.project);
		let scriptProgram: ts.Program;
		const context: FileTransformationContext = {
			rootDir: options.rootDir,
			emitHeader: options.emitHeader,
			actionsNamespace: options.actionsNamespace,
			workflowsNamespace: options.workflowsNamespace,
			outputs: outputs,
			diagnostics: diagnostics,
			sourceFiles: [],
			configIdsMap: {},
			getFile: path => fileByPath[path],
			readFile: fileName => system.readFile(fileName).toString(),
			writeFile: (fileName: string, data: string | Buffer) => {
				if (data) {
					writeFile(fileName, data);
					emittedFiles.push(fileName);
				}
			},
			getScriptProgram: () =>
				scriptProgram
				|| (
					scriptProgram = context.sourceFiles.length
						? createScriptProgram(context, compilerOptions)
						: undefined
				),
		};

		files.map(file => transformerFactoryMap[file.type](file, context)).forEach(transform => transform());

		generateIndexTypes(context);

		if (context.sourceFiles.length) {
			ts.getPreEmitDiagnostics(context.getScriptProgram()).forEach(d => diagnostics.addNative(d));
		}

		return {
			diagnostics: diagnostics.toArray(),
			emittedFiles,
		};
	}

	function createScriptProgram(context: FileTransformationContext, compilerOptions: ts.CompilerOptions): ts.Program {
		const sourceFileByPath: Record<string, ts.SourceFile> = context.sourceFiles.reduce((obj, sourceFile) => {
			obj[sourceFile.fileName] = sourceFile;
			return obj;
		}, {});

		const sourceFileCache: Record<string, ts.SourceFile> = {};
		const defaultLibLocation = system.normalizePath(system.joinPath(system.getExecutingDirPath(), "..", "lib"));
		const defaultLibFileName = system.joinPath(defaultLibLocation, "lib.d.ts");

		const compilerHost: ts.CompilerHost = {
			getSourceFile: (fileName: string, languageVersion: ts.ScriptTarget, onError?: (message: string) => void, shouldCreateNewSourceFile?: boolean) => {
				let sourceFile = sourceFileByPath[fileName];
				if (!sourceFile) {
					if (shouldCreateNewSourceFile || !sourceFileCache[fileName]) {
						sourceFileCache[fileName] = ts.createSourceFile(
							fileName,
							system.readFile(fileName).toString(),
							languageVersion ?? ts.ScriptTarget.Latest,
							true);
					}
					sourceFile = sourceFileCache[fileName];
				}
				return sourceFile;
			},
			writeFile: noop,
			getDefaultLibFileName: () => defaultLibFileName,
			getDefaultLibLocation: () => defaultLibLocation,
			useCaseSensitiveFileNames: () => false,
			getCanonicalFileName: fileName => fileName,
			getCurrentDirectory: () => system.getCurrentDirectory(),
			getNewLine: () => "\n",
			fileExists: fileName => system.fileExists(fileName),
			readFile: fileName => system.readFile(fileName).toString(),
			directoryExists: directoryName => system.directoryExists(directoryName),
			getDirectories: path => system.getDirectories(path),
		};

		const program = ts.createProgram({
			rootNames: context.sourceFiles.map(f => f.fileName),
			options: compilerOptions,
			host: compilerHost,
		});

		return program;
	}

	function getFiles(): FileDescriptor[] {
		let filePaths = system.getFiles(rootDir, true);
		const fileSet: Record<string, boolean> = {};
		const files: FileDescriptor[] = [];

		filePaths.forEach(filePath => fileSet[filePath.toLowerCase()] = true);

		if (options?.files?.length) {
			const filesFiltered = options.files.filter((item, i, ar) => ar.indexOf(item) === i);
			const selectedFiles = [];
			filesFiltered.forEach(fileName => {
				filePaths.forEach(filePath => {
					if (filePath.includes(fileName, 0)) {
						selectedFiles.push(filePath);
					}
				});
			});
			filePaths = selectedFiles;
			console.log(`Files in system: ${JSON.stringify(filePaths, null, 2)}`);
			console.log(`Files in the option obj: ${JSON.stringify(options.files)}`);
			console.log(`Selected files: ${JSON.stringify(fileSet, null, 2)}`);
		}
		filePaths.forEach(filePath => {
			let fileType = getFileType(filePath, fileSet);
			if (fileType !== undefined) {
				let fileName = system.basename(filePath);
				let relativeFilePath = system.relativePath(rootDir, filePath);
				let relativeDirPath = system.dirname(relativeFilePath);
				files.push({
					filePath: filePath,
					fileName: fileName,
					relativeFilePath: relativeFilePath,
					relativeDirPath: relativeDirPath === "." ? "" : relativeDirPath,
					type: fileType,
				});
			}
		});

		return files;
	}

	function getFileType(filePath: string, fileSet: Record<string, boolean>): FileType {
		filePath = filePath.toLowerCase();

		if (filePath.endsWith(".wf.ts")) {
			return FileType.Workflow;
		}
		if (filePath.endsWith(".conf.ts")) {
			return FileType.ConfigurationTS;
		}
		if (filePath.endsWith(".pl.ts")) {
			return FileType.PolicyTemplate;
		}
		if (filePath.endsWith(".d.ts")) {
			return FileType.TypeDef;
		}
		if (filePath.endsWith(".test.ts") || filePath.endsWith(".test.js")) {
			return FileType.JasmineTest;
		}
		if (filePath.endsWith(".ts") || filePath.endsWith(".js")) {
			return FileType.Action;
		}
		if (filePath.endsWith(".conf.yaml")) {
			return FileType.ConfigurationYAML;
		}
		if (filePath.endsWith(".saga.yaml")) {
			return FileType.Saga;
		}
		if (filePath.endsWith(".xml") && fileSet[`${system.changeFileExt(filePath, "")}.element_info.xml`]) {
			return FileType.NativeContent;
		}
		if (!filePath.endsWith(".element_info.xml") &&
			!filePath.endsWith(".element_info.yaml") &&
			!filePath.endsWith(".element_info.json") &&
			!filePath.endsWith(".wf.xml") &&
			!filePath.endsWith(".pl.xml") &&
			!filePath.endsWith(".saga.xml") &&
			!filePath.endsWith(".wf.form.json")) {
			return FileType.Resource;
		}
	}
}
