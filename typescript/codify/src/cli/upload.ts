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
import chalk from "chalk";
import { ICodifyObject, VraObject, VroObject } from "../model";
import { Parser } from "../parser";
import { CodifyArguments } from "../cli";
import { orderCodifyObjects } from "..";
import { getVraClientSingleton, getVroClientSingleton } from "./clients";

export async function upload(argv: CodifyArguments): Promise<void> {

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

  // upload the resolved content
  if (!argv.dryrun) {

    const upload = async (obj: ICodifyObject) => {
      if (obj instanceof VroObject) {
        const vroClient = await getVroClientSingleton(argv);
        await obj.upload(vroClient);
      } else if (obj instanceof VraObject) {
        const vraClient = await getVraClientSingleton(argv);
        await obj.upload(vraClient);
      } else {
        console.error(chalk.red(`Unknown Codify object instance: ${obj}`))
      }
    }

    if (argv.sequential) {
      // Perform sequential upload to counter race conditions.
      // This can happen if you upload two workflows/actions in
      // a category/module that doesn't exist yet. The upload
      // sequence will try to concurrently create the category/module
      // and only the first one will succeed while the second one will
      // fail as the category/module already exists.
      for (let i = 0; i < codifyObjects.length; i++) {
        await upload(codifyObjects[i]);
      }
    } else {
      codifyObjects.forEach(upload);
    }

  }

}
