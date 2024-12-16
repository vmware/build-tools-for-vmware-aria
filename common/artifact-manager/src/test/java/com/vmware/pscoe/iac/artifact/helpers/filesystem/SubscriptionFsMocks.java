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
package com.vmware.pscoe.iac.artifact.helpers.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgSubscription;

import static com.vmware.pscoe.iac.artifact.aria.store.VraNgDirs.DIR_SUBSCRIPTIONS;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;

public class SubscriptionFsMocks extends VraNgFsMock {
	/**
	 * SubscriptionFsMocks constructor.
	 * @param tempDir temporary directory.
	 */
	public SubscriptionFsMocks(File tempDir) {
		super(tempDir);
	}
	/**
	 * Returns working directory.
	 */
	@Override
	public File getWorkdir() {
		return Paths.get(this.tempDir.getPath(), DIR_SUBSCRIPTIONS).toFile();
	}

	/**
	 * Save subscription file to temporary directory.
	 * @param subscription subscription object
	 */
	public void addSubscription(VraNgSubscription subscription) {
		File file = Paths.get(
			this.getWorkdir().getAbsolutePath(),
			subscription.getName() + ".json"
		).toFile();

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName = Paths.get(file.getPath());
		writeFileToPath(itemName, subscription.getJson().getBytes());
	}
}
