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
import path from 'path';
import yaml from 'js-yaml';
import nconf from '../config';
import { v5 as uuidv5 } from 'uuid';
import { IWorkflow, Workflow, WorkflowContent, WorkflowForm } from '../model/workflows';
import { IParser } from './parser';

type VroObjectType = 'workflow' | 'unknown';
type WorkflowMeta = {
  id: string;
  name: string;
  description: string;
  version: string;
  folder: string;
  content: WorkflowContent;
  presentation: WorkflowForm;
};

export class WorkflowParser implements IParser {
  private contents: Partial<WorkflowMeta> = {};
  private nativeContents: Buffer = Buffer.alloc(0);

  constructor(private readonly fileName: string) {}

  async parse() {
    console.log(`Parsing Workflow file: ${this.fileName}`);
    try {
      try {
        this.contents = await fs.readJSON(this.fileName, 'utf-8');
      } catch (err) {
        // in case this is not a JSON file rather YAML
        if (err instanceof SyntaxError) {
          const contents = await fs.readFile(this.fileName, 'utf-8');
          this.contents = <WorkflowMeta>yaml.load(contents);
        } else {
          throw err;
        }
      }

      // native content
      const ncf = path.parse(this.fileName);
      const nativeContentFilePath = path.join(ncf.dir, `${ncf.name}`);
      if (fs.existsSync(nativeContentFilePath)) {
        this.nativeContents = await fs.readFile(nativeContentFilePath);
      }
    } catch (err) {
      console.warn(`Could not parse file: ${this.fileName}`);
    }
  }

  getObjectDefinition() {
    // determine vRO object type
    const objectType = this.getObjectType();

    let objectDefinition;

    switch (objectType) {
      case 'workflow':
        objectDefinition = <IWorkflow>{
          id: this.getObjectId(),
          name: this.getObjectName(),
          description: this.getObjctDescription(),
          version: this.getObjectVersion(),
          folder: this.getObjectFolder(),
          content: this.contents.content,
          nativeContent: this.nativeContents,
          presentation: this.getObjectPresentation(),
        };
        return new Workflow(objectDefinition);

      case 'unknown':
        return null;

      default:
        return null;
    }
  }

  private getObjectType(): VroObjectType {
    if (this.contents.content && this.contents.content['object-name'] === 'workflow:name=generic') {
      return 'workflow';
    } else {
      return 'unknown';
    }
  }

  private getObjectName(): string {
    return this.contents.name || path.basename(this.fileName, '.workflow.meta');
  }

  private getObjectFolder(): string {
    const defaultFolder = 'ACoE';
    const dirname = path.dirname(path.resolve(this.fileName));
    if (this.contents.folder) {
      return this.contents.folder;
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
    return this.contents.description || '';
  }

  private getObjectPresentation(): WorkflowForm | undefined {
    return this.contents.presentation;
  }
}
