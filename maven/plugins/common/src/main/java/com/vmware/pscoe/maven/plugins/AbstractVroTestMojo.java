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
	public static final int COVERAGE_TRESHOLDS_SIZE = 5;

	@Parameter(defaultValue = "${project}")
	protected MavenProject project;

	@Parameter(property = "skipTests", defaultValue = "false")
	protected boolean skipTests;

	@Parameter(required = false, property = "test", defaultValue = "${test.*}")
	private Map<String, String> test;

	/**
	 * Triggers the unit tests execution.
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info("Tests are skipped.");
		} else if (!hasTests()) {
			getLog().info("No test files found.");
		} else {
			Configuration config = new Configuration(test);
			printStarted(config);
			buildTestbed(config);
			runTests(config);
		}
	}

	/**
	 * @return A flag indicating whether or not tests exist for the project.
	 */
	protected Boolean hasTests() {
		return new File(getCliPath()).exists();
	}

	/**
	 * Prints a message indicating the testing step is starting.
	 * 
	 * @param config
	 */
	protected void printStarted(Configuration config) {
		getLog().info("");
		getLog().info("-------------------------------------------------------");
		getLog().info(" T E S T S");
		getLog().info("-------------------------------------------------------");
	}

	/**
	 * Bootstrapping the unit tests.
	 *
	 * @param config
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 * @throws org.apache.maven.plugin.MojoFailureException
	 */
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

	/**
	 * Build the command for running the tests.
	 *
	 * @param config
	 * @return The command to be executed for running the tests.
	 */
	protected List<String> buildTestbedCommand(Configuration config) {
		String projectRoot = project.getBasedir().toPath().toString();
		List<String> cmd = new ArrayList<>();
		cmd.add(getCliPath());
		cmd.add("build");
		addTestbedPaths(cmd, config);
		cmd.add("--projectRoot");
		cmd.add(projectRoot);
		cmd.add("--dependencies");
		cmd.add(Paths.get(projectRoot, DEP_ROOT_PATH).toString());
		cmd.add("--helpers");
		cmd.add(Paths.get(projectRoot, VRO_API_PATH).toString());
		cmd.add("--output");
		cmd.add(Paths.get(projectRoot, TESTBED_PATH).toString());
		cmd.add("--testFrameworkPackage");
		cmd.add(config.getFrameworkPackageName());
		cmd.add("--testFrameworkVersion");
		cmd.add(config.getFrameworkVersion());
		cmd.add("--runner");
		cmd.add(config.getTestsRunner());
		cmd.add("--jasmineReportersVerion");
		cmd.add(config.getJasmineReportersVersion());
		cmd.add("--ansiColorsVersion");
		cmd.add(config.getAnsiColorsVersion());

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

			if (config.isPerFileEnabled()) {
				cmd.add("--per-file");
				cmd.add("true");
			}
		}
		return cmd;
	}

	protected abstract void addTestbedPaths(List<String> cmd, Configuration config);

	/**
	 * Uses the project configuration it its pom file to build the tests coverage threshold values.
	 *
	 * @param config
	 * @return The threshold values list.
	 */
	protected String buildCoverageThresholdToken(Configuration config) {
		List<String> thresholds = new ArrayList<String>(COVERAGE_TRESHOLDS_SIZE);

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

	/**
	 * Builds and executes the tests run.
	 *
	 * @param config
	 * @throws org.apache.maven.plugin.MojoExecutionException
	 * @throws org.apache.maven.plugin.MojoFailureException
	 */
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

	/**
	 * Builds the command for running the tests.
	 *
	 * @param config
	 * @return
	 */
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

	/**
	 * Returns the path tot the vrotest dependency.
	 *
	 * @return The path to the vrotest dependency.
	 */
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
		private static final String FRAMEWORK_PACKAGE_NAME = "framework.package";
		private static final String FRAMEWORK_VERSION = "framework.version";
		private static final String JASMINE_REPORTERS_VERSION = "framework.jasmine.reporters.version";
		private static final String ANSI_COLORS_VERSION = "ansicolors.version";
		private static final String TESTS_RUNNER = "framework.runner";

		private final Map<String, String> props;

		public Configuration(Map<String, String> props) {
			this.props = props;
		}

		/**
		 * Returns a flag whether or not code report is enabled.
		 *
		 * @return The flag value.
		 */
		public Boolean isCoverageEnabled() {
			String value = props.get(COVERAGE_ENABLED);
			return value != null && Boolean.parseBoolean(value);
		}

		/**
		 * Returns a a list of code coverage reports.
		 *
		 * @return The coverate list.
		 */
		public List<String> getCoverageReports() {
			String value = props.get(COVERAGE_REPORTS);
			return Arrays.asList((value != null ? value.trim() : "").split(",")).stream()
					.map(reportName -> reportName.trim())
					.collect(Collectors.toList());
		}

		/**
		 * Loads the configuration value for maximum allowed errors value.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed warnings value.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed errors per branch value.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageBranchesThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_BRANCHES_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed warnings per branch value.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageBranchesThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_BRANCHES_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed errors for lines.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageLinesThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_LINES_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed warnings for lines.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageLinesThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_LINES_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed errors for functions.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageFunctionsThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_FUNCS_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed warnings for functions.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageFunctionsThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_FUNCS_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed errors for statements.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageStatementsThresholdError() {
			String value = props.get(COVERAGE_THRESHOLD_STMTS_ERROR);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration value for maximum allowed warnings for statements.
		 *
		 * @return The configuration value.
		 */
		public Integer getCoverageStatementsThresholdWarn() {
			String value = props.get(COVERAGE_THRESHOLD_STMTS_WARN);
			return value != null ? Integer.parseInt(value) : 0;
		}

		/**
		 * Loads the configuration flag value for per-file coverate.
		 *
		 * @return The configuration value.
		 */
		public Boolean isPerFileEnabled() {
			String value = props.get(COVERAGE_TEST_PERFILE);
			return value != null && Boolean.parseBoolean(value);
		}

		/**
		 * Returns the framework package name - "jasmine" or "jest".
		 *
		 * @return The configuration value.
		 */
		public String getFrameworkPackageName() {
			String value = props.get(FRAMEWORK_PACKAGE_NAME);
			return value != null ? value : "";
		}

		/**
		 * Returns the framework package version.
		 *
		 * @return The configuration value.
		 */
		public String getFrameworkVersion() {
			String value = props.get(FRAMEWORK_VERSION);
			return value != null ? value : "";
		}

		/**
		 * Returns the version of the Jasmine reporters package.
		 * Supported when Jasmine is used as tests framework.
		 *
		 * @return The configuration value.
		 */
		public String getJasmineReportersVersion() {
			String value = props.get(JASMINE_REPORTERS_VERSION);
			return value != null ? value : "";
		}

		/**
		 * Returns the version of the Ansi Colors package.
		 * Supported when Jasmine is used as tests framework.
		 *
		 * @return The configuration value.
		 */
		public String getAnsiColorsVersion() {
			String value = props.get(ANSI_COLORS_VERSION);
			return value != null ? value : "";
		}

		/**
		 * Returns the type of tests runner to be used - supports either none or "swc".
		 * Supported when Jest is used as tests framework.
		 *
		 * @return The configuration value.
		 */
		public String getTestsRunner() {
			String value = props.get(TESTS_RUNNER);
			return value != null ? value : "";
		}
	}
}
