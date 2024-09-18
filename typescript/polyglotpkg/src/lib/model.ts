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
import { Writable } from "stream";

export enum ActionType {
	UNKNOWN = 'unknown',
	VRO = 'vro',
	ABX = 'abx'
}

// This section contains a list of supported runtimes for actions. Adding a new runtime requires updating this section and this section only.
// If this is ever not the case, we need to refactor the code to make it so.

export enum ActionRuntime {
	UNKNOWN = 'unknown',
	VRO_NODEJS_12 = 'node:12',
	VRO_NODEJS_14 = 'node:14',
	VRO_NODEJS_18 = 'node:18',
	VRO_NODEJS_20 = 'node:20',

	VRO_POWERCLI_11_PS_62 = 'powercli:11-powershell-6.2',
	VRO_POWERCLI_12_PS_71 = 'powercli:12-powershell-7.1',

	VRO_PYTHON_37 = 'python:3.7',
	VRO_PYTHON_310 = 'python:3.10',

	ABX_NODEJS = 'nodejs',
	ABX_POWERSHELL = 'powershell',
	ABX_PYTHON = 'python'
}

// After adding a new runtime, add it to the appropriate list below.

export const VroNodeJsActionRuntimes = [
	ActionRuntime.VRO_NODEJS_12,
	ActionRuntime.VRO_NODEJS_14,
	ActionRuntime.VRO_NODEJS_18,
	ActionRuntime.VRO_NODEJS_20
];

export const VroPowershellActionRuntimes = [
	ActionRuntime.VRO_POWERCLI_11_PS_62,
	ActionRuntime.VRO_POWERCLI_12_PS_71
];

export const VroPythonActionRuntimes = [
	ActionRuntime.VRO_PYTHON_37,
	ActionRuntime.VRO_PYTHON_310
];

export const NodeJsActionRuntimes = VroNodeJsActionRuntimes.concat(ActionRuntime.ABX_NODEJS);
export const PowershellActionRuntimes = VroPowershellActionRuntimes.concat(ActionRuntime.ABX_POWERSHELL);
export const PythonActionRuntimes = VroPythonActionRuntimes.concat(ActionRuntime.ABX_PYTHON);

// END OF RUNTIME DEFINITIONS

/**
 * Holds a list of ABX runtimes and their corresponding VRO runtimes.
 * Used to replace VRO runtimes with ABX runtimes if abx project is detected.
 */
export const MappedAbxRuntimes = {
	[ActionRuntime.ABX_NODEJS]: VroNodeJsActionRuntimes,
	[ActionRuntime.ABX_POWERSHELL]: VroPowershellActionRuntimes,
	[ActionRuntime.ABX_PYTHON]: VroPythonActionRuntimes
};

export type ActionRuntimeType = typeof NodeJsActionRuntimes[number] | typeof PowershellActionRuntimes[number] | typeof PythonActionRuntimes[number];

export type PackageDefinition = {
	[key: string]: any;
	name: string;
	description: string;
	version: string;
	files?: Array<string>;
};

export type PlatformDefinition = PackageDefinition & {
	platform: {
		action: string,
		entrypoint: string,
		runtime: ActionRuntimeType,
		base?: string,
		tags?: Array<string>;
		memoryLimitMb?: number,
		timeoutSec?: number,
		protocolType?: 'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13';
	};
};

export type VroActionDefinition = PlatformDefinition & {
	vro: {
		module: string;
		id?: string,
		inputs?: {
			[key: string]: string;
		};
		outputType?: string,
	};
};

export type AbxActionDefinition = PlatformDefinition & {
	abx: {
		inputs?: {
			[key: string]: string;
		};
	};
};

export type PackagerOptions = {
	workspace: string,
	bundle: string,
	out: string,
	vro: string,
	skipVro: boolean,
	env: string | null,
	outputStream?: Writable;
};

export type BundleFileset = {
	files: Array<string>;
	baseDir: string;
};

export enum Events {
	COMPILE_START = 'compileStart',
	COMPILE_END = 'compileEnd',
	COMPILE_ERROR = 'compileError',
	DEPENDENCIES_START = 'dependenciesStart',
	DEPENDENCIES_END = 'dependenciesEnd',
	DEPENDENCIES_ERROR = 'dependenciesError',
	BUNDLE_START = 'bundleStart',
	BUNDLE_END = 'bundleEnd',
	BUNDLE_ERROR = 'bundleError'
}

export type ActionOptions = PackagerOptions & {
	// workspace: string;      Inherited: Project root directory
	// bundle: string;         Inherited: bundle file name
	mixed: boolean;         // true if multiple actions may exist
	polyglotJson: string;   // Full path to the file containing platform options for the current action
	actionBase: string;     // Directory where polyglot.json, tsconfig.json, requirements.txt are located
	outBase: string;        // Directory under which the out files and dependencies are to be copied to
	src: string;            // Relative path from workspace to the action source directory
	// out: string;            Inherited: Relative path from workspace to the out directory
	actionType: string;     // The type of action to be compiled. Either `vro` or `abx`
	actionRuntime: string;   // One of: `nodejs`, `python`, `powershell`
};

export type ProjectActions = Array<ActionOptions>;
