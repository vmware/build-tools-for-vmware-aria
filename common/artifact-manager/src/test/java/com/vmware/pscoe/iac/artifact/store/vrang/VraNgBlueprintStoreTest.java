package com.vmware.pscoe.iac.artifact.store.vrang;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.helpers.FsMocks;
import com.vmware.pscoe.iac.artifact.helpers.stubs.BlueprintMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.BlueprintVersionsMockBuilder;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrang.*;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * NOTE: This does not test duplicate names from one content source, since the Store is not responsible for that kind of logic.
 *
 * @TODO ADD assertions for versions when exporting
 */
public class VraNgBlueprintStoreTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected VraNgBlueprintStore store;
	protected RestClientVraNg restClient;
	protected Package pkg;
	protected ConfigurationVraNg config;
	protected VraNgPackageDescriptor vraNgPackageDescriptor;
	protected FsMocks fsMocks;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		fsMocks = new FsMocks(tempFolder.getRoot());
		store = new VraNgBlueprintStore();
		restClient = Mockito.mock(RestClientVraNg.class);
		pkg = PackageFactory.getInstance(PackageType.VRANG, tempFolder.getRoot());
		config = Mockito.mock(ConfigurationVraNg.class);
		vraNgPackageDescriptor = Mockito.mock(VraNgPackageDescriptor.class);

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
	void testExportContentWithNoBlueprints() {
		//GIVEN 
		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(new ArrayList<String>());

		//TEST
		store.exportContent();

		//VERIFY
		verify(restClient, never()).getAllBlueprints();
		verify(restClient, never()).getBlueprintVersions(anyString());

		assertEquals(0, tempFolder.getRoot().listFiles().length);
	}

	@Test
	void testExportContentWithAllBlueprints() {
		//GIVEN 
		List<VraNgBlueprint> blueprints = new ArrayList<>();
		BlueprintMockBuilder mockBuilder = new BlueprintMockBuilder("ngnix");

		blueprints.add(mockBuilder.setRequestScopeOrg(true).build());

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(null);
		when(restClient.getAllBlueprints()).thenReturn(blueprints);

		//TEST
		store.exportContent();

		String[] expectedBlueprints = {"ngnix"};
		String[] expectedBlueprintFiles = {"content.yaml",
			"details.json",
			"versions.json"};

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedBlueprints);
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "ngnix"), expectedBlueprintFiles);
	}

	@Test
	void testExportContentWithSpecificBlueprints() {
		//GIVEN 
		List<VraNgBlueprint> blueprints = new ArrayList<>();
		BlueprintMockBuilder ngnixMockBuilder = new BlueprintMockBuilder("ngnix");
		BlueprintMockBuilder tomcatMockBuilder = new BlueprintMockBuilder("tomcat");

		blueprints.add(ngnixMockBuilder.setRequestScopeOrg(true).build());
		blueprints.add(tomcatMockBuilder.setRequestScopeOrg(false).build());

		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("ngnix");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);
		when(restClient.getAllBlueprints()).thenReturn(blueprints);

		//TEST
		store.exportContent();

		String[] expectedBlueprints = {"ngnix"};
		String[] expectedBlueprintFiles = {"content.yaml",
			"details.json",
			"versions.json"};

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), expectedBlueprints);
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "ngnix"), expectedBlueprintFiles);
	}
	
	@Test
	void testExportContentWithNonExistingBlueprint() {
		//GIVEN 
		List<VraNgBlueprint> blueprints = new ArrayList<>();
		BlueprintMockBuilder ngnixMockBuilder = new BlueprintMockBuilder("ngnix");

		blueprints.add(ngnixMockBuilder.setRequestScopeOrg(true).build());

		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("nothing");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);
		when(restClient.getAllBlueprints()).thenReturn(blueprints);

		//TEST
		assertThrows(IllegalStateException.class, () -> store.exportContent());
	}

	@Test
	void testImportContentWithDotInName() {
		// GIVEN
		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("nginx 8.x test");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);

		BlueprintMockBuilder builder = new BlueprintMockBuilder("nginx 8.x test");
		VraNgBlueprint blueprint = builder.build();

		fsMocks.blueprintFsMocks().addBlueprint(blueprint);

		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), new String[]{"nginx 8.x test"});
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "nginx 8.x test"), new String[]{"content.yaml", "details.json"});

		List<VraNgBlueprint> bluePrintsOnServer = new ArrayList<>();

		when(restClient.getAllBlueprints()).thenReturn(bluePrintsOnServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createBlueprint(any());
		verify(restClient, never()).isBlueprintVersionPresent(any(), any());
		verify(restClient, times(1)).releaseBlueprintVersion(any(), any());
	}

	@Test
	void testImportContentFlipsVersions() throws IOException, ParseException {
		// GIVEN
		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("nginx");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);

		BlueprintMockBuilder builder = new BlueprintMockBuilder("nginx");
		VraNgBlueprint blueprint = builder.build();
		BlueprintVersionsMockBuilder versionsMockBuilder = new BlueprintVersionsMockBuilder(blueprint);
		versionsMockBuilder.setVersions(3);
		JsonArray versionsArray = versionsMockBuilder.build();

		fsMocks.blueprintFsMocks().addBlueprint(blueprint, versionsArray.toString());

		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), new String[]{"nginx"});
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "nginx"), new String[]{"content.yaml", "details.json", "versions.json"});

		List<VraNgBlueprint> bluePrintsOnServer = new ArrayList<>();

		when(restClient.getAllBlueprints()).thenReturn(bluePrintsOnServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createBlueprint(any());

		ArgumentCaptor<String> bpIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<JsonObject> jsonObjectCaptor = ArgumentCaptor.forClass(JsonObject.class);

		verify(restClient, times(3)).importBlueprintVersion(
			bpIdCaptor.capture(),
			jsonObjectCaptor.capture()
		);

		Date first = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(0).get("createdAt"));
		Date second = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(1).get("createdAt"));
		Date third = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(2).get("createdAt"));

		// Assert ordering
		assertEquals(-1, first.compareTo(second));
		assertEquals(-1, second.compareTo(third));
	}

	@Test
	void testImportContentWithNoVersions() throws IOException, ParseException {
		// GIVEN
		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("nginx");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);

		BlueprintMockBuilder builder = new BlueprintMockBuilder("nginx");
		VraNgBlueprint blueprint = builder.build();

		fsMocks.blueprintFsMocks().addBlueprint(blueprint);

		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), new String[]{"nginx"});
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "nginx"), new String[]{"content.yaml", "details.json"});

		List<VraNgBlueprint> bluePrintsOnServer = new ArrayList<>();

		when(restClient.getAllBlueprints()).thenReturn(bluePrintsOnServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createBlueprint(any());
		verify(restClient, never()).isBlueprintVersionPresent(any(), any());
		verify(restClient, times(1)).releaseBlueprintVersion(any(), any());
	}

	@Test
	void testImportContentFlipsVersionsWithDescending() throws IOException, ParseException {
		// GIVEN
		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("nginx");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);

		BlueprintMockBuilder builder = new BlueprintMockBuilder("nginx");
		VraNgBlueprint blueprint = builder.build();
		BlueprintVersionsMockBuilder versionsMockBuilder = new BlueprintVersionsMockBuilder(blueprint);
		versionsMockBuilder.setVersions(3);
		JsonArray tempArray = versionsMockBuilder.build();
		JsonArray versionsArray = new JsonArray();
		versionsArray.add(tempArray.get(2));
		versionsArray.add(tempArray.get(1));
		versionsArray.add(tempArray.get(0));

		fsMocks.blueprintFsMocks().addBlueprint(blueprint, versionsArray.toString());

		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), new String[]{"nginx"});
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "nginx"), new String[]{"content.yaml", "details.json", "versions.json"});

		List<VraNgBlueprint> bluePrintsOnServer = new ArrayList<>();

		when(restClient.getAllBlueprints()).thenReturn(bluePrintsOnServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createBlueprint(any());

		ArgumentCaptor<String> bpIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<JsonObject> jsonObjectCaptor = ArgumentCaptor.forClass(JsonObject.class);

		verify(restClient, times(3)).importBlueprintVersion(
			bpIdCaptor.capture(),
			jsonObjectCaptor.capture()
		);

		// Assert ordering
		Date first = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(0).get("createdAt"));
		Date second = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(1).get("createdAt"));
		Date third = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(2).get("createdAt"));

		// Assert ordering
		assertEquals(-1, first.compareTo(second));
		assertEquals(-1, second.compareTo(third));
	}

	@Test
	void testExportContentWithoutVersions() {
		// GIVEN
		List<VraNgBlueprint> blueprints = new ArrayList<>();
		BlueprintMockBuilder mockBuilder = new BlueprintMockBuilder("ngnix");

		blueprints.add(mockBuilder.setRequestScopeOrg(true).build());

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(null);
		when(restClient.getAllBlueprints()).thenReturn(blueprints);
		when(config.getIgnoreBlueprintVersions()).thenReturn(true);

		// TEST
		store.exportContent();

		String[] expectedBlueprintFiles = {"content.yaml", "details.json"};

		// VERIFY
		AssertionsHelper.assertFolderContainsFiles(
			fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "ngnix"), expectedBlueprintFiles);
	}

	@Test
	void testImportContentForNonExistingBlueprintsInConfiguration() throws IOException, ParseException {
		// GIVEN
		List<String> blueprintNames = new ArrayList<>();
		blueprintNames.add("xnign");

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(blueprintNames);

		BlueprintMockBuilder builder = new BlueprintMockBuilder("nginx");
		VraNgBlueprint blueprint = builder.build();
		BlueprintVersionsMockBuilder versionsMockBuilder = new BlueprintVersionsMockBuilder(blueprint);
		versionsMockBuilder.setVersions(3);
		JsonArray versionsArray = versionsMockBuilder.build();
		fsMocks.blueprintFsMocks().addBlueprint(blueprint, versionsArray.toString());

		AssertionsHelper.assertFolderContainsFiles(fsMocks.getTempFolderProjectPath(), new String[]{"nginx"});
		AssertionsHelper.assertFolderContainsFiles(fsMocks.findItemByNameInFolder(fsMocks.getTempFolderProjectPath(), "nginx"), new String[]{"content.yaml", "details.json", "versions.json"});

		List<VraNgBlueprint> bluePrintsOnServer = new ArrayList<>();

		when(restClient.getAllBlueprints()).thenReturn(bluePrintsOnServer);

		// START TEST
		store.importContent(tempFolder.getRoot());

		verify(restClient, times(0)).createBlueprint(any());

		when(vraNgPackageDescriptor.getBlueprint()).thenReturn(null);

		store.importContent(tempFolder.getRoot());

		// VERIFY
		verify(restClient, times(1)).createBlueprint(any());
		ArgumentCaptor<String> bpIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<JsonObject> jsonObjectCaptor = ArgumentCaptor.forClass(JsonObject.class);

		verify(restClient, times(3)).importBlueprintVersion(
			bpIdCaptor.capture(),
			jsonObjectCaptor.capture()
		);

		Date first = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(0).get("createdAt"));
		Date second = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(1).get("createdAt"));
		Date third = this.getDateFromJsonElement(jsonObjectCaptor.getAllValues().get(2).get("createdAt"));

		// Assert ordering
		assertEquals(-1, first.compareTo(second));
		assertEquals(-1, second.compareTo(third));
	}

	private Date getDateFromJsonElement(JsonElement jsonElement) throws ParseException {
		return new SimpleDateFormat(BlueprintVersionsMockBuilder.DATE_FORMAT).parse(
			jsonElement.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", ""));
	}
}
