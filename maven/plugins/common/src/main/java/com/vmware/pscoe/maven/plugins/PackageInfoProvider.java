package com.vmware.pscoe.maven.plugins;

public interface PackageInfoProvider {
    String getPackageName();

    String getVersion();

    String getDescription();

    void setDescription(String description);
}
