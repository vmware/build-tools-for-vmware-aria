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
import yaml from 'js-yaml';
import chalk from 'chalk';
import { AxiosInstance } from 'axios';
import { isUUID } from '../utils';
import { IBaseVraObject, VraObject } from './base';

const BLUEPRINT_API_VERSION = '2019-09-12';
const IAAS_API_VERSION = '2021-07-15';

type CloudTempateSchema = {
  formatVersion: number;
  inputs: {
    [name: string]: unknown;
  };
  resources: {
    [name: string]: unknown;
  };
};

export interface ICloudTemplate extends IBaseVraObject {
  objectType: 'VMwareCloudTemplate';
  description: string;
  requestScopeOrg: boolean;
  project: string;
  content: CloudTempateSchema;
}

type CloudTemplateResponse = {
  content: string;
  description: string;
  id: string;
  name: string;
  projectId: string;
  projectName: string;
  requestScopeOrg: boolean;
};

export class CloudTemplate extends VraObject {
  public readonly urn: string;

  constructor(protected readonly objectDefinition: ICloudTemplate) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.project}/${this.objectDefinition.name}`;
    console.log(`Resolved cloud template: ${this.urn}`);
  }

  get<ICloudTemplate>() {
    return <ICloudTemplate>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert cloud template
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance): Promise<void> {
    const remoteCloudTemplate = await CloudTemplate.getRemoteCloudTemplate(client, this.objectDefinition.name);

    // resolve references
    const projectId = await CloudTemplate.resolveReference(
      client,
      this.objectDefinition.project,
      'id',
      '/iaas/api/projects',
      IAAS_API_VERSION
    );

    const payload = {
      content: yaml.dump(this.objectDefinition.content),
      name: this.objectDefinition.name,
      description: this.objectDefinition.description,
      projectId: projectId,
      requestScopeOrg: this.objectDefinition.requestScopeOrg,
    };

    if (remoteCloudTemplate) {
      console.log(`Updating cloud template ${this.objectDefinition.name}`);
      await client.put(
        `/blueprint/api/blueprints/${remoteCloudTemplate.id}?apiVersion=${BLUEPRINT_API_VERSION}`,
        payload
      );
    } else {
      console.log(`Creating cloud template ${this.objectDefinition.name}`);
      await client.post(`/blueprint/api/blueprints?apiVersion=${BLUEPRINT_API_VERSION}`, payload);
    }

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Download cloud template.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string): Promise<void> {
    console.log(`Downloading cloud template ${this.objectDefinition.name}`);

    // make directory if it doesn't exist and write the configuration
    const cloudTemplateTargetPath = path.resolve(path.normalize(target));
    await fs.promises.mkdir(cloudTemplateTargetPath, { recursive: true });

    const cloudTemplateTarget = path.join(cloudTemplateTargetPath, `${this.objectDefinition.name}.vct.yaml`);
    console.log(`Writing cloud template to ${cloudTemplateTarget}`);
    await fs.promises.writeFile(cloudTemplateTarget, yaml.dump(this.objectDefinition));

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta(): string {
    return JSON.stringify(this.objectDefinition, null, 2);
  }

  /**
   * Resolve a cloud template object from remote identifier.
   * @param client {AxiosInstance}
   * @param cloudTemplateIdentifier {string} cloud template name or id
   * @returns the resolved cloud template
   */
  static async from(client: AxiosInstance, cloudTemplateIdentifier: string): Promise<CloudTemplate | null> {
    const cloudTemplate = await CloudTemplate.getRemoteCloudTemplate(client, cloudTemplateIdentifier);
    if (cloudTemplate) {
      return new CloudTemplate({
        objectType: 'VMwareCloudTemplate',
        name: cloudTemplate.name,
        description: cloudTemplate.description,
        requestScopeOrg: cloudTemplate.requestScopeOrg,
        project: await CloudTemplate.getReference(
          client,
          `/iaas/api/projects/${cloudTemplate.projectId}?apiVersion=${IAAS_API_VERSION}`,
          'name'
        ),
        content: <CloudTempateSchema>yaml.load(cloudTemplate.content),
      });
    } else {
      console.warn(`Could not resolve cloud template: ${cloudTemplateIdentifier}`);
      return null;
    }
  }

  /**
   * Retrieve remote cloud template.
   * @param client {AxiosInstance}
   * @param cloudTemplateIdentifier {string}
   * @returns remote cloud template
   */
  static async getRemoteCloudTemplate(
    client: AxiosInstance,
    cloudTemplateIdentifier: string
  ): Promise<CloudTemplateResponse | null> {
    if (isUUID(cloudTemplateIdentifier)) {
      const remoteTemplate = await client.get(
        `/blueprint/api/blueprints/${cloudTemplateIdentifier}?apiVersion=${BLUEPRINT_API_VERSION}`,
        { validateStatus: null }
      );
      return remoteTemplate.status === 200 ? remoteTemplate.data : null;
    } else {
      const params = new URLSearchParams({
        name: cloudTemplateIdentifier,
        apiVersion: BLUEPRINT_API_VERSION,
      });
      const cloudTemplateSearch = await client.get(`/blueprint/api/blueprints?${params.toString()}`);

      if (cloudTemplateSearch.data.numberOfElements >= 1) {
        const cloudTemplate = await client.get(
          `/blueprint/api/blueprints/${cloudTemplateSearch.data.content[0].id}?apiVersion=${BLUEPRINT_API_VERSION}`
        );
        return cloudTemplate.data;
      }
      return null;
    }
  }
}
