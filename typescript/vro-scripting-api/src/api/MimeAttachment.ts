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
     * vRO MimeAttachment intrinsic class representation
     */
    export class MimeAttachment {

        name: string;

        mimeType: string;

        content: string;

        /**
         * You can give a file or a file name as parameter when you instantiate a new MimeAttachment
         * Example:
         * 		var att = new MimeAttachment("script.txt") will create instance where
         * 	    att.name == "script.txt" and att.mimeType == "text/plain"
         * @param obj - file or file name, if is a string, this will be MimeAttachment file name
         */
        constructor(obj?: string | any) {
        }

        /**
         * Write the content of the mime attachment to file.
         * @param directory
         * @param filename
         */
        write(directory: any, filename: any): void {
            throw new NotSupportedError();
        }

    }

    global.MimeAttachment = MimeAttachment as any;
}
