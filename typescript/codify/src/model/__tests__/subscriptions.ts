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
import axios from 'axios';
import { omit } from 'lodash';
import { ISubscription, Subscription } from '../subscriptions';

// Define global mocks
jest.mock("fs-extra");
jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;
const mockedFs = fs as jest.Mocked<typeof fs>;

const MOCKED_SUBSCRIPTION = <ISubscription>{
  objectType: 'Subscription',
  id: 'sub_1641981534498',
  blocking: true,
  broadcast: false,
  constraints: {
    projectId: null
  },
  contextual: false,
  criteria: '',
  description: 'Sub Description',
  disabled: false,
  eventTopicId: 'compute.reservation.pre',
  name: 'Sample Subscription',
  priority: 10,
  runnableId: '8a7480947e2b5010017e4d80a9e00010',
  runnableType: 'extensibility.abx',
  system: false,
  timeout: 0,
  type: 'RUNNABLE',
}


describe('Subscription model', () => {

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('Create', async () => {
    const subscription = new Subscription({ ...MOCKED_SUBSCRIPTION });
    expect(subscription.get<ISubscription>()).toEqual(MOCKED_SUBSCRIPTION);
  });

  it('Resolve by name', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: { numberOfElements: 1, content: [omit(MOCKED_SUBSCRIPTION, 'objectType')] } });
    mockedAxios.get.mockResolvedValueOnce({ data: omit(MOCKED_SUBSCRIPTION, 'objectType') });
    const subscription = await Subscription.from(mockedAxios, 'Test Subscription');
    expect(subscription).not.toBe(null);
    expect(subscription?.get<ISubscription>()).toEqual(MOCKED_SUBSCRIPTION);
  });

  it('Resolve by id', async () => {
    mockedAxios.get.mockResolvedValueOnce({ status: 200, data: omit(MOCKED_SUBSCRIPTION, 'objectType') });
    const subscription = await Subscription.from(mockedAxios, 'sub_1641981534498');
    expect(subscription).not.toBe(null);
    expect(subscription?.get<ISubscription>()).toEqual(MOCKED_SUBSCRIPTION);
  });

  it('Resolve by name - not found', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: { numberOfElements: 0 } });
    const subscription = await Subscription.from(mockedAxios, 'Test Subscription');
    expect(subscription).toBe(null);
  });

  it('Resolve by id - not found', async () => {
    mockedAxios.get.mockResolvedValueOnce({ status: 404 });
    const subscription = await Subscription.from(mockedAxios, 'sub_1641981534498');
    expect(subscription).toBe(null);
  });

  it('Download', async () => {
    const subscription = new Subscription({ ...MOCKED_SUBSCRIPTION });
    await subscription.download(mockedAxios, 'dummy-folder');
    expect(mockedFs.promises.mkdir).toHaveBeenCalled();
    expect(mockedFs.promises.writeFile).toHaveBeenCalled();
  });

});
