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


import os from 'os';
import path from 'path';
import fs from 'fs-extra';
import moment from 'moment';
import axios, { AxiosInstance } from 'axios';
import https from 'https';
import chalk from 'chalk';
import { formatAndRethrowAxiosError } from '../utils';

const CACHE_DIR = os.tmpdir();

type RefreshTokenResponse = {
  refresh_token: string;
};

type AccessTokenResponse = {
  scope: string;
  access_token: string;
  refresh_token: string;
  id_token: string;
  token_type: string;
  expires_in: number;
};

type VroAuthentication = {
  authHostname?: string;
  hostname?: string;
  username?: string;
  password?: string;
  refreshToken?: string;
  reauthenticate?: boolean;
};

export async function getVroClient(auth: VroAuthentication): Promise<AxiosInstance> {
  const client = axios.create({
    baseURL: `https://${auth.authHostname || auth.hostname}`,
    httpsAgent: new https.Agent({ rejectUnauthorized: false }),
  });

  // add interceptor for error formatting
  client.interceptors.response.use(
    (response) => response,
    (error) => formatAndRethrowAxiosError(error)
  );

  if (auth.authHostname) {
    console.log(`Authentication is using authentication host: ${auth.authHostname}`);
  }

  if (auth.refreshToken) {
    console.log(`Authenticating against vRO ${auth.hostname} using refreshToken...`);

    // get access token
    const params = new URLSearchParams();
    params.append('refresh_token', auth.refreshToken);
    const accessResponse = await client.post<AccessTokenResponse>(
      '/csp/gateway/am/api/auth/api-tokens/authorize',
      params
    );

    // update instance
    client.defaults.headers.common['Authorization'] = `Bearer ${accessResponse.data.access_token}`;
  } else {
    if (!auth.hostname || !auth.username || !auth.password) {
      console.error(chalk.red('Did you forget to define your VRO_HOSTNAME, VRO_USERNAME and VRO_PASSWORD fields?'));
      throw new Error('Missing vRO credentials: hostname, username, password');
    }

    console.log(`Authenticating against vRO ${auth.hostname}...`);

    // determine username and domain
    const user = auth.username.includes('@') ? auth.username.split('@')[0] : auth.username;
    const domain = auth.username.includes('@') ? auth.username.split('@')[1] : 'System Domain';

    const cachedToken = !auth.reauthenticate && resolveCachedToken(auth.hostname);
    if (cachedToken) {
      console.log('Using cached credentials');
      // update instance
      client.defaults.headers.common['Authorization'] = `Bearer ${cachedToken}`;
    } else {
      // assert version
      const about = await client.get('/vco/api/about');
      console.log(`Resolved vRO version: ${about.data.version}`);

      // attempt to get refresh token using username authentication
      let refreshResponse = await client.post<RefreshTokenResponse>(
        `/csp/gateway/am/api/login?access_token`,
        {
          username: user,
          password: auth.password,
          domain,
        },
        {
          validateStatus: null,
        }
      );

      // fallback attempt using UPN authentication
      if (refreshResponse.status >= 400) {
        refreshResponse = await client.post<RefreshTokenResponse>(`/csp/gateway/am/api/login?access_token`, {
          username: auth.username,
          password: auth.password,
          domain,
        });
      }

      // get access token
      const params = new URLSearchParams();
      params.append('refresh_token', refreshResponse.data.refresh_token);
      const accessResponse = await client.post<AccessTokenResponse>(
        '/csp/gateway/am/api/auth/api-tokens/authorize',
        params
      );

      // update instance
      client.defaults.headers.common['Authorization'] = `Bearer ${accessResponse.data.access_token}`;

      cacheToken(auth.hostname, accessResponse.data);
    }
  }

  client.defaults.baseURL = `https://${auth.hostname}/vco/api`;

  return client;
}

function resolveCachedToken(hostname: string): string | null {
  const cacheFile = path.join(CACHE_DIR, `${hostname}.vro.json`);
  if (fs.existsSync(cacheFile)) {
    console.debug(`Reading token from ${cacheFile}`);
    const data: AccessTokenResponse = fs.readJSONSync(cacheFile);

    // evaluate expiration
    const jwt = Buffer.from(data.access_token.split('.')[1], 'base64').toString('ascii');
    const jwtData = JSON.parse(jwt);
    const expiration = moment(jwtData.exp * 1000);
    console.debug(`Token expires on ${expiration.toLocaleString()}`);
    return expiration.diff(moment()) < 300 ? null : data.access_token;
  } else {
    return null;
  }
}

function cacheToken(hostname: string, data: AccessTokenResponse) {
  const cacheFile = path.join(CACHE_DIR, `${hostname}.vro.json`);
  fs.writeJSONSync(cacheFile, data);
}
