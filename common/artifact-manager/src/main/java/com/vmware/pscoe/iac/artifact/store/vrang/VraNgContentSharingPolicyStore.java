package com.vmware.pscoe.iac.artifact.store.vrang;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;

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

public class VraNgContentSharingPolicyStore extends AbstractVraNgStore {
	private List<VraNgProject> projects;
    private String configuredProjectId;
	private final Logger logger = LoggerFactory.getLogger(VraNgContentSharingPolicyStore.class);

	@Override
	public void importContent(File sourceDirectory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		//1
		return this.vraNgPackageDescriptor.getContentSharingPolicy();
	}

	@Override
	protected void exportStoreContent() {
		// 2
		List<VraNgContentSharingPolicy> contentSharingPolicies = this.restClient.getContentSharingPolicies();
       // contentSharingPolicies.forEach(entitlement -> storeEntitlementOnFilesystem(vraNgPackage, entitlement));
	   logger.info("Gowtham partial success");
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
		// TODO Auto-generated method stub
		
	}
	
}
