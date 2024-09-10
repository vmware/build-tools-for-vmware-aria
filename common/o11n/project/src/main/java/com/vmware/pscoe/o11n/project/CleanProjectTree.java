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
