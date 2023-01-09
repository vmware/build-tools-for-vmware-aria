namespace vroapi {
    /**
     * vRO FileWriter intrinsic class representation
     */
    export class FileWriter {

        lineEndType: number | undefined;

        exists: boolean | undefined;

        constructor(file: File) {
            throw new NotSupportedError();
        }

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

    global.FileWriter = FileWriter as any;
}
