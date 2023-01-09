package com.vmware.pscoe.iac.artifact.model.vro;

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
