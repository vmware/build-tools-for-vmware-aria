package com.vmware.pscoe.iac.artifact.model.vro;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VroPackageContent extends PackageContent<VroPackageContent.ContentType> {
    
    public enum ContentType implements PackageContent.ContentType { 
        WORKFLOW, ACTION, CONFIGURATION, RESOURCE
    }

    public VroPackageContent(List<Content<ContentType>> content) {
        super(content);
    }

}
