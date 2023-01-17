package com.vmware.pscoe.maven.plugins;

/*
 * #%L
 * common
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
