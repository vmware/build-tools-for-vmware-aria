package com.vmware.pscoe.iac.artifact.store.vrang;

/*-
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 - 2024 VMware
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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomFormAndData;
import java.io.IOException;

public class VraNgCustomFormTest {
    @Test
    void testCustomFormConstruction() throws IOException {
        // GIVEN
        String jsonString = "{\n"
        + "    \"layout\":{\n"
        + "       \"pages\":[\n"
        + "          {\n"
        + "             \"id\":\"page_1\",\n"
        + "             \"title\":\"General\",\n"
        + "             \"sections\":[\n"
        + "                {\n"
        + "                   \"id\":\"section_project\",\n"
        + "                   \"fields\":[\n"
        + "                      {\n"
        + "                         \"id\":\"project\",\n"
        + "                         \"display\":\"dropDown\",\n"
        + "                         \"signpostPosition\":\"right-middle\"\n"
        + "                      }\n"
        + "                   ]\n"
        + "                },\n"
        + "                {\n"
        + "                   \"id\":\"section_deploymentName\",\n"
        + "                   \"fields\":[\n"
        + "                      {\n"
        + "                         \"id\":\"deploymentName\",\n"
        + "                         \"display\":\"textField\",\n"
        + "                         \"signpostPosition\":\"right-middle\"\n"
        + "                      }\n"
        + "                   ]\n"
        + "                },\n"
        + "                {\n"
        + "                   \"id\":\"section_0\",\n"
        + "                   \"fields\":[\n"
        + "                      {\n"
        + "                         \"id\":\"numberOne\",\n"
        + "                         \"display\":\"decimalField\",\n"
        + "                         \"signpostPosition\":\"right-middle\"\n"
        + "                      }\n"
        + "                   ]\n"
        + "                },\n"
        + "                {\n"
        + "                   \"id\":\"section_1\",\n"
        + "                   \"fields\":[\n"
        + "                      {\n"
        + "                         \"id\":\"numberTwo\",\n"
        + "                         \"display\":\"decimalField\",\n"
        + "                         \"signpostPosition\":\"right-middle\"\n"
        + "                      }\n"
        + "                   ]\n"
        + "                }\n"
        + "             ],\n"
        + "             \"state\":{\n"
        + "                \n"
        + "             }\n"
        + "          }\n"
        + "       ]\n"
        + "    },\n"
        + "    \"schema\":{\n"
        + "       \"project\":{\n"
        + "          \"label\":\"Project\",\n"
        + "          \"type\":{\n"
        + "             \"dataType\":\"string\",\n"
        + "             \"isMultiple\":false\n"
        + "          },\n"
        + "          \"valueList\":{\n"
        + "             \"id\":\"projects\",\n"
        + "             \"type\":\"scriptAction\"\n"
        + "          },\n"
        + "          \"constraints\":{\n"
        + "             \"required\":true\n"
        + "          }\n"
        + "       },\n"
        + "       \"numberOne\":{\n"
        + "          \"label\":\"Number One\",\n"
        + "          \"type\":{\n"
        + "             \"dataType\":\"decimal\",\n"
        + "             \"isMultiple\":false\n"
        + "          },\n"
        + "          \"default\":1,\n"
        + "          \"constraints\":{\n"
        + "             \"required\":true\n"
        + "          }\n"
        + "       },\n"
        + "       \"numberTwo\":{\n"
        + "          \"label\":\"Number Two\",\n"
        + "         \"type\":{\n"
        + "             \"dataType\":\"decimal\",\n"
        + "             \"isMultiple\":false\n"
        + "          },\n"
        + "          \"default\":2,\n"
        + "          \"constraints\":{\n"
        + "             \"required\":true\n"
        + "          }\n"
        + "       },\n"
        + "       \"deploymentName\":{\n"
        + "          \"label\":\"Deployment Name\",\n"
        + "          \"type\":{\n"
        + "             \"dataType\":\"string\",\n"
        + "             \"isMultiple\":false\n"
        + "          },\n"
        + "          \"constraints\":{\n"
        + "             \"required\":true,\n"
        + "             \"max-value\":80\n"
        + "          }\n"
        + "       }\n"
        + "    },\n"
        + "    \"options\":{\n"
        + "       \"externalValidations\":[\n"
        + "          \n"
        + "       ]\n"
        + "    }\n"
        + " }\n";

        // WHEN
        VraNgCustomForm restFormSource = new VraNgCustomForm("123",
            "Some Name", jsonString, null,
            "com.vmw.vro.workflow", "requestForm",
            "requestForm", "ON", "JSON");
        VraNgCustomFormAndData repoFormSource =
        new VraNgCustomFormAndData("123",
            "Some Name", null, null,
            "com.vmw.vro.workflow", "requestForm",
            "requestForm", "ON", "JSON");

        repoFormSource.setForm(jsonString);
        JsonElement form = repoFormSource.getForm();
        VraNgCustomFormAndData repoFormNew =
        new VraNgCustomFormAndData(restFormSource);

        JsonElement formnew = repoFormNew.getForm();

        // THEN
        assertDoesNotThrow(() -> new VraNgCustomForm(repoFormSource));
        assertTrue(form.isJsonObject());
        assertTrue(!form.isJsonPrimitive());
        assertTrue(form.getAsJsonObject().get("layout") != null);

        assertTrue(formnew.isJsonObject());
        assertTrue(!formnew.isJsonPrimitive());
        assertTrue(formnew.getAsJsonObject().get("layout") != null);

    }
}
