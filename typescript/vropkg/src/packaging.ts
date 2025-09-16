/*-
 * #%L
 * vropkg
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
import * as fs from "fs-extra";
import * as archiver from 'archiver';
import * as unzipper from 'unzipper';
import getLogger from "./logger";

/*
 * Utility class for variety of packaging operations.
 */

/**
 * Create an archive for a given path. The archive will be finalized once
 * the archive object finalize method gets called
 *
 * @param outputPath archive given path
 * @returns {archiver.Archiver} archiver object
 */
// Extend the Archiver type at runtime with a done Promise for completion awareness
export type ArchiverWithDone = archiver.Archiver & { done: Promise<void> };

export const archive = (outputPath: string): ArchiverWithDone => {
	const output = fs.createWriteStream(outputPath);
	const instance = archiver.create('zip', { zlib: { level: 6 } });

	// Create a promise that resolves when the output stream is finished/closed
	const done = new Promise<void>((resolve, reject) => {
		const onError = (err: any) => reject(err);
		output.on('finish', () => resolve());
		output.on('close', () => resolve());
		output.on('error', onError);
		instance.on('error', onError);
		instance.on('warning', (err: any) => {
			// pass through non-ENOENT as error, log ENOENT
			if (err?.code === 'ENOENT') {
				console.warn(err);
			} else {
				onError(err);
			}
		});
	});

	instance.pipe(output);
	(instance as any).done = done;

	return instance as ArchiverWithDone;
}

/**
 * Extract the content of a ZIP file into a selected folder
 *
 * @param assemblyFilePath relative or full file system path to the package file to be disassembled.
 * @param destinationDir folder under which the package content will be extracted
 */
export const extract = async (assemblyFilePath: string, destinationDir): Promise<void> => {
	const fileToExtract = await unzipper.Open.file(assemblyFilePath);
	return fileToExtract.extract({ path: destinationDir }).catch(error => {
		getLogger().info(
			`Error extracting ${assemblyFilePath} into ${destinationDir}.` +
			`Error ${error.message},file ${error.fileName}, line ${error.lineNumber}`
		);
	})
}
