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
import { PolyglotAction, Action, IBaseAction, IPolyglotAction } from '../../model/actions';
import { ActionParser } from '../action';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const MOCKS: { [filePath: string]: string } = {

  'polyglotAction.js': `
    /**
     * Example polyglot action
     * @vro_type         polyglot
     * @vro_id           aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
     * @vro_name         examplePolyglotAction
     * @vro_module       com.vmware.acoe.demo
     * @vro_entrypoint   handler.handler
     * @vro_timeout      3600
     * @vro_memory       512000000
     * @vro_version      1.0.1
     * @vro_input        {string} username Service user's username
     * @vro_input        {SecureString} password Service user's password
     * @vro_input        {boolean} customFlag
     * @vro_output       {Array/string} operation results
     */
  `,

  'polyglotAction.py': `
    '''
    /**
     * Example polyglot action
     * @vro_type         polyglot
     * @vro_id           aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
     * @vro_name         examplePolyglotAction
     * @vro_module       com.vmware.acoe.demo
     * @vro_entrypoint   handler.handler
     * @vro_timeout      3600
     * @vro_memory       512000000
     * @vro_version      1.0.0
     * @vro_input        {string} username Service user's username
     * @vro_input        {SecureString} password Service user's password
     * @vro_input        {boolean} customFlag
     * @vro_output       {Array/string} operation results
     */
    '''
  `,

  'polyglotAction.ps1': `
    <#
    /**
     * Example polyglot action
     * @vro_type         polyglot
     * @vro_id           aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
     * @vro_name         examplePolyglotAction
     * @vro_module       com.vmware.acoe.demo
     * @vro_entrypoint   handler.handler
     * @vro_timeout      3600
     * @vro_memory       512000000
     * @vro_version      1.0.0
     * @vro_input        {string} username Service user's username
     * @vro_input        {SecureString} password Service user's password
     * @vro_input        {boolean} customFlag
     * @vro_output       {Array/string} operation results
     */
    #>
  `,

  'action.js': `
    /**
     * Example action
     * @vro_type         action
     * @vro_name         exampleAction
     * @vro_module       com.vmware.acoe.demo
     * @vro_input        {string} user User name
     * @vro_output       {Array/string} operation results
     */
  `,

  'inferredPolyglotAction.js': `
    /**
     * @vro_type         polyglot
     */
  `,

  'inferredAction.js': `
    /**
     * @vro_type         action
     */
  `,

  'unknown.js': `
  /**
   * @vro_type         unknownObject
   */
  `,

  'unknown2.js': `
    /**
     * @vro_sometag      unknownValue
     */
  `,

  'noAction.js': `
    /**
     * Greet the user
     * @param {string} person the person to greet.
     */
  `,
}

describe('Action Parser', () => {

  let readFileSpy: jest.SpyInstance;

  beforeEach(() => {
    readFileSpy = jest
      .spyOn(fs.promises, 'readFile')
      .mockImplementationOnce((_path, _options) => Promise.resolve(MOCKS[(<string>_path).split('/').pop() || <string>_path]));
  });

  it('Parse JS file', async () => {
    const actionParser = new ActionParser('polyglotAction.js');
    await actionParser.parse();
    expect(readFileSpy).toHaveBeenCalledWith('polyglotAction.js', "utf-8");
  });

  it(`Parse NodeJS Polyglot Action`, async () => {
    const actionParser = new ActionParser('polyglotAction.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(PolyglotAction);
    expect(object?.get<IPolyglotAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IPolyglotAction>().id).toEqual('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(object?.get<IPolyglotAction>().name).toEqual('examplePolyglotAction');
    expect(object?.get<IPolyglotAction>().description).toEqual('Example polyglot action');
    expect(object?.get<IPolyglotAction>().module).toEqual('com.vmware.acoe.demo');
    expect(object?.get<IPolyglotAction>().runtime).toEqual('node:14');
    expect(object?.get<IPolyglotAction>().entrypoint).toEqual('handler.handler');
    expect(object?.get<IPolyglotAction>().version).toEqual('1.0.1');
    expect(object?.get<IPolyglotAction>().timeout).toEqual(60 * 60);
    expect(object?.get<IPolyglotAction>().memory).toEqual(512 * 1000 * 1000);
  });

  it(`Parse Python Polyglot Action`, async () => {
    const actionParser = new ActionParser('polyglotAction.py');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(PolyglotAction);
    expect(object?.get<IPolyglotAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IPolyglotAction>().id).toEqual('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(object?.get<IPolyglotAction>().name).toEqual('examplePolyglotAction');
    expect(object?.get<IPolyglotAction>().description).toEqual('Example polyglot action');
    expect(object?.get<IPolyglotAction>().module).toEqual('com.vmware.acoe.demo');
    expect(object?.get<IPolyglotAction>().runtime).toEqual('python:3.7');
    expect(object?.get<IPolyglotAction>().entrypoint).toEqual('handler.handler');
    expect(object?.get<IPolyglotAction>().version).toEqual('1.0.0');
    expect(object?.get<IPolyglotAction>().timeout).toEqual(60 * 60);
    expect(object?.get<IPolyglotAction>().memory).toEqual(512 * 1000 * 1000);
  });

  it(`Parse PowerShell Polyglot Action`, async () => {
    const actionParser = new ActionParser('polyglotAction.ps1');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(PolyglotAction);
    expect(object?.get<IPolyglotAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IPolyglotAction>().id).toEqual('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(object?.get<IPolyglotAction>().name).toEqual('examplePolyglotAction');
    expect(object?.get<IPolyglotAction>().description).toEqual('Example polyglot action');
    expect(object?.get<IPolyglotAction>().module).toEqual('com.vmware.acoe.demo');
    expect(object?.get<IPolyglotAction>().runtime).toEqual('powercli:12-powershell-7.1');
    expect(object?.get<IPolyglotAction>().entrypoint).toEqual('handler.handler');
    expect(object?.get<IPolyglotAction>().version).toEqual('1.0.0');
    expect(object?.get<IPolyglotAction>().timeout).toEqual(60 * 60);
    expect(object?.get<IPolyglotAction>().memory).toEqual(512 * 1000 * 1000);
  });

  it(`Parse Regular Action`, async () => {
    const actionParser = new ActionParser('action.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Action);
    expect(object?.get<IBaseAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IBaseAction>().name).toEqual('exampleAction');
    expect(object?.get<IBaseAction>().module).toEqual('com.vmware.acoe.demo');
  });

  it(`Infer NodeJS Polyglot Action`, async () => {
    const actionParser = new ActionParser('com vmware  codify/inferredPolyglotAction.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(PolyglotAction);
    expect(object?.get<IPolyglotAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IPolyglotAction>().name).toEqual('inferredPolyglotAction');
    expect(object?.get<IPolyglotAction>().description).toEqual('');
    expect(object?.get<IPolyglotAction>().module).toEqual('com.vmware.codify');
    expect(object?.get<IPolyglotAction>().runtime).toEqual('node:14');
    expect(object?.get<IPolyglotAction>().entrypoint).toEqual('handler.handler');
    expect(object?.get<IPolyglotAction>().version).toEqual('1.0.0');
    expect(object?.get<IPolyglotAction>().timeout).toEqual(10 * 60);
    expect(object?.get<IPolyglotAction>().memory).toEqual(256 * 1000 * 1000);
  });

  it(`Infer Regular Action`, async () => {
    const actionParser = new ActionParser('com.vmware.codify/inferredAction.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeInstanceOf(Action);
    expect(object?.get<IBaseAction>().id).toEqual(expect.stringMatching(UUID_REGEX));
    expect(object?.get<IBaseAction>().name).toEqual('inferredAction');
    expect(object?.get<IBaseAction>().module).toEqual('com.vmware.codify');
  });

  it(`Parse No Action`, async () => {
    const actionParser = new ActionParser('noAction.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeNull();
  });

  it(`Parse Unknown Object`, async () => {
    const actionParser = new ActionParser('unknown.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeNull();
  });

  it(`Parse vRO Objects with unknown properties`, async () => {
    const actionParser = new ActionParser('unknown2.js');
    await actionParser.parse();
    const object = actionParser.getObjectDefinition();
    expect(object).toBeNull();
  });

});

