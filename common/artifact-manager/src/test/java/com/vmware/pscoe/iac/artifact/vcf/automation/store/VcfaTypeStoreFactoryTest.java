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
package com.vmware.pscoe.iac.artifact.vcf.automation.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import com.vmware.pscoe.iac.artifact.common.store.Package;
import com.vmware.pscoe.iac.artifact.common.store.PackageFactory;
import com.vmware.pscoe.iac.artifact.common.store.PackageType;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.rest.RestClientVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageDescriptor;
import com.vmware.pscoe.iac.artifact.vcf.automation.store.models.VcfaPackageMemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for VcfaTypeStoreFactory store selection and execution order.
 */
class VcfaTypeStoreFactoryTest {

    @TempDir
    File tempDir;

    private RestClientVcfAuto restClient;
    private Package pkg;
    private ConfigurationVcfAuto config;
    private VcfaPackageDescriptor descriptor;
    private VcfaTypeStoreFactory factory;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClientVcfAuto.class);
        pkg = PackageFactory.getInstance(PackageType.VCF_AUTO_MODERN, new File(tempDir, "pkg"));
        config = mock(ConfigurationVcfAuto.class);
        descriptor = mock(VcfaPackageDescriptor.class);
        factory = VcfaTypeStoreFactory.withConfig(restClient, pkg, config, descriptor);
    }

    @Test
    void testOrderArrays_AreEqualAndNonEmpty() {
        VcfaPackageMemberType[] importOrder = VcfaTypeStoreFactory.getImportOrder();
        VcfaPackageMemberType[] exportOrder = VcfaTypeStoreFactory.getExportOrder();
        VcfaPackageMemberType[] deleteOrder = VcfaTypeStoreFactory.getDeleteOrder();

        assertNotNull(importOrder);
        assertTrue(importOrder.length > 0);
        assertArrayEquals(importOrder, exportOrder);
        assertArrayEquals(exportOrder, deleteOrder);
    }

    @Test
    void testOrderArrays_ContainExpectedTypes() {
        VcfaPackageMemberType[] order = VcfaTypeStoreFactory.getImportOrder();

        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.BLUEPRINT));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.CUSTOM_RESOURCE));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.POLICY));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.PROPERTY_GROUP));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.RESOURCE_ACTION));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.SCENARIO));
        assertTrue(Arrays.asList(order).contains(VcfaPackageMemberType.SUBSCRIPTION));
    }

    @Test
    void testGetStoreForType_MapsBlueprint() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.BLUEPRINT);
        assertTrue(store instanceof VcfaBlueprintStore);
    }

    @Test
    void testGetStoreForType_MapsCatalogItem() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.CATALOG_ITEM);
        assertTrue(store instanceof VcfaCatalogItemStore);
    }

    @Test
    void testGetStoreForType_MapsContentSource() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.CONTENT_SOURCE);
        assertTrue(store instanceof VcfaContentSourceStore);
    }

    @Test
    void testGetStoreForType_MapsCustomResource() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.CUSTOM_RESOURCE);
        assertTrue(store instanceof VcfaCustomResourceStore);
    }

    @Test
    void testGetStoreForType_MapsResourceAction() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.RESOURCE_ACTION);
        assertTrue(store instanceof VcfaResourceActionStore);
    }

    @Test
    void testGetStoreForType_MapsSubscription() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.SUBSCRIPTION);
        assertTrue(store instanceof VcfaSubscriptionStore);
    }

    @Test
    void testGetStoreForType_MapsPolicy() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.POLICY);
        assertTrue(store instanceof VcfaPolicyStore);
    }

    @Test
    void testGetStoreForType_MapsPropertyGroup() {
        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.PROPERTY_GROUP);
        assertTrue(store instanceof VcfaPropertyGroupStore);
    }

    @Test
    void testGetStoreForType_MapsCatalogEntitlement() throws Exception {
        when(descriptor.getCatalogEntitlement()).thenReturn(null);
        when(restClient.getCatalogEntitlements()).thenReturn(Arrays.asList());

        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.CATALOG_ENTITLEMENT);
        assertTrue(store instanceof VcfaEntitlementStore);

        store.exportContent();
        verify(restClient).getCatalogEntitlements();
    }

    @Test
    void testGetStoreForType_MapsScenario() throws Exception {
        when(restClient.getScenarios()).thenReturn(Arrays.asList());

        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.SCENARIO);
        assertTrue(store instanceof VcfaScenarioStore);

        store.exportContent();
        verify(restClient).getScenarios();
    }

    @Test
    void testGetStoreForType_InitializesDependencies() throws Exception {
        when(descriptor.getCatalogEntitlement()).thenReturn(null);
        when(restClient.getCatalogEntitlements()).thenReturn(Arrays.asList());

        IVcfaStore store = factory.getStoreForType(VcfaPackageMemberType.CATALOG_ENTITLEMENT);
        assertDoesNotThrow(store::exportContent);
    }
}
