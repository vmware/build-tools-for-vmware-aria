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


import path from 'path';
import chalk from 'chalk';
import fs from 'fs-extra';
import yaml from 'js-yaml';
import { AxiosInstance } from 'axios';
import { IBaseVraObject, VraObject } from './base';
import { isResourceActionId } from '../utils';
import { omit } from 'lodash';

const IAAS_API_VERSION = '2021-07-15';

export interface IResourceAction extends IBaseVraObject {
  objectType: 'ResourceAction';
  id: string;
  description: string;
  project: string | null;
  displayName: string;
  providerName: string;
  resourceType: string;
  status: string;
  runnableItem: {
    id: string;
    name: string;
    type: string;
    inputParameters: {
      type: string;
      name: string;
    }[];
    endpointLink: string;
    inputBindings: {
      inputKey: string;
      value: string;
      type: {
        dataType: string;
      };
    }[];
  };
  formDefinition: {
    id: string;
    name: string;
    form: string;
    sourceType: string;
    sourceId: string;
    type: string;
    tenant: string;
    status: string;
    formFormat: string;
  };
}

type ResourceActionResponse = {
  id: string;
  name: string;
  description: string;
  projectId: string;
  displayName: string;
  providerName: string;
  resourceType: string;
  status: string;
  orgId: string;
  runnableItem: {
    id: string;
    name: string;
    type: string;
    inputParameters: {
      type: string;
      name: string;
    }[];
    endpointLink: string;
    inputBindings: {
      inputKey: string;
      value: string;
      type: {
        dataType: string;
      };
    }[];
  };
  formDefinition: {
    id: string;
    name: string;
    form: string;
    sourceType: string;
    sourceId: string;
    type: string;
    tenant: string;
    status: string;
    formFormat: string;
  };
};

export class ResourceAction extends VraObject {
  public readonly urn: string;

  constructor(protected readonly objectDefinition: IResourceAction) {
    super(objectDefinition);
    this.urn = `${this.objectDefinition.name}`;
    console.log(`Resolved resource action: ${this.urn}`);
  }

  get<IResourceAction>() {
    return <IResourceAction>(<unknown>this.objectDefinition);
  }

  /**
   * Upsert resource action
   * @param client {AxiosInstance}
   */
  async upload(client: AxiosInstance): Promise<void> {
    const remoteResourceAction = await ResourceAction.getRemoteResourceAction(client, this.objectDefinition.id);

    // Resolve references. The project reference may or may not be part of the object definition.
    // The reason for this is that the resource action can be scoped for a specific project or it
    // can be shared across all projects in the organization. If it is shared, the projectId prop
    // does not exist in the payload when fetching the action, hence no project reference.
    const projectId = this.objectDefinition.project
      ? await ResourceAction.resolveReference(
          client,
          this.objectDefinition.project,
          'id',
          '/iaas/api/projects',
          IAAS_API_VERSION
        )
      : null;

    if (remoteResourceAction) {
      console.log(`Updating resource action ${this.objectDefinition.name}`);

      // When updating a resource action, a POST request with the full resource action
      // definition is sent. This updates the existing action, including its custom form.
      await client.post('/form-service/api/custom/resource-actions', {
        ...omit(this.objectDefinition, 'objectType', 'project'),
        projectId,
      });
    } else {
      console.log(`Creating resource action ${this.objectDefinition.name}`);

      // When creating a resource action, the custom form cannot be sent in the create request,
      // that's why it is omitted from the payload. Once the resource action is created,
      // a second request with the custom form is sent to effectively bring the custom form to a
      // state that is consistent with the definition in the file
      await client.post('/form-service/api/custom/resource-actions', {
        ...omit(this.objectDefinition, 'objectType', 'project', 'formDefinition'),
        projectId,
      });
      await client.post('/form-service/api/custom/resource-actions', {
        ...omit(this.objectDefinition, 'objectType', 'project'),
        projectId,
      });
    }

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Download resource action.
   * @param client {AxiosClient}
   * @param target {string} target directory or file
   */
  async download(client: AxiosInstance, target: string): Promise<void> {
    console.log(`Downloading resource action ${this.objectDefinition.name}`);

    // make directory if it doesn't exist and write the configuration
    const resourceActionTargetPath = path.resolve(path.normalize(target));
    await fs.promises.mkdir(resourceActionTargetPath, { recursive: true });

    const resourceActionTarget = path.join(resourceActionTargetPath, `${this.objectDefinition.name}.day2.yaml`);
    console.log(`Writing resource action to ${resourceActionTarget}`);
    await fs.promises.writeFile(resourceActionTarget, yaml.dump(this.objectDefinition));

    console.log(chalk.green(` -> Done`));
  }

  /**
   * Add metadata. This method is intended to be called as part of the download sequence.
   * It appends any metadata to the download content.
   */
  getMeta(): string {
    return JSON.stringify(this.objectDefinition, null, 2);
  }

  /**
   * Resolve a resource action object from remote identifier.
   * @param client {AxiosInstance}
   * @param resourceActionIdentifier {string} resource action name or id
   * @returns the resolved resource action
   */
  static async from(client: AxiosInstance, resourceActionIdentifier: string): Promise<ResourceAction | null> {
    const resourceAction = await ResourceAction.getRemoteResourceAction(client, resourceActionIdentifier);
    if (resourceAction) {
      const resourceActionDefinition =
        resourceAction.runnableItem.type === 'abx.action'
          ? omit(
              resourceAction,
              'orgId',
              'projectId',
              'formDefinition.tenant',
              'runnableItem.endpointLink',
              'runnableItem.projectId',
              'runnableItem.inputParameters'
            )
          : omit(resourceAction, 'orgId', 'projectId', 'formDefinition.tenant');

      return new ResourceAction({
        objectType: 'ResourceAction',
        project: resourceAction.projectId
          ? await ResourceAction.getReference(
              client,
              `/iaas/api/projects/${resourceAction.projectId}?apiVersion=${IAAS_API_VERSION}`,
              'name'
            )
          : null,
        ...resourceActionDefinition,
      });
    } else {
      console.warn(`Could not resolve resource action: ${resourceActionIdentifier}`);
      return null;
    }
  }

  /**
   * Retrieve remote resource action.
   * @param client {AxiosInstance}
   * @param resourceActionIdentifier {string}
   * @returns remote resource action
   */
  static async getRemoteResourceAction(
    client: AxiosInstance,
    resourceActionIdentifier: string
  ): Promise<ResourceActionResponse | null> {
    if (isResourceActionId(resourceActionIdentifier)) {
      const remoteResourceAction = await client.get(
        `/form-service/api/custom/resource-actions/${resourceActionIdentifier}`,
        { validateStatus: null }
      );
      return remoteResourceAction.status === 200 ? remoteResourceAction.data : null;
    } else {
      const params = new URLSearchParams({
        $filter: `(name eq '${resourceActionIdentifier}')`,
      });
      const resourceActionSearch = await client.get(`/form-service/api/custom/resource-actions?${params.toString()}`);

      if (resourceActionSearch.data.numberOfElements >= 1) {
        const remoteResourceAction = await client.get(
          `/form-service/api/custom/resource-actions/${resourceActionSearch.data.content[0].id}`
        );
        return remoteResourceAction.data;
      }
      return null;
    }
  }
}
