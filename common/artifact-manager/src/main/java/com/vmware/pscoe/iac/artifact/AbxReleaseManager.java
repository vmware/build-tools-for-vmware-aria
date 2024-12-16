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
package com.vmware.pscoe.iac.artifact;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxActionVersion;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.rest.RestClientVraNg;

public class AbxReleaseManager {
	private final Logger logger = LoggerFactory.getLogger(AbxReleaseManager.class);
	private RestClientVraNg restClient;
	private static final int NEXT_VER_GROUP_1 = 1;
	private static final int NEXT_VER_GROUP_2 = 2;
	private static final int NEXT_VER_GROUP_3 = 3;

	/**
	 * Constructor.
	 * 
	 * @param restClient rest client to use for releasing.
	 */
	public AbxReleaseManager(RestClientVraNg restClient) {
		this.restClient = restClient;
	}

	/**
	 * Release content of an action based on version.
	 * 
	 * @param version version to release (if set to 'auto', version will be incremented automatically).
	 * @param baseDir base directory to use.
	 */
	public void releaseContent(String version, File baseDir) {
		logger.info("Creating package descriptor from: {}", baseDir.getAbsolutePath());
		AbxPackageDescriptor abxDescriptor = AbxPackageDescriptor.getInstance(baseDir);

		// Get existing actions from server
		List<AbxAction> abxActionsOnServer = this.restClient.getAllAbxActions();
		Map<String, AbxAction> abxActionsOnServerByName = abxActionsOnServer.stream()
				.collect(Collectors.toMap(AbxAction::getName, item -> item));

		AbxAction existingAction = abxActionsOnServerByName.get(abxDescriptor.getAction().getName());
		if (existingAction == null) {
			logger.error("Action {} does not exist on server. Cannot release!", abxDescriptor.getAction().getName());
			return;
		}

		// determine release version
		if (version.equals("auto")) {
			releaseNextVersion(existingAction);
		} else if (version.equals("project")) {
			releaseVersion(existingAction, abxDescriptor.getAction().getVersion());
		} else {
			releaseVersion(existingAction, version);
		}
	}

	/**
	 * Attempt to generate a next version and release it.
	 * 
	 * @param actionOnServer ABX action
	 */
	protected void releaseNextVersion(AbxAction actionOnServer) {
		AbxActionVersion latestVersion = this.restClient.getAbxLastUpdatedVersion(actionOnServer);
		String nextVersion;
		if (latestVersion != null) {
			logger.debug("Latest version: {}", latestVersion.getName());
			logger.debug("Latest version id: {}", latestVersion.getId());
			nextVersion = this.getNextVersion(latestVersion.getName());
		} else {
			logger.info("No previous version found. Creating initial version");
			nextVersion = this.getNextVersion(null);
		}

		logger.debug("Next version of action {}: {}", actionOnServer.getName(), nextVersion);
		this.releaseVersion(actionOnServer, nextVersion);
	}

	/**
	 * Release a new version of the abx action.
	 * 
	 * @param actionOnServer ABX action
	 * @param version        new version
	 */
	protected void releaseVersion(AbxAction actionOnServer, String version) {
		logger.info("Creating abx action version {}", version);
		AbxActionVersion newVersion = this.restClient.createAbxVersion(actionOnServer, version);

		logger.info("Releasing abx action version {}", newVersion.getName());
		this.restClient.releaseAbxVersion(actionOnServer, newVersion.getId());

		logger.info("Version successfully released");
	}

	/**
	 * Generate next version based on the previous version format. Supported version
	 * formats are: * MAJOR * MAJOR.MINOR * MAJOR.MINOR.PATCH A datetime-based
	 * version will be returned if the previous version format does not match any of
	 * the supported formats.
	 * 
	 * @param version previous version
	 * @return next version
	 */
	private String getNextVersion(String version) {
		if (version == null) {
			// create a version based on the date and time
			return getDateVersion();
		}

		Matcher major = Pattern.compile("([0-9]+)").matcher(version);
		Matcher majorMinor = Pattern.compile("([0-9]+)\\.([0-9]+)").matcher(version);
		Matcher majorMinorPatch = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)").matcher(version);
		if (majorMinorPatch.matches()) {
			logger.debug("Detected version pattern MAJOR.MINOR.PATCH from {} with incrementable segment '{}'", version,
					majorMinorPatch.group(NEXT_VER_GROUP_3));
			// increment the patch segment
			return majorMinorPatch.group(NEXT_VER_GROUP_1) + "." + majorMinorPatch.group(NEXT_VER_GROUP_2) + "."
					+ (Integer.parseInt(majorMinorPatch.group(NEXT_VER_GROUP_3)) + 1);
		} else if (majorMinor.matches()) {
			logger.debug("Detected version pattern MAJOR.MINOR from '{}' with incrementable segment '{}'", version,
					majorMinor.group(NEXT_VER_GROUP_2));
			// increment the minor segment
			return majorMinor.group(NEXT_VER_GROUP_1) + "." + (Integer.parseInt(majorMinor.group(NEXT_VER_GROUP_2)) + 1);
		} else if (major.matches()) {
			logger.debug("Detected version pattern MAJOR from '{}' with incrementable segment '{}'", version,
					major.group(1));
			// increment the major segment
			return Integer.toString(Integer.parseInt(major.group(NEXT_VER_GROUP_1)) + 1);
		} else {
			logger.debug("Could not determine version pattern from {}", version);
			return getDateVersion();
		}
	}

	/**
	 * Create a version based on the current date and time.
	 * 
	 * @return datetime-based version
	 */
	private String getDateVersion() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return dateFormat.format(date);
	}
}
