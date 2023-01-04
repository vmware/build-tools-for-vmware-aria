package com.vmware.pscoe.maven.plugins;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.StringJoiner;

public final class TypescriptHelper {
	public static String getActionsNamespaceForProject(MavenProject project) {
		StringJoiner actionNamespacePathJoiner = new StringJoiner(File.separator);
		Arrays.stream(project.getGroupId().split("\\.")).forEach(actionNamespacePathJoiner::add);
		Arrays.stream(project.getArtifactId().split("\\.")).forEach(actionNamespacePathJoiner::add);
		return actionNamespacePathJoiner.toString();
	}
}
