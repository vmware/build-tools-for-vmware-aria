

import chalk from 'chalk';
import { CodifyArguments } from '../cli';
import { BaseAction, Configuration, ICodifyObject, VraObject, VroObject, Workflow, AbxAction } from '../model';
import { CloudTemplate } from '../model/cloudtemplates';
import { ResourceAction } from '../model/resourceactions';
import { Subscription } from '../model/subscriptions';
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
import { getVraClientSingleton, getVroClientSingleton } from './clients';

export async function download(argv: CodifyArguments): Promise<void> {
  const codifyObjects: ICodifyObject[] = [];

  // ======================
  // vRO Objects Resolution
  // ======================

  // resolve remote workflows
  if (argv.workflow) {
    const vroClient = await getVroClientSingleton(argv);
    if (Array.isArray(argv.workflow)) {
      (await Promise.all(argv.workflow.map((wf) => Workflow.from(vroClient, wf.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VroObject>obj));
    } else {
      const obj = await Workflow.from(vroClient, argv.workflow.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote actions
  if (argv.action) {
    const vroClient = await getVroClientSingleton(argv);
    if (Array.isArray(argv.action)) {
      (await Promise.all(argv.action.map((action) => BaseAction.from(vroClient, action.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VroObject>obj));
    } else {
      const obj = await BaseAction.from(vroClient, argv.action.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote config elements
  if (argv.config) {
    const vroClient = await getVroClientSingleton(argv);
    if (Array.isArray(argv.config)) {
      (await Promise.all(argv.config.map((config) => Configuration.from(vroClient, config.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VroObject>obj));
    } else {
      const obj = await Configuration.from(vroClient, argv.config.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote vro packages
  if (argv.package) {
    const vroClient = await getVroClientSingleton(argv);
    if (Array.isArray(argv.package)) {
      (await Promise.all(argv.package.map((vroPackage) => VroPackage.from(vroClient, vroPackage.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VroObject>obj));
    } else {
      const obj = await VroPackage.from(vroClient, argv.package.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // ======================
  // vRA Objects Resolution
  // ======================

  // resolve remote cloud templates
  if (argv.template) {
    const vraClient = await getVraClientSingleton(argv);
    if (Array.isArray(argv.template)) {
      (await Promise.all(argv.template.map((template) => CloudTemplate.from(vraClient, template.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VraObject>obj));
    } else {
      const obj = await CloudTemplate.from(vraClient, argv.template.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote subscriptions
  if (argv.subscription) {
    const vraClient = await getVraClientSingleton(argv);
    if (Array.isArray(argv.subscription)) {
      (await Promise.all(argv.subscription.map((sub) => Subscription.from(vraClient, sub.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VraObject>obj));
    } else {
      const obj = await Subscription.from(vraClient, argv.subscription.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote resource actions
  if (argv.day2) {
    const vraClient = await getVraClientSingleton(argv);
    if (Array.isArray(argv.day2)) {
      (await Promise.all(argv.day2.map((day2) => ResourceAction.from(vraClient, day2.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VraObject>obj));
    } else {
      const obj = await ResourceAction.from(vraClient, argv.day2.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // resolve remote abx actions
  if (argv.abx) {
    const vraClient = await getVraClientSingleton(argv);
    if (Array.isArray(argv.abx)) {
      (await Promise.all(argv.abx.map((abx) => AbxAction.from(vraClient, abx.trim()))))
        .filter((obj) => obj)
        .forEach((obj) => codifyObjects.push(<VraObject>obj));
    } else {
      const obj = await AbxAction.from(vraClient, argv.abx.trim());
      if (obj) {
        codifyObjects.push(obj);
      }
    }
  }

  // download the resolved content
  if (!argv.dryrun) {
    const download = async (obj: ICodifyObject) => {
      if (!argv.target) {
        throw new Error('Argument "target" is required');
      }

      if (obj instanceof VroObject) {
        const vroClient = await getVroClientSingleton(argv);
        await obj.download(vroClient, argv.target);
      } else if (obj instanceof VraObject) {
        const vraClient = await getVraClientSingleton(argv);
        await obj.download(vraClient, argv.target);
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
