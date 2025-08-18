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
package com.vmware.pscoe.iac.artifact.aria.codestream.store;

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
import java.util.LinkedHashMap;
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
import com.vmware.pscoe.iac.artifact.aria.codestream.models.Endpoint;
import com.vmware.pscoe.iac.artifact.aria.codestream.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.aria.codestream.store.models.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

public class CsEndpointStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected CsEndpointStore store;
	protected RestClientCs restClient;
	protected Package pkg;
	protected ConfigurationCs config;
	protected CsPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
			tempFolder.newFolder("endpoints");

		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new CsEndpointStore();
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
	void testExportDesiredEndpoint() {
		// GIVEN
		List<Endpoint> endpoints = new ArrayList<>();
		Endpoint endpoint = new Endpoint();
		endpoints.add(endpoint);
		endpoint.setName("testEndpoint");
		endpoint.setProperties(new LinkedHashMap<>());
		List<String> names = Arrays.asList(new String[] { "testEndpoint" });
		when(restClient.getProjectEndpoints()).thenReturn(endpoints);
		when(vraNgPackageDescriptor.getEndpoint()).thenReturn(names);

		// TEST
		store.exportContent();

		String[] expectedPipelinefile = { "testEndpoint.yaml" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(getTempFolderProjectPath(), expectedPipelinefile);
	}

	@Test
	void testExportMissingEndpoint() {
		// GIVEN
		List<Endpoint> endpoints = new ArrayList<>();
		Endpoint endpoint = new Endpoint();
		endpoints.add(endpoint);
		endpoint.setName("testEndpoint");

		List<String> names = Arrays.asList(new String[] { "notMatchingPipeline" });
		when(restClient.getProjectEndpoints()).thenReturn(endpoints);
		when(vraNgPackageDescriptor.getEndpoint()).thenReturn(names);

		// TEST
		store.exportContent();

		String[] expectedPipelinefile = {};

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(getTempFolderProjectPath(), expectedPipelinefile);

	}

	@Test
	void testImportContentIm() {
		// GIVEN
		List<Endpoint> endpoints = new ArrayList<>();
		Endpoint endpoint = new Endpoint();
		endpoints.add(endpoint);
		endpoint.setName("testEndpoint");

		// pipelines.add(pipeline);
		createTempFile("testEndpoint", endpoint);

		endpoint = new Endpoint();
		endpoint.setName("testEndpointNew");
		endpoint.setId("xxx-xxxx-xxxx");
		createTempFile("testEndpointNew", endpoint);

		when(restClient.getProjectEndpoints()).thenReturn(endpoints);
		when(restClient.getProjectName()).thenReturn("myProject");
		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createEndpoint(any());
		verify(restClient, times(1)).updateEndpoint(any());
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

	private void createTempFile(String name, Endpoint obj) {
		File file = Paths.get(tempFolder.getRoot().getPath(), "endpoints", name + ".yaml").toFile();
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
