package com.vmware.pscoe.iac.artifact.strategy;

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

import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.Package;

public class StrategyForceLatestVersions extends StrategySkipOldVersions {

	private final Logger logger = LoggerFactory.getLogger(StrategySkipOldVersions.class);

	/**
	 * This strategy will only import a package if it's the same or newer version
	 * than the one in the Orchestrator server.
	 */
	public List<Package> filterHigherVersions(List<Package> sourceEndpointPackages,
			List<Package> destinationEndpointPackages) {
		Hashtable<String, Package> latestPackages = new Hashtable<>();
		destinationEndpointPackages.forEach(aPackage -> {
			Package latest = latestPackages.get(aPackage.getName());
			if (latest == null || latest.compareTo(aPackage) < 0) {
				latestPackages.put(aPackage.getName(), aPackage);
			}
		});

		logger.info("STRATEGY| PASS | Source.Version > Destination.Version");
		List<Package> sourceEndpointPackagesHigerVersion = sourceEndpointPackages.stream().filter(sourcePackage -> {
			Package latest = latestPackages.get(sourcePackage.getName());
			boolean pass = true;
			if (latest != null) {
				int diff = sourcePackage.compareTo(latest);
				// 0 or more means we are uploading same or newer version, we are ok with that
				if (diff >= 0) {
					pass = true;
				} else {
					// -1 or less means we are uploading an older version, we are not ok with that
					pass = false;
				}
			}

			if (!pass) {
				logInfoPackages(sourcePackage, latest, "FAIL", ">");
				throw new RuntimeException(
						String.format("Package %s version %s is older than the one in the Orchestrator server %s",
								sourcePackage.getName(),
								sourcePackage.getVersion(), latest.getVersion()));
			}

			logInfoPackages(sourcePackage, latest, pass ? "PASS" : "SKIP", ">");
			return pass;
		}).collect(Collectors.toList());

		return sourceEndpointPackagesHigerVersion;
	}
}
