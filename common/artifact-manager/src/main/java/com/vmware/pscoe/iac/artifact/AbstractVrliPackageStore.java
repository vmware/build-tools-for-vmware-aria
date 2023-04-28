package com.vmware.pscoe.iac.artifact;

/*-
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

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.vrli.VrliPackageDescriptor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVrliPackageStore extends GenericPackageStore<VrliPackageDescriptor> {
	/**
	 * Constant for the alerts directory.
	 */
	protected static final String DIR_ALERTS = "alerts";

	/**
	 * Constant for the content packs directory.
	 */
	private final String dirContentPacks = "content_packs";

	/**
	 * Variable for logging.
	 */
	protected Logger logger;

	/**
	 *
	 * @param pkg package to export
	 * @param vrliPackageDescriptor descriptor of the package
	 * @param dryrun whether it should be a dry run
	 * @return exported package
	 */
	@Override
	public final Package exportPackage(final Package pkg, final VrliPackageDescriptor vrliPackageDescriptor, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, pkg));
		List<String> alertNames = vrliPackageDescriptor.getAlerts();
		if (alertNames != null) {
			this.exportAlerts(pkg, alertNames);
		} else {
			logger.info("No alerts found in content.yaml");
		}
		List<String> contentPackNames = vrliPackageDescriptor.getContentPacks();
		if (contentPackNames != null) {
			this.exportContentPacks(pkg, contentPackNames);
		} else {
			logger.info("No content packs found in content.yaml");
		}

		return pkg;
	}


	/**
	 * @param pkgs packages to export
	 * @param dryrun whether it should be a dry run
	 * @return exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> pkgs, final boolean dryrun) {
		this.vlidateServer(pkgs);

		List<Package> sourceEndpointPackages = pkgs;
		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}
		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : pkgs) {
			VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));
			exportedPackages.add(this.exportPackage(pkg, descriptor, dryrun));
		}

		return exportedPackages;
	}


	/**
	 *
	 * @param pkgs packages to import
	 * @param dryrun whether it should be a dry run
	 * @param mergePackages whether to merge packages
	 * @param enableBackup whether to enable package backup
	 * @return imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkgs, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
		this.validateFilesystem(pkgs);

		List<Package> sourceEndpointPackages = pkgs;
		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}
		List<Package> importedPackages = new ArrayList<>();
		for (Package pkg : sourceEndpointPackages) {
			importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
		}

		return importedPackages;
	}


	/**
	 *
	 * @param pkg packages to import
	 * @param dryrun whether it should be a dry run
	 * @param enableBackup whether to enable package backup
	 * @return imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkg, final boolean dryrun, final boolean enableBackup) {
		return this.importAllPackages(pkg, dryrun, false, enableBackup);
	}


	/** 
	* @param pkg package to export
	* @param dryrun whether it should be a dry run
	* @return exported package
	*/
	@Override
	public final Package exportPackage(final Package pkg, final boolean dryrun) {
		VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));

		return this.exportPackage(pkg, descriptor, dryrun);
	}

	/**
	 * @param pkg package to export
	 * @param exportDescriptor description of the exported package
	 * @param dryrun whether it should be a dry run
	 * @return exported package
	 */
	@Override
	public final Package exportPackage(final Package pkg, final File exportDescriptor, final boolean dryrun) {
		VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(exportDescriptor);

		return this.exportPackage(pkg, descriptor, dryrun);
	}

	/**
	 * @param pkg package to import
	 * @param dryrun whether it should be a dry run
	 * @param mergePackages whether to merge the packages
	 * @return imported package
	 */
	@Override
	public final Package importPackage(final Package pkg, final boolean dryrun, final boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, pkg));

		File tmp;
		try {
			tmp = Files.createTempDirectory("vrbt-temp-import-dir").toFile();
			logger.info("Created temp dir {}", tmp.getAbsolutePath());
			new PackageManager(pkg).unpack(tmp);
		} catch (IOException e) {
			logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
			throw new RuntimeException("Unable to extract pacakge.", e);
		}
		importAlerts(tmp);
		importContentPacks(tmp);

		return pkg;
	}

	/**
	 * @param tmp the temp file to import alerts from
	 */
	protected final void importAlerts(final File tmp) {
		File alertsDirectory = Paths.get(tmp.getPath(), DIR_ALERTS).toFile();

		if (alertsDirectory.exists()) {
			FileUtils.listFiles(alertsDirectory, new String[] {"json"}, false).stream().forEach(this::importAlert);
		}
	}

	/**
	 * @param tmp the temp file to import content packs from
	 */
	protected final void importContentPacks(final File tmp) {
		File contentPacksDirectory = Paths.get(tmp.getPath(), dirContentPacks).toFile();

		if (contentPacksDirectory.exists()) {
			FileUtils.listFiles(contentPacksDirectory, new String[] {"json"}, false).stream().forEach(this::importContentPack);
		}
	}

	/**
	 *
	 * @return the packages that are received
	 */
	@Override
	public final List<Package> getPackages() {
		throw new UnsupportedOperationException("getPackages: vRLI does not provide native package support.");
	}

	/**
	 * @param pkg packages to delete
	 * @param withContent whather to delete the package with content
	 * @param dryrun whether it should be a dry run
	 * @return the deleted package
	 */
	@Override
	protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
		throw new UnsupportedOperationException("deletePackage: vRLI does not provide native package support.");
	}

	/**
	 * @param pkg package to get
	 * @return the package content
	 */
	@Override
	protected final PackageContent getPackageContent(final Package pkg) {
		throw new UnsupportedOperationException("getPackageContent: vRLI does not provide native package support.");
	}

	/**
	 * @param content content to be deleted
	 * @param dryrun whether it should be a dry run
	 */
	@Override
	protected final void deleteContent(final PackageContent.Content content, final boolean dryrun) {
		throw new UnsupportedOperationException("deleteContent: vRLI does not provide native package support.");
	}

	/**
	 * @param vrliPakage package to export content from
	 * @param contentPackName the name of the package
	 * @param contentPackData the content of the package
	 * @return the file with the exports
	 */
	protected final File exportContentPack(final Package vrliPakage, final String contentPackName, final String contentPackData) {
		File store = new File(vrliPakage.getFilesystemPath());
		File contentPacksFile = Paths.get(store.getPath(), dirContentPacks, contentPackName + ".json").toFile();
		contentPacksFile.getParentFile().mkdirs();

		try {
			logger.info("Exporting content pack '{}'", contentPackName);
			Files.write(Paths.get(contentPacksFile.getPath()), contentPackData.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error("Unable to store content pack {} {}", contentPackName, contentPacksFile.getPath());
			throw new RuntimeException(String.format("Unable to store content pack '%s' : %s.", contentPackName, e.getMessage()));
		}

		return contentPacksFile;
	}

	/**
	 * @param alertFile the file with the alert to import
	 */
	protected abstract void importAlert(File alertFile);

	/**
	 * @param contentPackFile the file with the content pack to import
	 */
	protected abstract void importContentPack(File contentPackFile);

	/**
	 * @param vrliPakage the vrli package to export alerts from
	 * @param alertNames the alert names to export
	 */
	protected abstract void exportAlerts(Package vrliPakage, List<String> alertNames);

	/**
	 * @param vrliPakage the vrli package to export content pack from
	 * @param contentPackNames the content pack names to export
	 */
	protected abstract void exportContentPacks(Package vrliPakage, List<String> contentPackNames);
}
