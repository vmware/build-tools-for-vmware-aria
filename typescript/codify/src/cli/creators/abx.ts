

import nconf from 'nconf';
import prompts from 'prompts';
import fs from 'fs-extra';
import path from 'path';
import chalk from 'chalk';
import { getProjectPath } from '../../utils';

// ====================================
// ABX Project
// ====================================

/**
 * Ask the user for inputs that will parametrize the
 * ABX project creation.
 */
export async function createAbxProjectAsk() {
  const prompt = await prompts([
    {
      type: 'text',
      name: 'path',
      message: 'What is your project named?',
      initial: 'my-project',
      validate: (name: string) => {
        const validSymbolsOnly = /^[0-9a-z-_]+$/g.test(name);

        if (!validSymbolsOnly) {
          return 'Your project name contains invalid symbols';
        }

        if (fs.pathExistsSync(name)) {
          return `Directory ${name} already exists`;
        }

        return true;
      },
    },
    {
      type: 'select',
      name: 'runtime',
      message: 'What is your ABX runtime?',
      choices: [
        { title: 'Python', value: 'python', selected: true },
        { title: 'NodeJS (JavaScript)', value: 'javascript' },
        { title: 'NodeJS (TypeScript)', value: 'typescript' },
        { title: 'PowerShell', value: 'powershell' },
      ],
    },
  ]);

  // copy template in dir
  await createAbxProject(prompt.path, prompt.runtime);
}

/**
 * Create the project structure from a project name.
 * 1. Create project folder
 * 2. Copy template files
 * 3. Generate setup.py
 * 4. Update template files
 * @param projectName {string}
 * @param runtime {string}
 */
async function createAbxProject(projectName: string, runtime = 'python') {
  const projectRoot = getProjectPath(projectName);

  const templateDir = path.normalize(path.join(__dirname, '..', '..', '..', 'templates', `abx-${runtime}`));
  if (!fs.pathExistsSync(templateDir)) {
    return console.error(chalk.red(`ABX actions based on ${runtime} are not supported yet. Stay tuned`));
  }

  // create project directory
  await fs.mkdir(projectRoot, { recursive: true });

  // copy template files
  await fs.copy(templateDir, projectRoot);
  await fs.rename(path.join(projectRoot, '.env.template'), path.join(projectRoot, '.env'));
  await fs.rename(path.join(projectRoot, '.gitignore.template'), path.join(projectRoot, '.gitignore'));

  if (runtime === 'python' || runtime === 'powershell') {
    // add project meta to setup.py for Python-based actions
    const meta =
      '' +
      `AUTHOR = "${process.env.user || 'ACoE'}"\n` +
      `AUTHOR_EMAIL = "acoe-eng@vmware.com"\n` +
      `NAME = "${projectName}"\n` +
      `DESC = "Codify ABX project"\n` +
      `VERSION = "1.0.0"\n` +
      `LICENSE = "ISC"\n`;

    let data = await fs.readFile(path.join(projectRoot, 'setup.py'), { encoding: 'utf-8' });
    data = data.replace(/#<CODIFY_META>/g, meta);
    await fs.writeFile(path.join(projectRoot, 'setup.py'), data, { encoding: 'utf-8' });
  } else if (runtime === 'javascript') {
    // generate package.json for Javascript-based actions
    const packageJson = {
      name: projectName,
      version: '1.0.0',
      description: `Codify ABX project (generated with Codify v${nconf.get('VERSION')})`,
      private: true,
      scripts: {
        'format:check': 'prettier --check .',
        'format:write': 'prettier --write .',
        'lint:check': 'eslint . --ignore-pattern .gitignore',
        'lint:fix': 'eslint --fix . --ignore-pattern .gitignore',
        build: 'gulp build',
        clean: 'gulp clean',
        test: 'jest --coverage',
      },
      'pre-commit': ['lint:check'],
      keywords: ['abx', 'vra', 'vra8'],
      author: process.env.user || 'ACoE',
      license: 'ISC',
      devDependencies: {
        '@types/jest': '^27.4.1',
        'adm-zip': '^0.5.9',
        axios: '^0.25.0',
        dotenv: '^14.3.0',
        eslint: '^8.3.0',
        'eslint-config-prettier': '^8.3.0',
        'eslint-plugin-header': '^3.1.1',
        'eslint-plugin-jest': '^26.1.4',
        'eslint-plugin-prettier': '^4.0.0',
        'fs-extra': '^10.0.0',
        globby: '^13.0.0',
        gulp: '^4.0.2',
        jest: '^27.5.1',
        'js-yaml': '^4.1.0',
        minimist: '^1.2.5',
        'pre-commit': '^1.2.2',
        prettier: '^2.4.1',
        uuid: '^8.3.2',
        which: '^2.0.2',
      },
      dependencies: {},
    };

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

    await fs.writeJSON(path.join(projectRoot, 'package.json'), packageJson, { spaces: 2, encoding: 'utf-8' });
  }
}
