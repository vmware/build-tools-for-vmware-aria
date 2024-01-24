package com.vmware.pscoe.iac.artifact.store.vrang;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
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

/**
 * Store  for all 6 Policy Types
 */
public class VraNgPolicyStore extends AbstractVraNgStore {


	private AbstractVraNgStore[] policyStores = new AbstractVraNgStore[6];
	private final Logger logger = LoggerFactory.getLogger(VraNgPolicyStore.class);
	
	public VraNgPolicyStore () {
		System.out.println(("Multiple Store constructors follow"));
		this.policyStores[0] =  new VraNgContentSharingPolicyStore();
		this.policyStores[1] =  new VraNgResourceQuotaPolicyStore();
		//TODO: add the rest here after implemented
		this.policyStores[2] = null;
		this.policyStores[3] = null;
		this.policyStores[4] = null;
		this.policyStores[5] = null;
	}

	@Override
	public void importContent(File sourceDirectory) {

		for (AbstractVraNgStore store : policyStores) {
			if (store!=null) {
				store.importContent(sourceDirectory);
				System.out.println("Imported content for " + store.getClass());
			}
		}
	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		logger.info("{}->getItemListFromDescriptor", VraNgPolicyStore.class);
		List<String> resultList = new ArrayList<String>();
		for (AbstractVraNgStore store : policyStores) {
			if (store!=null) {
				logger.info("For Store {}; found the following items:{}",store.getClass(), store.getItemListFromDescriptor());
				List<String> list = store.getItemListFromDescriptor();
				if  (list != null) {
					resultList.addAll(list);
				}
			}
		}
		if (resultList.isEmpty())
			return null;
		else
			return resultList;
	}

	@Override
	protected void exportStoreContent() {
		System.out.println("Export store content for all policies follows. Stores number: " + policyStores.length);
		for (AbstractVraNgStore store : policyStores) {
			if (store!=null) {
				System.out.println("Exporting full non null store:" + store.getClass());
				store.exportStoreContent();
			}
		}
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
		logger.info(("Export store content for all policies follows.")); 
		for (AbstractVraNgStore store : policyStores) {
			if (store!=null) {
				System.out.println("Exporting items from non null store:" + store.getClass());
				store.exportStoreContent(itemNames);
			}

		}
	}


}
