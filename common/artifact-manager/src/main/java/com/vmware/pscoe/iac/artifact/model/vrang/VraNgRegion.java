package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgRegion {

    private final String id;
    private final String cloudAccountId;

    public VraNgRegion(String id, String cloudAccountId) {
        this.id = id;
        this.cloudAccountId = cloudAccountId;
    }

    public String getId() {
        return id;
    }

    public String getCloudAccountId() {
        return cloudAccountId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgRegion other = (VraNgRegion) obj;
        return this.id.equals(other.getId());
    }

}
