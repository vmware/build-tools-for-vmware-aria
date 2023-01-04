namespace vroapi {
    /**
     * Represents an action in the system
     */
    export class Action {

        name: string;

        /**
         * Returns the description of the Action.
         */
        description: string;

        version?: string;

        returnType?: string;

        script: string;

        versionHistoryItems?: VersionHistoryItem[] = [];

        parameters: Parameter[] = [];

        module: Module = new Module();
    }

    global.Action = Action;
}
