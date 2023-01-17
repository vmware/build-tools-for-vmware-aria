package com.vmware.pscoe.iac.artifact.model.cs;

public enum CsPackageMemberType {
    PIPELINE("pipeline"),
    VARIABLE("variable"),
    ENDPOINT("endpoint"),
    CUSTOM_INTEGRATION("custom-integration"),
    GIT_WEBHOOK("git-webhook"),
    DOCKER_WEBHOOK("docker-webhook"),
    GERRIT_TRIGGER("gerrit-trigger"),
    GERRIT_LISTENER("gerrit-listener");

    private final String name;
    private final boolean isNativeContent;

    CsPackageMemberType(String name) {
        this(name, true);
    }

    CsPackageMemberType(String name, boolean isNativeContent) {
        this.name = name;
        this.isNativeContent = isNativeContent;
    }

    public boolean isNativeContent() {
        return this.isNativeContent;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static CsPackageMemberType fromString(String name) {
        for (CsPackageMemberType type : CsPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
