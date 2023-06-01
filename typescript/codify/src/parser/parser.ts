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
import globby from 'globby';
import { ICodifyObject } from '../model/base';
import { WorkflowParser } from './workflow';
import { ConfigurationParser } from './configuration';
import { CloudTemplateParser } from './cloudtemplate';
import { SubscriptionParser } from './subscription';
import { ActionParser } from './action';
import { ResourceActionParser } from './resourceaction';

export interface IParser {
  parse(): Promise<void>
  getObjectDefinition(): ICodifyObject | null
}

export class Parser {

  /**
   * This map determines which parser constructor will be used for which extension.
   * There's only one parser per extension.
   */
  static MAP: { [extension: string]: new (file: string) => IParser } = {
    '.js': ActionParser,
    '.py': ActionParser,
    '.ps1': ActionParser,
    '.workflow.meta': WorkflowParser,
    '.conf.yml': ConfigurationParser,
    '.conf.yaml': ConfigurationParser,
    '.vct.yml': CloudTemplateParser,
    '.vct.yaml': CloudTemplateParser,
    '.sub.yml': SubscriptionParser,
    '.sub.yaml': SubscriptionParser,
    '.day2.yml': ResourceActionParser,
    '.day2.yaml': ResourceActionParser,
  };

  /**
   * Parse a file and attempt to generate a Codify object. Determination of what is the
   * Codify object depends on the structure of the file or its extension.
   * @param fileName {string}
   * @returns Codify object
   */
  static async parseFile(fileName: string): Promise<ICodifyObject | null> {

    if (!await (fs.pathExists(fileName))) {
      throw new Error(`File ${fileName} does not exist`);
    }

    let codifyObject: ICodifyObject | null = null;

    const mappedParser = Object.keys(Parser.MAP).find(extension => fileName.endsWith(extension));
    if (mappedParser) {
      const parser = new Parser.MAP[mappedParser](fileName);
      await parser.parse();
      codifyObject = parser.getObjectDefinition();
    }

    return codifyObject;
  }

  /**
   * Parse an entire directory and extract Codify objects
   * @param projectDir {string}
   * @returns list of Codify objects
   */
  static async parseDirectory(projectDir: string): Promise<ICodifyObject[]> {

    if (!await (fs.pathExists(projectDir))) {
      throw new Error(`Path ${projectDir} does not exist`);
    }

    const possibleFiles = await globby([
      ...Object.keys(Parser.MAP).map(ext => `**/*${ext}`),
      '!**/node_modules/**'
    ], {
      absolute: true,
      expandDirectories: false,
      cwd: projectDir,
    });

    const possibleObjects = await Promise.all(possibleFiles.map(file => Parser.parseFile(file)));

    const codifyObjects: ICodifyObject[] = [];
    possibleObjects.forEach(obj => {
      if (obj) codifyObjects.push(obj);
    });

    return codifyObjects;
  }

}
