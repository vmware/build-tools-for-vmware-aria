namespace vroapi {
    /**
     * vRO PluginTypeDescription intrinsic class representation
     */
    export class PluginTypeDescription {

        name: string;

        kind: string;

        type: string;

        description: string;

    }

    global.PluginTypeDescription = PluginTypeDescription as any;
}
