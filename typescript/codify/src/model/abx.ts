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
import chalk from 'chalk';
import { AxiosInstance } from 'axios';
import { IBaseAbxObject, Input, VraObject } from './base';
import { ActionParser } from '../parser/action';

const IAAS_API_VERSION = '2021-07-15';
const ABX_API_VERSION = '8.1.0';

export interface IAbxAction extends IBaseAbxObject {
  project: string;
  inputs: Input[];
  content: string;
  runtime: AbxActionRuntime;
  entrypoint: string;
  timeout: number; // in seconds
  memory: number; // in bytes
  dependencies: string;
  shared: boolean;
  provider: AbxActionProvider;
  configuration: Record<string, string | boolean | number>;
}

export type AbxActionProvider = '' | 'aws' | 'azure' | 'on-prem';

export type AbxActionRuntime = 'nodejs' | 'python' | 'powershell' | 'unknown';

type AbxActionResponse = {
  name: string;
  description?: string;
  createdMillis: number;
  updatedMillis: number;
  metadata: {
    actionIsRetriable: boolean;
  };
  runtime: string;
  source?: string;
  contentId?: string;
  dependencies?: string;
  entrypoint: string;
  inputs: {
    [key: string]: string;
  };
  cpuShares: number;
  memoryInMB: number;
  showMemoryAlert: boolean;
  timeoutSeconds: number;
  deploymentTimeoutSeconds: number;
  actionType: string;
  provider: string;
  configuration: Record<string, string | boolean | number>;
  system: boolean;
  shared: boolean;
  scalable: boolean;
  asyncDeployed: boolean;
  useTrustedCertificates: boolean;
  id: string;
  orgId: string;
  projectId: string;
  selfLink: string;
};

type AbxConstantResponse = {
  name: string;
  id: string;
  orgId: string;
  selfLink: string;
  value: string;
  updatedMillis: number;
  createdMillis: number;
  encrypted: boolean;
};

export class AbxAction extends VraObject {
  public readonly urn: string;

  constructor(protected readonly objectDefinition: IAbxAction) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.project}/${this.objectDefinition.name}`;
    console.log(`Resolved ABX action: ${this.urn}`);
  }

  get<IAbxAction>() {
    return <IAbxAction>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert ABX action.
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance): Promise<void> {
    const remoteAction = await AbxAction.getRemoteAbxAction(client, this.objectDefinition.name);

    // resolve references
    const projectId = await AbxAction.resolveReference(
      client,
      this.objectDefinition.project,
      'id',
      '/iaas/api/projects',
      IAAS_API_VERSION
    );

    // prepare constants
    const mappedInputs = await Promise.all(
      this.objectDefinition.inputs.map(async (input) => {
        const mappedInput = { ...input };
        if (mappedInput.type === 'constant') {
          mappedInput.name = await this.ensureConstant(client, false, mappedInput.name, mappedInput.value);
        } else if (mappedInput.type === 'encryptedConstant') {
          mappedInput.name = await this.ensureConstant(client, true, mappedInput.name, mappedInput.value);
        }
        return mappedInput;
      })
    );

    const payload = {
      actionType: 'SCRIPT', // this is always SCRIPT for non-flow, non-rest and non-poll actions
      projectId,
      entrypoint: this.objectDefinition.entrypoint,
      name: this.objectDefinition.name,
      runtime: this.objectDefinition.runtime,
      description: this.objectDefinition.description,
      id: this.objectDefinition.id,
      inputs: mappedInputs.reduce<Record<string, string>>((acc, value) => {
        acc[value.name] = value.value || '';
        return acc;
      }, {}),
      memoryInMB: this.objectDefinition.memory / 1000 / 1000,
      source: this.objectDefinition.content,
      system: false,
      timeoutSeconds: this.objectDefinition.timeout,
      dependencies: this.objectDefinition.dependencies,
      shared: this.objectDefinition.shared,
      provider: this.objectDefinition.provider,
      configuration: this.objectDefinition.configuration,
      // orgId: "<string>", // TODO: this might be needed for vRA Cloud
      // contentId: "<string>",
      // compressedContent: "<string>",
    };

    if (remoteAction) {
      console.log(`Updating ABX action ${this.objectDefinition.name}`);
      await client.put(`${remoteAction.selfLink}?apiVersion=${ABX_API_VERSION}`, payload);
    } else {
      console.log(`Creating ABX action ${this.objectDefinition.name}`);
      await client.post(`/abx/api/resources/actions?apiVersion=${ABX_API_VERSION}`, payload);
    }

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Download ABX action.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string): Promise<void> {
    console.log(`Downloading ABX action ${this.objectDefinition.name}`);

    // prepend metadata if needed
    const meta = this.getMeta();
    if (meta) {
      this.objectDefinition.content = meta + this.objectDefinition.content;
    }

    const actionTargetPath = path.resolve(path.normalize(target));

    let extension = 'js';
    if (this.objectDefinition.runtime === 'nodejs') {
      extension = 'js';
    } else if (this.objectDefinition.runtime === 'python') {
      extension = 'py';
    } else if (this.objectDefinition.runtime === 'powershell') {
      extension = 'ps1';
    }

    const actionTarget = path.join(actionTargetPath, `${this.objectDefinition.name}.${extension}`);
    console.log(`Writing ABX action to ${actionTarget}`);

    // make directory if it doesn't exist and write the ABX action
    await fs.promises.mkdir(actionTargetPath, { recursive: true });
    await fs.writeFile(actionTarget, this.objectDefinition.content);
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta(): string | null {
    const annotations = ActionParser.extractAnnotations(this.objectDefinition.content);
    const abxAnnotation = annotations.find((a) => a.tags['abx']);

    if (abxAnnotation) {
      // this action already has metadata
      console.log(`ABX action ${this.objectDefinition.name} already has meta annotation`);
      return null;
    }

    console.log(`Generating annotation for ABX action: ${this.objectDefinition.name}`);

    let annotation = '// auto-generated meta\n\n';

    switch (this.objectDefinition.runtime) {
      case 'python':
        annotation = '# auto-generated meta\n\n';
        annotation += "'''\n";
        break;

      case 'powershell':
        annotation = '# auto-generated meta\n\n';
        annotation += '<#\n';
        break;

      default:
    }

    annotation += '/**\n';
    annotation += ` * ${this.objectDefinition.description || 'Codify-generated ABX action'}\n`;
    annotation += ` * @abx_type            abx\n`;
    annotation += ` * @abx_id              ${this.objectDefinition.id}\n`;
    annotation += ` * @abx_name            ${this.objectDefinition.name}\n`;
    annotation += ` * @abx_project         ${this.objectDefinition.project}\n`;
    this.objectDefinition.inputs.forEach((input) => {
      annotation += ` * @abx_input           {${input.type}} ${input.name} ${input.value}\n`;
    });
    annotation += ` * @abx_entrypoint      ${this.objectDefinition.entrypoint}\n`;
    annotation += ` * @abx_timeout         ${this.objectDefinition.timeout}\n`;
    annotation += ` * @abx_memory          ${this.objectDefinition.memory}\n`;
    annotation += ` * @abx_runtime         ${this.objectDefinition.runtime}\n`;
    if (this.objectDefinition.dependencies) {
      annotation += ` * @abx_dependencies    ${this.objectDefinition.dependencies}\n`;
    }
    if (this.objectDefinition.provider) {
      annotation += ` * @abx_provider        ${this.objectDefinition.provider}\n`;
    }
    if (this.objectDefinition.configuration) {
      annotation += ` * @abx_configuration   ${JSON.stringify(this.objectDefinition.configuration)}\n`;
    }
    if (this.objectDefinition.shared) {
      annotation += ` * @abx_shared\n`;
    }
    annotation += ' */\n';

    switch (this.objectDefinition.runtime) {
      case 'python':
        annotation += "'''\n";
        break;

      case 'powershell':
        annotation += '#>\n';
        break;

      default:
    }

    return annotation;
  }

  /**
   * Make sure that a constant exists.
   * @param client {AxiosInstance}
   * @param encrypted {boolean}
   * @param constantName {string}
   * @param constantValue {string}
   * @returns the mapped constant name
   */
  async ensureConstant(
    client: AxiosInstance,
    encrypted: boolean,
    constantName: string,
    constantValue?: string
  ): Promise<string> {
    const params = new URLSearchParams({
      $filter: `(name eq '${constantName}')`,
      size: '1000',
      apiVersion: ABX_API_VERSION,
    });
    const constants = await client.get(`/abx/api/resources/action-secrets?${params.toString()}`);

    if (constants.data.content.length > 0) {
      return `secret:${constants.data.content[0].id}`;
    } else {
      console.log(`Creating ${encrypted ? 'encrypted ' : ''}constant ${constantName}`);
      const newConstant = await client.post(`/abx/api/resources/action-secrets?apiVersion=${ABX_API_VERSION}`, {
        encrypted,
        name: constantName,
        value: constantValue || '',
      });
      return `secret:${newConstant.data.id}`;
    }
  }

  /**
   * Retrieve an ABX constant by id
   * @param client {AxiosInstance}
   * @param constantId {string} ABX constant id
   * @returns Resolved ABX constant
   */
  static async getAbxConstant(client: AxiosInstance, constantId: string): Promise<AbxConstantResponse> {
    const params = new URLSearchParams({ apiVersion: ABX_API_VERSION });
    const constant = await client.get<AbxConstantResponse>(
      `/abx/api/resources/action-secrets/${constantId}?${params.toString()}`
    );
    return constant.data;
  }

  /**
   * Resolve an ABX action object from remote identifier.
   * @param client {AxiosInstance}
   * @param abxIdentifier {string} ABX action name or id
   * @returns the resolved ABX action
   */
  static async from(client: AxiosInstance, abxIdentifier: string): Promise<AbxAction | null> {
    const remoteAction = await AbxAction.getRemoteAbxAction(client, abxIdentifier);
    if (remoteAction) {
      // Only script-based actions are supported
      if (remoteAction.actionType !== 'SCRIPT') {
        console.warn(`Unsupported ABX action type: ${remoteAction.actionType}`);
        return null;
      }

      // Only single-script actions are supported
      if (!remoteAction.source) {
        console.warn('ABX action bundles are not supported yet');
        return null;
      }

      return new AbxAction({
        id: remoteAction.id,
        project: await AbxAction.getReference(
          client,
          `/iaas/api/projects/${remoteAction.projectId}?apiVersion=${IAAS_API_VERSION}`,
          'name'
        ),
        description: remoteAction.description || '',
        name: remoteAction.name,
        content: remoteAction.source,

        // The inputs property is of type Input[] and the ABX inputs need to be mapped
        // to Input scturctures. Additionally, inputs can be references to secrets and constants
        // so constant resolution needs to be performed to derrive the proper values.
        inputs: await Promise.all(
          Object.keys(remoteAction.inputs).map(async (name) => {
            const inputValue = <string>remoteAction.inputs[name];
            let inputName = name;
            let inputType = 'string';
            if (name.startsWith('secret:')) {
              const constantId = <string>name.split(':').pop();
              const constant = await AbxAction.getAbxConstant(client, constantId);
              inputType = constant.encrypted ? 'encryptedConstant' : 'constant';
              inputName = constant.name;
            }
            return <Input>{ name: inputName, value: inputValue, type: inputType };
          })
        ),

        entrypoint: remoteAction.entrypoint,
        timeout: remoteAction.timeoutSeconds,
        memory: remoteAction.memoryInMB * 1000 * 1000, // AbxAction constructor expects memory in bytes
        runtime: <AbxActionRuntime>remoteAction.runtime,
        dependencies: remoteAction.dependencies || '',
        shared: remoteAction.shared,
        provider: <AbxActionProvider>remoteAction.provider,
        configuration: remoteAction.configuration,
      });
    } else {
      console.warn(`Could not resolve ABX action: ${abxIdentifier}`);
      return null;
    }
  }

  /**
   * Retrieve remote ABX action.
   * @param client {AxiosInstance}
   * @param abxIdentifier {string}
   * @returns remote ABX action
   */
  static async getRemoteAbxAction(client: AxiosInstance, abxIdentifier: string): Promise<AbxActionResponse | null> {
    const params = new URLSearchParams({
      $filter: `(name eq '${abxIdentifier}')`,
      apiVersion: ABX_API_VERSION,
    });
    const abxSearch = await client.get(`/abx/api/resources/actions?${params.toString()}`);

    if (abxSearch.data.numberOfElements >= 1) {
      return abxSearch.data.content[0];
    }
    return null;
  }
}
