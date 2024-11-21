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
import * as pkg from "./package";
import * as util from "./util";
import * as constants from "./constants";
import { BuildCommandFlags } from "./types/build-command-flags";

type ModuleDescriptor = Record<string, string>;

interface ConfigCategory {
	elements: Record<string, ConfigElement>;
	children: Record<string, ConfigCategory>;
}

interface ConfigElement {
	version: string;
	path: string;
}

interface ResourceCategory {
	elements: Record<string, ResourceElement>;
	children: Record<string, ResourceCategory>;
}

interface ResourceElement {
	version: string;
	mimeType: string;
	path: string;
}

export default async function (flags: BuildCommandFlags) {
	const packages: pkg.VroPackage[] = [];
	const modules: Record<string, ModuleDescriptor> = {};
	const configurations: Record<string, ConfigCategory> = {};
	const resources: Record<string, ResourceCategory> = {};

	await createFolderStruct();
	await copyContent();
	await referenceSourceMaps();
	await addExportsToLocalScripts();
	await loadPackages();
	await buildMetadata();
	await saveMetadata();
	await configureTestFramework(flags);
    await createCoverageConfig();

	async function createFolderStruct(): Promise<void> {
		await Promise.all([
			fs.ensureDir(path.join(flags.output, constants.SOURCE_PATH)),
			fs.ensureDir(path.join(flags.output, constants.TEST_PATH)),
			fs.ensureDir(path.join(flags.output, constants.CONFIGS_PATH)),
			fs.ensureDir(path.join(flags.output, constants.RESOURCES_PATH)),
			fs.ensureDir(path.join(flags.output, constants.DEPENDENCIES_PATH)),
			fs.ensureDir(path.join(flags.output, constants.HELPERS_PATH)),
			fs.ensureDir(path.join(flags.output, constants.LOGS_PATH)),
		]);
	}

	async function copyContent(): Promise<void> {
		// Copy files produced during the build phase into the new test bed folder
		const copyOptions: fs.CopyOptions = { overwrite: true, recursive: true, errorOnExist: false };
		const jsFilesFilter: fs.CopyFilterAsync = async src => (await fs.lstat(src)).isDirectory() || (path.extname(src).toLowerCase() === ".js" && !src.toLowerCase().endsWith(".test.js"))

		const [
			actionsExist,
			testHelpersExists,
			testsExist,
			configExist,
			resourcesExist,
			helpersExist,
			tsSourceExist,
		] = await Promise.all([
			flags.actions ? fs.pathExists(flags.actions) : Promise.resolve(false),
			flags.testHelpers ? fs.pathExists(flags.testHelpers) : Promise.resolve(false),
			flags.tests ? fs.pathExists(flags.tests) : Promise.resolve(false),
			flags.configurations ? fs.pathExists(flags.configurations) : Promise.resolve(false),
			flags.resources ? fs.pathExists(flags.resources) : Promise.resolve(false),
			flags.helpers ? fs.pathExists(flags.helpers) : Promise.resolve(false),
			flags["ts-src"] ? fs.pathExists(flags["ts-src"]) : Promise.resolve(false),
		]);

		await Promise.all([
			actionsExist ? await fs.copy(flags.actions, path.join(flags.output, constants.SOURCE_PATH), { ...copyOptions, filter: jsFilesFilter }) : Promise.resolve(),
			testHelpersExists ? await fs.copy(flags.testHelpers, path.join(flags.output, constants.SOURCE_PATH), { ...copyOptions, filter: jsFilesFilter }) : Promise.resolve(), // Put it in the same as the actions
			configExist ? await fs.copy(flags.configurations, path.join(flags.output, constants.CONFIGS_PATH), copyOptions) : Promise.resolve(),
			resourcesExist ? await fs.copy(flags.resources, path.join(flags.output, constants.RESOURCES_PATH), copyOptions) : Promise.resolve(),
			helpersExist ? await fs.copy(flags.helpers, path.join(flags.output, constants.HELPERS_PATH), copyOptions) : Promise.resolve(),
		]);

		// Copy test files
		if (testsExist) {
			const testFilesToCopy: Record<string, string> = {};
			await util.forEachFile(flags.tests, async filePath => {
				const normalizedFileName = path.basename(filePath).toLowerCase();
				const testSuffix = constants.SOURCE_TEST_SUFFIXES.find(x => normalizedFileName.endsWith(x));
				if (testSuffix) {
					// Copy to target test folder
					const newFilePath = filePath.substring(0, filePath.length - testSuffix.length) + constants.TARGET_TEST_SUFFIX;
					testFilesToCopy[filePath] = path.join(flags.output, constants.TEST_PATH, path.relative(flags.tests, newFilePath));
				}
				else {
					// Copy to target actions folder
					testFilesToCopy[filePath] = path.join(flags.output, constants.SOURCE_PATH, path.relative(flags.tests, filePath));
				}
			});
			if (Object.keys(testFilesToCopy).length) {
				await Promise.all(Object.values(testFilesToCopy).map((dest => fs.ensureDir(path.dirname(dest)))));
				await Promise.all(Object.entries(testFilesToCopy).map(([src, dest]) => fs.copyFile(src, dest)));
			}
		}

		// Copy TypeScript code to support code coverage reports
		if (tsSourceExist) {
			const tsDestPath = path.join(flags.output, constants.SOURCE_PATH, ...(flags["ts-namespace"] || "").split("."));
			const tsFilesFilter: fs.CopyFilterAsync = async src => (await fs.lstat(src)).isDirectory() || (path.extname(src).toLowerCase() === ".ts" && !src.toLowerCase().endsWith(".test.ts"));
			const tsCopyOptions: fs.CopyOptions = { ...copyOptions, filter: tsFilesFilter };
			await fs.copy(flags["ts-src"], tsDestPath, tsCopyOptions);
		}
	}

	async function referenceSourceMaps(): Promise<void> {
		if (flags.maps && await fs.pathExists(flags.maps)) {
			await fs.copy(flags.maps, path.join(flags.output, constants.SOURCE_PATH), { overwrite: true, recursive: true });
			const sourceFiles: string[] = [];
			await util.forEachFile(path.join(flags.output, constants.SOURCE_PATH), async filePath => {
				if (path.extname(filePath).toLowerCase() === ".js") {
					sourceFiles.push(filePath);
				}
			});
			const sourceMapFileExist = await Promise.all(sourceFiles.map(filePath => `${filePath}.map`).map(fs.pathExists));
			await Promise.all(sourceFiles.filter((_, i) => sourceMapFileExist[i]).map(addSourceMapRef));
			async function addSourceMapRef(filePath: string): Promise<void> {
				await fs.appendFile(filePath, `//# sourceMappingURL=${path.basename(filePath)}.map`);
			}
		}
	}

	async function addExportsToLocalScripts(): Promise<void> {
		// Export local script functions, so it is possible to be loaded by module resolution.
		const contentPrefix = "module.exports = ";
		await util.forEachFile(path.join(flags.output, constants.SOURCE_PATH), async filePath => {
			if (path.extname(filePath).toLowerCase() === ".js") {
				let content = (await fs.readFile(filePath)).toString("utf-8");
				if (!content.startsWith(contentPrefix)) {
					content = contentPrefix + content;
					await fs.writeFile(filePath, content);
				}
			}
		});
	}

	async function loadPackages(): Promise<void> {
		if (!(flags.dependencies && await fs.pathExists(flags.dependencies))) {
			return;
		}
		const entries = await fs.readdir(flags.dependencies);
		const packageEntries = entries.filter(p => path.extname(p).toLowerCase() === ".package").map(p => path.join(flags.dependencies, p));
		const stats = await Promise.all(packageEntries.map(p => fs.lstat(p)));
		const filePaths = packageEntries.filter((_, i) => stats[i].isFile());
		packages.push(...await Promise.all(filePaths.map(filePath => pkg.VroPackage.load(filePath, path.join(flags.output, constants.DEPENDENCIES_PATH)))));
	}

	async function buildMetadata(): Promise<void> {
		await Promise.all([
			buildMetadataFromSourceAction(),
			buildMetadataFromSourceConfigs(),
			buildMetadataFromSourceResources(),
		]);
		await buildMetadataFromPackages();
	}

	async function buildMetadataFromSourceAction(): Promise<void> {
		const basePath = path.join(flags.output, constants.SOURCE_PATH);
		await util.forEachFile(basePath, async filePath => {
			if (path.extname(filePath).toLowerCase() === ".js") {
				const relativePath = path.relative(basePath, filePath);
				const module = path.dirname(relativePath).split(path.sep).join(".");
				const name = path.basename(relativePath, ".js");
				const modInfo = modules[module] || (modules[module] = {});
				modInfo[name] = path.join(constants.SOURCE_PATH, relativePath);
			}
		});
	}

	async function buildMetadataFromSourceConfigs(): Promise<void> {
		const basePath = path.join(flags.output, constants.CONFIGS_PATH);
		await util.forEachFile(basePath, async filePath => {
			const elementInfoFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".element_info.xml";
			if (path.extname(filePath).toLowerCase() === ".xml" && await fs.pathExists(elementInfoFilePath)) {
				const elementInfo = await pkg.parseElementInfo(elementInfoFilePath);
				const category = elementInfo["categoryPath"].split(".").reduce((parent, name) => {
					const children = parent.children || (parent.children = {});
					return children[name] || (parent.children[name] = {} as ConfigCategory)
				}, { children: configurations } as ConfigCategory);
				const elements = category.elements || (category.elements = {});

				// Save attributes as JSON
				const config = await pkg.parseConfigElement(filePath);
				const attsFilePath = path.join(path.dirname(filePath), `${config.name}.json`);
				await fs.writeFile(attsFilePath, JSON.stringify(config.attributes, null, 2));

				elements[config.name] = {
					version: config.version || "1.0.0",
					path: path.relative(flags.output, attsFilePath),
				};
			}
		});
	}

	async function buildMetadataFromSourceResources(): Promise<void> {
		const basePath = path.join(flags.output, constants.RESOURCES_PATH);
		await util.forEachFile(basePath, async filePath => {
			const elementInfoFilePath = filePath + ".element_info.xml";
			if (await fs.pathExists(elementInfoFilePath)) {
				const elementInfo = await pkg.parseElementInfo(elementInfoFilePath);
				const category = elementInfo["categoryPath"].split(".").reduce((parent, name) => {
					const children = parent.children || (parent.children = {});
					return children[name] || (parent.children[name] = {} as ResourceCategory)
				}, { children: resources } as ResourceCategory);
				const elements = category.elements || (category.elements = {});
				const name = elementInfo["name"];
				elements[name] = {
					version: elementInfo["version"] || "1.0.0",
					mimeType: elementInfo["mimetype"],
					path: path.relative(flags.output, filePath),
				};
			}
		});
	}

	async function buildMetadataFromPackages(): Promise<void> {
		const elementsByPackage = await Promise.all(packages.map(pkg => pkg.elements));
		const packageElementsTupples = packages.map((pkg, i) => <[pkg.VroPackage, pkg.VroElement[]]>[pkg, elementsByPackage[i]]);
		packageElementsTupples.forEach(([pkg, elems]) => elems.forEach(elem => {
			switch (elem?.type || "") {
				case "ScriptModule":
					{
						const { name, module } = elem as pkg.VroScriptElement;
						const modInfo = modules[module] || (modules[module] = {});
						modInfo[name] = path.join(constants.DEPENDENCIES_PATH, pkg.name, elem.path);
					}
					break;
				case "ConfigurationElement":
					{
						const configElem = elem as pkg.VroConfigElement;
						const category = configElem.category.reduce((parent, name) => {
							const children = parent.children || (parent.children = {});
							return children[name] || (parent.children[name] = {} as ConfigCategory)
						}, { children: configurations } as ConfigCategory);
						const elements = category.elements || (category.elements = {});
						elements[configElem.name] = {
							version: configElem.version,
							path: path.join(constants.DEPENDENCIES_PATH, pkg.name, configElem.configPath),
						};

					}
					break;
				case "ResourceElement":
					{
						const resourceElem = elem as pkg.VroResourceElement;
						const category = resourceElem.category.reduce((parent, name) => {
							const children = parent.children || (parent.children = {});
							return children[name] || (parent.children[name] = {} as ResourceCategory)
						}, { children: resources } as ResourceCategory);
						const elements = category.elements || (category.elements = {});
						elements[resourceElem.name] = {
							version: resourceElem.version,
							mimeType: resourceElem.mimetype,
							path: path.join(constants.DEPENDENCIES_PATH, pkg.name, resourceElem.resourcePath),
						};
					}
					break;
			}
		}));
	}

	async function saveMetadata(): Promise<void> {
		await Promise.all([
			fs.writeFile(path.join(flags.output, constants.METADATA_MODULES_FILE), JSON.stringify(modules, null, 2)),
			fs.writeFile(path.join(flags.output, constants.METADATA_CONFIGS_FILE), JSON.stringify(configurations, null, 2)),
			fs.writeFile(path.join(flags.output, constants.METADATA_RESOURCES_FILE), JSON.stringify(resources, null, 2))
		]);
	}

    async function configureTestFramework(flags: BuildCommandFlags) {
        const customTestsConfigPath = path.join(flags.projectRoot, constants.TEST_CONFIG_PATH);

        if (fs.pathExistsSync(customTestsConfigPath)) {
            // Use use-defined unit tests bootstrapping configuration ("unit-tests.config/*")
            await Promise.all(fs.readdirSync(customTestsConfigPath).map(fileName => {
                const sourceFilePath = path.join(customTestsConfigPath, fileName);
                const destFilePath = path.join(flags.output, fileName);
                return fs.copy(sourceFilePath, destFilePath, { overwrite: true, recursive: true, errorOnExist: false });
            }));
        } else {
            // Build configuration based on the framework selection
            const templatesPath = path.join(constants.TEST_CONFIG_TEMPLATES_PATH, flags.testFrameworkPackage || "jasmine");
            await Promise.all(fs.readdirSync(templatesPath).map(copyTarget => {
                const sourceFilePath = path.join(templatesPath, copyTarget);
                const destFilePath = path.join(flags.output, copyTarget);
                return fs.copy(sourceFilePath, destFilePath, { overwrite: true, recursive: true, errorOnExist: false });
            }));

            const packageJsonPath = path.join(flags.output, constants.NODE_PROJECT_CONFIG_FILE);
            const packageJsonContent = JSON.parse(fs.readFileSync(packageJsonPath).toString("utf8"));
            const devDeps = packageJsonContent.devDependencies;

            const projectDetails = util.extractProjectPomDetails(flags.projectRoot);
            packageJsonContent.name = projectDetails.name;
            packageJsonContent.version = projectDetails.version;

            if (flags.testFrameworkPackage === "jest") {
                devDeps.jest = flags.testFrameworkVersion || devDeps.jest;
                if (flags.runner === "swc") {
                    devDeps["@swc/core"] = "latest";
                    devDeps["@swc/jest"] = "latest";
                }

                const setupFiles = fs.readdirSync(flags.helpers).map(file => `./helpers/${file}`);
                const jestConfigPath = path.join(flags.output, constants.JEST_CONFIG_FILE);
                const jestConfigContent = fs.readFileSync(jestConfigPath).toString("utf8");
                fs.writeFileSync(jestConfigPath, jestConfigContent.replace("setupFiles: []", "setupFiles: " + JSON.stringify(setupFiles)));
            } else {
                devDeps["ansi-colors"] = flags.ansiColorsVersion || devDeps["ansi-colors"];
                devDeps["jasmine"] = flags.testFrameworkVersion || devDeps["jasmine"];
                devDeps["jasmine-reporters"] = flags.jasmineReportersVerion || devDeps["jasmine-reporters"];

                const runFilePath = path.join(flags.output, constants.JASMINE_RUN_FILE);
                const runFileContent = fs.readFileSync(runFilePath).toString("utf8");
                fs.writeFileSync(runFilePath, runFileContent.replace("{TEST_RESULTS_PATH}", constants.TEST_RESULTS_PATH));
            }

            fs.writeFileSync(packageJsonPath, JSON.stringify(packageJsonContent, null, 2));
        }
    }

	async function createCoverageConfig(): Promise<void> {
        const coverageFilePath = path.join(flags.output, constants.COVERAGE_CONFIG_FILE);
        // Do not overwrite the file copied from the custom configuration provided in the project root.
        if (fs.existsSync(coverageFilePath)) {
            return;
        }

        const thresholdsToken = flags["coverage-thresholds"];
		const perFile = flags["per-file"];
		const covConfig: Record<string, any> = {
			"all": true,
			"check-coverage": !!thresholdsToken,
			"include": [
				"src"
			],
			"exclude": [
				"**/*_helper.js",
				"**/*.helper.[tj]s",
			],
			"reporter": (flags["coverage-reports"] || "text").split(",").map(x => x.trim()),
			"report-dir": "coverage",
			"per-file": !!perFile,
			"branches": 0,
			"lines": 0,
			"functions": 0,
			"statements": 0,
			"watermarks": {
				"lines": [0, 0],
				"functions": [0, 0],
				"branches": [0, 0],
				"statements": [0, 0]
			}
		};

		if (thresholdsToken) {
			thresholdsToken.split(",").map(x => x.trim().split(":")).forEach(entries => {
				const errThreshold = parseInt(entries[0]);
				const warnThreshold = entries.length > 1 ? parseInt(entries[1]) : errThreshold;
				const type = entries.length > 2 ? entries[2] : "all";
				switch (type) {
					case "all":
						covConfig["lines"] = errThreshold;
						covConfig["functions"] = errThreshold;
						covConfig["branches"] = errThreshold;
						covConfig["statements"] = errThreshold;
						covConfig["watermarks"]["lines"] = [errThreshold, warnThreshold];
						covConfig["watermarks"]["functions"] = [errThreshold, warnThreshold];
						covConfig["watermarks"]["branches"] = [errThreshold, warnThreshold];
						covConfig["watermarks"]["statements"] = [errThreshold, warnThreshold];
						break;
					case "lines":
						covConfig["lines"] = errThreshold;
						covConfig["watermarks"]["lines"] = [errThreshold, warnThreshold];
						break;
					case "functions":
						covConfig["functions"] = errThreshold;
						covConfig["watermarks"]["functions"] = [errThreshold, warnThreshold];
						break;
					case "branches":
						covConfig["branches"] = errThreshold;
						covConfig["watermarks"]["branches"] = [errThreshold, warnThreshold];
						break;
					case "statements":
						covConfig["statements"] = errThreshold;
						covConfig["watermarks"]["statements"] = [errThreshold, warnThreshold];
						break;
				}
			});
		}

        await fs.writeFile(coverageFilePath, JSON.stringify(covConfig, null, 2));
	}

}
