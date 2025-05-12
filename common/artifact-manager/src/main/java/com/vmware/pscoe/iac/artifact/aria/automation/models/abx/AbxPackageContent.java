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
package com.vmware.pscoe.iac.artifact.aria.automation.models.abx;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

import java.util.List;

public class AbxPackageContent extends PackageContent<AbxPackageContent.ContentType> {

	public enum ContentType implements PackageContent.ContentType {

		ACTION("action");

		private final String type;

		private ContentType(String type) {
			this.type = type;
		}

		public String getTypeValue() {
			return this.type;
		}

		public static ContentType getInstance(String type) {
			for (ContentType ct : ContentType.values()) {
				if (ct.getTypeValue().equalsIgnoreCase(type)) {
					return ct;
				}
			}
			return null;
		}

	}

	public AbxPackageContent(List<Content<ContentType>> content) {
		super(content);
	}

}
