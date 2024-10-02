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
import org.apache.commons.lang3.builder.HashCodeExclude;

/**
 * Represents a VRA NG Blueprint.
 */
public class VraNgBlueprint implements Identifiable {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;
	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	/**
	 * @param id The id of the blueprint
	 */
	private String id;

	/**
	 * @param name The name of the blueprint
	 */
	private String name;

	/**
	 * @param content The content of the blueprint
	 */
	private String content;

	/**
	 * @param description The description of the blueprint
	 */
	private String description;

	/**
	 * @param requestScopeOrg Whether the blueprint should be scoped to the
	 *                        request's organization
	 */
	private Boolean requestScopeOrg;

	/**
	 * @param id              The id of the blueprint
	 * @param name            The name of the blueprint
	 * @param content         The content of the blueprint
	 * @param description     The description of the blueprint
	 * @param requestScopeOrg Whether the blueprint should be scoped to the
	 *                        request's organization
	 */
	public VraNgBlueprint(String id, String name, String content, String description, Boolean requestScopeOrg) {
		this.id = id;
		this.name = name;
		this.content = content;
		this.description = description;
		this.requestScopeOrg = requestScopeOrg;
	}

	/**
	 * @return The id of the blueprint
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param id The id of the blueprint
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The name of the blueprint
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The content of the blueprint
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content The content of the blueprint
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return The description of the blueprint
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the requestScopeOrg
	 */
	public Boolean getRequestScopeOrg() {
		return this.requestScopeOrg;
	}

	/**
	 * @return if the object is equal to another object
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
	 * Using `id` should be enough here, as `id`s in Aria are unique. Adding more
	 * details for extra uniquieness.
	 *
	 * @return the hashcode numerical repesentation of the object
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(id)
				.append(name)
				.append(content)
				.append(description)
				.append(requestScopeOrg)
				.toHashCode();
	}
}
