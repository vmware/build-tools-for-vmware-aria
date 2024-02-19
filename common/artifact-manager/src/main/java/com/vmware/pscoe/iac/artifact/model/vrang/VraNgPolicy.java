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

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VraNgPolicy {
	private final Logger logger  = LoggerFactory.getLogger(VraNgPolicy .class);
	private final List<String> contentSharing;
	private final List<String> resourceQuota;
	private final List<String> day2Actions;

	public VraNgPolicy() {
		logger.debug("Empty constructor{}, Initializing member lists with empty array lists", VraNgPolicy.class);
		this.contentSharing = new ArrayList<>();
		this.resourceQuota = new ArrayList<>();
		this.day2Actions = new ArrayList<>();
	}

	public VraNgPolicy(List<String> contentSharing, List<String> resourceQuota, List<String> day2Actions) {
		logger.debug("Parametrized constructor {}", VraNgPolicy.class);
		logger.debug("content sharing in {}", contentSharing);
		logger.debug("resource quota in {}", resourceQuota);
		logger.debug("day2 actions in {}", day2Actions);
		this.contentSharing = contentSharing;
		this.resourceQuota = resourceQuota;
		this.day2Actions = day2Actions;

    }


	public List<String> getContentSharing() {
		logger.debug("getContentSharing{}",  this.contentSharing);
		return this.contentSharing;
	}
	public List<String> getResourceQuota() {
		logger.debug("getResourceQuota{}", this.resourceQuota );
		return this.resourceQuota;
	}
	public List<String> getDay2Actions() {
		logger.debug("getDay2Actions{}", this.day2Actions);
		return this.day2Actions;
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
