/*-
 * #%L
 * vropkg
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
import * as fs from "fs-extra";
import * as path from "path";
import * as cmdArgs from "command-line-args";
import * as winston from "winston";
import * as t from "./types";
import { loadCertificate } from "./security";
import { parseTree } from "./parse/tree";
import { parseFlat } from "./parse/flat";
import { VroJsProjParser } from "./parse/js";
import { serializeTree } from "./serialize/tree";
import { serializeFlat } from "./serialize/flat";
import { VroJsProjRealizer } from "./serialize/js";
import { CleanDefinition } from "./cleaner/definitionCleaner";
import { WINSTON_CONFIGURATION } from "./constants";

winston.loggers.add(WINSTON_CONFIGURATION.logPrefix, <winston.LoggerOptions>{
	level: WINSTON_CONFIGURATION.logLevel,
	format: winston.format.json(),
	// defaultMeta: { service: 'user-service' },
	transports: [
		new winston.transports.File({ filename: WINSTON_CONFIGURATION.logFiles.error, level: 'error' }),
		new winston.transports.File({ filename: WINSTON_CONFIGURATION.logFiles.debug, level: 'debug' }),
		new winston.transports.File({ filename: WINSTON_CONFIGURATION.logFiles.default }),
		new winston.transports.Console({ format: winston.format.simple() })
	]
});

interface CliInputs extends cmdArgs.CommandLineOptions {
	/** whether futher logging is in order */
	verbose: boolean;
	vv: boolean;

	/** whether to print out help string and exit */
	help: boolean;

	/** vRealize Orchestrator native input type [vro_native_folder, vro_native_package] */
	in: string;

	/** vRealize Orchestrator native output type [vro_native_folder, vro_native_package] */
	out: string;

	/** vRealize Orchestrator native source path */
	srcPath: string;

	/** vRealize Orchestrator native destination path */
	destPath: string;

	/** vRealize Orchestrator private key PEM **/
	privateKey: string;

	/** vRealize Orchestrator certificates in PEM format. The certificate with the public key must be listed last **/
	certificates: string;

	/** POM Project version **/
	version: string;

	/** POM packaging type **/
	packaging: string;

	/** POM Project artifactId **/
	artifactId: string;

	/** POM Project description **/
	description: string;

	/** POM Project groupId **/
	groupId: string;
}

const cliOpts = <cmdArgs.OptionDefinition[]>[
	{ name: "verbose", type: Boolean, defaultValue: false },
	{ name: "vv", type: Boolean, defaultValue: false },
	{ name: "help", alias: "h", type: Boolean, defaultValue: false },
	{ name: "in", alias: "i", type: String },
	{ name: "out", alias: "o", type: String },
	{ name: "srcPath", alias: "s", type: String },
	{ name: "destPath", alias: "d", type: String },
	{ name: "privateKeyPEM", type: String },
	{ name: "certificatesPEM", type: String },
	{ name: "keyPass", type: String },
	{ name: "version", type: String },
	{ name: "packaging", type: String },
	{ name: "artifactId", type: String },
	{ name: "description", type: String },
	{ name: "groupId", type: String },
];

async function run() {
	let input = cmdArgs(cliOpts, { stopAtFirstUnknown: false }) as CliInputs;
	if (!(input.verbose || input.vv)) {
		console.debug = () => { };
	}

	// validate input data
	let printHelp = validate(input);
	if (input.help || printHelp) {
		printVersion();
		printUsage();
		return;
	}

	// Cleanup empty definitions prior parsing / serializing
	cleanup(input);

	// Parse data project data
	let pkgPromise = parse(input, t.ProjectType[input.in]);
	let pkg: t.VroPackageMetadata = await pkgPromise;

	// Certificate is only used to sign the package when serializing
	let certificateRequired = t.ProjectType[input.out] === t.ProjectType.flat;
	if (certificateRequired) {
		pkg.certificate = loadCertificate(
			<t.PEM>input.certificatesPEM, /* certificatesPEM is PEM of PEMs */
			<t.PEM>input.privateKeyPEM,
			input.keyPass);

	}
	// Serialize project data
	serialize(input, t.ProjectType[input.out], pkg);
}

function printVersion(): void {
	let packageJsonPath = path.join(__dirname, "../package.json");
	if (fs.existsSync(packageJsonPath)) {
		let packageConfig = JSON.parse(fs.readFileSync(packageJsonPath).toString());
		console.log(`Version ${packageConfig.version}`);
	}
}

function printUsage(): boolean {
	let usageFilePath = path.join(__dirname, "../Usage.txt");
	if (fs.existsSync(usageFilePath)) {
		console.log(fs.readFileSync(usageFilePath).toString());
	}

	return false;
}

function validate(input: CliInputs): boolean {
	let printHelp = false;
	if (input._unknown) {
		console.error("Unexpected option:", input._unknown);
		printHelp = true;
	}
	if (!input.srcPath) {
		console.error("Missing srcPath");
		printHelp = true;
	}
	if (!input.destPath) {
		console.error("Missing destPath");
	}
	if (!input.version) {
		console.error("Missing project version")
	}
	if (!input.artifactId) {
		console.error("Missing artifactId")
	}
	if (!input.groupId) {
		console.error("Missing groupId")
	}
	let certificateRequired = t.ProjectType[input.out] == t.ProjectType.flat;
	if (certificateRequired && (!input.certificatesPEM || !input.privateKeyPEM)) {
		console.error("Missing privateKeyPEM or certificatesPEM");
		printHelp = true;
	}
	if (t.ProjectType[input.in] == null || t.ProjectType[input.out] == null) {
		console.error("Incorrect in/out parameter");
		printHelp = true;
	}
	if (!input.keyPass) {
		console.warn("No password has been specified for the private key with the --keyPass parameter. Assuming empty password has been used.");
	}

	return printHelp;
}

function cleanup(input: CliInputs): void {
	winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Removing empty definitions generated in the compile process ...`);
	let cleaner = new CleanDefinition();
	cleaner.removeEmptyDefinitions(input.srcPath);
}

async function parse(input: CliInputs, projectType: t.ProjectType): Promise<t.VroPackageMetadata> {
	let pkgPromise: any = null;
	winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Parsing project type '${input.in}'`);
	switch (projectType) {
		case t.ProjectType.tree: {
			pkgPromise = parseTree(input.srcPath, input.groupId, input.artifactId, input.version, input.packaging, input.description);
			break;
		}
		case t.ProjectType.flat: {
			pkgPromise = parseFlat(input.srcPath, input.destPath);
			break;
		}
		case t.ProjectType.js: {
			pkgPromise = new VroJsProjParser().parse(input.srcPath, input.groupId, input.artifactId, input.version, input.packaging);
			break;
		}
		default: {
			throw new Error("Unsupported input: " + input.in);
		}
	}
	winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Parsing of project type '${input.in}' completed`);

	return pkgPromise;
}

async function serialize(input: CliInputs, projectType: t.ProjectType, pkg: t.VroPackageMetadata): Promise<void> {
	winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Serializing project type '${input.out}'`);
	switch (projectType) {
		case t.ProjectType.tree: {
			serializeTree(pkg, input.destPath);
			break;
		}
		case t.ProjectType.flat: {
			serializeFlat(pkg, input.destPath);
			break;
		}
		case t.ProjectType.js: {
			await new VroJsProjRealizer().realize(pkg, input.destPath);
			break;
		}
		default: {
			throw new Error("Unsupported output: " + input.out);
		}
	}
	winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Serializing of project type '${input.out}' completed`);
}

run();
