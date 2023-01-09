namespace vroapi {
    /**
     * vRO Event intrinsic class representation
     */
    export class Event {

        source: any;

        when: number | undefined;

        /**
         * @param owner
         */
        consume(owner: string): void {
            throw new NotSupportedError();
        }

    }

    global.Event = Event as any;
}
