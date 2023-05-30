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
import java.util.stream.Collectors;

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
    private final Logger logger = LoggerFactory.getLogger(VroPackageStore.class);

    private final RestClientVro restClient;
    private final List<Strategy> strategies;
    private final List<PackageStoreExtention<VroPackageDescriptor>> extentions;

    protected VroPackageStore(RestClientVro restClient, List<Strategy> strategies, List<PackageStoreExtention<VroPackageDescriptor>> extentions) {
        this.restClient = restClient;
        this.strategies = strategies;
        this.extentions = extentions;
    }

    protected VroPackageStore(RestClientVro restClient, List<Strategy> strategies, List<PackageStoreExtention<VroPackageDescriptor>> extentions, Version productVersion) {
        this.restClient = restClient;
        this.strategies = strategies;
        this.extentions = extentions;
        super.setProductVersion(productVersion);
    }

    @Override
    public List<Package> getPackages() {
        List<Package> pkgs = restClient.getPackages();
        for(Package pkg : pkgs){
            logger.trace(String.format(PackageStore.PACKAGE_LIST, pkg));
        }
        return pkgs;
    }

    @Override
    public List<Package> exportAllPackages(List<Package> vroPackages, boolean dryrun) {
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
        for(Package pkg : sourceEndpointPackages){
            exportedPackages.add(this.exportPackage(pkg, dryrun));
        }

        return exportedPackages;
    }

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg, dryrun,false);
	}

	@Override
    public List<Package> importAllPackages(List<Package> vroPackages, boolean dryrun, boolean mergePackages) {
        this.validateFilesystem(vroPackages);

        List<Package> sourceEndpointPackages = vroPackages;
        List<Package> destinationEndpointPackages = restClient.getPackages();
        for (Strategy strategy : strategies) {
            sourceEndpointPackages = strategy.getImportPackages(sourceEndpointPackages, destinationEndpointPackages);
        }
        if (sourceEndpointPackages.isEmpty()) {
            return new ArrayList<>();
        }

        List<Package> importedPackages = new ArrayList<>();
        for(Package pkg : sourceEndpointPackages){
            importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
        }

        return importedPackages;
    }

    @Override
    public Package exportPackage(Package vraPackage, boolean dryrun) {
        logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));
        Package pkg = restClient.exportPackage(vraPackage, dryrun);
        for(PackageStoreExtention<VroPackageDescriptor> e : extentions) {
            e.exportPackage(pkg, null, dryrun);
        }
        return pkg;
    }

    @Override
	public Package exportPackage(Package vraPackage, VroPackageDescriptor vroPackageDescriptor, boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));
		Package pkg = restClient.exportPackage(vraPackage, dryrun);
		for(PackageStoreExtention<VroPackageDescriptor> e : extentions) {
		    e.exportPackage(pkg, vroPackageDescriptor, dryrun);
		}

		return pkg;
	}

    @Override
    public Package importPackage(Package vraPackage, boolean dryrun, boolean mergePackages) {
		if(mergePackages)
			logger.info(String.format(PackageStore.PACKAGE_MERGE, vraPackage));
        else
			logger.info(String.format(PackageStore.PACKAGE_IMPORT, vraPackage));

        logger.debug(String.format("System path: %s",vraPackage.getFilesystemPath()));
		Package pkg;

		try {
			pkg = restClient.importPackage(vraPackage, dryrun, mergePackages);
		}
		catch ( HttpClientErrorException e ) {
			if (e.getMessage().contains("Unknown object type 'Module' caused by: Unknown object type 'Module'")) {
				throw new RuntimeException("One of the actions that we tried to import contains a path that is too long.",e);
			}

			throw e;
		}

        for(PackageStoreExtention<VroPackageDescriptor> e : extentions) {
            e.importPackage(pkg, dryrun);
        }

        return pkg;
    }

    @Override
    public Package exportPackage(Package vroPackage, File vroPackageDescriptor, boolean dryrun) {
        throw new UnsupportedOperationException("Not supported operation in vRO");
    }

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        return restClient.deletePackage(pkg, withContent, dryrun);
    }

    @Override
    protected PackageContent getPackageContent(Package pkg) {
        return restClient.getPackageContent(pkg);
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        restClient.deleteContent(content, dryrun);
    }

}
