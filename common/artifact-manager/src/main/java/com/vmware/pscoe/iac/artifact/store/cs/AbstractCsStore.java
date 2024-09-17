/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
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
