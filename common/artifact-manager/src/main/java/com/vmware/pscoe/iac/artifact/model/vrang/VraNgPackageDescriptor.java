package com.vmware.pscoe.iac.artifact.model.vrang;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VraNgPackageDescriptor extends PackageDescriptor {
	/**
	 * List of catalogItems.
	 */
    private List<String> catalogItem;
	/**
	 * List of blueprints.
	 */
    private List<String> blueprint;
	/**
	 * List of subscriptions.
	 */
    private List<String> subscription;
	/**
	 * List of flavorMappings.
	 */
    private List<String> flavorMapping;
	/**
	 * List of imageMappings.
	 */
    private List<String> imageMapping;
	/**
	 * List of storageProfiles.
	 */
    private List<String> storageProfile;
	/**
	 * List of catalogEntitlements.
	 */
    private List<String> catalogEntitlement;
	/**
	 * List of propertyGroups.
	 */
    private List<String> propertyGroup;
	/**
	 * List of regionMappings.
	 */
    private VraNgRegionMapping regionMapping;
	/**
	 * List of customResources.
	 */
    private List<String> customResource;
	/**
	 * List of resourceActions.
	 */
    private List<String> resourceAction;
	/**
	 * List of contentSources.
	 */
    private List<String> contentSource;
	/**
	 * Container for lists of policies for each policy type.
	 */
	private VraNgPolicy policy;
	/**
	 * Logger variable.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgPackageDescriptor.class);

	/**
	 * Getter.
	 * @return propertyGroup list.
	 */
	public List<String> getPropertyGroup() {
		return this.propertyGroup;
	}

	/**
	 * Setter.
	 * @param propertyGroup input values.
	 */
	public void setPropertyGroup( List<String> propertyGroup ) {
		this.propertyGroup	= propertyGroup;
	}

	/**
	 * Getter.
	 * @return blueprints.
	 */

    public List<String> getBlueprint() {
        return this.blueprint;
    }

	/**
	 * Setter.
	 * @param blueprint input values.
	 */
    public void setBlueprint(List<String> blueprint) {
        this.blueprint = blueprint;
    }

	/**
	 * Getter.
	 * @return catalogItems.
	 */
    public List<String> getCatalogItem() {
        return this.catalogItem;
    }

	/**
	 * Setter.
	 * @param catalogItem input values.
	 */
    public void setCatalogItem( List<String> catalogItem ) {
        this.catalogItem	= catalogItem;
    }

	/**
	 * Getter.
	 * @return subscriptions.
	 */
    public List<String> getSubscription() {
        return this.subscription;
    }

	/**
	 * Setter.
	 * @param subscription input values.
	 */
    public void setSubscription(List<String> subscription) {
        this.subscription = subscription;
    }

	/**
	 * Getter.
	 * @return flavor mappings.
	 */
    public List<String> getFlavorMapping() {
        return this.flavorMapping;
    }

	/**
	 * Setter.
	 * @param flavorMapping input values.
	 */
    public void setFlavorMapping(List<String> flavorMapping) {
        this.flavorMapping = flavorMapping;
    }

	/**
	 * Getter.
	 * @return image mappings.
	 */
    public List<String> getImageMapping() {
        return this.imageMapping;
    }

	/**
	 * Setter.
	 * @param imageMapping input values.
	 */
    public void setImageMapping(List<String> imageMapping) {
        this.imageMapping = imageMapping;
    }

	/**
	 * Getter.
	 * @return storage profiles.
	 */
    public List<String> getStorageProfile() {
        return this.storageProfile;
    }

	/**
	 * Setter.
	 * @param storageProfile input values.
	 */
    public void setStorageProfile(List<String> storageProfile) {
        this.storageProfile = storageProfile;
    }

	/**
	 * Getter.
	 * @return region mappings.
	 */
    public VraNgRegionMapping getRegionMapping() {
        return this.regionMapping;
    }

	/**
	 * Setter.
	 * @param regionMapping input values.
	 */
    public void setRegionMapping(VraNgRegionMapping regionMapping) {
        this.regionMapping = regionMapping;
    }

	/**
	 * Getter.
	 * @return catalog entitlements.
	 */
    public List<String> getCatalogEntitlement() {
        return catalogEntitlement;
    }

	/**
	 * Setter.
	 * @param catalogEntitlement input values.
	 */
    public void setCatalogEntitlement(List<String> catalogEntitlement) {
        this.catalogEntitlement = catalogEntitlement;
    }

	/**
	 * Getter.
	 * @return policy container.
	 */
	public VraNgPolicy getPolicy() {
		logger.info("VraNgPolicy.getPolicy {}", this.policy);
        return this.policy;
    }

	/**
	 * Setter.
	 * @param policy input value.
	 */
	public void setPolicy(VraNgPolicy policy) {
        logger.info("VraNgPolicy.setPolicy {}", policy);
		this.policy= policy;
    }

	/**
	 * Getter.
	 * @return customResources.
	 */
    public List<String> getCustomResource() {
        return this.customResource;
    }

	/**
	 * Setter.
	 * @param customResource input values.
	 */
    public void setCustomResource(List<String> customResource) {
        this.customResource = customResource;
    }

	/**
	 * Getter.
	 * @return resource actions.
	 */
    public List<String> getResourceAction() {
        return this.resourceAction;
    }

	/**
	 * Setter.
	 * @param resourceAction input values.
	 */
    public void setResourceAction(List<String> resourceAction) {
        this.resourceAction = resourceAction;
    }

	/**
	 * Getter.
	 * @return Content sources.
	 */
    public List<String> getContentSource() {
        return contentSource;
    }

	/**
	 * Setter.
	 * @param contentSource input values.
	 */
    public void setContentSource(List<String> contentSource) {
        this.contentSource = contentSource;
    }

	/**
	 * Getter.
	 *
	 * @param type VraNgPackageMemberType
	 * @return list of members of specific type.
	 */
    public List<String> getMembersForType(VraNgPackageMemberType type) {
        switch (type) {
            case BLUEPRINT:
                return getBlueprint();
            case SUBSCRIPTION:
                return getSubscription();
            case FLAVOR_PROFILE:
                return getFlavorMapping();
            case IMAGE_PROFILE:
                return getImageMapping();
            case STORAGE_PROFILE:
                return getStorageProfile();
            case CATALOG_ENTITLEMENT:
                return getCatalogEntitlement();
            case PROPERTY_GROUP:
                return getPropertyGroup();
            case CUSTOM_RESOURCE:
                return getCustomResource();
            case RESOURCE_ACTION:
                return getResourceAction();
            case CONTENT_SOURCE:
                return getContentSource();
            case CATALOG_ITEM:
                return getCatalogItem();
            // case REGION_MAPPING:
            // return getRegionMapping().stream().map(rm -> {
            // rm.getCloudAccountTags().keySet();
            // }).collect(Collectors.toList());
            default:
                throw new RuntimeException("ContentType is not supported!");
        }
    }

	/**
	 * List of values, filtered by  type.isNativeContent().
	 * @return list
	 */
    public boolean hasNativeContent() {
        return Arrays.stream(VraNgPackageMemberType.values())
            .filter(type -> type.isNativeContent())
            .anyMatch(type -> {
                List<String> memberNames = this.getMembersForType(type);
                return memberNames != null && !memberNames.isEmpty();
            });
    }

	/**
	 * Create a VraNgPackageDescriptor instance from file.
	 * @param filesystemPath the file
	 * @return VraNgPackageDescriptor instance
	 */
    public static VraNgPackageDescriptor getInstance(File filesystemPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            VraNgPackageDescriptor pkgDescriptor = mapper.readValue(filesystemPath, VraNgPackageDescriptor.class);
            System.out.println(ReflectionToStringBuilder.toString(pkgDescriptor, ToStringStyle.MULTI_LINE_STYLE));

            return pkgDescriptor;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load vRA Package Descriptor[" + filesystemPath.getAbsolutePath() + "]", e);
        }
    }

	/**
	 *  Create a VraNgPackageDescriptor instance from VraNgPackageContent instance.
	 * @param content the package content instance
	 * @return VraNgPackageDescriptor instance
	 */
    public static VraNgPackageDescriptor getInstance(VraNgPackageContent content) {
        Map<VraNgPackageContent.ContentType, List<String>> map = new HashMap<>();
        for (VraNgPackageContent.ContentType type : VraNgPackageContent.ContentType.values()) {
            map.put(type, new ArrayList<>());
        }

        for (Content c : content.getContent()) {
            map.get(c.getType()).add(c.getName());
        }

        VraNgPackageDescriptor pd = new VraNgPackageDescriptor();
        pd.blueprint = map.get(VraNgPackageContent.ContentType.BLUEPRINT);
        pd.subscription = map.get(VraNgPackageContent.ContentType.SUBSCRIPTION);
        pd.flavorMapping = map.get(VraNgPackageContent.ContentType.FLAVOR_MAPPING);
        pd.imageMapping = map.get(VraNgPackageContent.ContentType.IMAGE_MAPPING);
        pd.storageProfile = map.get(VraNgPackageContent.ContentType.STORAGE_PROFILE);
        pd.catalogEntitlement = map.get(VraNgPackageContent.ContentType.CATALOG_ENTITLEMENT);
        pd.customResource = map.get(VraNgPackageContent.ContentType.CUSTOM_RESOURCE);
        pd.resourceAction = map.get(VraNgPackageContent.ContentType.RESOURCE_ACTION);
        pd.contentSource = map.get(VraNgPackageContent.ContentType.CONTENT_SOURCE);
        pd.catalogItem = map.get(VraNgPackageContent.ContentType.CATALOG_ITEM);
        pd.propertyGroup = map.get(VraNgPackageContent.ContentType.PROPERTY_GROUP);
		//pd.policy = map.get(VraNgPackageContent.ContentType.POLICY);

        // System.out.println("REGION MAPPINGS ------");
        // map.get(VraNgPackageContent.ContentType.REGION_MAPPING).forEach(e -> {
        //     System.out.println(e);
        // });
        // pd.regionMapping = map.get(VraNgPackageContent.ContentType.REGION_MAPPING);

        return pd;
    }

}
