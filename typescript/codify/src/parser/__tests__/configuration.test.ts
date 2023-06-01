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


import fs from 'fs-extra';
import { Configuration, IConfiguration } from '../../model';
import { ConfigurationParser } from '../configuration';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const MOCKS: { [filePath: string]: string } = {

  'config.conf.yaml': `
objectType: ConfigurationElement
id: aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
name: Sample Configuration
description: Sample Codify Configuration
version: 1.0.0
folder: ACoE/Codify
attributes:
  foo:
    type: string
    description: Foo description
  bar:
    type: SecureString
    description: Bar description
  baz:
    type: number
    description: ""
  host:
    type: REST:RESTHost
    description: "sample REST host"
  `,

  'no-conifg.conf.yaml': `
foo: bar
  `,

  'Inferred.conf.yaml': `
objectType: ConfigurationElement
  `,

};

describe('Configuration Parser', () => {

  let readFileSpy: jest.SpyInstance;
  let consoleSpy: jest.SpyInstance;

  beforeEach(() => {

    consoleSpy = jest.spyOn(global.console, 'warn');

    readFileSpy = jest
      .spyOn(fs, 'readFile')
      .mockImplementationOnce(_path => {
        const file = (<string>_path).split('/').pop() || <string>_path;
        return MOCKS[file]
          ? Promise.resolve(Buffer.from(MOCKS[file]))
          : Promise.reject(new Error('File does not exist'));
      });
  });

  it('Parse file', async () => {
    const configParser = new ConfigurationParser('config.conf.yaml');
    await configParser.parse();
    expect(readFileSpy).toHaveBeenCalledWith('config.conf.yaml', "utf-8");
  });

  it(`Parse Configuration`, async () => {
    const configParser = new ConfigurationParser('config.conf.yaml');
    await configParser.parse();
    const object = configParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Configuration);
    expect(object?.get<IConfiguration>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IConfiguration>().id).toEqual('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(object?.get<IConfiguration>().name).toEqual('Sample Configuration');
    expect(object?.get<IConfiguration>().description).toEqual('Sample Codify Configuration');
    expect(object?.get<IConfiguration>().version).toEqual('1.0.0');
    expect(object?.get<IConfiguration>().folder).toEqual('ACoE/Codify');
    expect(object?.get<IConfiguration>().attributes).toEqual(expect.objectContaining({
      foo: expect.any(Object),
      bar: expect.any(Object),
      baz: expect.any(Object),
      host: expect.any(Object)
    }));
  });

  it('Parse No Configuration', async () => {
    const configParser = new ConfigurationParser('no-conifg.conf.yaml');
    await configParser.parse();
    const object = configParser.getObjectDefinition();
    expect(object).toBeNull();
  });

  it('Failed Parse', async () => {
    const configParser = new ConfigurationParser('no-yaml.conf.yaml');
    await configParser.parse();
    const object = configParser.getObjectDefinition();
    expect(object).toBeNull();
    expect(console.warn).toHaveBeenCalledTimes(1);
  });

  it('Inferred Configiration', async () => {
    const configParser = new ConfigurationParser('Inferred.conf.yaml');
    await configParser.parse();
    const object = configParser.getObjectDefinition();
    expect(object).not.toBeNull();
    expect(object?.get<IConfiguration>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IConfiguration>().name).toEqual('Inferred');
    expect(object?.get<IConfiguration>().description).toEqual('');
    expect(object?.get<IConfiguration>().version).toEqual('1.0.0');
  });

});
