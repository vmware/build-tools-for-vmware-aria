namespace vroapi {
    /**
     * vRO EventSchedule intrinsic class representation
     */
    export class EventSchedule {

        source: any | undefined;

        when: number | undefined;

        lastExec: number | undefined;

        nextExecutionDate: any | undefined;

        nextExec: number | undefined;

        lastExecutionDate: any | undefined;

        /**
         * @param owner
         */
        consume(owner: string): void {
            throw new NotSupportedError();
        }

    }

    global.EventSchedule = EventSchedule;
}
