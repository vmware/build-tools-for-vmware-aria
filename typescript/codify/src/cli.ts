

import nconf from './config';
import dotenv from 'dotenv';
import minimist from 'minimist';
import { create } from './cli/create';
import { run } from './cli/run';
import { download } from './cli/download';
import { upload } from './cli/upload';
import { bom } from './cli/bom';
import { assemble } from './cli/assemble';
import { generate } from './cli/generate';
import { inspect } from './cli/inspect';

dotenv.config();

export interface CodifyArguments extends minimist.ParsedArgs {
  version?: boolean;
  dryrun?: boolean;
  source?: string;
  target?: string;
  workflow?: string;
  action?: string;
  config?: string;
  template?: string;
  subscription?: string;
  day2?: string;
  abx?: string;
  package?: string;

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
  input?: Array<string>;
  sequential?: boolean;
  reauthenticate?: boolean;
  vroAuthHostname?: string;
  vroHostname?: string;
  vroUsername?: string;
  vroPassword?: string;
  vroRefreshToken?: string;
  vraHostname?: string;
  vraUsername?: string;
  vraPassword?: string;
  vraRefreshToken?: string;
}

/**
 * Print the tool version
 */
function printVersion() {
  console.log(`Version ${nconf.get('VERSION')}`);
}

/**
 * Evaluate the command passed to the CLI.
 * @param argv {ParsedArgs}
 * @returns {string}
 */
function getCommand(argv: CodifyArguments): string {
  return argv._[0];
}

/**
 * Main CLI entrypoint.
 * @returns {Promise<void>}
 */
export async function runCLI(): Promise<void> {
  // define arguments with default values
  const argv = minimist(process.argv.slice(2), {
    default: {
      version: false,
      dryrun: false,

      // options valid for the 'upload' command
      source: null, // Folder or file to upload

      // options valid for the 'download' command
      target: './',
      workflow: null, // Workflow path or id to download.
      action: null, // Action path or id to download. Multiple are possible.
      config: null, // Config element path or id to download. Multiple are possible.
      package: null, // Package name to download. Multiple are possible.
      template: null, // Cloud template name or id to download. Multiple are possible.
      subscription: null, // Subscription name or id to download. Multiple are possible.
      day2: null, // Resource action name or id to download. Multiple are possible.
      abx: null, // ABX action name or id to download. Multiple are possible.

      // options valid for the 'run' command
      input: [],

      // sequential run of operarations (to avoid race conditions)
      sequential: false,

      // force reauthentication instead of using cached credentials
      reauthenticate: false,

      // vro credentials
      vroAuthHostname: process.env.VRO_AUTH_HOST || null,
      vroHostname: process.env.VRO_HOST || null,
      vroUsername: process.env.VRO_USER || null,
      vroPassword: process.env.VRO_PASS || null,
      vroRefreshToken: process.env.VRO_TOKEN || null,

      // vra credentials
      vraHostname: process.env.VRA_HOST || null,
      vraUsername: process.env.VRA_USER || null,
      vraPassword: process.env.VRA_PASS || null,
      vraRefreshToken: process.env.VRA_TOKEN || null,
    },
  });

  if (argv.version) {
    return printVersion();
  }

  if (getCommand(argv) === 'upload' && argv.source) {
    // ================================
    // handle object parsing / upload
    // ================================
    return await upload(argv);
  } else if (getCommand(argv) === 'download' && argv.target) {
    // ================================
    // handle object resolution / download
    // ================================
    return await download(argv);
  } else if (getCommand(argv) === 'run') {
    // ================================
    // handle object run
    // ================================
    return await run(argv);
  } else if (getCommand(argv) === 'create') {
    // ================================
    // handle project create
    // ================================
    return await create(argv);
  } else if (getCommand(argv) === 'generate') {
    // ================================
    // handle content generate
    // ================================
    return await generate(argv);
  } else if (getCommand(argv) === 'bom') {
    // ================================
    // handle BOM
    // ================================
    return await bom(argv);
  } else if (getCommand(argv) === 'assemble') {
    // ================================
    // handle Codify assembly
    // ================================
    return await assemble(argv);
  } else if (getCommand(argv) === 'inspect') {
    // ================================
    // handle Codify inspect
    // ================================
    return await inspect(argv);
  } else {
    // ================================
    // handle everything else
    // ================================
    return printVersion();
  }
}

(async function () {
  await runCLI();
})();
