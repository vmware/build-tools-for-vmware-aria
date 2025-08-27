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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.VcfaConfiguration;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.VcfaRestClient;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageContent;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType;

import static com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType.*;

/**
 * Factory for VCFA stores and execution order.
 */
public class VcfaTypeStoreFactory {

    private static final VcfaPackageMemberType[] IMPORT_ORDER = new VcfaPackageMemberType[] {
            PROPERTY_GROUP, CONTENT_SOURCE, CUSTOM_RESOURCE, RESOURCE_ACTION, BLUEPRINT, SUBSCRIPTION,
            CATALOG_ENTITLEMENT, CATALOG_ITEM, POLICY, SCENARIO
    };

    public static VcfaPackageMemberType[] getImportOrder() {
        return IMPORT_ORDER;
    }

    private static final VcfaPackageMemberType[] EXPORT_ORDER = IMPORT_ORDER;

    public static VcfaPackageMemberType[] getExportOrder() {
        return EXPORT_ORDER;
    }

    private static final VcfaPackageMemberType[] DELETE_ORDER = new VcfaPackageMemberType[] {
            // delete policies and entitlements first
            POLICY, SCENARIO, CATALOG_ITEM, CATALOG_ENTITLEMENT, SUBSCRIPTION, CONTENT_SOURCE, BLUEPRINT,
            RESOURCE_ACTION, CUSTOM_RESOURCE, PROPERTY_GROUP
    };

    public static VcfaPackageMemberType[] getDeleteOrder() {
        return DELETE_ORDER;
    }

    private final VcfaRestClient restClient;
    private final Package vcfaPackage;
    private final VcfaConfiguration config;
    private final VcfaPackageDescriptor descriptor;

    protected VcfaTypeStoreFactory(final VcfaRestClient restClient, final Package vcfaPackage,
            final VcfaConfiguration config, final VcfaPackageDescriptor descriptor) {
        this.restClient = restClient;
        this.vcfaPackage = vcfaPackage;
        this.config = config;
        this.descriptor = descriptor;
    }

    public static VcfaTypeStoreFactory withConfig(final VcfaRestClient restClient, final Package vcfaPackage,
            final VcfaConfiguration config, final VcfaPackageDescriptor descriptor) {
        return new VcfaTypeStoreFactory(restClient, vcfaPackage, config, descriptor);
    }

    public IVcfaStore getStoreForType(final com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType type) {
        AbstractVcfaStore store = selectStore(type);
        store.init(restClient, vcfaPackage, config, descriptor);
        return store;
    }

    private static AbstractVcfaStore selectStore(
            final com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType type) {
        switch (type) {
            case BLUEPRINT:
                return new VcfaBlueprintStore();
            case CATALOG_ITEM:
                return new VcfaCatalogItemStore();
            case CONTENT_SOURCE:
                return new VcfaContentSourceStore();
            case CUSTOM_RESOURCE:
                return new VcfaCustomResourceStore();
            case RESOURCE_ACTION:
                return new VcfaResourceActionStore();
            case SUBSCRIPTION:
                return new VcfaSubscriptionStore();
            case POLICY:
                return new VcfaPolicyStore();
            case PROPERTY_GROUP:
                return new VcfaPropertyGroupStore();
            case CATALOG_ENTITLEMENT:
                return new VcfaEntitlementStore();
            case SCENARIO:
                return new VcfaScenarioStore();
            default:
                throw new RuntimeException("unknown type: " + type);
        }
    }
}
