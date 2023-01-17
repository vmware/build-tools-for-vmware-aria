import { Logger } from 'winston';

import { PackagerOptions, ActionType, ActionRuntime, Events } from "./lib/model";
import { determineActionType, determineRuntime } from './lib/utils';
import { IStrategy } from './strategies/base';
import { NodejsStrategy } from './strategies/nodejs';
import { VroTree } from './vro';
import { PythonStrategy } from './strategies/python';
import { PowershellStrategy } from './strategies/powershell';
import createLogger from './lib/logger';
import { EventEmitter } from 'events';

export class Packager extends EventEmitter {

    private readonly logger: Logger;

    constructor(private readonly options: PackagerOptions) {
        super();
        this.logger = createLogger(false, options.outputStream);
    }

    async packageProject() {

        const actionRuntime = await determineRuntime(this.options.workspace, <ActionType>this.options.env);
        const actionType = await determineActionType(this.options.workspace, <ActionType>this.options.env);

        if (actionType === ActionType.UNKNOWN || actionRuntime === ActionRuntime.UNKNOWN) {
            throw new Error(`Unsupported action type or runtime: ${actionType} ${actionRuntime}`);
        }

        this.logger.info(`Packaging ${actionType} ${actionRuntime} action...`);

        let strategy: IStrategy;

        switch (actionRuntime) {
            case ActionRuntime.ABX_NODEJS:
            case ActionRuntime.VRO_NODEJS_12:
                strategy = new NodejsStrategy(this.logger, this.options, (e: Events) => this.emit(e));
                await strategy.packageProject();
                break;
            case ActionRuntime.ABX_PYTHON:
            case ActionRuntime.VRO_PYTHON_37:
                strategy = new PythonStrategy(this.logger, this.options, (e: Events) => this.emit(e));
                await strategy.packageProject();
                break;
            case ActionRuntime.ABX_POWERSHELL:
            case ActionRuntime.VRO_POWERCLI_11_PS_62:
                strategy = new PowershellStrategy(this.logger, this.options, (e: Events) => this.emit(e));
                strategy.packageProject();
                break;
            default:
                throw new Error(`Action runtime ${actionRuntime} is not yet supported`);
        }

        if (!this.options.skipVro && actionType === ActionType.VRO) {
            const tree = new VroTree(this.logger, this.options);
            await tree.createTree();
        }

    }

}
