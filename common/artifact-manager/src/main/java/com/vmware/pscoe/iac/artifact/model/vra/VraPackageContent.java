package com.vmware.pscoe.iac.artifact.model.vra;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VraPackageContent extends PackageContent<VraPackageContent.ContentType> {
    
    public enum ContentType implements PackageContent.ContentType { 
        XAAS_BLUEPRINT("xaas-blueprint"),
        XAAS_RESOURCE_TYPE("xaas-resource-type"),
        XAAS_RESOURCE_ACTION("xaas-resource-action"),
        XAAS_RESOURCE_MAPPING("xaas-resource-mapping"),
        COMPOSITE_BLUEPRINT("composite-blueprint"),
        SOFTWARE_COMPONENT("software-component"),
        PROPERTY_GROUP("property-group"),
        PROPERTY_DICTIONARY("property-definition"),
        WORKFLOW_SUBSCRIPTION("workflow-subscription"),
        GLOBAL_PROPERTY_DEFINITION("global-property-definition"),
        GLOBAL_PROPERTY_GROUP("global-property-group");
        
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

    public VraPackageContent(List<Content<ContentType>> content) {
        super(content);
    }

}
