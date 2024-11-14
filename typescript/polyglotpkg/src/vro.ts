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
import { v5 as uuidv5 } from 'uuid';
import { Logger } from "winston";
import { ActionOptions, VroActionDefinition } from "./lib/model";
import { create as createXML } from 'xmlbuilder2';
import { copyFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from 'fs';

/**
 * Create vRO tree structure that can be later converted to a vRO package
 */
export class VroTree {

	private readonly actionDirectory: string;
	private readonly treeDir: string;
	private readonly scriptModuleDir: string
	private readonly DEFAULT_VERSION = '1.0.0';
	private readonly DEFAULT_MEMORY_LIMIT_MB = 64;
	private readonly DEFAULT_TIMEOUT_SEC = 180;

	constructor(private readonly logger: Logger, private readonly options: ActionOptions) {
		this.actionDirectory = path.basename(options.actionBase);
		this.treeDir = options.vro;
		this.scriptModuleDir = path.join(this.options.vro, 'src', 'main', 'resources', 'ScriptModule');
	}

	async createTree() {
		this.logger.info('Creating vRO tree structure...');
		let actionDefintion = JSON.parse(readFileSync(this.options.polyglotJson).toString("utf8")) as VroActionDefinition;
		if (actionDefintion.platform.action === 'auto') {
			actionDefintion.platform.action = this.actionDirectory;
		}

		// create structure
		mkdirSync(this.scriptModuleDir, { recursive: true });

		// check if the bundle is created
		await this.checkFile(this.options.bundle);

		this.generatePOM(actionDefintion);
		this.generateAction(actionDefintion);
		this.generateMeta(actionDefintion);
		this.generateTags(actionDefintion);
		this.copyBundle(actionDefintion);

	}

	private generatePOM(actionDefintion: VroActionDefinition) {

		const content = {
			project: {
				'@xmlns': 'http://maven.apache.org/POM/4.0.0',
				'@xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
				'@xsi:schemaLocation': 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd',
				modelVersion: '4.0.0',
				groupId: actionDefintion.vro.module,
				artifactId: actionDefintion.platform.action,
				version: actionDefintion.version || this.DEFAULT_VERSION,
				packaging: 'package',
			}
		}

		const doc = createXML({ version: '1.0', encoding: 'UTF-8' }, content)
		const xml = doc.end({ prettyPrint: true });
        writeFileSync(path.join(this.treeDir, 'pom.xml'), xml);
	}

	private generateAction(actionDefintion: VroActionDefinition) {

		const runtime = this.options.actionRuntime;

		const content = {
			'dunes-script-module': {
				'@name': actionDefintion.platform.action,
				'@result-type': actionDefintion.vro.outputType,
				'@api-version': '6.0.0',
				'@id': this.getId(actionDefintion),
				'@version': (actionDefintion.version || this.DEFAULT_VERSION).replace('-SNAPSHOT', ''),
				'@allowed-operations': 'vfe',
				'@memory-limit': (actionDefintion.platform.memoryLimitMb || this.DEFAULT_MEMORY_LIMIT_MB) * 1000 * 1000,
				'@timeout': actionDefintion.platform.timeoutSec || this.DEFAULT_TIMEOUT_SEC,
				description: { '$': actionDefintion.description || '' },
				runtime: { '$': runtime },
				'entry-point': { '$': actionDefintion.platform.entrypoint },
				...(actionDefintion.vro.inputs && {
					param: Object.entries(actionDefintion.vro.inputs).map(([inputName, inputType]) => ({
						'@n': inputName,
						'@t': inputType
					}))
				}),
			}
		}

		const doc = createXML({ version: '1.0', encoding: 'UTF-8' }, content)
		const xml = doc.end({ prettyPrint: true });
		writeFileSync(path.join(this.scriptModuleDir, `${actionDefintion.platform.action}.xml`), xml);

	}

	private generateMeta(actionDefintion: VroActionDefinition) {

		const content = {
			'properties': {
				comment: 'UTF-16',
				entry: [
					{ '@key': 'categoryPath', '#': actionDefintion.vro.module, },
					{ '@key': 'type', '#': 'ScriptModule', },
					{ '@key': 'id', '#': this.getId(actionDefintion), },
					// TODO: check whether we need signature-owner
				]
			}
		}

		// TODO: generate doctype:
		// <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">

		const doc = createXML({ version: '1.0', encoding: 'UTF-8', standalone: false }, content);
		const xml = doc.end({ prettyPrint: true });
		writeFileSync(path.join(this.scriptModuleDir, `${actionDefintion.platform.action}.element_info.xml`), xml);

	}

	private generateTags(actionDefintion: VroActionDefinition) {

		const content = {
			'tags': {
				tag: (actionDefintion.platform.tags || []).map(t => ({ '@name': t, '@global': true }))
			}
		}

		const doc = createXML({ version: '1.0', encoding: 'UTF-8' }, content);
		const xml = doc.end({ prettyPrint: true });
		writeFileSync(path.join(this.scriptModuleDir, `${actionDefintion.platform.action}.tags.xml`), xml);
	}

	private copyBundle(actionDefintion: VroActionDefinition) {
		const source = this.options.bundle;
		const dest = path.join(this.scriptModuleDir, `${actionDefintion.platform.action}.bundle.zip`);
		copyFileSync(source, dest);
	}

	private getId(actionDefintion: VroActionDefinition) {
		return actionDefintion.vro.id || uuidv5(`${actionDefintion.vro.module}:${actionDefintion.platform.action}`, uuidv5.DNS)
	}

	private async checkFile(filePath: string) {
		let fileExists: boolean = existsSync(filePath);
		let timeout: number = 600000; // 10 minutes
		const interval: number = 5000;

		while (!fileExists) {
			await new Promise(resolve => setTimeout(resolve, interval));
			fileExists = existsSync(filePath);
			this.logger.info(`File exists: ${fileExists}`);
			timeout -= interval;
			if (timeout <= 0) {
				this.logger.error(`Bundle is still not ready: ${filePath}. Exiting...`);
				break;
			}
		}
		this.logger.info(`Waiting for ${interval / 1000} additional seconds...`);
		await new Promise(resolve => setTimeout(resolve, interval));
	}

}
