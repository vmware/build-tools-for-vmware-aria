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
import * as glob from "glob";

import * as path from "path";

import * as fs from "fs-extra";

import * as cp from "child_process";

import * as dwonloadGit from "download-git-repo";

import { NpmProxy, DependenciesMapper } from "./deps";
import ImportsRewriter from "./rewriter";
import tscfgmerge from "./tscfgmerge";
import * as t from "./types";

export class NpmConverter {
	private packRep: {
		name: string;
		version: string;
		repository: any;
		dependencies: { [name: string]: string };
	};

	private hasDirectDependencies: boolean;

	private npm: NpmProxy;

	constructor(public readonly opts: t.NpmConverterConfig) { }

	private async getNpmHandle() {
		if (!this.npm) {
			this.npm = new NpmProxy();
			await this.npm.init();
			console.debug("Npm Config initialized.");
		}

		return this.npm;
	}

	private getDestDir() {
		return path.resolve(this.opts.output.directory, this.opts.output.mvnArtifactId || this.packRep.name);
	}

	async resolveSource(): Promise<void> {
		let npm = await this.getNpmHandle();

		if (this.opts.source.type === t.SourceSpecType.Npm) {

			console.log("Fetching remote package info...");

			this.packRep = await npm.view(this.opts.source.packageName + (this.opts.source.packageVersion ? `@${this.opts.source.packageVersion}` : ""));
			console.debug(`package representation: ${JSON.stringify(this.packRep)}`);
			console.debug(`repository: ${JSON.stringify(this.packRep.repository)}`);
			console.debug(`dependencies: ${JSON.stringify(this.packRep.dependencies)}`);

			const gitRepo = sanitizeRepo(this.packRep.repository);
			await fs.ensureDir(this.opts.source.directory);
			await ensureGone(this.opts.source.directory);

			console.log(`Cloning source repo from: ${gitRepo} to ${this.opts.source.directory}`);
			await new Promise((resolve, reject) => {
				dwonloadGit(`direct:${gitRepo}`, this.opts.source.directory, { clone: true }, err => {
					err ? reject(err) : resolve(undefined);
				});
			});
			console.debug("Cloning done.");

			this.hasDirectDependencies = this.packRep.dependencies && Object.keys(this.packRep.dependencies).length > 0;
		} else {
			console.log("Fetching local package info...")
			this.packRep = await npm.viewLocal(this.opts.source.directory);
			this.hasDirectDependencies = this.packRep.dependencies && Object.keys(this.packRep.dependencies).length > 0;
		}
	}

	async mapDirectDependencies(depsMapper: DependenciesMapper): Promise<t.NpmMvnPackagePair[]> {
		let directMappedDeps: t.NpmMvnPackagePair[] = [];

		if (this.hasDirectDependencies) {

			console.debug("Package has dependencies which need to be resolved to exact versions...");

			console.log(`Gathering package dependencies using source directory "${this.opts.source.directory}" ...`);
			const deps = Object.entries(this.packRep.dependencies).map(([dep]) => dep);
			console.debug(`Resolved npm dependencies to: ${JSON.stringify(deps)}`);

			console.log("Mapping npm dependencies to mvn artifacts...");

			// filter out full dependency list by direct dependencies only.
			let { dependencies: directDeps }: any = await fs.readJson(path.resolve(this.opts.source.directory, "package.json"));

			let installDeps = depsMapper
				.parseNpmRefs(deps)
				// leave only direct deps
				.filter(directDep => directDeps[directDep.name]);

			// Map NPM dependencies to their Maven counterparts
			let { mapped: mappedDeps, unmapped: unmappedDeps } = depsMapper.mapPackages(installDeps);
			directMappedDeps = mappedDeps;

			if (!this.opts.output.skipUnmappedNpmDeps) {

				// Try to resolve unmapped dependencies
				if (unmappedDeps.length > 0 && depsMapper.defaultMvnGroupId) {
					console.debug(`Following dependencies were not mapped: ${JSON.stringify(unmappedDeps)}`);
					console.debug(`Will try to guess them based on provided conventionGroupId.`);
					directMappedDeps = mappedDeps.concat(depsMapper.guessMapNpmPack(unmappedDeps));
					unmappedDeps = [];
				}

				console.debug(`Mapping the following dependences: ${JSON.stringify(directMappedDeps)}`);
				if (unmappedDeps.length > 0) {
					console.debug(`Unable to map these dependencies: ${JSON.stringify(unmappedDeps)}`);
					throw new Error("Cannot resolve all dependencies will not continue further."); // maybe make this optional?
				}
			}
		}

		return directMappedDeps;
	}

	async generateVroProject(directMappedDeps: t.NpmMvnPackagePair[] = []): Promise<void> {
		console.log("Generating project folder...");

		if (this.opts.output.directory !== ".") {
			(await ensureGone(this.opts.output.directory)) && console.debug(`Output directory already exist ${this.opts.output.directory}. Removing it first...`);
		}

		const command =
			`mvn archetype:generate -DinteractiveMode=false ` +
			`-DarchetypeGroupId=com.vmware.pscoe.o11n.archetypes ` +
			`-DarchetypeArtifactId=package-typescript-archetype ` +
			`-DarchetypeVersion=${this.opts.output.toolchainVersion} ` +
			`-DgroupId=${this.opts.output.mvnGroupId} ` +
			`-DartifactId=${this.opts.output.mvnArtifactId} ` +
			`-Dversion=${this.opts.output.mvnVersion || this.packRep.version} `;

		console.debug(`Generating artifact through: ${command}`);

		fs.ensureDir(this.opts.output.directory);
		const cmdResult = await exec(command, { cwd: this.opts.output.directory });

		console.debug(`Command completed with code: ${cmdResult.code}`);
		cmdResult.message && console.debug(cmdResult.message);
		cmdResult.stdout && console.debug(cmdResult.stdout);
		cmdResult.stderr && console.debug(cmdResult.stderr);

		const pomXmlPath = path.resolve(this.getDestDir(), "pom.xml");

		if (directMappedDeps.length > 0) {
			console.log("Adding mapped maven dependencies to pom.xml...");

			const directDeps = directMappedDeps.map(mapping => mapping.mvn);
			console.debug(`Expanded direct dependencies to: ${JSON.stringify(directDeps)}`);

			updatePomDependencies(pomXmlPath, directDeps);
		} else {
			console.debug("No direct mapped depenencies provided...");
		}

		if (this.opts.output.mvnDependencies && this.opts.output.mvnDependencies.length > 0) {
			console.log("Adding additionally supplied dependencies to pom.xml...");
			updatePomDependencies(pomXmlPath, this.opts.output.mvnDependencies);
		} else {
			console.debug("No additional depenencies provided...");
		}
	}

	async copyTsFiles(dependencyMap) {
		console.log("Copying source TS files...");
		console.debug(`Source patterns: ${this.opts.include.sourceGlobs}`);

		const srcFiles = (this.opts.include.sourceGlobs || []).map<{ base: string; files: string[], destDir: string | null }>(pattern => {
			const { globPattern, destDir } = this.splitGlobMap(pattern);

			let files = glob.sync(globPattern, { cwd: this.opts.source.directory, nodir: true });

			let base = path.dirname(globPattern);
			while (base != "." && !fs.existsSync(path.resolve(this.opts.source.directory, base))) {
				base = path.dirname(base);
			}

			return {
				destDir,
				base,
				files: files.map(file => path.relative(base, file))
			};
		}, []);

		const destSrcDir = path.resolve(this.getDestDir(), "src");
		(await ensureGone(destSrcDir)) && console.debug(`${destSrcDir} directory already exist ${this.getDestDir()}. Removing it first...`);

		console.debug(`Copying source files: ${srcFiles.length ? JSON.stringify(srcFiles) : "NONE"}`);

		// resolve relative glob files found to destination ones...
		await Promise.all(
			srcFiles.reduce(
				(promises, { base, files, destDir }) => {
					const sourceFilesDestDir = destDir ? path.join(destSrcDir, destDir) : destSrcDir
					console.debug(`Copying files to: ${sourceFilesDestDir}`);
					console.debug(`Trimming directory ${base} from source paths...`)

					return promises.concat(
						files.map(file => {
							return fs.ensureDir(path.resolve(sourceFilesDestDir, path.dirname(file))).then(() => {
								const srcPath = path.resolve(this.opts.source.directory, base, file);
								let sourceCode = fs.readFileSync(srcPath, { encoding: "UTF-8" });
								console.log(`Processing file: ${srcPath}`)
								console.log(`Dependency map: ${JSON.stringify(dependencyMap)}`)

								const rewriter = new ImportsRewriter(
									sourceCode,
									dependencyMap.map(({ npm, mvn }) => {
										return {
											moduleName: npm.name,
											moduleRewrite: mvn.groupId
												.split(".")
												.concat(mvn.artifactId)
												.join(".")
										};
									})
								);
								const newSource = rewriter.rewrite();

								return fs.writeFile(path.resolve(sourceFilesDestDir, file), newSource, { encoding: "UTF-8" });
							});
						})
					);
				},
				<Promise<void>[]>[]
			)
		);

		console.debug("TS Files Copy completed.");
	}

	async copyAuxFiles() {
		console.debug(`Auxiliary patterns: ${this.opts.include.auxGlobs}`);
		const auxFilesSplits = (this.opts.include.auxGlobs || []).map(this.splitGlobMap);

		const auxFiles: { srcFiles: string[]; destDir: string }[] = auxFilesSplits.reduce((res, fileSplit) => {
			return res.concat({
				srcFiles: glob.sync(fileSplit.globPattern, { nodir: true }),
				destDir: fileSplit.destDir
			});
		}, []);

		console.debug(`Copying auxiliary files: ${auxFiles.length ? JSON.stringify(auxFiles) : "NONE"}`);
		console.log(`Copying files to: ${this.getDestDir()}`);

		await Promise.all(
			auxFiles.map(auxFile =>
				Promise.all(
					auxFile.srcFiles.map(srcFile => {
						const destDirSpec = path.resolve(this.getDestDir(), auxFile.destDir || path.dirname(srcFile));

						return fs.ensureDir(destDirSpec).then(() => {
							return fs.copyFile(srcFile, path.resolve(destDirSpec, path.basename(srcFile)));
						});
					})
				)
			)
		);
		console.debug("Auxiliary Files Copy completed.");
	}

	splitGlobMap(glob: string) {
		if (glob.includes(':')) {
			const split = glob.split(":");
			return {
				globPattern: split[0],
				destDir: split[1]
			};
		}
		return {
			globPattern: glob,
			destDir: null,
		}
	}

	async mergeTsConfigFiles() {
		console.info("Merging package tsconfig.json over archetype tsconfig.json...");
		const destTsconfigPath = path.resolve(this.getDestDir(), "tsconfig.json");
		const archetypeTsconfig: Object = await NpmConverter.ensureJsonContent(destTsconfigPath, undefined, {});
		const packageTsconfig: Object = await NpmConverter.ensureJsonContent(path.resolve(this.opts.source.directory, "tsconfig.json"), undefined, {});

		const mergeResult: any = tscfgmerge(archetypeTsconfig, packageTsconfig, this.opts.output.tsConfigMergeTemplate);

		console.info("Adapting tsconfig from archetype...");
		await fs.writeFile(destTsconfigPath, JSON.stringify(mergeResult, null, 4));
	}

	public static async ensureJsonContent(inputPath: string, defaulPath?: string, defaultContent?: any): Promise<any> {
		let resolvedPath = defaulPath;
		if (inputPath && (await fs.pathExists(inputPath))) {
			resolvedPath = inputPath;
		} else if (defaulPath && !(await fs.pathExists(defaulPath)) && !path.isAbsolute(defaulPath)) {
			resolvedPath = path.resolve(__dirname, defaulPath);
		}

		if (resolvedPath && (await fs.pathExists(resolvedPath))) {
			console.debug(`Loading JSON from ${resolvedPath}`);
			return fs.readJson(resolvedPath);
		} else {
			console.debug(`Loading default JSON for input:${inputPath} default:${defaulPath}`);
			return defaultContent;
		}
	}
}

function sanitizeRepo(repo: any): string {
	let result = repo;
	if (typeof repo === "object") {
		if (repo.type !== "git") {
			throw new Error(`Repository type (${repo.type}) is not supported.`);
		}
		result = repo.url;
	}

	if (typeof result === "string") {
		const repoLc = result.toLowerCase();
		const httpSign = repoLc.indexOf("http");
		const plusSign = repoLc.indexOf("+");
		if (httpSign > 0 || plusSign < httpSign) {
			result = result.substr(httpSign);
		}
	}

	// figure how to handle authenticated/ssh urls here?
	return result;
}

async function ensureGone(dirPath: string): Promise<boolean> {
	if (await fs.pathExists(dirPath)) {
		await fs.remove(dirPath);
		return true;
	}
	return false;
}

async function exec(command: string, options: cp.ExecOptions): Promise<CmdResult> {
	console.debug(`Executing command "${command}" with options`, options);
	return new Promise<CmdResult>((resolve, reject) => {
		cp.exec(command, options, (err: cp.ExecException, stdout, stderr) => {
			console.debug(stdout);
			if (err) {
				const message = `Command '${err.cmd}' exited with code ${err.code}`;
				console.error(message, stderr);
				reject({
					code: err.code,
					message,
					stdout,
					stderr
				});
			}

			resolve({ code: 0, stdout, stderr });
		});
	});
}

function updatePomDependencies(pomPath: string, depsSpec: t.MvnPackageRef[]): void {
	const pomContent = fs.readFileSync(pomPath, { encoding: "UTF-8" });
	const endTagPos = pomContent.indexOf("</dependencies>");

	fs.writeFileSync(pomPath, pomContent.substr(0, endTagPos), { encoding: "UTF-8" });

	depsSpec.forEach(dep => {
		fs.appendFileSync(
			pomPath,
			`  <dependency>
      <groupId>${dep.groupId}</groupId>
      <artifactId>${dep.artifactId}</artifactId>
      <type>${dep.packaging}</type>
      <version>${dep.version}</version>${dep.scope ? "\n      <scope>" + dep.scope + "</scope>" : ""}
	  </dependency>
  `,
			{ encoding: "UTF-8" }
		);
	});
	fs.appendFileSync(pomPath, pomContent.substr(endTagPos), { encoding: "UTF-8" });
}

interface CmdResult {
	message?: string;
	code: number;
	stdout?: string;
	stderr?: string;
}
