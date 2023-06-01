

require('dotenv').config();
const util = require('util');
const crypto = require('crypto');
const https = require('https');
const axios = require('axios');
const yaml = require('js-yaml');
const fs = require('fs-extra');
const path = require('path');

const argv = require('minimist')(process.argv.slice(2));

// ======================================================
// Load action
// ======================================================
const entrypoint = argv._[0];
const actionModule = entrypoint.split('.').slice(0, -1).join('/');
const handlerFunction = entrypoint.split('.').pop();
const action = require(`./src/${actionModule}`);

// ======================================================
// Load inputs from inputs file (if passed) and override
// them with inputs passed as CLI options
// ======================================================
let inputs = {};
const inputsFile = argv._[1] || null;
if (inputsFile && fs.existsSync(inputsFile)) {
  console.log(`Using inputs file ${inputsFile}`);
  const inputsFileContents = yaml.load(fs.readFileSync(inputsFile));
  inputs = { ...inputsFileContents };
}
Object.entries(argv)
  .filter(([key]) => key !== '_')
  .forEach(([key, value]) => (inputs[key] = value));

// ======================================================
// The context contains a token resolver that would be part of the execution
// context object passed to the action in ABX.
// ======================================================
const context = {
  getSecret: (secretId) => {
    console.log(`getSecret called with secretId=${secretId}`);
    return inputs[secretId] || null;
  },
  executionId: crypto.randomUUID(),
  request: initializeRequest(),
  getToken: async () => {
    // the vRA host and credentials are taken from the environment or passed as arguments
    if (
      (!argv._user || !argv._pass || !argv._host) &&
      !process.env.VRA_USER &&
      !process.env.VRA_PASS &&
      !process.env.VRA_HOST
    ) {
      throw new Error('Cannot resolve token. You need to specify VRA_HOST, VRA_USER and VRA_PASS');
    }

    const user = argv._user || process.env.VRA_USER;
    const password = argv._pass || process.env.VRA_PASS;
    const host = argv._host || process.env.VRA_HOST;

    // determine username and domain
    const username = user.includes('@') ? user.split('@')[0] : user;
    const domain = user.includes('@') ? user.split('@')[1] : 'System Domain';

    // create client for token resolution
    const client = axios.create({
      baseURL: `https://${host}`,
      httpsAgent: new https.Agent({ rejectUnauthorized: false }),
    });

    // get refresh token
    const refreshResponse = await client.post(`/csp/gateway/am/api/login?access_token`, {
      username,
      password,
      domain,
    });

    // get access token
    const params = new URLSearchParams();
    params.append('refresh_token', refreshResponse.data.refresh_token);
    const accessResponse = await client.post('/csp/gateway/am/api/auth/api-tokens/authorize', params);

    return accessResponse.data.access_token;
  },
};

// ======================================================
// Action invocation
// ======================================================
(async function () {
  console.log('===================================');
  console.log('ACTION START');
  console.log('===================================');
  const start = Date.now();

  let error = null;
  let result = null;

  try {
    if (!action[handlerFunction]) {
      throw new Error(`Module does not export function ${handlerFunction}`);
    }

    if (action[handlerFunction].length === 3) {
      result = await resolveAction(action, context, inputs);
    } else {
      result = await action[handlerFunction](context, inputs);
    }
  } catch (err) {
    error = err;
  }

  const duration = Date.now() - start;
  console.log('===================================');
  console.log(`Action completed in ${duration / 1000}s`);
  if (error) {
    console.error(`Error:`, error);
  } else {
    console.log(`Result:`, util.inspect(result, false, null, true));
  }
  console.log('===================================');
})();

async function resolveAction(action, context, argv) {
  return new Promise((resolve, reject) => {
    action.handler(context, argv, (err, data) => {
      if (err) {
        reject(err);
      } else {
        resolve(data);
      }
    });
  });
}

function initializeRequest() {
  return async function (link, operation, body = {}, privileged = false, passedHeaders = {}, handler = undefined) {
    let headers = {
      'Content-type': 'application/json',
      Accept: 'application/json',
      ...passedHeaders,
    };

    if (privileged) {
      headers['x-abx-privileged'] = 'true';
      console.log('Running in privileged mode...');
    }
    if (!operation) {
      throw new Error("'operation' parameter on context.request() is not provided!");
    }
    if (!link) {
      throw new Error("'link' parameter on context.request() is not provided!");
    }
    let op = operation.toUpperCase();

    let targetUri;
    if (link && link.startsWith('/')) {
      const proxyData = await buildAbxProxyUri(link, headers);
      targetUri = proxyData.link;
      headers = proxyData.headers;
    } else {
      targetUri = link;
    }

    const method = op;
    const conf = {
      url: targetUri,
      headers: headers,
      method: method,
    };
    if (op.trim() === 'GET') {
      return promiseRequest(conf, handler);
    } else if (op.trim() !== 'PATCH' && op.trim() !== 'POST' && op.trim() !== 'PUT' && op.trim() !== 'DELETE') {
      throw 'Unsupported operation on context.request(): ' + operation;
    }

    conf.body = body;
    return promiseRequest(conf, handler);
  };
}

function promiseRequest(conf, handler) {
  const parsedUrl = new URL(conf.url);
  const agent = new https.Agent({
    rejectUnauthorized: false,
  });
  const options = {};

  options.headers = conf.headers;
  options.url = conf.url;
  options.method = conf.method;
  options.data = conf.body;
  if (parsedUrl.protocol === 'https:') {
    options.httpsAgent = agent;
  } else {
    options.httpAgent = agent;
  }

  // Convert to ms, to maintain a generalization between runtimes.
  options.timeout = 60 * 1000;

  return new Promise((resolve, reject) => {
    // Make the request
    axios(options)
      // Handle response
      .then((response) => {
        const clientResponse = {
          content: response.data,
          headers: response.headers,
          status: response.status,
        };
        if (handler) {
          try {
            handler(clientResponse, null);
          } catch (error) {
            console.error('Exception in handler: ' + error);
          }
        }
        resolve(clientResponse);
      })

      // Handle errors that might occur during the request
      .catch((error) => {
        // https://axios-http.com/docs/handling_errors
        let errorDetails = null;
        if (error.response) {
          // The request was made and the server responded with a status code
          // that falls out of the range of 2xx
          errorDetails = {
            content: error.response.data,
            headers: error.response.headers,
            status: error.response.status,
          };
        } else {
          // The request was made but no response was received
          // OR Something happened in setting up the request that triggered an Error
          errorDetails = error.message;
        }

        if (handler) {
          try {
            handler(null, errorDetails);
          } catch (err) {
            console.log('Exception in handler: ' + err);
          }
        }
        console.error(`The following error occurred: ${util.inspect(errorDetails)}`);
        reject(errorDetails);
      });
  });
}

/**
 * Update the link and headers to allow vRA API calls.
 * @param {string} link
 * @param {any} headers
 * @returns the updated link and headers
 */
async function buildAbxProxyUri(link, headers) {
  const host = argv._host || process.env.VRA_HOST;

  // use cached or explicit token if available
  let token = getCachedToken();
  if (!token) {
    token = await context.getToken();
    cacheToken(token);
  }

  headers = { ...headers, Authorization: `Bearer ${token}` };
  return { link: `https://${host}${link}`, headers };
}

function getCachedToken() {
  const token = argv._access_token || process.env.VRA_ACCESS_TOKEN;
  if (token) {
    const jwt = Buffer.from(token.split('.')[1], 'base64').toString('ascii');
    const jwtData = JSON.parse(jwt);
    const expiration = jwtData.exp * 1000;
    const now = Date.now();
    return expiration - now < 300 * 1000 ? null : token;
  }
  return null;
}

function cacheToken(token) {
  const dotEnvFile = path.join(__dirname, '.env');
  if (fs.existsSync(dotEnvFile)) {
    const content = fs.readFileSync(dotEnvFile, 'utf-8');
    if (content.includes(`VRA_ACCESS_TOKEN=`)) {
      const replacedContent = content.replace(/^VRA_ACCESS_TOKEN=.*$/g, `VRA_ACCESS_TOKEN=${token}`);
      fs.writeFileSync(dotEnvFile, replacedContent);
    } else {
      fs.appendFileSync(dotEnvFile, `VRA_ACCESS_TOKEN=${token}`);
    }
  }
  argv._access_token = token;
}
