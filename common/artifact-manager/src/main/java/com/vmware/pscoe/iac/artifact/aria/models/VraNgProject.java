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
package com.vmware.pscoe.iac.artifact.aria.model;

/**
 * Represents a VraNgProject.
 */
public class VraNgProject implements Identifiable {
	/**
	 * @param id the project id
	 */
	private final String id;
	/**
	 * @param name the project name
	 */
	private final String name;

	/**
	 * @param id   the project id
	 * @param name the project name
	 */
	public VraNgProject(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the project id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the project name
	 */
	public String getName() {
		return this.name;
	}
}
