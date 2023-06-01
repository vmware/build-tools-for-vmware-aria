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


import nconf from '../config';
import util from 'util';
import chalk from 'chalk';
import { AxiosInstance } from 'axios';
import { attributesToMap } from '../utils';

export interface IBaseVroObject {
  id: string;
  name: string;
  description: string;
  version: string;
}

export interface IBaseAbxObject {
  id: string;
  name: string;
  description: string;
}

export interface IBaseVraObject {
  name: string;
}

export type Attribute = {
  name: string;
  value: string;
};

type CategoryType = 'WorkflowCategory' | 'ConfigurationElementCategory';

/**
 * The run inputs is an array of strings, each followig the format
 * "name:value". This serialization is needed to allow passing of
 * multiple inputs from command line. The value can be further processed and
 * cast to an appropriate type based on the input schema of the vRO object.
 */
export type RunInputs = string[];

/**
 * The input schema defines the structure of the inputs in an object definition.
 * The input schema can be used to create an input map with proper type casting.
 */
export type Input = {
  name: string;
  type: string;
  description?: string;
  value?: string;
};

/**
 * Each codify object must implement the ICodifyObject interface
 */
export interface ICodifyObject {
  urn: string;
  upload(client: AxiosInstance): Promise<void>;
  download(client: AxiosInstance, target: string): Promise<void>;
}

/**
 * vRO Object base class. This class serves as a base for all
 * vRO objects including actions, simple Polyglot actions,
 * workflows, configurations, resources, policies, etc.
 */
export abstract class VroObject implements ICodifyObject {
  public abstract readonly urn: string;
  public abstract readonly version: string;
  protected abstract objectDefinition: IBaseVroObject;

  constructor(objectDefinition: IBaseVroObject) {
    if (nconf.get('debug')) {
      console.log('Model', util.inspect(objectDefinition, false, null, true));
    }
  }

  abstract get<T>(): T;
  abstract upload(client: AxiosInstance): Promise<void>;
  abstract download(client: AxiosInstance, target: string): Promise<void>;
  abstract getMeta(): string | null;
  abstract run(client: AxiosInstance, inputs?: RunInputs | string): Promise<void>;

  protected getInputsMap(runInputs: RunInputs, schema: Input[]) {
    const schemaMap = schema.reduce<{ [key: string]: string }>((acc, prev) => {
      acc[prev.name] = prev.type;
      return acc;
    }, {});

    return runInputs.reduce<{ [key: string]: string | number | boolean }>((acc, prev) => {
      if (prev.includes(':')) {
        const parts = prev.split(':');
        acc[parts[0]] = this.castValue(schemaMap[parts[0]], parts[0], parts[1]);
      }
      return acc;
    }, {});
  }

  private castValue(type: string, name: string, value: string): string | number | boolean {
    switch (type) {
      case 'string':
        return value.toString();

      case 'number':
        return parseFloat(value);

      case 'boolean':
        return value === 'true' || value === '1' || value == 'yes';

      case 'Any':
        return value;

      case undefined:
        console.warn(`Input schema does not have input named "${name}"`);
        return value;

      default:
        console.warn(`Unsupported cast type "${type}" for value "${value}"`);
        return value;
    }
  }

  /**
   * Resolve workflow category id for the folder in the object definition (if exists).
   * @param client {AxiosInstance}
   * @param folder {string}
   * @param categoryType {CategoryType}
   * @returns the resolved ID if category exists
   */
  protected async getCategoryIdForFolder(
    client: AxiosInstance,
    folder: string,
    categoryType: CategoryType
  ): Promise<string | null> {
    const categoryName = folder.split('/').pop();

    const categories = await client.get(`/catalog/System/${categoryType}?conditions=name=${categoryName}`);

    const category = categories.data.link
      .map((link: { attributes: Attribute[] }) => attributesToMap(link.attributes))
      .find((linkMap: Map<string, string>) => linkMap.get('displayName') === folder);

    return category ? category.get('id') : null;
  }

  /**
   * Create a category tree for the folder in the object definition.
   * @param client {AxiosInstance}
   * @param folder {string}
   * @param categoryType {CategoryType}
   * @returns the created category ID for the most distant child
   */
  protected async createCategoryForFolder(
    client: AxiosInstance,
    folder: string,
    categoryType: CategoryType
  ): Promise<string> {
    console.log(`Creating category ${folder}`);

    // build category tree
    const allCategories = (await client.get(`/catalog/System/${categoryType}`)).data.link
      .map((link: { attributes: Attribute[] }) => attributesToMap(link.attributes))
      .reduce((acc: { [key: string]: string }, prev: Map<string, string>) => {
        acc[<string>prev.get('displayName')] = <string>prev.get('id');
        return acc;
      }, {});

    // determine the categories that need to be created
    const categoryNames = Object.keys(allCategories).sort().reverse();
    const nearestParentCategoryName = categoryNames.find((c) => folder.startsWith(`${c}/`));
    let categoriesToCreate = [];
    if (nearestParentCategoryName) {
      // exclude the parents that already exist
      categoriesToCreate = folder.replace(`${nearestParentCategoryName}/`, '').split('/');
    } else {
      // no parent exist to accommodate the category we want to create
      categoriesToCreate = folder.split('/');
    }

    // create the category tree
    let nearestParentCategoryId = nearestParentCategoryName ? allCategories[nearestParentCategoryName] : null;
    for (let i = 0; i < categoriesToCreate.length; i++) {
      const categoryName = categoriesToCreate[i];
      const payload = { type: categoryType, name: categoryName, description: '' };

      if (!nearestParentCategoryId) {
        // we need to add a root category
        const rootCategory = await client.post('/categories', payload);
        nearestParentCategoryId = rootCategory.data.id;
        console.debug(`Created root category ${categoryName} -> ${nearestParentCategoryId}`);
      } else {
        // we need to create a child category
        const childCategory = await client.post(`/categories/${nearestParentCategoryId}`, payload);
        nearestParentCategoryId = childCategory.data.id;
        console.debug(`Created child category ${categoryName} -> ${nearestParentCategoryId}`);
      }
    }

    return nearestParentCategoryId;
  }
}

/**
 * vRA Object base class. This class serves as a base for all
 * vRA objects including blueprints, catalog items, custom resources
 * resource actions, simple ABX actions, etc.
 */
export abstract class VraObject implements ICodifyObject {
  public abstract readonly urn: string;
  protected abstract objectDefinition: IBaseVraObject;

  constructor(objectDefinition: IBaseVraObject) {
    if (nconf.get('debug')) {
      console.log('Model', util.inspect(objectDefinition, false, null, true));
    }
  }

  abstract get<T>(): T;
  abstract upload(client: AxiosInstance): Promise<void>;
  abstract download(client: AxiosInstance, target: string): Promise<void>;
  abstract getMeta(): string | null;

  static async getReference(client: AxiosInstance, resourceUrl: string, prop: string): Promise<string> {
    const resource = await client.get(resourceUrl);
    return `ref:${prop}:${resource.data[prop]}`;
  }

  static async resolveReference(
    client: AxiosInstance,
    reference: string,
    prop: string,
    queryUrl: string,
    apiVersion: string
  ): Promise<string | null> {
    const params = new URLSearchParams({
      $filter: `(${reference.split(':')[1]} eq '${reference.split(':')[2]}')`,
      apiVersion,
    });

    const query = await client.get(`${queryUrl}?${params.toString()}`);
    if (query.data.content.length) {
      return query.data.content[0][prop];
    }

    console.error(
      chalk.red(
        `Could not resolve property "${prop}" for reference "${reference}" using GET "${queryUrl}?${params.toString()}"`
      )
    );
    return null;
  }
}
