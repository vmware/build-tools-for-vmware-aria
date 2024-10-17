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
import { Logger } from 'winston';

import { PackagerOptions, ActionType, ActionRuntime, Events, NodeJsActionRuntimes, PythonActionRuntimes, PowershellActionRuntimes } from "./lib/model";
import { createPackageJsonForABX, getProjectActions } from './lib/utils';
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
		// Collect list of actions included in the project
		const projectActions = await getProjectActions(this.options, <ActionType>this.options.env);

		// Loop all actions found and execute the packager for each of them
		for (var i = 0; i < projectActions.length; i++) {
			const actionType = projectActions[i].actionType;
			const actionRuntime = projectActions[i].actionRuntime;

			if (actionType === ActionType.UNKNOWN || actionRuntime === ActionRuntime.UNKNOWN) {
				throw new Error(`Unsupported action type or runtime: ${actionType} ${actionRuntime}`);
			}

			this.logger.info(`Packaging ${actionType} ${actionRuntime} action from ${projectActions[i].src}...`);

			let strategy: IStrategy;

			switch (true) {
				case NodeJsActionRuntimes.includes(actionRuntime as ActionRuntime):
					strategy = new NodejsStrategy(this.logger, projectActions[i], (e: Events) => this.emit(e));
					break;
				case PowershellActionRuntimes.includes(actionRuntime as ActionRuntime):
					strategy = new PowershellStrategy(this.logger, projectActions[i], (e: Events) => this.emit(e));
					break;
				case PythonActionRuntimes.includes(actionRuntime as ActionRuntime):
					strategy = new PythonStrategy(this.logger, projectActions[i], (e: Events) => this.emit(e));
					break;
				default:
					throw new Error(`Action runtime ${actionRuntime} is not yet supported`);
			}

			await strategy.packageProject();

			// Prepare input files for vRO packaging
			if (!this.options.skipVro && actionType === ActionType.VRO) {
				const tree = new VroTree(this.logger, projectActions[i]);
				await tree.createTree();
			}

			// Prepare input files for ABX packaging
			if (actionType === ActionType.ABX) {
				await createPackageJsonForABX(projectActions[i], projectActions[i].mixed);
			}
		}
	}

}
