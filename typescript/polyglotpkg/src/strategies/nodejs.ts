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
import ts from "typescript";
import path from 'path';

import { Logger } from "winston";
import { BaseStrategy } from "./base";
import { getActionManifest, notUndefined, run } from "../lib/utils";
import { ActionOptions, PackageDefinition, PlatformDefinition, Events } from "../lib/model";
import { existsSync, mkdirSync, readFileSync, rmSync, writeFileSync } from "fs";
import { findFiles } from "../lib/file-system";

export class NodejsStrategy extends BaseStrategy {

	constructor(logger: Logger, options: ActionOptions, phaseCb: Function) { super(logger, options, phaseCb) }

	/**
	 * package project into bundle
	 */
	async packageProject() {

		const polyglotJson = JSON.parse(readFileSync(this.options.polyglotJson).toString("utf8")) as PlatformDefinition;
		const tsconfigPath = path.join(this.options.actionBase, "tsconfig.json");
		const tmpTsconfigPath = this.options.mixed ? path.join(this.options.actionBase, "tsconfig.tmp.json") : tsconfigPath;
		if (!existsSync(tsconfigPath)) {
			throw new Error('Could not find tsconfig.json in the action root');
		}

		this.phaseCb(Events.COMPILE_START);
		const tsconfig = this.readConfigFile(tsconfigPath, tmpTsconfigPath);
		this.compile(tsconfigPath, tmpTsconfigPath, tsconfig);
		this.phaseCb(Events.COMPILE_END);
		this.phaseCb(Events.DEPENDENCIES_START);
		await this.installDependencies(polyglotJson);
		this.phaseCb(Events.DEPENDENCIES_END);
		this.phaseCb(Events.BUNDLE_START);
		await this.createBundle(polyglotJson, tsconfig);
		this.phaseCb(Events.BUNDLE_END);
		// Delete the temporary created tsconfig.json
		if (this.options.mixed) {
			rmSync(tmpTsconfigPath, { recursive: true });
		}
	}

	private async createBundle(polyglotJson: PlatformDefinition, tsconfig: ts.ParsedCommandLine): Promise<void> {
		const workspaceFolderPath = this.options.workspace;
		const baseDir = tsconfig.options.baseUrl || workspaceFolderPath;
		const patterns = ['package.json'];

		if (Array.isArray(polyglotJson.files) && polyglotJson.files.length > 0) {
			patterns.push(...polyglotJson.files);
			// Replace %src and %out placeholders with the actual paths from action options
			for (var i = 0; i < patterns.length; i++) {
				patterns[i] = patterns[i].replace('%src', this.options.src).replace('%out', this.options.out).replace(/\\/g, '/');
			}
		} else {
			patterns.push('*.js');

			if (tsconfig.options.outDir) {
				const outDir = path.relative(workspaceFolderPath, path.join(baseDir, tsconfig.options.outDir));
				patterns.push(`${outDir}/**`);
			}

			if (tsconfig.options.rootDir) {
				const rootDir = path.relative(workspaceFolderPath, path.join(baseDir, tsconfig.options.rootDir));
				patterns.push(`${rootDir}/**`);
			}
		}

        const filesToBundle = findFiles(patterns, {
            path: workspaceFolderPath,
            absolute: true
        });

        const depsToBundle = findFiles([ "**" ], {
            path: this.DEPENDENCY_TEMP_DIR,
            absolute: true
        });

		this.logger.info(`Packaging ${filesToBundle.length + depsToBundle.length} files into bundle ${this.options.bundle}...`);
		const actionBase = polyglotJson.platform.base ? path.resolve(polyglotJson.platform.base) : this.options.outBase;
		this.logger.info(`Action base: ${actionBase}`);
		this.zipFiles([
			{ files: filesToBundle, baseDir: actionBase },
			{ files: depsToBundle, baseDir: this.DEPENDENCY_TEMP_DIR }
		]);
	}


	private compile(tsconfigPath: string, tmpTsconfigPath: string, config: ts.ParsedCommandLine): void {
		this.logger.info(`Compiling project ${tsconfigPath}...`);

		const diagnostics: ts.Diagnostic[] = [];
		const buildHost = ts.createSolutionBuilderHost(
			ts.sys,
			undefined,
			(diagnostic: ts.Diagnostic) => {
				this.reportDiagnostics([diagnostic])
				diagnostics.push(diagnostic)
			},
			(diagnostic: ts.Diagnostic) => {
				this.reportDiagnostics([diagnostic])
				diagnostics.push(diagnostic)
			}
		);
		const builder = ts.createSolutionBuilder(buildHost, [tmpTsconfigPath], {
			verbose: true,
		});
		const exitStatus = builder.build();

		this.logger.info(`Exit status: ${exitStatus}`);
		this.logger.info(`Compilation complete`);
		if (diagnostics.some(val => val.category === ts.DiagnosticCategory.Error)) {
			throw new Error('Found compilation errors');
		}
	}

	private readConfigFile(srcTsconfigPath: string, newTsconfigPath: string): ts.ParsedCommandLine {
		let configFileText = readFileSync(srcTsconfigPath).toString();
		if (this.options.mixed) {
			// The source directory of each action contain tsconfig.json
			// It contains a %out placeholder to be replaced with a reference to action-specific out dir
			// A temporary copy of tsconfig.tmp.json is created with resolved %out and deleted after the compilation
			// The %out path is relative to the location and must be formatted as posix path
			const newOut = path.relative(this.options.actionBase, path.join(path.resolve(this.options.workspace), this.options.out)).replace(/\\/g, '/');
			configFileText = configFileText.replace(/%out/g, newOut);
			writeFileSync(newTsconfigPath, configFileText);
		}
		const result = ts.parseConfigFileTextToJson(newTsconfigPath, configFileText);
		const configObject = result.config;
		if (!configObject) {
			this.reportDiagnostics([result.error]);
			throw new Error(`Could not parse ${newTsconfigPath}`);
		}

		const configParseResult = ts.parseJsonConfigFileContent(configObject, ts.sys, this.options.workspace);
		const numberOfErrors = configParseResult.errors.length;
		if (numberOfErrors > 0) {
			this.reportDiagnostics(configParseResult.errors);
			throw new Error(`Found ${numberOfErrors} errors in ${newTsconfigPath}`);
		}
		this.logger.info(`TS compiler options: ${JSON.stringify(configParseResult.options, null, 2)}`);
		return configParseResult;
	}

	private reportDiagnostics(optionalDiagnostics: (ts.Diagnostic | undefined)[]): void {
		const diagnostics = optionalDiagnostics.filter(notUndefined);

		if (diagnostics.length <= 0) {
			return;
		}

		diagnostics.forEach((diagnostic) => {
			let message = `   ${ts.DiagnosticCategory[diagnostic.category]} ts(${diagnostic.code})`;
			if (diagnostic.file && diagnostic.start) {
				let { line, character } = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
				message += ` ${diagnostic.file.fileName} (${line + 1},${character + 1})`;
			}
			message += `: ${ts.flattenDiagnosticMessageText(diagnostic.messageText, "\n")}`;
			this.logger.info(message);
		});
	}

	private async installDependencies(polyglotJson: PlatformDefinition) {
		const depsCacheDir = this.DEPENDENCY_TEMP_DIR;
		mkdirSync(depsCacheDir, { recursive: true });
		const projectPkg = await getActionManifest(this.options.workspace) as PackageDefinition;
		const depPkg = await getActionManifest(this.options.actionBase) as PackageDefinition;
		const packageJson = { ...projectPkg, ...depPkg, ...polyglotJson };

		for (const [key, value] of Object.entries(packageJson.dependencies as { [key: string]: string })) {
			if (value.startsWith("file:")) {
				// There was some code that was recursively resolving dependencies
				// It was not working and causing failure and was removed
				const localLibPath = path.join(this.options.actionBase, value.replace("file:", ""));
				packageJson.dependencies[key] = path.relative(depsCacheDir, localLibPath);
			}
			// Remove dependencies to the toolchain
			if (key.startsWith("@vmware-pscoe")) {
				delete packageJson.dependencies[key];
			}
		}

		const deps = JSON.stringify(packageJson.dependencies);
		const hash = this.getHash(deps);
		const existingHash = this.readDepsHash(depsCacheDir);
		if (existingHash !== hash) {
			this.logger.info(`Installing dependencies at ${depsCacheDir}`);
			writeFileSync(path.join(depsCacheDir, 'package.json'), JSON.stringify(packageJson, null, 2));
			await run("npm", ["install", "--production"], depsCacheDir);
			this.writeDepsHash(deps);
		} else {
			this.logger.info("No change in dependencies. Skipping installation...");
		}
	}

}
