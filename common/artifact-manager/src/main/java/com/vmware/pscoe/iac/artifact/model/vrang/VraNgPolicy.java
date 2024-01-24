package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.*;

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

import com.vmware.pscoe.iac.artifact.store.vrang.VraNgPolicyStore;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VraNgPolicy {
	private final Logger logger  = LoggerFactory.getLogger(VraNgPolicy .class);
//	private final List<String> contentSharing;
//	private final List<String> resourceQuota;
	private final EnumMap<VraNgPolicyFolderName, List<String>> policiesMap;

	public VraNgPolicy() {
		logger.info("Empty constructor{}", VraNgPolicyStore.class);
		this.policiesMap = new EnumMap<VraNgPolicyFolderName, List<String>>(VraNgPolicyFolderName.class);
//		this.policiesMap.put(VraNgPolicyFolderName.CONTENT_SHARING, null);
//		this.policiesMap.put(VraNgPolicyFolderName.RESOURCE_QUOTA, null);
	}

	public VraNgPolicy(List<String> contentSharing, List<String> resourceQuota) {
		logger.info("Parametrized constructor {}", VraNgPolicyStore.class);
		logger.info("content sharing in {}", contentSharing);
		logger.info("resource quota in {}", resourceQuota);

        //policiesMap  = new EnumMap<VraNgPolicyFolderName, List<String>>(VraNgPolicyFolderName.CONTENT_SHARING, contentSharing, VraNgPolicyFolderName.RESOURCE_QUOTA, resourceQuota);
		this.policiesMap = new EnumMap<VraNgPolicyFolderName, List<String>>(VraNgPolicyFolderName.class);
		this.policiesMap.put(VraNgPolicyFolderName.CONTENT_SHARING, contentSharing);
		this.policiesMap.put(VraNgPolicyFolderName.RESOURCE_QUOTA, resourceQuota);
		logger.info("policiesMap result {}", policiesMap);
		//TODO: add the rest when implemented
    }
	private VraNgPolicy(List<String> policies) {
		logger.info("parametrized constructor {}", VraNgPolicyStore.class);
		logger.info("list in {}", policies);
		//logger.info("resource quota in{}", resourceQuota);
		this.policiesMap = new EnumMap<VraNgPolicyFolderName, List<String>>(VraNgPolicyFolderName.class);

		if (policies !=null ) {
			int index = -1;
			List<String> specificTypePolicyList  = new ArrayList<>();
			for (String policyName : policies) {
				if (policyName.endsWith(":")) {
					index++;
					String policyFolder = policyName.split(":")[0];
					this.policiesMap.put( VraNgPolicyFolderName.fromString(policyFolder),specificTypePolicyList );
					specificTypePolicyList.clear();
				} else {
					specificTypePolicyList.add(policyName);
				}
			}
		}
		logger.info("policiesMap result {}", policiesMap);
	}

	public List<String> getContentSharing() {
		logger.info("getContentSharing{}", this.policiesMap.get(VraNgPolicyFolderName.CONTENT_SHARING));
		//return this.contentSharing;
		return this.policiesMap.get(VraNgPolicyFolderName.CONTENT_SHARING);
	}
	public List<String> getResourceQuota() {
		logger.info("getResourceQuota{}", this.policiesMap.get(VraNgPolicyFolderName.RESOURCE_QUOTA));
		//return this.resourceQuota;
		return this.policiesMap.get(VraNgPolicyFolderName.RESOURCE_QUOTA);
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
