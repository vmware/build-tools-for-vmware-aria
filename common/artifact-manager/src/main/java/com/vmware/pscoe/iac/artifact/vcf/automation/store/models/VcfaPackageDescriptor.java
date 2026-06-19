package com.vmware.pscoe.iac.artifact.vcf.automation.store.models;

/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the 
 * terms and conditions of the subcomponent's license, as noted in the 
 * LICENSE file.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageDescriptor;

public final class VcfaPackageDescriptor extends PackageDescriptor {

    @JsonProperty("blueprint")
    private List<String> blueprint;

    @JsonProperty("subscription")
    private List<String> subscription;

    @JsonProperty("flavor-mapping")
    private List<String> flavorMapping;

    @JsonProperty("image-mapping")
    private List<String> imageMapping;

    @JsonProperty("storage-profile")
    private List<String> storageProfile;

    @JsonProperty("catalog-item")
    private List<String> catalogItem;

    @JsonProperty("custom-resource")
    private List<String> customResource;

    @JsonProperty("resource-action")
    private List<String> resourceAction;

    @JsonProperty("catalog-entitlement")
    private List<String> catalogEntitlement;

    @JsonProperty("content-source")
    private List<String> contentSource;

    @JsonProperty("property-group")
    private List<String> propertyGroup;

    @JsonProperty("policy")
    private Map<String, List<String>> policy;

    @JsonProperty("scenario")
    private List<String> scenario;

    /**
     * Constructor.
     */
    public VcfaPackageDescriptor() {
        super();
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================
    public List<String> getBlueprint() { return blueprint; }
    public void setBlueprint(List<String> blueprint) { this.blueprint = blueprint; }

    public List<String> getSubscription() { return subscription; }
    public void setSubscription(List<String> subscription) { this.subscription = subscription; }

    public List<String> getFlavorMapping() { return flavorMapping; }
    public void setFlavorMapping(List<String> flavorMapping) { this.flavorMapping = flavorMapping; }

    public List<String> getImageMapping() { return imageMapping; }
    public void setImageMapping(List<String> imageMapping) { this.imageMapping = imageMapping; }

    public List<String> getStorageProfile() { return storageProfile; }
    public void setStorageProfile(List<String> storageProfile) { this.storageProfile = storageProfile; }

    public List<String> getCatalogItem() { return catalogItem; }
    public void setCatalogItem(List<String> catalogItem) { this.catalogItem = catalogItem; }

    public List<String> getCustomResource() { return customResource; }
    public void setCustomResource(List<String> customResource) { this.customResource = customResource; }

    public List<String> getResourceAction() { return resourceAction; }
    public void setResourceAction(List<String> resourceAction) { this.resourceAction = resourceAction; }

    public List<String> getCatalogEntitlement() { return catalogEntitlement; }
    public void setCatalogEntitlement(List<String> catalogEntitlement) { this.catalogEntitlement = catalogEntitlement; }

    public List<String> getContentSource() { return contentSource; }
    public void setContentSource(List<String> contentSource) { this.contentSource = contentSource; }

    public List<String> getPropertyGroup() { return propertyGroup; }
    public void setPropertyGroup(List<String> propertyGroup) { this.propertyGroup = propertyGroup; }

    public Map<String, List<String>> getPolicy() { return policy; }
    public void setPolicy(Map<String, List<String>> policy) { this.policy = policy; }

    public List<String> getScenarios() { return scenario; }
    public void setScenarios(List<String> scenario) { this.scenario = scenario; }

    /**
     * Create a VcfaPackageDescriptor instance from file.
     */
    public static VcfaPackageDescriptor getInstance(File filesystemPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(filesystemPath, VcfaPackageDescriptor.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load VCFA package descriptor.", e);
        }
    }
}