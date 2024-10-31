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
import { writeFile } from "fs-extra";
import * as constants from "../constants";
import { BuildCommandFlags } from "../types/build-command-flags";
import { extractProjectPomDetails } from "./common";

export async function setUpJasmine(flags: BuildCommandFlags) {
    const files = {
        // Using this specific version to preserve backward compatibility
        "package.json": buildPackageJsonContent(
            flags.testFrameworkVersion || "^4.0.2",
            flags.projectRoot,
            flags.jasmineReportersVerion || "^2.5.2",
            flags.ansiColorsVersion || "^4.1.1",
        ),
        "run-jasmine.js": buildTestsRunnerScript(),
        "jasmine.config.json": buildJasmineConfigContent(),
    };
    const allFiles = [];
    for (let fileName in files) {
        const fileFullPath = flags.output + "/" + fileName;
        const promiseFile = writeFile(fileFullPath, files[fileName]);
        allFiles.push(promiseFile);
    }
    await Promise.all(allFiles);
}

function buildJasmineConfigContent() {
    return JSON.stringify({
        "spec_dir": "test",
        "spec_files": ["**/?(*.)+(spec|test).[jt]s?(x)"],
        "helpers": ["../helpers/*.js"],
        "failSpecWithNoExpectations": false,
        "stopSpecOnExpectationFailure": false,
        "stopOnSpecFailure": false,
        "random": false
    }, null, 2);
}

function buildPackageJsonContent(
    jasmineVersion: string,
    projectRoot: string,
    jasmineReportersVerion: string,
    ansiColorsVersion: string
) {
    const projectDetails = extractProjectPomDetails(projectRoot);
    return JSON.stringify({
        "name": projectDetails.name,
        "version": projectDetails.version,
        "description": "Unit tests runtime-compatible package.",
        "scripts": {
            "test": "node run-jasmine.js"
        },
        "devDependencies": {
            "ansi-colors": ansiColorsVersion,
            "jasmine": jasmineVersion,
            "jasmine-reporters": jasmineReportersVerion
        }
    }, null, 2);
}

function buildTestsRunnerScript() {
    return `const Jasmine = require("jasmine");
const ansiColors = require("ansi-colors");
const jasmineReporters = require("jasmine-reporters");
const joinPath = require("path").join;

class ConsoleReporter {
    indentationText = " ".repeat(2);
    totalSpecs = 0;
    failedSpecs = 0;
    indent = 0;

    suiteStarted(result, done) {
        this.log(result.description);
        this.indent++;
        done();
    }

    suiteDone(_, done) {
        this.indent--;
        done();
    }

    specStarted(_, done) {
        this.totalSpecs++;
        this.indent++;
        done();
    }

    specDone(result, done) {
        this.indent--;
        if (result.status === "passed") {
            this.log(ansiColors.green(result.description));
        }
        else {
            this.failedSpecs++;
            this.error(ansiColors.red(result.description));
            this.indent++;
            (result.failedExpectations || []).filter(e => !e.passed).forEach(e => {
                if (e.message) {
                    this.error(e.message);
                }
                if (e.stack) {
                    this.error(e.stack);
                }
            });
            this.indent--;
        }
        done();
    }

    jasmineDone(_, done) {
        this.log(\`\${this.totalSpecs} specs, \${this.failedSpecs} failures\`);
        done();
    }

    log(s) {
        s = s.split("\\n").map(line => \`\${this.indentationText.repeat(this.indent)}\${line}\`).join("\\n")
        console.log(s);
    }

    error(s) {
        s = s.split("\\n").map(line => \`\${this.indentationText.repeat(this.indent)}\${line}\`).join("\\n")
        console.error(s);
    }
}

(async () => {
    const jasmine = new Jasmine();
    jasmine.clearReporters();
    jasmine.loadConfigFile("./jasmine.config.json");

    jasmine.clearReporters();
    jasmine.addReporter(new ConsoleReporter());
    jasmine.addReporter(new jasmineReporters.JUnitXmlReporter({
        savePath: joinPath(process.cwd(), "${constants.TEST_RESULTS_PATH}"),
        consolidateAll: false,
        filePrefix: "TEST-"
    }));

    await jasmine.execute();
})();`;
}
