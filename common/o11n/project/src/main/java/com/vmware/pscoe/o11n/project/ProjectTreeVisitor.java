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

import java.io.File;

public interface ProjectTreeVisitor {
    void visitWorkflow(File file, String category);

    void visitScriptModule(File file, String category);

    void visitResourceElement(File file, String category);

    void visitConfigurationElement(File file, String category);

    void visitPolicyTemplate(File file, String category);

    void visitTest(File testFile, String category);
}
