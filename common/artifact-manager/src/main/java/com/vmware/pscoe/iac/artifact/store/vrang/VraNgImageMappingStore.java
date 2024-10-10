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
package com.vmware.pscoe.iac.artifact.store.vrang;

import com.vmware.pscoe.iac.artifact.model.vrang.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Store for image mappings in vRA NG.
 */
public class VraNgImageMappingStore extends AbstractVraNgStore {

	/**
	 * @param logger
	 */
	private final Logger logger = LoggerFactory.getLogger(VraNgImageMappingStore.class);

	/**
	 * Unused as regional content needs refactoring.
	 */
	public void deleteContent() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void importContent(File sourceDirectory) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'importContent'");
	}

	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getImageMapping();
	}

	@Override
	protected void exportStoreContent() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'exportStoreContent'");
	}

	@Override
	protected void exportStoreContent(List<String> itemNames) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'exportStoreContent'");
	}

	@Override
	protected <T extends Identifiable> List<T> getAllServerContents() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAllServerContents'");
	}

	@Override
	protected void deleteResourceById(String resId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deleteResourceById'");
	}
}
