/*
 * #%L
 * o11n-typescript-package-maven-plugin
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
@Workflow({
    id: "c9be5b70-a650-43d0-a8a3-e39f731b055b",
    version: "1.0.0",
})
export class TestWorkflow2 {
    public install(foo: string, bar: string, @Out result: any): void {
        System.log(`foo=${foo}, bar=${bar}`);
        result = "test result2";
    }
}