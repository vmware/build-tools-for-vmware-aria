package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.util.Optional;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class CleanMojo extends AbstractIacMojo {
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(required = false, property = "dryrun", defaultValue = "false")
    private boolean dryrun;

    @Parameter(required = true, property = "includeDependencies", defaultValue = "true")
    private boolean includeDependencies;
    
    @Parameter(required = false, property = "cleanUpLastVersion", defaultValue = "false")
    private boolean cleanUpLastVersion;
    
    @Parameter(required = false, property = "cleanUpOldVersions", defaultValue = "true")
    private boolean cleanUpOldVersions;

    private void deleteArtifact(Artifact a) throws MojoExecutionException {
        PackageType pkgType = PackageType.fromExtension(a.getType());
        String artifactFile = String.format("%s.%s-%s.package", a.getGroupId(), a.getArtifactId(), a.getVersion());
        if (pkgType != null) {
            getLog().info("Package: " + artifactFile);
            getLog().info("Package type: " + pkgType.toString());
            com.vmware.pscoe.iac.artifact.model.Package pkg = PackageFactory.getInstance(pkgType, new File(artifactFile));
            try {
                PackageStore store = getConfigurationForType(PackageType.fromExtension(a.getType()))
                        .flatMap(configuration -> Optional.of(PackageStoreFactory.getInstance(configuration)))
                        .orElseThrow(() -> new ConfigurationException("Unable to find PackageStore based on configuration. "
                                + "Make sure there is configuration for type: " + pkgType.name()));
                store.deletePackage(pkg, cleanUpLastVersion, cleanUpOldVersions, dryrun);
            } catch (ConfigurationException e) {
                getLog().error(e);
                throw new MojoExecutionException(e, "Error processing configuration", "Error processing configuration");
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        final String artifactType = project.getArtifact().getType();
        final PackageType packageType = PackageType.fromExtension(artifactType);
        if (packageType == null) {
            getLog().warn(String.format("Skipping clean up because of unsupported artifact type '%s'", artifactType));
            return;
        }

        if (includeDependencies) {
            for (Object o : project.getArtifacts()) {
                deleteArtifact((Artifact) o);
            }
        }
        deleteArtifact(project.getArtifact());
    }

}
