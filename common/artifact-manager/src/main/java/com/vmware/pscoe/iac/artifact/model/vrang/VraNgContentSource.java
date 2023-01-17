package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;
import java.util.Map;

public class VraNgContentSource extends VraNgContentSourceBase {
    protected String projectId;
    protected Map<String, String> config;

    public VraNgContentSource(String id, String name, VraNgContentSourceType typeId,
            String projectId, Integer itemsFound, Integer itemsImported,
            List<String> importErrors) {
        this.id = id;
        this.name = name;
        this.typeId = typeId.toString();
        this.projectId = projectId;
        this.itemsFound = itemsFound;
        this.itemsImported = itemsImported;
        this.lastImportErrors = importErrors;
    }

    public VraNgContentSource(String id, String name, VraNgContentSourceType typeId,
            String projectId, Integer itemsFound, Integer itemsImported) {
        this.id = id;
        this.name = name;
        this.typeId = typeId.toString();
        this.projectId = projectId;
        this.itemsFound = itemsFound;
        this.itemsImported = itemsImported;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
