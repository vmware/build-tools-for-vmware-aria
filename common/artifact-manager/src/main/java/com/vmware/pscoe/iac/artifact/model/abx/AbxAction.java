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
package com.vmware.pscoe.iac.artifact.model.abx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class AbxAction {

	public String name;
	public String description;              // optional
	public String version;                  // optional
	public Platform platform;               // optional
	public AbxDefinition abx;
	public String id;                       // optional

	public String getName() {
		return (this.platform != null && this.platform.action != null) ? this.platform.action : this.name;
	}

	// The bundle is not part of the ABX package.json, however it is
	// useful to be stored as file reference for enhanced usage.

	public File bundle;

	public void setBundle(File bundle) {
		this.bundle = bundle;
	}

	public String getBundleAsB64() throws IOException {
		byte[] content = Files.readAllBytes(bundle.toPath());
		return Base64.getEncoder().encodeToString(content);
	}
}
