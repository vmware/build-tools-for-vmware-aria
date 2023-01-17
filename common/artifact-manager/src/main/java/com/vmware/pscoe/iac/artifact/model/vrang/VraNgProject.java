package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgProject {
    private final String id;
    private final String name;

    public VraNgProject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

