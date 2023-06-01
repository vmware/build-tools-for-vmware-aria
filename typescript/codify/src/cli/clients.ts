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


import { AxiosInstance } from 'axios';
import { CodifyArguments } from '../cli';

let vroClientSingleton: AxiosInstance;
let vraClientSingleton: AxiosInstance;
import { getVraClient, getVroClient } from '../client';

export async function getVroClientSingleton(argv: CodifyArguments): Promise<AxiosInstance> {
  if (!vroClientSingleton) {
    vroClientSingleton = await getVroClient({
      authHostname: argv.vroAuthHostname,
      hostname: argv.vroHostname,
      username: argv.vroUsername,
      password: argv.vroPassword,
      refreshToken: argv.vroRefreshToken,
      reauthenticate: argv.reauthenticate,
    });
  }

  return vroClientSingleton;
}

export async function getVraClientSingleton(argv: CodifyArguments): Promise<AxiosInstance> {
  if (!vraClientSingleton) {
    vraClientSingleton = await getVraClient({
      hostname: argv.vraHostname,
      username: argv.vraUsername,
      password: argv.vraPassword,
      refreshToken: argv.vraRefreshToken,
      reauthenticate: argv.reauthenticate,
    });
  }

  return vraClientSingleton;
}
