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
package com.vmware.pscoe.iac.artifact.model.vcd;

import java.io.File;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

import org.apache.commons.lang3.NotImplementedException;

public class VcdPackageDescriptor extends PackageDescriptor {

	private List<String> content;

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public List<String> getMembersForType() {
		throw new NotImplementedException("To be implemented");
	}

	public static VcdPackageDescriptor getInstance(File filesystemPath) {
		throw new NotImplementedException("To be implemented");
	}

	public static VcdPackageDescriptor getInstance() {
		throw new NotImplementedException("To be implemented");
	}

}
