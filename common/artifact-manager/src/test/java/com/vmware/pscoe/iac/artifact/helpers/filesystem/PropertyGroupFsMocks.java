package com.vmware.pscoe.iac.artifact.helpers.filesystem;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertyGroupFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "property-groups";

	public PropertyGroupFsMocks(File tempDir) {
		super( tempDir );
	}

	@Override
	public File getWorkdir() {
		return Paths.get( this.tempDir.getPath(), WORKDIR ).toFile();
	}

	/**
	 * Adds the property group dir if it does not exist
	 *
	 * @param	propertyGroup - The Property group to add
	 */
	public void addPropertyGroup( VraNgPropertyGroup propertyGroup ) {
		File customPropertyGroupFile	= Paths.get(
			this.getWorkdir().getAbsolutePath(),
			propertyGroup.getName() + ".json"
		).toFile();

		Gson gson		= new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName	= Paths.get( customPropertyGroupFile.getPath() );
		writeFileToPath( itemName, gson.toJson( propertyGroup ).getBytes() );
	}
}
