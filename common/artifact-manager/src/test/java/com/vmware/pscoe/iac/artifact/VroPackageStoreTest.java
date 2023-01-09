package com.vmware.pscoe.iac.artifact;

import com.vmware.pscoe.iac.artifact.extentions.PackageStoreExtention;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vro.VroPackageDescriptor;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.strategy.Strategy;
import com.vmware.pscoe.iac.artifact.strategy.StrategySkipOldVersions;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VroPackageStoreTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private VroPackageStore vroPackageStore;
	private RestClientVro restClientVro;
	private List<PackageStoreExtention<VroPackageDescriptor>> extentions;

	@BeforeEach
	void init() {
		restClientVro = Mockito.mock(RestClientVro.class);

		PackageStoreExtention<VroPackageDescriptor> extention = new PackageStoreExtention<VroPackageDescriptor>() {

			@Override
			public Package exportPackage(Package serverPackage, VroPackageDescriptor packageDescriptor, boolean dryrun) {
				return serverPackage;
			}

			@Override
			public Package importPackage(Package filesystemPackage, boolean dryrun) {
				return filesystemPackage;
			}
		};

		ArrayList<Strategy> strategies = new ArrayList<>();
		strategies.add(new StrategySkipOldVersions());

		extentions = new ArrayList<>();
		extentions.add(extention);
		vroPackageStore = new VroPackageStore(restClientVro, strategies, extentions);
	}

	@Test
	void testImportAllPackagesShouldBeSucessfull() throws IOException {
		tempFolder.create();
		File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123", "DashboardName", "AlertDefinitions");
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		List<Package> packages = new ArrayList<>();
		packages.add(vropsPkg);

		Mockito
			.when(restClientVro.importPackage(vropsPkg, false, true))
			.thenReturn(vropsPkg);

		vroPackageStore.importAllPackages(packages, false, true);
	}


	@Test
	void testImportPackageShouldBeSucessfull() throws IOException {
		tempFolder.create();
		File packageZip = PackageMocked.createSamplePackageZip(tempFolder.newFolder(), "ViewName", "viewid123", "DashboardName", "AlertDefinitions");
		Package vropsPkg = PackageFactory.getInstance(PackageType.VROPS, packageZip);
		Mockito
			.when(restClientVro.importPackage(vropsPkg, false, true))
			.thenReturn(vropsPkg);

		vroPackageStore.importPackage(vropsPkg, false, true);
	}
}
