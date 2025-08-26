/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the 
 * terms and conditions of the subcomponent's license, as noted in the 
 * LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.vcf.automation.store.models;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageDescriptor;

public final class VcfaPackageDescriptor extends PackageDescriptor {

    private VcfaPackageContent content;

    /**
     * Constructor.
     */
    public VcfaPackageDescriptor() {
        super();
    }

    /**
     * @return the content
     */
    public VcfaPackageContent getContent() {
        return content;
    }

    /**
     * @param content the content to be set.
     */
    protected void setContent(VcfaPackageContent content) {
        this.content = content;
    }

    /**
     * Create a VcfaPackageDescriptor instance from file.
     * 
     * @param filesystemPath the file
     * @return VcfaPackageDescriptor instance
     */
    public static VcfaPackageDescriptor getInstance(File filesystemPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            VcfaPackageDescriptor pkgDescriptor = mapper.readValue(filesystemPath, VcfaPackageDescriptor.class);
            return pkgDescriptor;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load VCFA package descriptor.", e);
        }
    }
}
