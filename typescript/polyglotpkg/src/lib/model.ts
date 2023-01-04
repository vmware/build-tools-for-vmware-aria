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

export type PlatformDefintion = PackageDefinition & {
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

export type VroActionDefinition = PlatformDefintion & {
    vro: {
        module: string
        id?: string,
        inputs?: {
            [key: string]: string
        }
        outputType?: string,
    }
};

export type AbxActionDefinition = PlatformDefintion & {
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
