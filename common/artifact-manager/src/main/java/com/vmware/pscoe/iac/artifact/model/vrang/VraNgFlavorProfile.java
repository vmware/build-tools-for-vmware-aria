package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;

public class VraNgFlavorProfile {

    private final String id;
    private List<VraNgFlavorMapping> flavorMappings;

    public VraNgFlavorProfile(String id, List<VraNgFlavorMapping> flavorMappings) {
        this.id = id;
        this.flavorMappings = flavorMappings;
    }

    public String getId() {
        return this.id;
    }

    public List<VraNgFlavorMapping> getFlavorMappings() {
        return flavorMappings;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgFlavorProfile other = (VraNgFlavorProfile) obj;
        return this.id.equals(other.getId());
    }

}
