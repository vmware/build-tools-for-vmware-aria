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


import path from "path";
import fs from 'fs-extra';
import { Parser } from "../parser";
import { CodifyArguments } from "../cli";
import { ICodifyObject, VroObject } from "../model";
import { orderCodifyObjects } from "../utils";

export async function bom(argv: CodifyArguments): Promise<void> {

  if (!argv.source) {
    throw new Error('Argument "source" is required');
  }

  let codifyObjects: ICodifyObject[] = [];

  const source = path.resolve(argv.source);
  const lstat = await fs.promises.lstat(source);
  if (lstat.isFile()) {
    console.log(`Resolved source file: ${source}`);
    const obj = await Parser.parseFile(source);
    if (obj) {
      codifyObjects.push(obj);
    }
  } else if (lstat.isDirectory()) {
    console.log(`Resolved source directory: ${source}`);
    codifyObjects = await Parser.parseDirectory(source);
  }

  orderCodifyObjects(codifyObjects);

  const bom = codifyObjects.map(obj => {
    return {
      Type: obj.constructor.name,
      URN: obj.urn,
      ...(obj instanceof VroObject ? { Version: obj.version } : {})
    }
  });

  console.table(bom)

}
