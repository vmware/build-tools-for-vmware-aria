package com.vmware.pscoe.maven.plugins;

/*-
 * #%L
 * idem-package-maven-plugin
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

import org.apache.maven.plugins.annotations.LifecyclePhase;

import com.vmware.pscoe.iac.artifact.PackageManager;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationIdem;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File dir;

    @Parameter(defaultValue = "${idem.pkgFolder}", readonly = true)
    private File idemPkgDir;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        MavenProjectPackageInfoProvider pkgInfoPrvdr = new MavenProjectPackageInfoProvider(project);

        getLog().info("basedir " + project.getBasedir());
        File pkgFile = new File(dir, pkgInfoPrvdr.getPackageName() + "." + PackageType.IDEM.getPackageExtention());
        getLog().info("Target Idem package file " + pkgFile.getAbsolutePath());

        Package pkg = PackageFactory.getInstance(PackageType.IDEM, pkgFile);
        try {

            getLog().info("Packaging Idem bundle from: " + idemPkgDir);
            PackageManager mgr = new PackageManager(pkg);
            mgr.pack(idemPkgDir);
            File installFile = new File(dir, ConfigurationIdem.IDEM_PLUGIN_INSTALL_FILE);
            if (!installFile.exists()) {
                // Add the install script to the target folder
                createInstallScript(installFile);
            }

            mgr.addTextFileToExistingZip(installFile, Paths.get("."));
            project.getArtifact().setFile(pkgFile);
        } catch (Exception e) {
            String message = String.format("Error creating Idem-plugin bundle: %s", e.getMessage());
            throw new MojoExecutionException(e, message, message);
        }
    }

    private void createInstallScript(File installFile) throws MojoExecutionException {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream(ConfigurationIdem.IDEM_PLUGIN_INSTALL_FILE);
                OutputStream os = new FileOutputStream(installFile)) {
            IOUtil.copy(in, os);
            getLog().debug("Adding to package: " + ConfigurationIdem.IDEM_PLUGIN_INSTALL_FILE);
        } catch (IOException e) {
            throw new MojoExecutionException("Error adding install script to Idem-plugin bundle", e);
        }
    }

}