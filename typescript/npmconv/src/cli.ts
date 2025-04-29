/*
 * #%L
 * npmconv
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

import cmdArgs from "command-line-args";

import { DependenciesMapper } from "./deps";
import * as npmconv from "./index";
import * as t from "./types";

interface CliInputs extends cmdArgs.CommandLineOptions {
	/** whether futher logging is in order */
	verbose: boolean;
	vv: boolean;
	/** whether to print out help string and exit */
	help: boolean;
	/**
	 * the maping of npm deps and vro plugin representation
	 * Format is:
	 * npmPackageName[@npmPacakgeVersion]/[mvnGroupId:]mvnArtifactId[:mvnVersion]
	 * if no npmPackageVersion - the one currently in used will be assummed
	 * if no mvnGroupId - the conventionGroupId input will be assummed
	 * if no mvnVersion - the derived npm version will be used
	 */
	artifactDepsMap?: string[];
	/**
	 * Explicity mvn dependencies to add to pom.xml with compile scope.
	 */
	compileMvnDeps?: string[];
	/**
	 * Explicity mvn dependencies to add to pom.xml.
	 */
	auxMvnDeps?: string[];
	/**
	 * The convention group id to fall back to if a grouop id is not explicitly provided/available otherwise.
	 */
	conventionGroupId?: string;
	/**
	 * The groupId to set for the final vRO package. Blank if fallback to conventionGroupId
	 */
	groupId?: string;

	artifactId?: string;
	version?: string;
	/**
	 *
	 */
	outDir: string;
	/**
	 * glob patterns for selecting source files from cloned package to end up in src/ folder of output project.
	 * This is realtive to the git cloned folder and not CWD.
	 */
	srcInclude: string[];

	/**
	 * glob patterns for selecting files from CWD to end up in same location under destination output project.
	 * format is "<glob_pattern>:<dest_dir>"
	 *    - glob_pattern is for example: sample/\**\/* referring to all files in sample
	 *    - dest_dir is the relative path under desttination vRO folder to place the files under. For example: src. If not provided will assume same base as in glob_pattern
	 */
	auxInclude: string[];
	/**
	 * tsconfig merging is performed on top of the artifact generated tsconfig.json by specifying which
	 * properties to explicitly set, retain (@), exclude (-) from both and anything else
	 * not specified is copied over. The default template is read from ./tsconfig.merge.json
	 */
	tsconfig: string;
	/**
	 * Optionally specify the version of the vRO toolchain to convert to. Defaults to latest RELEASE version
	 */
	toolchainVersion: string;
	/**
	 * mvn package name to convert
	 */
	package: string;
	/**
	 * do not rewrite unmapped npm dependencies
	 */
	skipUnmappedDeps: boolean;
}

const cliOpts = <cmdArgs.OptionDefinition[]>[
	{ name: "package", type: String, multiple: false, defaultOption: true },
	{ name: "verbose", type: Boolean, defaultValue: false },
	{ name: "vv", type: Boolean, defaultValue: false },
	{ name: "help", alias: "h", type: Boolean, defaultValue: false },
	{ name: "srcInclude", alias: "s", type: String, multiple: true, defaultValue: ["src/**/*"] },
	{ name: "auxInclude", type: String, multiple: true, defaultValue: [] },
	{ name: "outDir", alias: "o", type: String, defaultValue: "." },
	{ name: "groupId", alias: "g", type: String },
	{ name: "artifactId", alias: "a", type: String },
	{ name: "version", alias: "v", type: String },
	{ name: "conventionGroupId", alias: "c", type: String },
	{ name: "artifactDepsMap", alias: "d", type: String, multiple: true },
	{ name: "skipUnmappedDeps", type: Boolean, defaultValue: false },
	{ name: "auxMvnDeps", type: String, multiple: true, defaultValue: [] },
	{ name: "compileMvnDeps", type: String, multiple: true, defaultValue: [] },
	{ name: "tsconfig", alias: "t", type: String, defaultValue: "tsconfig.merge.json" },
	{ name: "toolchainVersion", type: String, defaultValue: "RELEASE" }
];

async function run(): Promise<void> {
	let input = cmdArgs(cliOpts, { stopAtFirstUnknown: true }) as CliInputs;

	if (!(input.verbose || input.vv)) {
		console.debug = () => { };
	}

	console.debug("Parsed Inputs:", input);

	let printHelp = false;

	if (input._unknown) {
		console.error("Unexpected option:", input._unknown);
		printHelp = true;
	}

	if (!input.groupId && !input.conventionGroupId) {
		console.error("Missing either groupId or conventionGroupId specification");
		printHelp = true;
	}

	if (input.help || printHelp) {
		printVersion();
		printUsage();
		return;
	}

	let depsMapper = new DependenciesMapper(input.artifactDepsMap, input.conventionGroupId);
	let packageSplit = input.package.split("@");

	const conv = new npmconv.NpmConverter({
		source: !fs.existsSync(input.package)
			? <t.NpmPackageSourceSpec>{
				type: t.SourceSpecType.Npm,
				directory: `target/${input.package}`,
				packageName: packageSplit[0],
				packageVersion: packageSplit[1]
			}
			: <t.LocalSourceSpec>{
				type: t.SourceSpecType.FileSystem,
				directory: input.package
			},
		include: {
			sourceGlobs: input.srcInclude,
			auxGlobs: input.auxInclude
		},
		output: {
			directory: input.outDir,
			toolchainVersion: input.toolchainVersion,
			mvnGroupId: input.groupId || input.conventionGroupId,
			mvnArtifactId: input.artifactId || packageSplit[0],
			mvnVersion: input.version,
			mvnDependencies: depsMapper
				.parseMvnRefs(input.compileMvnDeps, input.conventionGroupId, "RELEASE", "tgz", "compile")
				.concat(depsMapper.parseMvnRefs(input.auxMvnDeps, input.conventionGroupId, "RELEASE", "pom")),
			tsConfigMergeTemplate: await npmconv.NpmConverter.ensureJsonContent(input.tsconfig, "tsconfig.merge.json", {}),
			skipUnmappedNpmDeps: input.skipUnmappedDeps,
		}
	});

	await conv.resolveSource();
	const directMappedDeps = await conv.mapDirectDependencies(depsMapper);
	console.debug(`direct deps: ${directMappedDeps.length}`);
	await conv.generateVroProject(directMappedDeps);
	await conv.copyTsFiles(directMappedDeps);
	await conv.copyAuxFiles();
	await conv.mergeTsConfigFiles();

	console.info("Project converted.");
}

function printVersion(): void {
	let packageJsonPath = path.join(__dirname, "../package.json");
	if (fs.existsSync(packageJsonPath)) {
		let packageConfig = JSON.parse(fs.readFileSync(packageJsonPath).toString());
		console.log(`Version ${packageConfig.version}`);
	}
}

function printUsage(arg?: string): boolean {
	let usageFilePath = path.join(__dirname, "../Usage.txt");
	if (fs.existsSync(usageFilePath)) {
		console.log(fs.readFileSync(usageFilePath).toString());
	}

	return false;
}

run();
