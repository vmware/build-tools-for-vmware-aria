package com.vmware.pscoe.iac.artifact.model.vra;

public enum VraPackageMemberType {
    COMPOSITE_BLUEPRINT("composite-blueprint"), 
    XAAS_BLUEPRINT("xaas-blueprint"), 
    XAAS_RESOURCE_ACTION("xaas-resource-action"), 
    XAAS_RESOURCE_TYPE("xaas-resource-type"), 
    XAAS_RESOURCE_MAPPING("xaas-resource-mapping"), 
    SOFTWARE_COMPONENT("software-component"), 
    PROPERTY_DEFINITION("property-definition"), 
    PROPERTY_GROUP("property-group"), 
    COMPONENT_PROFILE_VALUE("component-profile-value"),
    WORKFLOW_SUBSCRIPTION("workflow-subscription", false),
    GLOBAL_PROPERTY_DEFINITION("global-property-definition", false),
    GLOBAL_PROPERTY_GROUP("global-property-group", false);

    /*
     * Not supported
     * 
     * form-definition component-profile-value o11n-package-type
     * reservation-type-category-type reservation-type-type xaas-bundle-content
     */

    private final String name;
    private final boolean isNativeContent;

    VraPackageMemberType(String name) {
        this(name, true);
    }

    VraPackageMemberType(String name, boolean isNativeContent) {
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

    public static VraPackageMemberType fromString(String name) {
        for (VraPackageMemberType type : VraPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
