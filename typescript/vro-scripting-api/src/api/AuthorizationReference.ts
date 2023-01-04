namespace vroapi {
    /**
     * vRO AuthorizationReference intrinsic class representation
     */
    export class AuthorizationReference {

        relationName: string;

        returnType: string;

        type: string;

        value: any;

        authorizedObjects: any;

        stringRepresentation: string;

    }

    global.AuthorizationReference = AuthorizationReference;
}
