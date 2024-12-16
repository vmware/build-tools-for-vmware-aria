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
package com.vmware.pscoe.iac.artifact.aria.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgContentSource;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.model.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VrangContentSourceUtils {
	private static final int CONTENT_SOURCE_SYNC_WAIT_TIME = 1000;
	private final Logger logger = LoggerFactory.getLogger(VrangContentSourceUtils.class);

	protected RestClientVraNg restClient;
	protected Package vraNgPackage;

	public VrangContentSourceUtils(RestClientVraNg restClient, Package vraNgPackage) {
		this.restClient = restClient;
		this.vraNgPackage = vraNgPackage;
	}

	public String syncContentSource(VraNgContentSourceBase contentSource, Integer timeout) {
		String id = this.restClient.createOrUpdateContentSource(contentSource);
		contentSource.setId(id);
		try {
			this.waitForContentSourceSync(contentSource, contentSource.getName(), timeout);
		} catch (RuntimeException e) {
			throw new RuntimeException(String.format("Synchronization of content source %s failed, error %s", id, e.getMessage()), e);
		}
		return id;
	}

	private void waitForContentSourceSync(VraNgContentSourceBase contentSource, String name, Integer timeout) {
		String contentSourceId = contentSource.getId();
		long finish = System.currentTimeMillis() + timeout;
		boolean isContentSourceSynced = this.isContentSourceSynced(contentSourceId);
		try {
			logger.info("Waiting (max {} ms) content source '{}' ('{}') to be synchronized on target system", timeout, name, contentSourceId);
			while (!isContentSourceSynced && System.currentTimeMillis() < finish) {
				Thread.sleep(CONTENT_SOURCE_SYNC_WAIT_TIME);
				isContentSourceSynced = this.isContentSourceSynced(contentSourceId);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isContentSourceSynced(String contentSourceId) {
		VraNgContentSourceBase contentSource = this.restClient.getContentSource(contentSourceId);
		if (contentSource.getLastImportErrors() != null && !contentSource.getLastImportErrors().isEmpty()) {
			String message = String.format("Unable to synchronize content source '%s' due to '%s'", contentSourceId, contentSource.getLastImportErrors().toString());
			throw new RuntimeException(message);
		}

		Integer foundItems = contentSource.getItemsFound();
		Integer importedItems = contentSource.getItemsImported();
		Boolean syncCompleted = foundItems != 0 && importedItems != 0 && foundItems.equals(importedItems);
		// Blueprint content sources require another call to update their items details for some reason...
		// In any case - we're triggering an update on all types
		if (!syncCompleted && contentSource.getType() == VraNgContentSourceType.BLUEPRINT) {
			this.restClient.createOrUpdateContentSource(contentSource);
		}
		return syncCompleted;
	}

	public File storeContentSourceOnFilesystem(VraNgContentSourceBase contentSource) {
		File store = new File(this.vraNgPackage.getFilesystemPath());
		File contentSourceFile = Paths.get(store.getPath(), VraNgDirs.DIR_CONTENT_SOURCES, contentSource.getName() + ".json").toFile();
		contentSourceFile.getParentFile().mkdirs();

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			String contentSourceJson = gson.toJson(contentSource, contentSource.getType().getTypeClass());
			logger.info("Created file {}", Files.write(Paths.get(contentSourceFile.getPath()), contentSourceJson.getBytes(), StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to store workflow content source {} {}", contentSource.getName(), contentSourceFile.getPath());
			throw new RuntimeException("Unable to store workflow content source.", e);
		}

		return contentSourceFile;
	}

	public Boolean isForSameOrNoneProject(VraNgContentSourceBase contentSource, String projectId) {
		if (contentSource instanceof VraNgContentSource) {
			VraNgContentSource contentSourceProject = (VraNgContentSource) contentSource;
			return projectId.equals(contentSourceProject.getProjectId())
					|| projectId.equals(contentSourceProject.getConfig().get("sourceProjectId"));
		}

		return true;
	}
}
