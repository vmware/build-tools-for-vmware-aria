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
package com.vmware.pscoe.iac.artifact.model.abx;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AbxPackageDescriptor extends PackageDescriptor {

	private AbxAction action;

	private static final String PACKAGE_JSON = "package.json";
	private static final String BUNDLE = "bundle.zip";

	public AbxAction getAction() {
		return this.action;
	}

	public void setAction(AbxAction action) {
		this.action = action;
	}

	public static AbxPackageDescriptor getInstance(File filesystemPath) {

		Logger logger = LoggerFactory.getLogger(AbxPackageDescriptor.class);

		AbxPackageDescriptor pd = new AbxPackageDescriptor();

		String packageJsonPath = filesystemPath.getPath() + File.separator + AbxPackageDescriptor.PACKAGE_JSON;

		try (JsonReader reader = new JsonReader(new FileReader(packageJsonPath))) {
			AbxAction action = new Gson().fromJson(reader, AbxAction.class);
			logger.info("Action definition: " + new Gson().toJson(action));
			action.setBundle(new File(filesystemPath, AbxPackageDescriptor.BUNDLE));
			pd.setAction(action);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", filesystemPath.getPath()), e);
		}

		return pd;
	}

}
