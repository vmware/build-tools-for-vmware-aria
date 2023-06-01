#!/usr/bin/env python

import yaml
import uuid
import glob
import shlex
import shutil
import subprocess
from os import path, makedirs, remove, chdir, getcwd
from setuptools import Command
from setuptools import setup

ID_NAMESPACE = uuid.UUID('5cfde456-6086-4874-bea4-445967d9f345')

# Declare Python min version
python_requires = ">=3.7"

#<CODIFY_META>

# Change the CWD to the directory of this file to ensure
# proper operation when invoked from another dir.
this_file_dir = path.dirname(__file__)
if this_file_dir:
    chdir(path.dirname(__file__))
    print(f'Working directory set to {getcwd()}')

# Build production requirements
with open(path.join("requirements", "base.txt")) as f:
    REQUIREMENTS = list(
        filter(lambda x: not x.startswith('-r '), f.read().splitlines())
    )
with open(path.join("requirements", "prod.txt")) as f:
    REQUIREMENTS += list(
        filter(lambda x: not x.startswith('-r '), f.read().splitlines())
    )


class Bundle(Command):

    description = "Create ABX bundle"

    # https://dankeder.com/posts/adding-custom-commands-to-setup-py/
    user_options = [
        ('keep-build', None, 'Keep the build folder'),
        ('keep-bundle', None, 'Keep the bundle in the dist folder'),
        ('nodeps', None, 'Skip dependency bundling'),
        ('src=', 's', 'Source directory'),
        ('action=', None, 'Action to build'),
    ]

    def initialize_options(self):
        self.keep_build = False
        self.keep_bundle = False
        self.nodeps = False
        self.src = 'src'
        self.action = None

    def finalize_options(self):
        assert path.exists(
            self.src), f'Directory {self.src} does not exist. Provide a valid value for the --src option.'

    def execute(self, command, capture_output=False):
        """
        The execute command will loop and keep on reading the stdout and
        check for the return code and displays the output in real time.
        """

        print(f'Running shell command: {command}')

        if capture_output:
            return subprocess.check_output(shlex.split(command))

        process = subprocess.Popen(
            shlex.split(command), stdout=subprocess.PIPE)

        while True:
            output = process.stdout.readline().decode("utf-8").rstrip()
            if output == "" and process.poll() is not None:
                break
            if output:
                print(output)

        return_code = process.poll()

        if return_code != 0:
            print(
                f'Error running command {command} - exit code: {return_code}')
            raise IOError('Shell Commmand Failed')

        return return_code

    def run(self):

        # =================================
        # Clean up
        # =================================
        shutil.rmtree('dist', True)
        shutil.rmtree('build', True)

        # =================================
        # Detect ABX actions
        # =================================
        def is_abx(file):
            py_file = f'{path.splitext(file)[0]}.py'
            return path.exists(py_file)

        def parse_abx(file):

            print(f'Parsing file {file}')

            default_abx = {
                'exportVersion': '1',
                'exportId': '',
                'name': path.splitext(path.basename(file)[0]),
                'runtime': 'python',
                'entrypoint': 'handler.handler',
                'timeoutSeconds': 600,
                'deploymentTimeoutSeconds': 900,
                'actionType': 'SCRIPT',
                'configuration': None,
                'memoryInMB': 300,
            }

            with open(file, "r") as fp:
                abx = yaml.safe_load(fp)

            # merge the two dicts and generate an ID
            merged_abx = {**default_abx, **abx}
            merged_abx['exportId'] = str(
                uuid.uuid5(ID_NAMESPACE, merged_abx['name']))
            return merged_abx

        yaml_files = list(glob.iglob(f'{self.src}/*.yaml')) + \
            list(glob.iglob(f'{self.src}/*.yml'))
        abx_actions = map(parse_abx, filter(is_abx, yaml_files))

        # =================================
        # Copy source files
        # =================================
        shutil.copytree(self.src, 'build')

        # =================================
        # Install dependencies
        # =================================
        if not self.nodeps:
            self.execute(
                "pip3 install --upgrade -t build -r requirements/prod.txt")

        # =================================
        # Build ABX actions
        # =================================
        shutil.make_archive(path.join('dist', 'bundle'), 'zip', 'build')
        for abx in abx_actions:

            if self.action and self.action != abx["name"]:
                print(f'Skipping action {abx["name"]}')
                continue

            print(f'Bundling action {abx["name"]}')

            # Create ABX dir
            abx_dir = path.join('dist', abx["name"])
            makedirs(abx_dir)

            # Create ABX file
            abx_file = path.join(abx_dir, f'{abx["name"]}.abx')
            with open(abx_file, 'w') as fp:
                fp.write(yaml.dump(abx))

            # Create bundle file
            bundle_file = path.join(abx_dir, f'{abx["name"]}.zip')
            shutil.copyfile(path.join('dist', 'bundle.zip'), bundle_file)

            # Create final ABX
            shutil.make_archive(
                path.join('dist', f'{abx["name"]}.abx'), 'zip', abx_dir)

            # Clean up ABX dir
            shutil.rmtree(abx_dir)

        # =================================
        # Clean up
        # =================================
        if not self.keep_build:
            shutil.rmtree('build', True)
        if not self.keep_bundle:
            remove(path.join('dist', 'bundle.zip'))


setup(
    name=NAME,
    version=VERSION,
    description=DESC,
    author=AUTHOR,
    author_email=AUTHOR_EMAIL,
    license=LICENSE,
    install_requires=REQUIREMENTS,
    packages=['src'],
    cmdclass={
        "bundle": Bundle
    }
)
