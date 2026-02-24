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
package com.vmware.pscoe.iac.artifact.aria.automation.store.models;

import java.util.List;

import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent;

/**
 * This class extends the PackageContent class and provides a list of content
 * types specific to VraNg.
 */
public class VraNgPackageContent extends PackageContent<VraNgPackageContent.ContentType> {

	/**
	 * This enum represents the various content types that a VraNgPackage can have.
	 */
	public enum ContentType implements PackageContent.ContentType {
		/**
		 * Content type for blueprints.
		 */
		BLUEPRINT("blueprint"),
		/**
		 * Content type for subscriptions.
		 */
		SUBSCRIPTION("subscription"),
		/**
		 * Content type for catalog entitlement.
		 */
		CATALOG_ENTITLEMENT("catalog-entitlement"),
		/**
		 * Content type for custom resources.
		 */
		CUSTOM_RESOURCE("custom-resource"),
		/**
		 * Content type for resource actions.
		 */
		RESOURCE_ACTION("resource-action"),
		/**
		 * Content type for property groups.
		 */
		PROPERTY_GROUP("property-group"),
		/**
		 * Content type for content sources.
		 */
		CONTENT_SOURCE("content-source"),
		/**
		 * Content type for catalog items.
		 */
		CATALOG_ITEM("catalog-item"),
		/**
		 * Content type for content sharing policies.
		 */
		CONTENT_SHARING_POLICY("content-sharing"),
		/**
		 * Content type for lease policies.
		 */
		LEASE_POLICY("lease"),

		/**
		 * Content type for resource quota policies.
		 */
		RESOURCE_QUOTA_POLICY("resource-quota"),

		/**
		 * Content type for day 2 actions policies.
		 */
		DAY2_ACTIONS_POLICY("day2-actions"),

		/**
		 * Content type for deployment limit policies.
		 */
		DEPLOYMENT_LIMIT_POLICY("deployment-limit"),

		/**
		 * Content type for approval policies.
		 */
		APPROVAL_POLICY("approval"),

		/**
		 * Content type for scenarios.
		 */
		SCENARIO("scenario");

		/**
		 * PackageContent type.
		 */
		private final String type;

		/**
		 * Constructor for ContentType.
		 *
		 * @param type the type of the content.
		 */
		ContentType(String type) {
			this.type = type;
		}

		/**
		 * Gets the type value.
		 *
		 * @return the type value.
		 */
		public String getTypeValue() {
			return this.type;
		}

		/**
		 * Returns the ContentType instance corresponding to the given type.
		 *
		 * @param type the type of the content.
		 * @return the ContentType instance.
		 */
		public static ContentType getInstance(String type) {
			for (ContentType ct : ContentType.values()) {
				if (ct.getTypeValue().equalsIgnoreCase(type)) {
					return ct;
				}
			}
			return null;
		}

	}

	/**
	 * Constructor for VraNgPackageContent.
	 *
	 * @param content the content of the package.
	 */
	public VraNgPackageContent(List<Content<ContentType>> content) {
		super(content);
	}

}
