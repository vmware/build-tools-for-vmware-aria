namespace vroapi {
    /**
     * vRO VersionHistoryItem intrinsic class representation
     */
    export class VersionHistoryItem {

        comment: string;

        date: any = "";

        version: string;

        user: string;

    }

    global.VersionHistoryItem = VersionHistoryItem as any;
}
