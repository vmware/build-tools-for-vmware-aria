package com.vmware.pscoe.iac.artifact;

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


import java.time.Duration;
import java.time.Instant;
import java.util.*;

import com.vmware.pscoe.iac.artifact.model.vro.WorkflowExecution;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;

public class VroWorkflowExecutor {

    // ANSI Console Color Text Escape Sequences.
    private static final String BRIGHT_FOREGROUND = "\u001B[90m";
    private static final String NORMAL_FOREGROUND = "\u001B[39m";
    private static final String NORMAL            = "\u001B[0m";
    private static final String BRIGHT_RED        = "\u001B[1;31m";
    private static final String RED               = "\u001B[0;31m";
    private static final String BRIGHT_YELLOW     = "\u001B[1;33m";
    private static final String YELLOW            = "\u001B[0;33m";
    private static final String GREEN             = "\u001B[0;32m";
    private static final Integer WORKFLOW_FINISH_POLL_INTERVAL = 250;
    private static final Integer WORKFLOW_EXEC_POLL_INTERVAL = 1000;

    private static final long SERVICE_UNAVAILABLE_SLEEP_MILLIS = 60000; // How long to sleep before retrying in case the service is not available.

    private RestClientVro restClient;

    public VroWorkflowExecutor(RestClientVro restClient) {
        this.restClient = restClient;
    }

    HashSet<String> terminalStates = new HashSet<>(Arrays.asList("completed", "failed", "canceled"));

    /**
     * Executes a workflow synchronously and waits for it to finish/fail
     *
     * @param workflowId - The ID of the workflow
     * @param params     - Properties containing the input parameters of the workflow
     * @param timeout    - Timeout (in seconds) to wait for the workflow to finish
     * @return Properties containing all string output parameters of the workflow
     * @throws WorkflowExecutionException exception
     */
    public WorkflowExecution executeWorkflow(String workflowId, Properties params, int timeout) throws WorkflowExecutionException {
        // check whether workflow exists prior execution
        if (!restClient.isWorkflowExisting(workflowId)) {
            throw new WorkflowExecutionException(String.format("The workflow '%s' cannot be found on the target VRO '%s'", workflowId, restClient.getHost()));
        }
        Properties inputParametersTypes = restClient.getInputParametersTypes(workflowId);

        Set<String> printedMessages = new HashSet<>();
        String executionId = restClient.startWorkflow(workflowId, params, inputParametersTypes);
        Calendar timeoutAt = Calendar.getInstance();
        timeoutAt.add(Calendar.SECOND, timeout);

        long lastLogTimestamp = 0;
        while (!workflowFinished(restClient, workflowId, executionId)) {
            if (Calendar.getInstance().compareTo(timeoutAt) > 0) {
                throw new WorkflowExecutionException("Timeout while waiting for workflow to finish.");
            }
            lastLogTimestamp = printLogMessages(workflowId, executionId, lastLogTimestamp, printedMessages);
            try {
                Thread.sleep(WORKFLOW_FINISH_POLL_INTERVAL);
            } catch (InterruptedException e) {
                throw new WorkflowExecutionException("Interrupted while waiting for workflow to finish.", e);
            }
        }

        WorkflowExecution execution = restClient.getExecution(workflowId, executionId);
        try {
            Thread.sleep(WORKFLOW_EXEC_POLL_INTERVAL);
        } catch (InterruptedException e) {
            throw new WorkflowExecutionException("Interrupted while waiting for workflow logs to be flushed.", e);
        }
        printLogMessages(workflowId, executionId, lastLogTimestamp, printedMessages);

        if (execution.isFailed() || execution.isCanceled()) {
            throw new WorkflowExecutionException(execution.getError());
        } else {
            return execution;
        }
    }

    private boolean workflowFinished(RestClientVro restClient, String workflowId, String executionId) {
        try {
            return terminalStates.contains(restClient.getExecutionState(workflowId, executionId));
        } catch (RuntimeException rte) {
            handleServiceUnavailableException("Cannot get status of workflow       ", workflowId, rte);
            return false;
        }
    }

    private long printLogMessages(String workflowId, String executionId, long lastLogTimestamp, Set<String> printedMessages) {
        try {
            final long timestamp = Instant.now().minus(Duration.ofSeconds(30)).toEpochMilli();
            final List<String> logs = restClient.getWorkflowLogs(workflowId, executionId, "debug", lastLogTimestamp);
            logs.forEach(msg -> {
                if (!printedMessages.contains(msg)) {
                    final String colorMsg = (msg)
                            .replaceFirst("\\[(.+?)]", "[" + BRIGHT_FOREGROUND + "$1" + NORMAL_FOREGROUND + "]")
                            .replaceFirst("(?s)\\[warning](.+)", BRIGHT_YELLOW + "[warning]" + YELLOW + "$1" + NORMAL)
                            .replaceFirst("(?s)\\[error](.+)"  , BRIGHT_RED    + "[error]"   + RED    + "$1" + NORMAL);
                    System.out.println(colorMsg);
                    printedMessages.add(msg);
                }
            });
            return timestamp;
        } catch (RuntimeException rte) {
            handleServiceUnavailableException("Cannot get log messages for workflow", workflowId, rte);
            return lastLogTimestamp;
        }
    }

    private void handleServiceUnavailableException(String failedOperation, String workflowId, RuntimeException rte) {
        System.out.println(RED + failedOperation + " [" + YELLOW + workflowId + RED + "] : "
                + NORMAL + rte.getClass().getName() + " : " + rte.getLocalizedMessage() + ". "
                + GREEN + "Sleeping for " + SERVICE_UNAVAILABLE_SLEEP_MILLIS + " milliseconds." + NORMAL);
        printStackTrace(rte);
        sleepSilently(SERVICE_UNAVAILABLE_SLEEP_MILLIS); // Give it some more time to recover (1 minute more) and do not fill up the logs so quickly
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignored
        }
    }

    private void printStackTrace(Throwable t) {
        if (System.getProperty("DEBUG") != null) {
            t.printStackTrace();
        }
    }

    public static class WorkflowExecutionException extends Exception {
        private static final long serialVersionUID = -6160545755029886900L;

        public WorkflowExecutionException(String message) {
            super(message);
        }

        public WorkflowExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
