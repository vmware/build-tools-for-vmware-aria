package com.vmware.pscoe.iac.artifact.model.vcd;

import java.io.File;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

import org.apache.commons.lang3.NotImplementedException;

public class VcdPackageDescriptor extends PackageDescriptor {

    private List<String> content;

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getMembersForType() {
        throw new NotImplementedException("To be implemented");
    }

    public static VcdPackageDescriptor getInstance(File filesystemPath) {
        throw new NotImplementedException("To be implemented");
    }

    public static VcdPackageDescriptor getInstance() {
        throw new NotImplementedException("To be implemented");
    }

}
