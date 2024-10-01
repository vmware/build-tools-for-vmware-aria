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

import com.vmware.pscoe.iac.artifact.model.vrang.objectmapping.VraNgCloudAccountTag;

import org.apache.commons.lang3.NotImplementedException;

/**
 * This class is used to map the region to the cloud account tags.
 */
public class VraNgRegionMapping {

	/**
	 * @param cloudAccountTags The cloud account tags to be set
	 */
	private VraNgCloudAccountTag cloudAccountTags;

	/**
	 * Default constructor.
	 */
	public VraNgRegionMapping() {
		super();
		this.cloudAccountTags = new VraNgCloudAccountTag();
	}

	/**
	 * Constructor with cloud account tags.
	 * 
	 * @param cloudAccountTags The cloud account tags to be set
	 */
	public VraNgRegionMapping(VraNgCloudAccountTag cloudAccountTags) {
		this.cloudAccountTags = cloudAccountTags;
	}

	/**
	 * Get the cloud account tags.
	 * 
	 * @return The cloud account tags
	 */
	public VraNgCloudAccountTag getCloudAccountTags() {
		return this.cloudAccountTags;
	}

	/**
	 * Check if the object is equal to this object.
	 *
	 * @param obj The object to compare
	 * @return True if the object is equal to this object, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
