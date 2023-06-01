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


import chalk from 'chalk';
import { createCodifyProjectAsk } from './creators/codify';
import { createAbxProjectAsk } from './creators/abx';
import { CodifyArguments } from '../cli';

export async function create(argv: CodifyArguments): Promise<void> {
  if (argv.project) {
    await createCodifyProjectAsk();
  } else if (argv.abx) {
    await createAbxProjectAsk();
  } else if (argv.polyglot) {
    console.error(chalk.red('Polgyglot projects are not supported yet. Stay tuned'));
  } else if (argv.ipam) {
    console.error(chalk.red('IPAM projects are not supported yet. Stay tuned'));
  } else {
    // by default create a Codify project
    await createCodifyProjectAsk();
  }
}
