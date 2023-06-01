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
import { attributesToMap, isUUID } from '../utils';
import { Attribute, IBaseVroObject, RunInputs, VroObject } from './base';

export type ConfigurationAttribute = {
  type: string;
  description: string;
  name?: string;
  value?: {
    [type: string]: {
      value: string;
    };
  };
};

type ConfigurationEntity = {
  name: string;
  description: string;
  id: string;
  'category-id': string;
  version: string;
  href: string;
  attributes: ConfigurationAttribute[];
};

export interface IConfiguration extends IBaseVroObject {
  objectType: 'ConfigurationElement';
  folder: string;
  attributes?: {
    [name: string]: ConfigurationAttribute;
  };
}
export class Configuration extends VroObject {
  public readonly urn: string;
  public readonly version: string;

  constructor(protected readonly objectDefinition: IConfiguration) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.folder}/${this.objectDefinition.name}`;
    this.version = this.objectDefinition.version;
    console.log(`Resolved configuration: ${this.urn}`);
  }

  get<IConfiguration>() {
    return <IConfiguration>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert configuration
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance): Promise<void> {
    // check remote configuration
    const remoteConfig = await Configuration.getRemoteConfig(client, this.objectDefinition.id);

    // Upsert configuration
    if (remoteConfig) {
      await this.updateConfiguration(client, remoteConfig);
    } else {
      await this.createConfiguration(client);
    }
  }

  /**
   * Download configuration.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string): Promise<void> {
    console.log(`Downloading configuration ${this.objectDefinition.folder}/${this.objectDefinition.name}`);

    const config = await client.get(`/configurations/${this.objectDefinition.id}`);

    this.objectDefinition.attributes = config.data.attributes.reduce(
      (acc: { [name: string]: ConfigurationAttribute }, attr: { name: string; description?: string; type: string }) => {
        acc[attr.name] = {
          type: attr.type,
          description: attr.description || '',
        };
        return acc;
      },
      {}
    );

    // make directory if it doesn't exist and write the configuration
    const configTargetPath = path.resolve(path.join(path.normalize(target), this.objectDefinition.folder));
    await fs.promises.mkdir(configTargetPath, { recursive: true });

    const configTarget = path.join(configTargetPath, `${this.objectDefinition.name}.conf.yaml`);
    console.log(`Writing configuration to ${configTarget}`);
    await fs.promises.writeFile(configTarget, yaml.dump(this.objectDefinition));

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
   * Run configuration
   * @param client {AxiosInstance}
   */
  async run(client: AxiosInstance, inputs: RunInputs): Promise<void> {
    console.log(' -> Run is not available for configurations');
  }

  /**
   * Resolve a configuration object from remote identifier.
   * @param client {AxiosInstance}
   * @param configIdentifier {string} configuration path (folder/config) or id
   * @returns the resolved configuration
   */
  static async from(client: AxiosInstance, configIdentifier: string): Promise<Configuration | null> {
    if (isUUID(configIdentifier)) {
      // resolve using id

      const remoteConfig = await Configuration.getRemoteConfig(client, configIdentifier);

      if (remoteConfig) {
        // resolve folder
        const category = await client.get(`/categories/${remoteConfig['category-id']}`);
        return new Configuration({
          objectType: 'ConfigurationElement',
          id: remoteConfig.id,
          name: remoteConfig.name,
          description: remoteConfig.description,
          version: remoteConfig.version,
          folder: category.data.path,
        });
      } else {
        console.warn(`Could not resolve configuration: ${configIdentifier}`);
        return null;
      }
    } else {
      // resolve using path

      const categoryPathSegments = configIdentifier.split('/');
      const name = categoryPathSegments.pop();
      const folder = categoryPathSegments.join('/');

      // determine target configuration based on name and immediate category
      const configs = await client.get(`/catalog/System/ConfigurationElement?conditions=name=${name}`);
      const targetConfig = configs.data.link
        .map((link: { attributes: Attribute[] }) => attributesToMap(link.attributes))
        .find((linkMap: Map<string, string>) => {
          const categoryPath = (<string>linkMap.get('category-path'))
            .split('/')
            .map((c) => c.trim())
            .join('/');
          return folder.endsWith(categoryPath);
        });

      if (targetConfig) {
        // TODO: verify categories because the check above uses folder.endsWith instead of strict equality.
        return new Configuration({
          objectType: 'ConfigurationElement',
          id: targetConfig.get('id'),
          name: targetConfig.get('name'),
          description: targetConfig.get('description'),
          version: targetConfig.get('version'),
          folder,
        });
      } else {
        console.warn(`Could not resolve configuration: ${configIdentifier}`);
        return null;
      }
    }
  }

  /**
   * Get remote configuration by id.
   * @param client {AxiosInstance}
   * @param configId {string}
   * @returns the remote configuration
   */
  static async getRemoteConfig(client: AxiosInstance, configId: string): Promise<ConfigurationEntity> {
    const remoteConfig = await client.get(`/configurations/${configId}`, { validateStatus: null });
    return remoteConfig.status === 200 ? remoteConfig.data : null;
  }

  private async updateConfiguration(client: AxiosInstance, remoteConfig: ConfigurationEntity) {
    console.log(`Updating configuration ${this.objectDefinition.folder}/${this.objectDefinition.name}`);

    let categoryId = await this.getCategoryIdForFolder(
      client,
      this.objectDefinition.folder,
      'ConfigurationElementCategory'
    );
    if (!categoryId) {
      categoryId = await this.createCategoryForFolder(
        client,
        this.objectDefinition.folder,
        'ConfigurationElementCategory'
      );
    }

    const existingAttributes = remoteConfig.attributes.reduce((map: Map<string, ConfigurationAttribute>, attr) => {
      map.set(<string>attr.name, attr);
      return map;
    }, new Map());

    const attributes = Object.entries(this.objectDefinition.attributes || {}).map(([attibuteName, attribute]) => {
      const attr: ConfigurationAttribute = {
        name: attibuteName,
        type: attribute.type,
        description: attribute.description,
      };
      // preserve existing attribute's value
      const existingAttribute = existingAttributes.get(attibuteName);
      if (existingAttribute?.type === attribute.type && existingAttribute.value) {
        attr.value = existingAttribute.value;
      }
      return attr;
    });

    await client.put(`/configurations/${this.objectDefinition.id}`, {
      id: this.objectDefinition.id,
      description: this.objectDefinition.description,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      'category-id': categoryId,
      attributes,
    });
    console.log(chalk.green(` -> Done`));
  }

  private async createConfiguration(client: AxiosInstance) {
    console.log(`Creating configuration ${this.objectDefinition.folder}/${this.objectDefinition.name}`);

    let categoryId = await this.getCategoryIdForFolder(
      client,
      this.objectDefinition.folder,
      'ConfigurationElementCategory'
    );
    if (!categoryId) {
      categoryId = await this.createCategoryForFolder(
        client,
        this.objectDefinition.folder,
        'ConfigurationElementCategory'
      );
    }

    const attributes = Object.entries(this.objectDefinition.attributes || {}).map(([attibuteName, attribute]) => ({
      name: attibuteName,
      type: attribute.type,
      description: attribute.description,
    }));

    await client.post(`/configurations`, {
      id: this.objectDefinition.id,
      description: this.objectDefinition.description,
      name: this.objectDefinition.name,
      version: this.objectDefinition.version,
      'category-id': categoryId,
      attributes,
    });
    console.log(chalk.green(` -> Done`));
  }
}
