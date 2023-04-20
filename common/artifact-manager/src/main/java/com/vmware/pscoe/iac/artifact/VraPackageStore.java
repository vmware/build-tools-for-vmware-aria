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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.extentions.PackageStoreExtention;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageContent;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;

public class VraPackageStore extends GenericPackageStore<VraPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
	private final Logger logger = LoggerFactory.getLogger(VraPackageStore.class);

	/**
	 * The vRA rest client.
	 */
	private final RestClientVra restClient;

	/**
	 * The vRA package store strategies.
	 */
	private final List<Strategy> strategies;

	/**
	 * The vRA package store extensions.
	 */
	private final List<PackageStoreExtention<VraPackageDescriptor>> extentions;

	/**
	 *
	 * @param vraRestClient the vRA rest client
	 * @param vraStrategies the vRA strategies
	 * @param vraExtentions the vRA extensions
	 */
	protected VraPackageStore(final RestClientVra vraRestClient, final List<Strategy> vraStrategies, final List<PackageStoreExtention<VraPackageDescriptor>> vraExtentions) {
		this.restClient = vraRestClient;
		this.strategies = vraStrategies;
		this.extentions = vraExtentions;
	}

	/**
	 *
	 * @param vraRestClient the vRA rest client
	 * @param vraStrategies the vRA strategies
	 * @param vraExtentions the vRA extensions
	 * @param vraProductVersion the vRA product version
	 */
    protected VraPackageStore(final RestClientVra vraRestClient, final List<Strategy> vraStrategies, final List<PackageStoreExtention<VraPackageDescriptor>> vraExtentions, final Version vraProductVersion) {
        this.restClient = vraRestClient;
        this.strategies = vraStrategies;
        this.extentions = vraExtentions;
        super.setProductVersion(vraProductVersion);
    }

	/**
	 * Gets the packages.
	 * @return the extracted vRA packages
	 */
	@Override
	public List<Package> getPackages() {
		List<Package> pkgs = restClient.getPackages();
		for (Package pkg : pkgs) {
			logger.info(String.format(PackageStore.PACKAGE_LIST, pkg));
		}
		return pkgs;
	}

	/**
	 * Exports all packages.
	 * @param vraPackages the packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> vraPackages, final boolean dryrun) {
		this.vlidateServer(vraPackages);

		List<Package> sourceEndpointPackages = vraPackages;
		List<Package> destinationEndpointPackages = vraPackages.stream()
				.filter(pkg -> new File(pkg.getFilesystemPath()).exists()).collect(Collectors.toList());
		for (Strategy strategy : strategies) {
			sourceEndpointPackages = strategy.getExportPackages(sourceEndpointPackages, destinationEndpointPackages);
		}

		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : vraPackages) {
			VraPackageDescriptor vraPackageDescriptor = VraPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));
			exportedPackages.add(this.exportPackage(pkg, vraPackageDescriptor, dryrun));
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
	 * @param vraPackages the packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> vraPackages, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
		this.validateFilesystem(vraPackages);

		List<Package> sourceEndpointPackages = vraPackages;
		List<Package> destinationEndpointPackages = restClient.getPackages();
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
	 * @param vraPackage the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package vraPackage, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));
		Package pkg = restClient.exportPackage(vraPackage, dryrun);
		for (PackageStoreExtention<VraPackageDescriptor> e : extentions) {
		    e.exportPackage(pkg, null, dryrun);
		}

		return pkg;
	}

	/**
	 * Exports a package.
	 * @param vraPackage the package to export
	 * @param vraPackageDescriptor the package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package vraPackage, final VraPackageDescriptor vraPackageDescriptor, final boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));
		Package resultVraPackage = vraPackage;

		if (vraPackageDescriptor.hasNativeContent()) {
			resultVraPackage = restClient.exportPackage(resultVraPackage, dryrun);
		}

		for (PackageStoreExtention<VraPackageDescriptor> e : extentions) {
		    e.exportPackage(resultVraPackage, vraPackageDescriptor, dryrun);
		}

		return resultVraPackage;
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
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, vraPackage));
		Package resultVraPackage = vraPackage;

		boolean hasNativeContent;
		try {
			hasNativeContent = new PackageManager(resultVraPackage).getAllFiles().stream().anyMatch(f -> f.equals("metadata.yaml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (hasNativeContent) {
			resultVraPackage = restClient.importPackage(resultVraPackage, dryrun);
		}

		for (PackageStoreExtention<VraPackageDescriptor> e : extentions) {
            e.importPackage(resultVraPackage, dryrun);
        }

        return resultVraPackage;
	}

	/**
	 * Exports a package.
	 * @param vraPackage the package to export
	 * @param vraPackageDescriptorFile the descriptor of the package to export
	 * @param dryrun whether it should be dry run
	 * @return
	 */
	@Override
	public final Package exportPackage(final Package vraPackage, final File vraPackageDescriptorFile, final boolean dryrun) {
		VraPackageDescriptor vraPackageDescriptor = VraPackageDescriptor.getInstance(vraPackageDescriptorFile);
		if (vraPackageDescriptor.hasNativeContent()) {
			try {
				restClient.createPackage(vraPackage, vraPackageDescriptor);
			} catch (URISyntaxException e) {
				throw new RuntimeException("Cannot create vRA package with provided descriptor.", e);
			}
		}

		return this.exportPackage(vraPackage, vraPackageDescriptor, dryrun);
	}

	/**
	 * Deletes a package.
	 * @param pkg the package to delete
	 * @param withContent whether to delete the package with its content
	 * @param dryrun whether it should be dry run
	 * @return the deleted package
	 */
	@Override
    protected Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
        return restClient.deletePackage(pkg, withContent, dryrun);
    }

	/**
	 * Gets the vRA package content.
	 * @param pkg the package which content to get
	 * @return the package content
	 */
	@Override
    protected final VraPackageContent getPackageContent(final Package pkg) {
        return restClient.getPackageContentPrimitive(pkg);
    }

	/**
	 * Deletes conent.
	 * @param content the conent to delete
	 * @param dryrun whether it should be dry run
	 */
	@Override
    protected final void deleteContent(final Content content, final boolean dryrun) {
        restClient.deleteContentPrimitive(content, dryrun);
    }
}
