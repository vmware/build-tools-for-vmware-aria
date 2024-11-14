/*-
 * #%L
 * polyglotpkg
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
import { copyFileSync, mkdirSync, readdirSync, realpathSync, statSync } from "fs";
import { join } from "path";

interface FindFilesOptions {
    exclude?: RegExp[] | string[];
    maxDepth?: number;
    currentDepth?: number;
    path?: string;
    subPath?: string;
    absolute?: boolean;
}
export function findFiles(patterns: RegExp[] | string[], options: FindFilesOptions = {}): string[] {
    let result: string[] = [];

    const maxDepth = options.maxDepth || 10;
    const currentDepth = options.currentDepth || 0;

    if (currentDepth > maxDepth) {
        return result;
    }

    const subPath = options.subPath ? join("/", options.subPath) : "";
    const defaultPath = ".";
    const path = options.path || defaultPath;
    const exclude = options.exclude || [];
    const workPath = join(path, subPath);
    const files = [];
    const directories = [];

    for (const _path of readdirSync(workPath)) {
        const fullPath = workPath === defaultPath ? _path : join(workPath, _path);
        statSync(fullPath).isFile()
            ? files.push(fullPath)
            : directories.push(_path);
    }

    const someMatch = (fileName: string, pattern: string|RegExp) => {
        let regex: RegExp;
        if (pattern instanceof RegExp) {
            regex = pattern;
        } else {
            const single = "{single}";
            const singleRegex = "[a-z0-9\-\_\.]*";
            const double = "{double}";
            const doubleRegex = "[a-z0-9\-\_\.\/]*";
            if (pattern.indexOf("**") !== -1) {
                pattern = pattern.split("**").join(double);
            }
            if (pattern.indexOf("*") !== -1) {
                pattern = pattern.split("*").join(single);
            }
            pattern = pattern.split(single).join(singleRegex);
            pattern = pattern.split(double).join(doubleRegex);
            regex = new RegExp("^" + pattern + "$", "gi");
        }
        return regex.test(fileName);
    };
    const checkMatch = (fileName: string) =>
        patterns.some(pattern => someMatch(fileName, pattern)
        ) && (
            exclude.length === 0 ||
            !exclude.some(pattern => someMatch(fileName, pattern))
        );

    for (const file of files) {
        const shortFileName = file.substring(path.length + 1);
        if (checkMatch(shortFileName)) {
            result.push(!!options.absolute ? realpathSync(file) : shortFileName);
        }
    }

    for (const dir of directories) {
        result = [
            ...result,
            ...findFiles(patterns, {
                ...options,
                currentDepth: currentDepth + 1,
                subPath: join(subPath, dir)
            })
        ];
    }

    return result;
}
