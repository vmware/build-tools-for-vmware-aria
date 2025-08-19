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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import com.vmware.pscoe.iac.artifact.aria.orchestrator.helpers.VroWorkflowExecutor;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.model.WorkflowExecution;
import com.vmware.pscoe.iac.artifact.common.configuration.ConfigurationException;

public class ExecuteWorkflowMojo extends AbstractIacMojo {

	@Parameter(required = false, property = "in")
	private Map<String, String> in;

	@Parameter(required = true, property = "id")
	private String id;

	@Parameter(required = false, property = "outputFile")
	private File outputFile;

	/**
	 * Time in seconds to wait for the vRO workflow execution to complete before
	 * returning error. Default value: 300 seconds.
	 */
	@Parameter(required = false, property = "timeout", defaultValue = "300")
	private int timeout;

	@Parameter(required = false, property = "outputParameter", defaultValue = "")
	String outputParameter;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		getLog().info("Executing workflow with ID " + id);

		final Properties paramProps = new Properties();
		paramProps.putAll(in);
		overwriteFromCmdLine(paramProps, "in.");

		try {
			WorkflowExecution workflowExecutionResult = new VroWorkflowExecutor(getVroRestClient()).executeWorkflow(id,
					paramProps, timeout);
			getLog().info("Workflow " + workflowExecutionResult.getState());
			workflowExecutionResult.getOutput().forEach((param, value) -> getLog().info(" * " + param + " = " + value));
			if (outputParameter != null && outputParameter.length() > 0) {
				String outputParameterValue = workflowExecutionResult.getOutput().getProperty(outputParameter);
				if (outputParameterValue == null) {
					throw new MojoExecutionException("Workflow completed successfully, but output parameter "
							+ outputParameter + " is missing.");
				}
				if (outputFile != null) {
					com.google.common.io.Files.asCharSink(outputFile, StandardCharsets.UTF_8)
							.write(outputParameterValue);
				}
			}
		} catch (ConfigurationException e) {
			throw new MojoExecutionException("Could not process the configuration", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write output to file " + outputFile, e);
		} catch (VroWorkflowExecutor.WorkflowExecutionException e) {
			getLog().error(e);
			throw new MojoExecutionException("Workflow execution failed.", e);
		}
	}
}
