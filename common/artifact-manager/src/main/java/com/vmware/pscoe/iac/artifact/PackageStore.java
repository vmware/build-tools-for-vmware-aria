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
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.Version;

/**
 * An interface representing working with packages from the view point of the server that consumes such packages.
 *
 * In that sense exporting a packaging is the process that includes contacting the server and downloading a package
 * file from it into a local file/folder structure.
 *
 * On the contrary, importing a packaging means to take some existing file/folder structure and upload it into the
 * server, where the server would take appropriate actions to properly apply the package.
 *
 * Locally, the package is represented by an {@link Package} object.
 * @param <T> Could be different for different sort of servers (vRA, vRO, vROps, etc...).
 */
public interface PackageStore<T extends PackageDescriptor> {
	/**
	 * The package list.
	 */
	String PACKAGE_LIST   = "Package | LIST   | %s";

	/**
	 * The package to import.
	 */
    String PACKAGE_IMPORT = "Package | IMPORT | %s";

	/**
	 * The package to export.
	 */
    String PACKAGE_EXPORT = "Package | EXPORT | %s";

	/**
	 * The package to merge.
	 */
	String PACKAGE_MERGE = "Package | MERGE | %s";

	/**
	 * Download the packages.
	 * @return the downloaded packages
	 */
    List<Package> getPackages();

	/**
	 * Exports all packages.
	 * @param pkg the packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
    List<Package> exportAllPackages(List<Package> pkg, boolean dryrun);

	/**
	 * Imports all packages.
	 * @param pkg the packages to import
	 * @param dryrun whether it should be dry run
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	List<Package> importAllPackages(List<Package> pkg, boolean dryrun, boolean enableBackup);

	/**
	 * Imports all packages.
	 * @param pkg the packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	List<Package> importAllPackages(List<Package> pkg, boolean dryrun, boolean mergePackages, boolean enableBackup);

	/**
	 * Deletes a package.
	 * @param pkg the package to delete
	 * @param lastVersion whether it should delete the last version
	 * @param oldVersions whether it should delete the old versions
	 * @param dryrun whether it should be dry run
	 * @return the deleted package
	 */
    List<Package> deletePackage(Package pkg, boolean lastVersion, boolean oldVersions,  boolean dryrun);

	/**
	 * Deletes all the packages.
	 * @param vroPackages the vRO packages to delete
	 * @param lastVersion whether it should delete the last version
	 * @param oldVersions whether it should delete the old versions
	 * @param dryrun whether it should be dry run
	 * @return the deleted packages
	 */
    List<Package> deleteAllPackages(List<Package> vroPackages, boolean lastVersion, boolean oldVersions, boolean dryrun);

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param packageDescriptor the package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    Package exportPackage(Package pkg, T packageDescriptor, boolean dryrun);

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
    Package exportPackage(Package pkg, boolean dryrun);

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param exportDescriptor the descriptor of the package to export
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun);

	/**
	 * Imports a package.
	 * @param pkg the package to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @return the imported package
	 */
    Package importPackage(Package pkg, boolean dryrun, boolean mergePackages);

	/**
	 * Gets product version.
	 * @return the version of the product
	 */
    Version getProductVersion();
}
