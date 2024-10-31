/*
 * #%L
 * vrotest
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
import * as path from "path";
import * as fs from "fs-extra";
import minimist from "minimist";
import build from "./build";
import run from "./run";

interface RootCommandFlags {
	help?: boolean;
	version?: boolean;
	_: string[];
}

type Command<TFlags> = (flags: TFlags) => Promise<void>;

const commands: Record<string, Command<unknown>> = { build, run };

(async () => {
	const flags = parseCommandLine();
	if (flags.version) {
		printVersion();
		return;
	}

	if (flags.help) {
		printVersion();
		printUsage(flags._[0]);
		return;
	}

	const cmdName = flags._[0] || "";
	if (!cmdName) {
		printUsage();
		process.exit(1);
	}

	const cmd = commands[cmdName];
	if (!cmd) {
		printUsage();
		process.exit(1);
	}

	await cmd(flags);
})();

function parseCommandLine(): RootCommandFlags {
	const cmdOptions = {
		boolean: ["help", "version", "instrument"],
		string: [
			"output", "actions", "testHelpers", "tests", "maps", "resources",
			"configurations", "dependencies", "helpers", "output", "projectRoot",
			"ts-src", "ts-namespace", "coverage-thresholds", "coverage-reports", "per-file",
            "testFrameworkPackage", "testFrameworkVersion", "runner"
		],
		alias: {
			"h": "help",
			"v": "version",
		}
	} as minimist.Opts;
	return minimist(process.argv.slice(2), cmdOptions) as RootCommandFlags;
}

function printVersion(): void {
	const packageJsonPath = path.join(__dirname, "..", "package.json");

	if (fs.existsSync(packageJsonPath)) {
		const packageConfig = JSON.parse(fs.readFileSync(packageJsonPath).toString());
		console.log(`Version ${packageConfig.version}`);
	}
}

function printUsage(command?: string): void {
	command = command && commands[command] ? command : "root";
	const usageFilePath = path.join(__dirname, "..", "usage", `${command}.txt`);
	if (fs.existsSync(usageFilePath)) {
		console.log(fs.readFileSync(usageFilePath).toString());
	}
}
