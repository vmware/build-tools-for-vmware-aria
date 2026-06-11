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
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaScenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPolicy;
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

	public Map<String, Object> versionBlueprint(String blueprintId, Map<String, Object> payload)
			throws IOException {
		return versionBlueprintPrimitive(blueprintId, payload);
	}

	public VcfaBlueprint updateBlueprint(String id, VcfaBlueprint blueprint) throws IOException {
		return updateBlueprintPrimitive(id, objectMapper.convertValue(blueprint, Map.class));
	}

	public void deleteBlueprint(String id) throws IOException {
		deleteBlueprintPrimitive(id);
	}

	// --- Catalog Items
	public List<VcfaCatalogItem> getCatalogItems() throws IOException {
		return getCatalogItemsPrimitive();
	}

	public VcfaCatalogItem createCatalogItem(VcfaCatalogItem item) throws IOException {
		return createCatalogItemPrimitive(item);
	}

	public VcfaCatalogItem updateCatalogItem(VcfaCatalogItem item) throws IOException {
		return updateCatalogItemPrimitive(item.getId(), item);
	}

	public void deleteCatalogItem(String id) throws IOException {
		deleteCatalogItemPrimitive(id);
	}

	// --- Request Forms
	public VcfaCatalogItemForm getCatalogItemForm(String sourceType, String catalogItemId) throws IOException {
		return getCatalogItemFormPrimitive(sourceType, catalogItemId);
	}

	public void updateCatalogItemForm(String sourceType, String catalogItemId, VcfaCatalogItemForm form)
			throws IOException {
		updateCatalogItemFormPrimitive(sourceType, catalogItemId, form);
	}

	// --- Content sources
	public List<VcfaContentSource> getContentSources() throws IOException {
		return getContentSourcesPrimitive();
	}

	public VcfaContentSource createContentSource(VcfaContentSource payload) throws IOException {
		return createContentSourcePrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public VcfaContentSource updateContentSource(String id, VcfaContentSource payload) throws IOException {
		return updateContentSourcePrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deleteContentSource(String id) throws IOException {
		deleteContentSourcePrimitive(id);
	}

	// --- Custom resource types
	public List<VcfaCustomResourceType> getCustomResources() throws IOException {
		return getCustomResourcesPrimitive();
	}

	public VcfaCustomResourceType createCustomResourceType(VcfaCustomResourceType payload) throws IOException {
		return createCustomResourceTypePrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public void deleteCustomResourceType(String id) throws IOException {
		deleteCustomResourceTypePrimitive(id);
	}

	// --- Resource actions
	public List<VcfaResourceAction> getResourceActions() throws IOException {
		return getResourceActionsPrimitive();
	}

	public VcfaResourceAction createResourceAction(VcfaResourceAction payload) throws IOException {
		return createResourceActionPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public void deleteResourceAction(String id) throws IOException {
		deleteResourceActionPrimitive(id);
	}

	// --- Property groups
	public List<VcfaPropertyGroup> getPropertyGroups() throws IOException {
		return getPropertyGroupsPrimitive();
	}

	public VcfaPropertyGroup createPropertyGroup(VcfaPropertyGroup payload) throws IOException {
		return createPropertyGroupPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public VcfaPropertyGroup updatePropertyGroup(String id, VcfaPropertyGroup payload) throws IOException {
		return updatePropertyGroupPrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deletePropertyGroup(String id) throws IOException {
		deletePropertyGroupPrimitive(id);
	}

	// --- Policies
	public List<VcfaPolicy> getPolicies() {
		try {
			// A clean, single-line method call delegation to your primitive engine
			return getPoliciesPrimitive();
		} catch (IOException e) {
			throw new RuntimeException(
					"Fatal failure loading remote policy asset configuration streams from primitive client handler", e);
		}
	}

	public VcfaPolicy createPolicy(VcfaPolicy payload) throws IOException {
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
	public List<VcfaScenario> getScenarios() throws IOException {
		return getScenariosPrimitive();
	}

	public VcfaScenario createScenario(VcfaScenario payload) throws IOException {
		return createScenarioPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public VcfaScenario updateScenario(String id, VcfaScenario payload) throws IOException {
		return updateScenarioPrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deleteScenario(String id) throws IOException {
		deleteScenarioPrimitive(id);
	}

	// --- Subscriptions
	public List<VcfaSubscription> getSubscriptions() throws IOException {
		return getSubscriptionsPrimitive();
	}

	public VcfaSubscription createSubscription(VcfaSubscription payload) throws IOException {
		return createSubscriptionPrimitive(objectMapper.convertValue(payload, Map.class));
	}

	public VcfaSubscription updateSubscription(String id, VcfaSubscription payload) throws IOException {
		return updateSubscriptionPrimitive(id, objectMapper.convertValue(payload, Map.class));
	}

	public void deleteSubscription(String id) throws IOException {
		deleteSubscriptionPrimitive(id);
	}

	// --- Projects
	public String getProjectId(final String projectName) throws IOException {
		return getProjectIdPrimitive(projectName);
	}
}
