package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgCustomResource {

    private final String id;
    private final String name;
    private final String json;

    public VraNgCustomResource(String id, String name, String json) {
        this.id = id;
        this.name = name;
        this.json = json;
    }

    public String getId() {
        return this.id;
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

        VraNgCustomResource other = (VraNgCustomResource) obj;
        return this.id.equals(other.getId());
    }

}
