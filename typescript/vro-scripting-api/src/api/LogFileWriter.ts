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
     * vRO LogFileWriter intrinsic class representation
     */
    export class LogFileWriter {

        lineEndType: number | undefined;

        exists: boolean | undefined;

        /**
         * Opens the file for writing. From this point on the file is locked. Use <b>close</b> to release it.
         */
        open(): void {
            throw new NotSupportedError();
        }

        /**
         * Reinitializes the length to 0 and sets the file-pointer in the very begining of the file.
         */
        clean(): void {
            throw new NotSupportedError();
        }

        /**
         * Writes a line to the file. The file must be opened first using <b>open</b>.
         * @param value
         */
        writeLine(value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Writes a string to the file. The file must be opened first using <b>open</b>.
         * @param value
         */
        write(value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Closes a previously opened file.
         */
        close(): void {
            throw new NotSupportedError();
        }

    }

    global.LogFileWriter = LogFileWriter as any;
}
