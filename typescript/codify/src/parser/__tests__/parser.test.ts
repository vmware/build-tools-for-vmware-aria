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


const MOCKS: { [filePath: string]: string | string[] } = {

  'noAction.txt': '',

  'polyglotAction.js': `
    /**
     * Example polyglot action
     * @vro_type         polyglot
     * @vro_id           aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
     * @vro_name         examplePolyglotAction
     * @vro_module       com.vmware.acoe.demo
     * @vro_entrypoint   handler.handler
     * @vro_version      1.0.1
     * @vro_input        {string} username Service user's username
     * @vro_input        {SecureString} password Service user's password
     * @vro_input        {boolean} customFlag
     * @vro_output       {Array/string} operation results
     */
  `,

  './emptyDirectory': [],
  './fullDirectory': ['polyglotAction.js'],
};

// This mock needs to be defined before importing the Parser module as it loads the globby
// module and directly consumes it. Think of how to move this to a separate module and mock
// that module instead.
jest.mock('globby', () => jest.fn((_patterns: string[], _options: { cwd: string }) => {
  return Promise.resolve(MOCKS[_options.cwd]);
}));

import fs from 'fs-extra';
import { PolyglotAction } from '../../model/actions';
import { Parser } from '../parser';

describe('Parser', () => {

  let readFileSpy: jest.SpyInstance;
  let pathExistsSpy: jest.SpyInstance;

  beforeEach(() => {
    readFileSpy = jest
      .spyOn(fs.promises, 'readFile')
      .mockImplementation((_path, _options) => Promise.resolve(<string>MOCKS[<string>_path]));

    pathExistsSpy = jest
      .spyOn(fs, 'pathExists')
      .mockImplementation((_path, _options) => Promise.resolve(MOCKS[_path] !== undefined));

  });

  it('Parse file - action', async () => {
    const obj = await Parser.parseFile('polyglotAction.js');
    expect(pathExistsSpy).toHaveBeenCalledWith('polyglotAction.js');
    expect(readFileSpy).toHaveBeenCalledWith('polyglotAction.js', "utf-8");
    expect(obj).not.toBeNull();
  });

  it('Parse file - no action', async () => {
    const obj = await Parser.parseFile('noAction.txt');
    expect(obj).toBeNull();
  });

  it('File does not exist', async () => {
    await expect(Parser.parseFile('nonExistentFile.js'))
      .rejects
      .toThrow();
  });

  it('Directory does not exist', async () => {
    await expect(Parser.parseDirectory('./nonExistentDir'))
      .rejects
      .toThrow();
  });

  it('Parse directory with no files', async () => {
    await expect(Parser.parseDirectory('./emptyDirectory'))
      .resolves
      .toEqual([])
  });

  it('Parse directory', async () => {
    const parseDirectoryResult = await Parser.parseDirectory('./fullDirectory');
    expect(parseDirectoryResult).toHaveLength(1);
    expect(parseDirectoryResult[0]).toBeInstanceOf(PolyglotAction);
  });

});
