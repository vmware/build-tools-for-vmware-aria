namespace vroapi {
    /**
     * vRO EventGauge intrinsic class representation
     */
    export class EventGauge {

        source: any | undefined;

        when: number | undefined;

        perfKey: string | undefined;

        value: number | undefined;

        device: string | undefined;

        /**
         * @param owner
         */
        consume(owner: string): void {
            throw new NotSupportedError();
        }

    }

    global.EventGauge = EventGauge;
}
