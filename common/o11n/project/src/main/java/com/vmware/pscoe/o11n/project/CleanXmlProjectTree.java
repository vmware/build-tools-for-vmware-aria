package com.vmware.pscoe.o11n.project;

/*
 * #%L
 * o11n-project
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
