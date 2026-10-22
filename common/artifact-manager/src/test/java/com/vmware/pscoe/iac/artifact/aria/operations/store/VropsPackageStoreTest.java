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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	// -------------------------------------------------------------------------
	// Content-validation tests (validateContentMatchesDescriptor)
	// -------------------------------------------------------------------------

	/**
	 * Valid XML for a view file accepted by getViewId().
	 */
	private static final String SAMPLE_VIEW_XML =
			"<Content><Views><ViewDef id=\"testid\"></ViewDef></Views></Content>";

	/**
	 * Builds a VropsPackageStore whose CLI manager does nothing and whose REST
	 * client returns safe empty/no-op responses. The store's temp directory is
	 * placed inside the shared tempFolder so it is cleaned up automatically.
	 */
	private VropsPackageStore createMinimalStore() throws IOException {
		CliManagerVrops cliMock = Mockito.mock(CliManagerVrops.class);
		Mockito.doReturn(false).when(cliMock).hasAnyCommands();
		Mockito.doNothing().when(cliMock).addViewToImportList(any(File.class));
		RestClientVrops restMock = Mockito.mock(RestClientVrops.class);
		return new VropsPackageStore(cliMock, restMock, tempFolder.newFolder());
	}

	/**
	 * Wraps the zip in a Package and invokes importAllPackages.
	 */
	private List<Package> doImport(VropsPackageStore store, File packageZip) {
		Package pkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		List<Package> pkgs = new ArrayList<>();
		pkgs.add(pkg);
		return store.importAllPackages(pkgs, false, false);
	}

	@Test
	void importPackageValidationPassesWhenContentYamlMatchesPackageExactly() throws Exception {
		// GIVEN: content.yaml lists exactly one view; package contains that view file
		tempFolder.create();
		String yaml = "---\nview:\n  - MyView\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: no exception expected
		List<Package> result = doImport(store, zip);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	void importPackageValidationPassesWhenWildcardPatternCoversAllPackageFiles() throws Exception {
		// GIVEN: content.yaml uses a wildcard; package has two views that both match
		tempFolder.create();
		String yaml = "---\nview:\n  - My*\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		entries.put("views/MyOtherView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: wildcard covers both files - no exception
		List<Package> result = doImport(store, zip);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	void importPackageValidationPassesWhenWildcardPatternMatchesNoFiles() throws Exception {
		// GIVEN: content.yaml has a wildcard for views; package has no view files at all.
		// Wildcards may legitimately match zero items on export, so no error is expected.
		tempFolder.create();
		String yaml = "---\nview:\n  - My*\n";
		Map<String, String> entries = new LinkedHashMap<>();
		// No view files in the package
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: no exception expected
		List<Package> result = doImport(store, zip);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	void importPackageValidationFailsWhenExactEntryInContentYamlIsMissingFromPackage() throws Exception {
		// GIVEN: content.yaml lists two views; package only contains one of them
		tempFolder.create();
		String yaml = "---\nview:\n  - MyView\n  - MissingView\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: RuntimeException must mention the missing view
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(store, zip));
		assertTrue(ex.getMessage().contains("MissingView"),
				"Exception message should name the missing item; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("missing in package"),
				"Exception message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationFailsWhenPackageContainsFileNotListedInContentYaml() throws Exception {
		// GIVEN: content.yaml lists only one view; package has an extra view file
		tempFolder.create();
		String yaml = "---\nview:\n  - MyView\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		entries.put("views/ExtraView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: RuntimeException must mention the uncovered file
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(store, zip));
		assertTrue(ex.getMessage().contains("ExtraView"),
				"Exception message should name the unlisted item; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("not listed in content.yaml"),
				"Exception message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationFailsWhenFileOnDiskIsNotCoveredByWildcard() throws Exception {
		// GIVEN: content.yaml has a wildcard "My*"; package has a view that does NOT match
		tempFolder.create();
		String yaml = "---\nview:\n  - My*\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/OtherView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: OtherView is not covered by "My*"
		RuntimeException ex = assertThrows(RuntimeException.class, () -> doImport(store, zip));
		assertTrue(ex.getMessage().contains("OtherView"),
				"Exception message should name the uncovered file; was: " + ex.getMessage());
		assertTrue(ex.getMessage().contains("not listed in content.yaml"),
				"Exception message should describe the problem; was: " + ex.getMessage());
	}

	@Test
	void importPackageValidationCoversMultipleContentTypes() throws Exception {
		// GIVEN: package has both views and alert definitions; content.yaml lists both
		tempFolder.create();
		String yaml = "---\nview:\n  - MyView\nalert-definition:\n  - MyAlert\n";
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		entries.put("alert_definitions/MyAlert.json", "{\"id\":\"1\"}");
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), yaml, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: both types match - no exception
		List<Package> result = doImport(store, zip);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	void importPackageValidationIsSkippedWhenNoContentYamlIsPresent() throws Exception {
		// GIVEN: package has a view file but NO content.yaml - validation must be skipped
		tempFolder.create();
		Map<String, String> entries = new LinkedHashMap<>();
		entries.put("views/MyView.xml", SAMPLE_VIEW_XML);
		File zip = PackageMocked.createVropsPackageZip(tempFolder.newFolder(), null, entries);

		VropsPackageStore store = createMinimalStore();

		// WHEN / THEN: no exception - validation is opt-in (only runs when content.yaml exists)
		List<Package> result = doImport(store, zip);
		assertNotNull(result);
		assertEquals(1, result.size());
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
