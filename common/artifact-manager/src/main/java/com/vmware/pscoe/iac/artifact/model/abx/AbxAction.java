package com.vmware.pscoe.iac.artifact.model.abx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class AbxAction {

    public String name;
    public String description;              // optional
    public String version;                  // optional
    public Platform platform;               // optional
    public AbxDefinition abx;
    public String id;                       // optional

    public String getName() {
        return (this.platform != null && this.platform.action != null) ? this.platform.action : this.name;
    }

    // The bundle is not part of the ABX package.json, however it is
    // useful to be stored as file reference for enhanced usage.

    public File bundle;

    public void setBundle(File bundle) {
        this.bundle = bundle;
    }

    public String getBundleAsB64() throws IOException {
        byte[] content = Files.readAllBytes(bundle.toPath());
        return Base64.getEncoder().encodeToString(content);
    }
}
