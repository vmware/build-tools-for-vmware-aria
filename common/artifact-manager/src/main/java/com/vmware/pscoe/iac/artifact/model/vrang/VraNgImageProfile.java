package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;

public class VraNgImageProfile {

    private final String id;
    private List<VraNgImageMapping> imageMappings;

    public VraNgImageProfile(String id, List<VraNgImageMapping> imageMappings) {
        this.id = id;
        this.imageMappings = imageMappings;
    }

    public String getId() {
        return this.id;
    }

    public List<VraNgImageMapping> getImageMappings() {
        return imageMappings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgImageProfile other = (VraNgImageProfile) obj;
        return this.id.equals(other.getId());
    }

}
