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


import fs from 'fs-extra';
import { ISubscription, Subscription } from '../../model/subscriptions';
import { SubscriptionParser } from '../subscription';

const MOCKS: { [filePath: string]: string } = {

  'subscription.sub.yaml': `
objectType: Subscription
id: sub_1641981534498
blocking: true
broadcast: false
constraints:
  projectId: null
contextual: false
criteria: ''
description: 'Sub Description'
disabled: false
eventTopicId: compute.reservation.pre
name: Sample Subscription
priority: 10
runnableId: 8a7480947e2b5010017e4d80a9e00010
runnableType: extensibility.abx
system: false
timeout: 0
type: RUNNABLE
  `,

  'no-subscription.sub.yaml': `
foo: bar
  `,

  'Inferred.sub.yaml': `
objectType: Subscription
  `,

};

describe('Configuration Parser', () => {

  let readFileSpy: jest.SpyInstance;
  let consoleSpy: jest.SpyInstance;

  beforeEach(() => {

    consoleSpy = jest.spyOn(global.console, 'warn');

    readFileSpy = jest
      .spyOn(fs, 'readFile')
      .mockImplementationOnce(_path => {
        const file = (<string>_path).split('/').pop() || <string>_path;
        return MOCKS[file]
          ? Promise.resolve(Buffer.from(MOCKS[file]))
          : Promise.reject(new Error('File does not exist'));
      });
  });

  it('Parse file', async () => {
    const subParser = new SubscriptionParser('subscription.sub.yaml');
    await subParser.parse();
    expect(readFileSpy).toHaveBeenCalledWith('subscription.sub.yaml', "utf-8");
  });

  it(`Parse subscription`, async () => {
    const subParser = new SubscriptionParser('subscription.sub.yaml');
    await subParser.parse();
    const object = subParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Subscription);
    expect(object?.get<ISubscription>().id).toEqual('sub_1641981534498');
    expect(object?.get<ISubscription>().name).toEqual('Sample Subscription');
    expect(object?.get<ISubscription>().description).toEqual('Sub Description');
  });

  it('Parse no subscription', async () => {
    const subParser = new SubscriptionParser('no-subscription.sub.yaml');
    await subParser.parse();
    const object = subParser.getObjectDefinition();
    expect(object).toBeNull();
  });

  it('Failed parse', async () => {
    const subParser = new SubscriptionParser('no-yaml.sub.yaml');
    await subParser.parse();
    const object = subParser.getObjectDefinition();
    expect(object).toBeNull();
    expect(console.warn).toHaveBeenCalledTimes(1);
  });

  it('Inferred subscription', async () => {
    const subParser = new SubscriptionParser('Inferred.sub.yaml');
    await subParser.parse();
    const object = subParser.getObjectDefinition();
    expect(object).not.toBeNull();
    expect(object?.get<ISubscription>().name).toEqual('Inferred');
    expect(object?.get<ISubscription>().description).toEqual('');
  });

});
