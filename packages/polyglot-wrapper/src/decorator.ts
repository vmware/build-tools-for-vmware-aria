import { Polyglot } from "./polyglot";

let existingRequiredParameters: any[] = [];

export function polyglot(arg1: { package: string, method: string }) {
    if (arg1.package && arg1.package == null || arg1.package == "")
        throw new Error(`Missing required argument: package on polyglot decorator definition`);

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

    if (arg1.package && arg1.method == null || arg1.method == "")
        throw new Error(`Missing required argument: method on polyglot decorator definition`);

    return (target: any, propertyName: string, descriptor: TypedPropertyDescriptor<Function>) => {
        let method = descriptor.value!;

        System.log("Applying decorator definition");
        System.log(`Name of the method: ${propertyName}`);

        descriptor.value = function () {
            let requiredParameters: any[] = existingRequiredParameters;

            System.log(`Checking parameters: ${JSON.stringify(requiredParameters)}`);
            const outParamIndex = indexOfOutParameter(requiredParameters, propertyName);
            const polyglotInParams = polyglotInputParameters(requiredParameters, propertyName, arguments);

            System.log(`Executing polyglot action: ${JSON.stringify(polyglotInParams)}`);
            const polyglotWrapper = new Polyglot<any>();
            const resultPolyglotAction = polyglotWrapper.passParams(arg1.package, arg1.method, polyglotInParams);
            System.log(`Assigned result to output parameter: ${resultPolyglotAction}`);

            arguments[outParamIndex] = resultPolyglotAction;
            System.log(`Assigned result to arguments: ${JSON.stringify(arguments[outParamIndex])}`);

            System.log("Calling original function after decoration process");
            return method.apply(this, arguments);
        };


        function indexOfOutParameter(array: any[], methodName: string): number {
            System.log("Searching output parameter index");
            let indexOf = -1;
            {
                for (let i = 0; i < array.length && indexOf == -1; i += 1) {
                    const item = array[i];

                    if (item && item['type'] && item['type'] == "Out" && item['propertyKey'] == methodName)
                        indexOf = item['index'];

                }
            }

            System.log(`Output parameter index: ${indexOf}`);

            return indexOf;
        };

        function compare(a, b) {
            if (a.index < b.index) {
                return -1;
            }
            if (a.index > b.index) {
                return 1;
            }
            return 0;
        }

        function polyglotInputParameters(array: any[], methodName: string, parameters: any): any[] {
            System.log("Searching inputs parameters");
            let result = [];
            {
                array = array.sort(compare)
                for (let i = 0; i < array.length; i += 1) {
                    const item = array[i];

                    if (item && item['type'] && item['type'] == "In" && item['propertyKey'] == methodName)
                        result.push(parameters[item.index]);

                }
            }

            System.log(`Input parameters are: ${JSON.stringify(result)}`);

            return result;
        };
    }
}


export function polyglotIn(target: Object, propertyKey: string | symbol, parameterIndex: number) {
    existingRequiredParameters.push({ type: "In", index: parameterIndex, target: target, propertyKey: propertyKey });
}

export function polyglotOut(target: Object, propertyKey: string | symbol, parameterIndex: number) {
    existingRequiredParameters.push({ type: "Out", index: parameterIndex, target: target, propertyKey: propertyKey });
}
