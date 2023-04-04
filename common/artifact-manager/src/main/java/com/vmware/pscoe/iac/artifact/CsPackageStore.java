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

	private final Logger logger = LoggerFactory.getLogger(CsPackageStore.class);

	private final RestClientCs restClient;

	private final ConfigurationCs config;

	protected CsPackageStore(RestClientCs restClient, ConfigurationCs config) {
		this.restClient = restClient;
		this.config = config;
	}

	@Override
	public List<Package> getPackages() {
		throw new UnsupportedOperationException("getPackages: Code Stream Services does not provide native support for packages.");
	}

	@Override
	public List<Package> exportAllPackages(List<Package> csPackages, boolean dryrun) {
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

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun,false);
	}

	@Override
	public List<Package> importAllPackages(List<Package> csPackages, boolean dryrun, boolean mergePackages) {
		this.validateFilesystem(csPackages);

		List<Package> sourceEndpointPackages = csPackages;
		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> importedPackages = new ArrayList<>();
		for (Package pkg : sourceEndpointPackages) {
			importedPackages.add(this.importPackage(pkg, dryrun,mergePackages));
		}

		return importedPackages;
	}

	@Override
	public Package exportPackage(Package csPackage, boolean dryrun) {
		CsPackageDescriptor csPackageDescriptor = CsPackageDescriptor
				.getInstance(new File(csPackage.getFilesystemPath()));
		return this.exportPackage(csPackage, csPackageDescriptor, dryrun);
	}

	@Override
	public Package exportPackage(Package csPackage, CsPackageDescriptor csPackageDescriptor, boolean dryrun) {
		logger.info(String.format(PackageStore.PACKAGE_EXPORT, csPackage));
		CsTypeStoreFactory storeFactory = CsTypeStoreFactory.withConfig(restClient, csPackage, config, csPackageDescriptor);
		for (CsPackageContent.ContentType type : CsTypeStoreFactory.EXPORT_ORDER) {
			logger.info("EXPORTING: {}", type.getTypeValue());
			storeFactory.getStoreForType(type).exportContent();
		}
		return csPackage;
	}

	@Override
	public Package importPackage(Package csPackage, boolean dryrun, boolean mergePackages) {
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

	@Override
	public Package exportPackage(Package csPackage, File csPackageDescriptorFile, boolean dryrun) {
		CsPackageDescriptor csPackageDescriptor = CsPackageDescriptor.getInstance(csPackageDescriptorFile);
		return this.exportPackage(csPackage, csPackageDescriptor, dryrun);
	}

	@Override
	protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
		throw new UnsupportedOperationException("deletePackage: Code Stream Services does not provide native support for packages.");
	}

	@Override
	protected CsPackageContent getPackageContent(Package pkg) {
		throw new UnsupportedOperationException("getPackageContent: Code Stream Services does not provide native support for packages.");
	}

	@Override
	protected void deleteContent(Content content, boolean dryrun) {
		throw new UnsupportedOperationException("deleteContent: Code Stream Services does not provide native support for packages.");
	}
}
