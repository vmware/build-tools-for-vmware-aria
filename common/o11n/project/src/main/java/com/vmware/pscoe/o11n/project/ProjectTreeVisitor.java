package com.vmware.pscoe.o11n.project;

import java.io.File;

public interface ProjectTreeVisitor {
    void visitWorkflow(File file, String category);

    void visitScriptModule(File file, String category);

    void visitResourceElement(File file, String category);

    void visitConfigurationElement(File file, String category);

    void visitPolicyTemplate(File file, String category);

    void visitTest(File testFile, String category);
}
