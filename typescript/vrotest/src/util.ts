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
import * as iconv from "iconv-lite";
import charsetDetector from "charset-detector";
import JSZip from "jszip";
import { join, dirname } from "path";
import { XMLParser } from "fast-xml-parser";
import { readFile, readFileSync, existsSync, readdir, lstat, ensureDir, writeFile } from "fs-extra";

export async function forEachFile(dir: string, action: (filePath: string) => Promise<void>) {
	const entries = await readdir(dir);
	const stats = await Promise.all(entries.map(p => lstat(join(dir, p))));
	const fileNames = entries.filter((_, i) => stats[i].isFile());
	const childDirNames = entries.filter((_, i) => stats[i].isDirectory());
	await Promise.all(fileNames.map(fileName => join(dir, fileName)).map(action));
	await Promise.all(childDirNames.map(childDirName => forEachFile(join(dir, childDirName), action)));
}

export function decodeBuffer(buf: Buffer): string {
	const encodings = charsetDetector(buf);
	if (encodings.length) {
		const encoding = encodings[0].charsetName;
		return iconv.decode(buf, encoding);
	}
	else {
		return buf.toString("utf8");
	}
}

export async function parseXmlFile<T>(filePath: string): Promise<T> {
	const buf = await readFile(filePath);
	const str = decodeBuffer(buf);
	const obj = new XMLParser({ ignoreAttributes: false }).parse(str) as T;
	return obj;
}

export function extractProjectPomDetails(projectPath: string) {
    const details = {
        name: "vro-test",
        version: "1.0.0-SNAPSHOT",
    };

    const pomPath = join(projectPath, "pom.xml");
    if (!existsSync(pomPath)) {
        return details;
    }

    const pomContent = readFileSync(pomPath).toString("utf8");
    const parser = new XMLParser();
    const pomXml = parser.parse(pomContent);

    details.name = `${pomXml.project.groupId}.${pomXml.project.artifactId}`;
    details.version = pomXml.project.version;

    return details;
}

export async function extractZipToFolder(zipFilePath: string, targetPath: string): Promise<void> {
	const zipData = await readFile(zipFilePath);
	const zip = await JSZip.loadAsync(zipData);
	const zipFiles = Object.entries(zip.files).filter(([_, zipObj]) => !zipObj.dir);
	const buffers = await Promise.all(zipFiles.map(([_, zipObj]) => zipObj.async("nodebuffer")));
	const fileBufferTupples = zipFiles.map(([n, _]) => n).map((fileName, index) => <[string, Buffer]>[join(targetPath, fileName), buffers[index]]);
	await Promise.all(fileBufferTupples.map(([filePath, _]) => ensureDir(dirname(filePath))));
	await Promise.all(fileBufferTupples.map(([filePath, data]) => writeFile(filePath, data)));
}

export async function withWorkingDir(dir: string, action: () => Promise<void>): Promise<void> {
	const cwd = process.cwd();
	const needDirChange = dir && dir !== cwd;
	if (needDirChange) {
		process.chdir(dir);
	}
	try {
		await action();
	}
	finally {
		if (needDirChange) {
			process.chdir(cwd);
		}
	}
}
