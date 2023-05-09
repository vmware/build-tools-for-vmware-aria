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
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the 
 * terms and conditions of the subcomponent's license, as noted in the 
 * LICENSE file.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageContent;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.store.vrang.VraNgTypeStoreFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VraNgPackageStore extends GenericPackageStore<VraNgPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgPackageStore.class);

	/**
	 * The vRA rest client.
	 */
	private final RestClientVraNg restClient;

	/**
	 * The vRA configuration.
	 */
	private final ConfigurationVraNg config;

	/**
	 *
	 * @param vraRestClient the vRA rest client
	 * @param vraConfig     the vRA config
	 */
	protected VraNgPackageStore(final RestClientVraNg vraRestClient, final ConfigurationVraNg vraConfig) {
		this.restClient = vraRestClient;
		this.config = vraConfig;
	}

	/**
	 * Gets the vRA packages.
	 * 
	 * @return the extracted vRA packages
	 */
	@Override
	public final List<Package> getPackages() {
		throw new UnsupportedOperationException(
				"getPackages: Cloud Automation Services does not provide native support for packages.");
	}

	/**
	 * Exports all packages.
	 * 
	 * @param vraPackages the packages to export
	 * @param dryrun      whether it should be dry run
	 * @return the exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> vraPackages, final boolean dryrun) {
		this.vlidateServer(vraPackages);

		List<Package> sourceEndpointPackages = vraPackages;
		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : vraPackages) {
			VraNgPackageDescriptor vraPackageDescriptor = VraNgPackageDescriptor
					.getInstance(new File(pkg.getFilesystemPath()));
			exportedPackages.add(this.exportPackage(pkg, vraPackageDescriptor, dryrun));
		}

		return exportedPackages;
	}

	/**
	 * Imports all packages.
	 * 
	 * @param pkg          the packages to import
	 * @param dryrun       whether it should be dry run
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkg, final boolean dryrun,
			final boolean enableBackup) {
		return this.importAllPackages(pkg, dryrun, false, enableBackup);
	}

	/**
	 * Imports all packages.
	 * 
	 * @param vraNgPackages the packages to import
	 * @param dryrun        whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup  whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public List<Package> importAllPackages(final List<Package> vraNgPackages, final boolean dryrun,
			final boolean mergePackages, final boolean enableBackup) {
		this.validateFilesystem(vraNgPackages);
		this.waitForDataCollectionDelay();

		List<Package> sourceEndpointPackages = vraNgPackages;
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
	 * Exports a package.
	 * 
	 * @param vraNgPackage the package to export
	 * @param dryrun       whether it should be a dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package vraNgPackage, final boolean dryrun) {
		VraNgPackageDescriptor vraNgPackageDescriptor = VraNgPackageDescriptor
				.getInstance(new File(vraNgPackage.getFilesystemPath()));

		return this.exportPackage(vraNgPackage, vraNgPackageDescriptor, dryrun);
	}

	/**
	 * Waits a variable amount of time for vRA data collection.
	 * If nothing is passed, then we will not wait.
	 *
	 * Note: Should we introduce a default one if nothing is passed?
	 */
	private void waitForDataCollectionDelay() {
		String collectionDelayRaw = this.config.getDataCollectionDelaySeconds();

		if (collectionDelayRaw == null) {
			return;
		}

		try {
			int collectionDelay = Integer.parseInt(collectionDelayRaw);
			if (collectionDelay > 0) {
				logger.warn(
						"Waiting {} seconds for the vRO data collection. This is configurable with vrang.data.collection.delay.seconds property",
						collectionDelay);

				final long collectionDelayMultiplier = 1000L;
				long delayInMs = collectionDelay * collectionDelayMultiplier;
				Thread.sleep(delayInMs);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted waiting for data collection", e);
		} catch (NumberFormatException e) {
			logger.warn("vrang.data.collection.delay.seconds passed with invalid value {}", collectionDelayRaw);
		}
	}

	/**
	 * Main handler for exporting vra-ng package based on content.yaml file
	 * This method performs the following steps:
	 * 1. Export Property groups
	 * 2. Export blueprints
	 * 3. Export subscriptions
	 * 4. Export regional content - flavor mappings, image mappings, storage
	 * profiles
	 * 5. Export catalog entitlements
	 * 6. Export catalog items
	 *
	 * @param vraNgPackage           vRA package
	 * @param vraNgPackageDescriptor vRA package descriptor
	 * @param dryrun                 dryrun
	 * @return package
	 */
	@Override
	public final Package exportPackage(final Package vraNgPackage, final VraNgPackageDescriptor vraNgPackageDescriptor,
			final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraNgPackage));
		VraNgTypeStoreFactory storeFactory = VraNgTypeStoreFactory.withConfig(restClient, vraNgPackage, config,
				vraNgPackageDescriptor);
		for (VraNgPackageContent.ContentType type : VraNgTypeStoreFactory.getExportOrder()) {
			storeFactory.getStoreForType(type).exportContent();
		}

		return vraNgPackage;
	}

	/**
	 * Exports a package.
	 * 
	 * @param vraPackage                 the package to export
	 * @param vraNgPackageDescriptorFile the descriptor of the package to export
	 * @param dryrun                     whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package vraPackage, final File vraNgPackageDescriptorFile,
			final boolean dryrun) {
		VraNgPackageDescriptor vraNgPackageDescriptor = VraNgPackageDescriptor.getInstance(vraNgPackageDescriptorFile);

		return this.exportPackage(vraPackage, vraNgPackageDescriptor, dryrun);
	}

	/**
	 * Main handler for importing vra-ng package. This method performs the following
	 * steps:
	 * 1. Import Custom Resources
	 * 2. Import Cloud Assembly blueprints
	 * a. Import blueprints
	 * b. Import content sources
	 * c. Import content sharing
	 * 3. Import vRA subscriptions
	 * 4. Import vRA regional content - flavor mappings, image mappings, storage
	 * profiles
	 * 5. Import Service Broker custom forms
	 * 6. Import Service Broker catalog entitlements
	 * 7. Import Resource Actions
	 *
	 * @param vraNgPackage vRA package
	 * @param dryrun       dryrun
	 * @return package
	 */
	@Override
	public final Package importPackage(final Package vraNgPackage, final boolean dryrun, final boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, vraNgPackage));

		File tmp;
		try {
			tmp = Files.createTempDirectory("iac-package-import").toFile();
			logger.info("Created temp dir {}", tmp.getAbsolutePath());
			new PackageManager(vraNgPackage).unpack(tmp);
		} catch (IOException e) {
			logger.error("Unable to extract package '{}' in temporary directory.", vraNgPackage.getFQName());
			throw new RuntimeException("Unable to extract pacakge.", e);
		}
		VraNgPackageDescriptor vraPackageDescriptor = VraNgPackageDescriptor
				.getInstance(new File(tmp.toPath().toString() + "/content.yaml"));
		VraNgTypeStoreFactory storeFactory = VraNgTypeStoreFactory.withConfig(restClient, vraNgPackage, config,
				vraPackageDescriptor);
		for (VraNgPackageContent.ContentType type : VraNgTypeStoreFactory.getImportOrder()) {
			logger.info("Currently importing: {}", type.getTypeValue());
			storeFactory.getStoreForType(type).importContent(tmp);
		}
		return vraNgPackage;
	}

	/**
	 * Deletes a package.
	 * 
	 * @param pkg         the package to delete
	 * @param withContent whether to delete the package with its content
	 * @param dryrun      whether it should be dry run
	 * @return the deleted package
	 */
	@Override
	protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
		throw new UnsupportedOperationException(
				"deletePackage: Cloud Automation Services does not provide native support for packages.");
	}

	/**
	 * Deletes a package.
	 * 
	 * @param pkg         the package to delete
	 * @param lastVersion whether it should delete the last version
	 * @param oldVersions whether it should delete the old versions
	 * @param dryrun      whether it should be dry run
	 * @return the deleted package
	 */
	@Override
	public final List<Package> deletePackage(final Package pkg, final boolean lastVersion, final boolean oldVersions,
			final boolean dryrun) {
		throw new UnsupportedOperationException(
				"deletePackage(List): Cloud Automation Services does not provide native support for packages.");
	}

	/**
	 *
	 * @param pkg the package which content to get
	 * @return the content of the package
	 */
	@Override
	protected final VraNgPackageContent getPackageContent(final Package pkg) {
		throw new UnsupportedOperationException(
				"Cloud Automation Services does not provide native support for packages.");
	}

	/**
	 * Deletes content.
	 * 
	 * @param content the content to delete
	 * @param dryrun  whether it should be dry dun
	 */
	@Override
	protected final void deleteContent(final Content content, final boolean dryrun) {
		throw new UnsupportedOperationException(
				"deleteContent: Cloud Automation Services does not provide native support for packages.");
	}
}
