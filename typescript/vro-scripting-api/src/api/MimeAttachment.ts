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
