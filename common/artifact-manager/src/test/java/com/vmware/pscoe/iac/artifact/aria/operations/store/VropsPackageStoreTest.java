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
package com.vmware.pscoe.iac.artifact.aria.operations.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.jcraft.jsch.JSchException;
import com.vmware.pscoe.iac.artifact.PackageMocked;
import com.vmware.pscoe.iac.artifact.aria.operations.cli.CliManagerVrops;
import com.vmware.pscoe.iac.artifact.aria.operations.models.AuthGroupDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.models.AuthUserDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.models.PolicyDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.models.ReportDefinitionDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.models.SupermetricDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.models.ViewDefinitionDTO;
import com.vmware.pscoe.iac.artifact.aria.operations.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.aria.operations.store.models.VropsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.aria.operations.store.models.VropsPackageMemberType;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

public class VropsPackageStoreTest {
	private static String VROPS_VERSION_8_17 = "8.17.0";
	private static String VROPS_VERSION_8_10 = "8.10.0";

	/**
	 * Temp Folder.
	 */
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	void exportPackageWhenPackageIsAvailable() throws Exception {
		// GIVEN
		tempFolder.create();
		String testViewName = "Test View";
		File packageDir = tempFolder.newFolder();

		CliManagerVrops cliMock = getCliManagerMock(testViewName);
		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);

		ReportDefinitionDTO allReportDefs = new ReportDefinitionDTO();
		List<ReportDefinitionDTO.ReportDefinition> reportDefinitions = new ArrayList<>();
		ReportDefinitionDTO.ReportDefinition repDef = new ReportDefinitionDTO.ReportDefinition();
		repDef.setName("Test report definition");
		reportDefinitions.add(repDef);
		allReportDefs.setReportDefinitions(reportDefinitions);

		SupermetricDTO allSupermetrics = new SupermetricDTO();
		List<SupermetricDTO.SuperMetric> superMetrics = new ArrayList<>();
		SupermetricDTO.SuperMetric supermetric = new SupermetricDTO.SuperMetric();
		supermetric.setName("Test supermetric");
		superMetrics.add(supermetric);
		allSupermetrics.setSuperMetrics(superMetrics);

		ViewDefinitionDTO allViewDefs = new ViewDefinitionDTO();
		List<ViewDefinitionDTO.ViewDefinition> viewDefs = new ArrayList<>();
		ViewDefinitionDTO.ViewDefinition viewDef = new ViewDefinitionDTO.ViewDefinition();
		viewDef.setName(testViewName);
		viewDefs.add(viewDef);
		allViewDefs.setViewDefinitions(viewDefs);

		PolicyDTO.Policy defaultPolicy = new PolicyDTO.Policy();
		defaultPolicy.setName("default policy");
		defaultPolicy.setId("1");
		defaultPolicy.setPriority(Long.valueOf(1));
		defaultPolicy.setZipFile(new byte[] { '1' });

		Mockito.doReturn(allReportDefs).when(restClientMock).getAllReportDefinitions();
		Mockito.doReturn(allSupermetrics).when(restClientMock).getAllSupermetrics();
		Mockito.doReturn(allViewDefs).when(restClientMock).getAllViewDefinitions();
		Mockito.doReturn(defaultPolicy).when(restClientMock).getDefaultPolicy();

		VropsPackageStore store = new VropsPackageStore(cliMock, restClientMock);
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageDir);
		VropsPackageDescriptor descriptor = getVropsPackageDescriptorMock(testViewName, defaultPolicy.getName());

		// WHEN
		store.exportPackage(vropsPkg, descriptor, false);

		// THEN
		File views = new File(packageDir, "views");
		assertTrue(views.exists());
		assertTrue(new File(views, "resources").exists());
	}

	@Test
	void importPackageWhenPackageIsOkForVrops812andAbove() throws Exception {
		this.importVropsPackage(VROPS_VERSION_8_17);
	}

	@Test
	void importPackageWhenPackageIsOkForVrops812andBelow() throws Exception {
		this.importVropsPackage(VROPS_VERSION_8_10);
	}

	private void importVropsPackage(String vropsVersion) throws Exception {
		// GIVEN
		tempFolder.create();

		String testCustomGroupName = "Test custom group";
		String testCustomGroupPayload = "{}";
		String existingGroup = "group1";
		String existingUser = "user1";
		String existingDashboard = "DashboardName";
		String defaultPolicy = "Default Policy";
		String policyId = "1";

		String[] shareGroups = new String[] { existingGroup };
		String[] unshareGroups = new String[] { existingGroup };
		String[] activateGroups = new String[] { existingUser };
		String[] activateUsers = new String[] { existingUser };

		List<AuthGroupDTO> allGroups = new ArrayList<>();
		AuthGroupDTO group1Dto = new AuthGroupDTO();
		group1Dto.setDisplayName(existingGroup);
		allGroups.add(group1Dto);

		List<AuthUserDTO> allUsers = new ArrayList<>();
		AuthUserDTO user1Dto = new AuthUserDTO();
		user1Dto.setUsername(existingUser);
		allUsers.add(user1Dto);

		CliManagerVrops cliMock = Mockito.mock(CliManagerVrops.class);
		Mockito.doNothing().when(cliMock).connect();
		Mockito.doNothing().when(cliMock).importFilesToVrops();
		Mockito.doNothing().when(cliMock).addDashboardToImportList(Mockito.isA(File.class));

		Mockito.doNothing().when(cliMock).activateDashboard(existingDashboard, Arrays.asList(activateGroups), true);
		Mockito.doNothing().when(cliMock).activateDashboard(existingDashboard, Arrays.asList(activateUsers), false);
		Mockito.doNothing().when(cliMock).deactivateDashboard(existingDashboard, Arrays.asList(activateGroups), true);
		Mockito.doNothing().when(cliMock).deactivateDashboard(existingDashboard, Arrays.asList(activateUsers), false);

		Mockito.doNothing().when(cliMock).shareDashboard(existingDashboard, shareGroups);
		Mockito.doNothing().when(cliMock).unshareDashboard(existingDashboard, unshareGroups);

		Mockito.doNothing().when(cliMock).addViewToImportList(Mockito.isA(File.class));
		Mockito.doNothing().when(cliMock).addReportToImportList(Mockito.isA(File.class));

		Mockito.doReturn(true).when(cliMock).hasAnyCommands();

		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
		Mockito.doReturn(vropsVersion).when(restClientMock).getVersion();
		Mockito.doReturn(allGroups).when(restClientMock).findAllAuthGroups();
		Mockito.doReturn(allUsers).when(restClientMock).findAllAuthUsers();
		Mockito.doReturn(allGroups).when(restClientMock)
				.findAuthGroupsByNames(Arrays.asList(new String[] { existingGroup }));
		Mockito.doReturn(allUsers).when(restClientMock)
				.findAuthUsersByNames(Arrays.asList(new String[] { existingUser }));

		Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(),
				VropsPackageMemberType.ALERT_DEFINITION, new HashMap<>());
		Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(),
				VropsPackageMemberType.SYMPTOM_DEFINITION, new HashMap<>());
		Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(),
				VropsPackageMemberType.RECOMMENDATION, new HashMap<>());
		Mockito.doNothing().when(restClientMock).importCustomGroupInVrops(testCustomGroupName, testCustomGroupPayload);
		Mockito.doNothing().when(restClientMock).setDefaultPolicy(defaultPolicy);
		Mockito.doNothing().when(restClientMock).setPolicyPriorities(Arrays.asList(new String[] { policyId }));
		Mockito.doNothing().when(restClientMock).importPolicyFromZip(any(), Mockito.isA(File.class), anyBoolean());

		VropsPackageStore store = new VropsPackageStore(cliMock, restClientMock, tempFolder.newFolder());

		File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123",
				existingDashboard, "AlertDefinitions");
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		List<Package> packages = new ArrayList<>();
		packages.add(vropsPkg);

		// WHEN
		List<Package> importedPackages = store.importAllPackages(packages, false, false);

		// THEN
		assertNotNull(importedPackages);
		assertEquals(importedPackages.size(), packages.size());
		Mockito.verify(cliMock, Mockito.times(packages.size())).connect();
		Mockito.verify(cliMock, Mockito.times(packages.size())).importFilesToVrops();
	}

	@Test
	void importPackageUsesContentManagementApiForVcfOps9() throws Exception {
		// GIVEN
		tempFolder.create();
		CliManagerVrops cliMock = Mockito.mock(CliManagerVrops.class);
		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
		Mockito.doReturn(true).when(restClientMock).isVersionAbove90();
		Mockito.doReturn(Arrays.asList("admin")).when(restClientMock).getDashboardImportUsers();
		List<String> importedContent = new ArrayList<>();
		Mockito.doAnswer(invocation -> {
			File bundle = invocation.getArgument(0);
			assertTrue(bundle.exists());
			importedContent.add(invocation.getArgument(1));
			return null;
		}).when(restClientMock).importContent(Mockito.isA(File.class), Mockito.anyString());

		VropsPackageStore store = new VropsPackageStore(cliMock, restClientMock, tempFolder.newFolder());
		File packageZip = createVcfOpsPackageZip(tempFolder.newFolder());
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);

		// WHEN
		store.importPackage(vropsPkg, false, false);

		// THEN
		assertEquals(Arrays.asList("super metrics and metric configurations", "view 'Test View'",
				"dashboard 'Test Dashboard' for user(s) 'admin'"), importedContent);
		Mockito.verify(cliMock, Mockito.never()).importFilesToVrops();
		Mockito.verify(cliMock, Mockito.never()).addDashboardToImportList(Mockito.any());
	}

	@Test
	void selectContentProcessesAllWhenNull() throws Exception {
		tempFolder.create();
		VropsPackageStore store = new VropsPackageStore(null, null, tempFolder.newFolder());

		assertEquals(Arrays.asList("A", "B"), store.selectContent("views", null, Arrays.asList("A", "B", null)));
	}

	@Test
	void selectContentProcessesNothingWhenEmpty() throws Exception {
		tempFolder.create();
		VropsPackageStore store = new VropsPackageStore(null, null, tempFolder.newFolder());

		assertEquals(Collections.emptyList(),
				store.selectContent("views", Collections.emptyList(), Arrays.asList("A", "B")));
	}

	@Test
	void selectContentProcessesListedAndMatchingItems() throws Exception {
		tempFolder.create();
		VropsPackageStore store = new VropsPackageStore(null, null, tempFolder.newFolder());

		assertEquals(Arrays.asList("Cluster View", "VM View", "VM Other"), store.selectContent("views",
				Arrays.asList("Cluster View", "VM*", "Nothing*"), Arrays.asList("VM View", "Cluster View", "VM Other")));
	}

	@Test
	void selectContentFailsWhenListedItemIsMissing() throws Exception {
		tempFolder.create();
		VropsPackageStore store = new VropsPackageStore(null, null, tempFolder.newFolder());

		RuntimeException e = assertThrows(RuntimeException.class, () -> store.selectContent("views",
				Arrays.asList("Cluster View", "Missing View"), Arrays.asList("Cluster View")));
		assertTrue(e.getMessage().contains("Missing View"), e.getMessage());
	}

	@Test
	void selectAssetFilesFollowsContentYamlRules() throws Exception {
		// GIVEN
		tempFolder.create();
		File dir = tempFolder.newFolder();
		for (String name : new String[] { "VM View.json", "VM Other.json", "Cluster View.json",
				"Min%2FMax View.json", "readme.txt" }) {
			new File(dir, name).createNewFile();
		}
		new File(dir, "resources").mkdir();

		// WHEN / THEN
		assertEquals(Arrays.asList("Cluster View.json", "Min%2FMax View.json", "VM Other.json", "VM View.json"),
				names(VropsPackageStore.selectAssetFiles("views", dir, null, "json", "json")));
		assertEquals(Collections.emptyList(),
				names(VropsPackageStore.selectAssetFiles("views", dir, Collections.emptyList(), "json", "json")));
		assertEquals(Arrays.asList("Min%2FMax View.json", "VM Other.json", "VM View.json"),
				names(VropsPackageStore.selectAssetFiles("views", dir,
						Arrays.asList("Min/Max View", "VM*", "Nothing*"), "json", "json")));
		IOException e = assertThrows(IOException.class, () -> VropsPackageStore.selectAssetFiles("views", dir,
				Arrays.asList("VM View", "Missing View"), "json", "json"));
		assertTrue(e.getMessage().contains("Missing View"), e.getMessage());
	}

	@Test
	void selectAssetFilesWithFallbackExtension() throws Exception {
		// GIVEN
		tempFolder.create();
		File dir = tempFolder.newFolder();
		new File(dir, "Host-Capacity").createNewFile();
		new File(dir, "sample.xml").createNewFile();

		// WHEN / THEN
		assertEquals(Arrays.asList("Host-Capacity", "sample.xml"),
				names(VropsPackageStore.selectAssetFiles("metric configurations", dir,
						Arrays.asList("Host-Capacity", "sample"), "xml", null)));
		assertEquals(Arrays.asList("Host-Capacity", "sample.xml"),
				names(VropsPackageStore.selectAssetFiles("metric configurations", dir, null, "xml", null)));
	}

	@Test
	void selectAssetFilesOfDirectories() throws Exception {
		// GIVEN
		tempFolder.create();
		File dir = tempFolder.newFolder();
		new File(dir, "Report One").mkdir();
		new File(dir, "Report Two").mkdir();

		// WHEN / THEN
		assertEquals(Arrays.asList("Report One", "Report Two"),
				names(VropsPackageStore.selectAssetFiles("reports", dir, null, null, null)));
		assertEquals(Arrays.asList("Report Two"),
				names(VropsPackageStore.selectAssetFiles("reports", dir, Arrays.asList("Report Two"), null, null)));
	}

	@Test
	void toFileNameEscapesPathSeparatorsDistinctly() {
		assertEquals("VMs Min%2FMax", VropsPackageStore.toFileName("VMs Min/Max"));
		assertEquals("Capacity %5C Idle VM", VropsPackageStore.toFileName("Capacity \\ Idle VM"));
		assertEquals("Capacity | Idle VM", VropsPackageStore.toFileName("Capacity | Idle VM"));
		assertEquals("Capacity \\ Idle VM/x", VropsPackageStore.fromFileName("Capacity %5C Idle VM%2Fx"));
	}

	@Test
	void getReportIdReadsTheReportDefinition() throws Exception {
		tempFolder.create();
		File content = new File(tempFolder.newFolder(), "content.xml");
		Files.write(content.toPath(), "<Content><Reports><ReportDef id=\"r-1\"/></Reports></Content>"
				.getBytes(StandardCharsets.UTF_8));

		assertEquals("r-1", VropsPackageStore.getReportId(content));
	}

	@Test
	void exportPackageUsesContentManagementApiForVcfOps9() throws Exception {
		// GIVEN
		tempFolder.create();
		File packageDir = tempFolder.newFolder();
		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
		Mockito.doReturn(true).when(restClientMock).isVersionAbove90();
		Mockito.doReturn("admin").when(restClientMock).getDashboardUser();

		ViewDefinitionDTO views = new ViewDefinitionDTO();
		ViewDefinitionDTO.ViewDefinition view = new ViewDefinitionDTO.ViewDefinition();
		view.setName("My/View");
		view.setId(VropsContentExportMocked.VIEW_1_ID);
		views.setViewDefinitions(Arrays.asList(view));
		Mockito.doReturn(views).when(restClientMock).getAllViewDefinitions();
		SupermetricDTO superMetrics = new SupermetricDTO();
		for (String name : new String[] { "SM 1", "SM 2" }) {
			SupermetricDTO.SuperMetric superMetric = new SupermetricDTO.SuperMetric();
			superMetric.setName(name);
			superMetrics.getSuperMetrics().add(superMetric);
		}
		Mockito.doReturn(superMetrics).when(restClientMock).getAllSupermetrics();
		List<List<String>> exportedTypes = new ArrayList<>();
		Mockito.doAnswer(invocation -> {
			exportedTypes.add(invocation.getArgument(0));
			VropsContentExportMocked.createExportBundle(invocation.getArgument(1));
			return null;
		}).when(restClientMock).exportContent(Mockito.anyList(), Mockito.isA(File.class));

		VropsPackageDescriptor descriptor = new VropsPackageDescriptor();
		descriptor.setView(Arrays.asList("My/View"));
		descriptor.setDashboard(Arrays.asList("Dash One", "Dash Two"));
		descriptor.setSuperMetric(Arrays.asList("SM 2"));

		VropsPackageStore store = new VropsPackageStore(Mockito.mock(CliManagerVrops.class), restClientMock,
				tempFolder.newFolder());

		// WHEN
		store.exportPackage(PackageFactory.getInstance(PackageType.VROPS, packageDir), descriptor, false);

		// THEN
		assertEquals(Arrays.asList(Arrays.asList("VIEW_DEFINITIONS", "DASHBOARDS", "SUPER_METRICS")), exportedTypes);
		assertTrue(new File(packageDir, "views/My%2FView.xml").exists());
		assertTrue(new File(packageDir, "dashboards/Dash One.json").exists());
		assertTrue(new File(packageDir, "dashboards/Dash Two.json").exists());
		assertTrue(new File(packageDir, "dashboards/metadata/dashboardSharingMetadata.vrops.json").exists());
		assertTrue(new File(packageDir, "supermetrics/SM 2.json").exists());
		assertFalse(new File(packageDir, "supermetrics/SM 1.json").exists());
		assertFalse(new File(packageDir, "reports").exists());
	}

	@Test
	void exportPackageFailsWhenDashboardImportIsNotCompleted() throws Exception {
		// GIVEN
		tempFolder.create();
		File packageDir = tempFolder.newFolder();
		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
		Mockito.doReturn(true).when(restClientMock).isVersionAbove90();
		Mockito.doReturn("admin").when(restClientMock).getDashboardUser();
		Mockito.doAnswer(invocation -> VropsContentExportMocked.createExportBundle(invocation.getArgument(1)))
				.when(restClientMock).exportContent(Mockito.anyList(), Mockito.isA(File.class));

		VropsPackageDescriptor descriptor = new VropsPackageDescriptor();
		descriptor.setDashboard(null);

		VropsPackageStore store = new VropsPackageStore(Mockito.mock(CliManagerVrops.class), restClientMock,
				tempFolder.newFolder());

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class, () -> store
				.exportPackage(PackageFactory.getInstance(PackageType.VROPS, packageDir), descriptor, false));

		// THEN
		assertTrue(e.getMessage().contains("dashboards [Dash Pending] of user 'admin' is not completed"),
				e.getMessage());
		assertFalse(new File(packageDir, "dashboards").exists());
	}

	@Test
	void exportPackageFailsBeforeTheExportWhenListedViewIsMissing() throws Exception {
		// GIVEN
		tempFolder.create();
		RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
		Mockito.doReturn(true).when(restClientMock).isVersionAbove90();
		Mockito.doReturn(new ViewDefinitionDTO()).when(restClientMock).getAllViewDefinitions();
		VropsPackageDescriptor descriptor = new VropsPackageDescriptor();
		descriptor.setView(Arrays.asList("Missing View"));
		VropsPackageStore store = new VropsPackageStore(Mockito.mock(CliManagerVrops.class), restClientMock,
				tempFolder.newFolder());

		// WHEN
		RuntimeException e = assertThrows(RuntimeException.class, () -> store.exportPackage(
				PackageFactory.getInstance(PackageType.VROPS, tempFolder.newFolder()), descriptor, false));

		// THEN
		assertTrue(e.getMessage().contains("Missing View"), e.getMessage());
		Mockito.verify(restClientMock, Mockito.never()).exportContent(Mockito.anyList(), Mockito.any());
	}

	private static List<String> names(List<File> files) {
		List<String> names = new ArrayList<>();
		files.forEach(file -> names.add(file.getName()));
		return names;
	}

	private static File createVcfOpsPackageZip(File dir) throws IOException {
		File packageZip = new File(dir, "vcfops.zip");
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(packageZip))) {
			addZipEntry(zipOut, "content.yaml", "---\nview:\n  - Test View\n");
			addZipEntry(zipOut, "views/Test View.xml",
					"<Content><Views><ViewDef id=\"view-id\"></ViewDef></Views></Content>");
			addZipEntry(zipOut, "dashboards/Test Dashboard.json", "{\"entries\": {}, \"dashboards\": [{}]}");
			addZipEntry(zipOut, "supermetrics/Test Super Metric.json", "{\"sm-id\": {\"name\": \"Test Super Metric\"}}");
		}

		return packageZip;
	}

	private static void addZipEntry(ZipOutputStream zipOut, String name, String content) throws IOException {
		zipOut.putNextEntry(new ZipEntry(name));
		zipOut.write(content.getBytes(StandardCharsets.UTF_8));
		zipOut.closeEntry();
	}

	private static VropsPackageDescriptor getVropsPackageDescriptorMock(String viewName, String policyName) {
		VropsPackageDescriptor mock = new VropsPackageDescriptor() {
			@Override
			public List<String> getView() {
				List<String> list = new ArrayList<String>();
				list.add(viewName);
				return list;
			}

			@Override
			public String getDefaultPolicy() {
				return policyName;
			}
		};
		return mock;
	}

	private static CliManagerVrops getCliManagerMock(String testViewName) {
		CliManagerVrops mock = new CliManagerVrops(null) {
			@Override
			public void connect() throws JSchException {
			}

			@Override
			public void close() {
			}

			@Override
			public void addViewToImportList(File file) {
			}

			@Override
			public void addDashboardToImportList(File file) {
			}

			@Override
			public void addReportToImportList(File file) {
			}

			@Override
			public void addSuperMetricsToImportList(File file) {
			}

			@Override
			public void addMetricConfigsToImportList(File file) {
			}

			@Override
			public void importFilesToVrops() {
			}

			@Override
			public void shareDashboard(String dashboard, String[] groups) {
			}

			@Override
			public void unshareDashboard(String dashboard, String[] groups) {
			}

			@Override
			public void activateDashboard(String dashboard, List<String> resources, boolean isGroupResource) {
			}

			@Override
			public void deactivateDashboard(String dashboard, List<String> resources, boolean isGroupResource) {
			}

			@Override
			public boolean hasAnyCommands() {
				return true;
			}

			@Override
			public void exportDashboard(String dashboardName, File localDir) throws JSchException {
			}

			@Override
			public void exportView(String viewName, File localDir) throws JSchException {
				if (viewName == null || !viewName.trim().equalsIgnoreCase(testViewName)) {
					return;
				}
				try {
					File zip = PackageMocked.createSampleViewsZip(localDir);
					zip.renameTo(new File(zip.getParent(), testViewName + ".zip"));
				} catch (IOException ioe) {
					throw new JSchException(ioe.getLocalizedMessage(), ioe);
				}
			}

		};
		return mock;
	}
}
