
export class Wrapper<T>{


    /**
     * This method execute a Polyglot action with zero parameter
     * @actionPackage string package where is the  polyglot action
     * @name string name of the method to execute
     */
    executeWithoutParameters(packageName: string, actionName: string): T {
        System.log(`Executing polyglot action on package: ${packageName}`);

/*-
 * #%L
 * polyglot-wrapper
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
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);

        //Call to the polyglot action
        let result = module[actionName]();
        return result as T;

    }


    /**
    * This method execute a Polyglot action with one parameter
    * @actionPackage string package where is the  polyglot action
    * @name string name of the method to execute
    */
    execute1Param(packageName: string, actionName: string, arg2: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);

        //Call to the polyglot action
        let result = module[actionName](arg2);
        return result as T;

    }

    execute2Param(packageName: string, actionName: string, arg2: any, arg3: any): T {

        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);

        //Call to the polyglot action
        let result = module[actionName](arg2, arg3);
        return result as T;

    }

    execute3Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }

    execute4Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }

    execute5Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }

    execute6Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any, arg7: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6, arg7);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }


    execute7Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any, arg7: any, arg8: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6, arg7, arg8);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }


    execute8Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any, arg7: any, arg8: any, arg9: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }

    execute9Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any, arg7: any, arg8: any, arg9: any, arg10: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }

    execute10Param(packageName: string, actionName: string, arg2: any, arg3: any, arg4: any, arg5: any, arg6: any, arg7: any, arg8: any, arg9: any, arg10: any, arg11: any): T {
        System.log(`Executing polyglot action on package: ${packageName}`);
        System.log(`Executing action : ${actionName}`);

        let module = System.getModule(packageName);
        let result = module[actionName](arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);

        System.log(`Casting result of polyglot action...`)
        return result as T;
    }
}
