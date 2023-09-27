package com.vmware.pscoe.iac.artifact.store.filters;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.stream.Collectors;

public class CustomFolderFileFilter implements FilenameFilter {
	/**
	 * Descriptor names (content.yaml contents per asset).
	 */
	private final List<String> descriptionNames;
	/**
	 * Logger instance.
	 */
	private final Logger logger;

	/**
	 * Constructor.
	 * @param descriptionNames Items as they appear in the content.yaml file.
	 */	
	public CustomFolderFileFilter(List<String> descriptionNames) {
		super();
		// convert the descriptor content to lower case (in order search to be case insensitive).
		this.descriptionNames = descriptionNames == null ? null : descriptionNames.stream().map(item -> item.toLowerCase().trim()).collect(Collectors.toList());
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * Method that filters files based on what is defined in the content.yaml.
	 * Accepts only files and filters them by name as defined in their respective categories in the content.yaml.
	 * 
	 * @param dir File to filter.
	 * @param name name of the file.
	 * @return boolean.
	 */
	@Override
	public boolean accept(File dir, String name) {
		logger.debug("Process file for filtering '{}/{}'", dir.getAbsolutePath(), name);
		String items = descriptionNames != null ? String.join(", ", descriptionNames) : "NULL";
		logger.debug("Items in descriptor (content.yaml): {}", items);

		File currentFile = new File(dir, name);
		// There are cases where in the main item folder, there is a sub-directory, e.g. catalog-item/forms/.
		if (currentFile.isDirectory()) { 
			return false;
		}

		// Following name replace regex, matches the file extension only, by which it enables to have "." in the names
		// for the different entities - bps, forms, etc. e.g. Name "RHEL 8.X Family" -> produced "RHEL 8.X Family.json"
		// -> run replaceFirst [.][^.]+$ = "" -> RHEL 8.X Family, furthermore the search in the descriptor is case insensitive.
		return descriptionNames == null || descriptionNames.contains(name.replaceFirst("[.][^.]+$", "").toLowerCase().trim());
	}
}
