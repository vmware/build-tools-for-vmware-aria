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

/**
 * This strategy will only import a package if it's the same or newer version
 * than the one in the Orchestrator server.
 * Snapshot versions are considered newer if they are the same version.
 */
public class StrategyForceLatestVersions extends StrategySkipOldVersions {

	/**
	 * @param logger - the class logger
	 */
	private final Logger logger = LoggerFactory.getLogger(StrategyForceLatestVersions.class);

	/**
	 * We filter the packages.
	 *
	 * Logic:
	 * - If the package is not present in the destination, we will import it.
	 * - If the package has a newer version, and we will import it (`diff` will be a positive value)
	 * - If the package has the same version, we will import it if it's a snapshot. (`diff` will be 0)
	 * - If the package has an older version, we will not import it. (`diff` will be a negative value)
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
			if (latest != null) {
				int diff = sourcePackage.compareTo(latest);

				if (diff > 0 || diff == 0 && latest.isSnapshot()) {
					logInfoPackages(sourcePackage, latest, "PASS", ">");
					return true;
				} else if (diff == 0) {
					return false;
				} else {
					logInfoPackages(sourcePackage, latest, "FAIL", ">");
					throw new RuntimeException(
							String.format("Package %s version %s is older than the one in the Orchestrator server %s",
									sourcePackage.getName(),
									sourcePackage.getVersion(), latest.getVersion()));
				}
			}

			logInfoPackages(sourcePackage, latest, "PASS", ">");
			return true;
		}).collect(Collectors.toList());

		return sourceEndpointPackagesHigerVersion;
	}
}
