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


import axios from 'axios';
import { IWorkflow, Workflow } from '../workflows';

// Mock axios and set the type
jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCKED_WORKFLOW = <IWorkflow>{
  id: "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee",
  name: "Test Workflow",
  description: '',
  version: "1.0.0",
  folder: "Codify"
}


describe('Workflow model', () => {

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('Create', async () => {
    const workflow = new Workflow({ ...MOCKED_WORKFLOW });
    expect(workflow.get<IWorkflow>()).toEqual(MOCKED_WORKFLOW);
  });

  it('Resolve by name', async () => {

    mockedAxios.get.mockResolvedValueOnce({
      data: {
        link: [
          {
            attributes: [
              { value: "https://fqdn:443/vco/api/workflows/afca7da4-eb84-41fb-bbe5-a27b8d96587f/", name: "itemHref", },
              { value: "true", name: "canExecute", },
              { value: "true", name: "editable", },
              { value: "Workflow", name: "@type", },
              { value: "true", name: "canEdit", },
              { name: "description", },
              { value: "https://fqdn:443/vco/api/catalog/System/WorkflowCategory/8a7480a17e2b4e32017e490083e80bd0/", name: "categoryHref", },
              { value: "Workflow", name: "type", },
              { value: "Codify:__SYSTEM_TAG__", name: "globalTags", },
              { value: "1.0.0", name: "version", },
              { value: "Codify", name: "categoryName", },
              { value: "1648743493920", name: "createdAt", },
              { value: "Sample Workflow", name: "@name", },
              { value: "Sample Workflow", name: "name", },
              { value: "afca7da4-eb84-41fb-bbe5-a27b8d96587f", name: "id", },
              { value: "Workflow", name: "@fullType", },
              { value: "afca7da4-eb84-41fb-bbe5-a27b8d96587f", name: "@id", },
              { value: "8a7480a17e2b4e32017e490083e80bd0", name: "categoryId", },
              { value: "false", name: "customIcon", },
              { value: "1648743493920", name: "updatedAt", },
            ],
            href: "https://fqdn:443/vco/api/workflows/afca7da4-eb84-41fb-bbe5-a27b8d96587f/",
            rel: "down",
          },
        ]
      }
    });
    const workflow = await Workflow.from(mockedAxios, 'Codify/Sample Workflow');
    expect(workflow).not.toBe(null);
  });

  it('Resolve by name - not found', async () => {
    mockedAxios.get.mockResolvedValueOnce({ data: { link: [] } });
    const workflow = await Workflow.from(mockedAxios, 'Codify/Test Workflow');
    expect(workflow).toBe(null);
  });

  it('Resolve by id - not found', async () => {
    mockedAxios.get.mockResolvedValueOnce({ status: 404 });
    const workflow = await Workflow.from(mockedAxios, 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee');
    expect(workflow).toBe(null);
  });

});
