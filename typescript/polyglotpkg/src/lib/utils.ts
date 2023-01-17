import globby from 'globby';
import fs from 'fs-extra';
import which from 'which';
import { spawn } from 'child_process';
import { ActionType, ActionRuntime, AbxActionDefinition, VroActionDefinition, PlatformDefintion } from './model';
import createLogger from './logger';

const logger = createLogger();

/**
 * Determine the action runtime based on the action manifest or
 * the action handler if runtime is not specified.
 * @param projectPath
 */
export async function determineRuntime(projectPath: string, actionType?: ActionType): Promise<ActionRuntime> {
    const pkg = await getActionManifest(projectPath) as PlatformDefintion;

    if (actionType) {
        switch (pkg.platform.runtime) {
            case 'nodejs':
                return actionType === ActionType.ABX ? ActionRuntime.ABX_NODEJS : ActionRuntime.VRO_NODEJS_12;
            case "powershell":
                return actionType === ActionType.ABX ? ActionRuntime.ABX_POWERSHELL : ActionRuntime.VRO_POWERCLI_11_PS_62;
            case "python":
                return actionType === ActionType.ABX ? ActionRuntime.ABX_PYTHON : ActionRuntime.VRO_PYTHON_37;
            default:
                return pkg.platform.runtime;
        }
    }

    switch (pkg.platform.runtime) {
        case "nodejs":
            return pkg.vro ? ActionRuntime.VRO_NODEJS_12 : ActionRuntime.ABX_NODEJS;
        case 'powershell':
            return pkg.vro ? ActionRuntime.VRO_POWERCLI_11_PS_62 : ActionRuntime.ABX_POWERSHELL;
        case 'python':
            return pkg.vro ? ActionRuntime.VRO_PYTHON_37 : ActionRuntime.ABX_PYTHON;
        default:
            return pkg.platform.runtime;
    }
}

/**
 * Determine the action type based on the action manifest.
 */
export async function determineActionType(projectPath: string, actionType?: ActionType): Promise<ActionType> {
    const pkg = await getActionManifest(projectPath);

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
        which(cmd, {all: true}, (err: Error | null, commandPath: string[] | undefined) => {
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
