#!/usr/bin/python

#  How to use this script:
#     0. Copy the script to the same directory as the plugins and run it. The script will install the plugins on the cluster located in the same directory.
#     1. Modify the main method. "iterate_plugins_state" will build a dictionary with the current/desired state of the plugins and pass it to the callback function
#     2. You can use the callback function to print the current/desired state of the plugins
#     3. You can use the callback function to install the plugins that are not installed on the cluster or have a different version
#     4. Write your own callback function and pass it to the iterate_plugins_state function in order to achieve your desired state
#     5. Each callback must have the following signature: callback(current_state: Dict[str, Dict], desired_state: Dict[str, Dict]) and must exit the script

import subprocess
import json
import re
import glob
import os
from typing import List, Dict, Tuple

SUCCESS_EXEC = "SUCCESS_360"
FAILURE_EXEC = "FAILURE"
VERSION_REGEX = r'^(\d+\.)?(\d+\.)?(\*|\d+)$'


def _extract_name_and_version_from_whl(whl_name: str) -> Tuple[str, Tuple[int]]:
    name_parts = whl_name.split('-')

    plugin_name = name_parts[0]
    if not plugin_name:
        print(f" {whl_name} does not have a valid name")
        err_exit()

    v = list(filter(lambda n: not not re.match(VERSION_REGEX, n), name_parts))
    if not v or len(v) != 1:
        print(f" {whl_name} does not have a valid version")
        err_exit()

    version = str(v[0])
    return os.path.basename(plugin_name), tuple(map(int, version.split('.')))


def get_installed_idem_plugins() -> Dict:
    """Get the installed plugins on the cluster

    Returns:
        dict: Plugins installed on the cluster
    """
    # vracli -j plugin --idem list
    params = ['vracli', '-j', 'plugin', '--idem', 'list']
    print(f"CMD: {' '.join(params)}")
    result = subprocess.run(params, capture_output=True, text=True)
    plugins = json.loads(result.stdout)
    # {"status_code": 0, "output_data": [{"name": "idem_aws-1.6.3-py3-none-any.whl", "nodes": ["sc2-10-186-224-195.nimbus.eng.vmware.com", "sc2-10-186-224-127.nimbus.eng.vmware.com", "sc2-10-186-231-253.nimbus.eng.vmware.com"]}], "error": "", "logs": ""}
    return plugins


def install_idem_plugin_from_file(plugin: Dict) -> Dict:
    """Install a plugin on the cluster using the vracli

    Args:
        whl_file (str): Plugin to be installed. Wheel Filename

    Returns:
        dict: Status of the installation
    """
    # vracli -j plugin --idem install --file <path_to_file> <plugin_name>
    params = ['vracli', '-j', 'plugin', '--idem', 'install', '--file', f'{plugin["whl"]}', f'{plugin["name"]}']
    print(f"CMD: {' '.join(params)}")
    result = subprocess.run(params, capture_output=True, text=True)
    json_res = json.loads(result.stdout)

    return json_res

# currently unused


def get_installed_plugin_for_node(worker_node: str, plugin: str) -> Dict:
    """Get the installed plugin for a specific node using kubectl

    Args:
        node (str): Node name
        plugins (dict): Plugins installed on the cluster

    Returns:
        dict: Plugin installed on the node
    """
    # kubectl -n prelude exec -it idem-service-worker-78bd85cb7-z8r2d -- pip list --format json
    params = ['kubectl', '-n', 'prelude', 'exec', '-it', f'{worker_node}', '--', 'pip', 'list', '--format', 'json']
    print(f"CMD: {' '.join(params)}")
    result = subprocess.run(params, capture_output=True, text=True)
    plugins = json.loads(result.stdout)
    return list(filter(lambda n: n['name'] == plugin, plugins))


def iterate_plugins_state(plgns: List[Dict], whl_files: List[str], callback):
    """Build a dictionary with the current/desired state of the plugins and pass it to the callback function

    Args:
        plgns (_type_): Plugins installed on the cluster
        whl_files (_type_): Plugins to be installed
        callback (function): Callback function to be called with the current/desired state of the plugins
    """
    current_state = {}
    for plgn in plgns.get('output_data', []):
        plugin_name, version = _extract_name_and_version_from_whl(plgn['name'])
        current_state[plgn['name']] = {
            "name": plugin_name,
            "version": version,  # currently unused, maybe in the future
            "whl": os.path.join(os.path.dirname(os.path.abspath(__file__)), plgn['name']),
            "nodes": plgn["nodes"]
        }

    desired_state = {}
    for whl in whl_files:
        plugin_name, version = _extract_name_and_version_from_whl(whl)
        desired_state[os.path.basename(whl)] = {
            "name": plugin_name,
            "version": version,  # currently unused, maybe in the future
            "whl": os.path.join(os.path.dirname(os.path.abspath(__file__)), whl),
            "nodes": []
        }

    callback(current_state, desired_state)


def print_cb(current_state: Dict[str, Dict], desired_state: Dict[str, Dict]):
    """Print the current/desired state of the plugins

    Args:
        current_state (dict): {'idem_aws-1.6.3-py3-none-any.whl': {'name': 'idem_aws', 'version': (1, 6, 3), 'whl': 'idem_aws-1.6.3-py3-none-any.whl' 'nodes': ['sc2-10-186-224-195.nimbus.eng.vmware.com']}}
        desired_state {dict}: {}
    """
    print(f"Current: {json.dumps(current_state, indent=4)}")
    print(f"Desired: {json.dumps(desired_state, indent=4)}")

    success_exit()


def install_desired_plugins(current_state: Dict[str, Dict], desired_state: Dict[str, Dict]):
    """Install the plugins that are not installed on the cluster or have a different version

    Args:
        current_state (Dict): Current state of the plugins
        desired_state (Dict): Desired state of the plugins
    """

    # Get the differnece between the current and desired state
    diff_keys_desired = set(desired_state.keys()) - set(current_state.keys())

    if len(diff_keys_desired) == 0:
        print("No plugins to install")
        success_exit()

    for k in diff_keys_desired:
        # Install the plugin
        res = install_idem_plugin_from_file(desired_state[k])
        if res.get('status_code') != 0:
            print(f" Failed to install {desired_state[k]['whl']}")
            print(f" {res.get('error')}")
            print(f" {res.get('logs')}")
            err_exit()
        print(f" Installed {desired_state[k]['whl']}")
        print(f" {json.dumps(res, indent=4)}")
    success_exit()


def err_exit():
    print(FAILURE_EXEC)
    exit(1)


def success_exit():
    print(SUCCESS_EXEC)
    exit(0)


if __name__ == '__main__':

    whl_files = glob.glob(os.path.join(os.path.dirname(os.path.abspath(__file__)), "*.whl"))
    if len(whl_files) < 1:
        print("No wheel files found")
        err_exit()

    plgns = get_installed_idem_plugins()
    if plgns.get('status_code') == 0:
        # iterate_plugins_state(plgns, whl_files, print_cb)
        iterate_plugins_state(plgns, whl_files, install_desired_plugins)
        err_exit()

    print(f" {plgns.get('error')}")
    print(f" {plgns.get('logs')}")
    err_exit()
