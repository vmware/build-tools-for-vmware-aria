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

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified model for Catalog Entitlement. A catalog entitlement is used to
 * define content sharing of catalog items in or among projects. A catalog item
 * is any requestable item coming from a content source, e.g. Cloud Assembly
 * blueprints, vRealize Orchestrator workflows, Cloud Formation templates, ABX
 * actions, etc. With catalog entitlements a catalog administrator can define a
 * set of catalog items, or entire catalog sources to be visible within the
 * scope of one or more project, thus enabling users that belong to the project
 * to be able to request those catalog items.
 */
public class VraNgCatalogEntitlement {

    // {
    // "definition": {
    // "description": "string",
    // "iconId": "string",
    // "id": "string",
    // "name": "string",
    // "numItems": 0,
    // "sourceName": "string",
    // "sourceType": "string",
    // "type": "string"
    // },
    // "id": "string",
    // "projectId": "string"
    // }

    private String id;
    private String name;
    private VraNgCatalogEntitlementType type;
    private VraNgContentSourceType sourceType;
    private String sourceId;
    private String iconId;
    private List<String> projects = new ArrayList<>();

    public VraNgCatalogEntitlement(String name, VraNgCatalogEntitlementType type, VraNgContentSourceType sourceType) {
        this.name = name;
        this.type = type;
        this.sourceType = sourceType;
    }

    public VraNgCatalogEntitlement(String name, List<String> projects, VraNgCatalogEntitlementType type, VraNgContentSourceType sourceType) {
        this.name = name;
        this.type = type;
        this.sourceType = sourceType;
        this.projects = projects;
    }

    public VraNgCatalogEntitlement(String id, String sourceId, String name, VraNgCatalogEntitlementType type, VraNgContentSourceType sourceType) {
        this.id = id;
        this.sourceId = sourceId;
        this.name = name;
        this.type = type;
        this.sourceType = sourceType;
    }

    public VraNgCatalogEntitlement(String id, String sourceId, String name, List<String> projects, VraNgCatalogEntitlementType type,
            VraNgContentSourceType sourceType) {
        this.id = id;
        this.sourceId = sourceId;
        this.name = name;
        this.type = type;
        this.sourceType = sourceType;
        this.projects = projects;
    }

    public VraNgCatalogEntitlement() {
    }

    /**
     * Get the id of the entitlement.
     * 
     * @return catalog entitlement id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id of the entitlement.
     * 
     * @param id - id of the entitlement
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Get the iconId of the entitlement.
     * 
     * @return catalog entitlement icon
     */
    public String getIconId() {
        return this.iconId;
    }

     /**
     * Set the id of the entitlement.
     * 
     * @param iconId - iconId of the entitlement
     */
    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    /**
     * Get the name of the entitlement. Usually this is the name of a catalog item
     * source or a catalog item.
     * 
     * @return catalog entitlement name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the entitlement.
     * 
     * @param name name of the entitlement
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the entitlement. This represents the type of the catalog item
     * behind the entitlement.
     * 
     * @return catalog item type
     */
    public VraNgCatalogEntitlementType getType() {
        return this.type;
    }

    /**
     * Set the type of the entitlement. This represents the type of the catalog item
     * behind the entitlement.
     * 
     * @param type - type of the entitlement
     */
    public void setType(VraNgCatalogEntitlementType type) {
        this.type = type;
    }

    /**
     * Get the content source type of the entitlement. This represents the type of
     * the content source, e.g. abx, vro, cloud assembly, etc.
     * 
     * @return source type
     */
    public VraNgContentSourceType getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(VraNgContentSourceType sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * Get the content source id of the entitlement. This correlates to a catalog
     * source or catalog item.
     * 
     * @return source id
     */
    public String getSourceId() {
        return this.sourceId;
    }

    /**
     * Set source id of the entitlement. This is used when a new entitlement is
     * being created.
     * 
     * @param id source id
     */
    public void setSourceId(String id) {
        this.sourceId = id;
    }

    /**
     * Get the project of the entitlement. This correlates to the projects that it
     * has to be shared with.
     * 
     * @return projects - the projects associated with the entitlement.
     */
    public List<String> getProjects() {
        return projects;
    }

    /**
     * Sets the project of the entitlement. This correlates to the projects that it
     * has to be shared with.
     * 
     * @param projects projects to be set
     */
    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

}
