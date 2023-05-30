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
	private final Logger logger = LoggerFactory.getLogger(VcdNgPackageStore.class);

	private final RestClientVcd restClient;
	private final List<Strategy> strategies;

	protected VcdNgPackageStore(RestClientVcd restClient, List<Strategy> strategies) {
		this.restClient = restClient;
		this.strategies = strategies;
	}

    protected VcdNgPackageStore(RestClientVcd restClient, List<Strategy> strategies, Version productVersion) {
        this.restClient = restClient;
        this.strategies = strategies;
        super.setProductVersion(productVersion);
    }

	@Override
	public List<Package> getPackages() {
		List<Package> pkgs = restClient.getAllUiExtensions();

		for (Package pkg : pkgs) {
			logger.info(String.format(PackageStore.PACKAGE_LIST, pkg));
		}

		return pkgs;
	}

	@Override
	public List<Package> exportAllPackages(List<Package> pkg, boolean dryrun) {
		throw new RuntimeException("Package export is not supported");
	}

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun,false);
	}

	@Override
	public List<Package> importAllPackages(List<Package> pkgs, boolean dryrun, boolean mergePackages) {
		if(dryrun) {
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

	@Override
	public Package exportPackage(Package pkg, boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	@Override
	public Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	@Override
	public Package exportPackage(Package pkg, VcdPackageDescriptor vraPackageDescriptor, boolean dryrun) {
		throw new UnsupportedOperationException("Package export is not supported");
	}

	@Override
	public Package importPackage(Package pkg, boolean dryrun, boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, pkg));
		if(dryrun) {
			throw new RuntimeException("dryrun option not supported");
		}

		return restClient.addOrReplaceUiPlugin(pkg);
	}

	@Override
	protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
		if(dryrun) {
			throw new RuntimeException("dryrun option not supported");
		}
		Package remotePkg = restClient.getUiExtension(pkg);
		restClient.deleteUiPlugin(remotePkg);
		restClient.removeUiExtension(remotePkg);
		return remotePkg;
	}

	@Override
	protected PackageContent getPackageContent(Package pkg) {
		throw new UnsupportedOperationException("Parsing package content is not supported");
	}

	@Override
	protected void deleteContent(Content content, boolean dryrun) {
		throw new UnsupportedOperationException("Parsing package content is not supported");
	}


}
