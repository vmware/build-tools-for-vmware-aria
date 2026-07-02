/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2026 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.vcf.automation.store.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent;

/**
 * This class extends the PackageContent class and provides a list of content
 * types specific to VCFA.
 */
public final class VcfaPackageContent extends PackageContent<VcfaPackageMemberType> {
    // 1. Declare the field here
    private List<String> blueprint;
    // ... other properties like subscription, flavorMapping, etc ...

    /**
     * Constructor for VcfaPackageContent.
     *
     * @param content the content of the package.
     */
    public VcfaPackageContent(List<Content<VcfaPackageMemberType>> content) {
        super(content);
    }

    /**
     * Retrieves the list of tracked subscription names from the package manifest.
     *
     * @return a list of subscription names, or an empty list if none are declared.
     */
    public List<String> getSubscription() {
        if (this.getContent() == null) {
            return Collections.emptyList();
        }

        List<String> subscriptionNames = new ArrayList<>();

        // Loop through the flat list of individual content items
        for (Content<VcfaPackageMemberType> contentItem : this.getContent()) {
            // Check if this specific item is a subscription
            if (contentItem.getType() == VcfaPackageMemberType.SUBSCRIPTION) {
                String name = contentItem.getName();
                if (name != null) {
                    subscriptionNames.add(name);
                }
            }
        }

        return subscriptionNames;
    }

    public List<String> getBlueprint() { 
        return this.blueprint; 
    }
    
    public void setBlueprint(List<String> blueprint) { 
        this.blueprint = blueprint; 
    }

    // 1. Add the field property (ensure it matches the naming convention of the
    // parser)
    private List<String> contentSources;

    // 2. Add the getter method used by VcfaContentSourceStore.deleteContent()
    public List<String> getContentSources() {
        return this.contentSources;
    }

    // 3. Add the setter method used by the deserializer/parser
    public void setContentSources(List<String> contentSources) {
        this.contentSources = contentSources;
    }
}
