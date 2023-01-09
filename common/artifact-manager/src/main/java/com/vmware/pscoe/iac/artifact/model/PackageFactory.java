package com.vmware.pscoe.iac.artifact.model;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class PackageFactory {

    private static final Pattern nameVersionPattern = Pattern.compile("(.*)-(\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?)(\\.*)?");

    private PackageFactory() {
    }

    private static Package createPackage(PackageType type, String id, String packageFQN, String packageFilePath) {
        Matcher matcher = nameVersionPattern.matcher(packageFQN);
        if (matcher.find()) {
            return new Package(type, id, matcher.group(1), matcher.group(2), packageFilePath);
        } else {
            return new Package(type, id, packageFQN, null, packageFilePath);
        }
    }

    public static Package getInstance(PackageType type, File packageFile) {
        return getInstance(type, null, packageFile);
    }

    public static Package getInstance(PackageType type, File packageFile, String packageName) {
        return createPackage(type, null, packageName, packageFile.getAbsolutePath());
    }

    public static Package getInstance(PackageType type, String id, File packageFile) {
        String packageFileBaseName = FilenameUtils.getBaseName(packageFile.getName());
        return createPackage(type, id, packageFileBaseName, packageFile.getAbsolutePath());
    }
}
