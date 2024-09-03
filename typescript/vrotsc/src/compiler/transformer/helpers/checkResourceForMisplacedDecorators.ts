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

/**
 * Checks a Resource file for a specific decorator and matching extension, indicationg an unintentionally omitted ".ts" extension:
 * @param {string} filePath -path of the resource file
 * @param {string} content - content of the file as string
 * @throws Error if the file with one of these extensions contains a matching TS class-level decorator in its contents:
 * - .conf -> Configuration
 * - .pl -> PolicyTemplate
 * - .wf -> Workflow
 */
export function checkResourceForMisplacedDecorators(filePath: string, content: string) {
	filePath = filePath?.toLowerCase() || "";
	let errMsg: string;
	if (filePath.endsWith(".wf") && content?.match(new RegExp("@Workflow\\s*\\(\\s*{", "gm"))) {
		errMsg = `'.wf' is not a valid Workflow file extension. Use '.wf.ts' instead.`;
	}
	else if (filePath.endsWith(".pl") && content?.match(new RegExp("@PolicyTemplate\\s*\\(\\s*{", "gm"))) {
		errMsg = `'.pl' is not a valid Policy Template file extension. Use '.pl.ts' instead.`;
	}
	else if (filePath.endsWith(".conf") && content?.match(new RegExp("@Configuration\\s*\\(\\s*{", "gm"))) {
		errMsg = `'.conf' is not a valid Configuration Element file extension. Use '.conf.ts' instead.`;
	}
	if (errMsg) {
		throw new Error(errMsg);
	}
}
