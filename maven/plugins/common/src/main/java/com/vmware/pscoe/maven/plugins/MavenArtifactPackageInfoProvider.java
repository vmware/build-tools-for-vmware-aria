package com.vmware.pscoe.maven.plugins;

import org.apache.maven.artifact.Artifact;

public class MavenArtifactPackageInfoProvider implements PackageInfoProvider {
    private Artifact artifact;

    public MavenArtifactPackageInfoProvider(Artifact artifact) {
        this.artifact = artifact;
    }

    @Override
    public String getPackageName() {
        return String.format("%s.%s-%s", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    @Override
    public String getVersion() {
        return artifact.getVersion();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {
        // no need of implementation
    }
}
