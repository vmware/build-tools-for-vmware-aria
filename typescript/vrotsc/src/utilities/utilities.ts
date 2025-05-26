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
import { system } from "../system/system";
import { FileType } from "../types";
import * as fs from "fs-extra";
import * as os from "os";
import { minimatch } from "minimatch";

/**
* Generates a unique id for a given file path.
* The id is generated using the uuidv3 library.
*
* uuidv3 requires a name and a namespace.
* We use the name to generate a unique id for the given namespace.
* Example:
* ````
* generateElementId(FileType.Workflow, "path/to/workflow.yaml")
* returns: 9aced742-20c5-3b79-a337-94b13ad83d72
* ````
* Every time the same path is passed, the same id will be returned.
* the defined namespaces are hardcoded and are used to generate the id.
*
* @throws {Error} if the file type is not implemented.
*/
export function generateElementId(fileType: FileType, path: string): string {
	return system.uuid(path.replace(/\\/g, "/"), getIdHashForFile());

	function getIdHashForFile(): string {
		switch (fileType) {
			case FileType.Workflow:
				return "0d79ca9f-3e6c-4194-b73c-35eb5ba9cb80";
			case FileType.PolicyTemplate:
				return "42bf5b9b-20f3-428c-bdf4-d800a7cdc265";
			case FileType.ConfigurationTS:
			case FileType.ConfigurationYAML:
				return "b93b589d-53ac-48e5-b9ca-59d83447d64c";
			case FileType.Resource:
				return "89e14cd8-f955-4634-aadf-34306c862737";
			default:
				throw new Error(`Unimplemented ID Generation type: ${fileType}`);
		}
	}
}

/** Category for test helper file patterns in the .vroignore file */
export const TEST_HELPER_VROIGNORE_CATEGORY = "Test Helper Files";

/** Default content for the .vroignore file. */
export const DEFAULT_VRO_IGNORE_FILE_CONTENT = [
	"# " + TEST_HELPER_VROIGNORE_CATEGORY,
	"## Files with these paths  will be included in the tests but will not be part of the coverage or the vro package",
	"**/*_helper.js",
	"**/*.helper.[tj]s",
	"",
	"# Others",
	"## Other files to ignore from package/coverage"
]

/**
 * Creates a .vroignore-type file with default content at the given filepath
 * @param {string} path - file path where the file will be created (main project folder/.vroignore)
 * @returns {boolean} true if successfully created, false otherwise
 */
export function createDefaultVroIgnoreFile(path: string) {
	try {
		fs.writeFileSync(path, DEFAULT_VRO_IGNORE_FILE_CONTENT.join(os.EOL), 'utf-8');
		return true;
	} catch (err) {
		console.error(`Failed to create file ${path}`);
		return false;
	}
}

/**
 * Reads .vroignore file contents by categories
 * @param {string} path - file path of the .vroignore file. If no file exists,
 * checks the default .vroignore file contents
 * @param {string[]} categories - categories to read from the file. If not provided, reads all
 * @returns {string[]} array of glob patterns for the respective categories.
 */
export function readVrIgnorePatternsFromFile(path: string, ...categories: string[]) {
	let rows: string[]
	try {
		rows = fs.readFileSync(path, 'utf-8')
			.split(/\r?\n/gm)
			.map(p => p.trim())
			.filter(p => !!p);
	} catch {
		console.error(`Failed to read vRO ignore file ${path}!`);
	}
	if (!rows?.length) {
		console.warn(`Returning default vRO ignore patterns.`);
		rows = DEFAULT_VRO_IGNORE_FILE_CONTENT;
	} else {
		console.info(`Successfully read vRO ignore file '${path}'`);
	}

	rows = rows.filter(s => !/^\s*##/.test(s) && !/^[\s#]+$/.test(s)); // descriptions, empty categories
	const result: Record<string, string[]> = {};
	let currentCategory: string = null;
	rows.forEach(row => {
		const isCategoryRow = /^\s*#/.test(row);
		currentCategory = ((isCategoryRow ? row.replace("#", "").trim() : currentCategory)
			|| TEST_HELPER_VROIGNORE_CATEGORY).toLowerCase();
		if(!result[currentCategory]) {
			result[currentCategory] = [];
		}
		if (!isCategoryRow && result[currentCategory].indexOf(row) < 0) {
			result[currentCategory].push(row);
		}
	});
	console.info(`All vRO ignore patterns: ${JSON.stringify(result)}`);
	const resArr = Object.keys(result)
		.filter(k => !categories?.length || categories.find(cat => cat.toLowerCase() == currentCategory))
		.reduce((res, k) => [...res, ...(result[k])], []);
	console.info(`vRO ignore patterns: ${JSON.stringify(resArr)}`)
	return resArr;
}

export function filePathMatchesGlob(filePath: string, globPatterns: string[]): boolean {
	const negatedPatterns = globPatterns.filter(p => p.startsWith("!"));
	globPatterns = globPatterns.filter(p => p.startsWith("!"));
	return globPatterns.find(p => minimatch(filePath, p)) && !negatedPatterns.find(p => minimatch(filePath, p));
}
