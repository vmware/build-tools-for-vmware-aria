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
package com.vmware.pscoe.iac.artifact.model.vra;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VraPackageContent extends PackageContent<VraPackageContent.ContentType> {

	public enum ContentType implements PackageContent.ContentType { 
		XAAS_BLUEPRINT("xaas-blueprint"),
		XAAS_RESOURCE_TYPE("xaas-resource-type"),
		XAAS_RESOURCE_ACTION("xaas-resource-action"),
		XAAS_RESOURCE_MAPPING("xaas-resource-mapping"),
		COMPOSITE_BLUEPRINT("composite-blueprint"),
		SOFTWARE_COMPONENT("software-component"),
		PROPERTY_GROUP("property-group"),
		PROPERTY_DICTIONARY("property-definition"),
		WORKFLOW_SUBSCRIPTION("workflow-subscription"),
		GLOBAL_PROPERTY_DEFINITION("global-property-definition"),
		GLOBAL_PROPERTY_GROUP("global-property-group");

		private final String type;

		private ContentType(String type) {
			this.type = type;
		}

		public String getTypeValue(){
			return this.type;
		}

		public static ContentType getInstance(String type) {
			for(ContentType ct: ContentType.values()) {
				if(ct.getTypeValue().equalsIgnoreCase(type)) {
					return ct;
				}
			}
			return null;
		}

	}

	public VraPackageContent(List<Content<ContentType>> content) {
		super(content);
	}

}
