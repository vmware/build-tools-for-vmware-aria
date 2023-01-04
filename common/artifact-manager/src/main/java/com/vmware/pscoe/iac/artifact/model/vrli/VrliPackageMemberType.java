package com.vmware.pscoe.iac.artifact.model.vrli;

public enum VrliPackageMemberType {
    ALERTS("alerts"), CONTENT_PACKS("content_packs");
	
    private final String name;

	VrliPackageMemberType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static VrliPackageMemberType fromString(String name) {
        for (VrliPackageMemberType type : VrliPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
