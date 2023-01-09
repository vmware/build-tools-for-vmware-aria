package com.vmware.pscoe.iac.artifact.model.basic;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

public class BasicPackageDescriptor extends PackageDescriptor {
    private List<String> content;

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getMembersForType(BasicPackageMemberType type) {
        if (BasicPackageMemberType.CONTENT.equals(type)) {
            return getContent();
        } else {
            throw new RuntimeException(String.format("ContentType '%s' is not supported!", type));
        }
    }

    public static BasicPackageDescriptor getInstance(File filesystemPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        try {
            return mapper.readValue(filesystemPath, BasicPackageDescriptor.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to load Basic Package Descriptor [" + filesystemPath.getAbsolutePath() + "]", e);
        }
    }

}
