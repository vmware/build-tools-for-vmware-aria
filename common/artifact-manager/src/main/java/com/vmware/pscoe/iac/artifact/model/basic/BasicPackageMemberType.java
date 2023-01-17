package com.vmware.pscoe.iac.artifact.model.basic;

public enum BasicPackageMemberType {
    CONTENT("content");

    private final String name;

    BasicPackageMemberType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static BasicPackageMemberType fromString(String name) {
        for (BasicPackageMemberType type : BasicPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
