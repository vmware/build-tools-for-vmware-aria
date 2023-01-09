package com.vmware.pscoe.iac.artifact.model.vrang;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VraNgPackageContent extends PackageContent<VraNgPackageContent.ContentType> {
    
    public enum ContentType implements PackageContent.ContentType { 
        BLUEPRINT("blueprint"),
        SUBSCRIPTION("subscription"),
        FLAVOR_MAPPING("flavor-mapping"),
        IMAGE_MAPPING("image-mapping"),
        STORAGE_PROFILE("storage-profile"),
        REGION_MAPPING("region-mapping"),
        CATALOG_ENTITLEMENT("catalog-entitlement"),
        CUSTOM_RESOURCE("custom-resource"),
        RESOURCE_ACTION("resource-action"),
        PROPERTY_GROUP("property-group"),
        CONTENT_SOURCE("content-source"),
        CATALOG_ITEM("catalog-item");
        private final String type;
        
        private ContentType(String type) {
            this.type = type;
        }
        
        public String getTypeValue(){
            return this.type;
        }
        
        public static ContentType getInstance(String type) {
            for(ContentType ct: ContentType.values()) {
                if(ct.getTypeValue().equalsIgnoreCase(type)) {
                    return ct;
                }
            }
            return null;
        }
        
    }

    public VraNgPackageContent(List<Content<ContentType>> content) {
        super(content);
    }

}
