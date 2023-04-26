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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.extentions.PackageStoreExtention;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.vro.VroPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;
import org.springframework.web.client.HttpClientErrorException;

public class VroPackageStore extends GenericPackageStore<VroPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
    private final Logger logger = LoggerFactory.getLogger(VroPackageStore.class);

	/**
	 * The vRO rest client.
	 */
    private final RestClientVro restClient;

	/**
	 * The vRO package store strategies.
	 */
    private final List<Strategy> strategies;

	/**
	 * The vRO package store extensions.
	 */
    private final List<PackageStoreExtention<VroPackageDescriptor>> extentions;

	/**
	 *
	 * @param vroRestClient the vRO rest client
	 * @param vroStrategies the vRO strategies
	 * @param vroExtentions the vRO extensions
	 */
    protected VroPackageStore(final RestClientVro vroRestClient, final List<Strategy> vroStrategies, final List<PackageStoreExtention<VroPackageDescriptor>> vroExtentions) {
        this.restClient = vroRestClient;
        this.strategies = vroStrategies;
        this.extentions = vroExtentions;
    }

	/**
	 *
	 * @param vroRestClient the vRO rest client
	 * @param vroStrategies the vRO strategies
	 * @param vroExtentions the vRO extensions
	 * @param vroProductVersion the vRO product version
	 */
    protected VroPackageStore(final RestClientVro vroRestClient, final List<Strategy> vroStrategies, final List<PackageStoreExtention<VroPackageDescriptor>> vroExtentions, final Version vroProductVersion) {
        this.restClient = vroRestClient;
        this.strategies = vroStrategies;
        this.extentions = vroExtentions;
        super.setProductVersion(vroProductVersion);
    }

	/**
	 * Gets the vRO packages.
	 * @return the extracted packages
	 */
    @Override
    public final List<Package> getPackages() {
        List<Package> pkgs = restClient.getPackages();
        for (Package pkg : pkgs) {
            logger.trace(String.format(PackageStore.PACKAGE_LIST, pkg));
        }

        return pkgs;
    }

	/**
	 * Exports all packages.
	 * @param vroPackages the packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
    @Override
    public final List<Package> exportAllPackages(final List<Package> vroPackages, final boolean dryrun) {
        this.vlidateServer(vroPackages);

        List<Package> sourceEndpointPackages = vroPackages;
        List<Package> destinationEndpointPackages = vroPackages.stream().filter(pkg -> new File(pkg.getFilesystemPath()).exists()).collect(Collectors.toList());
        for (Strategy strategy : strategies) {
            sourceEndpointPackages = strategy.getExportPackages(sourceEndpointPackages, destinationEndpointPackages);
        }
        if (sourceEndpointPackages.isEmpty()) {
            return new ArrayList<>();
        }

        List<Package> exportedPackages = new ArrayList<>();
        for (Package pkg : sourceEndpointPackages) {
            exportedPackages.add(this.exportPackage(pkg, dryrun));
        }

        return exportedPackages;
    }

	/**
	 * Imports all packages.
	 * @param pkg the packages to import
	 * @param dryrun whether it should be dry run
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkg, final boolean dryrun, final boolean enableBackup) {
		return this.importAllPackages(pkg, dryrun, false, enableBackup);
	}

	/**
	 * Imports all packages.
	 * @param vroPackages the packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param vroEnableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
    public final List<Package> importAllPackages(final List<Package> vroPackages, final boolean dryrun, final boolean mergePackages, final boolean vroEnableBackup) {
		this.validateFilesystem(vroPackages);
		
		System.out.println("Start executing import all packages...");
		this.validateFilesystem(vroPackages);

		List<Package> packagesToImport = vroPackages;
		List<Package> destinationEndpointPackages = restClient.getPackages();
		for (Strategy strategy : strategies) {
			packagesToImport = strategy.getImportPackages(packagesToImport, destinationEndpointPackages); //filtered packages on file system
		}
		if (packagesToImport.isEmpty()) {
			return new ArrayList<>();
		}

		if (vroEnableBackup && packagesToImport.size() > 0) {
			//TO change the packages to backup to ALL the packages currently present in vRO -> in this if statements replace packagesToImport with destinationEndpointPackages
			System.out.println("Number of packages to backup: " + packagesToImport.size());
			boolean exportConfigAttributeValues = true;
			boolean exportConfigSecureStringValues = true;

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss");
			String currentDateTimeString = timeStampFormat.format(timestamp);
			Package firstPackage = packagesToImport.get(0);
			String backupFilesDirectory = this.createBackupFileDirectory(firstPackage, currentDateTimeString);

			packagesToImport.forEach(pkg -> {
				String originalPkgFilePath = pkg.getFilesystemPath();
				String backupFilePath = this.createBackupFilePath(pkg, currentDateTimeString, backupFilesDirectory);

				try {
					pkg.setFilesystemPath(backupFilePath);
					restClient.exportPackage(pkg, dryrun, exportConfigAttributeValues, exportConfigSecureStringValues);
				} catch (Exception ex) {
					String exceptionMessage = ex.getMessage();
					System.out.println("ExceptionMessage: " + exceptionMessage);
					System.out.println("Package Name: " + pkg.getName());

					if (!exceptionMessage.contains("404 Not Found")
						||
						!exceptionMessage.contains(pkg.getName())) { //Unexpected exception
						throw ex;
					} else { //The package to be imported has been deleted from the server
						System.out.println(ex.getMessage());
					}
				}

				System.out.println("Restoring original file path...");
				pkg.setFilesystemPath(originalPkgFilePath);
				System.out.println("File path after restoration: " + pkg.getFilesystemPath());
			});
		}

        List<Package> importedPackages = new ArrayList<>();
        for (Package pkg : packagesToImport) {
            importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
        }

        return importedPackages;
    }

	/**
	 * Exports a package.
	 * @param vraPackage the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
    @Override
    public final Package exportPackage(final Package vraPackage, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));

		boolean exportConfigAttributeValues = false;
		boolean exportConfigSecureStringValues = false;

        Package pkg = restClient.exportPackage(vraPackage, dryrun, exportConfigAttributeValues, exportConfigSecureStringValues);
        for (PackageStoreExtention<VroPackageDescriptor> e : extentions) {
            e.exportPackage(pkg, null, dryrun);
        }

        return pkg;
    }

	/**
	 * Exports a package.
	 * @param vraPackage the package to export
	 * @param vroPackageDescriptor the package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    @Override
	public final Package exportPackage(final Package vraPackage, final VroPackageDescriptor vroPackageDescriptor, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));

		boolean exportConfigAttributeValues = false;
		boolean exportConfigSecureStringValues = false;

		Package pkg = restClient.exportPackage(vraPackage, dryrun, exportConfigAttributeValues, exportConfigSecureStringValues);
		for (PackageStoreExtention<VroPackageDescriptor> e : extentions) {
		    e.exportPackage(pkg, vroPackageDescriptor, dryrun);
		}

		return pkg;
	}

	/**
	 * Imports a package.
	 * @param vraPackage the package to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @return the imported package
	 */
    @Override
    public final Package importPackage(final Package vraPackage, final boolean dryrun, final boolean mergePackages) {
		if (mergePackages) {
			logger.info(String.format(PackageStore.PACKAGE_MERGE, vraPackage));
		} else {
			logger.info(String.format(PackageStore.PACKAGE_IMPORT, vraPackage));
		}

        logger.debug(String.format("System path: %s", vraPackage.getFilesystemPath()));
		Package pkg;

		try {
			pkg = restClient.importPackage(vraPackage, dryrun, mergePackages);
		} catch (HttpClientErrorException e) {
			if (e.getMessage().contains("Unknown object type 'Module' caused by: Unknown object type 'Module'")) {
				throw new RuntimeException("One of the actions that we tried to import contains a path that is too long.", e);
			}

			throw e;
		}

        for (PackageStoreExtention<VroPackageDescriptor> e : extentions) {
            e.importPackage(pkg, dryrun);
        }

        return pkg;
    }

	/**
	 * Exports a package.
	 * @param vroPackage the package to export
	 * @param vroPackageDescriptor the descriptor of the package to export
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    @Override
    public final Package exportPackage(final Package vroPackage, final File vroPackageDescriptor, final boolean dryrun) {
        throw new UnsupportedOperationException("Not supported operation in vRO");
    }

	/**
	 * Deletes a package.
	 * @param pkg the package to delete
	 * @param withContent whether to delete the package with its content
	 * @param dryrun whether it should be dry run
	 * @return the deleted package
	 */
	@Override
    protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
        return restClient.deletePackage(pkg, withContent, dryrun);
    }

	/**
	 * Gets package conent.
	 * @param pkg the package which content to get
	 * @return the content of the package
	 */
	@Override
    protected final PackageContent getPackageContent(final Package pkg) {
        return restClient.getPackageContent(pkg);
    }

	/**
	 * Deletes conent.
	 * @param content the conent to delete
	 * @param dryrun whether it should be dry run
	 */
	@Override
    protected final void deleteContent(final Content content, final boolean dryrun) {
        restClient.deleteContent(content, dryrun);
    }

	/**
	 * Creates backup file directory.
	 * @param pkg the package to back up
	 * @param currentDateTimeString the current date-time in human friendly format
	 * @return the directory for backup
	 */
	private String createBackupFileDirectory(final Package pkg, final String currentDateTimeString) {
		Path pkgFullPath = Paths.get(pkg.getFilesystemPath());
		logger.debug("pkgFullPath: " + pkgFullPath.toString());

		Path parent = pkgFullPath.getParent();
		logger.debug("parent: " + parent.toString());

		String newParentPath = parent.toString() + "\\backup_" + currentDateTimeString + "\\";
		logger.debug("newParentPath: " + newParentPath);

		File newParentPackage = new File(newParentPath);
		boolean backupDirectoryCreated = newParentPackage.mkdir();
		logger.debug("backupDirectoryCreated: " + backupDirectoryCreated);

		return newParentPath;
	}

	/**
	 * Creates the path of the backup file.
	 * @param pkg the package to back up
	 * @param currentDateTimeString the current date-time in human friendly format
	 * @param backupDirectoryPath the directory for backup
	 * @return the path of the file to be backed up
	 */
	private String createBackupFilePath(final Package pkg, final String currentDateTimeString, final String backupDirectoryPath) {
		Path pkgFullPath = Paths.get(pkg.getFilesystemPath());
		logger.debug("pkgFullPath: " + pkgFullPath.toString());

		Path fileName = pkgFullPath.getFileName();
        logger.debug("fileName: " + fileName.toString());

		String newFileName = fileName.toString() + "_bak_" + currentDateTimeString;
		logger.debug("newFileName: " + newFileName);

		String newFullPath = backupDirectoryPath + newFileName;
        logger.debug("newFullPath: " + newFullPath);

		return newFullPath;
	}
}
