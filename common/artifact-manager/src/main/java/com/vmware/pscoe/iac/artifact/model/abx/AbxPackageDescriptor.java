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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class AbxPackageDescriptor extends PackageDescriptor {
	private static final String PACKAGE_JSON = "package.json";
	private static final String BUNDLE = "bundle.zip";
	private AbxAction action;

	public AbxAction getAction() {
		return this.action;
	}

	public void setAction(AbxAction action) {
		this.action = action;
	}

	public static AbxPackageDescriptor getInstance(File filesystemPath) {
		AbxPackageDescriptor pd = new AbxPackageDescriptor();

		String packageJsonPath = filesystemPath.getPath() + File.separator + AbxPackageDescriptor.PACKAGE_JSON;
		ObjectMapper mapper = new ObjectMapper();
		try {
			AbxAction action = mapper.readValue(FileUtils.readFileToString(new File(packageJsonPath), Charset.defaultCharset()), AbxAction.class);
			File bundleFile = new File(filesystemPath, AbxPackageDescriptor.BUNDLE);
			action.setBundle(Files.readAllBytes(Paths.get(bundleFile.getAbsolutePath())));
			pd.setAction(action);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", packageJsonPath), e);
		} catch (JsonIOException | JsonSyntaxException e) {
			throw new RuntimeException(String.format("Error parsing contents of file: %s", packageJsonPath), e);
		} catch (Exception e) {
			throw new RuntimeException(String.format("General error processing file: %s", packageJsonPath), e);
		}

		return pd;
	}
}
