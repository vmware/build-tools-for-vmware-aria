package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.plugins.annotations.*;

@Mojo(name = "run-vro-tests", defaultPhase = LifecyclePhase.TEST)
public class JsBasedActionsTestMojo extends AbstractVroTestMojo {
	private static final String SRC_JS_PATH = Paths.get("src", "main", "resources").toString();
	private static final String SRC_TEST_PATH = Paths.get("src", "test", "resources").toString();

	protected Boolean hasTests() {
		String projectRoot = project.getBasedir().toPath().toString();
		return super.hasTests() && new File(Paths.get(projectRoot, SRC_TEST_PATH).toString()).exists();
	}

	protected void addTestbedPaths(List<String> cmd, Configuration config) {
		String projectRoot = project.getBasedir().toPath().toString();
		cmd.add("--actions");
		cmd.add(Paths.get(projectRoot, SRC_JS_PATH).toString());
		cmd.add("--tests");
		cmd.add(Paths.get(projectRoot, SRC_TEST_PATH).toString());
	}
}
