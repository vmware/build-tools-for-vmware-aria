package com.vmware.pscoe.o11n.project;

import com.google.common.io.Files;

import java.io.File;

public class CleanXmlProjectTree extends CleanProjectTree {

    @Override
    public void visitWorkflow(File file, String category) {
        super.visitWorkflow(file, category);
        deleteInfoFileFor(file, true);
    }

    @Override
    public void visitScriptModule(File file, String category) {
        super.visitScriptModule(file, category);
        deleteInfoFileFor(file, true);
    }

    @Override
    public void visitResourceElement(File file, String category) {
        super.visitResourceElement(file, category);
        deleteInfoFileFor(file, false);
    }

    @Override
    public void visitConfigurationElement(File file, String category) {
        super.visitConfigurationElement(file, category);
        deleteInfoFileFor(file, true);
    }

    @Override
    public void visitPolicyTemplate(File file, String category) {
        super.visitPolicyTemplate(file, category);
        deleteInfoFileFor(file, true);
    }

    private void deleteInfoFileFor(File file, boolean excludeExtension) {
        if (file != null) {
            final String fileName = excludeExtension ? Files.getNameWithoutExtension(file.getName()) : file.getName();
            final File infoFile = file.toPath().getParent().resolve(fileName + ".element_info.xml").toFile();
            if (infoFile.exists()) {
                deleteFile(infoFile);
            }
        }
    }
}
