/*-
 * #%L
 * vropkg
 * %%
 * Copyright (C) 2023 - 2025 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import { existsSync, writeFileSync, readFileSync } from "fs";
import { EOL } from "os";
import { resolve } from "path";

/** Cached .vroignore files per (resolved) path */
const CACHE: Record<string, Record<VroIgnoreCategory, string[]>> = {};
/** Default test helper file path patterns */
export const DEFAULT_TEST_HELPER_FILE_PATTERNS = ["**/*_helper.js", "**/*.helper.[tj]s"];

/**
 * Default .vroignore file contents by category.
 * Defines the categories (lines starting with a single '#')
 * with their descriptions (starting with '##') and corresponding default glob patterns for files to be ignored (uncomented lines)
 */
export const VroIgnoreCategories = {
	General: [
		"## This file contains glob patterns for file paths to be ignored during compilation, packaging and testing.",
		"## The file will be (re)generated when the project is rebuilt to maintain the default categories and patterns, ignoring blank lines and repeating comments.",
		"## The default categories, defined as rows starting with a single '#', are 'General', 'Packaging', 'Compilation', 'Testing', 'TestHelpers'.",
		"## Patterns not under one of those categories will be considered in the 'General' category and ignored during all operations.",
		"## Patterns must be listed on separate rows (without in-line comments), can be negated with a single '!' at the start and will be trimmed before processing.",
		"## The 'TestHelpers'category will contain these two patterns by default: '**/*_helper.js', '**/*.helper.[tj]s'",
		"## Rows starting with '##' are considered as comments and will be ignored on processing."
	],
	Packaging : [
		"## Files with these paths will not be included in the vro package"
	],
	Compilation: ["## Files with these paths will be compiled without TS definitions"],
	Testing: ["## Files with these paths will be excluded from test coverage"],
	TestHelpers: [
		"## Files with these paths will be included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package",
		...DEFAULT_TEST_HELPER_FILE_PATTERNS
	]
}

/** Categories in .vroignore file */
export type VroIgnoreCategory = keyof typeof VroIgnoreCategories;

/**
 * Reads the .vroignore-type file from the given path. If no such file exists, creates one with default content.
 * Does not throw on I/O operations.
 * @param {string} path - RESOLVED file path where the file will be created (main project folder/.vroignore)
 * @returns {boolean} Record with keys - the categories from the file and values - patterns of ignored files per category.
 */
export function getOrCreateDefault(path: string): Record<VroIgnoreCategory, string[]> {
	path = resolve(path);
	if (CACHE[path]) {
		console.info(`Found cached ignore patterns for ${path}: ${JSON.stringify(CACHE[path])}`)
		return CACHE[path];
	}
	let rowsTrimmed: string[] = [];
	if (existsSync(path)){
		try {
			rowsTrimmed = readFileSync(path, 'utf-8').trim().split(/\r?\n/gm).map(p => p.trim()).filter(p => !!p);
		} catch (err) {
			console.error(`Failed to read existing file ${path}: ${err}.`);
		}
	}
	const defaultContent = toContent(VroIgnoreCategories);
	let updatedContent: string;
	let rowsByCategory: Record<VroIgnoreCategory, string[]> =  {...VroIgnoreCategories}; // start from default content then update from file to maintain structure
	Object.keys(VroIgnoreCategories).forEach(key => rowsByCategory[key] = [...VroIgnoreCategories[key]]);
	if (!rowsTrimmed.length) {
		updatedContent = defaultContent;
	} else if ((rowsTrimmed.join(EOL)) !== defaultContent) {
		let currentCategory: VroIgnoreCategory = 'General';
		rowsTrimmed.forEach(row => {
			const isCategoryRow = !isComment(row) && /^#/.test(row);
			if (isCategoryRow) {
				row = row.replace("#", "").trim();
				if (row in VroIgnoreCategories) {
					currentCategory = row as VroIgnoreCategory;
				} else {
					console.warn(`${path}: Unrecognized category: '${row}'. Adding ignore patterns to 'General' category.`);
					currentCategory = 'General';
				}
			} else if (rowsByCategory[currentCategory].indexOf(row) < 0) {
				rowsByCategory[currentCategory].push(row);
			}
		});
		updatedContent = toContent(rowsByCategory);
	}
	if (!updatedContent) {
		console.info(`Found existing .vroignore file with default content: ${path}`);
	} else {
		try {
			writeFileSync(path, updatedContent, {encoding:'utf8', flag: "w"});
			console.info(`Saved vro ignore file at ${path} with default content.`);
		} catch (err) {
			console.error(`Failed to save vro ignore file at ${path}: ${err}`);
		}
	}

	Object.keys(rowsByCategory).forEach(key => rowsByCategory[key] = rowsByCategory[key].filter(p => !!p && !isComment(p)));
	console.info(`Caching vRO ignore patterns for ${path}: ${JSON.stringify(rowsByCategory)}`);
	CACHE[path] = rowsByCategory;

	return rowsByCategory;
}

/**
 * Reads .vroignore file contents by categories
 * @param {string} path - file path of the .vroignore file. If no file exists,
 * checks the default .vroignore file contents
 * @param {VroIgnoreCategory[]} categories - categories to read from the file. If not provided, reads all.
 * @returns {string[]} array of glob patterns for the respective categories. Always includes entries for the 'General' category
 */
export function readVroIgnorePatternsFromFile(path: string, ...categories: VroIgnoreCategory[]): string[] {
	const includeAll = !categories?.length;
	let result = getOrCreateDefault(path);
	let resArr = Object.keys(VroIgnoreCategories)
		.filter((k: VroIgnoreCategory) => includeAll || k == 'General' || categories.find(cat => cat == k))
		.reduce((res, k) => [...res, ...(result[k])], []);
	
	return Array.from(new Set(resArr));
}

// HELPER FUNCTIONS

/**
 * Prepares .vroignore file contents
 * @param {Record<VroIgnoreCategory, string[]>} rec - record with rows (ignore patterns, comments) per category
 * @returns {string}  string content to be written to file on the OS
 */
function toContent(rec: Record<VroIgnoreCategory, string[]>): string {
	return Object.keys(rec)
		.reduce((res, key) => [...res, `# ${key}`, ...rec[key]], [])
		.join(EOL);
}

/**
 * Checks if the given row matches a comment in .vroignore file
 * @param {string} trimmedRow - row
 * @returns {boolean} true if the row starts with '##'
 */
function isComment(trimmedRow: string): boolean {
	return /^##/.test(trimmedRow);
}
