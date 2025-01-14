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
package com.vmware.pscoe.iac.artifact.aria.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Aria automation > Infrastructure > Region.
 */
public class VraNgRegion {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;

	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	private final String id;
	private final String cloudAccountId;

	/**
	 * @param id             - the id of the region
	 * @param cloudAccountId - the cloud account id for the region
	 */
	public VraNgRegion(String id, String cloudAccountId) {
		this.id = id;
		this.cloudAccountId = cloudAccountId;
	}

	/**
	 * @return the id of the region
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the cloud account id
	 */
	public String getCloudAccountId() {
		return cloudAccountId;
	}

	/**
	 * @param obj - obj to check if it equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgRegion other = (VraNgRegion) obj;
		return this.id.equals(other.getId());
	}

	/**
	 * @return the hashcode representation of the object
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(cloudAccountId)
				.toHashCode();
	}
}
