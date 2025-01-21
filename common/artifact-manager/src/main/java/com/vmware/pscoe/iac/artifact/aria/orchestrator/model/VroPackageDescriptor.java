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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.model;

import java.io.File;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

import org.apache.commons.lang3.NotImplementedException;

public class VroPackageDescriptor extends PackageDescriptor {

	private List<String> workflow;
	private List<String> action;
	private List<String> configurationElement;
	private List<String> resourceElement;

	public List<String> getWorkflow() {
		return workflow;
	}

	public void setWorkflow(List<String> workflow) {
		this.workflow = workflow;
	}

	public List<String> getAction() {
		return action;
	}

	public void setAction(List<String> action) {
		this.action = action;
	}

	public List<String> getConfigurationElement() {
		return configurationElement;
	}

	public void setConfigurationElement(List<String> configurationElement) {
		this.configurationElement = configurationElement;
	}

	public List<String> getResourceElement() {
		return resourceElement;
	}

	public void setResourceElement(List<String> resourceElement) {
		this.resourceElement = resourceElement;
	}

	public List<String> getMembersForType() {
		throw new NotImplementedException("To be implemented");
	}

	public static VroPackageDescriptor getInstance(File filesystemPath) {
		throw new NotImplementedException("To be implemented");
	}

	public static VroPackageDescriptor getInstance() {
		throw new NotImplementedException("To be implemented");
	}

}
