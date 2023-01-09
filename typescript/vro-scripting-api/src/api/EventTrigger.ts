namespace vroapi {
    /**
     * vRO EventTrigger intrinsic class representation
     */
    export class EventTrigger {

        source: Object | undefined;

        triggerName: string | undefined;

        valueOf: string | undefined;

        when: number | undefined;

        /**
         * @param {string} owner
         */
        consume(owner: string): void {
            throw new NotSupportedError();
        }

        /**
         * @param {string} key
         */
        getValue(key: string): string {
            throw new NotSupportedError();
        }

    }

    global.EventTrigger = EventTrigger;
}
