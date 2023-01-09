namespace vroapi {
    /**
     * vRO LogQuery intrinsic class representation
     */
    export class LogQuery {

        fetchLimit: number | undefined;

        fromDate: any;

        toDate: any;

        originatorId: string;

        targetUri: string;

        severityThreshold: number | undefined;

    }

    global.LogQuery = LogQuery as any;
}
