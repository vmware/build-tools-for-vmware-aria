package com.vmware.pscoe.iac.artifact.model.vrang.objectmapping;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

public class VraNgCloudAccountTag {

    private final String exportTag;
    private final List<String> importTags;

    public VraNgCloudAccountTag() {
        super();
        this.exportTag = "";
        this.importTags = new ArrayList<>();
    }

    public VraNgCloudAccountTag(String exportTag, List<String> importTags) {
        this.exportTag = exportTag;
        this.importTags = importTags;
    }

    public String getExportTag() {
        return this.exportTag;
    }

    public List<String> getImportTags() {
        return this.importTags;
    }

    @Override
    public boolean equals(Object obj) {
        throw new NotImplementedException("Not implemented");
    }

}
