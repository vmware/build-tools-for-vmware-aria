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

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a VRA NG Subscription.
 */
public class VraNgSubscription implements Identifiable {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;

	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	/**
	 * @param id The ID of the subscription.
	 */
	private final String id;

	/**
	 * @param name The name of the subscription.
	 */
	private final String name;

	/**
	 * @param json The JSON representation of the subscription.
	 */
	private final String json;

	/**
	 * @param id   The ID of the subscription.
	 * @param name The name of the subscription.
	 * @param json The JSON representation of the subscription.
	 */
	public VraNgSubscription(String id, String name, String json) {
		this.id = id;
		this.name = name;
		this.json = json;
	}

	/**
	 * @return The ID of the subscription.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return The name of the subscription.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The JSON representation of the subscription.
	 */
	public String getJson() {
		return this.json;
	}

	/**
	 * Checks if the subscription is equal to another object.
	 *
	 * @param obj The object to compare.
	 * @return True if the subscription is equal to the object, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgBlueprint other = (VraNgBlueprint) obj;
		return this.id.equals(other.getId());
	}

	/**
	 * @return the hashCode representation of the object
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
