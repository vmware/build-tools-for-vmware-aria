

import path from 'path';
import fs from 'fs-extra';
import { Parser } from '../parser';
import { CodifyArguments } from '../cli';
import { ICodifyObject, VroObject } from '../model';
import { getVroClientSingleton } from './clients';
import { orderCodifyObjects } from '../utils';
import { VroPackage } from '../model/vropackage';

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

/**
 * Create package assembly. Currently only vRO packages are supported.
 * @param argv {CodifyArguments}
 */
export async function assemble(argv: CodifyArguments): Promise<void> {
  if (!argv.source) {
    throw new Error('Argument "source" is required');
  }

  if (!argv.package) {
    throw new Error('Argument "package" is requred');
  }

  let codifyObjects: ICodifyObject[] = [];

  async function processSourceEntry(sourceStr: string) {
    const source = path.resolve(sourceStr);
    const lstat = await fs.promises.lstat(source);
    if (lstat.isFile()) {
      console.log(`Resolved source file: ${source}`);
      const obj = await Parser.parseFile(source);
      if (obj) {
        codifyObjects.push(obj);
      }
    } else if (lstat.isDirectory()) {
      console.log(`Resolved source directory: ${source}`);
      const objs = await Parser.parseDirectory(source);
      codifyObjects = [...codifyObjects, ...objs];
    }
  }

  if (Array.isArray(argv.source)) {
    await Promise.all(argv.source.map(processSourceEntry));
  } else {
    await processSourceEntry(argv.source);
  }

  orderCodifyObjects(codifyObjects);

  // build package
  const vroPackage = new VroPackage({
    id: VroPackage.getObjectId(argv.package),
    name: argv.package,
    content: <VroObject[]>codifyObjects.filter((obj) => obj instanceof VroObject),
    description: argv.description || 'Codify-assembled package',
    version: 'none',
  });

  const vroClient = await getVroClientSingleton(argv);
  await vroPackage.upload(vroClient);
}
