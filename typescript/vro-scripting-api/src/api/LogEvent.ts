namespace vroapi {
    /**
     * vRO LogEvent intrinsic class representation
     */
    export class LogEvent {

        severity: number | undefined;

        logTimeStamp: any = "";

        shortDescription: string;

        longDescription: string;

        originatorUri: string;

        originatorUserName: string;

        originatorId: string;

        id: string;

    }

    global.LogEvent = LogEvent as any;
}
