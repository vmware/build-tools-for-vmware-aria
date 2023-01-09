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
	})
}

