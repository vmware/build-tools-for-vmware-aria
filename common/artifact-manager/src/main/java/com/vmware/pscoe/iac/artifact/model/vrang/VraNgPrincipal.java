package com.vmware.pscoe.iac.artifact.model.vrang;

/*-
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

public class VraNgPrincipal {
	/**
	 * principal type.
	 */
	private String type;
	/**
	 * principal reference object id.
	 */
	private String referenceId;

	/**
	 * Default constructor.
	 */
	public VraNgPrincipal() {
	}

	/**
	 * Constructor.
	 * 
	 * @param type        type of the principal.
	 * @param referenceId reference object id of the principal.
	 */
	public VraNgPrincipal(String type, String referenceId) {
		this.type = type;
		this.referenceId = referenceId;
	}

	/**
	 * Get the type of the principal.
	 * 
	 * @return item type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the principal.
	 * 
	 * @param type item type.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the type reference object id for the principal.
	 * 
	 * @return item reference object id.
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * Set the reference id of the principal.
	 * 
	 * @param referenceId item reference object id.
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
}
