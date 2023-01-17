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
     * vRO FileReader intrinsic class representation
     */
    export class FileReader {

        exists: boolean | undefined;

        constructor(file: File) {
            throw new NotSupportedError();
        }

        /**
         * Opens the file for reading. <p>Once open an index in the file is maintained. This means you can do succesive <b>readLine</b>.</p>
         */
        open(): void {
            throw new NotSupportedError();
        }

        /**
         * Reads one line from the opened file. <p>To go back to the start of the file, <b>close</b> it and re-<b>open</b> it.</p>
         */
        readLine(): string {
            throw new NotSupportedError();
        }

        /**
         * Reads all lines from the opened file.
         */
        readAll(): string {
            throw new NotSupportedError();
        }

    }

    global.FileReader = FileReader as any;
}
