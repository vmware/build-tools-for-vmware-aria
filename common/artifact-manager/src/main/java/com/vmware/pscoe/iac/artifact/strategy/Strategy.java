package com.vmware.pscoe.iac.artifact.strategy;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.Package;

public interface Strategy {

    public List<Package> getImportPackages(List<Package> sourceEndpointPackages,
                    List<Package> destinationEndpointPackages);

    public List<Package> getExportPackages(List<Package> sourceEndpointPackages,
                    List<Package> destinationEndpointPackages);

}
