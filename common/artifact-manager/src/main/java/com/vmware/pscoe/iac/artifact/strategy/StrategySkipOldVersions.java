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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.Package;

public class StrategySkipOldVersions implements Strategy {

    private final Logger logger = LoggerFactory.getLogger(StrategySkipOldVersions.class);

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
                int diff = latest.compareTo(sourcePackage);
                // same version with -SNAPSHOT is considered newer
                if (diff == 0 && latest.isSnapshot()) {
                    pass = true;
                } else {
                    pass = diff < 0;
                }
            }
            logInfoPackages(sourcePackage, latest, pass ? "PASS" : "SKIP", ">");
            return pass;
        }).collect(Collectors.toList());

        return sourceEndpointPackagesHigerVersion;
    }

    /**
     * @return - sourceEndpointPackages with higher version then their server
     * representative
     */
    @Override
    public List<Package> getImportPackages(List<Package> sourceEndpointPackages,
                                           List<Package> destinationEndpointPackages) {
        logger.info("STRATEGY| INFO | Apply Configuration strategies for import");
        return filterHigherVersions(sourceEndpointPackages, destinationEndpointPackages);
    }

    /**
     * @return - sourceEndpointPackages with higher version then their server
     * representative
     */
    @Override
    public List<Package> getExportPackages(List<Package> sourceEndpointPackages,
                                           List<Package> destinationEndpointPackages) {
        logger.info("STRATEGY| INFO | Apply Configuration strategies for export");
        return filterHigherVersions(sourceEndpointPackages, destinationEndpointPackages);
    }

    private void logInfoPackages(Package sourcePackage, Package destinationPackage, String filterFlag, String filterSign) {
        String msg = String.format("PACKAGE | %s | %s (%s) %s (%s)", filterFlag, sourcePackage.getName(),
                sourcePackage.getVersion(), filterSign,
                (destinationPackage != null ? destinationPackage.getVersion() : "missing"));
        logger.info(msg);
    }

}
