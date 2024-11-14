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
import * as path from "path";
import * as fs from "fs-extra";
import * as childProc from "child_process";
import * as util from "./util";

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
        const nycPath = '"' + path.join(nodeModulesDir, "nyc", "bin", "nyc.js") + '"';
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
        console.log("Installing node modules for the tests.");
        childProc.execSync('npm install', { stdio: 'ignore' });
        console.log("Installing node modules for the tests completed.");

        await util.withWorkingDir(testDir, async () => {
            console.log("Starting unit tests.");
            var child = childProc.spawnSync("npm", [ "test" ], { encoding : 'utf8' });
            if (child.status === 0) {
                console.log(child.stdout);
                console.log(child.stderr);
            } else {
                console.error("Error occurred in unit tests execution: " + child.stderr);
            }
            console.log("Running unit tests completed.");
        });
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
