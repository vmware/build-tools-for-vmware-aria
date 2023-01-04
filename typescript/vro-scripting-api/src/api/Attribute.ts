namespace vroapi {
    /**
     * vRO Attribute intrinsic class representation
     */
    export class Attribute {

        name: string;

        description: string;

        type: string;

        value: any;
    }

    global.Attribute = Attribute;
}
