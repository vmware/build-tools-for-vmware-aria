/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the 
 * terms and conditions of the subcomponent's license, as noted in the 
 * LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.common.store.GenericPackageStore;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageContent;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType;

/**
 * VCFA Package Store implementation.
 * 
 * Note: This is a simplified implementation that provides basic structure.
 * A full implementation would require individual content type stores following
 * the VraNgTypeStoreFactory pattern used by VraNgPackageStore.
 */
public class VcfaPackageStore extends GenericPackageStore<VcfaPackageDescriptor> {
    
    private static final Logger logger = LoggerFactory.getLogger(VcfaPackageStore.class);
    
    private final RestClientVcfAuto restClient;
    private final ConfigurationVcfAuto config;

    public VcfaPackageStore(RestClientVcfAuto restClient, ConfigurationVcfAuto config) {
        this.restClient = restClient;
        this.config = config;
    }

    @Override
    public List<Package> getPackages() {
        logger.info("Getting VCFA packages");
        // Return empty list for now - would need to implement package discovery
        return new ArrayList<>();
    }

    @Override
    public List<Package> exportAllPackages(List<Package> packages, boolean dryrun) {
        logger.info("Exporting {} VCFA packages", packages.size());
        
        List<Package> exportedPackages = new ArrayList<>();
        for (Package pkg : packages) {
            try {
                VcfaPackageDescriptor descriptor = VcfaPackageDescriptor
                    .getInstance(new File(pkg.getFilesystemPath()));
                exportedPackages.add(exportPackage(pkg, descriptor, dryrun));
            } catch (Exception e) {
                logger.error("Failed to export package: {}", pkg.getFQName(), e);
            }
        }
        
        return exportedPackages;
    }

    @Override
    public List<Package> importAllPackages(List<Package> packages, boolean dryrun, boolean mergePackages) {
        return importAllPackages(packages, dryrun, mergePackages, false);
    }

    @Override
    public List<Package> importAllPackages(List<Package> packages, boolean dryrun, boolean mergePackages, boolean enableBackup) {
        logger.info("Importing {} VCFA packages (backup: {})", packages.size(), enableBackup);
        
        List<Package> importedPackages = new ArrayList<>();
        for (Package pkg : packages) {
            try {
                importedPackages.add(importPackage(pkg, dryrun, mergePackages));
            } catch (Exception e) {
                logger.error("Failed to import package: {}", pkg.getFQName(), e);
            }
        }
        
        return importedPackages;
    }

    @Override
    public Package exportPackage(Package pkg, VcfaPackageDescriptor descriptor, boolean dryrun) {
        logger.info("Exporting VCFA package: {}", pkg.getFQName());
        
        if (dryrun) {
            logger.info("Dry run mode - no actual export performed");
            return pkg;
        }
        
        try {
            // Authentication is handled by RestClientFactory and interceptors

            VcfaTypeStoreFactory storeFactory = VcfaTypeStoreFactory.withConfig(restClient, pkg, config, descriptor);
            for (VcfaPackageMemberType type : VcfaTypeStoreFactory.getExportOrder()) {
                storeFactory.getStoreForType(type).exportContent();
            }

            logger.info("Successfully exported VCFA package: {}", pkg.getFQName());
            return pkg;

        } catch (Exception e) {
            logger.error("Failed to export VCFA package: {}", pkg.getFQName(), e);
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public Package exportPackage(Package pkg, boolean dryrun) {
        logger.info("Exporting VCFA package: {}", pkg.getFQName());
        // Use a default empty descriptor for simple export
        VcfaPackageDescriptor descriptor = new VcfaPackageDescriptor();
        return exportPackage(pkg, descriptor, dryrun);
    }

    @Override
    public Package exportPackage(Package pkg, File descriptorFile, boolean dryrun) {
        VcfaPackageDescriptor descriptor = VcfaPackageDescriptor.getInstance(descriptorFile);
        return exportPackage(pkg, descriptor, dryrun);
    }

    @Override
    public Package importPackage(Package pkg, boolean dryrun, boolean mergePackages) {
        logger.info("Importing VCFA package: {}", pkg.getFQName());
        
        if (dryrun) {
            logger.info("Dry run mode - no actual import performed");
            return pkg;
        }
        
        try {
            // Authentication is handled by RestClientFactory and interceptors
            
            java.io.File tmp;
            try {
                tmp = java.nio.file.Files.createTempDirectory("iac-package-import").toFile();
                logger.info("Created temp dir {}", tmp.getAbsolutePath());
                new com.vmware.pscoe.iac.artifact.common.store.PackageManager(pkg).unpack(tmp);
            } catch (java.io.IOException e) {
                logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
                throw new RuntimeException("Unable to extract package.", e);
            }

            VcfaPackageDescriptor pkgDescriptor = VcfaPackageDescriptor
                    .getInstance(new java.io.File(tmp.toPath().toString() + "/content.yaml"));
            VcfaTypeStoreFactory storeFactory = VcfaTypeStoreFactory.withConfig(restClient, pkg, config,
                    pkgDescriptor);
            for (VcfaPackageMemberType type : VcfaTypeStoreFactory.getImportOrder()) {
                logger.info("Currently importing: {}", type.getTypeValue());
                storeFactory.getStoreForType(type).importContent(tmp);
            }
            
            logger.info("Successfully imported VCFA package: {}", pkg.getFQName());
            return pkg;
            
        } catch (Exception e) {
            logger.error("Failed to import VCFA package: {}", pkg.getFQName(), e);
            throw new RuntimeException("Import failed", e);
        }
    }

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        logger.info("Deleting VCFA package: {}", pkg.getFQName());
        
        if (dryrun) {
            logger.info("Dry run mode - no actual deletion performed");
            return pkg;
        }
        
        java.io.File tmp;
        try {
            tmp = java.nio.file.Files.createTempDirectory("iac-package-delete").toFile();
            logger.info("Created temp dir {}", tmp.getAbsolutePath());
            new com.vmware.pscoe.iac.artifact.common.store.PackageManager(pkg).unpack(tmp);
        } catch (java.io.IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract package.", e);
        }

        VcfaPackageDescriptor pkgDescriptor = VcfaPackageDescriptor
                .getInstance(new java.io.File(tmp.toPath().toString() + "/content.yaml"));
        VcfaTypeStoreFactory storeFactory = VcfaTypeStoreFactory.withConfig(restClient, pkg, config, pkgDescriptor);

        for (VcfaPackageMemberType type : VcfaTypeStoreFactory.getDeleteOrder()) {
            logger.info("Currently deleting: {}", type.getTypeValue());
            storeFactory.getStoreForType(type).deleteContent();
        }

        return pkg;
    }

    @Override
    protected VcfaPackageContent getPackageContent(Package pkg) {
        throw new UnsupportedOperationException(
                "VCFA does not provide native support for getting package content from packages.");
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        logger.info("Deleting VCFA content: {} ({})", content.getName(), content.getType());
        
        if (dryrun) {
            logger.info("Dry run mode - no actual deletion performed");
            return;
        }
        
        // TODO: Implement content deletion logic based on content type
        logger.warn("Content deletion not yet implemented for VCFA content type: {}", content.getType());
    }
}
