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
import { PackagerOptions } from "./lib/model";
import { create as createXML } from 'xmlbuilder2';
import { copyFileSync, mkdirSync, readFileSync, writeFileSync } from 'fs';
import { findFiles } from './lib/file-system';
import Zip from 'adm-zip';

/**
 * Create vRO tree structure that can be later converted to a vRO package
 */
export class VroEnvironment {

	private readonly scriptModuleDir: string
	private readonly DEFAULT_VERSION = '1.0.0';
	private readonly DEFAULT_MEMORY_LIMIT_MB = 64;
	private readonly DEFAULT_TIMEOUT_SEC = 180;

	constructor(private readonly logger: Logger, private readonly options: PackagerOptions) {
        this.scriptModuleDir = path.join(this.options.vro, 'src', 'main', 'resources', 'ScriptModule');
	}

	async createEnvironments() {
        const envFiles = findFiles([ 'src/resources/environments/*.json' ], {
            exclude: [],
            path: this.options.workspace,
            absolute: true
        });

        if (envFiles.length == 0) {
            this.logger.info('No vRO environments found in project');
            return;
        }

		this.logger.info('Creating vRO environments structure...');
		mkdirSync(this.scriptModuleDir, { recursive: true });
        for (var i = 0; i < envFiles.length; i++) {
            const envName = path.parse(envFiles[i]).name;
    		// create structure
    		this.copyEnvironmentData(envFiles[i], envName);
	    	this.generateMeta(envFiles[i], envName);
		    this.generateTags(envName);
		    this.createEmptyBundle(envName);
        }
	}

	private copyEnvironmentData(source: string, envName: string) {
        const dest = path.join(this.scriptModuleDir, `${envName}.xml`);
		copyFileSync(source, dest);
	}

	private generateMeta(source: string, envName: string) {
		const content = {
			'properties': {
				comment: 'UTF-16',
				entry: [
					{ '@key': 'type', '#': 'ActionEnvironment', },
					{ '@key': 'id', '#': this.getId(source), },
				]
			}
		}

		const doc = createXML({ version: '1.0', encoding: 'UTF-8', standalone: false }, content);
		const xml = doc.end({ prettyPrint: true });
		writeFileSync(path.join(this.scriptModuleDir, `${envName}.element_info.xml`), xml);
	}

	private generateTags(envName: string) {
		const content = {
			'tags': {
				tag: [].map(t => ({ '@name': t, '@global': true }))
			}
		}

		const doc = createXML({ version: '1.0', encoding: 'UTF-8' }, content);
		const xml = doc.end({ prettyPrint: true });
		writeFileSync(path.join(this.scriptModuleDir, `${envName}.tags.xml`), xml);
	}

	private createEmptyBundle(envName: string) {
        const zip = new Zip();
        zip.writeZip(path.join(this.scriptModuleDir, `${envName}.bundle.zip`));
	}

	private getId(source: string) {
        return JSON.parse(readFileSync(source).toString()).id;
	}

}
