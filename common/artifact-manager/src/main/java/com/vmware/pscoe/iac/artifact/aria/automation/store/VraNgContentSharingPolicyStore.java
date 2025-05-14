/*
 * #%L
 * artifact-manager
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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgItem;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.models.VraNgPolicyTypes;
import java.util.List;

public class VraNgContentSharingPolicyStore extends AbstractVraNgPolicyStore<VraNgContentSharingPolicy> {
	/**
	 * Content sharing type: content source.
	 */
	private static final String CONTENT_SOURCE = "CATALOG_SOURCE_IDENTIFIER";
	/**
	 * Content sharing type: catalog item.
	 */
	private static final String CATALOG_ITEM = "CATALOG_ITEM_IDENTIFIER";

	/**
	 * Constructor for policy store of type VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE
	 */
	public VraNgContentSharingPolicyStore() {
		super(VraNgPolicyTypes.CONTENT_SHARING_POLICY_TYPE, "content-sharing", "Content Sharing", 
				VraNgContentSharingPolicy.class);
	}

	/**
	 * Get all the content sharing policies from the server.
	 *
	 * @return list of content sharing policies.
	 */
	protected List<VraNgContentSharingPolicy> getAllServerContents() {
		return this.restClient.getContentSharingPolicies();
	}

	/**
	 * Get List from descriptor.
	 * 
	 * @return null or list of content sharing policies.
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		if (this.vraNgPackageDescriptor.getPolicy() == null) {
			return null;
		} else {
			return this.vraNgPackageDescriptor.getPolicy().getContentSharing();
		}
	}

	/**
	 * Makes an API call to create a Policy
	 * 
	 * @param policy
	 */
	@Override
	protected void createPolicy(final VraNgContentSharingPolicy policy) {
		this.restClient.createContentSharingPolicy(policy);
	}

	/**
	 * Resolve the entitled users, organization and scope in the DTO object that
	 * will be serialized / deserialized (depending on the operation type (export or
	 * import)).
	 * 
	 * @param csPolicy         policy that need to be enriched with resolved data.
	 * @param isByNameResolved a flag whether to resolve the data by name. . In case
	 *                         import of policies the flag should be true otherwise
	 *                         it should be false.
	 *
	 */
	@Override
	protected void resolvePolicyItem(VraNgContentSharingPolicy csPolicy, boolean isByNameResolved) {
		if (csPolicy.getDefinition() == null || csPolicy.getDefinition().getEntitledUsers() == null) {
			return;
		}

		List<VraNgContentSourceBase> contentSources = this.restClient
				.getContentSourcesForProject(csPolicy.getProjectId());

		List<VraNgCatalogItem> catalogItems = this.restClient.getCatalogItemsForProject(csPolicy.getProjectId());
		csPolicy.getDefinition().getEntitledUsers().forEach(user -> {
			// resolve the name of the element based on its type
			if (user.getItems() != null) {
				user.getItems().forEach(item -> {
					this.resolveSingleItem(item, isByNameResolved, contentSources, catalogItems);
				});
			}
		});
	}

	/**
	 * Resolve the the single vra item name / id.
	 * 
	 * @param item             item where data should be resolved.
	 * @param isByNameResolved a flag whether to resolve the data by name. . In case
	 *                         import of policies the flag should be false otherwise
	 *                         it should be true.
	 * 
	 * @param contentSources   list of content sources per project
	 * @param catalogItems     list of catalog items per project
	 */
	private void resolveSingleItem(VraNgItem item, boolean isByNameResolved,
			List<VraNgContentSourceBase> contentSources, List<VraNgCatalogItem> catalogItems) {
		switch (item.getType()) {
			case CATALOG_ITEM: {
				VraNgCatalogItem foundCatalogItem;
				if (isByNameResolved) {
					foundCatalogItem = catalogItems.stream()
							.filter(catalogItem -> catalogItem.getName().equalsIgnoreCase(item.getName())).findFirst()
							.orElseThrow(() -> errorFrom(null,
									"Catalog Item with name: '%s' could not be found. Cannot import/export Policy.",
									item.getName()));
					item.setId(foundCatalogItem.getId());
					item.setName(null);
				} else {
					foundCatalogItem = catalogItems.stream()
							.filter(catalogItem -> catalogItem.getId().equalsIgnoreCase(item.getId())).findFirst()
							.orElseThrow(() -> errorFrom(null,
									"Catalog Item with name: '%s' could not be found. Cannot import/export Policy.",
									item.getName()));
					item.setName(foundCatalogItem.getName());
					item.setId(null);
				}
				break;
			}
			case CONTENT_SOURCE: {
				VraNgContentSourceBase foundContentSource;
				if (isByNameResolved) {
					foundContentSource = contentSources.stream()
							.filter(contentSource -> contentSource.getName().equalsIgnoreCase(item.getName()))
							.findFirst()
							.orElseThrow(() -> errorFrom(null,
									"Content source with name: '%s' could not be found. Cannot import/export Policy.",
									item.getName()));
					item.setId(foundContentSource.getId());
					item.setName(null);
				} else {
					foundContentSource = contentSources.stream()
							.filter(contentSource -> contentSource.getId().equalsIgnoreCase(item.getId()))
							.findFirst()
							.orElseThrow(() -> errorFrom(null,
									"Content source with name: '%s' could not be found. Cannot import/export Policy.",
									item.getName()));
					item.setName(foundContentSource.getName());
					item.setId(null);
				}
				break;
			}
			default: {
				logger.warn("Type {}, for definition: {} is unsupported", item.getType(), item.getId());
			}
		}
	}
}
