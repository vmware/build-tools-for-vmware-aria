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
package com.vmware.pscoe.iac.artifact.aria.model.objectmapping;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VraNgCloudAccountTag {
	/**
	 * @param Prime Number 17.
	 */
	private static final int PRIME_NUMBER_17 = 17;

	/**
	 * @param Prime Number 31.
	 */
	private static final int PRIME_NUMBER_31 = 31;

	/**
	 * @param exportTag the tag to export.
	 */
	private final String exportTag;
	/**
	 * @param importTags the tags to import.
	 */
	private final List<String> importTags;

	/**
	 * Default constructor.
	 */
	public VraNgCloudAccountTag() {
		super();
		this.exportTag = "";
		this.importTags = new ArrayList<>();
	}

	/**
	 * Constructor with export tag and import tags.
	 * 
	 * @param exportTag  the tag to export
	 * @param importTags the tags to import
	 */
	public VraNgCloudAccountTag(String exportTag, List<String> importTags) {
		this.exportTag = exportTag;
		this.importTags = importTags;
	}

	/**
	 * Get the export tag.
	 * 
	 * @return the export tag
	 */
	public String getExportTag() {
		return this.exportTag;
	}

	/**
	 * Get the import tags.
	 * 
	 * @return the import tags
	 */
	public List<String> getImportTags() {
		return this.importTags;
	}

	/**
	 * Check if the object is equal to this object.
	 *
	 * @param obj The object to compare
	 * @return True if the object is equal to this object, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * @return the hashcode representation of the object
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
				.append(exportTag)
				.append(importTags)
				.toHashCode();
	}
}
