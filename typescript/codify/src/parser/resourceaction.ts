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
import { IParser } from './parser';
import { IResourceAction, ResourceAction } from '../model/resourceactions';

type VraObjectType = 'resourceaction' | 'unknown';

export class ResourceActionParser implements IParser {

  private contents!: IResourceAction;

  constructor(private readonly fileName: string) { }

  async parse() {
    console.log(`Parsing Resource Action file: ${this.fileName}`);
    try {
      this.contents = <IResourceAction>yaml.load(await fs.readFile(this.fileName, 'utf-8'));
    } catch (err) {
      console.warn(`Could not parse file: ${this.fileName}`);
    }
  }

  getObjectDefinition() {

    // determine vRO object type
    const objectType = this.getObjectType();

    let objectDefinition;

    switch (objectType) {

      case 'resourceaction':
        objectDefinition = <IResourceAction>{
          ...this.contents,
          objectType: this.contents.objectType,
          project: this.contents.project,
          id: this.getObjectId(),
          name: this.getObjectName(),
          description: this.getObjctDescription(),
        };
        return new ResourceAction(objectDefinition);

      case 'unknown':
        return null;

      default:
        return null;

    }

  }

  private getObjectType(): VraObjectType {
    if (this.contents && this.contents.objectType === 'ResourceAction') {
      return 'resourceaction'
    } else {
      return 'unknown';
    }
  }

  private getObjectName(): string {
    return this.contents.name || (this.fileName.endsWith('.yaml')
      ? path.basename(this.fileName, '.day2.yaml')
      : path.basename(this.fileName, '.day2.yml'));
  }

  private getObjectId(): string | null {
    return this.contents.id || null;
  }

  private getObjctDescription(): string {
    return this.contents.description || ''
  }

}
