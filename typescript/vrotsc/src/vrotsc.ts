import minimist = require("minimist");
import * as ansiColors from "ansi-colors";
import { existsSync, readFileSync } from "fs-extra";
import { join } from "path";
import { system } from "./system/system";
import { DiagnosticCategory, ProgramOptions, WriteFileCallback, Diagnostic } from "./types";
import { StringBuilderClass } from "./utilities/stringBuilder";
import { createProgram } from "./compiler/program";
import path = require("path");

interface ParsedArgs {
	help?: boolean;
	version?: boolean;
	emitHeader?: boolean;
	project?: string;
	files?: string;
	output?: string;
	actionsOut?: string;
	workflowsOut?: string;
	policiesOut?: string;
	configsOut?: string;
	resourcesOut?: string;
	testsOut?: string;
	testHelpersOut?: string;
	typesOut?: string;
	mapsOut?: string;
	actionsNamespace?: string;
	workflowsNamespace?: string;
	_: string[];
}

/**
 * Executes the main program.
 *
 * This function is the entry point of the application. It parses the command line arguments,
 * sets up the necessary options for the program, creates the program, emits the results, and
 * handles any diagnostics that are produced.
 */
export function execute() {
	const commandLine = <ParsedArgs>minimist(system.args, {
		boolean: ["help", "version", "emitHeader"],
		string: ["files", "output", "actionsOut", "workflowsOut", "policiesOut", "configsOut", "resourcesOut", "testsOut", "testHelpersOut", "typesOut", "mapsOut", "actionsNamespace", "workflowsNamespace", "project"],
		alias: {
			"h": "help",
			"v": "version",
			"t": "target",
			"o": "output",
			"p": "project"
		}
	});

	if (commandLine.version) {
		printVersion();
		return;
	}

	if (commandLine.help || commandLine._.length > 1) {
		printVersion();
		printUsage();
		return;
	}

	const rootDir = commandLine._.length ? system.resolvePath(commandLine._[0]) : system.getCurrentDirectory();
	const outDir = commandLine.output || "";
	const programOptions: ProgramOptions = {
		rootDir: rootDir,
		emitHeader: commandLine.emitHeader,
		project: commandLine.project,
		actionsNamespace: commandLine.actionsNamespace,
		workflowsNamespace: commandLine.workflowsNamespace,
		files: commandLine.files ? commandLine.files.split(",") : null,
		outputs: {
			actions: commandLine.actionsOut || system.joinPath(outDir, "actions"),
			workflows: commandLine.workflowsOut || system.joinPath(outDir, "workflows"),
			configs: commandLine.configsOut || system.joinPath(outDir, "configs"),
			resources: commandLine.resourcesOut || system.joinPath(outDir, "resources"),
			policyTemplates: commandLine.policiesOut || system.joinPath(outDir, "policies"),
			tests: commandLine.testsOut || system.joinPath(outDir, "tests"),
			testHelpers: commandLine.testHelpersOut || system.joinPath(outDir, "testHelpers"),
			types: commandLine.typesOut || system.joinPath(outDir, "types"),
			maps: commandLine.mapsOut || system.joinPath(outDir, "maps"),
		}
	};
	const writeFileCallback: WriteFileCallback = (fileName: string, data: string | Buffer) => {
		let dirName = system.dirname(fileName);
		system.ensureDir(dirName);
		system.writeFile(fileName, data);
	};
	const program = createProgram(programOptions);
	const emitResult = program.emit(writeFileCallback);
	printDiagnostics(emitResult.diagnostics);

	const errorCount = emitResult.diagnostics.filter(d => d.category === DiagnosticCategory.Error).length;
	if (errorCount) {
		console.error(`Found ${errorCount} error${errorCount > 1 ? "s" : ""}.`);
		system.exit(1);
	}
}

/**
 * Prints the version of the application.
 *
 * This function reads the version from the package.json file and prints it to the console.
 */
function printVersion(): void {
	let packageJsonPath = join(__dirname, "..", "package.json");

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

	if (existsSync(packageJsonPath)) {
		let packageConfig = JSON.parse(
			readFileSync(packageJsonPath).toString()
		);
		console.log(`Version ${packageConfig.version}`);
	}
}

/**
 * Prints the usage instructions for the application.
 *
 * This function reads the usage instructions from a text file and prints them to the console.
 */
function printUsage(): void {
	let usageFilePath = join(__dirname, "..", "Usage.txt");
	if (existsSync(usageFilePath)) {
		console.log(readFileSync(usageFilePath).toString());
	}
}

/**
 * Prints the diagnostics produced by the program.
 *
 * This function iterates over the diagnostics produced by the program and prints them to the console.
 * It uses different colors for different categories of diagnostics.
 *
 * @param {readonly Diagnostic[]} diagnostics - The diagnostics to print.
 */
function printDiagnostics(diagnostics: readonly Diagnostic[]): void {
	diagnostics.forEach(d => {
		const sb = new StringBuilderClass();
		if (d.file) {
			sb.append(ansiColors.cyan(d.file.split("/").join(path.sep)));
			sb.append(":");
			sb.append(ansiColors.yellow("" + (d.line || 1)));
			sb.append(":");
			sb.append(ansiColors.yellow("" + (d.col || 1)));
			sb.append(" - ");
		}

		switch (d.category) {
			case DiagnosticCategory.Warning:
				sb.append(ansiColors.yellow("warning"));
				break;
			case DiagnosticCategory.Error:
				sb.append(ansiColors.red("error"));
				break;
			case DiagnosticCategory.Suggestion:
				sb.append(ansiColors.magenta("suggestion"));
				break;
			case DiagnosticCategory.Message:
				sb.append(ansiColors.gray("info"));
				break;
		}

		sb.append(` ${d.messageText}`);
		console.log(sb.toString());
	});
}
