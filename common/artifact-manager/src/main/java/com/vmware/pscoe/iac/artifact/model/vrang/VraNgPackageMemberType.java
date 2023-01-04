package com.vmware.pscoe.iac.artifact.model.vrang;

public enum VraNgPackageMemberType {
    BLUEPRINT("blueprint"),
    SUBSCRIPTION("subscription"),
    FLAVOR_PROFILE("flavor-profile"),
    IMAGE_PROFILE("image-profile"),
    STORAGE_PROFILE("storage-profile"),
    REGION_MAPPING("region-mapping"),
    CATALOG_ENTITLEMENT("catalog-entitlement"),
    CUSTOM_RESOURCE("custom-resource"),
    RESOURCE_ACTION("resource-action"),
	PROPERTY_GROUP("property-group"),
    CONTENT_SOURCE("content-source"),
    CATALOG_ITEM("catalog-item");

    private final String name;
    private final boolean isNativeContent;

    VraNgPackageMemberType(String name) {
        this(name, true);
    }

    VraNgPackageMemberType(String name, boolean isNativeContent) {
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

    public static VraNgPackageMemberType fromString(String name) {
        for (VraNgPackageMemberType type : VraNgPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
