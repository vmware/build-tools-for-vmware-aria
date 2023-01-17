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

import com.google.common.io.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.junit.Assert.*;

public class JsBasedActionsProjectTreeTest {
    private static final Logger logger = LogManager.getLogger(JsBasedActionsProjectTreeTest.class);

    @Test
    public void testWalk() throws Exception {
        URI src = Resources.getResource(this.getClass(), "example").toURI();
        ProjectTree tree = new JsBasedActionsProjectTree(Paths.get(src));
        HashSet<String> actions = new HashSet<>();
        HashSet<String> tests = new HashSet<>();
        tree.walk(new ProjectTreeVisitor() {
            @Override
            public void visitWorkflow(File file, String category) {

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

            }

            @Override
            public void visitPolicyTemplate(File file, String category) {

            }

            @Override
            public void visitTest(File testFile, String category) {
                tests.add(category + "." + testFile.getName());
            }
        });

        assertEquals(4, actions.size());
        assertTrue(actions.contains("local.corp.common.example.sample.js"));
        assertTrue(actions.contains("local.corp.common.example.sampleMock.js"));
        assertTrue(actions.contains("local.corp.common.other.dependent.js"));
        assertTrue(actions.contains("local.corp.common.other.echo.js"));

        assertEquals(1, tests.size());
        assertTrue(tests.contains("local.corp.common.example.SampleTests.js"));
    }

    @Test
    public void testWalkIgnoreTests() throws Exception {
        URI src = Resources.getResource(this.getClass(), "example").toURI();
        ProjectTree tree = new JsBasedActionsProjectTree(Paths.get(src), true);
        HashSet<String> actions = new HashSet<>();
        HashSet<String> tests = new HashSet<>();
        tree.walk(new ProjectTreeVisitor() {
            @Override
            public void visitWorkflow(File file, String category) {

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
        assertTrue(!actions.contains("local.corp.common.example.sampleMock.js"));
        assertTrue(actions.contains("local.corp.common.other.dependent.js"));
        assertTrue(actions.contains("local.corp.common.other.echo.js"));

        assertEquals(1, tests.size());
        assertTrue(tests.contains("local.corp.common.example.SampleTests.js"));
    }



    @Test
    public void testAction() throws Exception {
        URI src = Resources.getResource(this.getClass(), "example").toURI();
        ProjectTree tree = new JsBasedActionsProjectTree(Paths.get(src));
        System.out.println(tree.action("sample", "local.corp.common.example"));
        assertTrue(tree.action("sample", "local.corp.common.example").exists());
        assertTrue(tree.action("dependent", "local.corp.common.other").exists());
        assertTrue(tree.action("echo", "local.corp.common.other").exists());
        assertFalse(tree.action("sample", "local.corp.common").exists());
    }
}
