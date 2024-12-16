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
package com.vmware.pscoe.iac.artifact.aria.store.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.aria.model.VraNgBlueprint;
import com.vmware.pscoe.iac.artifact.aria.rest.RestClientVraNg;

public class VraNgReleaseManager {

	RestClientVraNg restClient;

	private final Logger logger = LoggerFactory.getLogger(VraNgReleaseManager.class);

	public VraNgReleaseManager(RestClientVraNg restClient) {
		this.restClient = restClient;
	}

	public void releaseContent(String contentType, List<String> contentNames, String version,
			boolean releaseIfNotUpdated) {

		if (contentType.equals("blueprint") || contentType.equals("all")) {
			List<VraNgBlueprint> blueprints = this.restClient.getAllBlueprints();
			if (contentNames.size() > 0) {
				blueprints = blueprints.stream()
						.filter(bp -> contentNames.contains(bp.getName()))
						.collect(Collectors.toList());
			}

			List<String> invalidBlueprints = new ArrayList<>();
			blueprints.forEach(bp -> {
				if (this.restClient.isBlueprintVersionPresent(bp.getId(), version)) {
					invalidBlueprints.add(bp.getName());
				}
			});

			if (invalidBlueprints.size() > 0) {
				throw new RuntimeException("Blueprints [" + String.join(", ", invalidBlueprints)
						+ "] already have a released version " + version);
			}

			blueprints.forEach(bp -> {
				if (releaseIfNotUpdated == true) {
					this.restClient.releaseBlueprintVersion(bp.getId(), version);
					logger.info("Released blueprint " + bp.getName() + " version " + version);
				} else {
					this.releaseVersion(bp, version);
				}
			});
		}

	}

	/**
	 * Attempt to generate a next version and release it.
	 * 
	 * @param blueprint blueprint
	 */
	public void releaseNextVersion(VraNgBlueprint blueprint) {
		String latestVersion = this.restClient.getBlueprintLastUpdatedVersion(blueprint.getId());
		String nextVersion = this.getNextVersion(latestVersion);
		logger.debug("Next version of blueprint {}: {}", blueprint.getName(), nextVersion);

		try {
			this.releaseVersion(blueprint, nextVersion);
		} catch (Exception e) {
			// Attempt to fix versions imported in reverse order, which produces an Error on
			// imports
			logger.warn("Couldn't release version '{}'. Attempting to release date version", nextVersion);
			this.releaseVersion(blueprint, this.getDateVersion());
		}
	}

	/**
	 * Release a new version of the blueprint provided that there is no previous
	 * version or there are
	 * changes in the content since the latest released version.
	 * 
	 * @param blueprint blueprint
	 * @param version   new version
	 */
	public void releaseVersion(VraNgBlueprint blueprint, String version) {
		String latestVersion = this.restClient.getBlueprintLastUpdatedVersion(blueprint.getId());
		if (latestVersion == null || this.isUpdated(blueprint, latestVersion)) {
			this.restClient.releaseBlueprintVersion(blueprint.getId(), version);
			logger.info("Released blueprint " + blueprint.getName() + " version " + version);
		} else {
			logger.info("Skipping release of blueprint " + blueprint.getName() + ". No changes since latest version.");
		}
	}

	/**
	 * Perform a check whether the blueprint content has been updated since the
	 * latest released version.
	 * 
	 * @param blueprint     blueprint
	 * @param latestVersion latest version
	 * @return true if there are changes
	 */
	private boolean isUpdated(VraNgBlueprint blueprint, String latestVersion) {
		String draftContent = blueprint.getContent();
		String latestVersionContent = this.restClient.getBlueprintVersionContent(blueprint.getId(), latestVersion);
		if (draftContent.equals(latestVersionContent)) {
			return false;
		}

		return true;
	}

	/**
	 * Generate next version based on the previous version format.
	 * Supported version formats are:
	 * * MAJOR
	 * * MAJOR.MINOR
	 * * MAJOR.MINOR.PATCH
	 * A datetime-based version will be returned if the previous version format does
	 * not match
	 * any of the supported formats.
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
					majorMinorPatch.group(3));
			// increment the patch segment
			return majorMinorPatch.group(1) + "." + majorMinorPatch.group(2) + "."
					+ (Integer.parseInt(majorMinorPatch.group(3)) + 1);
		} else if (majorMinor.matches()) {
			logger.debug("Detected version pattern MAJOR.MINOR from '{}' with incrementable segment '{}'", version,
					majorMinor.group(2));
			// increment the minor segment
			return majorMinor.group(1) + "." + (Integer.parseInt(majorMinor.group(2)) + 1);
		} else if (major.matches()) {
			logger.debug("Detected version pattern MAJOR from '{}' with incrementable segment '{}'", version,
					major.group(1));
			// increment the major segment
			return Integer.toString(Integer.parseInt(major.group(1)) + 1);
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
