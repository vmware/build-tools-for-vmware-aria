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

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class that unify the way the content is exported for all sub-classes that are based on regional stores
 * When no item ([]) is given, nothing is exported
 * When a list of specific items ([item1, itme2]) are given, only the given itmes are exported
 * When nothing (null) is given, all items from the store are exported
 */
public abstract class AbstractVraNgRegionalStore {
    protected RestClientVraNg restClient;
    protected Package vraNgPackage;
    protected VraNgPackageDescriptor vraNgPackageDescriptor;
    protected Logger logger;

    private void ini(
		RestClientVraNg restClient,
		Package vraNgPackage,
		VraNgPackageDescriptor vraNgPackageDescriptor
		) {
			this.restClient = restClient;
			this.vraNgPackage = vraNgPackage;
			this.vraNgPackageDescriptor = vraNgPackageDescriptor;
		}

	public void init(
		RestClientVraNg restClient,
		Package vraNgPackage,
		VraNgPackageDescriptor vraNgPackageDescriptor
		) {
			this.ini(restClient, vraNgPackage, vraNgPackageDescriptor);
			this.logger = LoggerFactory.getLogger(this.getClass());
		}

	public void init(
		RestClientVraNg restClient,
		Package vraNgPackage,
		VraNgPackageDescriptor vraNgPackageDescriptor,
		Logger logger
		) {
			this.ini(restClient, vraNgPackage, vraNgPackageDescriptor);
			this.logger = logger;
		}

	/**
	 * The main export method. It unifies the way the vRA NG Regional Stores are exported.
	 * When no item ([]) is given, nothing is exported
	 * When a list of specific items ([item1, itme2]) are given, only the given itmes are exported
	 * When nothing (null) is given, all items from the store within the given region are exported
	 * 
	 * In Sub-classes the abstract methods getItemListFromDescriptor, exportStoreContent should be overwritten
	 * @param cloudAccounts list of cloud accounts
	 */
	public void exportContent( List<VraNgCloudAccount> cloudAccounts ) {
		List<String> itemNames	= this.getItemListFromDescriptor();
		List<String> cloudAccountNames = cloudAccounts.stream().map(VraNgCloudAccount::getName).collect(Collectors.toList());

		logger.info( "Currently exporting regional content from: {}", this.getClass().getSimpleName() );

		if ( itemNames == null ) {
			logger.info( "Nothing/null passed exporting everything from cloud accounts {}", cloudAccountNames );
			this.exportStoreContent( cloudAccounts );

		}
		else if ( ! itemNames.isEmpty() ) {
			logger.info( "Items: {}. Exporting items part of cloud accounts {}", itemNames, cloudAccountNames);
			this.exportStoreContent( cloudAccounts, itemNames );
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
	 * @param cloudAccounts list of cloud accounts
	 */
	protected abstract void exportStoreContent( List<VraNgCloudAccount> cloudAccounts );

	/**
	 * Called when the List returned from getItemListFromDescriptor is not empty
	 * @param cloudAccounts list of cloud accounts
	 * @param itemNames list of names
	 */
	protected abstract void exportStoreContent( List<VraNgCloudAccount> cloudAccounts, List<String> itemNames );
}
