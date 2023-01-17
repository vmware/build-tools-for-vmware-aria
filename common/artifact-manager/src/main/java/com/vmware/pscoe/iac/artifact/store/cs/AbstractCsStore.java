package com.vmware.pscoe.iac.artifact.store.cs;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientCs;

public abstract class AbstractCsStore implements ICsStore {
    protected RestClientCs restClient;
    protected Package csPackage;
    protected CsPackageDescriptor descriptor;
    protected ConfigurationCs config;

    public void init(RestClientCs restClient, Package csPackage, ConfigurationCs config, CsPackageDescriptor vraNgPackageDescriptor) {
        this.restClient = restClient;
        this.csPackage = csPackage;
        this.descriptor = vraNgPackageDescriptor;
        this.config = config;
    }
}
