package com.vmware.pscoe.iac.artifact.extentions;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

public interface PackageStoreExtention<T extends PackageDescriptor> {
    
    public Package exportPackage(Package serverPackage, T packageDescriptor, boolean dryrun);
    
    public Package importPackage(Package filesystemPackage, boolean dryrun);

}
