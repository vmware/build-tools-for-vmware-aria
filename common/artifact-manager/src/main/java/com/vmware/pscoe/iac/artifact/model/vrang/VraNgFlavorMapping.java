package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgFlavorMapping {

    private final String name;
    private final String json;

    public VraNgFlavorMapping(String name, String json) {
        this.name = name;
        this.json = json;
    }

    public String getName() {
        return this.name;
    }

    public String getJson() {
        return this.json;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgFlavorMapping other = (VraNgFlavorMapping) obj;
        return this.name.equals(other.getName());
    }

}
