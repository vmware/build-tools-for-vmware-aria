package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.Collections;
import java.util.List;

public class VraNgCloudAccount {

    private final String id;
    private final String name;
    private final String type;
    private final List<String> regionIds;
    private final List<String> tags;

    public VraNgCloudAccount(String id, String name, String type, List<String> regionIds, List<String> tags) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.regionIds = regionIds;
        this.tags = tags;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getRegionIds() {
        return Collections.unmodifiableList(this.regionIds);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgBlueprint other = (VraNgBlueprint) obj;
        return this.id.equals(other.getId());
    }

}
