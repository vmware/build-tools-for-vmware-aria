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
