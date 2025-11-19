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
import * as childProc from "child_process";
import { readdirSync } from "fs";
import { basename, dirname, join, posix } from "path";
import * as util from "./util";

export interface RunCommandFlags {
    instrument?: boolean;
    _: string[];
}

export default async function (flags: RunCommandFlags): Promise<void> {
    const testDir = toPathArg(flags._[1] || process.cwd()).replace(/"+/gm, "");
    if (flags.instrument) {
        const nodeModulesDir = await findNodeModules(__dirname);
        if (!nodeModulesDir) {
            throw new Error(`Unable to find node_modules folder`);
        }
        const nycPath = toPathArg(nodeModulesDir, "nyc", "bin", "nyc.js");
        const nodeCmd = toPathArg(process.argv[0]);
        const vroTestCmd = toPathArg(process.argv[1]);
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
        await util.withWorkingDir(testDir, async () => {
            console.log("Installing node modules for the tests.");
            // Show install output so failures are visible to callers instead of being swallowed.
            childProc.execSync('npm install', { stdio: 'inherit' });
            console.log("Installing node modules for the tests completed.");

            console.log("Starting unit tests.");
            var child = childProc.spawnSync("npm", ["test"], { encoding: 'utf8', shell: true });
            if (child.status === 0) {
                console.log(child.stdout);
                console.log(child.stderr);
            } else {
                console.error("Error occurred in unit tests execution: " + child.stderr);
                // Exit with non-zero code so build fails on test failures
                process.exit(1);
            }
            console.log("Running unit tests completed.");
        });
    }
}

async function findNodeModules(dir: string): Promise<string> {
    const NODE_MODULES_NAME = "node_modules";
    while (dir) {
        if (basename(dir) === NODE_MODULES_NAME) {
            return dir;
        }
        if (readdirSync(dir).some(e => e === NODE_MODULES_NAME)) {
            return join(dir, NODE_MODULES_NAME);
        }
        dir = dirname(dir);
    }
    return dir;
}

function toPathArg(...args: string[]) {
    const res = args.length == 1 ? args[0] : join(...args).replace(/[\\/]+/, posix.sep);
    return !res ? '""' : (res.indexOf(" ") >= 0 && res.indexOf('"') < 0 ? `"${res}"` : res);
}
