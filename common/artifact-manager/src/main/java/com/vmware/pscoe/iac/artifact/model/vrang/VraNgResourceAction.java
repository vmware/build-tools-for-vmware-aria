/**
 * Package.
 */
package com.vmware.pscoe.iac.artifact.model.vrang;

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

public class VraNgResourceAction {

	/**
	 * Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;
	/**
	 * Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;
	/**
	 * Resource Action ID.
	 */
	private final String id;
	/**
	 * Resource Action Name.
	 */
	private final String name;
	/**
	 * Resource Action JSON.
	 */
	private final String json;
	/**
	 * Resource Action Resource Type.
	 */
	private final String resourceType;

	/**
	 * Creates a new Resource Action.
	 * 
	 * @param identifier   Resource Action id
	 * @param recourseName Resource Action name
	 * @param jsonString   Resrouce Action json
	 */
	public VraNgResourceAction(final String identifier, final String recourseName, final String jsonString) {
		this.id = identifier;
		this.name = recourseName;
		this.json = jsonString;
		this.resourceType = null;
	}

	/**
	 * Creates a new Resource Action.
	 * 
	 * @param identifier           Resource Action id
	 * @param actionName         Resource Action name
	 * @param jsonString         Resource Action json
	 * @param actionResourceType Resource Action Resource Type
	 */
	public VraNgResourceAction(final String identifier, final String actionName, final String jsonString, final String actionResourceType) {
		this.id = identifier;
		this.name = actionName;
		this.json = jsonString;
		this.resourceType = actionResourceType;
	}

	/**
	 * Gets the id of the Resource Action.
	 * 
	 * @return Resource Action ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the Name of the Resource Action.
	 * 
	 * @return Resource Action Name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the JSON String of the Resource Action.
	 * 
	 * @return Resource Action JSON String
	 */
	public String getJson() {
		return this.json;
	}

	/**
	 * Gets the Resource Type of the Resource Action.
	 * 
	 * @return Resource Action Resource Type
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * Equals Implementation.
	 * 
	 * @return true if equal else false
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		VraNgResourceAction other = (VraNgResourceAction) obj;
		return this.id.equals(other.getId());
	}

	/**
	 * Hash Implementation.
	 * 
	 * @return object hash
	 */
	@Override
	public int hashCode() {
		int result = PRIME_NUMBER_17;
		return PRIME_NUMBER_31 * result + this.id.hashCode();
	}

}
