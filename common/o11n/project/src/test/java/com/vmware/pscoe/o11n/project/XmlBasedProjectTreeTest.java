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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class XmlBasedProjectTreeTest {
	private String[] workflowNameList = {
			"Normal workflow name",
			"Workflow name - some valid 2 symbols",
			"This is not valid",
			"Might contain quote as well"
	};
	private String mainPath = "";
	private String basePath = "";
	private String wfSuffix = ".xml";

	@Before
	public void generateMainPath() {
		String[] mainPathList = {"src", "main", "resources", "Workflow", "First", "Second", "Third"};
		mainPath = "";
		basePath = "";
		StringBuilder builderMain = new StringBuilder();
		StringBuilder builderBase = new StringBuilder();
		for (int i = 0; i < mainPathList.length; i++) {
			builderMain.append(mainPathList[i]);

			if (i >= (mainPathList.length - 3)) {
				builderBase.append(mainPathList[i]);
			}
			if(i!=mainPathList.length-1){
				builderMain.append(File.separator);
				builderBase.append(File.separator);
			}
		}
		mainPath = builderMain.toString();
		basePath = builderBase.toString();
	}

	@Test
	public void workflow() throws Exception {
		XmlBasedProjectTree projectTree = new XmlBasedProjectTree(Paths.get(""));

		assertEquals(mainPath + File.separator + workflowNameList[0]+wfSuffix, projectTree.workflow(workflowNameList[0], basePath).toString());
		assertEquals(mainPath + File.separator + workflowNameList[1]+wfSuffix, projectTree.workflow(workflowNameList[1], basePath).toString());
		assertEquals(mainPath + File.separator + workflowNameList[2]+wfSuffix, projectTree.workflow(workflowNameList[2]+"!", basePath).toString());
		assertEquals(mainPath + File.separator + workflowNameList[2]+wfSuffix, projectTree.workflow(workflowNameList[2]+"!", basePath).toString());
		assertEquals(mainPath + File.separator + workflowNameList[3]+wfSuffix, projectTree.workflow("Might \"contain\" quote 'as' well", basePath).toString());
	}

}
