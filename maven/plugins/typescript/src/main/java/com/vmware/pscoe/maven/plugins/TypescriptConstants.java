package com.vmware.pscoe.maven.plugins;

import java.nio.file.Paths;

public final class TypescriptConstants {

	public static final String OUT_ROOT_PATH = Paths.get("target", "vro-sources").toString();
	public static final String OUT_XML_ROOT_PATH = Paths.get(OUT_ROOT_PATH, "xml").toString();
	public static final String OUT_XML_SRC_PATH = Paths.get(OUT_XML_ROOT_PATH, "src", "main", "resources").toString();
	public static final String OUT_JS_ROOT_PATH = Paths.get(OUT_ROOT_PATH, "js").toString();
	public static final String OUT_JS_SRC_PATH = Paths.get(OUT_JS_ROOT_PATH, "src", "main", "resources").toString();
	public static final String OUT_TEST_HELPER_SRC_PATH = Paths.get(OUT_ROOT_PATH, "testHelpers", "src", "main", "resources").toString();
	public static final String OUT_TYPE_PATH = Paths.get("target", "vro-types").toString();

}
