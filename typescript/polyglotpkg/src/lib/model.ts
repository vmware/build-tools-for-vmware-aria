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
    ABX = 'abx',
}

export enum ActionRuntime {
    UNKNOWN = 'unknown',
    VRO_NODEJS_12 = 'node:12',
    VRO_NODEJS_14 = 'node:14',
    VRO_POWERCLI_11_PS_62 = 'powercli:11-powershell-6.2',
    VRO_POWERCLI_12_PS_71 = 'powercli:12-powershell-7.1',
    VRO_PYTHON_37 = 'python:3.7',
    ABX_NODEJS = 'nodejs',
    ABX_POWERSHELL = 'powershell',
    ABX_PYTHON = 'python',
}

export type ActionRuntimeType =
    ActionRuntime.ABX_NODEJS | ActionRuntime.VRO_NODEJS_12 | ActionRuntime.VRO_NODEJS_14
    | ActionRuntime.ABX_POWERSHELL | ActionRuntime.VRO_POWERCLI_11_PS_62 | ActionRuntime.VRO_POWERCLI_12_PS_71
    | ActionRuntime.VRO_PYTHON_37 | ActionRuntime.ABX_PYTHON

export type PackageDefinition = {
    [key: string]: any,
    name: string,
    description: string,
    version: string,
    files?: Array<string>,
};

export type PlatformDefinition = PackageDefinition & {
    platform: {
        action: string,
        entrypoint: string,
        runtime: ActionRuntimeType,
        base?: string,
        tags?: Array<string>
        memoryLimitMb?: number,
        timeoutSec?: number,
        protocolType?: 'Ssl3' | 'Tls' | 'Tls11' | 'Tls12' | 'Tls13'
    }
}

export type VroActionDefinition = PlatformDefinition & {
    vro: {
        module: string
        id?: string,
        inputs?: {
            [key: string]: string
        }
        outputType?: string,
    }
};

export type AbxActionDefinition = PlatformDefinition & {
    abx: {
        inputs?: {
            [key: string]: string
        }
    }
};

export type PackagerOptions = {
    workspace: string,
    bundle: string,
    out: string,
    vro: string,
    skipVro: boolean,
    env: string | null,
    outputStream?: Writable,
}

export type BundleFileset = {
    files: Array<string>,
    baseDir: string
}

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
    // workspace: string,      Inherited: Project root directory
    // bundle: string,         Inherited: bundle file name
    mixed: boolean,         // true if multiple actions may exist
    polyglotJson: string,   // Full path to the file containing platform options for the current action
    actionBase: string,     // Directory where polyglot.json, tsconfig.json, requirements.txt are located
    outBase: string,        // Directory under which the out files and dependencies are to be copied to
    src: string,            // Relative path from workspace to the action source directory
    // out: string,            Inherited: Relative path from workspace to the out directory
    actionType: string,     // The type of action to be compiled. Either `vro` or `abx`
    actionRuntime: string   // One of: `nodejs`, `python`, `powershell`
}

export type ProjectActions = Array<ActionOptions>
