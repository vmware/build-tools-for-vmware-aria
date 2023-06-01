

'''
/**
Example Python-based Polyglot action
@vro_type         polyglot
@vro_entrypoint   handler.handler
@vro_version      1.0.0
@vro_input        {string} username Service user's username
@vro_input        {SecureString} password Service user's password
@vro_input        {boolean} customFlag
@vro_output       {Array/string} operation results
*/
'''

import json

def handler(context, inputs):
    print(json.dumps(inputs, indent=2))
    return ['foo', 'bar']
