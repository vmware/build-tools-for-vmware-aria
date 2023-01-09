package com.vmware.pscoe.maven.plugins;

import com.vmware.pscoe.iac.artifact.VroWorkflowExecutor;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

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
            WorkflowExecution workflowExecutionResult = new VroWorkflowExecutor(getVroRestClient()).executeWorkflow(id, paramProps, timeout);
            getLog().info("Workflow " + workflowExecutionResult.getState());
            workflowExecutionResult.getOutput().forEach((param, value) -> getLog().info(" * " + param + " = " + value));
            if (outputParameter != null && outputParameter.length() > 0) {
                String outputParameterValue = workflowExecutionResult.getOutput().getProperty(outputParameter);
                if (outputParameterValue == null) {
                    throw new MojoExecutionException("Workflow completed successfully, but output parameter " + outputParameter + " is missing.");
                }
                if (outputFile != null) {
                    com.google.common.io.Files.asCharSink(outputFile, StandardCharsets.UTF_8).write(outputParameterValue);
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
