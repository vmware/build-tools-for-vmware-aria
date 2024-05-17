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

import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.configuration.*;
import com.vmware.pscoe.iac.artifact.rest.*;
import com.vmware.pscoe.iac.artifact.rest.client.vrli.RestClientVrliV1;
import com.vmware.pscoe.iac.artifact.rest.client.vrli.RestClientVrliV2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.cli.CliManagerFactory;
import com.vmware.pscoe.iac.artifact.cli.CliManagerVrops;
import com.vmware.pscoe.iac.artifact.extentions.PackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraCatalogItemPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraCustomFormPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraGlobalPropertyDefinitionPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraGlobalPropertyGroupPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraIconPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.extentions.VraSubscriptionPackageStoreExtention;
import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vro.VroPackageDescriptor;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;
import com.vmware.pscoe.iac.artifact.strategy.StrategyForceLatestVersions;
import com.vmware.pscoe.iac.artifact.strategy.StrategySkipOldVersions;

/**
 * Factory class to create PackageStore instances.
 */
public final class PackageStoreFactory {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PackageStoreFactory() {
	}

	/**
	 * Logger instance.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(VraPackageStore.class);

	/**
	 * Regarding the strategies, the following rules apply.
	 * 
	 * - If forceImportLatestVersions is set, the StrategyForceLatestVersions is used
	 *   and the importOldVersions flag is ignored.
	 *
	 * @param configuration The configuration object.
	 * @return The PackageStore instance.
	 */
	public static <T extends Configuration> PackageStore<?> getInstance(T configuration) {
		List<Strategy> strategies = new ArrayList<>();
		LOGGER.info("Searching for Package Store for type " + configuration.getPackageType());

		String version;

		// TODO: You should be able to select a strategy to use, this doesn't make much sense,
		//   but we have no choice since we want to be backward compatible
		if (configuration.isForceImportLatestVersions()) { 
			LOGGER.info("Using StrategyForceLatestVersions");
			strategies.add(new StrategyForceLatestVersions());
		} else if (!configuration.isImportOldVersions()) {
			LOGGER.info("Using StrategySkipOldVersions");
			strategies.add(new StrategySkipOldVersions());
		}

		if (configuration instanceof ConfigurationVroNg) {
			LOGGER.info("Detected ConfigurationVroNg");
			ConfigurationVroNg config = (ConfigurationVroNg) configuration;
			RestClientVro restClient = RestClientFactory.getClientVroNg(config);
			version = restClient.getVersion();
			LOGGER.info("Detecting vRO Server version '{}'.", version);
			List<PackageStoreExtention<VroPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVroExtensions(version, config, restClient));

			return new VroPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationVro) {
			LOGGER.info("Detected ConfigurationVro");
			ConfigurationVro config = (ConfigurationVro) configuration;
			RestClientVro restClient = RestClientFactory.getClientVro(config);
			version = restClient.getVersion();
			LOGGER.info("Detecting vRO Server version '{}'.", version);
			List<PackageStoreExtention<VroPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVroExtensions(version, config, restClient));

			return new VroPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationVra) {
			LOGGER.info("Detected ConfigurationVra");
			ConfigurationVra config = (ConfigurationVra) configuration;
			RestClientVra restClient = RestClientFactory.getClientVra(config);
			version = restClient.getVersion();
			LOGGER.info("Detecting vRA Server version '{}'.", version);
			List<PackageStoreExtention<VraPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVraExtensions(version, config, restClient));

			return new VraPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationAbx) {
			LOGGER.info("Detected ConfigurationAbx");
			LOGGER.info("Creating configuration for ABX");
			ConfigurationAbx config = (ConfigurationAbx) configuration;

			// ABX service is part of vRA therefore the same REST client is used
			RestClientVraNg restClient = RestClientFactory.getClientVraNg(config);

			// Specific ABX operations are handled by dedicated ABX package store
			return new AbxPackageStore(restClient, config);
		}
		if (configuration instanceof ConfigurationCs) {
			LOGGER.info("Detected ConfigurationCs");
			ConfigurationCs config = (ConfigurationCs) configuration;
			RestClientCs restClient = RestClientFactory.getClientCs(config);
			LOGGER.info("Creating configuration for Code Stream");
			return new CsPackageStore(restClient, config);
		}

		if (configuration instanceof ConfigurationVraNg) {
			LOGGER.info("Detected ConfigurationVraNg");
			ConfigurationVraNg config = (ConfigurationVraNg) configuration;
			RestClientVraNg restClient = RestClientFactory.getClientVraNg(config);
			LOGGER.info("Creating configuration for VRA NG");

			return new VraNgPackageStore(restClient, config);
		}

		if (configuration instanceof ConfigurationVcd) {
			LOGGER.info("Detected ConfigurationVcd");
			ConfigurationVcd config = (ConfigurationVcd) configuration;
			RestClientVcd restClient = RestClientFactory.getClientVcd(config);
			version = restClient.getVersion();
			LOGGER.info("Detecting vCD Server version '{}'.", version);

			return new VcdNgPackageStore(restClient, strategies, new Version(version));
		}

		if (configuration instanceof ConfigurationVrops) {
			LOGGER.info("Detected ConfigurationVrops");
			ConfigurationVrops config = (ConfigurationVrops) configuration;

			CliManagerVrops cliManager = CliManagerFactory.getVropsCliManager(config);
			RestClientVrops restClient = RestClientFactory.getClientVrops(config);
			version = restClient.getVersion();
			LOGGER.info("Detecting vROPs Server version '{}'.", version);

			return new VropsPackageStore(cliManager, restClient, new Version(version));
		}

		if (configuration instanceof ConfigurationVrli) {
			LOGGER.info("Detected ConfigurationVrli");
			ConfigurationVrli config = (ConfigurationVrli) configuration;
			RestClientVrliV1 restClientV1 = RestClientFactory.getClientVrliV1(config);
			RestClientVrliV2 restClientV2 = RestClientFactory.getClientVrliV2(config);

			try {
				version = restClientV1.getVersion();
			} catch (Exception e) {
				version = restClientV2.getVersion();
			}
			LOGGER.info("Detected vRLI version " + version);
			if (!StringUtils.isEmpty(version) && Version.compareSemanticVersions(version, "8.8") > -1) {
				LOGGER.info("Instantiate REST Client v2.");
				return new VrliPackageStoreV2(restClientV2);
			}
			LOGGER.info("Instantiate REST Client v1.");
			return new VrliPackageStoreV1(restClientV1);
		}

		if (configuration instanceof ConfigurationSsh) {
			LOGGER.info("Detected ConfigurationSsh");
			ConfigurationSsh config = (ConfigurationSsh) configuration;
			return new SshPackageStore(config);
		}

		throw new RuntimeException(
				"There is no PackageStore defined for Configuration Type " + configuration.getClass().getSimpleName());
	}

	private static List<PackageStoreExtention<VraPackageDescriptor>> loadVraExtensions(String vraVersion,
			ConfigurationVra config, RestClientVra client) {
		List<PackageStoreExtention<VraPackageDescriptor>> extentions = new ArrayList<>();

		if (new Version(vraVersion).compareTo(new Version("7.4-SNAPSHOT")) >= 0) {
			extentions.add(new VraCustomFormPackageStoreExtention(client));
		}
		extentions.add(new VraSubscriptionPackageStoreExtention(client));
		extentions.add(new VraGlobalPropertyDefinitionPackageStoreExtention(client));
		extentions.add(new VraGlobalPropertyGroupPackageStoreExtention(client));
		extentions.add(new VraIconPackageStoreExtention(client));
		extentions.add(new VraCatalogItemPackageStoreExtention(client));

		return extentions;
	}

	private static List<PackageStoreExtention<VroPackageDescriptor>> loadVroExtensions(String vroVersion,
			ConfigurationNg config, RestClientVro client) {
		// No vRO extensions for now
		return new ArrayList<>();
	}

}
