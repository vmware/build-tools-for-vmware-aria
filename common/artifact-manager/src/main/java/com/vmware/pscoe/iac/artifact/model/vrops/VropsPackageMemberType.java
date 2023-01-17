package com.vmware.pscoe.iac.artifact.model.vrops;

public enum VropsPackageMemberType {
    VIEW("view"), DASHBOARD("dashboard"), REPORT("report"), ALERT_DEFINITION("alert_definition"), SYMPTOM_DEFINITION("symptom_definition"), POLICY("policy"),
    SUPERMETRIC("supermetric"), RECOMMENDATION("recommendation"), METRICCONFIG("metric_config"), CUSTOM_GROUP("custom_group");

    private final String name;

    VropsPackageMemberType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static VropsPackageMemberType fromString(String name) {
        for (VropsPackageMemberType type : VropsPackageMemberType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
