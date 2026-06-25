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
package com.vmware.pscoe.iac.artifact.vcf.automation.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.vcf.automation.configuration.ConfigurationVcfAuto;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.CatalogEntitlement;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaBlueprint;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItem;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCatalogItemForm;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaContentSource;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaCustomResourceType;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPolicy;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaPropertyGroup;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaResourceAction;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaScenario;
import com.vmware.pscoe.iac.artifact.vcf.automation.models.VcfaSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the high-level RestClientVcfAuto wrapper methods.
 */
class RestClientVcfAutoTest {

    private RestClientVcfAuto client;
    private ConfigurationVcfAuto config;

    @BeforeEach
    void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(Configuration.HOST, "localhost");
        props.setProperty(Configuration.PORT, "443");
        props.setProperty(ConfigurationVcfAuto.PROJECT_NAME, "My Project");
        props.setProperty(ConfigurationVcfAuto.ORGANIZATION_NAME, "My Org");
        props.setProperty(ConfigurationVcfAuto.REFRESH_TOKEN, "token");
        config = ConfigurationVcfAuto.fromProperties(props);
        client = spy(new RestClientVcfAuto(config));
    }

    // ==================== Blueprints ====================

    @Test
    void testGetBlueprints() throws IOException {
        List<VcfaBlueprint> expected = Collections.singletonList(new VcfaBlueprint());
        doReturn(expected).when(client).getBlueprintsPrimitive();

        assertEquals(expected, client.getBlueprints());
    }

    @Test
    void testGetBlueprintById() throws IOException {
        VcfaBlueprint expected = new VcfaBlueprint();
        doReturn(expected).when(client).getBlueprintByIdPrimitive("bp-1");

        assertEquals(expected, client.getBlueprintById("bp-1"));
    }

    @Test
    void testCreateBlueprint() throws IOException {
        VcfaBlueprint expected = new VcfaBlueprint();
        doReturn(expected).when(client).createBlueprintPrimitive(anyMap());

        assertEquals(expected, client.createBlueprint(new VcfaBlueprint()));
    }

    @Test
    void testUpdateBlueprint() throws IOException {
        VcfaBlueprint expected = new VcfaBlueprint();
        doReturn(expected).when(client).updateBlueprintPrimitive(eq("bp-1"), anyMap());

        assertEquals(expected, client.updateBlueprint("bp-1", new VcfaBlueprint()));
    }

    @Test
    void testDeleteBlueprint() throws IOException {
        doNothing().when(client).deleteBlueprintPrimitive("bp-1");
        assertDoesNotThrow(() -> client.deleteBlueprint("bp-1"));
    }

    @Test
    void testVersionBlueprint() throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("version", "1.0.0");
        Map<String, Object> expected = new HashMap<>(payload);
        doReturn(expected).when(client).versionBlueprintPrimitive(eq("bp-1"), anyMap());

        assertEquals(expected, client.versionBlueprint("bp-1", payload));
    }

    @Test
    void testCreateBlueprintForm() throws IOException {
        doNothing().when(client).createBlueprintFormPrimitive(eq("bp-1"), anyMap(), anyString(), anyString(),
                anyString(), anyBoolean(), anyString());

        assertDoesNotThrow(() -> client.createBlueprintForm("bp-1", Collections.emptyMap(), "yaml", "name",
                "desc", true, ".body{}"));
    }

    @Test
    void testGetBlueprintVersions() throws IOException {
        doReturn("[]").when(client).getBlueprintVersionsPrimitive("bp-1");
        assertEquals("[]", client.getBlueprintVersions("bp-1"));
    }

    @Test
    void testUnreleaseBlueprintVersion() throws IOException {
        doNothing().when(client).unreleaseBlueprintVersionPrimitive("bp-1", "v-1");
        assertDoesNotThrow(() -> client.unreleaseBlueprintVersion("bp-1", "v-1"));
    }

    // ==================== Catalog Items ====================

    @Test
    void testGetCatalogItems() throws IOException {
        List<VcfaCatalogItem> expected = Collections.singletonList(new VcfaCatalogItem());
        doReturn(expected).when(client).getCatalogItemsPrimitive();
        assertEquals(expected, client.getCatalogItems());
    }

    @Test
    void testCreateCatalogItem() throws IOException {
        VcfaCatalogItem expected = new VcfaCatalogItem();
        doReturn(expected).when(client).createCatalogItemPrimitive(any(VcfaCatalogItem.class));
        assertEquals(expected, client.createCatalogItem(new VcfaCatalogItem()));
    }

    @Test
    void testDeleteCatalogItem() throws IOException {
        doNothing().when(client).deleteCatalogItemPrimitive("ci-1");
        assertDoesNotThrow(() -> client.deleteCatalogItem("ci-1"));
    }

    @Test
    void testGetCatalogItemForm() throws IOException {
        VcfaCatalogItemForm expected = new VcfaCatalogItemForm();
        doReturn(expected).when(client).getCatalogItemFormPrimitive("com.vmw.blueprint", "bp-1");
        assertEquals(expected, client.getCatalogItemForm("com.vmw.blueprint", "bp-1"));
    }

    @Test
    void testUpdateCatalogItemForm() throws IOException {
        doNothing().when(client).updateCatalogItemFormPrimitive(anyString(), anyString(), any(VcfaCatalogItemForm.class));
        assertDoesNotThrow(() -> client.updateCatalogItemForm("com.vmw.blueprint", "ci-1", new VcfaCatalogItemForm()));
    }

    // ==================== Content Sources ====================

    @Test
    void testGetContentSources() throws IOException {
        List<VcfaContentSource> expected = Collections.singletonList(new VcfaContentSource());
        doReturn(expected).when(client).getContentSourcesPrimitive();
        assertEquals(expected, client.getContentSources());
    }

    @Test
    void testCreateContentSource() throws IOException {
        VcfaContentSource expected = new VcfaContentSource();
        doReturn(expected).when(client).createContentSourcePrimitive(anyMap());
        assertEquals(expected, client.createContentSource(new VcfaContentSource()));
    }

    @Test
    void testUpdateContentSource() throws IOException {
        VcfaContentSource expected = new VcfaContentSource();
        doReturn(expected).when(client).updateContentSourcePrimitive(eq("cs-1"), anyMap());
        assertEquals(expected, client.updateContentSource("cs-1", new VcfaContentSource()));
    }

    @Test
    void testDeleteContentSource() throws IOException {
        doNothing().when(client).deleteContentSourcePrimitive("cs-1");
        assertDoesNotThrow(() -> client.deleteContentSource("cs-1"));
    }

    // ==================== Custom Resources ====================

    @Test
    void testGetCustomResources() throws IOException {
        List<VcfaCustomResourceType> expected = Collections.singletonList(new VcfaCustomResourceType());
        doReturn(expected).when(client).getCustomResourcesPrimitive();
        assertEquals(expected, client.getCustomResources());
    }

    @Test
    void testCreateCustomResourceType() throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Custom");
        doReturn(response).when(client).createCustomResourceTypePrimitive(anyMap());

        VcfaCustomResourceType input = new VcfaCustomResourceType();
        VcfaCustomResourceType result = client.createCustomResourceType(input);
        assertNotNull(result);
    }

    @Test
    void testDeleteCustomResourceType() throws IOException {
        doNothing().when(client).deleteCustomResourceTypePrimitive("cr-1");
        assertDoesNotThrow(() -> client.deleteCustomResourceType("cr-1"));
    }

    // ==================== Resource Actions ====================

    @Test
    void testGetResourceActions() throws IOException {
        List<VcfaResourceAction> expected = Collections.singletonList(new VcfaResourceAction());
        doReturn(expected).when(client).getResourceActionsPrimitive();
        assertEquals(expected, client.getResourceActions());
    }

    @Test
    void testCreateResourceAction() throws IOException {
        doReturn("{\"id\":\"ra-1\"}").when(client).createResourceActionPrimitiveString(anyMap());
        assertEquals("{\"id\":\"ra-1\"}", client.createResourceAction(new VcfaResourceAction()));
    }

    @Test
    void testDeleteResourceAction() throws IOException {
        doNothing().when(client).deleteResourceActionPrimitive("ra-1");
        assertDoesNotThrow(() -> client.deleteResourceAction("ra-1"));
    }

    // ==================== Property Groups ====================

    @Test
    void testGetPropertyGroups() throws IOException {
        List<VcfaPropertyGroup> expected = Collections.singletonList(new VcfaPropertyGroup());
        doReturn(expected).when(client).getPropertyGroupsPrimitive();
        assertEquals(expected, client.getPropertyGroups());
    }

    @Test
    void testCreatePropertyGroup() throws IOException {
        VcfaPropertyGroup expected = new VcfaPropertyGroup();
        doReturn(expected).when(client).createPropertyGroupPrimitive(anyMap());
        assertEquals(expected, client.createPropertyGroup(new VcfaPropertyGroup()));
    }

    @Test
    void testUpdatePropertyGroup() throws IOException {
        VcfaPropertyGroup expected = new VcfaPropertyGroup();
        doReturn(expected).when(client).updatePropertyGroupPrimitive(eq("pg-1"), anyMap());
        assertEquals(expected, client.updatePropertyGroup("pg-1", new VcfaPropertyGroup()));
    }

    @Test
    void testDeletePropertyGroup() throws IOException {
        doNothing().when(client).deletePropertyGroupPrimitive("pg-1");
        assertDoesNotThrow(() -> client.deletePropertyGroup("pg-1"));
    }

    // ==================== Policies ====================

    @Test
    void testGetPolicies() throws IOException {
        List<VcfaPolicy> expected = Collections.singletonList(new VcfaPolicy());
        doReturn(expected).when(client).getPoliciesPrimitive();
        assertEquals(expected, client.getPolicies());
    }

    @Test
    void testCreateOrUpdatePolicy() throws IOException {
        VcfaPolicy expected = new VcfaPolicy();
        doReturn(expected).when(client).createOrUpdatePolicyPrimitive(anyMap());
        assertEquals(expected, client.createOrUpdatePolicy(new VcfaPolicy()));
    }

    @Test
    void testDeletePolicy() throws IOException {
        doNothing().when(client).deletePolicyPrimitive("pol-1");
        assertDoesNotThrow(() -> client.deletePolicy("pol-1"));
    }

    // ==================== Catalog Entitlements ====================

    @Test
    void testGetCatalogEntitlements() throws IOException {
        List<CatalogEntitlement> expected = Collections.singletonList(new CatalogEntitlement());
        doReturn(expected).when(client).getCatalogEntitlementsPrimitive();
        assertEquals(expected, client.getCatalogEntitlements());
    }

    // ==================== Scenarios ====================

    @Test
    void testGetScenarios() throws IOException {
        VcfaScenario item = new VcfaScenario();
        item.setId("sc-1");
        doReturn(Collections.singletonList(item)).when(client).getScenariosListPrimitive();
        doReturn(new VcfaScenario()).when(client).getScenarioExpandedPrimitive("sc-1");

        List<VcfaScenario> result = client.getScenarios();
        assertEquals(1, result.size());
    }

    @Test
    void testCreateScenario() throws IOException {
        VcfaScenario expected = new VcfaScenario();
        doReturn(expected).when(client).createScenarioPrimitive(anyMap());
        assertEquals(expected, client.createScenario("{\"id\":\"sc-1\"}"));
    }

    @Test
    void testUpdateScenario() throws IOException {
        VcfaScenario expected = new VcfaScenario();
        doReturn(expected).when(client).updateScenarioPrimitive(eq("sc-1"), anyMap());
        assertEquals(expected, client.updateScenario("sc-1", "{\"id\":\"sc-1\"}"));
    }

    @Test
    void testDeleteScenario() throws IOException {
        doNothing().when(client).deleteScenarioPrimitive("sc-1");
        assertDoesNotThrow(() -> client.deleteScenario("sc-1"));
    }

    // ==================== Subscriptions ====================

    @Test
    void testGetSubscriptions() throws IOException {
        List<VcfaSubscription> expected = Collections.singletonList(new VcfaSubscription());
        doReturn(expected).when(client).getSubscriptionsPrimitive();
        assertEquals(expected, client.getSubscriptions());
    }

    @Test
    void testCreateSubscription() throws IOException {
        VcfaSubscription expected = new VcfaSubscription();
        doReturn(expected).when(client).createSubscriptionPrimitive(anyMap());
        assertEquals(expected, client.createSubscription(new VcfaSubscription()));
    }

    @Test
    void testUpdateSubscription() throws IOException {
        VcfaSubscription expected = new VcfaSubscription();
        doReturn(expected).when(client).updateSubscriptionPrimitive(eq("sub-1"), anyMap());
        assertEquals(expected, client.updateSubscription("sub-1", new VcfaSubscription()));
    }

    @Test
    void testDeleteSubscription() throws IOException {
        doNothing().when(client).deleteSubscriptionPrimitive("sub-1");
        assertDoesNotThrow(() -> client.deleteSubscription("sub-1"));
    }

    // ==================== ABX & Project/Org Resolution ====================

    @Test
    void testGetAbxActionIdByName() throws IOException {
        doReturn("abx-1").when(client).getAbxActionIdByNamePrimitive("action");
        assertEquals("abx-1", client.getAbxActionIdByName("action"));
    }

    @Test
    void testGetAbxActionNameById() throws IOException {
        doReturn("action").when(client).getAbxActionNameByIdPrimitive("abx-1");
        assertEquals("action", client.getAbxActionNameById("abx-1"));
    }

    @Test
    void testGetProjectNameById() throws IOException {
        assertEquals("My Project", client.getProjectNameById());
    }

    @Test
    void testGetProjectIdByName() throws IOException {
        doReturn("proj-1").when(client).getProjectIdPrimitive("My Project");
        assertEquals("proj-1", client.getProjectId("My Project"));
    }

    @Test
    void testGetOrganizationId() throws IOException {
        doReturn("proj-1").when(client).getProjectId();
        doReturn("org-1").when(client).getOrgIdFromProjectPrimitive("proj-1");
        assertEquals("org-1", client.getOrganizationId());
    }

    @Test
    void testGetOrganizationIdByName() throws IOException {
        doReturn("org-1").when(client).getOrganizationIdPrimitive("My Org");
        assertEquals("org-1", client.getOrganizationId("My Org"));
    }

    @Test
    void testGetVroTargetIntegrationEndpointLink() throws IOException {
        doReturn("/link").when(client).getVroTargetIntegrationEndpointLinkPrimitive();
        assertEquals("/link", client.getVroTargetIntegrationEndpointLink());
    }
}
