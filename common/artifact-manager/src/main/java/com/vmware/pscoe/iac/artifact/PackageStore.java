package com.vmware.pscoe.iac.artifact;

import java.io.File;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.Version;

/**
 * An interface representing working with packages from the view point of the server that consumes such packages.
 *
 * In that sense exporting a packaging is the process that includes contacting the server and downloading a package
 * file from it into a local file/folder structure.
 *
 * On the contrary, importing a packaging means to take some existing file/folder structure and upload it into the
 * server, where the server would take appropriate actions to properly apply the package.
 *
 * Locally, the package is represented by an {@link Package} object.
 * @param <T> Could be different for different sort of servers (vRA, vRO, vROps, etc...).
 */
public interface PackageStore<T extends PackageDescriptor> {
    
    String PACKAGE_LIST   = "Package | LIST   | %s";
    String PACKAGE_IMPORT = "Package | IMPORT | %s";
    String PACKAGE_EXPORT = "Package | EXPORT | %s";
	String PACKAGE_MERGE = "Package | MERGE | %s";

    List<Package> getPackages();

    List<Package> exportAllPackages(List<Package> pkg, boolean dryrun);

	List<Package> importAllPackages(List<Package> pkg, boolean dryrun);

	List<Package> importAllPackages(List<Package> pkg, boolean dryrun, boolean mergePackages);
    
    List<Package> deletePackage(Package pkg, boolean lastVersion, boolean oldVersions,  boolean dryrun);
    
    List<Package> deleteAllPackages(List<Package> vroPackages, boolean lastVersion, boolean oldVersions, boolean dryrun);

    Package exportPackage(Package pkg, T packageDescriptor, boolean dryrun);

    Package exportPackage(Package pkg, boolean dryrun);

    Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun);

    Package importPackage(Package pkg, boolean dryrun, boolean mergePackages);

    Version getProductVersion();
}
