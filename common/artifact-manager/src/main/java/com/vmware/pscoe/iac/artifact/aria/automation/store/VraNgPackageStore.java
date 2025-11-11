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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageAdapter;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageContent;
import com.vmware.pscoe.iac.artifact.aria.automation.store.models.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.GenericPackageStore;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent.Content;

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
	public VraNgPackageStore(final RestClientVraNg vraRestClient, final ConfigurationVraNg vraConfig) {
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
			// No delay configured, proceed immediately, as no delay is desired (most likely
			// vRA doesn't expect vRO payloads)
			return;
		}

		try {
			// before proceeding with sleeping, try to force data collection through API
			this.triggerDataCollection();

			// if data collection running is fine, proceed with sleeping for certain period
			// so that changes to be reflected in vRA
		} catch (Exception e) {
			logger.error(
					"Unable to trigger VCFA Orchestrator data collection. Proceeding with old sleeping mechanism, waiting for vrang.data.collection.delay.seconds: {}",
					collectionDelayRaw, e);
		}

		try {
			int collectionDelay = Integer.parseInt(collectionDelayRaw);
			if (collectionDelay > 0) {
				logger.info(
						"Waiting additional {} seconds for the VCFA Orchestrator data collection so that VCFA Orchestrator data is up to date. This is configurable with vrang.data.collection.delay.seconds property",
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

	private void triggerDataCollection() {
		logger.info("Triggering VCFA Orchestrator data collection through vRA API");
		this.restClient.triggerVroDataCollection();
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
			throw new RuntimeException("Unable to extract package.", e);
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

	// =================================================== Full Delete

	/**
	 * Fully deletes a package.
	 * 
	 * @param pkg         the package to delete
	 * @param withContent whether to delete the package with its content
	 * @param dryrun      whether it should be dry run
	 * @return the deleted package
	 */
	@Override
	protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
		VraNgPackageAdapter adapter = new VraNgPackageAdapter(pkg);

		VraNgPackageDescriptor descriptor;

		try {
			descriptor = adapter.getDescriptor();
		} catch (IOException e) {
			throw new RuntimeException("Unable to get descriptor for package " + pkg.getFQName(), e);
		}

		VraNgTypeStoreFactory storeFactory = VraNgTypeStoreFactory.withConfig(restClient, pkg, config, descriptor);

		for (VraNgPackageContent.ContentType type : VraNgTypeStoreFactory.getDeleteOrder()) {
			logger.info("Currently deleting: {}", type.getTypeValue());
			storeFactory.getStoreForType(type).deleteContent();
		}

		return pkg;
	}

	/**
	 * Deletes a package.
	 *
	 * This is a direct call to `deletePackage`. Normally as it stands, versioning
	 * is not supported in Aria Automation.
	 * This function may not even be callable with the current logic as
	 * `getPackageContent` is called before `deletePackage`, but leaving it as is
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
		return Collections.singletonList(this.deletePackage(pkg, true, dryrun));
	}

	// ================================================== Partial Deletes

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
				"deleteContent: Cloud Automation Services does not provide native support for packages, content cannot be deleted");
	}
}
