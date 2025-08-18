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
package com.vmware.pscoe.iac.artifact.common.store.filters;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFolderFolderFilter implements FilenameFilter {
	private final List<String> descriptionNames;
	private final Logger logger;

	public CustomFolderFolderFilter(List<String> descriptionNames) {
		super();
		this.descriptionNames = descriptionNames;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * Method that filters files based on what is defined in the content.yaml.
	 * Walks trough the files in a specific directory and decides if the the file
	 * should be chosen or not
	 * 
	 * @param dir  File to filter
	 * @param name name of the file
	 * @return boolean
	 */
	@Override
	public boolean accept(File dir, String name) {
		logger.debug("Process file for filtering '{}/{}'", dir.getAbsolutePath(), name);
		String items = descriptionNames != null ? String.join(", ", descriptionNames) : "NULL";
		logger.debug("Items in descriptor (content.yaml): {}", items);
		File currentFile = new File(dir, name);
		if (!currentFile.isDirectory()) {
			return false;
		}
		return descriptionNames == null || descriptionNames.contains(name);
	}
}
