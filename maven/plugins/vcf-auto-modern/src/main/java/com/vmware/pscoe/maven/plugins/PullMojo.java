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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageStore;
import com.vmware.pscoe.iac.artifact.common.store.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "pull")
public class PullMojo extends AbstractIacMojo {
    
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = false, property = "dryrun", defaultValue = "false")
    private boolean dryrun;

    @Override
    protected void overwriteConfigurationPropertiesForType(PackageType type, Properties props) {
        props.setProperty(Configuration.IMPORT_OLD_VERSIONS, "true");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("vcfa-all-app-pull");
        } catch (IOException e) {
            throw new MojoExecutionException("Could not create a temp directory");
        }

        MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
        File pkgFile = tempDir.resolve(pkgInfoProvider.getPackageName() + "." + PackageType.VCF_AUTO_MODERN.getPackageExtension()).toFile();
        Package pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, pkgFile);
        
        try {
            PackageStore<?> store = PackageStoreFactory.getInstance(getConfigurationForVcfa());
            store.exportPackage(pkg, new File(project.getBasedir(), "content.yaml"), dryrun);
            PackageManager.copyContents(new File(pkg.getFilesystemPath()),
                    new File(pkgInfoProvider.getSourceDirectory().getAbsolutePath()));
        } catch (ConfigurationException | IOException e) {
            getLog().error(e);
            String message = String.format("Error pulling VCFA All App package : %s", e.getMessage());
            throw new MojoExecutionException(e, message, message);
        }
    }
}
