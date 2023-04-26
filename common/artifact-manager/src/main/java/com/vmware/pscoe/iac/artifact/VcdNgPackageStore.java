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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.vcd.VcdPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVcd;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;

public class VcdNgPackageStore extends GenericPackageStore<VcdPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
	private final Logger logger = LoggerFactory.getLogger(VcdNgPackageStore.class);

	/**
	 * The vcd rest client.
	 */
	private final RestClientVcd restClient;

	/**
	 * The vcd package store strategies.
	 */
	private final List<Strategy> strategies;

	/**
	 *
	 * @param vcdRestClient the vcd rest client
	 * @param vcdStrategies the vcd package store strategies
	 */
	protected VcdNgPackageStore(final RestClientVcd vcdRestClient, final List<Strategy> vcdStrategies) {
		this.restClient = vcdRestClient;
		this.strategies = vcdStrategies;
	}

	/**
	 *
	 * @param vcdRestClient the vcd rest client
	 * @param vcdStrategies the vcd package store strategies
	 * @param vcdProductVersion the vcd product version
	 */
    protected VcdNgPackageStore(final RestClientVcd vcdRestClient, final List<Strategy> vcdStrategies, final Version vcdProductVersion) {
        this.restClient = vcdRestClient;
        this.strategies = vcdStrategies;
        super.setProductVersion(vcdProductVersion);
    }

	/**
	 * Gets the packages.
	 * @return the extracted vcd packages
	 */
	@Override
	public final List<Package> getPackages() {
		List<Package> pkgs = restClient.getAllUiExtensions();

		for (Package pkg : pkgs) {
			logger.info(String.format(PackageStore.PACKAGE_LIST, pkg));
		}

		return pkgs;
	}

	/**
	 * Exports all packages.
	 * @param pkg the packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> pkg, final boolean dryrun) {
		throw new RuntimeException("Package export is not supported");
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
	 * @param pkgs the packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkgs, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
		if (dryrun) {
			throw new UnsupportedOperationException("dryrun option not supported");
		}

		List<Package> sourceEndpointPackages = pkgs;
		List<Package> destinationEndpointPackages = restClient.getAllUiExtensions();
		for (Strategy strategy : strategies) {
			sourceEndpointPackages = strategy.getImportPackages(sourceEndpointPackages, destinationEndpointPackages);
		}

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
	 * @param pkg the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package pkg, final boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param exportDescriptor the descriptor of the package to export
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package pkg, final File exportDescriptor, final boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param vraPackageDescriptor the package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package pkg, final VcdPackageDescriptor vraPackageDescriptor, final boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	/**
	 * Imports a package.
	 * @param pkg the package to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @return the imported package
	 */
	@Override
	public final Package importPackage(final Package pkg, final boolean dryrun, final boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, pkg));
		if (dryrun) {
			throw new RuntimeException("dryrun option not supported");
		}

		return restClient.addOrReplaceUiPlugin(pkg);
	}

	/**
	 * Deletes a package.
	 * @param pkg
	 * @param withContent
	 * @param dryrun
	 * @return the deleted package
	 */
	@Override
	protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
		if (dryrun) {
			throw new RuntimeException("dryrun option not supported");
		}
		Package remotePkg = restClient.getUiExtension(pkg);
		restClient.deleteUiPlugin(remotePkg);
		restClient.removeUiExtension(remotePkg);
		return remotePkg;
	}

	/**
	 * Gets package content.
	 * @param pkg
	 * @return the packate content
	 */
	@Override
	protected final PackageContent getPackageContent(final Package pkg) {
		throw new UnsupportedOperationException("Parsing package content is not supported");
	}

	/**
	 * Deletes package content.
	 * @param content
	 * @param dryrun
	 */
	@Override
	protected final void deleteContent(final Content content, final boolean dryrun) {
		throw new UnsupportedOperationException("Parsing package content is not supported");
	}
}
