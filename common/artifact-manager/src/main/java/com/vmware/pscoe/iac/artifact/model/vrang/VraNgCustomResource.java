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

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class represents a custom resource in vRA NG.
 */
public class VraNgCustomResource implements Identifiable {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;

	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	/**
	 * @param id the id of the custom resource
	 */
	private final String id;

	/**
	 * @param name the name of the custom resource
	 */
	private final String name;

	/**
	 * @param json the json of the custom resource
	 */
	private final String json;

	/**
	 * Constructor.
	 * 
	 * @param id   the id of the custom resource
	 * @param name the name of the custom resource
	 * @param json the json of the custom resource
	 */
	public VraNgCustomResource(String id, String name, String json) {
		this.id = id;
		this.name = name;
		this.json = json;
	}

	/**
	 * @return the id of the custom resource
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the name of the custom resource
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the json of the custom resource
	 */
	public String getJson() {
		return this.json;
	}

	/**
	 * @param obj the object to compare
	 * @return a flag if the custom resource is equal to another object
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgCustomResource other = (VraNgCustomResource) obj;
		return this.id.equals(other.getId());
	}

	/**
	 * @return the hash code of the custom resource
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(id)
				.append(name)
				.append(json)
				.toHashCode();
	}
}
