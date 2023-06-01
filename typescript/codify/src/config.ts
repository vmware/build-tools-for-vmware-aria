

import nconf from 'nconf';
import path from 'path';
import fs from 'fs-extra';
nconf.env().argv().defaults({
  debug: false,

  // Note: do not change this namespace as it is the basis
  // for ID generation using UUIDv5
  ID_NAMESPACE: '63656844-757e-4c7a-b695-56841af8edfc',

  VERSION: (() => {
    const packageJsonPath = path.join(__dirname, "../package.json");

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
    if (fs.pathExistsSync(packageJsonPath)) {
      const packageConfig = fs.readJSONSync(packageJsonPath);
      return packageConfig.version
    }
    return 'unknown';
  })(),

  UPLOAD_ORDER: [
    'Configuration',
    'Action',
    'PolyglotAction',
    'Workflow',
    'AbxAction',
    'ResourceAction',
    'CloudTemplate',
    'Subscription'
  ],

});
export default nconf;
