package com.vmware.pscoe.o11n.project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class CleanProjectTree implements ProjectTreeVisitor {

    private static final Logger logger = LogManager.getLogger(CleanProjectTree.class);

    @Override
    public void visitWorkflow(File file, String category) {
        deleteFile(file);
    }

    @Override
    public void visitScriptModule(File file, String category) {
        deleteFile(file);
    }

    @Override
    public void visitResourceElement(File file, String category) {
        deleteFile(file);
    }

    @Override
    public void visitConfigurationElement(File file, String category) {
        deleteFile(file);
    }

    @Override
    public void visitPolicyTemplate(File file, String category) {
        deleteFile(file);
    }

    @Override
    public void visitTest(File testFile, String category) {
        // tests will not be removed from the project
    }

    protected void deleteFile(File file) {
        if (file != null) {
            logger.info("Deleting file " + file.getAbsolutePath());
            if (!file.delete()) {
                logger.warn("Could not delete file " + file.getAbsolutePath());
            }
        }
    }
}
