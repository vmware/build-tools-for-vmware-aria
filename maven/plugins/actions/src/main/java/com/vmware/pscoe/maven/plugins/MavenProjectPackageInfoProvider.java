package com.vmware.pscoe.maven.plugins;

import org.apache.maven.project.MavenProject;

public class MavenProjectPackageInfoProvider implements PackageInfoProvider {
    private final String suffix;
    private final MavenProject project;

    public MavenProjectPackageInfoProvider(MavenProject project) {
        this(project, null);
    }

    public MavenProjectPackageInfoProvider(MavenProject project, String suffix) {
        this.project = project;
        this.suffix = suffix == null ? "" : suffix;
    }

    @Override
    public String getPackageName() {
        return project.getGroupId() + "." + project.getName() + "-" + project.getVersion() + suffix;

    }

    @Override
    public String getVersion() {
        return project.getVersion();
    }

    @Override
    public String getDescription() {
        return this.project.getDescription();
    }

    @Override
    public void setDescription(String description) {
        this.project.setDescription(description);
    }

}
