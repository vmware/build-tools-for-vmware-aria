/*-
 * #%L
 * vrotest
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import { UnitTestFrameworkBootstrapper } from "./framework-bootstrapper";
import * as constants from "../constants";

export class JasmineBootstrapper implements UnitTestFrameworkBootstrapper {

    public getTestScript(): string {
        return "jasmine --config=./jasmine.json";
    }

    public getFrameworkPackageName(): string {
        return "jasmine";
    }

    public getFrameworkVersion(): string {
        // Using this specific value to preserve backward compatibility
        return "^4.0.2";
    }

    public getConfigFilePath(): string {
        return constants.JASMINE_CONFIG_FILE;
    }

    public getConfigFileContent(): Object {
        return {
			spec_dir: "test",
			spec_files: ["**/?(*.)+(spec|test).[jt]s?(x)"],
			helpers: ["../helpers/*.js"],
			failSpecWithNoExpectations: false,
			stopSpecOnExpectationFailure: false,
			stopOnSpecFailure: false,
			random: false,
		};
    }

}
