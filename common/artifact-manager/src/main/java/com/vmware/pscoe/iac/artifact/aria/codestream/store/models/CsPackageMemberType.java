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
package com.vmware.pscoe.iac.artifact.aria.codestream.store.models;

public enum CsPackageMemberType {
	PIPELINE("pipeline"),
	VARIABLE("variable"),
	ENDPOINT("endpoint"),
	CUSTOM_INTEGRATION("custom-integration"),
	GIT_WEBHOOK("git-webhook"),
	DOCKER_WEBHOOK("docker-webhook"),
	GERRIT_TRIGGER("gerrit-trigger"),
	GERRIT_LISTENER("gerrit-listener");

	private final String name;
	private final boolean isNativeContent;

	CsPackageMemberType(String name) {
		this(name, true);
	}

	CsPackageMemberType(String name, boolean isNativeContent) {
		this.name = name;
		this.isNativeContent = isNativeContent;
	}

	public boolean isNativeContent() {
		return this.isNativeContent;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static CsPackageMemberType fromString(String name) {
		for (CsPackageMemberType type : CsPackageMemberType.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
