

import prompts from 'prompts';
import chalk from 'chalk';
import { getVroClientSingleton } from '../clients';
import { CodifyArguments } from '../../cli';
import { ICodifyObject, VroObject } from '../../model/base';
import { mapAttributes } from '../../utils';
import { Action, Configuration, Workflow } from '../../model';
import path from 'path';

// ====================================
// Generate Codify content from vRO package
// ====================================

type PackageContentsResponse = {
  workflows: { workflow: { attributes: { name: string;
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
 value: string }[] } }[];
  actions: { actions: { attributes: { name: string; value: string }[] } }[];
  configurations: { configuration: { attributes: { name: string; value: string }[] } }[];
};

/**
 * Ask the user for inputs that will parametrize the
 * Codify project creation.
 */
export async function createCodifyProjectFromPackageAsk(argv: CodifyArguments) {
  const vroClient = await getVroClientSingleton(argv);

  const prompt = await prompts([
    {
      type: 'text',
      name: 'package',
      message: 'What is the id of your vRO package?',
      initial: argv.package,
      validate: async (id: string) => {
        const remotePackage = await vroClient.head(`/packages/${id}`, { validateStatus: null });
        return remotePackage.status !== 200 ? `The package "${id}" does not exist on vRO: ${argv.vroHostname}!` : true;
      },
    },
    {
      type: 'text',
      name: 'target',
      message: 'What is the target directory where content should be downloaded?',
      initial: argv.target || './src',
      validate: (name: string) => {
        return true;
      },
    },
  ]);

  // ===============================
  // Get package contents
  // ===============================
  const codifyObjects: ICodifyObject[] = [];
  const packageContents = await vroClient.get<PackageContentsResponse>(`/packages/${prompt.package}`);

  // handle workflows
  (
    await Promise.all(
      packageContents.data.workflows.map((obj) => {
        const objAttributes = mapAttributes(obj.workflow.attributes);
        return Workflow.from(vroClient, objAttributes.id);
      })
    )
  )
    .filter((obj) => obj)
    .forEach((obj) => codifyObjects.push(<VroObject>obj));

  // handle actions
  (
    await Promise.all(
      packageContents.data.actions.map((obj) => {
        const objAttributes = mapAttributes(obj.actions.attributes);
        return Action.from(vroClient, objAttributes.id);
      })
    )
  )
    .filter((obj) => obj)
    .forEach((obj) => codifyObjects.push(<VroObject>obj));

  // handle configurations
  (
    await Promise.all(
      packageContents.data.configurations.map((obj) => {
        const objAttributes = mapAttributes(obj.configuration.attributes);
        return Configuration.from(vroClient, objAttributes.id);
      })
    )
  )
    .filter((obj) => obj)
    .forEach((obj) => codifyObjects.push(<VroObject>obj));

  // ===============================
  // Download the resolved content
  // ===============================
  if (!argv.dryrun) {
    const download = async (obj: ICodifyObject) => {
      if (obj instanceof VroObject) {
        await obj.download(vroClient, getTarget(prompt.target, obj.constructor.name));
      } else {
        console.error(chalk.red(`Unknown Codify object instance: ${obj}`));
      }
    };

    if (argv.sequential) {
      for (let i = 0; i < codifyObjects.length; i++) {
        await download(codifyObjects[i]);
      }
    } else {
      codifyObjects.forEach(download);
    }
  }
}

function getTarget(base: string, contentType: string) {
  switch (contentType) {
    case 'Configuration':
      return path.join(base, 'configurations');
    case 'Action':
      return path.join(base, 'actions');
    case 'Workflow':
      return path.join(base, 'workflows');
    default:
      return base;
  }
}
