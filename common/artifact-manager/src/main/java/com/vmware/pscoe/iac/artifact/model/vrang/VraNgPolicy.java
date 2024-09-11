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
package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VraNgPolicy {
	/**
	 * Logger instance for this class.
	 */
	private final Logger logger  = LoggerFactory.getLogger(VraNgPolicy .class);
	/**
	 * List of content sharing policy names.
	 */
	private final List<String> contentSharing;
	/**
	 * List of resource quota policy names.
	 */
	private final List<String> resourceQuota;
	/**
	 * List of day 2 actions policy names.
	 */
	private final List<String> day2Actions;
	/**
	 * List of lease policy names.
	 */
	private final List<String> lease;
	/**
	 * List of approval policy names.
	 */
	private final List<String> approval;
	/**
	 * List of deployment limit policy names.
	 */
	private final List<String> deploymentLimit;

	/**
	 * Empty Constructor.
	 */
	public VraNgPolicy() {
		logger.debug("Empty constructor{}, Initializing member lists with empty array lists", VraNgPolicy.class);
		this.contentSharing = new ArrayList<>();
		this.resourceQuota = new ArrayList<>();
		this.day2Actions = new ArrayList<>();
		this.lease = new ArrayList<>();
		this.approval = new ArrayList<>();
		this.deploymentLimit = new ArrayList<>();
	}

	/**
	 * Constructor.
	 * @param contentSharing list of content sharing policy names.
	 * @param resourceQuota list of resource quota policy names.
	 * @param day2Actions list of day 2 actions policy names.
	 * @param lease list of lease policy names.
	 * @param approval list of approval policy names.
	 * @param deploymentLimit list of deployment limit policy names.
	 */
	public VraNgPolicy(List<String> contentSharing, List<String> resourceQuota, List<String> day2Actions, List<String> lease, List<String> approval, List<String> deploymentLimit) {
		logger.debug("Parametrized constructor {}", VraNgPolicy.class);
		logger.debug("content sharing in {}", contentSharing);
		logger.debug("resource quota in {}", resourceQuota);
		logger.debug("day2 actions in {}", day2Actions);
		logger.debug("lease  in {}", lease);
		logger.debug("approval  in {}", approval);
		logger.debug("deploymentLimit  in {}", deploymentLimit);
		this.contentSharing = contentSharing;
		this.resourceQuota = resourceQuota;
		this.day2Actions = day2Actions;
		this.lease = lease;
		this.approval = approval;
		this.deploymentLimit = deploymentLimit;
	}

	/**
	 * Getter.
	 * @return list of content sharing policy names.
	 */
	public List<String> getContentSharing() {
		logger.debug("getContentSharing{}",  this.contentSharing);
		return this.contentSharing;
	}
	/**
	 * Getter.
	 * @return list of resource quota policy names.
	 */
	public List<String> getResourceQuota() {
		logger.debug("getResourceQuota{}", this.resourceQuota);
		return this.resourceQuota;
	}
	/**
	 * Getter.
	 * @return list of day 2 actions policy names.
	 */
	public List<String> getDay2Actions() {
		logger.debug("getDay2Actions{}", this.day2Actions);
		return this.day2Actions;
	}
	/**
	 * Getter.
	 * @return list of lease policy names.
	 */
	public List<String> getLease() {
		logger.debug("lease{}", this.lease);
		return this.lease;
	}
	/**
	 * Getter.
	 * @return list of approval policy names.
	 */
	public List<String> getApproval() {
		logger.debug("approval{}", this.approval);
		return this.approval;
	}
	/**
	 * Getter.
	 * @return list of deployment limit policy names.
	 */
	public List<String> getDeploymentLimit() {
		logger.debug("deploymentLimit{}", this.deploymentLimit);
		return this.deploymentLimit;
	}

}
