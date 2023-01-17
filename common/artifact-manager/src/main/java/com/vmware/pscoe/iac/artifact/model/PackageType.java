package com.vmware.pscoe.iac.artifact.model;

public enum PackageType {
    VRO("package", "vro"), 
    VRA("vra", "vra"), 
    VRANG("vra-ng", "vra-ng"), 
    VCDNG("vcd-ng", "vcd-ng"), 
    VROPS("vrops", "vrops"),
    VRLI("vrli", "vrli"),
    ABX("abx", "abx"),
    BASIC("bsc", "bsc"),
    CS("cs", "cs");

    private final String packageContainer;
    private final String packageExtension;
    

    PackageType(String packageExtension, String packageContainer) {
        this.packageContainer = packageContainer;
        this.packageExtension = packageExtension;
    }

    public String getPackageContainer() {
        return packageContainer;
    }

    public String getPackageExtention() {
        return packageExtension;
    }

    public static PackageType fromExtension(String packageFileExtension) {
        for (PackageType type : PackageType.values()) {
            if (type.packageExtension.equalsIgnoreCase(packageFileExtension)) {
                return type;
            }
        }
        return null;
    }

}
