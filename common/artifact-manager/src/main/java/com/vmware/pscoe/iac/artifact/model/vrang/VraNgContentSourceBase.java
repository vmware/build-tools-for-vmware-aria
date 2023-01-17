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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VraNgContentSourceBase {

    @JsonProperty("id")
    protected String id;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("description")
    protected String description;

    @JsonProperty("typeId")
    protected String typeId;

    @JsonProperty("itemsImported")
    protected int itemsImported;

    @JsonProperty("itemsFound")
    protected int itemsFound;

    @JsonProperty("lastImportErrors")
    protected transient List<String> lastImportErrors = new ArrayList<>();

    @JsonProperty("global")
    protected boolean global;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Integer getItemsFound() {
        return itemsFound;
    }

    public void setItemsFound(Integer itemsFound) {
        this.itemsFound = itemsFound;
    }

    public Integer getItemsImported() {
        return itemsImported;
    }

    public void setItemsImported(Integer itemsImported) {
        this.itemsImported = itemsImported;
    }

    public List<String> getLastImportErrors() {
        return lastImportErrors;
    }

    public void setLastImportErrors(List<String> lastImportErrors) {
        this.lastImportErrors = lastImportErrors;
    }
    @JsonIgnore
    public VraNgContentSourceType getType() {
        return VraNgContentSourceType.fromString(this.typeId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

}
