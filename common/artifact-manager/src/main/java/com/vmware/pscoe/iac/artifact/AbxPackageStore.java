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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationAbx;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.abx.AbxConstant;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageContent;
import com.vmware.pscoe.iac.artifact.model.abx.AbxPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgSecret;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbxPackageStore extends GenericPackageStore<AbxPackageDescriptor> {
	/**
	 * Variable for logging.
	 */
	private final Logger logger = LoggerFactory.getLogger(AbxPackageStore.class);

	/**
	 * The vRA rest client.
	 */
	private final RestClientVraNg restClient;

	/**
	 * The abx configuration.
	 */
	private final ConfigurationAbx config;

	/**
	 *
	 * @param restClientVraNg vRA Rest Client
	 * @param abxConfig abx configuration
	 */
	protected AbxPackageStore(final RestClientVraNg restClientVraNg, final ConfigurationAbx abxConfig) {
		this.restClient = restClientVraNg;
		this.config = abxConfig;
	}

	/**
	 *
	 * @param pkg package to get content from
	 * @return abx package content to return
	 */
	@Override
    protected final AbxPackageContent getPackageContent(final Package pkg) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 *
	 * @return received packages
	 */
    @Override
    public final List<Package> getPackages() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 *
	 * @param pkg package to delete
	 * @param withContent whether to delete it with content
	 * @param dryrun whether it should be dry run
	 * @return the deleted package
	 */
	@Override
    protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
        throw new NotImplementedException("Not implemented");
    }

	/**
	 *
	 * @param content the content to delete
	 * @param dryrun whether it should be dry run
	 */
	@Override
    protected final void deleteContent(final Content content, final boolean dryrun) {
        throw new NotImplementedException("Not implemented");
    }

	/**
	 *
	 * @param abxPackages the axb packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
	@Override
	public final List<Package> exportAllPackages(final List<Package> abxPackages, final boolean dryrun) {
		this.vlidateServer(abxPackages);

		List<Package> sourceEndpointPackages = abxPackages;

		if (sourceEndpointPackages.isEmpty()) {
			return new ArrayList<>();
		}

		List<Package> exportedPackages = new ArrayList<>();
		for (Package pkg : abxPackages) {
			AbxPackageDescriptor abxPackageDescriptor = AbxPackageDescriptor
					.getInstance(new File(pkg.getFilesystemPath()).getParentFile());
			exportedPackages.add(this.exportPackage(pkg, abxPackageDescriptor, dryrun));
		}

		return exportedPackages;
	}

	/**
	 *
	 * @param pkg the packages to import
	 * @param dryrun whether it should be dry run
	 * @param enableBackup whether to enable backup of package import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkg, final boolean dryrun, final boolean enableBackup) {
		return this.importAllPackages(pkg, dryrun, false, enableBackup);
	}

	/**
	 *
	 * @param abxPackages the abx packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup whether to enable backup of package import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> abxPackages, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
		this.validateFilesystem(abxPackages);

		List<Package> sourceEndpointPackages = abxPackages;
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
	 *
	 * @param abxPackage the abx package to export
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package abxPackage, final boolean dryrun) {
	    File abxPackageFile = new File(abxPackage.getFilesystemPath());
		return this.exportPackage(abxPackage, abxPackageFile, dryrun);
	}

	/**
	 *
	 * @param abxPackage the abx package to export
	 * @param abxPackageDescriptorParent the parent of the abx package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
	@Override
	public final Package exportPackage(final Package abxPackage, final File abxPackageDescriptorParent, final boolean dryrun) {
		AbxPackageDescriptor abxPackageDescriptor = AbxPackageDescriptor.getInstance(abxPackageDescriptorParent);
        return exportPackage(abxPackage, abxPackageDescriptor, dryrun);
	}

    /**
     * Main handler for exporting abx package based on package.json file.
     * @param pkg abx package
     * @param packageDescriptor abx package descriptor file
     * @param dryrun whether it should be dry run
     * @return package
     */
    @Override
    public final Package exportPackage(final Package pkg, final AbxPackageDescriptor packageDescriptor, final boolean dryrun) {
        logger.info(String.format(PackageStore.PACKAGE_EXPORT, pkg));

		logger.warn("ABX content pull is not supported yet");

        return pkg;
    }

	/**
	 * Main handler for importing abx package.
	 * @param abxPackage ABX package
	 * @param dryrun dryrun flag
	 * @return package
	 */
	@Override
	public final Package importPackage(final Package abxPackage, final boolean dryrun, final boolean mergePackages) {
		logger.info(String.format(PackageStore.PACKAGE_IMPORT, abxPackage));

		File tmp;
		try {
			tmp = Files.createTempDirectory("iac-package-import").toFile();
            logger.info("Created temp dir {}", tmp.getAbsolutePath());
			new PackageManager(abxPackage).unpack(tmp);
		} catch (IOException e) {
			logger.error("Unable to extract package '{}' in temporary directory.", abxPackage.getFQName());
			throw new RuntimeException("Unable to extract package.", e);
		}

		// build package descriptor and use it to import the action
		AbxPackageDescriptor pkgDescriptor = AbxPackageDescriptor.getInstance(tmp);
		importAction(pkgDescriptor, dryrun);

		return abxPackage;
	}

	/**
	 * Add ABX action constants to the payload.
	 * @param actionToImport ABX action to import
	 */
	protected final void addActionConstantsToPayload(final AbxAction actionToImport) {
		if (actionToImport.abx.inputConstants != null && actionToImport.abx.inputConstants.length > 0) {
			logger.debug("Number of definied constants: " + actionToImport.abx.inputConstants.length);
			for (String name : actionToImport.abx.inputConstants) {
				AbxConstant abxConstant = this.restClient.getAbxConstant(name);
				if (abxConstant == null) {
					throw new RuntimeException("Unable to find action constant with name: " + name);
				}
				actionToImport.abx.inputs.put(String.format("secret:%s", abxConstant.id), "");
			}
		}
	}

	/**
	 * Add secrets to the payload.
	 * @param actionToImport ABX action to import
	 */
	protected final void addSecretsToPayload(final AbxAction actionToImport) {
		if (actionToImport.abx.inputSecrets != null && actionToImport.abx.inputSecrets.length > 0) {
			logger.debug("Number of definied secrets: " + actionToImport.abx.inputSecrets.length);
			for (String name : actionToImport.abx.inputSecrets) {
				VraNgSecret secret = this.restClient.getSecret(name);
				if (secret == null) {
					throw new RuntimeException("Unable to find secret with name: " + name);
				}
				actionToImport.abx.inputs.put(String.format("psecret:%s", secret.id), "");
			}
		}
	}

	/**
	 *
	 * @param pkgDescriptor the abx package descriptor
	 * @param dryrun whether it should be dry run
	 */
	private void importAction(final AbxPackageDescriptor pkgDescriptor, final boolean dryrun) {

		// Get existing actions from server
		List<AbxAction> abxActionsOnServer = this.restClient.getAllAbxActions();
		Map<String, AbxAction> abxActionsOnServerByName = abxActionsOnServer.stream()
				.collect(Collectors.toMap(AbxAction::getName, item -> item));

		// Build payload
		AbxAction actionToImport = pkgDescriptor.getAction();

		if (actionToImport.abx.inputs == null) {
			actionToImport.abx.inputs = new HashMap<>();
		}

		addSecretsToPayload(actionToImport);
		addActionConstantsToPayload(actionToImport);

		// Issue REST request
		if (abxActionsOnServerByName.containsKey(actionToImport.getName())) {
			AbxAction actionToUpdate = abxActionsOnServerByName.get(actionToImport.getName());
			logger.info("Updating action: {} ({})", actionToImport.getName(), actionToUpdate.id);
			if (!dryrun) {
				this.restClient.updateAbxAction(actionToUpdate.id, actionToImport);
			} else {
				logger.info("Dryrun has been set to 'true'. Skipping actual update...");
			}

		} else {
			logger.info("Creating action: " + actionToImport.getName());
			if (!dryrun) {
				this.restClient.createAbxAction(actionToImport);
			} else {
				logger.info("Dryrun has been set to 'true'. Skipping actual create...");
			}
		}
	}
}
