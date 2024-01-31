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

	public VranNgJsonValue getCpu() {
		return cpu;
	}

	public void setCpu(VranNgJsonValue cpu) {
		this.cpu = cpu;
	}

	public VranNgJsonValue getInstances() {
		return instances;
	}

	public void setInstances(VranNgJsonValue instances) {
		this.instances = instances;
	}

	public VraNgJsonValueWithUnit getMemory() {
		return memory;
	}

	public void setMemory(VraNgJsonValueWithUnit memory) {
		this.memory = memory;
	}

	public VraNgJsonValueWithUnit getStorage() {
		return storage;
	}

	public void setStorage(VraNgJsonValueWithUnit storage) {
		this.storage = storage;
	}

	private VraNgLimits ()
	{

	}

	public VraNgLimits(String cpu, String instances, String memory, String storage){
		this.cpu = new VranNgJsonValue(cpu);
		this.instances = new VranNgJsonValue(instances);
		this.memory = new VraNgJsonValueWithUnit(memory);
		this.storage = new VraNgJsonValueWithUnit(storage);
	}
	private VranNgJsonValue cpu;
	private VranNgJsonValue instances;
	private VraNgJsonValueWithUnit memory;
	private VraNgJsonValueWithUnit storage;
}
