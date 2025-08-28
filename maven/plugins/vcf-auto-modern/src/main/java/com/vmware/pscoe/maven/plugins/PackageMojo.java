/*
 * #%L
 * vcf-auto-modern-package-maven-plugin
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
package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractVroMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);

        getLog().info("basedir " + project.getBasedir());
        File pkgFile = new File(directory,
                pkgInfoProvider.getPackageName() + "." + PackageType.VCF_AUTO_MODERN.getPackageExtension());
        getLog().info("Target VCFA All App package file " + pkgFile.getAbsolutePath());

        Package pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, pkgFile);
        try {

            getLog().info("Packaging VCFA All App bundle from: " + pkgInfoProvider.getSourceDirectory().getAbsolutePath());
            PackageManager mgr = new PackageManager(pkg);
            mgr.pack(pkgInfoProvider.getSourceDirectory());

            File contentFile = new File(project.getBasedir().getPath() + File.separator + "content.yaml");
            if (contentFile.exists()) {
                getLog().debug("Adding to package: " + contentFile.getName());
                mgr.addTextFileToExistingZip(contentFile, Paths.get("."));
            }

            project.getArtifact().setFile(pkgFile);
        } catch (IOException e) {
            String message = String.format("Error creating VCFA All App bundle: %s", e.getMessage());
            throw new MojoExecutionException(e, message, message);
        }
    }
}
