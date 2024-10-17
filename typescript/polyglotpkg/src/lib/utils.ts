/*-
 * #%L
 * polyglotpkg
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

import globby from 'globby';
import fs from 'fs-extra';
import which from 'which';
import path from 'path';
import { spawn } from 'child_process';
import {
	ActionType,
	ActionRuntime,
	AbxActionDefinition,
	VroActionDefinition,
	PackageDefinition,
	PlatformDefinition,
	ProjectActions,
	ActionOptions,
	PackagerOptions,
	MappedAbxRuntimes,
} from './model';
import createLogger from './logger';

const logger = createLogger();

/**
 * Determine the action runtime based on the action manifest or
 * the action handler if runtime is not specified.
 *
 * If the action type is ABX, the runtime is determined based on the given runtime. Runtimes of the same type are mapped.
 *
 * @TODO: This needs to be deprecated in favor of the runtime being correctly specific in the manifest. That however is a breaking change.
 *          new Issue created for this to be discussed: https://github.com/vmware/build-tools-for-vmware-aria/issues/391
 *
 * @param pkg
 */
export function determineRuntime(pkg: PlatformDefinition, actionType?: ActionType): ActionRuntime {
	const isAbx = actionType === ActionType.ABX || !pkg.vro;
	const runtime = pkg.platform.runtime;

	// @TODO: This needs to be deprecated in favor of the runtime being correctly specific in the manifest. Do so during the next major release.
	// ===========================================================
	if (runtime === ActionRuntime.ABX_NODEJS) {
		return isAbx ? ActionRuntime.ABX_NODEJS : ActionRuntime.VRO_NODEJS_12;
	}

	if (runtime === ActionRuntime.ABX_POWERSHELL) {
		return isAbx ? ActionRuntime.ABX_POWERSHELL : ActionRuntime.VRO_POWERCLI_11_PS_62;
	}

	if (runtime === ActionRuntime.ABX_PYTHON) {
		return isAbx ? ActionRuntime.ABX_PYTHON : ActionRuntime.VRO_PYTHON_37;
	}
	// ===========================================================

	if (!isAbx) {
		return runtime;
	}

	for (const [key, value] of Object.entries(MappedAbxRuntimes)) {
		if (value.includes(runtime)) {
			return key as ActionRuntime;
		}
	}

	return runtime;
}

/**
 * Determine the action type based on the action manifest.
 * @param pkg
 */
export function determineActionType(pkg: PlatformDefinition, actionType?: ActionType): ActionType {
	if (actionType === ActionType.ABX) {
		return pkg?.abx ? ActionType.ABX : ActionType.UNKNOWN;
	} else if (actionType === ActionType.VRO) {
		return pkg?.vro ? ActionType.VRO : ActionType.UNKNOWN;
	} else if (pkg?.vro) {
		return ActionType.VRO;
	} else if (pkg?.abx) {
		return ActionType.ABX;
	} else {
		return ActionType.UNKNOWN;
	}
}

/**
 * Collect information about all actions under project.
 * Expected folder structure:
 *   project_root/
 *     package.json - project configuration file
 *     src/
 *       <nodejs_action>/
 *         polyglot.json - platform parameters
 *         package.json - dependencies
 *         tsconfig.json - ts configuration
 *         handler.ts - action handler source file
 *       <powershell_action>/
 *         polyglot.json - platform parameters
 *         handler.ps1 - action handler source file
 *       <python_action>/
 *         polyglot.json - platform parameters
 *         requirements.txt - required modules
 *         handler.py
 *
 * Folders created during build:
 *   project_root/
 *     dist/
 *       action_name/
 *         dist/
 *           bundle.zip
 *     out/
 *       action_name/
 *         out/
 *         polyglot-cache/
 *     target/
 *
 * The function will search for subfolders of src containing polyglot.json files.
 * It will create an array of ActionOptions objects, one per folder,
 * that will contain all relevant paths and parameters for packaging the action.
 * If a folder name starts with "template-" it is ignored.
 * For each folder it will seach for an applicable package.json,
 * starting from the action folder and checking parent folders up to project root.
 *
 * If no polyglot.json files were found, a legacy project compatibility mode is assumed.
 * Legacy project folder structure:
 *   project root/
 *     package.json
 *     tsconfig.json (nodejs only)
 *     requirements.txt (python only)
 *     src/
 *       handler.ts or handler.ps1 or handler.py
 *     dist/
 *       bundle.zip
 *     out/
 *     polyglot-cache/
 *     target/
 *
 */
export async function getProjectActions(options: PackagerOptions, actionType?: ActionType): Promise<ProjectActions> {

	// Search for all polyglot.json files located in subfolders of src
	const plg = await globby(['src/**/polyglot.json', '!**/node_modules/**'], {
		cwd: options.workspace,
		absolute: true
	});

	if (plg.length === 0) {
		// No polyglot.json found. Assuming legacy project.
		// Locate package.json from project root
		const pkg = await globby(['package.json', '!**/node_modules/**'], {
			cwd: options.workspace,
			absolute: true
		});

		if (pkg.length === 0) {
			return [];
		}
		const pkgObj = await fs.readJSONSync(pkg[0]);

		// Set options for a legacy project
		let projectAction: ActionOptions =
		{
			...options,
			mixed: false,
			polyglotJson: pkg[0],          // platform options are located in project_root/package.json
			actionBase: options.workspace, // where to search for tsconfig.json and requirements.txt
			outBase: options.workspace,    // create out and polyglot-cache subfolders in project_root
			src: 'src',
			out: 'out',
			actionRuntime: await determineRuntime(pkgObj, actionType),
			actionType: await determineActionType(pkgObj, actionType)
		};
		return [projectAction];
	}

	let projectActions: ProjectActions = [];
	const projectBasePath: string = path.resolve(options.workspace);
	// Loop all actions found
	for (var i = 0; i < plg.length; i++) {
		let actionBasePath: string = path.dirname(plg[i]);
		if (actionBasePath.includes('template-')) {
			// Ignore folders starting with "template-"
			continue;
		}

		const actionFolder: string = path.relative(path.join(projectBasePath, 'src'), actionBasePath);
		// To separate out files of multiple actions out and polyglot-cache are created under project_root/out/action_name
		const outBasePath: string = path.join(options.workspace, 'out', actionFolder);
		// To separate bundle.zip files of multiple actions they are created under project_root/dist/action_name/dist
		const bundleZipPath: string = path.resolve('.', 'dist', actionFolder, 'dist', 'bundle.zip');
		const plgObj = await fs.readJSONSync(plg[i]);
		let projectAction: ActionOptions =
		{
			...options,
			bundle: bundleZipPath,
			mixed: true,
			polyglotJson: plg[i],
			actionBase: actionBasePath,
			outBase: outBasePath,
			src: path.relative(projectBasePath, actionBasePath),
			out: path.relative(projectBasePath, path.join(outBasePath, 'out')),
			actionRuntime: await determineRuntime(plgObj, actionType),
			actionType: await determineActionType(plgObj, actionType)
		};
		projectActions.push(projectAction);
	}
	return projectActions;
}

/**
 * Create a package.json file for ABX action dist.
 */
export async function createPackageJsonForABX(options: ActionOptions, isMixed: boolean) {
	const projectPkg = await getActionManifest(options.workspace) as PackageDefinition;
	const polyglotPkg = isMixed && await fs.readJSONSync(options.polyglotJson) as AbxActionDefinition;
	if (polyglotPkg && polyglotPkg.platform.action === "auto") {
		polyglotPkg.platform.action = path.basename(options.actionBase);
	}
	const bundlePkg = isMixed ? { ...projectPkg, ...polyglotPkg } : projectPkg;
	const actionDistPkgPath = path.join(options.workspace, 'dist', isMixed ? path.basename(options.actionBase) : "", 'package.json');
	fs.writeJsonSync(actionDistPkgPath, bundlePkg);
}

/**
 * Return the parsed content of the project's package.
 */
export async function getActionManifest(projectPath: string): Promise<AbxActionDefinition | VroActionDefinition | null> {

	const pkg = await globby(['package.json', '!**/node_modules/**'], {
		cwd: projectPath,
		absolute: true
	});

	if (pkg.length === 0) {
		return null;
	}

	const pkgObj = await fs.readJSONSync(pkg[0]);
	return pkgObj;
}

/**
 * Return true if the value is not undefined
 * @param x
 */
export function notUndefined<T>(x: T | undefined): x is T {
	return x !== undefined;
}

/**
 * Run external command and wait for it to complete
 * @param cmd
 */
export function run(cmd: string, args: Array<string> = [], cwd: string = process.cwd()): Promise<number> {
	return new Promise((resolve, reject) => {
		which(cmd, { all: true }, (err: Error | null, commandPath: string[] | undefined) => {
			if (err || !commandPath) {
				return reject(new Error(`Cannot find "${cmd}"`));
			}
			const proc = spawn(quoteString(commandPath[0]), args, { cwd, shell: true, stdio: 'inherit' });
			proc.on('close', exitCode => {
				if (exitCode !== 0) {
					const commandLine = `${quoteString(commandPath[0])} ${args.join(' ')}`;
					logger.error(`Error running command: ${commandLine}`);
					return reject(new Error(`Exit code for ${cmd}: ${exitCode}`));
				}
				resolve(exitCode);
			});
		});
	});
}

function quoteString(str: string) {
	return /\s+/.test(str) ? `"${str}"` : str;
}
