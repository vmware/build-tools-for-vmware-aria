/*
 * #%L
 * cs-package-maven-plugin
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

import com.vmware.pscoe.iac.artifact.aria.codestream.models.Variable;
import com.vmware.pscoe.iac.artifact.aria.codestream.store.models.CsPackageDescriptor;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageManager;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageMojo extends AbstractVroMojo {

	/**
	 * The name of the content descriptor file.
	 */
	private static final String CONTENT_YAML_FILE_NAME = "content.yaml";

	/**
	 * The YAML file extension.
	 */
	private static final String YAML_EXTENSION = "yaml";

	/**
	 * Directory name for pipelines.
	 */
	private static final String DIR_PIPELINES = "pipelines";
	/**
	 * Directory name for variables.
	 */
	private static final String DIR_VARIABLES = "variables";
	/**
	 * Directory name for endpoints.
	 */
	private static final String DIR_ENDPOINTS = "endpoints";
	/**
	 * Directory name for custom integrations.
	 */
	private static final String DIR_CUSTOM_INTEGRATIONS = "custom-integrations";
	/**
	 * Directory name for custom git webhooks.
	 */
	private static final String DIR_GIT_WEBHOOKS = "git-webhooks";
	/**
	 * Directory name for custom docker webhooks.
	 */
	private static final String DIR_DOCKER_WEBHOOKS = "docker-webhooks";
	/**
	 * Directory name for custom gerrit triggers.
	 */
	private static final String DIR_GERRIT_TRIGGERS = "gerrit-triggers";
	/**
	 * Directory name for custom gerrit listeners.
	 */
	private static final String DIR_GERRIT_LISTENERS = "gerrit-listeners";
	/**
	 * The file name used for the variables store (all variables in a single file).
	 */
	private static final String VARIABLES_FILE_NAME = "variables";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenProjectPackageInfoProvider pkgInfoProvider = new MavenProjectPackageInfoProvider(project);

		File contentYamlFile = new File(project.getBasedir(), CONTENT_YAML_FILE_NAME);
		if (!contentYamlFile.exists()) {
			throw new MojoExecutionException(
				"Missing required 'content.yaml' manifest descriptor in project base directory: "
					+ project.getBasedir().getAbsolutePath());
		}

		CsPackageDescriptor descriptor = CsPackageDescriptor.getInstance(contentYamlFile);
		File sourceDirectory = pkgInfoProvider.getSourceDirectory();

		// Validate that local content matches what is described in content.yaml
		validateContentMatchesDescriptor(sourceDirectory, descriptor);

		getLog().info("basedir " + project.getBasedir());
		File pkgFile = new File(directory,
				pkgInfoProvider.getPackageName() + "." + PackageType.CS.getPackageExtension());
		getLog().info("Target CS package file " + pkgFile.getAbsolutePath());

		com.vmware.pscoe.iac.artifact.common.store.Package pkg = PackageFactory.getInstance(PackageType.CS, pkgFile);
		try {
			getLog().info("Packaging CS bundle from: " + sourceDirectory.getAbsolutePath());
			PackageManager mgr = new PackageManager(pkg);
			mgr.pack(sourceDirectory);
			// Include content.yaml in the package so it can be used during import
			mgr.addTextFileToExistingZip(contentYamlFile, Paths.get("."));
			project.getArtifact().setFile(pkgFile);
		} catch (IOException e) {
			throw new MojoExecutionException(e, "Error creating CS bundle", "Error creating CS bundle");
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
	private void validateContentMatchesDescriptor(final File sourceDirectory, final CsPackageDescriptor descriptor)
			throws MojoExecutionException {
		List<String> errors = new ArrayList<>();

		// Validate pipelines (one .yaml file per pipeline)
		validateOneFilePerItem(sourceDirectory, DIR_PIPELINES, YAML_EXTENSION,
				descriptor.getPipeline(), "pipeline", errors);

		// Validate variables (all variables in a single variables.yaml file)
		validateVariables(sourceDirectory, descriptor.getVariable(), errors);

		// Validate endpoints (one .yaml file per endpoint)
		validateOneFilePerItem(sourceDirectory, DIR_ENDPOINTS, YAML_EXTENSION,
				descriptor.getEndpoint(), "endpoint", errors);

		// Validate custom integrations (one .yaml file per integration)
		validateOneFilePerItem(sourceDirectory, DIR_CUSTOM_INTEGRATIONS, YAML_EXTENSION,
				descriptor.getCustomIntegration(), "custom-integration", errors);

		// Validate git webhooks (one .yaml file per webhook)
		validateOneFilePerItem(sourceDirectory, DIR_GIT_WEBHOOKS, YAML_EXTENSION,
				descriptor.getGitWebhook(), "git-webhook", errors);

		// Validate docker webhooks (one .yaml file per webhook)
		validateOneFilePerItem(sourceDirectory, DIR_DOCKER_WEBHOOKS, YAML_EXTENSION,
				descriptor.getDockerWebhook(), "docker-webhook", errors);

		// Validate gerrit triggers (one .yaml file per trigger)
		validateOneFilePerItem(sourceDirectory, DIR_GERRIT_TRIGGERS, YAML_EXTENSION,
				descriptor.getGerritTrigger(), "gerrit-trigger", errors);

		// Validate gerrit listeners (one .yaml file per listener)
		validateOneFilePerItem(sourceDirectory, DIR_GERRIT_LISTENERS, YAML_EXTENSION,
				descriptor.getGerritListener(), "gerrit-listener", errors);

		if (!errors.isEmpty()) {
			throw new MojoExecutionException(
				"Local content does not match content.yaml descriptor. Please update either the local files "
					+ "or content.yaml to match the existing state:\n" + String.join("\n", errors));
		}
	}

	/**
	 * Validates a content type where each item has exactly one corresponding file on disk.
	 *
	 * @param sourceDirectory the source directory
	 * @param subDir          the sub-directory name for this content type
	 * @param extension       the file extension (without the leading dot)
	 * @param descriptorItems the list of item names from content.yaml (may be null)
	 * @param contentType     the label of the content type (for error messages)
	 * @param errors          the list to append error messages to
	 */
	private void validateOneFilePerItem(final File sourceDirectory, final String subDir, final String extension,
			final List<String> descriptorItems, final String contentType, final List<String> errors) {
		List<String> inDescriptor = descriptorItems != null ? descriptorItems : Collections.emptyList();
		File dir = new File(sourceDirectory, subDir);
		List<String> onDisk = getFilesWithoutExtension(dir, extension);
		reportMismatches(contentType, inDescriptor, onDisk, errors);
	}

	/**
	 * Validates variables. All variables are stored in a single file (variables/variables.yaml),
	 * so this method checks that the variable names in content.yaml match those in that file.
	 *
	 * @param sourceDirectory  the source directory
	 * @param descriptorVars   the list of variable names from content.yaml (may be null)
	 * @param errors           the list to append error messages to
	 */
	private void validateVariables(final File sourceDirectory, final List<String> descriptorVars,
			final List<String> errors) {
		List<String> inDescriptor = descriptorVars != null ? descriptorVars : Collections.emptyList();
		File variablesFile = new File(new File(sourceDirectory, DIR_VARIABLES),
				VARIABLES_FILE_NAME + "." + YAML_EXTENSION);

		if (inDescriptor.isEmpty() && !variablesFile.exists()) {
			// Both are empty — no mismatch
			return;
		}
		if (!inDescriptor.isEmpty() && !variablesFile.exists()) {
			errors.add(String.format(
					"  [variable] Variables are listed in content.yaml %s but '%s' does not exist on disk.",
					inDescriptor, variablesFile.getPath()));
			return;
		}
		if (inDescriptor.isEmpty() && variablesFile.exists()) {
			errors.add(String.format(
					"  [variable] '%s' exists on disk but no variables are listed in content.yaml.",
					variablesFile.getPath()));
			return;
		}

		// Both have content — parse the file and compare names
		List<String> onDisk = readVariableNamesFromFile(variablesFile, errors);
		if (onDisk != null) {
			reportMismatches("variable", inDescriptor, onDisk, errors);
		}
	}

	/**
	 * Reads variable names from the variables YAML file.
	 *
	 * @param variablesFile the variables.yaml file
	 * @param errors        the list to append error messages to if reading fails
	 * @return sorted list of variable names, or null if reading failed
	 */
	private List<String> readVariableNamesFromFile(final File variablesFile, final List<String> errors) {
		try {
			YAMLMapper yamlMapper = new YAMLMapper();
			Variable[] variables = yamlMapper.readValue(variablesFile, Variable[].class);
			return Arrays.stream(variables)
					.map(Variable::getName)
					.sorted()
					.collect(Collectors.toList());
		} catch (IOException e) {
			errors.add(String.format("  [variable] Unable to read variables file '%s': %s",
					variablesFile.getPath(), e.getMessage()));
			return null;
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
