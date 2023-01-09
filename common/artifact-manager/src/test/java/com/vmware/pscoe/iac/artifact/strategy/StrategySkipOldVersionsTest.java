package com.vmware.pscoe.iac.artifact.strategy;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StrategySkipOldVersionsTest {

    @Test
    void filterHigherVersions() {
        List<String> sourcePackageNames = new ArrayList<>();
        sourcePackageNames.add("com.vmware.pscoe.library.vra.dispatcher-1.3.0"); // should be skipped b/c older
        sourcePackageNames.add("com.vmware.pscoe.library.nsx-1.3.0"); // should be imported b/c newer
        sourcePackageNames.add("com.vmware.pscoe.library.vc-3.11.0"); // should be skipped b/c same
        sourcePackageNames.add("com.vmware.pscoe.library.vrops-1.0.0"); // should be imported b/c not present
        List<Package> sourcePackages = sourcePackageNames.stream().map(c -> PackageFactory.getInstance(PackageType.VRO, new File(c + ".package"))).collect(Collectors.toList());

        List<String> destinationPackageNames = new ArrayList<>();
        destinationPackageNames.add("com.vmware.pscoe.library.vra.dispatcher-1.2.0");
        destinationPackageNames.add("com.vmware.pscoe.library.vra.dispatcher-2.3.0");
        destinationPackageNames.add("com.vmware.pscoe.library.nsx-1.2.0");
        destinationPackageNames.add("com.vmware.pscoe.library.vc-3.2.1");
        destinationPackageNames.add("com.vmware.pscoe.library.vc-3.11.0");
        List<Package> destinationPackages = destinationPackageNames.stream().map(c -> PackageFactory.getInstance(PackageType.VRO, new File(c + ".package"))).collect(Collectors.toList());

        StrategySkipOldVersions strategySkipOldVersions = new StrategySkipOldVersions();
        List<Package> imported = strategySkipOldVersions.filterHigherVersions(sourcePackages, destinationPackages);
        Set<String> importedNames = imported.stream().map(aPackage -> aPackage.getFQName()).collect(Collectors.toSet());
        assertEquals(2, importedNames.size());
        assertTrue(importedNames.contains("com.vmware.pscoe.library.nsx-1.3.0"));
        assertTrue(importedNames.contains("com.vmware.pscoe.library.vrops-1.0.0"));
    }

    @Test
    void filterHigherVersionsWithSnapshot() {
        List<String> sourcePackageNames = new ArrayList<>();
        sourcePackageNames.add("com.vmware.pscoe.library.vra.dispatcher-2.3.0-SNAPSHOT"); // should be imported b/c same version snapshot is considered newer
        sourcePackageNames.add("com.vmware.pscoe.library.vc-3.2.0"); // should be skipped b/c newer snapshot present
        sourcePackageNames.add("com.vmware.pscoe.library.nsx-1.3.0"); // should be imported b/c snapshot is considered older than released
        List<Package> sourcePackages = sourcePackageNames.stream().map(c -> PackageFactory.getInstance(PackageType.VRO, new File(c + ".package"))).collect(Collectors.toList());

        List<String> destinationPackageNames = new ArrayList<>();
        destinationPackageNames.add("com.vmware.pscoe.library.vra.dispatcher-1.3.0");
        destinationPackageNames.add("com.vmware.pscoe.library.vra.dispatcher-2.3.0-SNAPSHOT");
        destinationPackageNames.add("com.vmware.pscoe.library.nsx-1.3.0-SNAPSHOT");
        destinationPackageNames.add("com.vmware.pscoe.library.vc-3.2.1-SNAPSHOT");
        List<Package> destinationPackages = destinationPackageNames.stream().map(c -> PackageFactory.getInstance(PackageType.VRO, new File(c + ".package"))).collect(Collectors.toList());

        StrategySkipOldVersions strategySkipOldVersions = new StrategySkipOldVersions();
        List<Package> imported = strategySkipOldVersions.filterHigherVersions(sourcePackages, destinationPackages);
        Set<String> importedNames = imported.stream().map(aPackage -> aPackage.getFQName()).collect(Collectors.toSet());
        assertEquals(2, importedNames.size());
        assertTrue(importedNames.contains("com.vmware.pscoe.library.nsx-1.3.0"));
        assertTrue(importedNames.contains("com.vmware.pscoe.library.vra.dispatcher-2.3.0-SNAPSHOT"));
    }
}