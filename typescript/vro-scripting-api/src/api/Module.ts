namespace vroapi {
    /**
     * vRO Module intrinsic class representation
     */
    export class Module {

        actions: Action[] = [];

        actionDescriptions: Action[] = [];

        name: string;

        description: string;

    }

    global.Module = Module as any;
}
