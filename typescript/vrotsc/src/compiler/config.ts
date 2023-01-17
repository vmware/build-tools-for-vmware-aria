/*
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");

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
                    "lib.es2017.string.d.ts"
                ],
                strict: false,
                allowUnreachableCode: true,
                noImplicitUseStrict: true,
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
            };
        }
    }

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
        }
    }
}
