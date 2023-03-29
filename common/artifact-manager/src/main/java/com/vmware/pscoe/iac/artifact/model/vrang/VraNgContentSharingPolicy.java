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

/**

*/
public class VraNgContentSharingPolicy {
	private String name;
	private String typeId;
	private String enforcementType;
	private String description;
	private VraNgDefinition definition;

	public VraNgContentSharingPolicy() {
	}

	public VraNgContentSharingPolicy(String name, String typeId, String enforcementType,String description, VraNgDefinition definition ) {
		this.name= name;
		this.typeId= typeId;
		this.enforcementType= enforcementType;
		this.description= description;
		this.definition= definition;
	}

	/**
	 * Get the name of the content sharing policy.
	 * 
	 * @return content sharing policy name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the content sharing policy.
	 * 
	 * @param name - name of the content sharing policy
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the typeId of the content sharing policy.
	 * 
	 * @return content sharing policy typeId
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * Set the typeId of the content sharing policy.
	 * 
	 * @param typeId - typeId of the content sharing policy
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	/**
	 * Get the enforcementType of the content sharing policy.
	 * 
	 * @return content sharing policy enforcementType
	 */
	public String getEnforcementType() {
		return enforcementType;
	}

	/**
	 * Set the enforcementType of the content sharing policy.
	 * 
	 * @param enforcementType - enforcementType of the content sharing policy
	 */
	public void setEnforcementType(String enforcementType) {
		this.enforcementType = enforcementType;
	}

	/**
	 * Get the description of the content sharing policy.
	 * 
	 * @return content sharing policy description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the content sharing policy.
	 * 
	 * @param description - description of the content sharing policy
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the definition of the content sharing policy.
	 * 
	 * @return content sharing policy definition
	 */
	public VraNgDefinition getDefinition() {
		return definition;
	}

	/**
	 * Set the definition of the content sharing policy.
	 * 
	 * @param definition - definition of the content sharing policy
	 */
	public void setDefinition(VraNgDefinition definition) {
		this.definition = definition;
	}
}
