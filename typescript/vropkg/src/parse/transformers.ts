/*-
 * #%L
 * vropkg
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
/**
 * Regex used to match 'for each' loops
 *
 * Works for the following cases:
 *   - Single line
 *   - Multiple lines
 *   - Nested loops
 *   - Multiple nested loops
 *   - Multiple nested loops with multiple lines
 *
 * Does not work for:
 *  - `for each (var n in Object.values(x)) {` or other complex expressions
 */
const FOR_EACH_REGEX = /for each\s*\(\s*var\s+(\w+)\s+in\s+(\w+)\s*\)\s*\{\s*([\s\S]*?)\s*\}/;

/**
 * Structure of the transformed code
 *
 * @NOTE: Indentation gets lost in the transformation and mutliple line for-each loops will be on a single line
 *
 * @param {string} iterator Iterator variable
 * @param {string} collection Collection to iterate over
 * @param {string} body Body of the loop
 *
 * @returns {string} Transformed code
 */
function getTransformationReplacer(iterator: string, collection: string, body: string): string {
	return `for (var $index_${iterator} in ${collection}) {
	var ${iterator} = ${collection}[$index_${iterator}];
	${body}
}`;
}

/**
 * Transforms 'for each' loops to standard 'for' loops with variable declaration
 *
 * @param {string} source Source code to transform
 * @returns {string} Transformed source code
 */
export function transformForEachToFor(source: string): string {
	while (FOR_EACH_REGEX.test(source)) {
		source = source.replace(
			FOR_EACH_REGEX,
			(match, iterator, collection, body) => getTransformationReplacer(iterator, collection, body)
		);
	}

	return source;
}
