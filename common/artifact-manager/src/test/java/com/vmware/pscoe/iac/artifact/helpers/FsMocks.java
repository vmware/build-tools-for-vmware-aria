
package com.vmware.pscoe.iac.artifact.helpers;

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
import com.vmware.pscoe.iac.artifact.helpers.filesystem.ApprovalPolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.BlueprintFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.CatalogItemFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.ContentSharingPolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.CustomResourceFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.Day2ActionsPolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.DeploymentLimitPolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.EntitlementFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.LeasePolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.PropertyGroupFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.ResourceActionFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.ResourceQuotaPolicyFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.SubscriptionFsMocks;

import java.io.File;

/**
 * Class responsible for accepting a temp directory and adding mock files to it.
 */
public class FsMocks {
	/**
	 * Temporary folder for storing mock test data.
	 */
	private final File tempDir;
	/**
	 * Temporary folder for storing mock test data for blueprintFsMocks.
	 */
	private BlueprintFsMocks blueprintFsMocks;
	/**
	 * Temporary folder for storing mock test data for contentSharingPolicyFsMocks.
	 */
	private ContentSharingPolicyFsMocks contentSharingPolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for resourceQuotaPolicyFsMocks.
	 */
	private ResourceQuotaPolicyFsMocks resourceQuotaPolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for day2ActionsPolicyFsMocks.
	 */
	private Day2ActionsPolicyFsMocks day2ActionsPolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for approvalPolicyFsMocks.
	 */
	private ApprovalPolicyFsMocks approvalPolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for leasePolicyFsMocks.
	 */
	private LeasePolicyFsMocks leasePolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for deploymentLimitPolicyFsMocks.
	 */
	private DeploymentLimitPolicyFsMocks deploymentLimitPolicyFsMocks;
	/**
	 * Temporary folder for storing mock test data for propertyGroupFsMocks.
	 */
	private PropertyGroupFsMocks propertyGroupFsMocks;
	/**
	 * Temporary folder for storing mock test data for catalogItemFsMocks.
	 */
	private CatalogItemFsMocks catalogItemFsMocks;
	/**
	 * Temporary folder for storing mock test data for customResourceFsMocks.
	 */
	private CustomResourceFsMocks customResourceFsMocks;
	/**
	 * Temporary folder for storing mock test data for entitlementStore.
	 */
	private EntitlementFsMocks entitlementStore;
	/**
	 * Temporary folder for storing mock test data for resourceAction.
	 */
	private ResourceActionFsMocks resourceAction;
	/**
	 * Temporary folder for storing mock test data for subscription.
	 */
	private SubscriptionFsMocks subscription;

	/**
	 * Constructor.
	 *
	 * @param tempDir the folder to store mock data.
	 */
	public FsMocks(File tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * Getter for blueprintFsMocks.
	 *
	 * @return blueprintFsMocks
	 */
	public BlueprintFsMocks blueprintFsMocks() {
		if (this.blueprintFsMocks == null) {
			this.blueprintFsMocks = new BlueprintFsMocks(this.tempDir);
		}

		return this.blueprintFsMocks;
	}

	/**
	 * Getter for contentSharingFsMocks.
	 *
	 * @return contentSharingFsMocks
	 */
	public ContentSharingPolicyFsMocks contentSharingFsMocks() {
		if (this.contentSharingPolicyFsMocks == null) {
			this.contentSharingPolicyFsMocks = new ContentSharingPolicyFsMocks(this.tempDir);
		}

		return this.contentSharingPolicyFsMocks;
	}

	/**
	 * Getter for resourceQuotaPolicyFsMocks.
	 *
	 * @return resourceQuotaPolicyFsMocks
	 */
	public ResourceQuotaPolicyFsMocks resourceQuotaPolicyFsMocks() {
		if (this.resourceQuotaPolicyFsMocks == null) {
			this.resourceQuotaPolicyFsMocks = new ResourceQuotaPolicyFsMocks(this.tempDir);
		}

		return this.resourceQuotaPolicyFsMocks;
	}

	/**
	 * Getter for day2ActionsPolicyFsMocks.
	 *
	 * @return day2ActionsPolicyFsMocks
	 */
	public Day2ActionsPolicyFsMocks getDay2ActionsPolicyFsMocks() {
		if (this.day2ActionsPolicyFsMocks == null) {
			this.day2ActionsPolicyFsMocks = new Day2ActionsPolicyFsMocks(this.tempDir);
		}

		return day2ActionsPolicyFsMocks;
	}

	/**
	 * Getter for deploymentLimitPolicyFsMocks.
	 *
	 * @return deploymentLimitPolicyFsMocks
	 */
	public DeploymentLimitPolicyFsMocks getDeploymentLimitPolicyFsMocks() {
		if (this.deploymentLimitPolicyFsMocks == null) {
			this.deploymentLimitPolicyFsMocks = new DeploymentLimitPolicyFsMocks(this.tempDir);
		}

		return deploymentLimitPolicyFsMocks;
	}

	/**
	 * Getter for approvalPolicyFsMocks.
	 *
	 * @return approvalPolicyFsMocks
	 */
	public ApprovalPolicyFsMocks getApprovalPolicyFsMocks() {
		if (this.approvalPolicyFsMocks == null) {
			this.approvalPolicyFsMocks = new ApprovalPolicyFsMocks(this.tempDir);
		}

		return approvalPolicyFsMocks;
	}

	/**
	 * Getter for leasePolicyFsMocks.
	 *
	 * @return leasePolicyFsMocks
	 */
	public LeasePolicyFsMocks getLeasePolicyFsMocks() {
		if (this.leasePolicyFsMocks == null) {
			this.leasePolicyFsMocks = new LeasePolicyFsMocks(this.tempDir);
		}

		return leasePolicyFsMocks;
	}

	/**
	 * Getter for subscription.
	 *
	 * @return subscription
	 */
	public SubscriptionFsMocks subscriptionFsMocks() {
		if (this.subscription == null) {
			this.subscription = new SubscriptionFsMocks(this.tempDir);
		}

		return this.subscription;
	}

	/**
	 * Getter for resourceAction.
	 *
	 * @return resourceAction
	 */
	public ResourceActionFsMocks resourceActionFsMocks() {
		if (this.resourceAction == null) {
			this.resourceAction = new ResourceActionFsMocks(this.tempDir);
		}

		return this.resourceAction;
	}

	/**
	 * Getter for entitlementStore.
	 *
	 * @return entitlementStore
	 */
	public EntitlementFsMocks entitlementStore() {
		if (this.entitlementStore == null) {
			this.entitlementStore = new EntitlementFsMocks(this.tempDir);
		}

		return this.entitlementStore;
	}

	/**
	 * Getter for propertyGroupFsMocks.
	 *
	 * @return propertyGroupFsMocks
	 */
	public PropertyGroupFsMocks propertyGroupFsMocks() {
		if (this.propertyGroupFsMocks == null) {
			this.propertyGroupFsMocks = new PropertyGroupFsMocks(this.tempDir);
		}

		return this.propertyGroupFsMocks;
	}

	/**
	 * Getter for catalogItemFsMocks.
	 *
	 * @return catalogItemFsMocks
	 */
	public CatalogItemFsMocks catalogItemFsMocks() {
		if (this.catalogItemFsMocks == null) {
			this.catalogItemFsMocks = new CatalogItemFsMocks(this.tempDir);
		}

		return this.catalogItemFsMocks;
	}

	/**
	 * Getter for customResourceFsMocks.
	 *
	 * @return customResourceFsMocks
	 */
	public CustomResourceFsMocks customResourceFsMocks() {
		if (this.customResourceFsMocks == null) {
			this.customResourceFsMocks = new CustomResourceFsMocks(this.tempDir);
		}

		return this.customResourceFsMocks;
	}

	/**
	 * Finds a specific item in the given folder.
	 * Throws if missing
	 *
	 * @param folder   -> the folder to search in
	 * @param itemName - The item name to search for
	 * @return File
	 */
	public File findItemByNameInFolder(File folder, String itemName) {
		for (File file : folder.listFiles()) {
			if (file.getName().equalsIgnoreCase(itemName)) {
				return file;
			}
		}

		throw new RuntimeException(String.format("Item %s not found", itemName));
	}

	/**
	 * Gets the temp folder's project path.
	 *
	 * @return File
	 */
	public File getTempFolderProjectPath() {
		File[] files = this.tempDir.listFiles();

		if (files == null || files.length == 0) {
			throw new RuntimeException("Could not find temp folder project path");
		}

		return files[0];
	}
}
