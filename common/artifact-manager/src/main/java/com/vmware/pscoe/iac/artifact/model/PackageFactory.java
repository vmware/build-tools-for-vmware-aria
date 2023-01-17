package com.vmware.pscoe.iac.artifact.model;

/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

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
