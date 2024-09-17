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

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TypescriptBasedProjectTreeTest {

	@Test
	public void walk() throws Exception {
		URI src = Resources.getResource(this.getClass(), "exampleTypescript").toURI();
		ProjectTree tree = new TypescriptBasedProjectTree(Paths.get(src));
		HashSet<String> actions = new HashSet<>();
		HashSet<String> tests = new HashSet<>();
		HashSet<String> workflows = new HashSet<>();
		HashSet<String> configs = new HashSet<>();

		tree.walk(new ProjectTreeVisitor() {
			@Override
			public void visitWorkflow(File file, String category) {
				workflows.add(category + "/" + file.getName());

			}

			@Override
			public void visitScriptModule(File scriptModuleFile, String category) {
				actions.add(category + "." + scriptModuleFile.getName());
			}

			@Override
			public void visitResourceElement(File file, String category) {

			}

			@Override
			public void visitConfigurationElement(File file, String category) {
				configs.add(category + "." + file.getName());
			}

			@Override
			public void visitPolicyTemplate(File file, String category) {

			}

			@Override
			public void visitTest(File testFile, String category) {
				tests.add(category + "." + testFile.getName());
			}
		});

		assertEquals(3, actions.size());
		assertTrue(actions.contains("local.corp.common.example.sample.js"));
		assertTrue(actions.contains("local.corp.common.other.dependent.js"));
		assertTrue(actions.contains("local.corp.common.other.echo.js"));

		assertEquals(1, tests.size());
		assertTrue(tests.contains("local.corp.common.example.SampleTests.js"));

		assertEquals(1, workflows.size());
		assertEquals("Example/Doer.xml", workflows.stream().findFirst().get());

		assertEquals(1, configs.size());
		assertEquals("Samples.sample.xml", configs.stream().findFirst().get());
	}
}
