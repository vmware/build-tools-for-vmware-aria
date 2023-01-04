package com.vmware.pscoe.iac.artifact.model.vrang;

import com.google.gson.annotations.SerializedName;

public enum VraNgCatalogEntitlementType {

    @SerializedName("CatalogItemIdentifier")
    CATALOG_ITEM_IDENTIFIER("CatalogItemIdentifier"),

    @SerializedName("CatalogSourceIdentifier")
    CATALOG_SOURCE_IDENTIFIER("CatalogSourceIdentifier"),

	@SerializedName("DEFAULT")
	DEFAULT("DEFAULT");

    private final String name;

    VraNgCatalogEntitlementType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static VraNgCatalogEntitlementType fromString(String name) {
        for (VraNgCatalogEntitlementType type : VraNgCatalogEntitlementType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}
