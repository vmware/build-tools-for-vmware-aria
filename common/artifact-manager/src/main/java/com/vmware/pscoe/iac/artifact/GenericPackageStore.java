package com.vmware.pscoe.iac.artifact;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.Version;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

public abstract class GenericPackageStore<T extends PackageDescriptor> implements PackageStore<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final String WILDCARD_MATCH_SYMBOL = "*";

    private Version productVersion;

    protected abstract Package deletePackage(Package pkg, boolean withContent, boolean dryrun);

    protected abstract PackageContent getPackageContent(Package pkg);

    protected abstract void deleteContent(Content content, boolean dryrun);

    protected Logger getLogger() {
        return logger;
    }

    protected void validateFilesystem(List<Package> packages) {
        packages.stream().forEach(pkg -> {
            if (!new File(pkg.getFilesystemPath()).exists()) {
                throw new RuntimeException("Cannot find package " + pkg.getFilesystemPath());
            }
        });
    }

    protected void vlidateServer(List<Package> packages) {
        List<Package> srvPackages = this.getPackages();

        packages.stream().forEach(pkg -> {
            if (!srvPackages.contains(pkg)) {
                throw new RuntimeException("Cannot find package " + pkg.getFQName() + " on server.");
            }
        });
    }

    @Override
    public List<Package> deleteAllPackages(List<Package> packages, boolean lastVersion, boolean oldVersions,
            boolean dryrun) {
        this.validateFilesystem(packages);

        List<Package> deleted = new ArrayList<>();
        for (Package pkg : packages) {
            deleted.addAll(this.deletePackage(pkg, lastVersion, oldVersions, dryrun));
        }

        return deleted;
    }

    @Override
    public Version getProductVersion() {
        return this.productVersion;
    }

    public void setProductVersion(Version productVersion) {
        this.productVersion = productVersion;
    }

    @Override
    public List<Package> deletePackage(Package vroPackage, boolean lastVersion, boolean oldVersions, boolean dryrun) {
        logger.info("Cleaning up server package '{}' versions LATEST={}, OLDER={} DRYRUN={}", vroPackage.getName(),
                lastVersion, oldVersions, dryrun);

        List<Package> deleted = new ArrayList<>();

        if (!lastVersion && !oldVersions) {
            logger.info("Nothing to do.");
            return deleted;
        }

        // Get all package versions
        List<Package> serverPackages = this.getPackages().stream().filter(p -> p.getName().equals(vroPackage.getName()))
                .collect(Collectors.toList());

        Collections.sort(serverPackages);

        LinkedList<Package> all = new LinkedList(serverPackages);

        for (Package p : all) {
            logger.info("Found package '{}' on server.", p.getFQName());
        }

        if (all.size() == 0 || !all.contains(vroPackage)) {
            logger.info("Nothing to do. There is no package '{}' available on the server.", vroPackage.getFQName());
            return deleted;
        }

        Package latest = all.pollLast();

        if (!latest.equals(vroPackage)) {
            logger.error("Not supported operation. Server contains higher version of package {} than the provided {}.",
                    latest, vroPackage);
            return deleted;
        }

        if (lastVersion && oldVersions) {
            for (Package p : all) {
                logger.info("Removing package version '{}' with its content.", p.getFQName());
                deleted.add(this.deletePackage(p, true, dryrun));
            }
        } else if (lastVersion) {
            if (all.size() == 0) {
                logger.info("Removing package version '{}' with its content.", vroPackage.getFQName());
                deleted.add(this.deletePackage(latest, true, dryrun));
            } else {
                Package previous = all.pollLast();
                logger.warn("Package version '{}' and its content will be cleaned up against previous version '{}'",
                        latest, previous);
                deleted.add(deletePackageVersion(previous, latest, dryrun));
            }
        } else if (oldVersions) {
            for (Package p : all) {
                deletePackageVersion(latest, p, dryrun);
                deleted.add(p);
            }
        }
        return deleted;
    }

    protected boolean isPackageAssetMatching(String matchExpression, String assetName) {
        String pattern = matchExpression;
        boolean startsWith = pattern.startsWith(WILDCARD_MATCH_SYMBOL);
        boolean endsWith = pattern.endsWith(WILDCARD_MATCH_SYMBOL);
        boolean containsWildcard = pattern.contains(WILDCARD_MATCH_SYMBOL);

        if (startsWith) {
            pattern = ".*" + pattern;
        }
        if (endsWith) {
            pattern = pattern + ".*";
        }
        if (containsWildcard) {
            pattern = pattern.replace(WILDCARD_MATCH_SYMBOL, ".*");
            pattern = ".*" + pattern + ".*";
        }

        return Pattern.compile(pattern).matcher(assetName).matches();
    }

    private Package deletePackageVersion(Package lastPackage, Package toBeRemovedPackage, boolean dryrun) {
        PackageContent latest = this.getPackageContent(lastPackage);

        try {
            PackageContent toBeRemoved = this.getPackageContent(toBeRemovedPackage);

            List<Content> contentToBeRemoved = new ArrayList<Content>();
            contentToBeRemoved.addAll(toBeRemoved.getContent());
            contentToBeRemoved.removeAll(latest.getContent());

            logger.info("Deleting content of package '{}' ...", toBeRemovedPackage.getFQName());
            for (Content c : contentToBeRemoved) {
                logger.info("Deleting content '{}'", c);
                try {
                    this.deleteContent(c, dryrun);
                } catch (Exception e) {
                    logger.warn("Could not delete content '" + c.toString() + "'", e);
                }
            }
            logger.info("Deleting package '{}' ...", toBeRemovedPackage.getFQName());
            this.deletePackage(toBeRemovedPackage, false, dryrun);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // alaredy deleted - ignore cleanup, re-throw error otherwise
            } else {
                throw e;
            }
        }

        return toBeRemovedPackage;
    }

}
