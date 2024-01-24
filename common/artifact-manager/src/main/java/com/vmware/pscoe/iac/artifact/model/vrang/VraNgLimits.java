package com.vmware.pscoe.iac.artifact.model.vrang;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

public class VraNgLimits {

	public String getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(String cpuCount) {
		this.cpuCount = cpuCount;
	}

	public String getVmCount() {
		return vmCount;
	}

	public void setVmCount(String vmCount) {
		this.vmCount = vmCount;
	}

	public String getMemoryGB() {
		return memoryGB;
	}

	public void setMemoryGB(String memoryGB) {
		this.memoryGB = memoryGB;
	}

	public String getStorageGB() {
		return storageGB;
	}

	public void setStorageGB(String storageGB) {
		this.storageGB = storageGB;
	}

	private String cpuCount;
	private String vmCount;
	private String memoryGB;
	private String storageGB;
}
