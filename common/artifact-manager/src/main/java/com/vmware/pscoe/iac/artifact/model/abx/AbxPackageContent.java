package com.vmware.pscoe.iac.artifact.model.abx;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

import java.util.List;

public class AbxPackageContent extends PackageContent<AbxPackageContent.ContentType> {

    public enum ContentType implements PackageContent.ContentType {

        ACTION("action");

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

    public AbxPackageContent(List<Content<ContentType>> content) {
        super(content);
    }

}
