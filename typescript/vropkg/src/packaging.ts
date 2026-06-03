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
import archiver = require('archiver');
import * as unzipper from 'unzipper';
import * as winston from 'winston';
import { WINSTON_CONFIGURATION } from "./constants";

/**
 * Extended archiver interface with custom properties for tracking
 * output stream and path for proper cleanup
 */
interface ExtendedArchiver extends archiver.Archiver {
	__outputStream?: fs.WriteStream;
	__outputPath?: string;
}

/*
 * Utility class for variety of packaging operations.
 */

/**
 * Create an archive for a given path. The archive will be finalized once
 * the archive object finalize method gets called.
 * 
 * IMPORTANT: Callers must await the Promise returned by finalize() to ensure
 * file handles are properly closed and prevent EMFILE errors.
 *
 * @param outputPath archive given path
 * @returns {archiver.Archiver} archiver object with properly configured output stream
 */
export const archive = (outputPath: string): archiver.Archiver => {
	const output = fs.createWriteStream(outputPath);
	const instance: ExtendedArchiver = archiver.create('zip', { zlib: { level: 6 } });
	
	// Store the output stream reference for proper cleanup
	instance.__outputStream = output;
	instance.__outputPath = outputPath;
	
	// Attach error handlers to output stream
	// Don't throw in event handlers - let finalizeArchive() handle errors via Promise rejection
	output.on('error', (err) => {
		winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).error(`Error writing archive to ${outputPath}: ${err.message}`);
		// Error will be caught by finalizeArchive() error handler
	});
	
	// Attach event handlers to archiver
	instance.on('warning', (err) => {
		if (err.code !== 'ENOENT') {
			winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).warn(`Archive warning (non-ENOENT): ${err.message}`);
			// Warning will be handled by finalizeArchive() if it becomes an error
		} else {
			winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).warn(`Archive warning: ${err.message}`);
		}
	});
	
	instance.on('error', (err) => {
		winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).error(`Error creating archive: ${err.message}`);
		// Error will be caught by finalizeArchive() error handler
	});
	
	// Log when archive is finalized
	output.on('close', () => {
		winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Archived ${instance.pointer()} bytes to ${outputPath}`);
	});
	
	// Pipe archive to output stream
	instance.pipe(output);
	
	return instance;
}

/**
 * Properly finalize an archive by awaiting both the archive finalization
 * and the output stream closure. This prevents EMFILE errors by ensuring
 * file handles are released before continuing.
 * 
 * @param archive The archiver instance to finalize
 * @returns Promise that resolves when both archive and stream are complete
 */
export const finalizeArchive = async (archive: archiver.Archiver): Promise<void> => {
	return new Promise<void>((resolve, reject) => {
		const extendedArchiver = archive as ExtendedArchiver;
		const output = extendedArchiver.__outputStream;
		const outputPath = extendedArchiver.__outputPath;
		
		if (!output) {
			// Fallback: just call finalize if no output stream is stored
			archive.finalize().then(() => resolve()).catch(reject);
			return;
		}
		
		// Guard against multiple Promise settlements
		let settled = false;
		const settleOnce = (settler: () => void) => {
			if (!settled) {
				settled = true;
				settler();
			}
		};
		
		// Use once() to avoid accumulating listeners
		output.once('close', () => {
			settleOnce(() => resolve());
		});
		
		output.once('error', (err: Error) => {
			settleOnce(() => reject(new Error(`Error writing archive to ${outputPath}: ${err.message}`)));
		});
		
		// Also capture errors from the archiver itself
		archive.once('error', (err: Error) => {
			settleOnce(() => reject(new Error(`Archive error for ${outputPath}: ${err.message}`)));
		});
		
		// Start the finalization process and capture any immediate rejection
		archive.finalize().catch((err: Error) => {
			settleOnce(() => reject(new Error(`Failed to finalize archive ${outputPath}: ${err.message}`)));
		});
	});
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
		winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).info(
			`Error extracting ${assemblyFilePath} into ${destinationDir}.` +
			`Error ${error.message},file ${error.fileName}, line ${error.lineNumber}`
		);
	})
}
