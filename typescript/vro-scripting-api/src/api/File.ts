/*
 * #%L
 * vro-scripting-api
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
namespace vroapi {
    /**
     * vRO File intrinsic class representation
     */
    export class File {

        hostname: string;

        path: string;

        isLocal: boolean | undefined;

        exists: boolean | undefined;

        length: number | undefined;

        directory: string;

        readonly name: string;

        extension: string;

        isDir: boolean | undefined;

        constructor(file: string) {
            throw new NotSupportedError();
        }

        /**
         * Writes the content to the file. If the file does not exist, it is created.
         * @param content
         */
        write(content: string): void {
            throw new NotSupportedError();
        }

        /**
         * Renames the file denoted by this abstract pathname. Many aspects of the behavior of this method are inherently platform-dependent: The rename operation might not be able to move a file from one filesystem to another, it might not be atomic, and it might not succeed if a file with the destination abstract pathname already exists. The return value should always be checked to make sure that the rename operation was successful.
         * @param destPathName
         */
        renameTo(destPathName: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Creates the file if it does not exist. Also creates its directory if needed.
         */
        createFile(): void {
            throw new NotSupportedError();
        }

        /**
         * Creates the directory structure if it does not exist.
         */
        createDirectory(): void {
            throw new NotSupportedError();
        }

        /**
         * Deletes the file from the file system.
         */
        deleteFile(): void {
            throw new NotSupportedError();
        }

        /**
         * List files / directories that end with the specified extension.
         * @param extension
         */
        list(extension: any): string[] {
            throw new NotSupportedError();
        }

        /**
         * Can read this file
         */
        canRead(): boolean {
            throw new NotSupportedError();
        }

        /**
         * Can write this file
         */
        canWrite(): boolean {
            throw new NotSupportedError();
        }

    }

    global.File = File as any;
}
