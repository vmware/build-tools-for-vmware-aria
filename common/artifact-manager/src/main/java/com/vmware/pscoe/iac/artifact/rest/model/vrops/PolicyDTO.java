package com.vmware.pscoe.iac.artifact.rest.model.vrops;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class PolicyDTO {

    @JsonProperty("policy-summaries")
    private List<Policy> policies;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("policy-summaries")
    public List<Policy> getPolicies() {
        return this.policies;
    }

    @JsonProperty("policy-summaries")
    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonPropertyOrder({ "id", "name" })
    public static class Policy {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonIgnore
        private byte[] zipFile;

        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<>();

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

        @JsonIgnore
        public void setZipFile(byte[] file) {
            this.zipFile = file;
        }

        @JsonIgnore
        public byte[] getZipFile() {
            return this.zipFile;
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
