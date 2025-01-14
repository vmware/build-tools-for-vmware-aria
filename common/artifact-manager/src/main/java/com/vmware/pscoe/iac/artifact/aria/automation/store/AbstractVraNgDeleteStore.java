/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.aria.automation.models.Identifiable;

/**
 * The purpose of this class is to provide a way to delete content from the
 * store that is universal to all VRA NG stores.
 */
public abstract class AbstractVraNgDeleteStore {
	/**
	 * This will delete all of the approvalPolicies that are present in the
	 * `content.yaml`.
	 *
	 * If the policy does not exist on the server, then nothing will happen.
	 */
	public void deleteContent() {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		List<Identifiable> serverResources = this.getAllServerContents();
		List<String> items = this.getItemListFromDescriptor();

		if (items == null) {
			logger.info("No items found in descriptor. Skipping deletion.");
			return;
		}

		for (Identifiable resource : serverResources) {
			if (items.contains(resource.getName())) {
				logger.info("Deleting resource '{}'", resource.getName());
				this.deleteResourceById(resource.getId());
			}
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return list of items
	 */
	protected abstract List<String> getItemListFromDescriptor();

	/**
	 * This will retrieve all the content for the store type and return it.
	 *
	 * What is being returned is entirely up to the implementation. You may want to
	 * filter and return by some filter.
	 *
	 * @return list of all content
	 */
	protected abstract <T extends Identifiable> List<T> getAllServerContents();

	/**
	 * This will delete a resource by its ID.
	 *
	 * @param resId the ID of the resource to delete
	 */
	protected abstract void deleteResourceById(String resId);
}
