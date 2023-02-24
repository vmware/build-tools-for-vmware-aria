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

public class VraNgPackageDescriptor extends PackageDescriptor {
    private List<String> catalogItem;
    private List<String> blueprint;
    private List<String> subscription;
    private List<String> flavorMapping;
    private List<String> imageMapping;
    private List<String> storageProfile;
    private List<String> catalogEntitlement;
    private List<String> propertyGroup;
    private VraNgRegionMapping regionMapping;
    private List<String> customResource;
    private List<String> resourceAction;
    private List<String> contentSource;
	private VraNgPolicy policy;

	public List<String> getPropertyGroup() {
		return this.propertyGroup;
	}

	public void setPropertyGroup( List<String> propertyGroup ) {
		this.propertyGroup	= propertyGroup;
	}

    public List<String> getBlueprint() {
        return this.blueprint;
    }

    public void setBlueprint(List<String> blueprint) {
        this.blueprint = blueprint;
    }

    public List<String> getCatalogItem() {
        return this.catalogItem;
    }

    public void setCatalogItem( List<String> catalogItem ) {
        this.catalogItem	= catalogItem;
    }

    public List<String> getSubscription() {
        return this.subscription;
    }

    public void setSubscription(List<String> subscription) {
        this.subscription = subscription;
    }

    public List<String> getFlavorMapping() {
        return this.flavorMapping;
    }

    public void setFlavorMapping(List<String> flavorMapping) {
        this.flavorMapping = flavorMapping;
    }

    public List<String> getImageMapping() {
        return this.imageMapping;
    }

    public void setImageMapping(List<String> imageMapping) {
        this.imageMapping = imageMapping;
    }

    public List<String> getStorageProfile() {
        return this.storageProfile;
    }

    public void setStorageProfile(List<String> storageProfile) {
        this.storageProfile = storageProfile;
    }

    public VraNgRegionMapping getRegionMapping() {
        return this.regionMapping;
    }

    public void setRegionMapping(VraNgRegionMapping regionMapping) {
        this.regionMapping = regionMapping;
    }

    public List<String> getCatalogEntitlement() {
        return catalogEntitlement;
    }

    public void setCatalogEntitlement(List<String> catalogEntitlement) {
        this.catalogEntitlement = catalogEntitlement;
    }

	public VraNgPolicy getPolicy() {
        return this.policy;
    }

	public void setPolicy(VraNgPolicy policy) {
        this.policy= policy;
    }

    public List<String> getCustomResource() {
        return this.customResource;
    }

    public void setCustomResource(List<String> customResource) {
        this.customResource = customResource;
    }

    public List<String> getResourceAction() {
        return this.resourceAction;
    }

    public void setResourceAction(List<String> resourceAction) {
        this.resourceAction = resourceAction;
    }

    public List<String> getContentSource() {
        return contentSource;
    }

    public void setContentSource(List<String> contentSource) {
        this.contentSource = contentSource;
    }
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

    public boolean hasNativeContent() {
        return Arrays.stream(VraNgPackageMemberType.values())
            .filter(type -> type.isNativeContent())
            .anyMatch(type -> {
                List<String> memberNames = this.getMembersForType(type);
                return memberNames != null && !memberNames.isEmpty();
            });
    }

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

        // System.out.println("REGION MAPPINGS ------");
        // map.get(VraNgPackageContent.ContentType.REGION_MAPPING).forEach(e -> {
        //     System.out.println(e);
        // });
        // pd.regionMapping = map.get(VraNgPackageContent.ContentType.REGION_MAPPING);

        return pd;
    }

}
