/*-
 * #%L
 * vropkg
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
import * as fs from "fs-extra";
import * as os from "os";

export const exist = (file: fs.PathLike) => fs.existsSync(file);
export function isDirectory(path:string) : boolean {
    try {
        let stat = fs.lstatSync(path);
        return stat.isDirectory();
    } catch (e) {
        // lstatSync throws an error if path doesn't exist
        return false;
    }
}
export const TEST_HELPER_VROIGNORE_CATEGORY = "Test Helper Files";
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
 * 
 * @param path 
 * @returns 
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
