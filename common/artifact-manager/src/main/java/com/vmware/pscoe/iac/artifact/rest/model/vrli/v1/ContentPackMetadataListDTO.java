package com.vmware.pscoe.iac.artifact.rest.model.vrli.v1;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.ContentPackDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "contentPackMetadataList" })
public class ContentPackMetadataListDTO implements Serializable {
    private static final long serialVersionUID = 7183649262190355333L;

    @JsonProperty("contentPackMetadataList")
    private List<ContentPackDTO> contentPackMetadataList;

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("contentPackMetadataList")
    public List<ContentPackDTO> getContentPackMetadataList() {
        return contentPackMetadataList;
    }

    @JsonProperty("contentPackMetadataList")
    public void setContentPackMetadataList(List<ContentPackDTO> contentPackMetadataList) {
        this.contentPackMetadataList = contentPackMetadataList;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
