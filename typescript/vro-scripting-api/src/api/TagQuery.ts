namespace vroapi {
    /**
     * vRO TagQuery intrinsic class representation
     */
    export class TagQuery {

        /**
         * Add new has tag conditions to query.
         * @param tag
         * @param value
         */
        hasTag(tag: string, value: string): void {
            throw new NotSupportedError();
        }

        /**
         * Add new has global tag conditions to query.
         * @param tag
         * @param value
         */
        hasGlobalTag(tag: string, value: string): void {
            throw new NotSupportedError();
        }

    }

    global.TagQuery = TagQuery as any;
}
