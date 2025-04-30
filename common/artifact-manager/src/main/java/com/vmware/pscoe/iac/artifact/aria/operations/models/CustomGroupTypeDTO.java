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
package com.vmware.pscoe.iac.artifact.aria.operations.models;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a Data Transfer Object (DTO) for a custom group type in VMware Aria Operations. This
 * class is used for serializing and de-serializing JSON data related to custom group types.
 */
@JsonPropertyOrder({"name", "key"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CustomGroupTypeDTO {

    /**
     * The name of the custom group type.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The unique key of the custom group type.
     */
    @JsonProperty("key")
    private String key;

    /**
     * A map to hold additional properties that are not explicitly defined in this class.
     */
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

    /**
     * Gets the name of the custom group type.
     *
     * @return the name of the custom group type.
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the custom group type.
     *
     * @param name the name to set.
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the unique key of the custom group type.
     *
     * @return the key of the custom group type.
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     * Sets the unique key of the custom group type.
     *
     * @param key the key to set.
     */
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the additional properties that are not explicitly defined in this class.
     *
     * @return a map of additional properties.
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Sets an additional property that is not explicitly defined in this class.
     *
     * @param name the name of the additional property.
     * @param value the value of the additional property.
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
