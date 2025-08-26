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

import java.util.List;

import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent;

/**
 * This class extends the PackageContent class and provides a list of content
 * types specific to VCFA.
 */
public final class VcfaPackageContent extends PackageContent<VcfaPackageMemberType> {

    /**
     * Constructor for VcfaPackageContent.
     *
     * @param content the content of the package.
     */
    public VcfaPackageContent(List<Content<VcfaPackageMemberType>> content) {
        super(content);
    }
}
