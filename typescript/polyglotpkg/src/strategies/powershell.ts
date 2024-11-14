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
import path from 'path';

import { Logger } from "winston";
import { BaseStrategy } from "./base";
import { run } from "../lib/utils";
import { ActionOptions, PlatformDefinition, Events } from "../lib/model";
import { cpSync, mkdirSync, readFileSync } from 'fs';
import { findFiles } from '../lib/file-system';

export class PowershellStrategy extends BaseStrategy {

	constructor(logger: Logger, options: ActionOptions, phaseCb: Function) { super(logger, options, phaseCb) }

	/**
	 * package project into bundle
	 */
	async packageProject() {

		const polyglotJson = JSON.parse(readFileSync(this.options.polyglotJson).toString("utf8")) as PlatformDefinition;

		this.phaseCb(Events.COMPILE_START);
		await this.compile(this.options.src, this.options.out);
		this.phaseCb(Events.COMPILE_END);
		this.phaseCb(Events.DEPENDENCIES_START);
		await this.installDependencies(polyglotJson);
		this.phaseCb(Events.DEPENDENCIES_END);
		this.phaseCb(Events.BUNDLE_START);
		await this.createBundle(polyglotJson);
		this.phaseCb(Events.BUNDLE_END);
	}

	private async createBundle(polyglotJson: PlatformDefinition): Promise<void> {
		const workspaceFolderPath = this.options.workspace;
		const patterns = ['package.json'];

		if (Array.isArray(polyglotJson.files) && polyglotJson.files.length > 0) {
			patterns.push(...polyglotJson.files);
			// Replace %src and %out placeholders with the actual paths from action options
			for (var i = 0; i < patterns.length; i++) {
				patterns[i] = patterns[i].replace('%src', this.options.src).replace('%out', this.options.out).replace(/\\/g, '/');
			}
		} else {
			patterns.push('*.ps1');
			const outDir = path.relative(workspaceFolderPath, this.options.out);
			patterns.push(`${outDir}/**`);
		}

        const filesToBundle = findFiles(patterns, {
            path: workspaceFolderPath,
            absolute: true
        });

        const modulesToBundle = findFiles([ 'Modules' ], {
            path: this.options.outBase, // use action specific out subfolder
            absolute: true
        });


		this.logger.info(`Packaging ${filesToBundle.length} files into bundle ${this.options.bundle}...`);
		const actionBase = polyglotJson.platform.base ? path.resolve(polyglotJson.platform.base) : this.options.outBase;
		this.logger.info(`Action base: ${actionBase}`);
        this.zipFiles([
			{ files: filesToBundle, baseDir: actionBase },
			{ files: modulesToBundle, baseDir: actionBase }
		]);
	}

	private async compile(source: string, destination: string) {
		this.logger.info(`Compiling project...`);
        cpSync(source, destination, { recursive: true });
		this.logger.info(`Compilation complete`);
	}

	private async installDependencies(polyglotJson: PlatformDefinition) {

		const psScriptName: string = polyglotJson.platform.entrypoint.split("/")[1].split(".")[0];
		const depsManifest: string = path.join(this.options.out, `${psScriptName}.ps1`);
		const deps: string = readFileSync(depsManifest, "utf-8");
		const modulesPath: string = path.resolve(path.join(this.options.outBase, "Modules"));
		const modules: string[] = [];

		deps.split(/\r?\n/).forEach(line => {
			if (line.indexOf("Import-Module") !== -1 && line.indexOf("#@ignore") === -1 && !line.startsWith("#")) {
				const module: string = line.replace("Import-Module", "").replace("-Name", "").replace(";", "").trim();
				modules.push(module);
			}
		});

		this.logger.info(`Powershell modules included: ${modules}`);

		const moduleNames = modules.join(",");
		const securityProtocolArg = polyglotJson.platform.protocolType ? `[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::${polyglotJson.platform.protocolType};` : "";
		const command = `${securityProtocolArg} Save-Module -Name ${moduleNames} -Path '${modulesPath}' -Repository PSGallery`;

		const args = polyglotJson.platform.protocolType ? [`-c "${command}"`] : [command];
		if (modules.length > 0) {
			mkdirSync(modulesPath, { recursive: true });
			this.logger.info(`Downloading and saving dependencies in "${modulesPath}..."`);
			await run("pwsh", args);
		} else {
			this.logger.info("No change in dependencies. Skipping installation...");
		}
	}

}
