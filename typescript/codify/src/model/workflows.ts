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
import FormData from 'form-data';
import chalk from 'chalk';
import yaml from 'js-yaml';
import { AxiosInstance } from 'axios';
import { Attribute, IBaseVroObject, RunInputs, VroObject } from './base';
import { attributesToMap, isUUID } from '..';

export type WorkflowContent = {
  'display-name': string;
  description: string;
  'object-name': string;
  id: string;
  version: string;
};
export type WorkflowForm = {
  schema: Record<string, { id: string; type: { dataType: string; isMultiple: boolean }; label: string }>;
  layout: { pages: Array<{ id: string; sections: Array<unknown> }> };
  options: unknown;
  itemId: unknown;
};
export interface IWorkflow extends IBaseVroObject {
  folder: string;
  content?: WorkflowContent;
  nativeContent?: Buffer;
  presentation?: WorkflowForm;
}

export class Workflow extends VroObject {
  public readonly urn: string;
  public readonly version: string;

  constructor(protected readonly objectDefinition: IWorkflow) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.folder}/${this.objectDefinition.name}`;
    this.version = this.objectDefinition.version;
    console.log(`Resolved workflow: ${this.urn}`);
  }

  get<IWorkflow>() {
    return <IWorkflow>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert workflow
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance) {
    await this.createWorkflow(client);
  }

  /**
   * Download wokflow.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string) {
    console.log(`Downloading workflow ${this.urn}`);

    // retrieve workflow content if needed
    if (!this.objectDefinition.content) {
      const workflowContent = await client.get(`/workflows/${this.objectDefinition.id}/content`, {
        headers: { Accept: `application/json` },
      });
      this.objectDefinition.content = workflowContent.data;
    }

    // retreive workflow presentation if needed
    if (!this.objectDefinition.presentation) {
      const workflowPresentation = await client.get(`/forms/${this.objectDefinition.id}`);
      this.objectDefinition.presentation = workflowPresentation.data;
    }

    // make directory if it doesn't exist and write the workflow
    const workflowTargetPath = path.resolve(path.join(path.normalize(target), this.objectDefinition.folder));
    await fs.promises.mkdir(workflowTargetPath, { recursive: true });

    // create meta file
    const metaTarget = path.join(workflowTargetPath, `${this.objectDefinition.name}.workflow.meta`);
    console.log(`Writing metadata to ${metaTarget}`);
    await fs.promises.writeFile(metaTarget, this.getMeta());

    // retrieve native content if needed
    if (!this.objectDefinition.nativeContent) {
      const nativeContent = await client.get(`/content/workflows/${this.objectDefinition.id}`, {
        responseType: 'arraybuffer',
      });
      this.objectDefinition.nativeContent = nativeContent.data;
    }

    // create binary data
    const workflowTarget = path.join(workflowTargetPath, `${this.objectDefinition.name}.workflow`);
    console.log(`Writing workflow to ${workflowTarget}`);
    await fs.writeFile(workflowTarget, this.objectDefinition.nativeContent);
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta() {
    return yaml.dump(this.objectDefinition, { indent: 2, lineWidth: -1 });
  }

  /**
   * Create a new workflow
   * @param client {AxiosInstance}
   * @returns
   */
  private async createWorkflow(client: AxiosInstance): Promise<void> {
    console.log(`Uploading workflow ${this.urn}`);

    if (!this.objectDefinition.content) {
      throw new Error(`Missing workflow content`);
    }

    // validate the workflows
    const isValid = await this.validateWorkflow(client, this.objectDefinition.content);
    if (!isValid) {
      console.error(chalk.red(`Workflow ${this.urn} is invalid. Skipping...`));
      return;
    }

    // upsert category
    let categoryId = await this.getCategoryIdForFolder(client, this.objectDefinition.folder, 'WorkflowCategory');
    if (!categoryId) {
      categoryId = await this.createCategoryForFolder(client, this.objectDefinition.folder, 'WorkflowCategory');
    }

    // upload workflow content
    const formData = new FormData();
    formData.append('file', this.objectDefinition.nativeContent, {
      filename: `${this.objectDefinition.name}.workflow`,
    });
    await client.post(`/content/workflows/${categoryId}?overwrite=true`, formData, { headers: formData.getHeaders() });

    // upload workflow's presentation
    if (this.objectDefinition.presentation) {
      await client.put(`/forms/${this.objectDefinition.id}`, this.objectDefinition.presentation);
    }

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Validate the workflow's content before upserting it.
   * @param client {AxiosInstance}
   * @param content {WorkflowContent}
   * @returns true if the workflow is valid
   */
  private async validateWorkflow(client: AxiosInstance, content: WorkflowContent): Promise<boolean> {
    const validationResponse = await client.put<{
      messages: { entry: { severity: string; text: string; title: string } }[];
    }>(`/workflows/validate/schema`, content);

    validationResponse.data.messages.forEach((message) => {
      switch (message.entry.severity) {
        case 'WARN':
          console.warn(chalk.yellow(`${message.entry.text}: ${message.entry.title}`));
          break;
        case 'ERROR':
          console.error(chalk.red(`${message.entry.text}: ${message.entry.title}`));
          break;
        default:
          console.error(chalk.red(`${message.entry.text}: ${message.entry.title}`));
          break;
      }
    });

    const isValid = !validationResponse.data.messages.some((message) => message.entry.severity === 'ERROR');
    return isValid;
  }

  /**
   * Resolve a vRO workflow object from remote identifier.
   * @param client {AxiosInstance}
   * @param workflowIdentifier {string} workflow path (folder/workflow) or id
   * @returns the resolved workflow
   */
  static async from(client: AxiosInstance, workflowIdentifier: string): Promise<Workflow | null> {
    if (isUUID(workflowIdentifier)) {
      // resolve using id

      const remoteWorlkflow = await client.get(`/workflows/${workflowIdentifier}`, { validateStatus: null });

      if (remoteWorlkflow.status === 200) {
        // resolve folder
        const category = await client.get(`/categories/${remoteWorlkflow.data['category-id']}`);
        return new Workflow({
          id: remoteWorlkflow.data.id,
          name: remoteWorlkflow.data.name,
          description: remoteWorlkflow.data.description,
          version: remoteWorlkflow.data.version,
          folder: category.data.path,
        });
      } else {
        console.warn(`Could not resolve workflow: ${workflowIdentifier}`);
        return null;
      }
    } else {
      // resolve using path

      const workflowPathSegments = workflowIdentifier.split('/');
      const name = workflowPathSegments.pop();
      const folder = workflowPathSegments.join('/');

      // determine target workflow based on name and immediate category
      const workflows = await client.get(`/workflows?conditions=name=${name}`);
      const targetWorkflow = workflows.data.link
        .map((link: { attributes: Attribute[] }) => attributesToMap(link.attributes))
        .find((linkMap: Map<string, string>) => folder.endsWith(<string>linkMap.get('categoryName')));

      if (targetWorkflow) {
        // TODO: verify categories because the check above uses folder.endsWith instead of strict equality.
        return new Workflow({
          id: targetWorkflow.get('id'),
          name: targetWorkflow.get('name'),
          description: targetWorkflow.get('description'),
          version: targetWorkflow.get('version'),
          folder,
        });
      } else {
        console.warn(`Could not resolve workflow: ${workflowIdentifier}`);
        return null;
      }
    }
  }

  /**
   * Run vRO workflow
   * @param client {AxiosInstance}
   */
  async run(client: AxiosInstance, inputs?: RunInputs | string): Promise<void> {
    const inputsMap = this.getInputsMap(inputs ? (Array.isArray(inputs) ? inputs : [inputs]) : [], []);
    console.log('Inputs:', inputsMap);
    throw new Error('Not supported yet');
  }
}
