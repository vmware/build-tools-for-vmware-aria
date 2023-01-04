namespace vroapi {
    /**
     * vRO Parameter intrinsic class representation
     */
    export class Parameter {

        name: string;

        description: string;

        type: string;

    }

    global.Parameter = Parameter as any;
}
