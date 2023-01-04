package com.vmware.pscoe.iac.artifact.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.vmware.pscoe.iac.artifact.PackageMocked;
import com.vmware.pscoe.iac.artifact.VropsPackageStore;
import com.vmware.pscoe.iac.artifact.cli.CliManagerVrops;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vrops.VropsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrops.VropsPackageMemberType;
import com.vmware.pscoe.iac.artifact.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ReportDefinitionDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.SupermetricDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ViewDefinitionDTO;

public class VropsPackageStoreTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    void exportPackageWhenPackageIsAvailable() throws IOException {
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

        Mockito.doReturn(allReportDefs).when(restClientMock).getAllReportDefinitions();
        Mockito.doReturn(allSupermetrics).when(restClientMock).getAllSupermetrics();
        Mockito.doReturn(allViewDefs).when(restClientMock).getAllViewDefinitions();

        VropsPackageStore store = new VropsPackageStore(cliMock, restClientMock);
        Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageDir);
        VropsPackageDescriptor descriptor = getVropsPackageDescriptorMock(testViewName);

        // WHEN
        Package pkg = store.exportPackage(vropsPkg, descriptor, false);

        // THEN
        File views = new File(packageDir, "views");
        assertTrue(views.exists());
        assertTrue(new File(views, "resources").exists());
    }

    @Test
    void importPackageWhenPackageIsOK() throws Exception {
        // GIVEN
        tempFolder.create();
        String testViewName = "Test View";
        String testCustomGroupName = "Test custom group";
        String testCustomGroupPayload = "{}";
        String[] shareGroups = new String[] { "group1", "group2", "group3" };
        String[] unshareGroups = new String[] { "group4", "group5", "group6" };
        CliManagerVrops cliMock = Mockito.mock(CliManagerVrops.class);
        Mockito.doNothing().when(cliMock).connect();
        Mockito.doNothing().when(cliMock).importFilesToVrops();
        Mockito.doNothing().when(cliMock).addDashboardToImportList(Mockito.isA(File.class));

        Mockito.doNothing().when(cliMock).shareDashboard("dashboard1", shareGroups);
        Mockito.doNothing().when(cliMock).unshareDashboard("dashboard2", unshareGroups);

        Mockito.doNothing().when(cliMock).addViewToImportList(Mockito.isA(File.class));
        Mockito.doNothing().when(cliMock).addReportToImportList(Mockito.isA(File.class));

        Mockito.doReturn(true).when(cliMock).hasAnyCommands();

        RestClientVrops restClientMock = Mockito.mock(RestClientVrops.class);
        Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(), VropsPackageMemberType.ALERT_DEFINITION, new HashMap<>());
        Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(), VropsPackageMemberType.SYMPTOM_DEFINITION, new HashMap<>());
        Mockito.doNothing().when(restClientMock).importDefinitionsInVrops(new HashMap<>(), VropsPackageMemberType.RECOMMENDATION, new HashMap<>());
        Mockito.doNothing().when(restClientMock).importCustomGroupInVrops(testCustomGroupName, testCustomGroupPayload, new HashMap<>());

        VropsPackageStore store = new VropsPackageStore(cliMock, restClientMock, tempFolder.newFolder());

        File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123", "DashboardName", "AlertDefinitions");
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

    private static VropsPackageDescriptor getVropsPackageDescriptorMock(String viewName) {
        VropsPackageDescriptor mock = new VropsPackageDescriptor() {
            @Override
            public List<String> getView() {
                List<String> list = new ArrayList<String>();
                list.add(viewName);
                return list;
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
            public boolean hasAnyCommands() {
                return true;
            }

            private void copyLocalToRemote() throws JSchException, IOException, SftpException {
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
