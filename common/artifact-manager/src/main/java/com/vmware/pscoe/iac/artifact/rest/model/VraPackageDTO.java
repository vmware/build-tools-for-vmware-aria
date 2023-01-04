package com.vmware.pscoe.iac.artifact.rest.model;

import java.util.List;

public class VraPackageDTO {

    // "{\"name\" : \"Demo3\", \"description\" : \"Demo1 Description\", \"contents\" : [\"5c46fe1f-20a6-471a-bd92-6d9458baad00\"] }"

    private final String name;
    private final String description;
    private final List<String> contents;

    public VraPackageDTO(String name, List<String> contents) {
        this.name = name;
        this.description = "Managed by IaaC for vRealize";
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getContents() {
        return contents;
    }

}
