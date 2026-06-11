package com.vmware.pscoe.iac.artifact.vcf.automation.models;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VcfaCatalogItemForm implements Identifiable {

    private String id;
    private String name;
    
    // CHANGED: Keep the raw field string reference internally
    private String form;
    
    private String styles;
    private String sourceType;
    private String sourceId;
    private String type;
    private String status;
    private String formFormat;

    public VcfaCatalogItemForm() {}

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // =========================================================================
    // DYNAMIC JACKSON TRANSPARENT SERIALIZATION HANDLERS
    // =========================================================================

    /**
     * When Jackson WRITES to the disk file, convert the embedded stringified 
     * json block into a readable, pretty-printed nested JSON object tree.
     */
    @JsonProperty("form")
    public JsonNode getFormAsJson() {
        if (this.form == null || this.form.trim().isEmpty()) {
            return null;
        }
        try {
            return new ObjectMapper().readTree(this.form);
        } catch (Exception e) {
            // Fallback safety layer if it's somehow not valid json
            return null;
        }
    }

    /**
     * When Jackson READS from the API or disk files, intercept the payload. 
     * If it is an object tree or a direct string, flatten/store it as a string context.
     */
    @JsonProperty("form")
    public void setFormFromJson(JsonNode node) {
        if (node == null || node.isNull()) {
            this.form = null;
            return;
        }
        if (node.isTextual()) {
            // Handled when reading from the raw API string format
            this.form = node.asText();
        } else {
            // Handled when reading from your local unescaped prettified JSON project file
            this.form = node.toString();
        }
    }

    // =========================================================================
    // REMAINING STANDARD PROPERTIES
    // =========================================================================

    public String getStyles() { return styles; }
    public void setStyles(String styles) { this.styles = styles; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFormFormat() { return formFormat; }
    public void setFormFormat(String formFormat) { this.formFormat = formFormat; }
}
