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
