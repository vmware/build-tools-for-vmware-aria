/*-
 * #%L
 * codify
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


import path from 'path';
import fs from 'fs-extra';
import moment from 'moment';
import util from 'util';
import chalk from 'chalk';
import { AxiosInstance } from 'axios';
import { Attribute, RunInputs } from '.';
import { isUUID, isObject } from '..';
import { IBaseVroObject, Input, VroObject } from './base';
import { ActionParser } from '../parser/action';

export interface IBaseAction extends IBaseVroObject {
  module: string;
  inputs: Input[];
  output: {
    type: string;
    description: string;
  } | null;
  content: string;
}

export interface IPolyglotAction extends IBaseAction {
  runtime: PolyglotActionRuntime;
  entrypoint: string;
  timeout: number;
  memory: number;
}

export type PolyglotActionRuntime = 'node:12' | 'node:14' | 'python:3.7' | 'powercli:12-powershell-7.1' | 'unknown';

type LogEntry = {
  entry: {
    'time-stamp': string;
    severity: string;
    'short-description': string;
  };
};

/**
 * Custom typeguard for IPolyglotAction.
 * @param value {any}
 * @returns ture if the object is of type IPolyglotAction
 */
function isIPolyglotAction(value: unknown): value is IPolyglotAction {
  return isObject(value) && value.entryPoint !== undefined;
}

export abstract class BaseAction extends VroObject {
  public readonly urn: string;
  public readonly version: string;

  constructor(protected readonly objectDefinition: IBaseAction | IPolyglotAction) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.module}/${this.objectDefinition.name}`;
    this.version = this.objectDefinition.version;
  }

  get<T>(): T {
    return <T>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert action
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance) {
    // check remote action
    const remoteActionId = (await this.remoteActionExists(client, this.objectDefinition.id))
      ? this.objectDefinition.id
      : null;

    // Upsert action
    if (remoteActionId) {
      await this.updateAction(client, remoteActionId);
    } else {
      try {
        await this.createAction(client);
      } catch (err) {
        if ((<Error>err).message.includes('Action already exists')) {
          const conflictingAction = await this.getRemoteActionByModuleAndName(
            client,
            this.objectDefinition.module,
            this.objectDefinition.name
          );
          console.error(chalk.red(`The action "${this.urn}" already exists in the target vRO under another id!`));
          console.error(chalk.red('Add the following tag to your JSDoc in  to define an explicit action ID:'));
          console.error(chalk.bgRed(`@vro_id           ${conflictingAction.id}`));
        }
        throw err;
      }
    }
  }

  /**
   * Get codify-specific metadata.
   * This method is intended to be called as part of the download sequence.
   */
  getMeta(): string | null {
    const annotations = ActionParser.extractAnnotations(this.objectDefinition.content);
    const vroAnnotation = annotations.find((a) => a.tags['vro']);

    if (vroAnnotation) {
      // this action already has metadata
      console.log(`Action ${this.objectDefinition.module}/${this.objectDefinition.name} already has meta annotation`);
      return null;
    }

    console.log(`Generating annotation for action: ${this.objectDefinition.module}/${this.objectDefinition.name}`);

    let annotation = '// auto-generated meta\n\n';
    if (isIPolyglotAction(this.objectDefinition)) {
      switch (this.objectDefinition.runtime) {
        case 'python:3.7':
          annotation = '# auto-generated meta\n\n';
          annotation += "'''\n";
          break;

        case 'powercli:12-powershell-7.1':
          annotation = '# auto-generated meta\n\n';
          annotation += '<#\n';
          break;

        default:
      }
    }

    annotation += '/**\n';
    annotation += ` * ${this.objectDefinition.description || 'Codify-generated action'}\n`;
    annotation += ` * @vro_type        ${isIPolyglotAction(this.objectDefinition) ? 'polyglot' : 'action'}\n`;
    annotation += ` * @vro_id          ${this.objectDefinition.id}\n`;
    annotation += ` * @vro_name        ${this.objectDefinition.name}\n`;
    annotation += ` * @vro_module      ${this.objectDefinition.module}\n`;
    annotation += ` * @vro_version     ${this.objectDefinition.version}\n`;
    this.objectDefinition.inputs.forEach((input) => {
      annotation += ` * @vro_input       {${input.type}} ${input.name} ${input.description}\n`;
    });
    if (this.objectDefinition.output) {
      annotation += ` * @vro_output      {${this.objectDefinition.output.type}} ${this.objectDefinition.output.description}\n`;
    }
    if (isIPolyglotAction(this.objectDefinition)) {
      annotation += ` * @vro_entrypoint  ${this.objectDefinition.entrypoint}\n`;
      annotation += ` * @vro_runtime     ${this.objectDefinition.runtime}\n`;
      annotation += ` * @vro_memory      ${this.objectDefinition.memory}\n`;
      annotation += ` * @vro_timeout     ${this.objectDefinition.timeout}\n`;
    }
    annotation += ' */\n';

    if (isIPolyglotAction(this.objectDefinition)) {
      switch (this.objectDefinition.runtime) {
        case 'python:3.7':
          annotation += "'''\n";
          break;

        case 'powercli:12-powershell-7.1':
          annotation += '#>\n';
          break;

        default:
      }
    }

    return annotation;
  }

  protected abstract updateAction(client: AxiosInstance, remoteActionId: string): Promise<void>;
  protected abstract createAction(client: AxiosInstance): Promise<void>;

  /**
   * Checks if a remote action exists by using its id.
   * @param client {AxiosInstance}
   * @param actionId {string}
   * @returns true if the action exists
   */
  protected async remoteActionExists(client: AxiosInstance, actionId: string) {
    const remoteActionContents = await client.head(`/actions/${actionId}`, { validateStatus: null });
    return remoteActionContents.status === 200;
  }

  protected async getRemoteActionByModuleAndName(client: AxiosInstance, module: string, name: string) {
    const remoteActionContents = await client.get(
      `/actions/${this.objectDefinition.module}/${this.objectDefinition.name}`
    );
    return remoteActionContents.data;
  }

  /**
   * Resolve a vRO action object from remote identifier.
   * @param client {AxiosInstance}
   * @param actionIdentifier {string} action path (module/action) or id
   * @returns the resolved action
   */
  static async from(client: AxiosInstance, actionIdentifier: string): Promise<PolyglotAction | Action | null> {
    let remoteAction = null;

    if (isUUID(actionIdentifier)) {
      // resolve using id

      const remoteActionResponse = await client.get(`/actions/${actionIdentifier}`, { validateStatus: null });
      if (remoteActionResponse.status === 200) {
        remoteAction = remoteActionResponse.data;
      }
    } else {
      // resolve using path
      remoteAction = await (async () => {
        const [moduleName, actionName] = actionIdentifier.split('/');

        // find module
        const modules = await client.get('/catalog/System/Catalog/Actions/sub-categories');
        const module = modules.data.link.find((m: { href: string }) => {
          return m.href.endsWith(`${moduleName}/`);
        });
        if (!module) {
          return null;
        }
        const remoteModuleId = module.attributes.find((a: Attribute) => a.name === 'id').value;

        // find action within the module
        const moduleContents = await client.get(`/categories/${remoteModuleId}`);
        const correctLink = moduleContents.data.relations.link
          .filter((l: { attributes?: Attribute[] }) => l['attributes'])
          .find((l: { attributes: Attribute[] }) =>
            l.attributes.find((a: Attribute) => a.name === 'name' && a.value === actionName)
          );

        if (!correctLink) {
          return null;
        }

        const remoteActionId = correctLink.attributes.find((a: Attribute) => a.name === 'id').value;
        const remoteActionResponse = await client.get(`/actions/${remoteActionId}`);

        return remoteActionResponse.data;
      })();
    }

    if (remoteAction && remoteAction.entryPoint) {
      return new PolyglotAction({
        id: remoteAction.id,
        name: remoteAction.name,
        description: remoteAction.description,
        version: remoteAction.version,
        module: remoteAction.module,
        inputs: remoteAction['input-parameters'] || [],
        output: { type: remoteAction['output-type'], description: '' },
        content: remoteAction.script,
        entrypoint: remoteAction.entryPoint,
        runtime: remoteAction.runtime,
        memory: remoteAction.runtimeMemoryLimit,
        timeout: remoteAction.runtimeTimeout,
      });
    } else if (remoteAction) {
      return new Action({
        id: remoteAction.id,
        name: remoteAction.name,
        description: remoteAction.description,
        version: remoteAction.version,
        module: remoteAction.module,
        inputs: remoteAction['input-parameters'] || [],
        output: { type: remoteAction['output-type'], description: '' },
        content: remoteAction.script,
      });
    }

    console.warn(`Could not resolve action: ${actionIdentifier}`);
    return null;
  }

  /**
   * Run vRO action
   * @param client {AxiosInstance}
   */
  async run(client: AxiosInstance, inputs?: RunInputs | string): Promise<void> {
    const inputsMap = this.getInputsMap(
      inputs ? (Array.isArray(inputs) ? inputs : [inputs]) : [],
      this.objectDefinition.inputs
    );

    // build payload
    const payload = {
      'async-execution': false,
      parameters: this.objectDefinition.inputs
        .map((input) => {
          if (inputsMap[input.name]) {
            return {
              name: input.name,
              type: input.type,
              value: {
                [input.type]: { value: inputsMap[input.name] },
              },
            };
          }
          return null;
        })
        .filter((input) => input !== null),
    };

    console.log('');
    console.log('=========================================');
    console.log(`Action inputs`);
    console.log('=========================================');
    console.log(util.inspect(payload.parameters, { depth: 100 }));

    // run action
    const execution = await client.post(`/actions/${this.objectDefinition.id}/executions`, payload);

    // show logs
    const logs = await client.get(`/actions/${execution.data['execution-id']}/logs`);
    console.log('');
    console.log('=========================================');
    console.log(`Action run ${execution.data['execution-id']}`);
    console.log('=========================================');
    console.debug(`${logs.data.logs.length} logs collected`);
    logs.data.logs.forEach((entry: LogEntry) => {
      const timestamp = moment(entry.entry['time-stamp']);
      console.log(`${timestamp.format()} ${entry.entry.severity.toUpperCase()} ${entry.entry['short-description']}`);
    });
    console.log('');

    // show output
    console.log('=========================================');
    console.log(`Action output`);
    console.log('=========================================');
    if (execution.data.error) {
      console.error(chalk.red('Action error:', execution.data.error));
    } else {
      console.log(util.inspect(execution.data.value, { depth: 100 }));
    }
    console.log('');
  }
}

export class PolyglotAction extends BaseAction {
  constructor(protected readonly objectDefinition: IPolyglotAction) {
    super(objectDefinition);
    console.debug(`Resolved Polyglot action ${this.urn}`);
  }

  /**
   * Download action.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string) {
    console.log(`Downloading action ${this.objectDefinition.module}/${this.objectDefinition.name}`);

    // prepend metadata if needed
    const meta = this.getMeta();
    if (meta) {
      this.objectDefinition.content = meta + this.objectDefinition.content;
    }

    const actionTargetPath = path.resolve(path.join(path.normalize(target), this.objectDefinition.module));

    let extension = 'js';
    if (this.objectDefinition.runtime.startsWith('node')) {
      extension = 'js';
    } else if (this.objectDefinition.runtime.startsWith('python')) {
      extension = 'py';
    } else if (this.objectDefinition.runtime.startsWith('powercli')) {
      extension = 'ps1';
    }

    const actionTarget = path.join(actionTargetPath, `${this.objectDefinition.name}.${extension}`);
    console.log(`Writing action to ${actionTarget}`);

    // make directory if it doesn't exist and write the action
    await fs.promises.mkdir(actionTargetPath, { recursive: true });
    await fs.writeFile(actionTarget, this.objectDefinition.content);
  }

  protected async updateAction(client: AxiosInstance, actionId: string) {
    console.log(`Updating Polyglot action ${this.objectDefinition.module}/${this.objectDefinition.name}`);
    await client.put(`/actions/${actionId}`, {
      description: this.objectDefinition.description,
      module: this.objectDefinition.module,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      script: this.objectDefinition.content,
      'output-type': this.objectDefinition.output?.type || null,
      'input-parameters': this.objectDefinition.inputs,
      // polyglot-specific options
      runtime: this.objectDefinition.runtime,
      entryPoint: this.objectDefinition.entrypoint,
      bundleHasContent: false,
      runtimeMemoryLimit: this.objectDefinition.memory,
      runtimeTimeout: this.objectDefinition.timeout,
    });
    console.log(chalk.green(` -> Done`));
  }

  protected async createAction(client: AxiosInstance) {
    console.log(`Creating Polyglot action ${this.objectDefinition.module}/${this.objectDefinition.name}`);
    await client.post(`/actions`, {
      id: this.objectDefinition.id,
      description: this.objectDefinition.description,
      module: this.objectDefinition.module,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      script: this.objectDefinition.content,
      'output-type': this.objectDefinition.output?.type || null,
      'input-parameters': this.objectDefinition.inputs,
      // polyglot-specific options
      runtime: this.objectDefinition.runtime,
      entryPoint: this.objectDefinition.entrypoint,
      bundleHasContent: false,
      runtimeMemoryLimit: this.objectDefinition.memory,
      runtimeTimeout: this.objectDefinition.timeout,
    });
    console.log(chalk.green(` -> Done`));
  }
}

export class Action extends BaseAction {
  constructor(protected readonly objectDefinition: IBaseAction) {
    super(objectDefinition);
    console.debug(`Resolved action ${this.urn}`);
  }

  /**
   * Download action.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string) {
    console.log(`Downloading action ${this.objectDefinition.module}/${this.objectDefinition.name}`);

    // prepend metadata if needed
    const meta = this.getMeta();
    if (meta) {
      this.objectDefinition.content = meta + this.objectDefinition.content;
    }

    const actionTargetPath = path.resolve(path.join(path.normalize(target), this.objectDefinition.module));
    const actionTarget = path.join(actionTargetPath, `${this.objectDefinition.name}.js`);
    console.log(`Writing action to ${actionTarget}`);

    // make directory if it doesn't exist and write the action
    await fs.promises.mkdir(actionTargetPath, { recursive: true });
    await fs.writeFile(actionTarget, this.objectDefinition.content);
  }

  protected async updateAction(client: AxiosInstance, actionId: string) {
    console.log(`Updating action ${this.objectDefinition.module}/${this.objectDefinition.name}`);
    await client.put(`/actions/${actionId}`, {
      description: this.objectDefinition.description,
      module: this.objectDefinition.module,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      script: this.objectDefinition.content,
      'output-type': this.objectDefinition.output?.type || null,
      'input-parameters': this.objectDefinition.inputs,
    });
    console.log(chalk.green(` -> Done`));
  }

  protected async createAction(client: AxiosInstance) {
    console.log(`Creating action ${this.objectDefinition.module}/${this.objectDefinition.name}`);
    await client.post(`/actions`, {
      id: this.objectDefinition.id,
      description: this.objectDefinition.description,
      module: this.objectDefinition.module,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      script: this.objectDefinition.content,
      'output-type': this.objectDefinition.output?.type || null,
      'input-parameters': this.objectDefinition.inputs,
    });
    console.log(chalk.green(` -> Done`));
  }
}
