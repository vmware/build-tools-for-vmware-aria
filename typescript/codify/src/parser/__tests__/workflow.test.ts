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
import { IWorkflow, Workflow } from '../../model';
import { WorkflowParser } from '../workflow';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const MOCKS: { [filePath: string]: string } = {

  'test.workflow': '',
  'test.workflow.meta': JSON.stringify(
    {
      "id": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
      "name": "Sample Workflow",
      "version": "1.0.0",
      "folder": "ACoE/Codify",
      "content": {
        "display-name": "Sample Workflow",
        "attrib": [],
        "presentation": {},
        "root-name": "item0",
        "object-name": "workflow:name=generic",
        "id": "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
        "version": "1.0.0",
        "api-version": "6.0.0",
        "restartMode": 1,
        "resumeFromFailedMode": 0,
        "editor-version": "2.0"
      }
    }
  ),


  'test2.workflow': '',
  'test2.workflow.meta': JSON.stringify(
    {
      "content": {
        "display-name": "Sample Workflow",
        "attrib": [],
        "presentation": {},
        "root-name": "item0",
        "object-name": "workflow:name=generic",
        "version": "1.0.0",
        "api-version": "6.0.0",
        "restartMode": 1,
        "resumeFromFailedMode": 0,
        "editor-version": "2.0"
      }
    }
  )

};

describe('Workflow Parser', () => {

  let readFileSpy: jest.SpyInstance;
  let readJSONSpy: jest.SpyInstance;
  let existsSyncSpy: jest.SpyInstance;

  beforeEach(() => {

    readJSONSpy = jest.spyOn(fs, 'readJSON')
      .mockImplementationOnce(_path => Promise.resolve(
        JSON.parse(MOCKS[(<string>_path).split('/').pop() || <string>_path])
      ));

    existsSyncSpy = jest.spyOn(fs, 'existsSync')
      .mockImplementation(_path => true);

    readFileSpy = jest.spyOn(fs, 'readFile')
      .mockImplementationOnce(_path => Promise.resolve(
        Buffer.from(MOCKS[(<string>_path).split('/').pop() || <string>_path])
      ));

  });

  it('Parse file', async () => {
    const workflowParser = new WorkflowParser('test.workflow.meta');
    await workflowParser.parse();
    expect(readJSONSpy).toHaveBeenCalledWith('test.workflow.meta', "utf-8");
    expect(existsSyncSpy).toHaveBeenCalledWith('test.workflow');
    expect(readFileSpy).toHaveBeenCalledWith('test.workflow');
  });

  it(`Parse Workflow`, async () => {
    const workflowParser = new WorkflowParser('test.workflow.meta');
    await workflowParser.parse();
    const object = workflowParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Workflow);
    expect(object?.get<IWorkflow>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IWorkflow>().id).toEqual('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(object?.get<IWorkflow>().name).toEqual('Sample Workflow');
    expect(object?.get<IWorkflow>().description).toEqual('');
    expect(object?.get<IWorkflow>().version).toEqual('1.0.0');
    expect(object?.get<IWorkflow>().folder).toEqual('ACoE/Codify');
  });

  it('Handle parsing errors', async () => {

    readJSONSpy.mockReset();
    readJSONSpy = jest.spyOn(fs, 'readJSON')
      .mockImplementationOnce(_path => { throw new Error('JSON Parse Error') });

    const workflowParser = new WorkflowParser('test.workflow.meta');
    await workflowParser.parse();
    expect(readJSONSpy).toHaveBeenCalledWith('test.workflow.meta', "utf-8");
    expect(workflowParser.getObjectDefinition()).toBeNull();
  });

  it(`Infer Workflow Data`, async () => {
    const workflowParser = new WorkflowParser('test2.workflow.meta');
    await workflowParser.parse();
    const object = workflowParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Workflow);
    expect(object?.get<IWorkflow>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IWorkflow>().name).toEqual('test2');
    expect(object?.get<IWorkflow>().description).toEqual('');
    expect(object?.get<IWorkflow>().version).toEqual('1.0.0');
  });

});
