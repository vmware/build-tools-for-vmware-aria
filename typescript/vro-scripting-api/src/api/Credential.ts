namespace vroapi {
    /**
     * vRO Credential intrinsic class representation
     */
    export class Credential {

        username: string;

        password: string;

        /**
         * @param otherPassword
         */
        checkPassword(otherPassword: string): boolean {
            throw new NotSupportedError();
        }
    }

    global.Credential = Credential as any;
}
