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
     * vRO ZipWriter intrinsic class representation
     */
    export class ZipWriter {

        /**
         * Add a string element to the specified zip file
         * @param entryName
         * @param content
         * @param encoding
         */
        addContent(entryName: any, content: any, encoding: any): void {
            throw new NotSupportedError();
        }

        /**
         * Write the element to the Zip File
         */
        writeZip(): void {
            throw new NotSupportedError();
        }

        /**
         * Add mime attachement to the specified zip file
         * @param mimeAttachment
         */
        addMimeAttachment(mimeAttachment: any): void {
            throw new NotSupportedError();
        }

        /**
         * Reinitializes the length to 0 and sets the file-pointer in the very begining of the file.
         */
        clean(): void {
            throw new NotSupportedError();
        }

    }

    global.ZipWriter = ZipWriter as any;
}
