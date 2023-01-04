namespace vroapi {
    /**
     * vRO Command intrinsic class representation
     */
    export class Command {

        output: string;

        result: number;

        input: string;

        /**
         * Executes the command. <p>Standard output is redirected to the <b>output</b> attribute.</p>
         * @param wait
         */
        execute(wait?: any): number {
            throw new NotSupportedError();
        }

        /**
         * Executes the command and logs the standard output to a file.
         * @param filename
         */
        executeAndLog(filename: string): number {
            throw new NotSupportedError();
        }

    }

    global.Command = Command;
}
