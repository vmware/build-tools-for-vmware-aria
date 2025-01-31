/*
 * #%L
 * artifact-manager
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
package com.vmware.pscoe.iac.artifact.aria.orchestrator.model;

import java.util.Properties;

public class WorkflowExecution {
	private final String state;
	private final Properties output;
	private final Properties input;
	private final String error;

	public WorkflowExecution(Properties input, Properties output, String state, String error) {
		this.state = state;
		this.output = output;
		this.input = input;
		this.error = error;
	}

	public boolean isRunning() {
		return state.equalsIgnoreCase("running");
	}

	public boolean isFailed() {
		return state.equalsIgnoreCase("failed");
	}

	public boolean isCanceled() {
		return state.equalsIgnoreCase("canceled");
	}

	public boolean isCompleted() {
		return state.equalsIgnoreCase("completed");
	}

	public String getState() {
		return state;
	}

	public Properties getOutput() {
		return output;
	}

	public Properties getInput() {
		return input;
	}

	public String getError() {
		return error;
	}
}
