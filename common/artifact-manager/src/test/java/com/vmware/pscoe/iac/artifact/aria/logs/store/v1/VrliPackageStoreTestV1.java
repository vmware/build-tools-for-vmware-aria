/*-
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
package com.vmware.pscoe.iac.artifact.aria.logs.store.v1;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.core.util.FileUtils;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.aria.logs.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.v1.RestClientVrliV1;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.v1.models.AlertDTO;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.v1.models.ContentPackDTO;
import com.vmware.pscoe.iac.artifact.aria.logs.store.models.VrliPackageDescriptor;
import com.vmware.pscoe.iac.artifact.helpers.AssertionsHelper;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

public final class VrliPackageStoreTestV1 {
	private static final String ALERTS_DIR = "alerts";
	private static final String CONTENT_PACKS_DIR = "content_packs";
	private static final String CONTENT_PACK_NAMESPACE = "com.example.content.pack.v1";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	protected RestClientVrliV1 restClientApiVersion1;
	protected Package vrliPackage;
	protected ConfigurationVrli config;
	protected VrliPackageDescriptor descriptor;
	protected VrliPackageStoreV1 storeApiVersion1;

	@BeforeEach
	void init() {
		try {
			tempFolder.create();
		} catch (IOException e) {
			throw new RuntimeException("Could not create a temp folder");
		}

		restClientApiVersion1 = Mockito.mock(RestClientVrliV1.class);
		vrliPackage = PackageFactory.getInstance(PackageType.VRLI, tempFolder.getRoot());
		config = this.createConfig(Boolean.TRUE);
		descriptor = Mockito.mock(VrliPackageDescriptor.class);
		storeApiVersion1 = new VrliPackageStoreV1(restClientApiVersion1);

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
	void exportAlertsApiV1() throws IOException {
		// GIVEN
		Mockito.doReturn(getAlerts()).when(restClientApiVersion1).getAllAlerts();
		Mockito.doReturn(getAlertNames()).when(descriptor).getAlerts();
		File path = new File(vrliPackage.getFilesystemPath() + File.separator + ALERTS_DIR);

		// WHEN
		storeApiVersion1.exportPackage(vrliPackage, descriptor, false);

		// THEN
		AssertionsHelper.assertFolderContainsFiles(path, new String[] { "ApiV1Alert01.json" });
	}

	@Test
	void importAlertsApiV1() throws IOException {
		// GIVEN
		Mockito.doCallRealMethod().when(restClientApiVersion1).importAlert(getAlert());

		File path = new File(vrliPackage.getFilesystemPath() + File.separator + ALERTS_DIR);
		FileUtils.mkdir(path, true);
		File alertFile = new File(path + "/ApiV1Alert01.json");

		String alertJson = getAlert();
		Package vrliPackage = this.createPackage(path.getParent(), alertFile.getName(), alertJson, true);

		// WHEN
		try {
			storeApiVersion1.importPackage(vrliPackage, false, true);
		} catch (RuntimeException e) {
			// THEN
			Mockito.doThrow(e);
		}

		// THEN
		assertTrue(Boolean.TRUE);
	}

	@Test
	void exportContentPacksV1() throws Exception {
		// GIVEN
		Mockito.doReturn(getContentPacks()).when(restClientApiVersion1).getAllContentPacks();
		Mockito.doReturn(getContentPack()).when(restClientApiVersion1).getContentPack(CONTENT_PACK_NAMESPACE);
		Mockito.doReturn(getContentPackNames()).when(descriptor).getContentPacks();
		File path = new File(vrliPackage.getFilesystemPath() + File.separator + CONTENT_PACKS_DIR);

		// WHEN
		storeApiVersion1.exportPackage(vrliPackage, descriptor, false);

		// THEN
		AssertionsHelper.assertFolderContainsFiles(path, new String[] { "Example Content Pack Api V1.json" });
	}

	@Test
	void importContentPackV1() throws IOException, ConfigurationException {
		// GIVEN
		String contentPackName = "exampleContentPack";
		String contentPackJson = this.getContentPack();
		File path = new File(vrliPackage.getFilesystemPath() + File.separator + CONTENT_PACKS_DIR);
		FileUtils.mkdir(path, true);
		File contentPackFile = new File(path + "/ContentPack01.json");
		Package vrliPackage = this.createPackage(path.getParent(), contentPackFile.getName(), contentPackJson, false);
		Mockito.doReturn(getContentPacks()).when(restClientApiVersion1).getAllContentPacks();
		Mockito.doReturn(getContentPack()).when(restClientApiVersion1).getContentPack(CONTENT_PACK_NAMESPACE);
		Mockito.doReturn(getContentPackNames()).when(descriptor).getContentPacks();
		Mockito.doCallRealMethod().when(restClientApiVersion1).importContentPack(contentPackName, contentPackJson);

		// WHEN
		try {
			storeApiVersion1.importPackage(vrliPackage, false, true);
		} catch (RuntimeException e) {
			// THEN
			Mockito.doThrow(e);
		}

		// THEN
		assertTrue(Boolean.TRUE);
	}

	private List<AlertDTO> getAlerts() {
		AlertDTO alertDto = new AlertDTO();
		alertDto.setId("ApiV1Alert01");
		alertDto.setName("Example Alert Api V1");
		List<AlertDTO> retVal = new ArrayList<>();
		retVal.add(alertDto);

		return retVal;
	}

	private String getAlert() {
		AlertDTO alertDto = new AlertDTO();
		alertDto.setId("ApiV1Alert01");
		alertDto.setName("Example Alert Api V1");

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(alertDto);
		} catch (JsonProcessingException e) {
			Mockito.doThrow(e);
		}

		return null;
	}

	private List<ContentPackDTO> getContentPacks() {
		ContentPackDTO contentPackDto = new ContentPackDTO();
		contentPackDto.setName("Example Content Pack Api V1");
		contentPackDto.setNamespace(CONTENT_PACK_NAMESPACE);

		List<ContentPackDTO> retVal = new ArrayList<>();
		retVal.add(contentPackDto);

		return retVal;
	}

	private String getContentPack() {
		ContentPackDTO contentPackDto = new ContentPackDTO();
		contentPackDto.setName("Example Content Pack Api V1");
		contentPackDto.setNamespace(CONTENT_PACK_NAMESPACE);

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(contentPackDto);
		} catch (JsonProcessingException e) {
			Mockito.doThrow(e);
		}

		return null;
	}

	private List<String> getAlertNames() {
		return Arrays.asList(new String[] { "Example Alert Api V1" });
	}

	private List<String> getContentPackNames() {
		return Arrays.asList(new String[] { "Example Content Pack Api V1" });
	}

	private ConfigurationVrli createConfig(Boolean isOverwrite) {
		Properties configuration = new Properties();
		configuration.setProperty("host", "mockHost");
		configuration.setProperty("port", "443");
		configuration.setProperty("provider", "local");
		configuration.setProperty("packageImportOverwriteMode", isOverwrite.toString());

		try {
			return ConfigurationVrli.fromProperties(configuration);
		} catch (ConfigurationException e) {
			Mockito.doThrow(e);
		}

		return Mockito.mock(ConfigurationVrli.class);
	}

	private Package createPackage(String path, String payloadName, String payloadJson, boolean isAlert)
			throws IOException {
		File tempZip = new File(path, UUID.randomUUID() + ".zip");
		FileOutputStream fos = new FileOutputStream(tempZip);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		ZipEntry contentZipEntry;
		if (isAlert) {
			contentZipEntry = new ZipEntry("/" + ALERTS_DIR + "/" + payloadName);
		} else {
			contentZipEntry = new ZipEntry("/" + CONTENT_PACKS_DIR + "/" + payloadName);
		}
		zipOut.putNextEntry(contentZipEntry);
		zipOut.write(payloadJson.getBytes(StandardCharsets.UTF_8));

		zipOut.close();
		fos.close();

		return PackageFactory.getInstance(PackageType.VRLI, tempZip);
	}
}
