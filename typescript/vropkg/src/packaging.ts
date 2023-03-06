import * as fs from "fs-extra";
import * as archiver from 'archiver';
import * as unzipper from 'unzipper';
import * as winston from 'winston';

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
export const archive = (outputPath: string): archiver.Archiver => {

	let instance =
		archiver.create('zip', { zlib: { level: 6 } })
			.on('warning', (err) => {
				if (err.code === 'ENOENT') {
					console.warn(err)
				} else {
					throw err;
				}
			})
			.on('error', function (err) {
				throw err;
			})

	instance.pipe(fs.createWriteStream(outputPath));

	return instance;
}

/**
 * Extract the content of a ZIP file into a selected folder
 *
 * @param assemblyFilePath relative or full file system path to the package file to be disassembled.
 * @param destinationDir folder under which the package content will be extracted
 */
export const extract = async (assemblyFilePath: string, destinationDir): Promise<void> => {
	let extractor = unzipper.Extract({ path: destinationDir })
	return fs.createReadStream(assemblyFilePath).pipe(extractor).promise().catch(error => {
		winston.loggers.get("vrbt").info(
			`Error extracting ${assemblyFilePath} into ${destinationDir}.` +
			`Error ${error.message},file ${error.fileName}, line ${error.lineNumber}`
		);

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
	})
}

