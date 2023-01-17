package com.vmware.pscoe.iac.artifact.helpers;

import com.vmware.pscoe.iac.artifact.helpers.filesystem.BlueprintFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.CatalogItemFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.CustomResourceFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.EntitlementFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.PropertyGroupFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.ResourceActionFsMocks;
import com.vmware.pscoe.iac.artifact.helpers.filesystem.SubscriptionFsMocks;

import java.io.File;

/**
 * Class responsible for accepting a temp directory and adding mock files to it
 */
public class FsMocks {
	private final File tempDir;
	private BlueprintFsMocks blueprintFsMocks;
	private PropertyGroupFsMocks propertyGroupFsMocks;
	private CatalogItemFsMocks catalogItemFsMocks;
	private CustomResourceFsMocks customResourceFsMocks;
	private EntitlementFsMocks entitlementStore;
	private ResourceActionFsMocks resourceAction;
	private SubscriptionFsMocks subscription;

	public FsMocks(File tempDir) {
		this.tempDir = tempDir;
	}

	public BlueprintFsMocks blueprintFsMocks() {
		if (this.blueprintFsMocks == null) {
			this.blueprintFsMocks = new BlueprintFsMocks(this.tempDir);
		}

		return this.blueprintFsMocks;
	}

	public SubscriptionFsMocks subscriptionFsMocks() {
		if (this.subscription == null) {
			this.subscription = new SubscriptionFsMocks(this.tempDir);
		}

		return this.subscription;
	}

	public ResourceActionFsMocks resourceActionFsMocks() {
		if (this.resourceAction == null) {
			this.resourceAction = new ResourceActionFsMocks(this.tempDir);
		}

		return this.resourceAction;
	}

	public EntitlementFsMocks entitlementStore() {
		if (this.entitlementStore == null) {
			this.entitlementStore = new EntitlementFsMocks(this.tempDir);
		}

		return this.entitlementStore;
	}

	public PropertyGroupFsMocks propertyGroupFsMocks() {
		if (this.propertyGroupFsMocks == null) {
			this.propertyGroupFsMocks = new PropertyGroupFsMocks(this.tempDir);
		}

		return this.propertyGroupFsMocks;
	}

	public CatalogItemFsMocks catalogItemFsMocks() {
		if (this.catalogItemFsMocks == null) {
			this.catalogItemFsMocks = new CatalogItemFsMocks(this.tempDir);
		}

		return this.catalogItemFsMocks;
	}

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
	 * @param    folder -> the folder to search in
	 * @param    itemName - The item name to search for
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
	 * Gets the temp folder's project path
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
