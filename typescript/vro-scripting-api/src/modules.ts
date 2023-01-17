/*
 * #%L
 * vro-scripting-api
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
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
