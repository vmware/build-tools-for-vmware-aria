package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgImageMapping {

    private final String name;
    private final String json;

    public VraNgImageMapping(String name, String json) {
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

        VraNgImageMapping other = (VraNgImageMapping) obj;
        return this.name.equals(other.getName());
    }

}
