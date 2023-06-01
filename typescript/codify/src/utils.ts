

import path from 'path';
import chalk from 'chalk';
import Zip from 'adm-zip';
import { XMLParser } from 'fast-xml-parser';
import nconf from './config';
import { Attribute, ICodifyObject } from './model';
import { execSync } from 'child_process';

/**
 * Check whether a string is an UUID.
 * @param str {string}
 * @returns true if string is UUID
 */
export function isUUID(str: string): boolean {
  return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(str);
}

/**
 * Check whether a string is a vRA subscription ID.
 * @param str
 * @returns true if string is a subscription ID
 */
export function isSubscriptionId(str: string): boolean {
  return /^sub_[0-9]{13}$/i.test(str);
}

/**
 * Check whether a string is a vRA resource action ID.
 * @param str
 * @returns true if string is a resource action ID
 */
export function isResourceActionId(str: string): boolean {
  return /^[a-zA-Z.]+\.custom\.[a-z-]+$/i.test(str);
}

/**
 * Generate a new key-value map from list of attributes.
 * @param attributes {Attribute[]}
 * @returns map of key-value pairs
 */
export function attributesToMap(attributes: Attribute[]): Map<string, string> {
  const map = new Map();
  attributes.forEach((a) => {
    map.set(a.name, a.value);
  });
  return map;
}

/**
 * Generate a new action module name from path.
 * @param path {string}
 * @returns module name
 */
export function generateModuleName(path: string) {
  return path
    .trim()
    .toLowerCase()
    .replace(/[^a-zA-Z\d\s:.]/gi, '')
    .replace(/[\s]+/gi, '.');
}

/**
 * Run `npm install` against a Node project.
 * @param path {string}
 */
export function npmInstall(path: string) {
  try {
    execSync('npm install', { stdio: 'inherit', cwd: path });
  } catch (err) {
    console.error(chalk.red(err));
  }
}

/**
 * Install the project dependencies. This is applicable
 * to any project type based on NodeJS.
 * @param projectName {string}
 */
export async function installProjectDependencies(projectName: string) {
  const projectRoot = getProjectPath(projectName);
  npmInstall(projectRoot);
}

export function getProjectPath(projectName: string) {
  return path.resolve(projectName);
}

/**
 * Type guard for TS objects
 * @param value {unknown}
 * @returns true if value is an object
 */
export function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

export function orderCodifyObjects(objects: ICodifyObject[]) {
  const uploadOrder: string[] = nconf.get('UPLOAD_ORDER');
  objects.sort((a, b) => {
    return uploadOrder.indexOf(a.constructor.name) - uploadOrder.indexOf(b.constructor.name);
  });
}

export function formatAndRethrowAxiosError(error: any) {
  if (error.isAxiosError && error.response) {
    const errorMessage = `${error.response.request.method} ${error.response.request.path} -> ${error.response.status} ${
      error.response.statusText
    }: ${JSON.stringify(error.response.data)}`;
    throw new Error(errorMessage);
  } else {
    throw error;
  }
}

/**
 * Read UTF-16 encoded content. XMLs in vRO packages are UTF-16LE
 * @param content {Buffer | string}
 * @returns string
 */
function readUtf16Content(content: Buffer | string): string {
  // Remove BOM
  let contentStr = Buffer.isBuffer(content) ? content.toString() : content;

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
  contentStr = contentStr.replace(/\ufffd/g, '');
  // eslint-disable-next-line no-control-regex
  contentStr = contentStr.replace(/\u0000/g, '');
  return contentStr;
}

/**
 * Read XML content from a file in zip
 * @param zipFile {string | Zip}
 * @param file {filename}
 * @returns {Object}
 */
export function readXmlFileFromZip(zipFile: string | Zip, file: string) {
  const zip = typeof zipFile === 'string' ? new Zip(zipFile) : zipFile;
  const xmlContent = readUtf16Content(zip.readAsText(file));
  const parser = new XMLParser({ ignoreAttributes: false });
  const content = parser.parse(xmlContent);
  return content;
}

export function mapEntries(entries: Array<{ '#text': string; '@_key': string }>): Record<string, string> {
  return entries.reduce<Record<string, string>>((agg, entry) => {
    agg[entry['@_key']] = entry['#text'];
    return agg;
  }, {});
}

export function mapAttributes(attributes: Array<{ name: string; value: string }>): Record<string, string> {
  return attributes.reduce<Record<string, string>>((agg, attribute) => {
    agg[attribute.name] = attribute.value;
    return agg;
  }, {});
}
