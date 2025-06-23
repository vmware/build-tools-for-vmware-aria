/*-
 * #%L
 * vrotest
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
import { minimatch } from "minimatch";
import { EOL } from "os";
import { resolve } from "path";

/** Default vRO ignore file content. Self explanatory */
export const DEFAULT_CONTENT = `# General
## This file contains glob patterns for file paths to be ignored during compilation, packaging and testing.
## The file will be (re)generated when the project is rebuilt to maintain the default categories and patterns, ignoring blank lines and repeating comments.
## The default categories, defined as rows starting with a single '#', are 'General', 'Packaging', 'Compilation', 'Testing', 'TestHelpers'.
## Patterns not under one of those categories will be considered in the 'General' category and ignored during all operations.
## Patterns must be listed on separate rows (without in-line comments), can be negated with a single '!' at the start and will be trimmed before processing.
## The 'TestHelpers'category will contain these two patterns by default: '**/*_helper.js', '**/*.helper.[tj]s'
## Rows starting with '##' are considered as comments and will be ignored on processing.
# Packaging
## Files with these paths will not be included in the vro package.
## For Workflows, Configurations, Resources and Policies: it is recommended to use the 'General' category (will skip xml element generation).
## Otherwise the patterns below must be based on the _element name_.element_info.xml files in target/vro-sources/xml.
# Compilation
## Files with these paths will be compiled without TS definitions
# Testing
## Files with these paths will be excluded from test coverage
# TestHelpers
## Files with these paths will be included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package
**/*_helper.js
**/*.helper.[tj]s`;

/** Default vRO ignore file content split into rows */
const DEFAULT_CONTENT_ROWS = DEFAULT_CONTENT.split(/\r?\n/gm);

/** Extracted category names as array */
const VroIgnoreCategoriesArray = [ 'General' , 'Packaging' , 'Compilation' , 'Testing' , 'TestHelpers'] as const;

/** Cached .vroignore files per (resolved) path */
const CACHE: Record<string, Record<VroIgnoreCategory, string[]>> = {};

/** Allows to set log level */
const log =  (msg: string) => {}; // avoids flooding console when called in a child process (stdout does not differentiate between console.debug and console.info)
const { warn } = console;

/**
 * Categories in .vroignore file (prefixed by a single #). Files matching the patterns in these categories will be:
 * - General - ignored by all operations
 * - Packaging - excluded from the vRO package
 * - Compilation - excluded from TS definitions and compilation
 * - Testing - excluded from test coverage
 * - TestHelpers -  included in the tests but will not have TS definitions and will not be included in the test coverage or the vro package
 */
export type VroIgnoreCategory = typeof VroIgnoreCategoriesArray[number];

/**
 * Utility class for handling vRO ignore files.
 * See {@link DEFAULT_CONTENT} contents for further details and {@link VroIgnoreCategory} for supported categories.
 */
export default class VroIgnore {

	/**
	 * Checks if a file path matches any of the provided patterns
	 * @param filePath - file path (resolved)
	 * @param globPatterns - array of patterns to check. Any patterns starting with "!" will be excluded
	 *  (does not support "^" or multiple starting "!")
	 * @returns true if the path matches any of the glob patterns and there is no negative pattern that matches it
	 *          false otherwise
	 */
	public static filePathMatchesGlob(filePath: string, globPatterns: string[]): boolean {
		filePath = resolve(filePath).replace(/[\\]+/gm,"/");;
		const matchingNegativePattern = globPatterns.filter(p => p.startsWith("!")).find(p => minimatch(filePath, p.substring(1)));
        if (matchingNegativePattern) {
            log(`'${filePath}' matches  negative pattern ${matchingNegativePattern} from ${JSON.stringify(globPatterns)}`);
            return false;
        }
        const matchingPositivePattern = globPatterns.filter(p => !p.startsWith("!")).find(p => minimatch(filePath, p));
		if (matchingPositivePattern) {
            log(`'${filePath}' matches pattern ${matchingPositivePattern} from ${JSON.stringify(globPatterns)}`);
		}
		return !!matchingPositivePattern;
	}

	/** Resolved path for the vRO ignore file; if a file doesn't exist there, it will be created with default content */
	public readonly resolvedPath: string;

	/** @param vroIgnoreFilePath - path to the vRO ignore file. Will be resolved and used as cache key to store the (lazily) fetched patterns. */
	constructor(vroIgnoreFilePath: string) {
		this.resolvedPath = resolve(vroIgnoreFilePath);
	}

	/**
	 * Glob ignore patterns from the given categories in the vRO ignore file
	 * @param {VroIgnoreCategory[]} categories - listed categories to combine the patterns from. If empty, assumes all
	 * @returns {string[]} Array combining the pattern from the selected categories (without repetition)
	 */
	public getPatterns(...categories: VroIgnoreCategory[]): string[] {
		const includeAll = !categories?.length;
		let result = this.getOrCreateDefault();
		let resArr = VroIgnoreCategoriesArray
			.filter((k: VroIgnoreCategory) => includeAll || categories.find(cat => cat == k))
			.reduce((res, k) => [...res, ...(result[k])], []);

		return Array.from(new Set(resArr));
	}
	/**
	 * Checks if the file path should be ignored for the given categories
	 * @param {string} filePath - file path to check (resolved)
	 * @param {VroIgnoreCategory[]} categories - listed categories to combine the patterns from. If empty, assumes all
	 * @returns {boolean} true if (for the given categories) the path matches any of the patterns and doesn't match any of the negated patterns
	 */
	public shouldIgnore(filePath: string, ...categories: VroIgnoreCategory[]): boolean{
		return VroIgnore.filePathMatchesGlob(filePath, this.getPatterns(...categories));
	}
	// HELPER METHODS
	/**
	 * Reads the .vroignore-type file from the given path. If no such file exists, creates one with default content.
	 * Does not throw on I/O operations. Caches the content for further use.
	 * @returns {boolean} Record with keys - the categories from the file and values - patterns of ignored files per category.
	 */
	private getOrCreateDefault(): Record<VroIgnoreCategory, string[]> {
		if (CACHE[this.resolvedPath]) {
			log(`Found cached ignore patterns for ${this.resolvedPath}: ${JSON.stringify(CACHE[this.resolvedPath])}`)
			return CACHE[this.resolvedPath];
		}
		const rowsTrimmed: string[] = this.readFileRowsTrimmed(this.resolvedPath);
		const existingContentTrimmed = rowsTrimmed.join(EOL);
		const defaultContent = DEFAULT_CONTENT_ROWS.join(EOL);
		let rowsByCategory =  this.populateRowsByCategory(rowsTrimmed);
		const contentToSave: string = !existingContentTrimmed ? defaultContent : this.toFileContent(rowsByCategory);

		if (contentToSave !== existingContentTrimmed) {
			const contentDesc = contentToSave === defaultContent ? "default" : "updated custom";
			try {
				writeFileSync(this.resolvedPath, contentToSave, {encoding:'utf8', flag: "w"});
				log(`Successfully saved file ${this.resolvedPath} with ${contentDesc} content: ${contentToSave}`);
			} catch (err) {
				warn(`Failed to save file ${this.resolvedPath} with ${contentDesc} content: ${err}`);
			}
		}

		Object.keys(rowsByCategory).forEach(key => rowsByCategory[key] = rowsByCategory[key].filter(p => !!p && !this.isComment(p)));
		log(`Caching vRO ignore patterns for ${this.resolvedPath}: ${JSON.stringify(rowsByCategory)}`);
		CACHE[this.resolvedPath] = rowsByCategory;

		return rowsByCategory;
	}

	/**
	 * Reads the rows from the file at the given path (if it exists) into an array. Trims each array element, removing empty ones.
	 * @param {string} path - path to read from
	 * @returns {string[]} Array containing the trimmed non-empty rows (empty if the file can't be read)
	 */
	private readFileRowsTrimmed(path: string): string[] {
		if (existsSync(path)) {
			try {
				return readFileSync(path, 'utf-8').trim().split(/\r?\n/gm).map(p => p.trim()).filter(p => !!p);
			} catch (err) {
				warn(`Failed to read file ${path}: ${err}.`);
			}
		}
		return [];
	}

	/**
	 * Starting from the default vRO ignore file content as base (see {@link DEFAULT_CONTENT}) and maintaining its structure, populates any additional patterns/comments by category.
	 * @param {string[]} rowsTrimmed - rows from an existing vRO ignore file (trimmed). Pattern and (##) comment rows are compared to rows under the same (or General) category (#)
	 * in DEFAULT_CONTENT and, if not present, are appended to the category rows.
	 * @returns Record combining the default content with any custom patterns/rows by category.
	 */
	private populateRowsByCategory(rowsTrimmed: string[]): Record<VroIgnoreCategory, string[]> {
		const rowsByCategory =  VroIgnoreCategoriesArray.reduce((res, el) => {res[el] = []; return res;}, {}) as Record<VroIgnoreCategory, string[]>;
		let currentCategory: VroIgnoreCategory = 'General';
		[...DEFAULT_CONTENT_ROWS, ...rowsTrimmed].forEach(row => {
			const isCategoryRow = !this.isComment(row) && /^#/.test(row);
			if (isCategoryRow) {
				row = row.replace("#", "").trim();
				if (VroIgnoreCategoriesArray.find(category => category === row)) {
					currentCategory = row as VroIgnoreCategory;
				} else {
					warn(`Unrecognized category: '${row}'. Adding ignore patterns to 'General' category.`);
					currentCategory = 'General';
				}
			} else if (rowsByCategory[currentCategory].indexOf(row) < 0) {
				rowsByCategory[currentCategory].push(row);
			}
		});
		return rowsByCategory;
	}

	/**
	 * Prepares .vroignore file contents
	 * @param {Record<VroIgnoreCategory, string[]>} rec - record with rows (ignore patterns, comments) per category
	 * @returns {string}  string content to be written to file on the OS
	 */
	private toFileContent(rec: Record<VroIgnoreCategory, string[]>): string {
		return Object.keys(rec)
			.reduce((res, key) => [...res, `# ${key}`, ...rec[key]], [])
			.join(EOL);
	}

	/**
	 * Checks if the given row matches a comment in .vroignore file
	 * @param {string} trimmedRow - row
	 * @returns {boolean} true if the row starts with '##'
	 */
	private isComment(trimmedRow: string): boolean {
		return /^##/.test(trimmedRow);
	}
}
