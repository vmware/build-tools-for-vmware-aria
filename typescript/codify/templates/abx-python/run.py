import urllib.request as urlopen
from dotenv import load_dotenv
from datetime import datetime
from collections import UserDict
import time
import re
import base64
import sys
import os
import importlib
import argparse
import yaml
import json
import socket
import ssl
import uuid

load_dotenv()

CACHED_ACCESS_TOKEN = None


def write_error(error):
    print(error, file=sys.stderr, flush=True)


def dump_object(obj):
    return json.dumps(obj, separators=(',', ':'), indent=2)


def write_output(result):
    to_string = dump_object(result)
    print(f'Result: {to_string}', file=sys.stdout, flush=True)


def do_request(method, uri, headers, data=None):

    if data:
        if isinstance(data, dict):
            data = json.dumps(data).encode()
        if isinstance(data, str):
            data = data.encode()

    req = urlopen.Request(uri, data, headers)
    req.get_method = lambda: method

    timeoutSecs = 60
    if not isinstance(timeoutSecs, float):
        timeoutSecs = float(timeoutSecs)

    try:
        # Do not check for certificates because the communication is internal
        resp = urlopen.urlopen(req, timeout=timeoutSecs,
                               context=ssl._create_unverified_context())

        client_response = {
            'content': resp.read(),
            'status': resp.getcode(),
            'headers': convert_to_dict(resp.info().items())
        }
        return client_response

    except urlopen.URLError as e:
        print(f'Unable to perform proxy call because {str(e)} .')
        client_response = {
            'content': e.read(),
            'status': e.getcode(),
            'headers': convert_to_dict(e.info().items())
        }
        return client_response

    except (socket.timeout, ssl.SSLError):
        print(
            f'Exceeded {str(timeoutSecs)} secs timeout for proxy call. Unable to perform it.')


def convert_to_dict(items):
    dict = {}
    for k, v in items:
        dict[k] = v
    return dict


def build_local_request_headers(target_uri, headers):

    access_token = get_cached_token()
    hostname = os.environ.get('VRA_HOST', None)
    username = os.environ.get('VRA_USER', None)
    password = os.environ.get('VRA_PASS', None)

    if not access_token:

        req_headers = {'Content-type': 'application/json',
                       'Accept': 'application/json'}

        domain = 'System Domain'
        user = username
        if '@' in username:
            parts = username.split('@')
            user = parts[0]
            domain = parts[1]

        # get refresh token
        res = do_request('POST', f'https://{hostname}/csp/gateway/am/api/login?access_token=true', req_headers, {
            'username': user,
            'password': password,
            'domain': domain
        })

        refresh_token = json.loads(res.get('content')).get('refresh_token')

        # exchange refresh token for access token
        res = do_request(
            'POST', f'https://{hostname}/csp/gateway/am/api/auth/api-tokens/authorize?refresh_token={refresh_token}', req_headers, None)

        access_token = json.loads(res.get('content')).get('access_token')

        cache_token(access_token)

    # update headers and target_uri
    headers['Authorization'] = f'Bearer {access_token}'
    target_uri = f'https://{hostname}{target_uri}'
    return target_uri, headers


def get_cached_token():
    global CACHED_ACCESS_TOKEN
    access_token = CACHED_ACCESS_TOKEN or os.environ.get(
        'VRA_ACCESS_TOKEN', None)

    # Evaluate cached token validity
    if access_token:
        jwt = decode_jwt(access_token)
        expiration = jwt.get('exp', None)
        now = int(time.mktime(datetime.now().timetuple()))
        return None if expiration - now < 300 else access_token

    return access_token


def decode_jwt(jwt):
    header, payload, sig = jwt.split('.')
    b64_bytes = payload.encode('ascii')
    rem = len(b64_bytes) % 4
    if rem > 0:
        b64_bytes += b"=" * (4 - rem)
    return json.loads(base64.urlsafe_b64decode(b64_bytes))


def cache_token(token):
    global CACHED_ACCESS_TOKEN

    dotenv_file = os.path.join(os.path.dirname(__file__), '.env')
    if os.path.exists(dotenv_file):

        with open(dotenv_file, 'r', encoding='utf-8') as fp:
            content = fp.read()

        if ('VRA_ACCESS_TOKEN=') in content:
            with open(dotenv_file, 'w', encoding='utf-8') as fp:
                content = re.sub('^VRA_ACCESS_TOKEN=.*$',
                                 f'VRA_ACCESS_TOKEN={token}', content, flags=re.MULTILINE)
                fp.write(content)
            pass
        else:
            with open(dotenv_file, 'a', encoding='utf-8') as fp:
                fp.write(f'VRA_ACCESS_TOKEN={token}\n')

    CACHED_ACCESS_TOKEN = token


class Context:

    def __init__(self):
        run_id = str(uuid.uuid4())
        self.request = self._initialize_request(run_id)
        self.get_secret = self._initialize_get_secret(run_id)

    def _initialize_request(self, run_id: str):

        def execute_delegate(target_uri: str, operation: str, body, **kwargs):
            headers = {'Content-type': 'application/json',
                       'Accept': 'application/json'}
            if "headers" in kwargs:
                for key, val in kwargs['headers'].items():
                    headers[key] = val

            if "privileged" in kwargs and kwargs['privileged'] is True:
                headers['x-abx-privileged'] = 'true'
                print('Running in privileged mode...')

            op = operation.upper()
            if target_uri.startswith("/"):
                target_uri, headers = build_local_request_headers(
                    target_uri, headers)

            if op == 'GET':
                resp = do_request(op, target_uri, headers)
            elif op == 'POST':
                resp = do_request(op, target_uri, headers, body)
            elif op == 'PATCH':
                resp = do_request(op, target_uri, headers, body)
            elif op == 'PUT':
                resp = do_request(op, target_uri, headers, body)
            elif op == 'DELETE':
                resp = do_request(op, target_uri, headers)
            else:
                print(
                    f'Unsupported operation on context.request(...) operation: {operation}')
                raise Exception(
                    f'Unsupported operation on context.request(...) operation: {operation}')
            if 'handler' in kwargs:
                kwargs['handler'](resp)
            else:
                return resp

        return execute_delegate

    def _initialize_get_secret(self, run_id):
        def get_secret(secret):
            return os.environ.get(secret, secret)

        return get_secret


class ActionInputs(UserDict):
    def __getitem__(self, key):
        return os.environ.get(self.data[key], self.data[key]) if type(self.data[key]) == str else self.data[key]


def main():
    '''
    Main entrypoint for running actions
    '''

    # Define arguments
    parser = argparse.ArgumentParser(description='Action runner')
    parser.add_argument('entrypoint', type=str,
                        help='path entry point in dot format')
    parser.add_argument('inputs', type=str, help='inputs file')
    parser.add_argument('--base', type=str, default='src',
                        help='entrypoint base')
    args = parser.parse_args()

    inputs_file = os.path.realpath(args.inputs)
    os.chdir(os.path.join(os.path.dirname(__file__), args.base))

    module_name = ''
    function_name = ''

    # Parse the entrypoint
    parts = args.entrypoint.split('.')
    if len(parts) == 2:
        module_name = parts[0]
        function_name = parts[1]
    elif len(parts) == 1:
        module_name = 'handler'
        function_name = parts[0]
    else:
        write_error('Invalid entrypoint')
        exit(1)

    # Load entrypoint module
    try:
        current_work_dir = os.getcwd()
        sys.path.insert(0, current_work_dir)
        loaded_module = importlib.import_module(module_name)
    except Exception as e:
        write_error(e)
        exit(1)

    # Validate entrypoint function
    if not hasattr(loaded_module, function_name):
        write_error(
            f'Cannot find handler function {function_name} in module {module_name}')
        exit(1)

    handler = getattr(loaded_module, function_name)

    print('===================================')
    print('ACTION START')
    print('===================================')

    try:
        with open(inputs_file) as fp:
            inputs = yaml.safe_load(fp.read())

        action_start = datetime.now()
        result = handler(Context(), ActionInputs(inputs))
        action_end = datetime.now()

        print('===================================')
        print(f'Action completed in {action_end - action_start}')
        write_output(result if result is not None else {})
        print('===================================')

    except Exception as e:
        write_error(e)
        raise e

    finally:
        print("Finished running action code.")
        sys.stdout.flush()
        sys.stderr.flush()
        print("Exiting python process.")


if __name__ == "__main__":
    main()
