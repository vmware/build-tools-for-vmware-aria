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
import { CloudTemplate, ICloudTemplate } from '../../model';
import { CloudTemplateParser } from '../cloudtemplate';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const MOCKS: { [filePath: string]: string } = {

  'template.vct.yaml': `
  objectType: VMwareCloudTemplate
  name: Test Template
  description: Test Codify cloud template.
  requestScopeOrg: true
  project: ref:name:Development
  content:
    formatVersion: 1
    inputs: {}
    resources: {}
  `,

  'no-template.vct.yaml': `
foo: bar
  `,

  'Inferred.vct.yaml': `
objectType: VMwareCloudTemplate
  `,

};

describe('Cloud Template Parser', () => {

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
    const cloudTemplateParser = new CloudTemplateParser('template.vct.yaml');
    await cloudTemplateParser.parse();
    expect(readFileSpy).toHaveBeenCalledWith('template.vct.yaml', "utf-8");
  });

  it(`Parse Cloud Template`, async () => {
    const cloudTemplateParser = new CloudTemplateParser('template.vct.yaml');
    await cloudTemplateParser.parse();
    const object = cloudTemplateParser.getObjectDefinition();
    expect(object).toBeInstanceOf(CloudTemplate);
    expect(object?.get<ICloudTemplate>().name).toEqual('Test Template');
    expect(object?.get<ICloudTemplate>().description).toEqual('Test Codify cloud template.');
    expect(object?.get<ICloudTemplate>().requestScopeOrg).toBe(true);
    expect(object?.get<ICloudTemplate>().project).toBe('ref:name:Development');
    expect(object?.get<ICloudTemplate>().content).toEqual(expect.objectContaining({
      formatVersion: expect.any(Number),
      inputs: expect.any(Object),
      resources: expect.any(Object)
    }));
  });

  it('Parse No Cloud Template', async () => {
    const cloudTemplateParser = new CloudTemplateParser('no-template.vct.yaml');
    await cloudTemplateParser.parse();
    const object = cloudTemplateParser.getObjectDefinition();
    expect(object).toBeNull();
  });

  it('Failed Parse', async () => {
    const cloudTemplateParser = new CloudTemplateParser('no-yaml.vct.yaml');
    await cloudTemplateParser.parse();
    const object = cloudTemplateParser.getObjectDefinition();
    expect(object).toBeNull();
    expect(console.warn).toHaveBeenCalledTimes(1);
  });

  it('Inferred Cloud Template', async () => {
    const cloudTemplateParser = new CloudTemplateParser('Inferred.vct.yaml');
    await cloudTemplateParser.parse();
    const object = cloudTemplateParser.getObjectDefinition();
    expect(object).not.toBeNull();
    expect(object?.get<ICloudTemplate>().name).toEqual('Inferred');
    expect(object?.get<ICloudTemplate>().description).toEqual('');
  });

});
