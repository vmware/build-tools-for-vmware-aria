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
import * as os from "os";
import { basename, dirname, extname, join, normalize, relative, resolve, sep } from "path";
import { statSync, readFileSync, removeSync, readdirSync, writeFileSync, ensureDirSync, emptyDirSync } from "fs-extra";
import { v3 as _uuid } from "uuid";

/**
* This class is a wrapper around Node.js file system and path modules.
* It provides a consistent API for file system and path operations.
* It also provides some additional methods for common file system operations.
*/

export const system = {
	args: process.argv.slice(2),
	newLine: os.EOL,
	pathSeparator: sep,
	getExecutingFilePath() {
		return toUnixPath(__filename);
	},
	getExecutingDirPath() {
		return toUnixPath(__dirname);
	},
	getCurrentDirectory() {
		return toUnixPath(process.cwd());
	},
	getEnvironmentVariable(name: string) {
		return process.env[name] || "";
	},
	resolvePath(...pathSegments: string[]): string {
		return toUnixPath(resolve(...pathSegments));
	},
	relativePath(from: string, to: string): string {
		return toUnixPath(relative(from, to));
	},
	joinPath(...paths: string[]): string {
		return toUnixPath(join(...paths));
	},
	normalizePath(path: string): string {
		return toUnixPath(normalize(path));
	},
	dirname(path: string): string {
		return toUnixPath(dirname(path));
	},
	basename(path: string, ext?: string): string {
		return toUnixPath(basename(path, ext));
	},
	extname(fileName: string): string {
		return toUnixPath(extname(fileName));
	},
	fileExists(path: string): boolean {
		try {
			return statSync(path).isFile();
		}
		catch (e) {
			return false;
		}
	},
	directoryExists(path: string): boolean {
		try {
			return statSync(path).isDirectory();
		}
		catch (e) {
			return false;
		}
	},
	readFile(path: string): Buffer {
		return readFileSync(path);
	},
	deleteFile(path: string): void {
		removeSync(path);
	},
	getFiles(path: string, recursive?: boolean): string[] {
		let files = readdirSync(path, { withFileTypes: true })
			.filter(ent => !ent.isDirectory())
			.filter(ent => ent.name[0] !== ".")
			.map(ent => system.joinPath(path, ent.name));

		if (recursive) {
			system.getDirectories(path).forEach(dirName => {
				files = files.concat(system.getFiles(system.joinPath(path, dirName), true));
			});
		}
		return files;
	},
	getDirectories(path: string): string[] {
		return readdirSync(path, { withFileTypes: true })
			.filter(ent => ent.isDirectory())
			.filter(ent => ent.name[0] !== ".")
			.map(ent => ent.name);
	},
	changeFileExt(name: string, newExt: string, currentExtFilter?: string[]): string {
		if (currentExtFilter && currentExtFilter.length) {
			const currentExt = currentExtFilter.find(ext => name.toLowerCase().endsWith(ext.toLowerCase()));
			if (currentExt) {
				name = name.substring(0, name.length - currentExt.length);
			}
		}
		else {
			const currentExt = extname(name);
			if (currentExt) {
				name = name.substring(0, name.length - currentExt.length);
			}
		}
		name += newExt;
		return name;
	},
	writeFile(path: string, data: string | Buffer): void {
		writeFileSync(path, data);
	},
	ensureDir(path: string): void {
		ensureDirSync(path);
	},
	emptyDir(path: string): void {
		emptyDirSync(path);
	},
	exit(exitCode?: number): void {
		process.exit(exitCode);
	},
	uuid(name: string, namespace: string): string {
		return _uuid(name, namespace);
	},
};

/**
* Converts a path to Unix format.
*/
function toUnixPath(path: string): string {
	return path.replace(/\\/g, "/");
}
