/*-
 * #%L
 * polyglotpkg
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

/*
 * Command line interface entrypoint. This module servers the purpose of loading
 * and interracting with the packager from command line.
 */
import path from 'path';
import cmdArgs from "command-line-args";
import createLogger from './lib/logger';
import util from 'util';
import { Packager } from './packager';
import { existsSync, readFileSync } from 'fs';

interface CliInputs extends cmdArgs.CommandLineOptions {
	/** verbose logging */
	verbose: boolean;
	x: boolean;
	/** print version */
	version: boolean,
	/** print out help string and exit */
	help: boolean;
	/** project directory containing package.json */
	projectDir: string,
	p: string,
	/** out directory containing intermediate transpiled files */
	outDir: string,
	o: string,
	/** distribution bundle name */
	bundleName: string,
	b: string,
	/** vro tree base location */
	vroTree: string,
	/** skip vro tree genration */
	skipVroTree: boolean,
	/** package for a specific environment */
	env: string,
	e: string,
}

const cliOpts = <cmdArgs.OptionDefinition[]>[
	{ name: "verbose", alias: "x", type: Boolean, defaultValue: false },
	{ name: "version", type: Boolean, defaultValue: false },
	{ name: "help", alias: "h", type: Boolean, defaultValue: false },
	{ name: "projectDir", alias: "p", type: String, defaultValue: path.resolve('.') },
	{ name: "outDir", alias: "o", type: String, defaultValue: path.resolve('.', 'out') },
	{ name: "bundleName", alias: "b", type: String, defaultValue: path.resolve('.', 'dist', 'bundle.zip') },
	{ name: "vroTree", type: String, defaultValue: path.resolve('.', 'dist', 'vro') },
	{ name: "skipVroTree", type: Boolean, defaultValue: false },
	{ name: "env", alias: "e", type: String, defaultValue: null },
];

const input = cmdArgs(cliOpts, { stopAtFirstUnknown: true }) as CliInputs;
const logger = createLogger(input.verbose);

async function run(): Promise<void> {

	logger.debug(`Change current dir to ${input.projectDir}`);
	process.chdir(input.projectDir);

	logger.debug(`Parsed Inputs: ${util.inspect(input)}`);

	if (input._unknown) {
		logger.error(`Unexpected option: ${input._unknown}`);
		printUsage();
		return;
	}

	if (input.help) {
		printVersion();
		printUsage();
		return;
	}

	if (input.version) {
		printVersion();
		return;
	}

	const packager = new Packager({
		bundle: input.bundleName,
		workspace: input.projectDir,
		out: input.outDir,

		vro: input.vroTree,         // vro tree dir
		skipVro: input.skipVroTree, // skip creating vro tree
		env: input.env,             // package for a specific environment
	});

	await packager.packageProject();

	logger.info('Package successfully created');
}

/**
 * Print the tool version
 */
function printVersion() {
	const packageJsonPath = path.join(__dirname, "../package.json");
	if (existsSync(packageJsonPath)) {
		const packageConfig = JSON.parse(readFileSync(packageJsonPath).toString());
		logger.info(`Version ${packageConfig.version}`);
	}
}

/**
 * Print help
 */
function printUsage() {
	const usageFilePath = path.join(__dirname, "../Usage.txt");
	if (existsSync(usageFilePath)) {
		const usageText = readFileSync(usageFilePath).toString();
		logger.info(usageText);
	}
}

run();
