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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.VcfAutoConfiguration;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType;

import static com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType.*;

/**
 * Factory for VCFA stores and execution order.
 */
public class VcfaTypeStoreFactory {
	private static final VcfaPackageMemberType[] IMPORT_ORDER = {
			// the blueprints have to be imported last, so that we have any resource actions
			// or custom resources present
			POLICY,
			PROPERTY_GROUP,
			SCENARIO,
			// The fllowing need an orchestrator integration in order to work
			CUSTOM_RESOURCE,
			RESOURCE_ACTION,
			SUBSCRIPTION,
			WORKFLOW,
			BLUEPRINT
	};

	private static final VcfaPackageMemberType[] EXPORT_ORDER = {
			// can be exported in any order, so we went with alphabetical
			BLUEPRINT,
			POLICY,
			PROPERTY_GROUP,
			SCENARIO,
			// The fllowing need an orchestrator integration in order to work
			CUSTOM_RESOURCE,
			RESOURCE_ACTION,
			SUBSCRIPTION,
			WORKFLOW
	};

	private static final VcfaPackageMemberType[] DELETE_ORDER = {
			// blueprints have to be cleaned first, so that the custom resources & resource
			// actions are not bound to anything and are deletable
			BLUEPRINT,
			POLICY,
			PROPERTY_GROUP,
			SCENARIO,
			// The fllowing need an orchestrator integration in order to work
			CUSTOM_RESOURCE,
			RESOURCE_ACTION,
			SUBSCRIPTION,
			WORKFLOW
	};

	public static VcfaPackageMemberType[] getImportOrder() {
		return IMPORT_ORDER;
	}

	public static VcfaPackageMemberType[] getExportOrder() {
		return EXPORT_ORDER;
	}

	public static VcfaPackageMemberType[] getDeleteOrder() {
		return DELETE_ORDER;
	}

	private final RestClientVcfAuto restClient;
	private final Package vcfaPackage;
	private final VcfAutoConfiguration config;
	private final VcfaPackageDescriptor descriptor;

	protected VcfaTypeStoreFactory(final RestClientVcfAuto restClient, final Package vcfaPackage,
			final VcfAutoConfiguration config, final VcfaPackageDescriptor descriptor) {
		this.restClient = restClient;
		this.vcfaPackage = vcfaPackage;
		this.config = config;
		this.descriptor = descriptor;
	}

	public static VcfaTypeStoreFactory withConfig(final RestClientVcfAuto restClient, final Package vcfaPackage,
			final VcfAutoConfiguration config, final VcfaPackageDescriptor descriptor) {
		return new VcfaTypeStoreFactory(restClient, vcfaPackage, config, descriptor);
	}

	public IVcfaStore getStoreForType(
			final com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType type) {
		AbstractVcfaStore store = selectStore(type);
		store.init(restClient, vcfaPackage, config, descriptor);
		return store;
	}

	private static AbstractVcfaStore selectStore(
			final com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType type) {
		switch (type) {
			case BLUEPRINT:
				return new VcfaBlueprintStore();
			case CUSTOM_RESOURCE:
				return new VcfaCustomResourceStore();
			case RESOURCE_ACTION:
				return new VcfaResourceActionStore();
			case SUBSCRIPTION:
				return new VcfaSubscriptionStore();
			case POLICY:
				return new VcfaPolicyStore();
			case PROPERTY_GROUP:
				return new VcfaPropertyGroupStore();
			case SCENARIO:
				return new VcfaScenarioStore();
			case WORKFLOW:
				return new VcfaVroWorkflowStore();
			default:
				throw new RuntimeException("unknown type: " + type);
		}
	}
}
