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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * VraNgCloudAccount is the Aria Automation > Infrastructire > Cloud Account
 * representation.
 */
public class VraNgCloudAccount {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;

	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;
	private final String id;
	private final String name;
	private final String type;
	private final List<String> regionIds;
	private final List<String> tags;

	/**
	 * @param id        - the id of the CA
	 * @param name      - the name of the CA
	 * @param type      - the type of the CA
	 * @param regionIds - all the region IDs for the cloud account
	 */
	public VraNgCloudAccount(String id, String name, String type, List<String> regionIds, List<String> tags) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.regionIds = regionIds;
		this.tags = tags;
	}

	/**
	 * @return the id of the cloud account
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the name of the CA
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the type of the CA
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return the region ids associated with the CA
	 */
	public List<String> getRegionIds() {
		return Collections.unmodifiableList(this.regionIds);
	}

	/**
	 * @return the tags associated with the CA
	 */
	public List<String> getTags() {
		return Collections.unmodifiableList(this.tags);
	}

	/**
	 * @param obj - object to check if it equals to this one
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgCloudAccount other = (VraNgCloudAccount) obj;
		return this.id.equals(other.getId());
	}

	/**
	 * @return the hash code of the object
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(name)
				.append(type)
				.toHashCode();
	}

}
