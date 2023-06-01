
'''
/**
Example Python-based ABX action
@abx_type         abx
@abx_name         Example PY ABX Action
@abx_project      ref:name:Development
@abx_entrypoint   handler
@abx_input        {string} username username
@abx_input        {string} password password
@abx_input        {string} customFlag
*/
'''

import json

def handler(context, inputs):
    print(json.dumps(inputs, indent=2))
    return ['foo', 'bar']
