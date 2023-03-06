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
import { Wrapper } from "./wrapper";

export class Polyglot<T> {

    /**
     * This method can execute a Polyglot action that have until 10 parameters
     * @actionPackage string package where is the  polyglot action
     * @name string name of the method to execute
     * @example 
     * @code execute('com.vmware.example','pgaexample',4,'text',{c1:'x'},{c2:'r'})
     */
    public execute(actionPackage: string, actionName: string, ...args): T {
        if (args && args != null && args.length > 0) {
            System.log(`Executing polyglot action with ${args.length} parameters...`)
            System.log(`Arguments compose parameter: ${JSON.stringify(args)}`);

            return this.passParams(actionPackage, actionName, args);
        }
        else {
            System.log("Executing polyglot action with 0 parameters...")
            const wrapper = new Wrapper<T>();
            return wrapper.executeWithoutParameters(actionPackage, actionName);
        }
    }

    public passParams(packageN: string, name: string, args: any[]): T {

        const wrapper = new Wrapper<T>();

        switch (args.length) {
            case 1:
                return wrapper.execute1Param(packageN, name, args[0]);
            case 2:
                return wrapper.execute2Param(packageN, name, args[0], args[1]);
            case 3:
                return wrapper.execute3Param(packageN, name, args[0], args[1], args[2]);
            case 4:
                return wrapper.execute4Param(packageN, name, args[0], args[1], args[2], args[3]);
            case 5:
                return wrapper.execute5Param(packageN, name, args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return wrapper.execute6Param(packageN, name, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return wrapper.execute7Param(packageN, name, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return wrapper.execute8Param(packageN, name, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return wrapper.execute9Param(packageN, name, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return wrapper.execute10Param(packageN, name, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                {
                    System.log("Unsupported number of parameters");
                    throw new Error("Unsupported number of parameters");
                }
        }
    }
}
