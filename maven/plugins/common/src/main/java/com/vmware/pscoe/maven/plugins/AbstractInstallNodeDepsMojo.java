package com.vmware.pscoe.maven.plugins;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractInstallNodeDepsMojo extends AbstractIacMojo {
    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

	@Parameter(property = "skipInstallNodeDeps", defaultValue = "false")
	protected boolean skipInstallNodeDeps;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        boolean allTgzLibsResolved = true;
        File nodeModules = new File(project.getBasedir(), "node_modules");
		if(skipInstallNodeDeps)
		{
			if(nodeModules.exists())
			{
				getLog().info("Skipping the Dependency installation");
				return;
			}
			else
			{
				getLog().info("Ignoring the flag skipInstallNodeDeps," +
				 "as node dependencies doesn't exist and are required for the successful build...");
			}
			
		}
        if (!nodeModules.exists()) {
            getLog().debug("node_modules doesn't exists. Creating it...");
            nodeModules.mkdirs();
        }
		
        List<String> deps = new LinkedList<>();
        String npmExec = SystemUtils.IS_OS_WINDOWS ? "npm.cmd" : "npm";
        deps.add(npmExec);
        deps.add("install");
        for (Object o : project.getArtifacts()) {
            Artifact a = (Artifact) o;
            if ("tgz".equals(a.getType())) {
                deps.add(a.getFile().getAbsolutePath());
                allTgzLibsResolved = allTgzLibsResolved && a.isResolved();
            }
        }
        if (!getLog().isDebugEnabled()) {
            deps.add("--silent");
        }

        if (!allTgzLibsResolved) {
            getLog().debug("Not All .tgz plugins resolved. Executing 'mvn dependency:go-offline' first");
            new ProcessExecutor()
				.name("Going Offline")
				.directory(project.getBasedir())
				.command("mvn dependency:go-offline")
				.execute(getLog());
        }

        new ProcessExecutor()
			.name("Dependency installation")
			.directory(project.getBasedir())
			.command(deps)
			.execute(getLog());
    }
}
