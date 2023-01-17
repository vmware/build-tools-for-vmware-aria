package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.LinkedHashMap;
import java.util.Map;

public class VraNgCatalogEntitlementDto {

    private String id;
    private String projectId;
    private Map<String, String> definition = new LinkedHashMap<String, String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Map<String, String> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, String> definition) {
        this.definition = definition;
    }

}
