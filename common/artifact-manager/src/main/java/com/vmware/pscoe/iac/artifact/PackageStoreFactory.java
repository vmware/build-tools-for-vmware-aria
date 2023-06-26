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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.cli.CliManagerFactory;
import com.vmware.pscoe.iac.artifact.cli.CliManagerVrops;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationAbx;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationSsh;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVroNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
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
import com.vmware.pscoe.iac.artifact.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.rest.RestClientFactory;
import com.vmware.pscoe.iac.artifact.rest.RestClientVcd;
import com.vmware.pscoe.iac.artifact.rest.RestClientVra;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.rest.client.vrli.RestClientVrliV1;
import com.vmware.pscoe.iac.artifact.rest.client.vrli.RestClientVrliV2;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;
import com.vmware.pscoe.iac.artifact.strategy.StrategySkipOldVersions;

public final class PackageStoreFactory {
	/**
	 * Logger instance.
	 */
	private static Logger logger = LoggerFactory.getLogger(VraPackageStore.class);

	/**
	 * Empty constructor.
	 */
	private PackageStoreFactory() {

	}

	/**
	 * Returns the package store instance based on the configuration type.
	 * 
	 * @param <T> configuration instance of an configuration type.
	 * @return packageStore instance based on the configuration.
	 * @throws RuntimeException if the configuration type is wrong.
	 */
	public static <T extends Configuration> PackageStore<?> getInstance(final T configuration) {
		List<Strategy> strategies = new ArrayList<>();
		logger.info("Searching for Package Store for type " + configuration.getPackageType());

		String version;
		if (!configuration.isImportOldVersions()) {
			strategies.add(new StrategySkipOldVersions());
		}

		if (configuration instanceof ConfigurationVroNg) {
			logger.info("Detected ConfigurationVroNg");
			ConfigurationVroNg config = (ConfigurationVroNg) configuration;
			RestClientVro restClient = RestClientFactory.getClientVroNg(config);
			version = restClient.getVersion();
			logger.info("Detecting vRO Server version '{}'.", version);
			List<PackageStoreExtention<VroPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVroExtensions(version, config, restClient));

			return new VroPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationVro) {
			logger.info("Detected ConfigurationVro");
			ConfigurationVro config = (ConfigurationVro) configuration;
			RestClientVro restClient = RestClientFactory.getClientVro(config);
			version = restClient.getVersion();
			logger.info("Detecting vRO Server version '{}'.", version);
			List<PackageStoreExtention<VroPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVroExtensions(version, config, restClient));

			return new VroPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationVra) {
			logger.info("Detected ConfigurationVra");
			ConfigurationVra config = (ConfigurationVra) configuration;
			RestClientVra restClient = RestClientFactory.getClientVra(config);
			version = restClient.getVersion();
			logger.info("Detecting vRA Server version '{}'.", version);
			List<PackageStoreExtention<VraPackageDescriptor>> extentions = new ArrayList<>();
			extentions.addAll(loadVraExtensions(version, config, restClient));

			return new VraPackageStore(restClient, strategies, extentions, new Version(version));
		}

		if (configuration instanceof ConfigurationAbx) {
			logger.info("Detected ConfigurationAbx");
			logger.info("Creating configuration for ABX");
			ConfigurationAbx config = (ConfigurationAbx) configuration;

			// ABX service is part of vRA therefore the same REST client is used
			RestClientVraNg restClient = RestClientFactory.getClientVraNg(config);

			// Specific ABX operations are handled by dedicated ABX package store
			return new AbxPackageStore(restClient, config);
		}

		if (configuration instanceof ConfigurationCs) {
			logger.info("Detected ConfigurationCs");
			ConfigurationCs config = (ConfigurationCs) configuration;
			RestClientCs restClient = RestClientFactory.getClientCs(config);
			logger.info("Creating configuration for Code Stream");
			return new CsPackageStore(restClient, config);
		}

		if (configuration instanceof ConfigurationVraNg) {
			logger.info("Detected ConfigurationVraNg");
			ConfigurationVraNg config = (ConfigurationVraNg) configuration;
			RestClientVraNg restClient = RestClientFactory.getClientVraNg(config);
			logger.info("Creating configuration for VRA NG");

			return new VraNgPackageStore(restClient, config);
		}

		if (configuration instanceof ConfigurationVcd) {
			logger.info("Detected ConfigurationVcd");
			ConfigurationVcd config = (ConfigurationVcd) configuration;
			RestClientVcd restClient = RestClientFactory.getClientVcd(config);
			version = restClient.getVersion();
			logger.info("Detecting vCD Server version '{}'.", version);

			return new VcdNgPackageStore(restClient, strategies, new Version(version));
		}

		if (configuration instanceof ConfigurationVrops) {
			logger.info("Detected ConfigurationVrops");
			ConfigurationVrops config = (ConfigurationVrops) configuration;

			CliManagerVrops cliManager = CliManagerFactory.getVropsCliManager(config);
			RestClientVrops restClient = RestClientFactory.getClientVrops(config);
			version = restClient.getVersion();
			logger.info("Detecting vROPs Server version '{}'.", version);

			return new VropsPackageStore(cliManager, restClient, new Version(version));
		}

		if (configuration instanceof ConfigurationVrli) {
			logger.info("Detected ConfigurationVrli");
			ConfigurationVrli config = (ConfigurationVrli) configuration;
			RestClientVrliV1 restClientV1 = RestClientFactory.getClientVrliV1(config);
			RestClientVrliV2 restClientV2 = RestClientFactory.getClientVrliV2(config);

			try {
				version = restClientV1.getVersion();
			} catch (Exception e) {
				version = restClientV2.getVersion();
			}
			logger.info("Detected vRLI version " + version);
			// if flag for the old alert API is in effect use API v1 client
			if (config.isOldAlertsApiUsed()) {
				logger.info("Usage of old alerts API is in effect.");
				return new VrliPackageStoreV1(restClientV1);
			}
			if (!StringUtils.isEmpty(version) && Version.compareSemanticVersions(version, "8.8") > -1) {
				logger.info("Instantiate REST Client v2.");
				return new VrliPackageStoreV2(restClientV2);
			}
			logger.info("Instantiate REST Client v1.");
			return new VrliPackageStoreV1(restClientV1);
		}

		if (configuration instanceof ConfigurationSsh) {
			logger.info("Detected ConfigurationSsh");
			ConfigurationSsh config = (ConfigurationSsh) configuration;
			return new SshPackageStore(config);
		}

		throw new RuntimeException("There is no PackageStore defined for Configuration Type " + configuration.getClass().getSimpleName());
	}

	/**
	 * Load vRA extensions like subscriptions, icons, catalog items, etc.
	 * 
	 * @param vraVersion version of the vRA used.
	 * @param config     vRA configuration type.
	 * @param client     vRA REST client to use.
	 * @return extensions list of loaded extensions.
	 */
	private static List<PackageStoreExtention<VraPackageDescriptor>> loadVraExtensions(final String vraVersion, final ConfigurationVra config,
			final RestClientVra client) {
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

	/**
	 * Load vRO extensions. Currently not supported.
	 * 
	 * @param vroVersion version of the vRO used.
	 * @param config     vRO configuration type.
	 * @param client     vRO REST client to use.
	 * @return extensions list of loaded extensions.
	 */
	private static List<PackageStoreExtention<VroPackageDescriptor>> loadVroExtensions(final String vroVersion, final ConfigurationNg config,
			final RestClientVro client) {
		// No vRO extensions for now
		return new ArrayList<>();
	}
}
