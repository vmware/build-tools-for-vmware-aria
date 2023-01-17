/*
 * #%L
 * vrotest
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
import Jasmine from "jasmine";
import * as path from "path";
import * as fs from "fs-extra";
import * as childProc from "child_process";
import * as ansiColors from "ansi-colors";
import * as util from "./util";
import * as constants from "./constants";
import * as jasmineReporters from "jasmine-reporters"

export interface RunCommandFlags {
    instrument?: boolean;
    _: string[];
}

export default async function (flags: RunCommandFlags): Promise<void> {
    const testDir = flags._[1] || process.cwd();
    if (flags.instrument) {
        const nodeModulesDir = await findNodeModules(__dirname);
        if (!nodeModulesDir) {
            throw new Error(`Unable to find node_modules folder`);
        }
        const nycPath = '"'+path.join(nodeModulesDir, "nyc", "bin", "nyc.js")+'"';
        const nodeCmd = `"${process.argv[0]}"`;
        const vroTestCmd = `"${process.argv[1]}"`;
        const args = [nycPath, nodeCmd, vroTestCmd, "run"];
        const nycProc = childProc.spawn(nodeCmd, args, {
            cwd: testDir,
            env: process.env,
            shell: true,
            stdio: "inherit",
        });
        await new Promise<void>((resolve, _) => {
            nycProc.on("close", code => {
                if (code === 0) {
                    resolve();
                }
                else {
                    process.exit(code);
                }
            });
        });
    }
    else {
        await util.withWorkingDir(testDir, async () => await runTests());
    }
}

async function runTests(): Promise<void> {
    const jasmine = new Jasmine();
    jasmine.clearReporters();
    addJasmineReporters(jasmine);
    jasmine.loadConfigFile(constants.JASMINE_CONFIG_FILE);
    await jasmine.execute();
}

/**
 * @brief   Adds Jasmine reporters
 *
 * @details The ConsoleReporter is used to output the test result data in the console
 *          The JUnitXmlReporter is used to generate JUNIT reports ( Surefire reports )
 *
 * @param   {Jasmine} jasmine
 */
function addJasmineReporters( jasmine: Jasmine ): void {
    jasmine.addReporter(new ConsoleReporter());
    jasmine.addReporter(new jasmineReporters.JUnitXmlReporter({
        savePath: path.join( process.cwd(), constants.TEST_RESULTS_PATH ),
        consolidateAll: false,
        filePrefix: "TEST-"
    }));
}

class ConsoleReporter implements jasmine.CustomReporter {
    private readonly indentationText = " ".repeat(2);
    private totalSpecs = 0;
    private failedSpecs = 0;
    private indent = 0;

    suiteStarted(result: jasmine.SuiteResult, done?: () => void): void | Promise<void> {
        this.log(result.description);
        this.indent++;
        done();
    }

    suiteDone?(_: jasmine.SuiteResult, done?: () => void): void | Promise<void> {
        this.indent--;
        done();
    }

    specStarted(_: jasmine.SpecResult, done?: () => void): void | Promise<void> {
        this.totalSpecs++;
        this.indent++;
        done();
    }

    specDone(result: jasmine.SpecResult, done?: () => void): void | Promise<void> {
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

    jasmineDone(_: jasmine.JasmineDoneInfo, done?: () => void): void | Promise<void> {
        this.log(`${this.totalSpecs} specs, ${this.failedSpecs} failures`);
        done();
    }

    private log(s: string) {
        s = s.split("\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\n")
        console.log(s);
    }

    private error(s: string) {
        s = s.split("\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\n")
        console.error(s);
    }
}

async function findNodeModules(dir: string): Promise<string> {
    const NODE_MODULES_NAME = "node_modules";
    while (dir) {
        if (path.basename(dir) === NODE_MODULES_NAME) {
            return dir;
        }
        if ((await fs.readdir(dir)).some(e => e === NODE_MODULES_NAME)) {
            return path.join(dir, NODE_MODULES_NAME);
        }
        dir = path.dirname(dir);
    }
    return dir;
}
