import { Writable } from "stream";

export enum ActionType {
    UNKNOWN = 'unknown',
    VRO = 'vro',
    ABX = 'abx',
}

export enum ActionRuntime {
    UNKNOWN = 'unknown',
    VRO_NODEJS_12 = 'node:12',
    VRO_POWERCLI_11_PS_62 = 'powercli:11-powershell-6.2',
    VRO_PYTHON_37 = 'python:3.7',
    ABX_NODEJS = 'nodejs',
    ABX_POWERSHELL = 'powershell',
    ABX_PYTHON = 'python',
}

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
        runtime: 'python' | 'nodejs' | 'powershell',
        base?: string,
        tags?: Array<string>
        memoryLimitMb?: number,
        timeoutSec?: number
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
