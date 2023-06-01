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


import path from 'path';
import fs from 'fs-extra';
import chalk from 'chalk';
import yaml from 'js-yaml';
import { AxiosInstance } from 'axios';
import { IBaseVraObject, VraObject } from './base';
import { isSubscriptionId } from '..';
import { omit } from 'lodash';

export interface ISubscription extends IBaseVraObject {
  objectType: 'Subscription';
  id: string;
  description: string;
  type: string;
  eventTopicId: string;
  blocking: boolean;
  criteria: string;
  constraints: {
    projectId: string | null;
  };
  timeout: number;
  broadcast: boolean;
  priority: number;
  disabled: boolean;
  system: boolean;
  contextual: boolean;
  runnableType: string;
  runnableId: string;
}

type SubscriptionResponse = {
  id: string;
  type: string;
  eventTopicId: string;
  name: string;
  orgId: string;
  ownerId: string;
  subscriberId: string;
  blocking: boolean;
  description: string;
  criteria: string;
  constraints: {
    projectId: string | null;
  };
  timeout: number;
  broadcast: boolean;
  priority: number;
  disabled: boolean;
  system: boolean;
  contextual: boolean;
  runnableType: string;
  runnableId: string;
};

export class Subscription extends VraObject {
  public readonly urn: string;

  constructor(protected readonly objectDefinition: ISubscription) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.name}`;
    console.log(`Resolved subscription: ${this.urn}`);
  }

  get<ISubscription>() {
    return <ISubscription>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert subscription
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance): Promise<void> {
    console.log(`Uploading subscription ${this.objectDefinition.name}`);

    const payload = { ...omit(this.objectDefinition, 'objectType') };
    await client.post('/event-broker/api/subscriptions', payload);

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Download subscription.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string): Promise<void> {
    console.log(`Downloading subscription ${this.objectDefinition.name}`);

    // make directory if it doesn't exist and write the configuration
    const subscriptionTargetPath = path.resolve(path.normalize(target));
    await fs.promises.mkdir(subscriptionTargetPath, { recursive: true });

    const subscriptionTarget = path.join(subscriptionTargetPath, `${this.objectDefinition.name}.sub.yaml`);
    console.log(`Writing subscription to ${subscriptionTarget}`);
    await fs.promises.writeFile(subscriptionTarget, yaml.dump(this.objectDefinition));

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta(): string {
    return JSON.stringify(this.objectDefinition, null, 2);
  }

  /**
   * Resolve a subscription object from remote identifier.
   * @param client {AxiosInstance}
   * @param subscriptionIdentifier {string} subscription name or id
   * @returns the resolved subscription
   */
  static async from(client: AxiosInstance, subscriptionIdentifier: string): Promise<Subscription | null> {
    const subscription = await Subscription.getRemoteSubscription(client, subscriptionIdentifier);
    if (subscription) {
      return new Subscription({
        objectType: 'Subscription',
        id: subscription.id,
        blocking: subscription.blocking,
        broadcast: subscription.broadcast,
        constraints: subscription.constraints,
        contextual: subscription.contextual,
        criteria: subscription.criteria,
        description: subscription.description,
        disabled: subscription.disabled,
        eventTopicId: subscription.eventTopicId,
        name: subscription.name,
        priority: subscription.priority,
        runnableId: subscription.runnableId,
        runnableType: subscription.runnableType,
        system: subscription.system,
        timeout: subscription.timeout,
        type: subscription.type,
      });
    } else {
      console.warn(`Could not resolve subscription: ${subscriptionIdentifier}`);
      return null;
    }
  }

  /**
   * Retrieve remote subscription.
   * @param client {AxiosInstance}
   * @param subscriptionIdentifier {string}
   * @returns remote subscription
   */
  static async getRemoteSubscription(
    client: AxiosInstance,
    subscriptionIdentifier: string
  ): Promise<SubscriptionResponse | null> {
    if (isSubscriptionId(subscriptionIdentifier)) {
      const remoteSubscription = await client.get(`/event-broker/api/subscriptions/${subscriptionIdentifier}`, {
        validateStatus: null,
      });
      return remoteSubscription.status === 200 ? remoteSubscription.data : null;
    } else {
      const params = new URLSearchParams({
        $filter: `(name eq '${subscriptionIdentifier}')`,
      });
      const subscriptionSearch = await client.get(`/event-broker/api/subscriptions?${params.toString()}`);

      if (subscriptionSearch.data.numberOfElements >= 1) {
        const remoteSubscription = await client.get(
          `/event-broker/api/subscriptions/${subscriptionSearch.data.content[0].id}`
        );
        return remoteSubscription.data;
      }
      return null;
    }
  }
}
