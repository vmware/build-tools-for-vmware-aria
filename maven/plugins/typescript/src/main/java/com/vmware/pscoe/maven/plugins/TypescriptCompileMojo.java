/*
 * #%L
 * o11n-typescript-package-maven-plugin
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

import com.google.common.io.Files;
import com.google.gson.stream.JsonWriter;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import org.apache.logging.log4j.util.Strings;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class TypescriptCompileMojo extends AbstractVroMojo {

	@Parameter(required = false, property = "vrotsc.emitHeader", defaultValue = "false")
	private boolean emitHeader;

	@Parameter(required = false, property = "files", defaultValue = "")
	private List<String> filesChanged;

	@Component
	private MavenProjectHelper projectHelper;

	public void execute() throws MojoExecutionException, MojoFailureException {
		printFilesSelected();
		getLog().debug("basedir " + project.getBasedir());

		PackageInfoProvider packageInfoProvider = new MavenProjectPackageInfoProvider(project);
		File destination = new File(directory,
				packageInfoProvider.getPackageName() + "." + PackageType.VRO.getPackageExtention());
		try {
			Files.createParentDirs(destination);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not create target directory.", e);
		}

		new ProcessExecutor()
				.name("Typescript transpilation")
				.directory(project.getBasedir())
				.throwOnError(true)
				.command(buildCompileCommand())
				.execute(getLog());

		String projectRoot = project.getBasedir().toPath().toString();
		List<String> packageNameList =
				new ArrayList<>(Arrays.asList(project.getGroupId().split("\\.")));
		File packageJson = new File(
				Paths.get(projectRoot, TypescriptConstants.OUT_TYPE_PATH, "package.json")
						.toString());
		packageJson.getParentFile().mkdirs();
		Arrays.stream(project.getArtifactId().split("\\.")).forEach(packageNameList::add);
		String packageName = Strings.join(packageNameList, '.');
		try (FileWriter fileWriter = new FileWriter(packageJson)) {
			try (JsonWriter writer = new JsonWriter(fileWriter)) {
				writer.beginObject();
				writer.name("name").value("@types/" + packageName);
				writer.name("version").value(project.getVersion());
				writer.name("private").value(true);
				writer.endObject();
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Could not generate type definitions.", e);
		}

		String npmExec = SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
		ArrayList<String> nodePackArgs = new ArrayList<>();
		nodePackArgs.add(npmExec);
		nodePackArgs.add("pack");
		if (!getLog().isDebugEnabled()) {
			nodePackArgs.add("--silent");
		}
		new ProcessExecutor().name("Generating node package").directory(packageJson.getParentFile())
				.command(nodePackArgs).execute(getLog());

		File tgz = new File(Paths.get(projectRoot, TypescriptConstants.OUT_TYPE_PATH,
				"types-" + packageName + "-" + project.getVersion() + ".tgz").toString());
		getLog().info("Attach atrifact " + tgz.getName());
		projectHelper.attachArtifact(project, "tgz", "", tgz);
	}

	private void printFilesSelected() {
		Log log = getLog();
		String totalFiles = this.filesChanged.size() == 0 ? "ALL" : this.filesChanged.size() + "";
		log.info(String.format("Files changed on Git Version. Total files to be compiled: %s", totalFiles));
		this.filesChanged.forEach(fileSelected -> log.info(String.format("File to compile: %s", fileSelected)));
	}

	private String createFileList() {
		String result = "";
		{
			result = this.filesChanged.stream().reduce((s, s2) -> {
					if (s2 == null)
						return s;
					else return s + "," + s2;
				}
			).orElse("");
		}
		return result;
	}

	private List<String> buildCompileCommand() {
		String actionNamespacePath = TypescriptHelper.getActionsNamespaceForProject(project);
		String projectRoot = project.getBasedir().toPath().toString();
		List<String> cmd = new ArrayList<>();
		String vrotscCmd = SystemUtils.IS_OS_WINDOWS ? "vrotsc.cmd" : "vrotsc";
		cmd.add(Paths.get(projectRoot, "node_modules", "@vmware-pscoe", "vrotsc", "bin", vrotscCmd).toString());
		cmd.add("src");
		if (emitHeader) {
			cmd.add("--emitHeader");
		}
		cmd.add("--actionsNamespace");
		cmd.add(project.getGroupId() + "." + project.getArtifactId());
		cmd.add("--workflowsNamespace");
		cmd.add(project.getArtifactId());
		cmd.add("--files");
		cmd.add(this.createFileList());
		cmd.add("--typesOut");
		cmd.add(TypescriptConstants.OUT_TYPE_PATH);
		cmd.add("--testsOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_ROOT_PATH, "test", actionNamespacePath).toString());
		cmd.add("--mapsOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_ROOT_PATH, "map", actionNamespacePath).toString());
		cmd.add("--actionsOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_JS_SRC_PATH, actionNamespacePath).toString());
		cmd.add("--testHelpersOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_TEST_HELPER_SRC_PATH, actionNamespacePath).toString());
		cmd.add("--workflowsOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_XML_SRC_PATH, "Workflow").toString());
		cmd.add("--policiesOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_XML_SRC_PATH, "PolicyTemplate").toString());
		cmd.add("--resourcesOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_XML_SRC_PATH, "ResourceElement").toString());
		cmd.add("--configsOut");
		cmd.add(Paths.get(TypescriptConstants.OUT_XML_SRC_PATH, "ConfigurationElement").toString());
		addVroIgnoreArgToCmd(cmd);
		return cmd;
	}
}
