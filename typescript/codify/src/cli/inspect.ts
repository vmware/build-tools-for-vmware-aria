

import path from 'path';
import fs from 'fs-extra';
import Zip from 'adm-zip';
import { CodifyArguments } from '../cli';
import { readXmlFileFromZip } from '../utils';

/**
 * Inspection utility
 * @param argv
 */
export async function inspect(argv: CodifyArguments): Promise<void> {
  // Take the path to the package
  const [, packagePath] = argv._;

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
  if (!packagePath) {
    throw new Error('Please provide a path to a vRO package as a first argument to the inspect command.');
  }

  if (!fs.pathExistsSync(packagePath)) {
    throw new Error(`Could not open vRO package from ${packagePath}`);
  }

  // Open the ZIP file
  const zip = new Zip(path.normalize(packagePath));
  const elements = zip.getEntries().filter((entry) => /^elements\/[a-z0-9-]+\/data$/gi.test(entry.entryName));

  const categoriesMap = zip
    .getEntries()
    .filter((entry) => /^elements\/[a-z0-9-]+\/categories$/gi.test(entry.entryName))
    .reduce<Map<string, string>>((acc, entry) => {
      const content = readXmlFileFromZip(zip, entry.entryName);
      const category = Array.isArray(content.categories.category)
        ? content.categories.category.map((cat: { name: string }) => cat.name).join('/')
        : content.categories.category.name;
      const id = (<RegExpExecArray>/^elements\/([a-z0-9-]+)\/categories$/gi.exec(entry.entryName))[1];
      acc.set(id, category);
      return acc;
    }, new Map());

  const bom = elements
    .map((entry, index) => {
      const content = readXmlFileFromZip(zip, entry.entryName);
      if (content['dunes-script-module']) {
        return {
          Type: 'Action',
          URN: `${categoriesMap.get(content['dunes-script-module']['@_id'])}/${
            content['dunes-script-module']['@_name']
          }`,
          version: content['dunes-script-module']['@_version'],
        };
      } else if (content['workflow']) {
        return {
          Type: 'Workflow',
          URN: `${categoriesMap.get(content['workflow']['@_id'])}/${content['workflow']['display-name']}`,
          version: content['workflow']['@_version'],
        };
      } else if (content['config-element']) {
        return {
          Type: 'Configuration',
          URN: `${categoriesMap.get(content['config-element']['@_id'])}/${content['config-element']['display-name']}`,
          version: content['config-element']['@_version'],
        };
      } else {
        return {
          Type: 'Unknown',
          URN: 'Unknown',
          version: 'Unknown',
        };
      }
    })
    .sort((a, b) => a.Type.localeCompare(b.Type));

  console.table(bom);
}
