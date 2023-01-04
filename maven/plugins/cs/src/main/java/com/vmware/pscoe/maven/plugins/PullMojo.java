package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.vmware.pscoe.iac.artifact.PackageStore;
import com.vmware.pscoe.iac.artifact.PackageStoreFactory;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;


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
		getLog().info("CS Pull Plugin: Executing in Project Base: \"" + project.getBasedir() + "\"");
		super.execute();

		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);
		Package pkg = PackageFactory.getInstance(PackageType.CS, pkgInfoProvider.getSourceDirectory());
		try {
            PackageStore<?> store = PackageStoreFactory.getInstance(getConfigurationForCs());
			store.exportPackage(pkg, new File(project.getBasedir(), "content.yaml"), dryrun);
		} catch (ConfigurationException e) {
			getLog().error(e);
			throw new MojoExecutionException(e, "Error pulling CS package", "Error pulling CS package");
		}
	}
}
