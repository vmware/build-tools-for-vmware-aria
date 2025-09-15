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
import { FileUtil } from './util/FileUtil';
import { ResourceWrapper } from './util/ResourceWrapper';
import { Readable, Writable } from 'stream';
import { EventEmitter } from 'events';

/**
 * Interface extending archiver.Archiver with Promise-based finalize
 */
export interface ManagedArchiver extends archiver.Archiver {
    finalize(): Promise<void>;
    closeWithError(error: Error): this;
    directory(localPath: string, destPath: string | false): this;
    append(buffer: Buffer | Readable, opts?: archiver.EntryData): this;
    pipe<T extends NodeJS.WritableStream>(destination: T, options?: { end?: boolean }): T;
}

/**
 * Create a managed archive for a given path with proper stream handling
 *
 * @param outputPath archive given path
 * @returns {ManagedArchiver} managed archiver object
 */
export const archive = async (outputPath: string): Promise<ManagedArchiver> => {
    const fileUtil = FileUtil.getInstance();
    const output = await fileUtil.createManagedWriteStream(fs.createWriteStream(outputPath));
    
    // Create the archiver instance with error handling
    const instance = archiver.create('zip', { zlib: { level: 6 } }) as ManagedArchiver;
    
    // Add Promise-based finalize method
    const originalFinalize = instance.finalize.bind(instance);
    instance.finalize = () => {
        return new Promise<void>((resolve, reject) => {
            output.once('close', () => resolve());
            output.once('error', reject);
            instance.once('error', (err: Error) => {
                instance.closeWithError(err);
                reject(err);
            });
            instance.pipe(output);
            
            // Call the original finalize
            try {
                originalFinalize();
            } catch (err) {
                reject(err instanceof Error ? err : new Error(String(err)));
            }
        });
    };
    
    // Add method to handle cleanup on error
    instance.closeWithError = (error: Error) => {
        output.destroy(error);
        instance.destroy(error);
        return instance;
    };

    // Set up error handlers
    instance.on('warning', (err: archiver.ArchiverError) => {
        if (err.code === 'ENOENT') {
            getLogger().warn(err);
        } else {
            instance.closeWithError(err);
        }
    });

    return instance;
};

/**
 * Extract the content of a ZIP file into a selected folder
 *
 * @param assemblyFilePath relative or full file system path to the package file to be disassembled.
 * @param destinationDir folder under which the package content will be extracted
 */
export const extract = async (assemblyFilePath: string, destinationDir: string): Promise<void> => {
    const fileUtil = FileUtil.getInstance();
    let resourceWrapper: ResourceWrapper | undefined;
    
    try {
        const fileToExtract = await unzipper.Open.file(assemblyFilePath);
        
        // Create a wrapper that implements EventEmitter
        resourceWrapper = new ResourceWrapper(fileToExtract, () => {
            if (fileToExtract.files) {
                fileToExtract.files.length = 0;
            }
        });
        
        // Register the wrapped resource with FileUtil
        await fileUtil.createManagedResource(resourceWrapper);
        
        // Extract the files
        return fileToExtract.extract({ path: destinationDir })
            .then(() => {
                resourceWrapper?.close(); // Clean up on success
            })
            .catch(error => {
                const typedError = error as Error & { fileName?: string; lineNumber?: number };
                getLogger().info(
                    `Error extracting ${assemblyFilePath} into ${destinationDir}.` +
                    `Error ${typedError.message}` +
                    (typedError.fileName ? `, file ${typedError.fileName}` : '') +
                    (typedError.lineNumber ? `, line ${typedError.lineNumber}` : '')
                );
                resourceWrapper?.error(error); // Signal error through wrapper
                throw error; // Re-throw to ensure proper error propagation
            });
    } catch (error) {
        getLogger().error(
            `Failed to open or extract ${assemblyFilePath}: ${error instanceof Error ? error.message : String(error)}`
        );
        if (resourceWrapper) {
            resourceWrapper.error(error instanceof Error ? error : new Error(String(error)));
        }
        throw error;
    }
}
