
package com.vmware.pscoe.iac.artifact.rest.model.vrops;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pageInfo", "links", "reportDefinitions" })
@JsonIgnoreProperties({ "pageInfo", "links" })
public class ReportDefinitionDTO implements Serializable {
    private static final long serialVersionUID = -8001968595944498757L;

    @JsonProperty("reportDefinitions")
    private List<ReportDefinition> reportDefinitions = new ArrayList<>();

    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("reportDefinitions")
    public List<ReportDefinition> getReportDefinitions() {
        return reportDefinitions;
    }

    @JsonProperty("reportDefinitions")
    public void setReportDefinitions(List<ReportDefinition> reportDefinitions) {
        this.reportDefinitions = reportDefinitions;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "id", "name", "description", "subject", "traversal-specs", "active", "owner" })
    public static class ReportDefinition implements Serializable {
        private static final long serialVersionUID = 5524309873790715075L;

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("subject")
        private List<String> subject = new ArrayList<>();

        @JsonProperty("traversal-specs")
        private TraversalSpecs traversalSpecs;

        @JsonProperty("active")
        private Boolean active;

        @JsonProperty("owner")
        private String owner;

        @JsonIgnore
        private transient Map<String, Object> additionalProperties = new HashMap<>();

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @JsonProperty("subject")
        public List<String> getSubject() {
            return subject;
        }

        @JsonProperty("subject")
        public void setSubject(List<String> subject) {
            this.subject = subject;
        }

        @JsonProperty("traversal-specs")
        public TraversalSpecs getTraversalSpecs() {
            return traversalSpecs;
        }

        @JsonProperty("traversal-specs")
        public void setTraversalSpecs(TraversalSpecs traversalSpecs) {
            this.traversalSpecs = traversalSpecs;
        }

        @JsonProperty("active")
        public Boolean isActive() {
            return active;
        }

        @JsonProperty("active")
        public void setActive(Boolean active) {
            this.active = active;
        }

        @JsonProperty("owner")
        public String getOwner() {
            return owner;
        }

        @JsonProperty("owner")
        public void setOwner(String owner) {
            this.owner = owner;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperties(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonPropertyOrder({ "specs" })
        public static class TraversalSpecs implements Serializable {
            private static final long serialVersionUID = 5055582582989558793L;

            @JsonProperty("specs")
            private List<Spec> specs = new ArrayList<>();

            @JsonIgnore
            private transient Map<String, Object> additionalProperties = new HashMap<>();

            @JsonProperty("specs")
            public List<Spec> getSpecs() {
                return specs;
            }

            @JsonProperty("specs")
            public void setSpecs(List<Spec> specs) {
                this.specs = specs;
            }

            @JsonAnyGetter
            public Map<String, Object> getAdditionalProperties() {
                return this.additionalProperties;
            }

            @JsonAnySetter
            public void setAdditionalProperties(String name, Object value) {
                this.additionalProperties.put(name, value);
            }

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonPropertyOrder({ "name", "description", "rootAdapterKindKey", "rootResourceKindKey", "adapterInstanceAssociation" })
            public static class Spec implements Serializable {
                private static final long serialVersionUID = 4779483169838434664L;

                @JsonProperty("name")
                private String name;

                @JsonProperty("description")
                private String description;

                @JsonProperty("rootAdapterKindKey")
                private String rootAdapterKindKey;

                @JsonProperty("rootResourceKindKey")
                private String rootResourceKindKey;

                @JsonProperty("adapterInstanceAssociation")
                private Boolean adapterInstanceAssociation;

                @JsonIgnore
                private transient Map<String, Object> additionalProperties = new HashMap<>();

                @JsonProperty("name")
                public String getName() {
                    return name;
                }

                @JsonProperty("name")
                public void setName(String name) {
                    this.name = name;
                }

                @JsonProperty("description")
                public String getDescription() {
                    return description;
                }

                @JsonProperty("description")
                public void setDescription(String description) {
                    this.description = description;
                }

                @JsonProperty("rootAdapterKindKey")
                public String getRootAdapterKindKey() {
                    return rootAdapterKindKey;
                }

                @JsonProperty("rootAdapterKindKey")
                public void setRootAdapterKindKey(String rootAdapterKindKey) {
                    this.rootAdapterKindKey = rootAdapterKindKey;
                }

                @JsonProperty("rootResourceKindKey")
                public String getRootResourceKindKey() {
                    return rootResourceKindKey;
                }

                @JsonProperty("rootResourceKindKey")
                public void setRootResourceKindKey(String rootResourceKindKey) {
                    this.rootResourceKindKey = rootResourceKindKey;
                }

                @JsonProperty("adapterInstanceAssociation")
                public Boolean isAdapterInstanceAssociation() {
                    return adapterInstanceAssociation;
                }

                @JsonProperty("adapterInstanceAssociation")
                public void setAdapterInstanceAssociation(Boolean adapterInstanceAssociation) {
                    this.adapterInstanceAssociation = adapterInstanceAssociation;
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
        }
    }
}
