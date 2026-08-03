/*
 * #%L
 * vrli-package-maven-plugin
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.vmware.pscoe.iac.artifact.aria.logs.store.models.VrliPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractVroMojo {

	/**
	 * The name of the content descriptor file.
	 */
	private static final String CONTENT_YAML_FILE_NAME = "content.yaml";

	/**
	 * The name of the alerts directory.
	 */
	private static final String DIR_ALERTS = "alerts";

	/**
	 * The name of the content packs directory.
	 */
	private static final String DIR_CONTENT_PACKS = "content_packs";

	/**
	 * The JSON file extension.
	 */
	private static final String JSON_EXTENSION = "json";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);

		File contentYamlFile = new File(project.getBasedir(), CONTENT_YAML_FILE_NAME);
		if (!contentYamlFile.exists()) {
			throw new MojoExecutionException(
				"Missing required 'content.yaml' manifest descriptor in project base directory: "
					+ project.getBasedir().getAbsolutePath());
		}

		VrliPackageDescriptor descriptor = VrliPackageDescriptor.getInstance(contentYamlFile);
		File sourceDirectory = pkgInfoProvider.getSourceDirectory();

		// Validate that local content matches what is described in content.yaml
		validateContentMatchesDescriptor(sourceDirectory, descriptor);

		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory,
				pkgInfoProvider.getPackageName() + "." + PackageType.VRLI.getPackageExtension());
		getLog().info("Target VRLI package file " + pkgFile.getAbsolutePath());

		Package pkg = PackageFactory.getInstance(PackageType.VRLI, pkgFile);
		try {
			getLog().info("Packaging VRLI bundle from: " + sourceDirectory.getAbsolutePath());
			PackageManager mgr = new PackageManager(pkg);
			mgr.pack(sourceDirectory);
			// Include content.yaml in the package so it can be used during import
			mgr.addTextFileToExistingZip(contentYamlFile, Paths.get("."));
			project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
			String message = String.format("Error creating VRLI bundle: %s", e.getMessage());
			throw new MojoExecutionException(e, message, message);
		}
	}

	/**
	 * Validates that the local content in the source directory matches the content described in content.yaml.
	 * Both directions are checked: items in content.yaml must have corresponding files on disk,
	 * and files on disk must be listed in content.yaml.
	 *
	 * @param sourceDirectory the project source directory (src/main/resources)
	 * @param descriptor      the package descriptor loaded from content.yaml
	 * @throws MojoExecutionException if the local content does not match content.yaml
	 */
	private void validateContentMatchesDescriptor(final File sourceDirectory, final VrliPackageDescriptor descriptor)
			throws MojoExecutionException {
		List<String> errors = new ArrayList<>();

		// Validate alerts
		List<String> alertsInDescriptor = descriptor.getAlerts() != null
				? descriptor.getAlerts() : Collections.emptyList();
		File alertsDir = new File(sourceDirectory, DIR_ALERTS);
		List<String> alertsOnDisk = getFilesWithoutExtension(alertsDir, JSON_EXTENSION);
		reportMismatches("alerts", alertsInDescriptor, alertsOnDisk, errors);

		// Validate content packs
		List<String> contentPacksInDescriptor = descriptor.getContentPacks() != null
				? descriptor.getContentPacks() : Collections.emptyList();
		File contentPacksDir = new File(sourceDirectory, DIR_CONTENT_PACKS);
		List<String> contentPacksOnDisk = getFilesWithoutExtension(contentPacksDir, JSON_EXTENSION);
		reportMismatches("content-packs", contentPacksInDescriptor, contentPacksOnDisk, errors);

		if (!errors.isEmpty()) {
			throw new MojoExecutionException(
				"Local content does not match content.yaml descriptor. Please update either the local files "
					+ "or content.yaml to match the existing state:\n" + String.join("\n", errors));
		}
	}

	/**
	 * Returns a sorted list of file names (without extension) in the given directory.
	 *
	 * @param dir       the directory to scan
	 * @param extension the file extension to filter by (without the leading dot)
	 * @return a sorted list of file names without the extension
	 */
	private List<String> getFilesWithoutExtension(final File dir, final String extension) {
		if (!dir.exists() || !dir.isDirectory()) {
			return Collections.emptyList();
		}
		File[] files = dir.listFiles(f -> f.isFile() && f.getName().endsWith("." + extension));
		if (files == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(files)
				.map(f -> f.getName().substring(0, f.getName().length() - extension.length() - 1))
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 * Compares two lists and adds error messages to the errors list if they differ.
	 *
	 * @param contentType    the label of the content type (used in messages)
	 * @param inDescriptor   the items listed in content.yaml
	 * @param onDisk         the items found on disk
	 * @param errors         the list to append error messages to
	 */
	private void reportMismatches(final String contentType, final List<String> inDescriptor,
			final List<String> onDisk, final List<String> errors) {
		List<String> missingOnDisk = inDescriptor.stream()
				.filter(name -> !onDisk.contains(name))
				.sorted()
				.collect(Collectors.toList());
		List<String> missingInDescriptor = onDisk.stream()
				.filter(name -> !inDescriptor.contains(name))
				.sorted()
				.collect(Collectors.toList());

		if (!missingOnDisk.isEmpty()) {
			errors.add(String.format("  [%s] Listed in content.yaml but missing on disk: %s",
					contentType, missingOnDisk));
		}
		if (!missingInDescriptor.isEmpty()) {
			errors.add(String.format("  [%s] Found on disk but not listed in content.yaml: %s",
					contentType, missingInDescriptor));
		}
	}
}
