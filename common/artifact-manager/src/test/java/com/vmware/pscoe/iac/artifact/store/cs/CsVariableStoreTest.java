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
package com.vmware.pscoe.iac.artifact.store.cs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.vmware.pscoe.iac.artifact.aria.codestream.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.aria.codestream.models.Variable;
import com.vmware.pscoe.iac.artifact.aria.codestream.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.aria.codestream.store.CsVariableStore;
import com.vmware.pscoe.iac.artifact.aria.codestream.store.models.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

public class CsVariableStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected CsVariableStore store;
	protected RestClientCs restClient;
	protected Package pkg;
	protected ConfigurationCs config;
	protected CsPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
			tempFolder.newFolder("variables");

		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new CsVariableStore();
		restClient = Mockito.mock(RestClientCs.class);
		pkg = PackageFactory.getInstance(PackageType.CS, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationCs.class);
		vraNgPackageDescriptor = Mockito.mock(CsPackageDescriptor.class);

		store.init(restClient, pkg, config, vraNgPackageDescriptor);
		System.out.println("==========================================================");
		System.out.println("START");
		System.out.println("==========================================================");
	}

	@AfterEach
	void tearDown() {
		tempFolder.delete();

		System.out.println("==========================================================");
		System.out.println("END");
		System.out.println("==========================================================");
	}

	@Test
	void testExportDesiredPipeline() {
		// GIVEN
		List<Variable> variables = new ArrayList<>();
		Variable var = new Variable();
		var.setName("var1");
		var.setType("type");
		var.setProject("myProject");
		variables.add(var);
		List<String> names = Arrays.asList(new String[] { "var1" });
		when(restClient.getProjectVariables()).thenReturn(variables);
		when(vraNgPackageDescriptor.getPipeline()).thenReturn(names);

		// TEST
		store.exportContent();

		String[] expectedPipelinefile = { "variables.yaml" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(getTempFolderProjectPath(), expectedPipelinefile);
	}

	@Test
	void testImportContentIm() {
		// GIVEN
		List<Variable> variables = new ArrayList<>();
		List<Variable> existingVars = new ArrayList<>();

		Variable var = new Variable();
		var.setName("var1");
		var.setType("REGULAR");
		var.setProject("myProject");
		var.setDescription("VAR1");
		variables.add(var);
		existingVars.add(var);

		var = new Variable();
		var.setName("var2");
		var.setType("REGULAR");
		var.setProject("myProject");
		var.setDescription("VAR2");
		variables.add(var);
		var = new Variable();
		var.setName("var2");
		var.setType("REGULAR");
		var.setProject("myProject");
		var.setDescription("VAR2-OLD");
		existingVars.add(var);

		var = new Variable();
		var.setName("var3");
		var.setType("REGULAR");
		var.setProject("myProject");
		var.setDescription("VAR3");
		variables.add(var);

		createTempFile(variables);

		when(restClient.getProjectVariables()).thenReturn(existingVars);
		when(restClient.getProjectName()).thenReturn("myProject");
		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createVariable(any());
		verify(restClient, times(1)).updateVariable(any());

	}

	/**
	 * Gets the temp folder's project path
	 *
	 * @return File
	 */
	private File getTempFolderProjectPath() {

		File[] files = tempFolder.getRoot().listFiles();

		if (files == null || files.length == 0) {
			throw new RuntimeException("Could not find temp folder project path");
		}

		return files[0];
	}

	private void createTempFile(List<Variable> obj) {
		File file = Paths.get(tempFolder.getRoot().getPath(), "variables", "variables" + ".yaml").toFile();
		file.getParentFile().mkdirs();
		try {
			YAMLMapper yamlMapper = new YAMLMapper();
			yamlMapper.setSerializationInclusion(Include.NON_NULL);
			String yamlString = yamlMapper.writeValueAsString(obj);
			// StringWriter writer = new StringWriter();
			// writer.write(triggerYaml);
			Files.write(Paths.get(file.getPath()), yamlString.getBytes(),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Unable to store file.", e);
		}
	}

}
