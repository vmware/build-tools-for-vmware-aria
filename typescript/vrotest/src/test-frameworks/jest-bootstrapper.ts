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
import { readdirSync, writeFile } from "fs-extra";
import { BuildCommandFlags } from "../types/build-command-flags";
import { extractProjectPomDetails } from "./common";

export async function setUpJest(flags: BuildCommandFlags) {
    const files = {
        "package.json": buildPackageJsonContent(flags.testFrameworkVersion || "latest", flags.runner, flags.projectRoot),
        "jest.config.js": buildJestConfigScript(flags.helpers, flags.runner),
    };
    const allFiles = [];
    for (let fileName in files) {
        const fileFullPath = flags.output + "/" + fileName;
        const promiseFile = writeFile(fileFullPath, files[fileName]);
        allFiles.push(promiseFile);
    }
    await Promise.all(allFiles);
}

function buildPackageJsonContent(jasmineVersion: string, runner: string, projectRoot: string) {
    const projectDetails = extractProjectPomDetails(projectRoot);
    const content = {
        "name": projectDetails.name,
        "version": projectDetails.version,
        "description": "Unit tests runtime-compatible package.",
        "scripts": {
            "test": "jest --config=jest.config.js"
        },
        "devDependencies": {
            "jest": jasmineVersion,
        }
    };
    if (runner === "swc") {
        content.devDependencies["@swc/core"] = "latest";
        content.devDependencies["@swc/jest"] = "latest";
    }
    return JSON.stringify(content, null, 2);
}

function buildJestConfigScript(helpersPath: string, runner: string) {
    const config: any = {
        verbose: true,
        setupFiles: readdirSync(helpersPath).map(file => `./helpers/${file}`),
        bail: false,
        randomize: false,
        errorOnDeprecated: false,
    };
    if (runner === "swc") {
        config.transform = {
            '^.+\\\\.(t|j)sx?$': '@swc/jest'
        };
    }
    return `/** @type {import('jest').Config} */\nmodule.exports = ${JSON.stringify(config, null, 2)};\n`;
}
