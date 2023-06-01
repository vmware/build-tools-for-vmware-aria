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
const VRA_CLOUD_HOSTNAME = 'api.mgmt.cloud.vmware.com';

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

type VraAuthentication = {
  hostname?: string;
  username?: string;
  password?: string;
  refreshToken?: string;
  reauthenticate?: boolean;
};

export async function getVraClient(auth: VraAuthentication): Promise<AxiosInstance> {
  const client = axios.create({
    baseURL: `https://${auth.hostname}`,
    httpsAgent: new https.Agent({ rejectUnauthorized: false }),
  });

  // add interceptor for error formatting
  client.interceptors.response.use(
    (response) => response,
    (error) => formatAndRethrowAxiosError(error)
  );

  if (auth.refreshToken) {
    if (!auth.hostname) {
      console.error(chalk.red('Did you forget to define your VRA_HOSTNAME fields?'));
      throw new Error('Missing vRA credentials: hostname');
    }

    console.log(`Authenticating against vRA ${auth.hostname} using refreshToken...`);

    // get access token
    if (auth.hostname?.endsWith(VRA_CLOUD_HOSTNAME)) {
      const accessResponse = await client.post<{ token: string }>('/iaas/api/login', {
        refreshToken: auth.refreshToken,
      });
      // update instance
      client.defaults.headers.common['Authorization'] = `Bearer ${accessResponse.data.token}`;
    } else {
      const params = new URLSearchParams();
      params.append('refresh_token', auth.refreshToken);
      const accessResponse = await client.post<AccessTokenResponse>(
        '/csp/gateway/am/api/auth/api-tokens/authorize',
        params
      );

      // update instance
      client.defaults.headers.common['Authorization'] = `Bearer ${accessResponse.data.access_token}`;
    }
  } else {
    if (!auth.hostname || !auth.username || !auth.password) {
      console.error(chalk.red('Did you forget to define your VRA_HOSTNAME, VRA_USERNAME and VRA_PASSWORD fields?'));
      throw new Error('Missing vRA credentials: hostname, username, password');
    }

    if (auth.hostname?.endsWith(VRA_CLOUD_HOSTNAME)) {
      throw new Error('Authenticating against vRA Cloud is not supported using username and password credentials');
    }

    console.log(`Authenticating against vRA ${auth.hostname}...`);

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
      const about = await client.get('/csp/gateway/portal/config.json');
      const version = Buffer.from(about.data.applicationVersion, 'base64').toString('ascii');
      console.log(`Resolved vRA version: ${version}`);

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

  // add interceptor validating presence of apiVersion query param
  client.interceptors.request.use(
    (config) => {
      // Note: event-broker does not support apiVersion param
      if (!config.url?.includes('apiVersion') && !config.url?.includes('event-broker/api')) {
        console.warn(`Missing 'apiVersion' parameter for request: ${config.method?.toUpperCase()} ${config.url}`);
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  return client;
}

function resolveCachedToken(hostname: string): string | null {
  const cacheFile = path.join(CACHE_DIR, `${hostname}.vra.json`);
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
  const cacheFile = path.join(CACHE_DIR, `${hostname}.vra.json`);
  fs.writeJSONSync(cacheFile, data);
}
