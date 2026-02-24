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
import { system } from "../system/system";

/**
 * Function to create compiler options for TypeScript.
 * @param {string} rootDir - The root directory of the project.
 * @param {string} [projectPath] - The path to the tsconfig.json file.
 * @returns {ts.CompilerOptions} - The compiler options for TypeScript.
 */
export function createCompilerOptions(rootDir: string, projectPath?: string): ts.CompilerOptions {
	if (projectPath) {
		const tsconfigText = system.readFile(projectPath).toString();
		const parsed = ts.parseConfigFileTextToJson(projectPath, tsconfigText);
		const parseHost = createParseConfigHost();
		const basePath = system.dirname(system.resolvePath(projectPath));
		const parsedCommandLine = ts.parseJsonConfigFileContent(parsed.config, parseHost, basePath);
		return parsedCommandLine.options;
	}
	else {
		return {
			module: ts.ModuleKind.ESNext,
			moduleResolution: ts.ModuleResolutionKind.NodeJs,
			target: ts.ScriptTarget.ES5,
			lib: [
				"lib.es5.d.ts",
				"lib.es2015.core.d.ts",
				"lib.es2015.collection.d.ts",
				"lib.es2015.iterable.d.ts",
				"lib.es2015.promise.d.ts",
				"lib.es2017.string.d.ts",
				"lib.es2016.array.include.d.ts"
			],
			strict: false,
			allowUnreachableCode: true,
			stripInternal: false,
			removeComments: false,
			experimentalDecorators: true,
			emitDecoratorMetadata: true,
			importHelpers: true,
			suppressOutputPathCheck: true,
			rootDir: rootDir,
			baseUrl: rootDir,
			allowJs: true,
			declaration: true,
			sourceMap: true,
			declarationMap: false,
            // verbatimModuleSyntax: true,
            ignoreDeprecations: "5.0"
		};
	}
}

/**
 * Function to create a ParseConfigHost.
 *
 * A ParseConfigHost provides methods that are used by the TypeScript compiler
 * to interact with the host environment, such as reading files or checking if
 * a file exists. This is particularly useful when dealing with tsconfig.json
 * files, as it allows the compiler to read and understand the configuration
 * settings.
 *
 * For us, we use it to read the tsconfig.json file and parse it into a
 * ts.CompilerOptions object. Required by the TypeScript compiler
 *
 * @returns {ts.ParseConfigHost} - The host for parsing the config.
 */
function createParseConfigHost(): ts.ParseConfigHost {
	return {
		useCaseSensitiveFileNames: false,
		fileExists: (fileName: string): boolean => {
			return system.fileExists(fileName);
		},
		readFile: (path: string): string => {
			return system.readFile(path).toString();
		},
		readDirectory: (path: string, extensions: string[], excludes: string[], includes: string[], depth: number): string[] => {
			return system.getFiles(path, false);
		}
	};
}
