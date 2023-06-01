

import path from 'path';
import fs from 'fs-extra';
import chalk from 'chalk';
import { v5 as uuidv5 } from 'uuid';
import { AxiosInstance } from 'axios';

import nconf from '../config';
import { IBaseVroObject, RunInputs, VroObject } from './base';
import { Workflow } from './workflows';
import { BaseAction } from './actions';
import { Configuration } from './configurations';

export interface IVroPackage extends IBaseVroObject {
  content: Array<VroObject>;
}

export class VroPackage extends VroObject {
  public readonly urn: string;
  public readonly version: string;

  constructor(protected readonly objectDefinition: IVroPackage) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.name}`;
    this.version = this.objectDefinition.version;
    console.log(`Resolved package: ${this.urn}`);

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
  }

  /**
   * Generate a hashed ID from the package name.
   * @param name {string}
   * @returns the id.
   */
  static getObjectId(name: string) {
    return uuidv5(name, nconf.get('ID_NAMESPACE'));
  }

  get<IVroPackage>() {
    return <IVroPackage>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert package
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance) {
    if (!this.objectDefinition.content) {
      throw new Error(`Missing package content`);
    }

    const payload = {
      description: this.objectDefinition.description,
      items: {
        workflows: await this.getRemoteIds(client, Workflow),
        actions: await this.getRemoteIds(client, BaseAction),
        environments: [],
        resources: [],
        configurations: await this.getRemoteIds(client, Configuration),
        'policy-templates': [],
        'workflow-tokens': [],
      },
      rebuild: false,
    };

    if (nconf.get('debug')) {
      console.log('Package payload', payload);
    }

    // Upsert package
    if (await this.remotePackageExists(client)) {
      console.log(`Updating package ${this.urn}`);
      await client.patch(`/packages/${this.objectDefinition.name}`, payload);
    } else {
      console.log(`Creating package ${this.urn}`);
      await client.put(`/packages/${this.objectDefinition.name}`, payload);
    }

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Download pacakge.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string) {
    console.log(`Downloading package ${this.urn}`);

    const packageContent = await client.get(`/packages/${this.objectDefinition.name}`, {
      params: {
        exportConfigurationAttributeValues: false,
        exportConfigSecureStringAttributeValues: false,
        exportGlobalTags: false,
        exportAsZip: false,
        allowedOperations: 'vfe',
      },
      headers: { Accept: 'application/zip,*/*' }, // this is important if we want to get the actual binary
      responseType: 'arraybuffer',
    });

    // make directory if it doesn't exist and write the package
    const packageTargetPath = path.resolve(path.normalize(target));
    await fs.promises.mkdir(packageTargetPath, { recursive: true });

    const packageTarget = path.join(packageTargetPath, `${this.objectDefinition.name}.package`);
    console.log(`Writing package to ${packageTarget}`);
    await fs.writeFile(packageTarget, packageContent.data);
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta() {
    return JSON.stringify(this.objectDefinition, null, 2);
  }

  /**
   * Run package
   * @param client {AxiosInstance}
   */
  async run(client: AxiosInstance, inputs: RunInputs): Promise<void> {
    console.log(chalk.red(' -> Run is not available for packages'));
  }

  /**
   * Resolve a vRO package object from remote identifier.
   * @param client {AxiosInstance}
   * @param packageIdentifier {string} package name
   * @returns the resolved package
   */
  static async from(client: AxiosInstance, packageIdentifier: string): Promise<VroPackage | null> {
    const targetPackage = await client.get(`/packages/${packageIdentifier}`, { validateStatus: null });

    if (targetPackage.status === 200) {
      return new VroPackage({
        id: targetPackage.data.id,
        name: targetPackage.data.name,
        description: targetPackage.data.description,
        version: 'none',
        content: [],
      });
    } else {
      console.warn(chalk.red(` -> Could not resolve package: ${packageIdentifier}`));
      return null;
    }
  }

  /**
   * Checks if a remote package exists.
   * @param client {AxiosInstance}
   * @returns true if the package exists
   */
  private async remotePackageExists(client: AxiosInstance) {
    const remoteActionContents = await client.head(`/packages/${this.objectDefinition.name}`, { validateStatus: null });
    return remoteActionContents.status === 200;
  }

  /**
   * Resolve remote IDs of objects by type
   */
  private async getRemoteIds(client: AxiosInstance, ctor: typeof VroObject): Promise<(string | null)[]> {
    const filteredContent = this.objectDefinition.content.filter((obj) => obj instanceof ctor);

    const promises = filteredContent.map(async (item) => {
      const resolvedItem = await (<typeof Workflow | typeof BaseAction | typeof Configuration>ctor).from(
        client,
        item.urn
      );

      if (!resolvedItem) {
        throw new Error(`Could not resovle object "${item.urn}". Maybe you need to upload it first.`);
      }
      return resolvedItem.get<IBaseVroObject>().id;
    });

    const remoteIds = await Promise.all(promises);
    console.log(`Resolved ${remoteIds.length} remote objects of type: ${ctor.name}`);
    return remoteIds;
  }
}
