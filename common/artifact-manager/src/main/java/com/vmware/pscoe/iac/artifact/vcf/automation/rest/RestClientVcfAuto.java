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
package com.vmware.pscoe.iac.artifact.vcf.automation.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogEntitlement;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.ContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.PropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.ResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Scenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Subscription;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.Policy;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;

/**
 * High-level client for VCF Automation API. Delegates to
 * RestClientVcfAutoPrimitive
 * and converts checked IOExceptions into runtime exceptions.
 */
public class RestClientVcfAuto extends RestClientVcfAutoPrimitive {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientVcfAuto.class);

	public RestClientVcfAuto(ConfigurationVcfAuto configuration) {
		super(configuration, null);
	}

	public RestClientVcfAuto(ConfigurationVcfAuto configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	public List<VcfaBlueprint> getBlueprints() throws IOException {
		return getBlueprintsPrimitive();
	}

	public VcfaBlueprint getBlueprintById(String id) throws IOException {
		return getBlueprintByIdPrimitive(id);
	}

	public VcfaBlueprint createBlueprint(VcfaBlueprint blueprint) throws IOException {
		return createBlueprintPrimitive(blueprint.toMap());
	}

	public VcfaBlueprint updateBlueprint(String id, VcfaBlueprint blueprint) throws IOException {
		return updateBlueprintPrimitive(id, objectMapper.convertValue(blueprint, Map.class));
	}

	public void deleteBlueprint(String id) throws IOException {
		deleteBlueprintPrimitive(id);
	}

	// --- Catalog items
	public List<CatalogItem> getCatalogItems() throws IOException {
		return getCatalogItemsPrimitive();
	}

	public CatalogItem createCatalogItem(CatalogItem payload) throws IOException {
		return createCatalogItemPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public CatalogItem updateCatalogItem(String id, CatalogItem payload) throws IOException {
		return updateCatalogItemPrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deleteCatalogItem(String id) throws IOException {
		deleteCatalogItemPrimitive(id);
	}

	// --- Content sources
	public List<ContentSource> getContentSources() throws IOException {
		return getContentSourcesPrimitive();
	}

	public ContentSource createContentSource(ContentSource payload) throws IOException {
		return createContentSourcePrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public ContentSource updateContentSource(String id, ContentSource payload) throws IOException {
		return updateContentSourcePrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deleteContentSource(String id) throws IOException {
		deleteContentSourcePrimitive(id);
	}

	// --- Custom resource types
	public List<CustomResourceType> getCustomResources() throws IOException {
		return getCustomResourcesPrimitive();
	}

	public CustomResourceType createCustomResourceType(CustomResourceType payload) throws IOException {
		return createCustomResourceTypePrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public void deleteCustomResourceType(String id) throws IOException {
		deleteCustomResourceTypePrimitive(id);
	}

	// --- Resource actions
	public List<ResourceAction> getResourceActions() throws IOException {
		return getResourceActionsPrimitive();
	}

	public ResourceAction createResourceAction(ResourceAction payload) throws IOException {
		return createResourceActionPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public void deleteResourceAction(String id) throws IOException {
		deleteResourceActionPrimitive(id);
	}

	// --- Property groups
	public List<PropertyGroup> getPropertyGroups() throws IOException {
		return getPropertyGroupsPrimitive();
	}

	public PropertyGroup createPropertyGroup(PropertyGroup payload) throws IOException {
		return createPropertyGroupPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public PropertyGroup updatePropertyGroup(String id, PropertyGroup payload) throws IOException {
		return updatePropertyGroupPrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deletePropertyGroup(String id) throws IOException {
		deletePropertyGroupPrimitive(id);
	}

	// --- Policies
	public List<Policy> getPolicies() throws IOException {
		return getPoliciesPrimitive();
	}

	public Policy createPolicy(Policy payload) throws IOException {
		return createPolicyPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public void deletePolicy(String id) throws IOException {
		deletePolicyPrimitive(id);
	}

	// --- Catalog entitlements
	public List<CatalogEntitlement> getCatalogEntitlements() throws IOException {
		return getCatalogEntitlementsPrimitive();
	}

	// --- Scenarios
	public List<Scenario> getScenarios() throws IOException {
		return getScenariosPrimitive();
	}

	// --- Subscriptions
	public List<Subscription> getSubscriptions() throws IOException {
		return getSubscriptionsPrimitive();
	}

	// --- Projects
	public String getProjectId(final String projectName) throws IOException {
		return getProjectIdPrimitive(projectName);
	}
}
