/*-
 * #%L
 * vrotest
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
export interface BuildCommandFlags {
	actions: string;
	projectRoot: string;
	testFrameworkPackage: string;
	testFrameworkVersion: string;
	runner: string;
	jasmineReportersVerion: string;
	ansiColorsVersion: string;
	testHelpers: string;
	tests: string;
	maps: string;
	resources: string;
	configurations: string;
	dependencies: string;
	helpers: string;
	output: string;
    vroIgnoreFile: string;
	"ts-src": string;
	"ts-namespace": string;
	"coverage-thresholds": string;
	"coverage-reports": string;
	"per-file": string;
}
