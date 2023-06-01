

import fs from 'fs-extra';
import doctrine from 'doctrine';
import path from 'path';
import nconf from '../config';
import { v5 as uuidv5 } from 'uuid';
import { Action, IBaseAction, IPolyglotAction, PolyglotAction } from '../model/actions';
import { generateModuleName, PolyglotActionRuntime } from '..';
import { IParser } from './parser';
import { AbxAction, AbxActionRuntime, IAbxAction } from '../model/abx';
import { Input } from '../model';

// extend doctrine Annotation
type TransformedAnnotation = {
  description: string
  tags: {
    [key: string]: doctrine.Tag | doctrine.Tag[]
  }
}

type ActionObjectType = 'polyglot' | 'action' | 'abx' | 'unknown';

enum VRO_OBJECT_ANNOTATIONS {
  TYPE = '_type',
  NAME = '_name',
  MODULE = '_module',
  VERSION = '_version',
  ID = '_id',
  INPUT = '_input',
  OUTPUT = '_output',
  ENTRYPOINT = '_entrypoint',
  TIMEOUT = '_timeout',
  MEMORY = '_memory',
}

enum ABX_OBJECT_ANNOTATIONS {
  TYPE = '_type',
  PROJECT = '_project',
  NAME = '_name',
  ID = '_id',
  INPUT = '_input',
  ENTRYPOINT = '_entrypoint',
  TIMEOUT = '_timeout',
  MEMORY = '_memory',
  DEPENDENCIES = '_dependencies',
  SHARED = '_shared',
  PROVIDER = '_provider',
  CONFIGURATION = '_configuration',
}

export class ActionParser implements IParser {

  private contents = '';
  private objectAnnotation: TransformedAnnotation | null = null;

  constructor(private readonly fileName: string) { }

  async parse() {
    console.log(`Parsing action file: ${this.fileName}`);
    this.contents = await fs.promises.readFile(this.fileName, 'utf-8');
    const annotations = ActionParser.extractAnnotations(this.contents);
    this.objectAnnotation = annotations.find(a => a.tags['vro'] || a.tags['abx']) || null;
  }

  static extractAnnotations(content: string) {
    const jsdocRegex = /\/\*\*\s*\n([^*]|(\*(?!\/)))*\*\//g
    const matches = content.matchAll(jsdocRegex);
    const comments = [];
    for (const match of matches) {
      const parsedComment = doctrine.parse(match[0], { unwrap: true });
      if (parsedComment) {
        comments.push(ActionParser.transformAnnotation(parsedComment));
      }
    }
    return comments;
  }

  static transformAnnotation(annotation: doctrine.Annotation): TransformedAnnotation {

    const tagMap = annotation.tags.reduce((acc: { [key: string]: doctrine.Tag | doctrine.Tag[] }, tag) => {
      if (acc[tag.title] && !Array.isArray(acc[tag.title])) {
        const currentElement = <doctrine.Tag>acc[tag.title];
        acc[tag.title] = [currentElement];
        (<doctrine.Tag[]>acc[tag.title]).push(tag)
      } else if (acc[tag.title] && Array.isArray(acc[tag.title])) {
        (<doctrine.Tag[]>acc[tag.title]).push(tag)
      } else {
        acc[tag.title] = tag;
      }
      return acc
    }, {});

    return {
      ...annotation,
      tags: tagMap
    };

  }

  getObjectDefinition() {
    if (!this.objectAnnotation) {
      return null;
    }

    // determine object type
    const objectType = this.getObjectType(this.objectAnnotation);

    let actionDefinition;

    switch (objectType) {

      case 'polyglot':
        actionDefinition = <IPolyglotAction>{
          id: this.getVroObjectId(this.objectAnnotation),
          description: this.objectAnnotation.description,
          name: this.getVroObjectName(this.objectAnnotation),
          module: this.getVroObjectModule(this.objectAnnotation),
          content: this.contents,
          version: this.getVroObjectVersion(this.objectAnnotation),
          inputs: this.getVroObjectInputs(this.objectAnnotation),
          output: this.getVroObjectOutput(this.objectAnnotation),
          // polyglot-specific properties
          entrypoint: this.getPolyglotObjectEntrypoint(this.objectAnnotation),
          timeout: this.getPolyglotObjectTimeout(this.objectAnnotation),
          memory: this.getPolyglotObjectMemory(this.objectAnnotation),
          runtime: this.getPolyglotObjectRuntime(),
        };
        return new PolyglotAction(actionDefinition);

      case 'action':
        actionDefinition = <IBaseAction>{
          id: this.getVroObjectId(this.objectAnnotation),
          description: this.objectAnnotation.description,
          name: this.getVroObjectName(this.objectAnnotation),
          module: this.getVroObjectModule(this.objectAnnotation),
          content: this.contents,
          version: this.getVroObjectVersion(this.objectAnnotation),
          inputs: this.getVroObjectInputs(this.objectAnnotation),
          output: this.getVroObjectOutput(this.objectAnnotation),
        };
        return new Action(actionDefinition);

      case 'abx':
        actionDefinition = <IAbxAction>{
          id: this.getAbxObjectId(this.objectAnnotation),
          project: this.getAbxObjectProject(this.objectAnnotation),
          description: this.objectAnnotation.description,
          name: this.getAbxObjectName(this.objectAnnotation),
          content: this.contents,
          inputs: this.getAbxObjectInputs(this.objectAnnotation),
          entrypoint: this.getAbxObjectEntrypoint(this.objectAnnotation),
          timeout: this.getAbxObjectTimeout(this.objectAnnotation),
          memory: this.getAbxObjectMemory(this.objectAnnotation),
          runtime: this.getAbxObjectRuntime(),
          dependencies: this.getAbxObjectDependencies(this.objectAnnotation),
          shared: this.getAbxObjectSharedSetting(this.objectAnnotation),
          provider: this.getAbxObjectProvider(this.objectAnnotation),
          configuration: this.getAbxObjectConfiguration(this.objectAnnotation),
        }
        return new AbxAction(actionDefinition);

      case 'unknown':
        return null;

      default:
        return null;

    }

  }

  /**
   * Infer Polyglot runtime based on file extension.
   * @returns {PolyglotActionRuntime}
   */
  private getPolyglotObjectRuntime(): PolyglotActionRuntime {
    if (this.fileName.endsWith('.js')) {
      return 'node:14';
    } else if (this.fileName.endsWith('.py')) {
      return 'python:3.7';
    } else if (this.fileName.endsWith('.ps1')) {
      return 'powercli:12-powershell-7.1';
    } else {
      return 'unknown'
    }
  }

  /**
   * Infer ABX runtime based on file extension.
   * @returns {AbxActionRuntime}
   */
  private getAbxObjectRuntime(): AbxActionRuntime {
    if (this.fileName.endsWith('.js')) {
      return 'nodejs';
    } else if (this.fileName.endsWith('.py')) {
      return 'python';
    } else if (this.fileName.endsWith('.ps1')) {
      return 'powershell';
    } else {
      return 'unknown'
    }
  }

  /**
   * Determine object type based on the annotations contained in the code.
   * The object type determines how to extract the metadata.
   * @param annotation {TransformedAnnotatio}
   * @returns {ActionObjectType}
   */
  private getObjectType(annotation: TransformedAnnotation): ActionObjectType {
    const vroTagsValue: doctrine.Tag[] = annotation.tags.vro
      ? Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro]
      : [];
    const vroObjectTypeTag = vroTagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.TYPE));

    const abxTagsValue: doctrine.Tag[] = annotation.tags.abx
      ? Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx]
      : [];
    const abxObjectTypeTag = abxTagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.TYPE));

    if (vroObjectTypeTag) {
      return <ActionObjectType>(vroObjectTypeTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.TYPE, '').trim();
    } else if (abxObjectTypeTag) {
      return <ActionObjectType>(abxObjectTypeTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.TYPE, '').trim();
    }

    return 'unknown';
  }

  /**
   * Infer the vRO object name. The name defined in the annotation
   * takes precedence before the file name.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectName(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectNameTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.NAME));
    if (!objectNameTag) {
      return path.basename(this.fileName, `.${this.fileName.split('.').pop()}`);
    } else {
      return (objectNameTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.NAME, '').trim();
    }
  }

  /**
   * Infer the ABX object name. The name defined in the annotation
   * takes precedence before the file name.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectName(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectNameTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.NAME));
    if (!objectNameTag) {
      return path.basename(this.fileName, `.${this.fileName.split('.').pop()}`);
    } else {
      return (objectNameTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.NAME, '').trim();
    }
  }

  /**
   * Infer the vRO object module.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectModule(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectModuleTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.MODULE));
    if (!objectModuleTag) {
      // infer this from the parent directory of the file
      const dirname = path.dirname(path.resolve(this.fileName));
      const defaultModule = 'com.vmware.acoe.temp';
      if (dirname === '.' || dirname === '/') {
        return defaultModule;
      } else {
        return generateModuleName(dirname.split(path.sep).pop() || defaultModule);
      }
    } else {
      return (objectModuleTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.MODULE, '').trim();
    }
  }

  /**
   * Infer the ABX object module.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectProject(annotation: TransformedAnnotation): string | null {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectModuleTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.PROJECT));
    if (!objectModuleTag) {
      return null;
    } else {
      return (objectModuleTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.PROJECT, '').trim();
    }
  }

  /**
   * Infer the vRO object version.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectVersion(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectVersionTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.VERSION));
    if (!objectVersionTag) {
      // TODO: infer this somehow - needs first-available parent package.json file
      return '1.0.0';

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
    } else {
      return (objectVersionTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.VERSION, '').trim();
    }
  }

  /**
   * Infer the vRO object id. If not provided in the annotation, the id is calculated
   * as uuidv5 from the object module and its name.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectId(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectIdTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.ID));
    if (!objectIdTag) {
      // generate the ID based on the module and the action name - this identifies the action in the scope of vRO
      return uuidv5(`${this.getVroObjectModule(annotation)}.${this.getVroObjectName(annotation)}`, nconf.get('ID_NAMESPACE'));
    } else {
      return (objectIdTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.ID, '').trim();
    }
  }

  /**
   * Infer the ABX object id. If not provided in the annotation, the id is calculated
   * as uuidv5 from the object name.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectId(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectIdTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.ID));
    if (!objectIdTag) {
      // generate the ID based on the module and the action name - this identifies the action in the scope of vRO
      return uuidv5(this.getAbxObjectName(annotation), nconf.get('ID_NAMESPACE'));
    } else {
      return (objectIdTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.ID, '').trim();
    }
  }

  /**
   * Infer the Polyglot object entrypoint, defaulting to "handler.handler".
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getPolyglotObjectEntrypoint(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectEntrypointTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.ENTRYPOINT));
    if (!objectEntrypointTag) {
      return 'handler.handler';
    } else {
      return (objectEntrypointTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.ENTRYPOINT, '').trim();
    }
  }

  /**
   * Infer the ABX object entrypoint, defaulting to "handler".
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectEntrypoint(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectEntrypointTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.ENTRYPOINT));
    if (!objectEntrypointTag) {
      return 'handler';
    } else {
      return (objectEntrypointTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.ENTRYPOINT, '').trim();
    }
  }

  /**
   * Get the vRO object inputs.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectInputs(annotation: TransformedAnnotation): Array<Input | null> {

    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectInputTags = tagsValue.filter(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.INPUT));
    const objectInputs = objectInputTags.map(inputTag => {
      const inputContens = (inputTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.INPUT, '').trim();

      // infer input type
      const regex = /(\{.+\})(?:\s)?(\w+)(?:\s)?(.+)?/g
      const match = regex.exec(inputContens);
      if (!match) return null
      return {
        name: match[2].trim(),
        type: match[1].replace('{', '').replace('}', '').trim(),
        description: (match[3] || '').trim()
      };
    }).filter(input => input);
    return objectInputs;
  }

  /**
   * Get the ABX object inputs.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectInputs(annotation: TransformedAnnotation): Array<Input | null> {

    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectInputTags = tagsValue.filter(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.INPUT));
    const objectInputs = objectInputTags.map(inputTag => {
      const inputContens = (inputTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.INPUT, '').trim();

      // infer input type
      const regex = /(\{.+\})(?:\s)?(\w+)(?:\s)?(.+)?/g
      const match = regex.exec(inputContens);
      if (!match) return null
      return {
        name: match[2].trim(),
        type: match[1].replace('{', '').replace('}', '').trim(),
        value: (match[3] || '').trim()
      };
    }).filter(input => input);
    return objectInputs;
  }

  /**
   * Get the vRO object output.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getVroObjectOutput(annotation: TransformedAnnotation) {

    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectOutputTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.OUTPUT));
    if (!objectOutputTag) {
      return null;
    } else {

      const outputContens = (objectOutputTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.OUTPUT, '').trim();

      // infer output type
      const regex = /(\{.+\})(?:\s)?(.+)?/g
      const match = regex.exec(outputContens);
      if (!match) return null
      return {
        type: match[1].replace('{', '').replace('}', '').trim(),
        description: (match[2] || '').trim()
      };
    }
  }

  /**
   * Infer the Polyglot object timeout from the annotation, defaulting to 10 minutes.
   * Value is in seconds.
   * @param annotation {TransformedAnnotation}
   * @returns {number}
   */
  private getPolyglotObjectTimeout(annotation: TransformedAnnotation): number {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectTimeoutTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.TIMEOUT));
    if (!objectTimeoutTag) {
      return 10 * 60; // 10 minutes
    } else {
      return parseInt((objectTimeoutTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.TIMEOUT, '').trim(), 10);
    }
  }

  /**
   * Infer the ABX object timeout from the annotation, defaulting to 10 minutes.
   * Value is in seconds.
   * @param annotation {TransformedAnnotation}
   * @returns {number}
   */
  private getAbxObjectTimeout(annotation: TransformedAnnotation): number {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectTimeoutTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.TIMEOUT));
    if (!objectTimeoutTag) {
      return 10 * 60; // 10 minutes
    } else {
      return parseInt((objectTimeoutTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.TIMEOUT, '').trim(), 10);
    }
  }

  /**
   * Infer the Polyglot object memory limit from the annotation, defaulting to 256 MB.
   * Value is in bytes.
   * @param annotation {TransformedAnnotation}
   * @returns {number}
   */
  private getPolyglotObjectMemory(annotation: TransformedAnnotation): number {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.vro) ? annotation.tags.vro : [annotation.tags.vro];
    const objectMemoryTag = tagsValue.find(t => t.description?.startsWith(VRO_OBJECT_ANNOTATIONS.MEMORY));
    if (!objectMemoryTag) {
      return 256 * 1000 * 1000;  // 256 MB
    } else {
      return parseInt((objectMemoryTag.description || '').replace(VRO_OBJECT_ANNOTATIONS.MEMORY, '').trim(), 10);
    }
  }

  /**
   * Infer the ABX object memory limit from the annotation, defaulting to 256 MB.
   * Value is in bytes.
   * @param annotation {TransformedAnnotation}
   * @returns {number}
   */
  private getAbxObjectMemory(annotation: TransformedAnnotation): number {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectMemoryTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.MEMORY));
    if (!objectMemoryTag) {
      return 256 * 1000 * 1000;  // 256 MB
    } else {
      return parseInt((objectMemoryTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.MEMORY, '').trim(), 10);
    }
  }

  /**
   * Get the ABX object dependencies.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectDependencies(annotation: TransformedAnnotation): string {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectDependenciesTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.DEPENDENCIES));
    if (!objectDependenciesTag) {
      return ''
    } else {
      return (objectDependenciesTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.DEPENDENCIES, '').trim();
    }
  }

  /**
   * Get the ABX shared setting.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectSharedSetting(annotation: TransformedAnnotation): boolean {
    const sharedValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectSharedTag = sharedValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.SHARED));
    return !objectSharedTag ? false : true;
  }

  /**
   * Get the ABX object provider.
   * @param annotation {TransformedAnnotation}
   * @returns {string}
   */
  private getAbxObjectProvider(annotation: TransformedAnnotation): '' | 'aws' | 'azure' | 'on-prem' {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectProviderTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.PROVIDER));
    if (!objectProviderTag) {
      return ''
    } else {
      const provider = (objectProviderTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.PROVIDER, '').trim();
      return (provider === '' || provider === 'aws' || provider === 'azure' || provider === 'on-prem')
        ? provider
        : '';
    }
  }

  /**
   * Get the ABX object configuration.
   * @param annotation {TransformedAnnotation}
   * @returns {Record<string, string | boolean | number>}
   */
  private getAbxObjectConfiguration(annotation: TransformedAnnotation): Record<string, string | boolean | number> {
    const tagsValue: doctrine.Tag[] = Array.isArray(annotation.tags.abx) ? annotation.tags.abx : [annotation.tags.abx];
    const objectConfigurationTag = tagsValue.find(t => t.description?.startsWith(ABX_OBJECT_ANNOTATIONS.CONFIGURATION));
    if (!objectConfigurationTag) {
      return {}
    } else {
      const configuration = (objectConfigurationTag.description || '').replace(ABX_OBJECT_ANNOTATIONS.CONFIGURATION, '').trim();
      let value = {};
      try {
        value = JSON.parse(configuration)
      } catch (err) {
        console.warn(`Could not parse configuration value: ${configuration}`)
      }
      return value
    }
  }

}
