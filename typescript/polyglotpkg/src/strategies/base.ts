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
import Zip from 'adm-zip';
import path from 'path';
import crypto from 'crypto';
import { Logger } from "winston";
import { ActionOptions, BundleFileset } from "../lib/model";
import { copyFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from 'fs';

export interface IStrategy {
	packageProject(): Promise<void>
}

export abstract class BaseStrategy implements IStrategy {

	protected readonly DEPENDENCY_TEMP_DIR: string;

	constructor(protected readonly logger: Logger, protected readonly options: ActionOptions, protected readonly phaseCb: Function) {
		this.DEPENDENCY_TEMP_DIR = path.join(path.resolve(options.outBase), 'polyglot-cache');
	}

	protected zipFiles(filesets: Array<BundleFileset>) {
		const zip = new Zip();

		filesets.forEach(fileset => {
			const { files: filesToBundle, baseDir } = fileset;
			for (var i = 0; i < filesToBundle.length; i++) {
				const filePath = filesToBundle[i];
				const zipPath = path.dirname(path.relative(baseDir, filePath));
				zip.addLocalFile(filePath, zipPath);
				this.logger.debug(`Packaged: ${filePath}`);
			}
		})

        mkdirSync(path.dirname(this.options.bundle), { recursive: true });
		zip.writeZip(this.options.bundle);
		this.logger.info(`Created ${this.options.bundle}`);
	}

	protected copyFiles(filesets: Array<BundleFileset>, dest: string) {
		for (const fileset of filesets) {
			const { files: filesToCopy, baseDir } = fileset;
			for (const filePath of filesToCopy) {
				const destPath = path.join(path.resolve(dest), path.relative(baseDir, filePath));
                mkdirSync(path.dirname(destPath), { recursive: true });
                copyFileSync(filePath, destPath);
				this.logger.debug(`Copied: ${filePath}`);
			}
		}
	}

	protected getHash(hashable: string) {
		return crypto.createHash('sha256').update(hashable).digest('hex');
	}

	protected writeDepsHash(hashable: string) {
		const hash = this.getHash(hashable);
		const file = path.join(this.DEPENDENCY_TEMP_DIR, 'deps.sha256');
		writeFileSync(file, hash);
	}

	protected readDepsHash(depsPath: string): string | null {
		const file = path.join(depsPath, 'deps.sha256');
		const exists = existsSync(file);
		if (exists) {
			return readFileSync(file, 'utf-8');
		} else {
			return null;
		}
	}

	abstract packageProject(): Promise<void>;

}
