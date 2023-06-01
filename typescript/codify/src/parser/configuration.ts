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


import fs from 'fs-extra';
import yaml from 'js-yaml';
import path from 'path';
import nconf from '../config';
import { v5 as uuidv5 } from 'uuid';
import { Configuration, ConfigurationAttribute, IConfiguration } from '../model/configurations';
import { IParser } from './parser';

type VroObjectType = 'configuration' | 'unknown';

export class ConfigurationParser implements IParser {

  private contents!: IConfiguration;

  constructor(private readonly fileName: string) { }

  async parse() {
    console.log(`Parsing Configuration file: ${this.fileName}`);
    try {
      this.contents = <IConfiguration>yaml.load(await fs.readFile(this.fileName, 'utf-8'));
    } catch (err) {
      console.warn(`Could not parse file: ${this.fileName}`);
    }
  }

  getObjectDefinition() {

    // determine vRO object type
    const objectType = this.getObjectType();

    let objectDefinition;

    switch (objectType) {

      case 'configuration':
        objectDefinition = <IConfiguration>{
          objectType: this.contents.objectType,
          id: this.getObjectId(),
          name: this.getObjectName(),
          description: this.getObjctDescription(),
          version: this.getObjectVersion(),
          folder: this.getObjectFolder(),
          attributes: this.getObjectAttributes(),
        };
        return new Configuration(objectDefinition);

      case 'unknown':
        return null;

      default:
        return null;

    }

  }

  private getObjectType(): VroObjectType {
    if (this.contents && this.contents.objectType === 'ConfigurationElement') {
      return 'configuration'
    } else {
      return 'unknown';
    }
  }

  private getObjectName(): string {
    return this.contents.name || (this.fileName.endsWith('.yaml')
      ? path.basename(this.fileName, '.conf.yaml')
      : path.basename(this.fileName, '.conf.yml'));
  }

  private getObjectFolder(): string {
    const defaultFolder = 'ACoE';
    const dirname = path.dirname(path.resolve(this.fileName));
    if (this.contents.folder) {
      return this.contents.folder
    } else if (dirname === '.' || dirname === '/') {
      return defaultFolder;
    } else {
      // TODO: return proper folder structure
      return dirname.split(path.sep).pop() || defaultFolder;
    }
  }

  private getObjectVersion(): string {
    // TODO: change this to "auto" for auto-versioning
    return this.contents.version || '1.0.0';
  }

  private getObjectId(): string | null {
    return this.contents.id || uuidv5(`${this.getObjectFolder()}/${this.getObjectName()}`, nconf.get('ID_NAMESPACE'));
  }

  private getObjctDescription(): string {
    return this.contents.description || ''
  }

  private getObjectAttributes(): { [name: string]: ConfigurationAttribute } {
    return this.contents.attributes || {}
  }

}
