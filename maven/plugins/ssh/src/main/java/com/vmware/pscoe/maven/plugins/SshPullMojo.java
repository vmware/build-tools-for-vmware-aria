package com.vmware.pscoe.maven.plugins;

import com.google.common.io.Files;
import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.*;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.PackageManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

@Mojo(name = "pull")
public class SshPullMojo extends AbstractIacMojo {
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

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		File pkgFile = new File(Files.createTempDir(), pkgInfoProvider.getPackageName() + "." + PackageType.BASIC.getPackageExtention()); 
		com.vmware.pscoe.iac.artifact.model.Package pkg = PackageFactory.getInstance(PackageType.BASIC, pkgFile);
		try {
			PackageStore store = PackageStoreFactory.getInstance(getConfigurationForSsh());
			store.exportPackage(pkg, new File(project.getBasedir(), "content.yaml"), dryrun);
            PackageManager.copyContents(Paths.get(pkg.getFilesystemPath(), "content").toFile(), 
            		new File(project.getBasedir(), "src"));
		} catch (ConfigurationException | IOException e) {
			getLog().error(e);
			throw new MojoExecutionException(e, "Error pulling SSH package", "Error pulling SSH package");
		}
	}
}
