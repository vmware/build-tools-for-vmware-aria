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


import { isResourceActionId } from '..';
import * as utils from '../utils';
import child_process from 'child_process';

jest.mock("child_process");

describe('Utils', () => {

  it(`Check UUID`, () => {
    expect(utils.isUUID('')).toBe(false);
    expect(utils.isUUID('  ')).toBe(false);
    expect(utils.isUUID('abc')).toBe(false);
    expect(utils.isUUID('fb9c3563-8623-4cbb-ad7c-56e3860068f3')).toBe(true);
  });

  it(`Check Subscription ID`, () => {
    expect(utils.isSubscriptionId('')).toBe(false);
    expect(utils.isSubscriptionId('  ')).toBe(false);
    expect(utils.isSubscriptionId('abc')).toBe(false);
    expect(utils.isSubscriptionId('fb9c3563-8623-4cbb-ad7c-56e3860068f3')).toBe(false);
    expect(utils.isSubscriptionId('sub_12345678901234')).toBe(false);
    expect(utils.isSubscriptionId('sub_123456789012')).toBe(false);
    expect(utils.isSubscriptionId('sub_1234567890123')).toBe(true);
    expect(utils.isSubscriptionId('aub_1234567890123')).toBe(false);
    expect(utils.isSubscriptionId('sub_123456789012a')).toBe(false);
  });

  it(`Generate empty map from attributes`, () => {
    const map = utils.attributesToMap([]);
    expect(utils.attributesToMap([])).toBeInstanceOf(Map);
    expect(map.size).toEqual(0);
  })

  it(`Generate map from attributes`, () => {
    const map = utils.attributesToMap([{ name: 'foo', value: 'bar' }]);
    expect(map.get('foo')).toEqual('bar');
  });

  it('Generate module name', () => {
    expect(utils.generateModuleName('foo')).toEqual('foo');
    expect(utils.generateModuleName('foo.bar')).toEqual('foo.bar');
    expect(utils.generateModuleName('foo bar')).toEqual('foo.bar');
    expect(utils.generateModuleName('  Foo Bar  ')).toEqual('foo.bar');
    expect(utils.generateModuleName('  Foo_Bar  ')).toEqual('foobar');
    expect(utils.generateModuleName('  Foo_Bar __  Baz  ')).toEqual('foobar.baz');
  });

  it('Run NPM install', () => {
    const mExecSync = <jest.MockedFunction<typeof child_process.execSync>>child_process.execSync;
    mExecSync.mockReturnValue('');

    utils.npmInstall('dummy-path');
    expect(mExecSync).toHaveBeenCalledWith('npm install', { cwd: 'dummy-path', stdio: 'inherit' });
  });

  it('Handle NPM install errors', () => {
    const mExecSync = <jest.MockedFunction<typeof child_process.execSync>>child_process.execSync;
    mExecSync.mockImplementationOnce(() => { throw new Error(); });

    expect(() => {
      utils.npmInstall('dummy-path');
    }).not.toThrow();
    expect(mExecSync).toHaveBeenCalledWith('npm install', { cwd: 'dummy-path', stdio: 'inherit' });
  });

  it('Check if a string represents a resource action ID', () => {
    expect(isResourceActionId('vra.custom.resourceaction')).toBe(true);
    expect(isResourceActionId('vra.default.resourceaction')).toBe(false);
  });

});
