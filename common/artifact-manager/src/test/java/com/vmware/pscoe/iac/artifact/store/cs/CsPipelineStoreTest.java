package com.vmware.pscoe.iac.artifact.store.cs;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
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
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientCs;

import net.minidev.json.JSONObject;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class CsPipelineStoreTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected CsPipelineStore store;
	protected RestClientCs restClient;
	protected Package pkg;
	protected ConfigurationCs config;
	protected CsPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
			tempFolder.newFolder("pipelines");

		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new CsPipelineStore();
		restClient = Mockito.mock(RestClientCs.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
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
		List<JsonObject> pipelines = new ArrayList<>();
		JsonObject pipeline = new JsonObject();
		pipelines.add(pipeline);
		pipeline.addProperty("name", "testPipeline");
		List<String> names = Arrays.asList(new String[] { "testPipeline" });
		when(restClient.getProjectPipelines()).thenReturn(pipelines);
		when(vraNgPackageDescriptor.getPipeline()).thenReturn(names);

		// TEST
		store.exportContent();

		String[] expectedPipelinefile = { "testPipeline.yaml" };

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(getTempFolderProjectPath(), expectedPipelinefile);
	}

	@Test
	void testExportMissingPipeline() {
		// GIVEN
		List<JsonObject> pipelines = new ArrayList<>();
		JsonObject pipeline = new JsonObject();
		pipeline.addProperty("name", "testPipeline");

		pipelines.add(pipeline);
		List<String> names = Arrays.asList(new String[] { "notMatchingPipeline" });
		when(restClient.getProjectPipelines()).thenReturn(pipelines);
		when(vraNgPackageDescriptor.getPipeline()).thenReturn(names);

		// TEST
		store.exportContent();

		String[] expectedPipelinefile = {};

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(getTempFolderProjectPath(), expectedPipelinefile);

	}

	@Test
	void testImportContentIm() {
		// GIVEN
		List<JsonObject> pipelines = new ArrayList<>();
		JsonObject pipeline = new JsonObject();
		pipeline.addProperty("name", "newPipeline");
		pipeline.addProperty("state", "ENABLED");
		// pipelines.add(pipeline);
		createTempFile("newPipeline", pipeline);

		pipeline = new JsonObject();
		pipeline.addProperty("name", "existingPipeline");
		pipeline.addProperty("state", "RELEASED");
		pipeline.addProperty("id", "xxxx-yyyyy-xxxxx");
		pipelines.add(pipeline);
		createTempFile("existingPipeline", pipeline);

		when(restClient.getProjectPipelines()).thenReturn(pipelines);
		when(restClient.getProjectName()).thenReturn("myProject");
		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createPipeline(any());
		verify(restClient, times(1)).updatePipeline(any(), any());
		verify(restClient, times(3)).patchPipeline(any(), any());

	}

	@Test
	void testImportPipelineWithRollbackDependencies() {
		// GIVEN Three pipelines. One of them used as a rollback to the other two.
		List<JsonObject> pipelines = new ArrayList<>(3);

		JsonObject rollbackPipe = new JsonObject();
		rollbackPipe.addProperty("name", "PipelineRollback");
		rollbackPipe.addProperty("state", "ENABLED");
		rollbackPipe.addProperty("id", "3C22AD16-95D9-4C20-B9FF-D035CF97AA88");

		JsonObject pipe1 = new JsonObject();
		pipe1.addProperty("name", "Pipeline1");
		pipe1.addProperty("state", "ENABLED");
		pipe1.addProperty("id", "740820E8-E249-4399-985F-DBA1C554DC38");

		JsonObject pipe2 = new JsonObject();
		pipe2.addProperty("name", "Pipeline2");
		pipe2.addProperty("state", "ENABLED");
		pipe2.addProperty("id", "FA3EEDE8-C6F5-4D59-8277-84B90B702464");

		JsonArray rollbacks = new JsonArray();
		rollbacks.add(rollbackPipe);
		pipe1.add("rollbacks", rollbacks);
		pipe2.add("rollbacks", rollbacks);

		pipelines.add(pipe1);
		pipelines.add(pipe2);
		pipelines.add(rollbackPipe);

		createTempFile("PipelineRollback", rollbackPipe);
		createTempFile("Pipeline1", pipe1);
		createTempFile("Pipeline2", pipe2);

		when(restClient.getProjectPipelines()).thenReturn(pipelines);
		when(restClient.getProjectName()).thenReturn("myProject");

		// TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		InOrder inOrder = inOrder(restClient);
		inOrder.verify(restClient).updatePipeline(eq("PipelineRollback"), any());
		inOrder.verify(restClient).updatePipeline(eq("Pipeline2"), any());
		inOrder.verify(restClient).updatePipeline(eq("Pipeline1"), any());

		verify(restClient, times(3)).patchPipeline(any(), any());

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

	private void createTempFile(String name, JsonObject obj) {
		File file = Paths.get(tempFolder.getRoot().getPath(), "pipelines", name + ".yaml").toFile();
		file.getParentFile().mkdirs();
		try {
			JsonNode jsonNodeTree = new ObjectMapper().readTree(obj.toString());
			YAMLMapper yamlMapper = new YAMLMapper();
			yamlMapper.setSerializationInclusion(Include.NON_NULL);
			String yamlString = yamlMapper.writeValueAsString(jsonNodeTree);
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
