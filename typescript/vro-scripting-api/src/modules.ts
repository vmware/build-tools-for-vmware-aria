namespace vroapi {
    export const modules = {
        getModule,
        getAllModules,
    };

    type ModuleDescriptor = Record<string, string>;

    const fs: typeof import("fs-extra") = require("fs-extra");
    const MAPPING_FILE_NAME = "vro-modules.json";
    const cache: Record<string, any> = {};
    let _scriptMapping: Record<string, ModuleDescriptor>;

    function getScriptMapping(): Record<string, ModuleDescriptor> {
        return _scriptMapping || (_scriptMapping = fs.existsSync(MAPPING_FILE_NAME) ? parseJsonFile(MAPPING_FILE_NAME) : {});
    }

    function getModule(name: string): Module {
        return cache[name] || (cache[name] = loadModule(name));
    }

    function getAllModules(): Module[] {
        return Object.keys(getScriptMapping()).map(name => getModule(name));
    }

    function loadModule(name: string): Module {
        const actionMap = getScriptMapping()[name];
        if (!actionMap) {
            return null;
        }

        const actions: Action[] = [];
        const module = new Module();
        module.name = name;
        module.actions = actions;
        module.actionDescriptions = actions;

        Object.entries(actionMap).forEach(([name, path]) => {
            const fullPath = `${process.cwd()}/${path}`;
            module[name] = (...params: any[]) => {
                const actionFunc = require(fullPath) as Function;
                return actionFunc.apply(undefined, params);
            };
            const action = new Action();
            action.name = name;
            action.module = module;
            action.script = fs.readFileSync(fullPath).toString("utf-8");
            actions.push(action);
        });

        return module;
    }
}
