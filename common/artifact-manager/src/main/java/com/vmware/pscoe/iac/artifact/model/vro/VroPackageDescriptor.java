package com.vmware.pscoe.iac.artifact.model.vro;

import java.io.File;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

import org.apache.commons.lang3.NotImplementedException;

public class VroPackageDescriptor extends PackageDescriptor {

    private List<String> workflow;
    private List<String> action;
    private List<String> configurationElement;
    private List<String> resourceElement;

    public List<String> getWorkflow() {
        return workflow;
    }

    public void setWorkflow(List<String> workflow) {
        this.workflow = workflow;
    }

    public List<String> getAction() {
        return action;
    }

    public void setAction(List<String> action) {
        this.action = action;
    }

    public List<String> getConfigurationElement() {
        return configurationElement;
    }

    public void setConfigurationElement(List<String> configurationElement) {
        this.configurationElement = configurationElement;
    }

    public List<String> getResourceElement() {
        return resourceElement;
    }

    public void setResourceElement(List<String> resourceElement) {
        this.resourceElement = resourceElement;
    }

    public List<String> getMembersForType() {
        throw new NotImplementedException("To be implemented");
    }

    public static VroPackageDescriptor getInstance(File filesystemPath) {
        throw new NotImplementedException("To be implemented");
    }

    public static VroPackageDescriptor getInstance() {
        throw new NotImplementedException("To be implemented");
    }

}
