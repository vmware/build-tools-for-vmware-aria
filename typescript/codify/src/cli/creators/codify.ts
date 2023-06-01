

import nconf from 'nconf';
import prompts from 'prompts';
import fs from 'fs-extra';
import path from 'path';
import { getProjectPath, installProjectDependencies } from '../../utils';

// ====================================
// Codify Project
// ====================================

/**
 * Ask the user for inputs that will parametrize the
 * Codify project creation.
 */
export async function createCodifyProjectAsk() {
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
      type: 'confirm',
      name: 'content',
      message: 'Add sample content?',
      initial: false,
    },
    {
      type: 'confirm',
      name: 'deps',
      message: 'Install project dependencies?',
      initial: true,
    },
  ]);

  // copy template in dir
  await createCodifyProject(prompt.path, prompt.content);

  if (prompt.deps) {
    await installProjectDependencies(prompt.path);
  }
}

/**
 * Create the project structure from a project name.
 * 1. Create project folder
 * 2. Copy template files
 * 3. Generate package.json
 * 4. Update template files
 * @param projectName {string}
 */
export async function createCodifyProject(projectName: string, includeContent: boolean) {
  const projectRoot = getProjectPath(projectName);

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

  // create directory
  await fs.mkdir(projectRoot, { recursive: true });

  // create package.json
  const packageJson = {
    name: projectName,
    version: '1.0.0',
    description: `Codify project (generated with Codify v${nconf.get('VERSION')})`,
    private: true,
    scripts: {
      'format:check': 'prettier --check .',
      'format:write': 'prettier --write .',
      'lint:check': 'eslint . --ignore-pattern .gitignore',
      'lint:fix': 'eslint --fix . --ignore-pattern .gitignore',
      test: 'jest --coverage',
    },
    'pre-commit': ['lint:check'],
    keywords: ['vro'],
    author: process.env.user || 'ACoE',
    license: 'ISC',
    devDependencies: {
      axios: '^0.24.0',
      dotenv: '^10.0.0',
      eslint: '^8.3.0',
      'eslint-config-prettier': '^8.3.0',
      'eslint-plugin-header': '^3.1.1',
      'eslint-plugin-prettier': '^4.0.0',
      jest: '^27.5.1',
      minimist: '^1.2.5',
      'pre-commit': '^1.2.2',
      prettier: '^2.4.1',
    },
  };

  const templateDir = path.normalize(path.join(__dirname, '..', '..', '..', 'templates', 'project'));
  await fs.copy(templateDir, projectRoot, {
    filter: (src) => (!includeContent ? !src.toLowerCase().includes('example') : true),
  });
  await fs.writeJSON(path.join(projectRoot, 'package.json'), packageJson, { spaces: 2, encoding: 'utf-8' });
  await fs.rename(path.join(projectRoot, '.env.template'), path.join(projectRoot, '.env'));
  await fs.rename(path.join(projectRoot, '.gitignore.template'), path.join(projectRoot, '.gitignore'));
}
