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
import { ISubscription, Subscription } from '../model/subscriptions';

type VraObjectType = 'subscription' | 'unknown';

export class SubscriptionParser implements IParser {

  private contents!: ISubscription;

  constructor(private readonly fileName: string) { }

  async parse() {
    console.log(`Parsing Subscription file: ${this.fileName}`);
    try {
      this.contents = <ISubscription>yaml.load(await fs.readFile(this.fileName, 'utf-8'));
    } catch (err) {
      console.warn(`Could not parse file: ${this.fileName}`);
    }
  }

  getObjectDefinition() {

    // determine vRO object type
    const objectType = this.getObjectType();

    let objectDefinition;

    switch (objectType) {

      case 'subscription':
        objectDefinition = <ISubscription>{
          ...this.contents,
          objectType: this.contents.objectType,
          id: this.getObjectId(),
          name: this.getObjectName(),
          description: this.getObjctDescription(),
        };
        return new Subscription(objectDefinition);

      case 'unknown':
        return null;

      default:
        return null;

    }

  }

  private getObjectType(): VraObjectType {
    if (this.contents && this.contents.objectType === 'Subscription') {
      return 'subscription'
    } else {
      return 'unknown';
    }
  }

  private getObjectName(): string {
    return this.contents.name || (this.fileName.endsWith('.yaml')
      ? path.basename(this.fileName, '.sub.yaml')
      : path.basename(this.fileName, '.sub.yml'));
  }

  private getObjectId(): string | null {
    return this.contents.id || null;
  }

  private getObjctDescription(): string {
    return this.contents.description || ''
  }

}
