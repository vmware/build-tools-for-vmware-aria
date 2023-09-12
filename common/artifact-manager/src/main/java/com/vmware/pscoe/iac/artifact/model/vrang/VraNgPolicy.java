package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.commons.lang3.NotImplementedException;

public class VraNgPolicy {

	private final List<String> contentSharing;
	private final List<String> resourceQuota;
	private final List<String> lease;
	private final List<String> day2Actions;
	private final List<String> approval;
	private final List<String> deploymentLimit;

	public VraNgPolicy() {
		super();
		this.contentSharing = new ArrayList<>();
		this.resourceQuota = new ArrayList<>();
		this.lease = new ArrayList<>();
		this.day2Actions = new ArrayList<>();
		this.approval = new ArrayList<>();
		this.deploymentLimit = new ArrayList<>();
	}

	public VraNgPolicy(List<String> contentSharing,
					   List<String> resourceQuota,
					   List<String> lease,
					   List<String> day2Actions,
					   List<String> approval,
					   List<String> deploymentLimit) {
		this.contentSharing = contentSharing;
		this.resourceQuota = resourceQuota;
		this.lease = lease;
		this.day2Actions = day2Actions;
		this.approval = approval;
		this.deploymentLimit = deploymentLimit;
	}

	public List<String> getContentSharingPolicies() {
		return this.contentSharing;
	}

	public List<String> getResourceQuotaPolicies() {
		return this.resourceQuota;
	}

	public List<String> getLeasePolicies() {
		return this.lease;
	}

	public List<String> getDay2ActionsPolicies() {
		return this.day2Actions;
	}

	public List<String> getApprovalPolicies() {
		return this.approval;
	}

	public List<String> getDeploymentLimitPolicies() {
		return this.deploymentLimit;
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
