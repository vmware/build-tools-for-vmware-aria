package com.vmware.pscoe.maven.plugins;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Paths;
import java.util.ArrayList;

	/**
	 * #%L
	 * common.
	 * %%
	 * Copyright (C) 2023 VMware
	 * %%
	 * Build Tools for VMware Aria
	 * Copyright 2023 VMware, Inc.
	 * 
	 * This product is licensed to you under the BSD-2 license (the "License"). 
	 * You may not use this product except in compliance with the BSD-2 License.  
	 * 
	 * This product may include a number of subcomponents with separate copyright 
	 * notices and license terms. Your use of these subcomponents is subject to the 
	 * terms and conditions of the subcomponent's license, as noted in the LICENSE file.
	 * #L%
	 * 
	 *  This class is responsible for calling vropkgcmd.
 	 */
public abstract class AbstractVroPkgMojo extends AbstractMojo {
	/**
	 * Keeps a handle to the pom.xml.
	 */
	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	/**
	* private key to keystore.
	*/
	@Parameter(property = "vroPrivateKeyPem", defaultValue = "")
	private String privateKeyPem;

	/**
	* Certificate of keystore.
	*/
	@Parameter(property = "vroCertificatePem", defaultValue = "")
	private String keystoreCert;

	/**
	 *  password to keystore.
	 */
	@Parameter(property = "vroKeyPass", defaultValue = "")
	private String keystorePassword;

	/**
	 * Description provided in pom.xml.
	 */
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

		this.description = getFormattedDescription();

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

	/**
	 * 
	 * Replaces all line breaks in description with space.
	 * 
	 * @return formatted description
	 */
	private String getFormattedDescription() {
		String descStr = "";
		if (project.getDescription() != null) {
			descStr = project.getDescription();
		}
		return descStr.replaceAll("\\R", " ");
	}
	/**
	  * Responsible for creating a virtual vroPkg cmd file and executing it.
	  * @param srcType
	  * @param srcPath
	  * @param destType
	  * @param destPath
	  * @throws MojoExecutionException
	  * @throws MojoFailureException
	  */
	protected void runVroPkg(final String srcType, final String srcPath, final String destType, final String destPath)
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
