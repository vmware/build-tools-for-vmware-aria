/*
 * #%L
 * common
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractVroTestMojo extends AbstractMojo {

	protected static final String DEP_ROOT_PATH =
			Paths.get("target", "dependency", "vro").toString();
	protected static final String VRO_API_PATH =
			Paths.get("node_modules", "@vmware-pscoe", "vro-scripting-api", "lib").toString();
	protected static final String TESTBED_PATH = Paths.get("target", "vro-tests").toString();

	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	@Parameter(property = "skipTests", defaultValue = "false")
	protected boolean skipTests;

	@Parameter(required = false, property = "test", defaultValue = "${test.*}")
	private Map<String, String> test;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info("Tests are skipped.");
		}
		else if (!hasTests()) {
			getLog().info("No test files found.");
		}
		else {
			Configuration config = new Configuration(test);
			printStarted(config);
			buildTestbed(config);
			runTests(config);
		}
	}

	protected Boolean hasTests() {
		return new File(getCliPath()).exists();
	}

	protected void printStarted(Configuration config) {
		getLog().info("");
		getLog().info("-------------------------------------------------------");
		getLog().info(" T E S T S");
		getLog().info("-------------------------------------------------------");
	}

	protected void buildTestbed(Configuration config)
			throws org.apache.maven.plugin.MojoExecutionException,
			org.apache.maven.plugin.MojoFailureException {
		new ProcessExecutor()
				.name("vRO test bed")
				.directory(project.getBasedir())
				.throwOnError(true)
				.silent(true)
				.command(buildTestbedCommand(config))
				.execute(getLog());
	}

	protected List<String> buildTestbedCommand(Configuration config) {
		String projectRoot = project.getBasedir().toPath().toString();
		List<String> cmd = new ArrayList<>();
		cmd.add(getCliPath());
		cmd.add("build");
		addTestbedPaths(cmd, config);
		cmd.add("--dependencies");
		cmd.add(Paths.get(projectRoot, DEP_ROOT_PATH).toString());
		cmd.add("--helpers");
		cmd.add(Paths.get(projectRoot, VRO_API_PATH).toString());
		cmd.add("--output");
		cmd.add(Paths.get(projectRoot, TESTBED_PATH).toString());

		if (config.isCoverageEnabled()) {
			String coverageReports = String.join(",", config.getCoverageReports());
			if (!coverageReports.isEmpty()) {
				cmd.add("--coverage-reports");
				cmd.add(coverageReports);
			}
			String coverageThresholds = buildCoverageThresholdToken(config);
			if (!coverageThresholds.isEmpty()) {
				cmd.add("--coverage-thresholds");
				cmd.add(coverageThresholds);
			}

			if(config.isPerFileEnabled()){
				cmd.add("--per-file");
				cmd.add("true");
			}
		}
		return cmd;
	}

	protected abstract void addTestbedPaths(List<String> cmd, Configuration config);

	protected String buildCoverageThresholdToken(Configuration config) {
		List<String> thresholds = new ArrayList<String>(5);

		Integer allError = config.getCoverageThresholdError();
		Integer allWarn = config.getCoverageThresholdWarn();
		if (allError > 0 || allWarn > 0) {
			thresholds.add(String.format("%d:%d:all", allError, allWarn));
		}

		Integer branchesError = config.getCoverageBranchesThresholdError();
		Integer branchesWarn = config.getCoverageBranchesThresholdWarn();
		if (branchesError > 0 || branchesWarn > 0) {
			thresholds.add(String.format("%d:%d:branches", branchesError, branchesWarn));
		}

		Integer linesError = config.getCoverageLinesThresholdError();
		Integer linesWarn = config.getCoverageLinesThresholdWarn();
		if (linesError > 0 || linesWarn > 0) {
			thresholds.add(String.format("%d:%d:lines", linesError, linesWarn));
		}

		Integer functionsError = config.getCoverageFunctionsThresholdError();
		Integer functionsWarn = config.getCoverageFunctionsThresholdWarn();
		if (functionsError > 0 || functionsWarn > 0) {
			thresholds.add(String.format("%d:%d:functions", functionsError, functionsWarn));
		}

		Integer statementsError = config.getCoverageStatementsThresholdError();
		Integer statementsWarn = config.getCoverageStatementsThresholdWarn();
		if (statementsError > 0 || statementsWarn > 0) {
			thresholds.add(String.format("%d:%d:statements", statementsError, statementsWarn));
		}

		return String.join(",", thresholds);
	}

	protected void runTests(Configuration config)
			throws org.apache.maven.plugin.MojoExecutionException, org.apache.maven.plugin.MojoFailureException {
		new ProcessExecutor()
				.name("vRO tests")
				.directory(project.getBasedir())
				.throwOnError(true, "One or more vRO tests failed")
				.silent(true)
				.command(buildRunCommand(config))
				.execute(getLog());
	}

	protected List<String> buildRunCommand(Configuration config) {
		List<String> cmd = new ArrayList<>();
		cmd.add(getCliPath());
		cmd.add("run");
		cmd.add(TESTBED_PATH);

		if (config.isCoverageEnabled()) {
			cmd.add("--instrument");
		}
		return cmd;
	}


	protected String getCliPath() {
		String projectRoot = project.getBasedir().toPath().toString();
		String vrotestCmd = SystemUtils.IS_OS_WINDOWS ? "vrotest.cmd" : "vrotest";
		return Paths
				.get(projectRoot, "node_modules", "@vmware-pscoe", "vrotest", "bin", vrotestCmd)
				.toString();
	}

	protected static class Configuration {
		private static final String COVERAGE_ENABLED = "coverage.enabled";
		private static final String COVERAGE_REPORTS = "coverage.reports";
		private static final String COVERAGE_THRESHOLD_ERROR = "coverage.thresholds.error";
		private static final String COVERAGE_THRESHOLD_WARN = "coverage.thresholds.warn";
		private static final String COVERAGE_THRESHOLD_BRANCHES_ERROR = "coverage.thresholds.branches.error";
		private static final String COVERAGE_THRESHOLD_BRANCHES_WARN = "coverage.thresholds.branches.warn";
		private static final String COVERAGE_THRESHOLD_LINES_ERROR = "coverage.thresholds.lines.error";
		private static final String COVERAGE_THRESHOLD_LINES_WARN = "coverage.thresholds.lines.warn";
		private static final String COVERAGE_THRESHOLD_FUNCS_ERROR = "coverage.thresholds.functions.error";
		private static final String COVERAGE_THRESHOLD_FUNCS_WARN = "coverage.thresholds.functions.warn";
		private static final String COVERAGE_THRESHOLD_STMTS_ERROR = "coverage.thresholds.statements.error";
		private static final String COVERAGE_THRESHOLD_STMTS_WARN = "coverage.thresholds.statements.warn";
		private static final String COVERAGE_TEST_PERFILE = "coverage.perfile";

		private final Map<String, String> props;

		public Configuration(Map<String, String> props) {
			this.props = props;
		}

		public Boolean isCoverageEnabled() {
			String value = props.get(COVERAGE_ENABLED);
			return value != null && Boolean.parseBoolean(value);
		}

		public List<String> getCoverageReports() {
			String value = props.get(COVERAGE_REPORTS);
			return Arrays.asList((value != null ? value.trim() : "").split(",")).stream()
					.map(reportName -> reportName.trim())
					.collect(Collectors.toList());
		}

		public Integer getCoverageThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageBranchesThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_BRANCHES_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageBranchesThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_BRANCHES_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageLinesThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_LINES_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageLinesThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_LINES_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageFunctionsThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_FUNCS_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageFunctionsThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_FUNCS_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageStatementsThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_STMTS_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Integer getCoverageStatementsThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_STMTS_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public Boolean isPerFileEnabled() {
			String value = props.get(COVERAGE_TEST_PERFILE);
			return value != null && Boolean.parseBoolean(value);
		}
	}
}
