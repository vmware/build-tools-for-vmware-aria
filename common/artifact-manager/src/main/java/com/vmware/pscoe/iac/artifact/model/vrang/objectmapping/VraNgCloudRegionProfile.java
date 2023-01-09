package com.vmware.pscoe.iac.artifact.model.vrang.objectmapping;

import java.util.List;

public class VraNgCloudRegionProfile {

    private String cloudAccountId;
    private String regionId;
    private String regionType;
    private List<String> tags;

    public String getCloudAccountId() {
        return cloudAccountId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public void setCloudAccountId(String cloudAccountId) {
        this.cloudAccountId = cloudAccountId;
    }

}
