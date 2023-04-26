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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageContent;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.store.cs.CsTypeStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsPackageStore extends GenericPackageStore<CsPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
	private final Logger logger = LoggerFactory.getLogger(CsPackageStore.class);

	/**
	 * The CSA rest client.
	 */
	private final RestClientCs restClient;

	/**
	 * The CS configuration.
	 */
	private final ConfigurationCs config;

	/**
	 *
	 * @param csRestClient
	 * @param csConfig
	 */
	protected CsPackageStore(final RestClientCs csRestClient, final ConfigurationCs csConfig) {
		this.restClient = csRestClient;
		this.config = csConfig;
	}

	/**
	 * Returns the packages.
	 * @return the list of packages to return
	 */
	@Override
	public final List<Package> getPackages() {
		throw new UnsupportedOperationException("getPackages: Code Stream Services does not provide native support for packages.");
	}

	/**
	 * Exports all the packages.
	 * @param csPackages the cs packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> csPackages, final boolean dryrun) {
		this.vlidateServer(csPackages);

		List<Package> sourceEndpointPackages = csPackages;

		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : csPackages) {
			CsPackageDescriptor csPackageDescriptor = CsPackageDescriptor
					.getInstance(new File(pkg.getFilesystemPath()));
			exportedPackages.add(this.exportPackage(pkg, csPackageDescriptor, dryrun));
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
	 * @param csPackages the packages to import
	 * @param dryrun whether it should be a dry dun
	 * @param mergePackages whether the packages should be merged
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> csPackages, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
		this.validateFilesystem(csPackages);

		List<Package> sourceEndpointPackages = csPackages;
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
	 * Export a cs package.
	 * @param csPackage the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package csPackage, final boolean dryrun) {
		CsPackageDescriptor csPackageDescriptor = CsPackageDescriptor
				.getInstance(new File(csPackage.getFilesystemPath()));
		return this.exportPackage(csPackage, csPackageDescriptor, dryrun);
	}

	/**
	 * Exports a cs package.
	 * @param csPackage the package to export
	 * @param csPackageDescriptor the package descriptor of the package to be exported
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package csPackage, final CsPackageDescriptor csPackageDescriptor, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, csPackage));
		CsTypeStoreFactory storeFactory = CsTypeStoreFactory.withConfig(restClient, csPackage, config, csPackageDescriptor);
		for (CsPackageContent.ContentType type : CsTypeStoreFactory.EXPORT_ORDER) {
			logger.info("EXPORTING: {}", type.getTypeValue());
			storeFactory.getStoreForType(type).exportContent();
		}

		return csPackage;
	}

	/**
	 * Imports a cs package.
	 * @param csPackage the cs package to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether it should merge the packages
	 * @return the package to be imported
	 */
	@Override
	public final Package importPackage(final Package csPackage, final boolean dryrun, final boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, csPackage));

		File tmp;
		try {
			tmp = Files.createTempDirectory("iac-package-import").toFile();
			logger.info("Created temp dir {}", tmp.getAbsolutePath());
			new PackageManager(csPackage).unpack(tmp);
		} catch (IOException e) {
			logger.error("Unable to extract package '{}' in temporary directory.", csPackage.getFQName());
			throw new RuntimeException("Unable to extract pacakge.", e);
		}

		CsTypeStoreFactory storeFactory = CsTypeStoreFactory.withConfig(restClient, csPackage, config, null);
		for (CsPackageContent.ContentType type : CsTypeStoreFactory.IMPORT_ORDER) {
			logger.info("IMPORTING : {}", type.getTypeValue());
			storeFactory.getStoreForType(type).importContent(tmp);
		}

		return csPackage;
	}

	/**
	 * Exports a package.
	 * @param csPackage the cs package to import
	 * @param csPackageDescriptorFile the descriptor file of the package to be exported
	 * @param dryrun whether it should be dry run
	 * @return the package to be exported
	 */
	@Override
	public final Package exportPackage(final Package csPackage, final File csPackageDescriptorFile, final boolean dryrun) {
		CsPackageDescriptor csPackageDescriptor = CsPackageDescriptor.getInstance(csPackageDescriptorFile);

		return this.exportPackage(csPackage, csPackageDescriptor, dryrun);
	}

	/**
	 * Deletes a package.
	 * @param pkg the package to be deleted
	 * @param withContent whether it should delete the packate without the content
	 * @param dryrun whether it should be dry run
	 * @return the package to be deleted
	 */
	@Override
	protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
		throw new UnsupportedOperationException("deletePackage: Code Stream Services does not provide native support for packages.");
	}

	/**
	 * Gets package content.
	 * @param pkg the package to get the content from
	 * @return the cs package content
	 */
	@Override
	protected final CsPackageContent getPackageContent(final Package pkg) {
		throw new UnsupportedOperationException("getPackageContent: Code Stream Services does not provide native support for packages.");
	}

	/**
	 * Deleted content.
	 * @param content the content to be deleted
	 * @param dryrun whether it should be dry run
	 */
	@Override
	protected final void deleteContent(final Content content, final boolean dryrun) {
		throw new UnsupportedOperationException("deleteContent: Code Stream Services does not provide native support for packages.");
	}
}
