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
import { CloudTemplate, ICloudTemplate } from '../model/cloudtemplates';

type VraObjectType = 'cloudtemplate' | 'unknown';

export class CloudTemplateParser implements IParser {

  private contents!: ICloudTemplate;

  constructor(private readonly fileName: string) { }

  async parse() {
    console.log(`Parsing Cloud template file: ${this.fileName}`);
    try {
      this.contents = <ICloudTemplate>yaml.load(await fs.readFile(this.fileName, 'utf-8'));
    } catch (err) {
      console.warn(`Could not parse file: ${this.fileName}`);
    }
  }

  getObjectDefinition() {

    // determine vRO object type
    const objectType = this.getObjectType();

    let objectDefinition;

    switch (objectType) {

      case 'cloudtemplate':
        objectDefinition = <ICloudTemplate>{
          objectType: this.contents.objectType,
          name: this.getObjectName(),
          description: this.getObjctDescription(),
          project: this.contents.project,
          requestScopeOrg: this.contents.requestScopeOrg,
          content: this.contents.content
        };
        return new CloudTemplate(objectDefinition);

      case 'unknown':
        return null;

      default:
        return null;

    }

  }

  private getObjectType(): VraObjectType {
    if (this.contents && this.contents.objectType === 'VMwareCloudTemplate') {
      return 'cloudtemplate'
    } else {
      return 'unknown';
    }
  }

  private getObjectName(): string {
    return this.contents.name || (this.fileName.endsWith('.yaml')
      ? path.basename(this.fileName, '.vct.yaml')
      : path.basename(this.fileName, '.vct.yml'));
  }

  private getObjctDescription(): string {
    return this.contents.description || ''
  }

}
