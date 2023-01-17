package com.vmware.pscoe.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import org.apache.commons.io.FileUtils;

public abstract class AbstractCleanNodeDepsMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

	@Parameter(property = "skipInstallNodeDeps", defaultValue = "false")
	protected boolean skipInstallNodeDeps;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException { 
        File nodeModulesDir = Paths.get(project.getBasedir().toString(), "node_modules").toFile();

		if(skipInstallNodeDeps)
		{
			getLog().info("Skipping the node_modules deletion");
			return;
		}
		
        tryDeleteDirectory(nodeModulesDir);
        tryDeleteFile("package.json");
        tryDeleteFile("package-lock.json");
    }

    protected void tryDeleteDirectory(File subdir) {
        if (subdir.exists()) {
            try {
                FileUtils.deleteDirectory(subdir);
            } catch (IOException e) {
                getLog().warn("Unable to delete " + subdir.getAbsolutePath() + "; Error: " + e.getMessage(), e);
            }
        }
    }

    protected void tryDeleteFile(String fileName) {
        Path filePath = Paths.get(project.getBasedir().toString(), fileName);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                getLog().warn("Unable to delete " + fileName);
            }
        }
    }
}
