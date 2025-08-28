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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgResourceAction;

public class VraNgResourceActionTest {
	@Test
	void testCustomFormConstruction() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		JsonObject resourceActionSerialized;
		JsonObject resourceActionDeserialized;
		String resourceActionDeserializedRead;

		// Read source files
		try {
			String resourceActionSerializedRead = IOUtils.toString(
					cl.getResourceAsStream("test/fixtures/resourceActionWithForm.json"), StandardCharsets.UTF_8);
			resourceActionSerialized = JsonParser.parseString(resourceActionSerializedRead).getAsJsonObject();

			resourceActionDeserializedRead = IOUtils.toString(
					cl.getResourceAsStream("test/fixtures/resourceActionWithPrettifiedForm.json"),
					StandardCharsets.UTF_8);
			resourceActionDeserialized = JsonParser.parseString(resourceActionDeserializedRead).getAsJsonObject();
			System.out.println("Deserialized " + resourceActionDeserializedRead);
		} catch (IOException ex) {
			throw ex;
		}

		// Create class instance
		JsonElement id = resourceActionSerialized.get("id");
		JsonElement name = resourceActionSerialized.get("displayName");
		String json = resourceActionSerialized.toString();
		VraNgResourceAction resourceInstanceSerialized = new VraNgResourceAction(id.getAsString(), name.getAsString(),
				json);

		// Extract the forms to validate the serialized one is properly transformed by
		// the class instance
		JsonObject parsedActionSerialized = JsonParser.parseString(resourceInstanceSerialized.getJson())
				.getAsJsonObject();
		JsonElement serializedForm = parsedActionSerialized.getAsJsonObject("formDefinition").get("form");
		JsonElement deserializedForm = resourceActionDeserialized.getAsJsonObject("formDefinition").get("form");

		// Validate that the "form" from the prettified JSON object and the
		// serialized/stringified string are treated equally and converted to prettified
		// JSON object
		assertTrue(serializedForm.isJsonObject());
		assertTrue(serializedForm.getAsJsonObject().get("layout") != null);

		assertTrue(deserializedForm.isJsonObject());
		assertTrue(deserializedForm.getAsJsonObject().get("layout") != null);

		assertEquals(serializedForm, deserializedForm);

		// Validate that the "form" in the prettified JSON object is preserved when
		// creating a new VraNgResourceAction instance

		JsonElement id2 = resourceActionDeserialized.get("id");
		JsonElement name2 = resourceActionDeserialized.get("displayName");
		String json2 = resourceActionDeserialized.toString();
		VraNgResourceAction resourceInstanceDeserialized = new VraNgResourceAction(id2.getAsString(),
				name2.getAsString(),
				json2);

		JsonObject parsedActionDeserialized = JsonParser.parseString(resourceInstanceDeserialized.getJson())
				.getAsJsonObject();
		JsonElement deserializedForm2 = parsedActionDeserialized.getAsJsonObject("formDefinition").get("form");

		assertTrue(deserializedForm2.isJsonObject());
		assertTrue(deserializedForm2.getAsJsonObject().get("layout") != null);
		assertEquals(serializedForm, deserializedForm2);
		assertEquals(deserializedForm, deserializedForm2);

	}
}
