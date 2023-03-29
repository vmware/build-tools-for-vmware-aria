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

	private final Logger logger = LoggerFactory.getLogger(VraPackageStore.class);

	private final RestClientVra restClient;
	private final List<Strategy> strategies;
	private final List<PackageStoreExtention<VraPackageDescriptor>> extentions;

	protected VraPackageStore(RestClientVra restClient, List<Strategy> strategies, List<PackageStoreExtention<VraPackageDescriptor>> extentions) {
		this.restClient = restClient;
		this.strategies = strategies;
		this.extentions = extentions;
	}
	
    protected VraPackageStore(RestClientVra restClient, List<Strategy> strategies, List<PackageStoreExtention<VraPackageDescriptor>> extentions, Version productVersion) {
        this.restClient = restClient;
        this.strategies = strategies;
        this.extentions = extentions;
        super.setProductVersion(productVersion);
    }	

	@Override
	public List<Package> getPackages() {
		List<Package> pkgs = restClient.getPackages();
		for (Package pkg : pkgs) {
			logger.info(String.format(PackageStore.PACKAGE_LIST, pkg));
		}
		return pkgs;
	}

	@Override
	public List<Package> exportAllPackages(List<Package> vraPackages, boolean dryrun) {
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

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun, boolean enableBackup) {
		return this.importAllPackages(pkg,dryrun,false, enableBackup);
	}

	@Override
	public List<Package> importAllPackages(List<Package> vraPackages, boolean dryrun, boolean mergePackages, boolean enableBackup) {
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

	@Override
	public Package exportPackage(Package vraPackage, boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));
		Package pkg = restClient.exportPackage(vraPackage, dryrun);
		for(PackageStoreExtention<VraPackageDescriptor> e : extentions) {
		    e.exportPackage(pkg, null, dryrun);
		}
		return pkg;
	}

	@Override
	public Package exportPackage(Package vraPackage, VraPackageDescriptor vraPackageDescriptor, boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, vraPackage));	
		if (vraPackageDescriptor.hasNativeContent()) {
			vraPackage = restClient.exportPackage(vraPackage, dryrun);
		}

		for (PackageStoreExtention<VraPackageDescriptor> e : extentions) {
		    e.exportPackage(vraPackage, vraPackageDescriptor, dryrun);
		}

		return vraPackage;
	}

	@Override
	public Package importPackage(Package vraPackage, boolean dryrun, boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, vraPackage));

		boolean hasNativeContent;
		try {
			hasNativeContent = new PackageManager(vraPackage).getAllFiles().stream().anyMatch(f -> f.equals("metadata.yaml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		if (hasNativeContent) {
			vraPackage = restClient.importPackage(vraPackage, dryrun);
		}

		for(PackageStoreExtention<VraPackageDescriptor> e : extentions) {
            e.importPackage(vraPackage, dryrun);
        }
        return vraPackage;
	}

	@Override
	public Package exportPackage(Package vraPackage, File vraPackageDescriptorFile, boolean dryrun) {
		VraPackageDescriptor vraPackageDescriptor = VraPackageDescriptor.getInstance(vraPackageDescriptorFile);
		if (vraPackageDescriptor.hasNativeContent()) {
			try {
				restClient.createPackage(vraPackage, vraPackageDescriptor);
			} catch (URISyntaxException e) {
				throw new RuntimeException("Cannot create vRA pacakge with provided descriptor.", e);
			}
		}
		return this.exportPackage(vraPackage, vraPackageDescriptor, dryrun);
	}

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        return restClient.deletePackage(pkg, withContent, dryrun);
    }

    @Override
    protected VraPackageContent getPackageContent(Package pkg) {
        return restClient.getPackageContentPrimitive(pkg);
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        restClient.deleteContentPrimitive(content, dryrun);
    }

}
