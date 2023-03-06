package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgIntegration;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.utils.VraNgIntegrationUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Abstract class that unify the way the content is exported for all subclasses
 * When no item ([]) is given, nothing is exported
 * When a list of specific items ([item1, item2]) are given, only the given items are exported
 * When nothing (null) is given, all items from the store are exported
 */
public abstract class AbstractVraNgStore implements IVraNgStore {
	protected RestClientVraNg restClient;
	protected Package vraNgPackage;
	// initialize the vraNgPackageDescriptor to avoid NPE
	protected VraNgPackageDescriptor vraNgPackageDescriptor = new VraNgPackageDescriptor();
	protected ConfigurationVraNg config;
	protected Logger logger;

	private void ini(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
					 VraNgPackageDescriptor vraNgPackageDescriptor) {
		this.restClient = restClient;
		this.vraNgPackage = vraNgPackage;
		this.vraNgPackageDescriptor = vraNgPackageDescriptor;
		this.config = config;
	}

	public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
					 VraNgPackageDescriptor vraNgPackageDescriptor) {
		this.ini(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config,
					 VraNgPackageDescriptor vraNgPackageDescriptor, Logger logger) {
		this.ini(restClient, vraNgPackage, config, vraNgPackageDescriptor);
		this.logger = logger;
	}

	/**
	 * The main export method. It unifies the way the vRA NG Stores are exported.
	 * When no item ([]) is given, nothing is exported
	 * When a list of specific items ([item1, item2]) are given, only the given items are exported
	 * When nothing (null) is given, all items from the store are exported
	 * 
	 * In Sub-classes the abstract methods getItemListFromDescriptor, exportStoreContent should be overwritten
	 */
	public void exportContent() {
		List<String> itemNames	= this.getItemListFromDescriptor();

		logger.info( "Currently exporting: {}", this.getClass().getSimpleName() );

		if ( itemNames == null ) {
			logger.info( "Nothing/null passed exporting everything" );
			this.exportStoreContent();

		}
		else if ( ! itemNames.isEmpty() ) {
			logger.info( "Exporting filtered items: {}", itemNames );
			this.exportStoreContent( itemNames );
		}
		else {
			logger.info( "Empty array is passed, not exporting anything" );
		}
	}

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of items
	 */
	protected abstract List<String> getItemListFromDescriptor();

	/**
	 * Called when the List returned from getItemListFromDescriptor is empty
	 */
	protected abstract void exportStoreContent();

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty
	 * @param itemNames list of names
	 */
	protected abstract void exportStoreContent( List<String> itemNames );


    protected String getVroTargetIntegrationEndpointLink() throws ConfigurationException {
        String integrationName = this.config.getVroIntegration();
        VraNgIntegration targetIntegration = this.restClient.getVraWorkflowIntegration(integrationName);
        if (targetIntegration != null && !StringUtils.isEmpty(targetIntegration.getName())) {
            return targetIntegration.getEndpointConfigurationLink();
        } else {
            // fetch the default vra integration
            VraNgIntegration defaultIntegration = VraNgIntegrationUtils.getInstance()
                    .getDefaultVraIntegration(this.restClient);

            if (!StringUtils.isEmpty(defaultIntegration.getName())) {
                logger.warn("Unable to find integration '{}' on host '{}' setting default integration to '{}'",
                        integrationName, this.config.getHost(), defaultIntegration.getName());
                return defaultIntegration.getEndpointConfigurationLink();
            } else {
                // After try for search for vRO integration if we can't find the right one we
                // interrupt the process
                throw new ConfigurationException(String.format(
                        "Unable to find integration '%s' on host '%s' and the default integration '%s' is not configured on it",
                        integrationName, this.config.getHost(), VraNgIntegrationUtils.DEFAULT_INTEGRATION_NAME));
            }
        }
    }
	protected File[] filterBasedOnConfiguration(File itemFolder, FilenameFilter filter) {
		return itemFolder.listFiles(filter);
	}
}
