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
import { getVraClient } from '../vra';

// Mock axios and set the type
// https://www.csrhymes.com/2022/03/09/mocking-axios-with-jest-and-typescript.html
jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('vRA Client', () => {

  afterEach(() => {
    jest.resetAllMocks();
  })

  it('Get client', async () => {

    jest.spyOn(fs, 'existsSync').mockReturnValueOnce(false);
    jest.spyOn(fs, 'writeJSONSync').mockReturnValueOnce();

    mockedAxios.create.mockReturnValueOnce(mockedAxios);
    mockedAxios.get.mockResolvedValueOnce({
      data: {
        applicationVersion: 'dlJlYWxpemUgQXV0b21hdGlvbiA4LjguMC4yMjE0OCAoMTk2OTE4ODkpCg==',
      }
    });
    mockedAxios.post.mockResolvedValueOnce({ data: { refresh_token: 'refresh-token' } });
    mockedAxios.post.mockResolvedValueOnce({ data: { access_token: 'access-token' } });

    const client = await getVraClient({ hostname: 'foo', username: 'bar@system domain', password: 'baz' });

    expect(client).toBeDefined();
    expect(client.defaults.headers.common['Authorization']).toEqual('Bearer access-token');

  });

  it('Get client with valid cached credentials', async () => {

    const token = Buffer.from(JSON.stringify({
      exp: Math.floor(Date.now() / 1000) + 1200
    })).toString('base64');

    jest.spyOn(fs, 'existsSync').mockReturnValueOnce(true);
    jest.spyOn(fs, 'readJSONSync').mockReturnValueOnce({
      access_token: `header.${token}.signature`
    });
    jest.spyOn(fs, 'writeJSONSync').mockReturnValueOnce();

    mockedAxios.create.mockReturnValueOnce(mockedAxios);

    const client = await getVraClient({ hostname: 'foo', username: 'bar@system domain', password: 'baz' });

    expect(client).toBeDefined();
    expect(client.defaults.headers.common['Authorization']).toEqual(`Bearer header.${token}.signature`);

  });

  it('Get client with expired cached credentials', async () => {

    const token = Buffer.from(JSON.stringify({
      exp: Math.floor(Date.now() / 1000)
    })).toString('base64');

    jest.spyOn(fs, 'existsSync').mockReturnValueOnce(true);
    jest.spyOn(fs, 'readJSONSync').mockReturnValueOnce({
      access_token: `header.${token}.signature`
    });
    jest.spyOn(fs, 'writeJSONSync').mockReturnValueOnce();

    mockedAxios.create.mockReturnValueOnce(mockedAxios);
    mockedAxios.get.mockResolvedValueOnce({
      data: {
        applicationVersion: 'dlJlYWxpemUgQXV0b21hdGlvbiA4LjguMC4yMjE0OCAoMTk2OTE4ODkpCg==',
      }
    });
    mockedAxios.post.mockResolvedValueOnce({ data: { refresh_token: 'refresh-token' } });
    mockedAxios.post.mockResolvedValueOnce({ data: { access_token: 'access-token' } });

    const client = await getVraClient({ hostname: 'foo', username: 'bar', password: 'baz' });

    expect(client).toBeDefined();
    expect(client.defaults.headers.common['Authorization']).toEqual('Bearer access-token');

  });

})
