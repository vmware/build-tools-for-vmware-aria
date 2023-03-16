package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.ArrayList;
import java.util.List;

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

import org.apache.commons.lang3.NotImplementedException;

public class VraNgPolicy {

	private final List<String> contentSharing;

	public VraNgPolicy() {
		super();
		this.contentSharing = new ArrayList<>();
	}

	public VraNgPolicy(List<String> contentSharing) {
		this.contentSharing = contentSharing;
	}

	public List<String> getContentSharing() {
		return this.contentSharing;
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

}
