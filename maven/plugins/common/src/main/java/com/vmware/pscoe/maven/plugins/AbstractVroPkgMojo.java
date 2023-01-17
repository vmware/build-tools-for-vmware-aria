package com.vmware.pscoe.maven.plugins;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class AbstractVroPkgMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	@Parameter(property = "vroPrivateKeyPem", defaultValue = "")
	private String privateKeyPem;

	@Parameter(property = "vroCertificatePem", defaultValue = "")
	private String keystoreCert;

	@Parameter(property = "vroKeyPass", defaultValue = "")
	private String keystorePassword;

	private String description;

	private void validateVroPkgParams() {
		if (privateKeyPem == null || privateKeyPem == "" || privateKeyPem.trim() == "") {
			throw new IllegalArgumentException("privateKeyPem must be provided.");
		}
		if (keystoreCert == null || keystoreCert == "" || keystoreCert.trim() == "") {
			throw new IllegalArgumentException("keystoreCert must be provided.");
		}
		if (keystorePassword == null || keystorePassword == "" || keystorePassword.trim() == "") {
			throw new IllegalArgumentException("keystorePassword must be provided.");
		}

		if (project.getDescription() == null) {
			this.description = "";
		} else {
			this.description = project.getDescription();
		}

		if (project.getVersion() == null || project.getVersion() == "" || project.getVersion().trim() == "") {
			throw new IllegalArgumentException("Project version is missing.");
		}

		if (project.getPackaging() == null || project.getPackaging() == "" || project.getPackaging().trim() == "") {
			throw new IllegalArgumentException("Project packaging is missing.");
		}

		if (project.getArtifactId() == null || project.getArtifactId() == "" || project.getArtifactId().trim() == "") {
			throw new IllegalArgumentException("Project artifactId is missing.");
		}

		if (project.getGroupId() == null || project.getGroupId() == "" || project.getGroupId().trim() == "") {
			throw new IllegalArgumentException("Project groupId is missing.");
		}
	}

	protected void runVroPkg(String srcType, String srcPath, String destType, String destPath)
			throws MojoExecutionException, MojoFailureException {

		this.validateVroPkgParams();

		String projectRoot = project.getBasedir().toPath().toString();
		String vroPkgExec = SystemUtils.IS_OS_WINDOWS ? "vropkg.cmd" : "vropkg";
		ArrayList<String> vroPkgCmd = new ArrayList<>();

		vroPkgCmd.add(Paths.get(projectRoot, "node_modules", "@vmware-pscoe", "vropkg", "bin", vroPkgExec).toString());
		vroPkgCmd.add("--in");
		vroPkgCmd.add(srcType);
		vroPkgCmd.add("--srcPath");
		vroPkgCmd.add(srcPath);
		vroPkgCmd.add("--out");
		vroPkgCmd.add(destType);
		vroPkgCmd.add("--destPath");
		vroPkgCmd.add(destPath);
		vroPkgCmd.add("--privateKeyPEM");
		vroPkgCmd.add(privateKeyPem);
		vroPkgCmd.add("--certificatesPEM");
		vroPkgCmd.add(keystoreCert);
		vroPkgCmd.add("--keyPass");
		vroPkgCmd.add(keystorePassword);
		vroPkgCmd.add("--version");
		vroPkgCmd.add(project.getVersion());
		vroPkgCmd.add("--packaging");
		vroPkgCmd.add(project.getPackaging());
		vroPkgCmd.add("--artifactId");
		vroPkgCmd.add(project.getArtifactId());
		vroPkgCmd.add("--description");
		vroPkgCmd.add(description);
		vroPkgCmd.add("--groupId");
		vroPkgCmd.add(project.getGroupId());

		new ProcessExecutor().name("Running vropkg...").directory(project.getBasedir()).throwOnError(true)
				.command(vroPkgCmd).execute(getLog());
	}
}
