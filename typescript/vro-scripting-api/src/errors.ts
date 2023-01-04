namespace vroapi {
    export class NotSupportedError extends Error {
        constructor(public message: string = "Not implemented!") {
            super(message);
        }
    }
}
