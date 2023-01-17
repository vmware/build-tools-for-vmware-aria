namespace vroapi {
    /**
     * vRO PluginModuleDescription intrinsic class representation
     */
    export class PluginModuleDescription {

        name: string;

        description: string;

        version: string;

        types: any[] = [];

    }

    global.PluginModuleDescription = PluginModuleDescription as any;
}
