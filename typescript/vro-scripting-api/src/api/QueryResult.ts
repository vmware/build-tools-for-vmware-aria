namespace vroapi {
    /**
     * vRO QueryResult intrinsic class representation
     */
    export class QueryResult {

        elements: any;

        totalCount: number | undefined;

        partial: boolean | undefined;

    }

    global.QueryResult = QueryResult as any;
}
