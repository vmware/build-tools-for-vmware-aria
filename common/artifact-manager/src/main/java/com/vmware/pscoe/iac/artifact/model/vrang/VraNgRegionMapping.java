package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.vrang.objectmapping.VraNgCloudAccountTag;

import org.apache.commons.lang3.NotImplementedException;

public class VraNgRegionMapping {

    private VraNgCloudAccountTag cloudAccountTags;

    public VraNgRegionMapping() {
        super();
        this.cloudAccountTags = new VraNgCloudAccountTag();
    }

    public VraNgRegionMapping(VraNgCloudAccountTag cloudAccountTags) {
        this.cloudAccountTags = cloudAccountTags;
    }

    public VraNgCloudAccountTag getCloudAccountTags() {
        return this.cloudAccountTags;
    }

    @Override
    public boolean equals(Object obj) {
        throw new NotImplementedException("Not implemented");
    }

}
