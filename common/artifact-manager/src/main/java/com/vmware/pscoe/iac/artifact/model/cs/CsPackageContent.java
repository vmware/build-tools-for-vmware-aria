package com.vmware.pscoe.iac.artifact.model.cs;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class CsPackageContent extends PackageContent<CsPackageContent.ContentType> {

    public enum ContentType implements
            PackageContent.ContentType {
        PIPELINE("pipeline"),
        VARIABLE("variable"),
        CUSTOM_INTEGRATION("custom-integration"),
        ENDPOINT("endpoint"),
        GIT_WEBHOOK("git-webhook"),
        DOCKER_WEBHOOK("docker-webhook"),
        GERRIT_TRIGGER("gerrit-trigger"),
        GERRIT_LISTENER("gerrit-listener");

        private final String type;

        private ContentType(String type) {
            this.type = type;
        }

        public String getTypeValue() {
            return this.type;
        }

        public static ContentType getInstance(String type) {
            for (ContentType ct : ContentType.values()) {
                if (ct.getTypeValue().equalsIgnoreCase(type)) {
                    return ct;
                }
            }
            return null;
        }
    }

    public CsPackageContent(List<Content<ContentType>> content) {
        super(content);
    }
}
