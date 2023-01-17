/*
 * Command line interface entrypoint. This module servers the purpose of loading
 * and interracting with the packager from command line.
 */

import path from 'path';
import fs from 'fs-extra';
import cmdArgs from "command-line-args";
import createLogger from './lib/logger';
import util from 'util';
import { Packager } from './packager';

interface CliInputs extends cmdArgs.CommandLineOptions {
    /** verbose logging */
    verbose: boolean;
    x: boolean;
    /** print version */
    version: boolean,
    /** print out help string and exit */
    help: boolean;
    /** project directory containing package.json */
    projectDir: string,
    p: string,
    /** out directory containing intermediate transpiled files */
    outDir: string,
    o: string,
    /** distribution bundle name */
    bundleName: string,
    b: string,
    /** vro tree base location */
    vroTree: string,
    /** skip vro tree genration */
    skipVroTree: boolean,
    /** package for a specific environment */
    env: string,
    e: string,
}

const cliOpts = <cmdArgs.OptionDefinition[]>[
    { name: "verbose", alias: "x", type: Boolean, defaultValue: false },
    { name: "version", type: Boolean, defaultValue: false },
    { name: "help", alias: "h", type: Boolean, defaultValue: false },
    { name: "projectDir", alias: "p", type: String, defaultValue: path.resolve('.') },
    { name: "outDir", alias: "o", type: String, defaultValue: path.resolve('.', 'out') },
    { name: "bundleName", alias: "b", type: String, defaultValue: path.resolve('.', 'dist', 'bundle.zip') },
    { name: "vroTree", type: String, defaultValue: path.resolve('.', 'dist', 'vro') },
    { name: "skipVroTree", type: Boolean, defaultValue: false },
    { name: "env", alias: "e", type: String, defaultValue: null },
];

const input = cmdArgs(cliOpts, { stopAtFirstUnknown: true }) as CliInputs;
const logger = createLogger(input.verbose);

async function run(): Promise<void> {

    logger.debug(`Parsed Inputs: ${util.inspect(input)}`);

    if (input._unknown) {
        logger.error(`Unexpected option: ${input._unknown}`);
        printUsage();
        return;
    }

    if (input.help) {
        printVersion();
        printUsage();
        return;
    }

    if (input.version) {
        printVersion();
        return;
    }

    const packager = new Packager({
        bundle: input.bundleName,
        workspace: input.projectDir,
        out: input.outDir,

        vro: input.vroTree,         // vro tree dir
        skipVro: input.skipVroTree, // skip creating vro tree
        env: input.env,             // package for a specific environment
    });

    await packager.packageProject();

    logger.info('Package successfully created');
}

/**
 * Print the tool version
 */
function printVersion() {
    const packageJsonPath = path.join(__dirname, "../package.json");
    if (fs.existsSync(packageJsonPath)) {
        const packageConfig = fs.readJSONSync(packageJsonPath);
        logger.info(`Version ${packageConfig.version}`);
    }
}

/**
 * Print help
 */
function printUsage() {
    const usageFilePath = path.join(__dirname, "../Usage.txt");
    if (fs.existsSync(usageFilePath)) {
        const usageText = fs.readFileSync(usageFilePath).toString();
        logger.info(usageText);
    }
}

run();
