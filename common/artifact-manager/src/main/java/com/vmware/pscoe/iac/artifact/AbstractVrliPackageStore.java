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

import com.vmware.pscoe.iac.artifact.GenericPackageStore;
import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.PackageStore;
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
	protected static final String DIR_ALERTS = "alerts";
	private static final String DIR_CONTENT_PACKS = "content_packs";

	protected Logger logger;

	@Override
	public Package exportPackage(Package pkg, VrliPackageDescriptor vrliPackageDescriptor, boolean dryrun) {
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
	@Override
	public List<Package> exportAllPackages(List<Package> pkgs, boolean dryrun) {
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

	@Override
	public List<Package> importAllPackages(List<Package> pkgs, boolean dryrun, boolean mergePackages) {
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
	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun,false);
	}

	@Override
	public Package exportPackage(Package pkg, boolean dryrun) {
		VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(new File(pkg.getFilesystemPath()));

		return this.exportPackage(pkg, descriptor, dryrun);
	}

	@Override
	public Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun) {
		VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(exportDescriptor);

		return this.exportPackage(pkg, descriptor, dryrun);
	}

	@Override
	public Package importPackage(Package pkg, boolean dryrun, boolean mergePackages) {
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
	protected void importAlerts(File tmp) {
		File alertsDirectory = Paths.get(tmp.getPath(), DIR_ALERTS).toFile();

		if (alertsDirectory.exists()) {
			FileUtils.listFiles(alertsDirectory, new String[] { "json" }, false).stream().forEach(this::importAlert);
		}
	}

	protected void importContentPacks(File tmp) {
		File contentPacksDirectory = Paths.get(tmp.getPath(), DIR_CONTENT_PACKS).toFile();

		if (contentPacksDirectory.exists()) {
			FileUtils.listFiles(contentPacksDirectory, new String[] { "json" }, false).stream().forEach(this::importContentPack);
		}
	}

	@Override
	public List<Package> getPackages() {
		throw new RuntimeException("VRLI does not support packages");
	}

	@Override
	protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
		throw new RuntimeException("Deleting packages is not supported");
	}

	@Override
	protected PackageContent getPackageContent(Package pkg) {
		throw new RuntimeException("Parsing package content is not supported");
	}

	@Override
	protected void deleteContent(PackageContent.Content content, boolean dryrun) {
		throw new RuntimeException("Delete content is not supported");
	}

	protected File exportContentPack(Package vrliPakage, String contentPackName, String contentPackData) {
		File store = new File(vrliPakage.getFilesystemPath());
		File contentPacksFile = Paths.get(store.getPath(), DIR_CONTENT_PACKS, contentPackName + ".json").toFile();
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
	protected abstract void importAlert(File alertFile);
	protected abstract void importContentPack(File contentPackFile);
	protected abstract void exportAlerts(Package vrliPakage, List<String> alertNames);
	protected abstract void exportContentPacks(Package vrliPakage, List<String> contentPackNames);
}
