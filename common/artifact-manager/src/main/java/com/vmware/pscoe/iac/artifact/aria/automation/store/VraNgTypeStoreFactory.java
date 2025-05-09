/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You
 * may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the
 * terms and conditions of the subcomponent's license, as noted in the
 * LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;

import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.APPROVAL_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.BLUEPRINT;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.CATALOG_ENTITLEMENT;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.CATALOG_ITEM;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.CONTENT_SHARING_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.CONTENT_SOURCE;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.CUSTOM_RESOURCE;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.DAY2_ACTIONS_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.DEPLOYMENT_LIMIT_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.LEASE_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.PROPERTY_GROUP;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.RESOURCE_ACTION;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.RESOURCE_QUOTA_POLICY;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.SUBSCRIPTION;
import static com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent.ContentType.SCENARIO;

/**
 * Factory to select and setup the store (handler) and determine the order of
 * execution.
 */
public class VraNgTypeStoreFactory {

	/**
	 * IMPORT_ORDER.
	 */
	private static final VraNgPackageContent.ContentType[] IMPORT_ORDER = {
			PROPERTY_GROUP,
			CONTENT_SOURCE,
			CUSTOM_RESOURCE,
			RESOURCE_ACTION,
			BLUEPRINT,
			SUBSCRIPTION,
			CATALOG_ENTITLEMENT,
			CATALOG_ITEM,
			CONTENT_SHARING_POLICY,
			LEASE_POLICY,
			RESOURCE_QUOTA_POLICY,
			DAY2_ACTIONS_POLICY,
			DEPLOYMENT_LIMIT_POLICY,
			APPROVAL_POLICY,
			SCENARIO
	};

	/**
	 * getImportOrder.
	 * {@link VraNgTypeStoreFactory#IMPORT_ORDER}
	 *
	 * @return VraNgPackageContent.ContentType[] IMPORT_ORDER
	 */
	public static VraNgPackageContent.ContentType[] getImportOrder() {
		return IMPORT_ORDER;
	}

	/**
	 * EXPORT_ORDER.
	 */
	private static final VraNgPackageContent.ContentType[] EXPORT_ORDER = {
			PROPERTY_GROUP,
			CONTENT_SOURCE,
			CUSTOM_RESOURCE,
			RESOURCE_ACTION,
			BLUEPRINT,
			SUBSCRIPTION,
			CATALOG_ENTITLEMENT,
			CATALOG_ITEM,
			CONTENT_SHARING_POLICY,
			LEASE_POLICY,
			RESOURCE_QUOTA_POLICY,
			DAY2_ACTIONS_POLICY,
			DEPLOYMENT_LIMIT_POLICY,
			APPROVAL_POLICY,
			SCENARIO
	};

	/**
	 * getExportOrder.
	 * {@link VraNgTypeStoreFactory#EXPORT_ORDER}
	 *
	 * @return VraNgPackageContent.ContentType[] EXPORT_ORDER
	 */
	public static VraNgPackageContent.ContentType[] getExportOrder() {
		return EXPORT_ORDER;
	}

	/**
	 * DELETE_ORDER.
	 */
	private static final VraNgPackageContent.ContentType[] DELETE_ORDER = {
			// Can be deleted in any order
			APPROVAL_POLICY,
			CONTENT_SHARING_POLICY,
			LEASE_POLICY,
			RESOURCE_QUOTA_POLICY,
			DAY2_ACTIONS_POLICY,
			DEPLOYMENT_LIMIT_POLICY,
			CATALOG_ENTITLEMENT,
			CATALOG_ITEM,
			CONTENT_SOURCE,
			SUBSCRIPTION,

			// Must be deleted before the following
			BLUEPRINT,

			// The following
			RESOURCE_ACTION,
			PROPERTY_GROUP,
			CUSTOM_RESOURCE,
			SCENARIO
	};

	/**
	 * getDeleteOrder.
	 * {@link VraNgTypeStoreFactory#DELETE_ORDER}
	 *
	 * @return VraNgPackageContent.ContentType[] DELETE_ORDER
	 */
	public static VraNgPackageContent.ContentType[] getDeleteOrder() {
		return DELETE_ORDER;
	}

	/**
	 * restClient.
	 */
	private final RestClientVraNg restClient;

	/**
	 * vraNgPackage.
	 */
	private final Package vraNgPackage;

	/**
	 * config.
	 */
	private final ConfigurationVraNg config;

	/**
	 * descriptor.
	 */
	private final VraNgPackageDescriptor descriptor;

	/**
	 * Constructor function.
	 *
	 * @param restClientNg rest client
	 * @param vraPackageNg vra package
	 * @param configNg     vra config
	 * @param descriptorNg vra descriptor
	 */
	protected VraNgTypeStoreFactory(
			final RestClientVraNg restClientNg,
			final Package vraPackageNg,
			final ConfigurationVraNg configNg,
			final VraNgPackageDescriptor descriptorNg) {
		this.restClient = restClientNg;
		this.vraNgPackage = vraPackageNg;
		this.config = configNg;
		this.descriptor = descriptorNg;
	}

	/**
	 * Factory method to create.
	 *
	 * @param restClient   rest client
	 * @param vraNgPackage vra package
	 * @param config       config vra
	 * @param descriptor   package descriptor
	 * @return VraNgTypeStoreFactory
	 */
	public static VraNgTypeStoreFactory withConfig(
			final RestClientVraNg restClient,
			final Package vraNgPackage,
			final ConfigurationVraNg config,
			final VraNgPackageDescriptor descriptor) {
		return new VraNgTypeStoreFactory(restClient, vraNgPackage, config, descriptor);
	}

	/**
	 * getStoreForType.
	 *
	 * @param type VraNgPackageContent.ContentType
	 * @return IVraNgStore
	 */
	public IVraNgStore getStoreForType(
			final VraNgPackageContent.ContentType type) {
		AbstractVraNgStore store = VraNgTypeStoreFactory.selectStore(restClient, type);
		store.init(restClient, vraNgPackage, config, descriptor);
		return store;
	}

	/**
	 * selectStore.
	 *
	 * @param restClient
	 * @param type
	 * @return AbstractVraNgStore
	 */
	private static AbstractVraNgStore selectStore(
			final RestClientVraNg restClient,
			final VraNgPackageContent.ContentType type) {
		switch (type) {
			case CATALOG_ITEM:
				boolean isVraAbove812 = restClient.getIsVraAbove812();
				return isVraAbove812 ? new VraNgCatalogItemStore812()
						: new VraNgCatalogItemStore();
			case CONTENT_SOURCE:
				return new VraNgContentSourceStore();
			case PROPERTY_GROUP:
				return new VraNgPropertyGroupStore();
			case BLUEPRINT:
				return new VraNgBlueprintStore();
			case SUBSCRIPTION:
				return new VraNgSubscriptionStore();
			case CATALOG_ENTITLEMENT:
				return new VraNgEntitlementStore();
			case CUSTOM_RESOURCE:
				return new VraNgCustomResourceStore();
			case RESOURCE_ACTION:
				return new VraNgResourceActionStore();
			case CONTENT_SHARING_POLICY:
				return new VraNgContentSharingPolicyStore();
			case RESOURCE_QUOTA_POLICY:
				return new VraNgResourceQuotaPolicyStore();
			case DAY2_ACTIONS_POLICY:
				return new VraNgDay2ActionsPolicyStore();
			case LEASE_POLICY:
				return new VraNgLeasePolicyStore();
			case DEPLOYMENT_LIMIT_POLICY:
				return new VraNgDeploymentLimitPolicyStore();
			case APPROVAL_POLICY:
				return new VraNgApprovalPolicyStore();
			case SCENARIO:
				return new VraNgScenarioStore();
			default:
				throw new RuntimeException("unknown type: " + type);
		}
	}
}
