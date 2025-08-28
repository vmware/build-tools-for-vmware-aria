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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.aria.automation.store.helpers.VraNgCustomFormSerializer;

public class VraNgCustomFormSerializerTest {
	protected String resourceActionSerializedString;
	protected String resourceActionDeserializedString;
	protected JsonObject resourceActionSerialized;
	protected JsonObject resourceActionDeserialized;

	@BeforeEach
	void init() throws IOException {
		ClassLoader cl = getClass().getClassLoader();

		// Read source files
		try {
			resourceActionSerializedString = IOUtils.toString(
					cl.getResourceAsStream("test/fixtures/resourceActionWithForm.json"), StandardCharsets.UTF_8);
			resourceActionSerialized = JsonParser.parseString(resourceActionSerializedString).getAsJsonObject();

			resourceActionDeserializedString = IOUtils.toString(
					cl.getResourceAsStream("test/fixtures/resourceActionWithPrettifiedForm.json"),
					StandardCharsets.UTF_8);
			resourceActionDeserialized = JsonParser.parseString(resourceActionDeserializedString).getAsJsonObject();
		} catch (IOException ex) {
			throw ex;
		}
	}

	@AfterEach
	void tearDown() {
		resourceActionSerializedString = null;
		resourceActionDeserializedString = null;
		resourceActionSerialized = null;
		resourceActionDeserialized = null;
	}

	@Test
	void testCustomFormDeserialization() {
		// JsonParser is used to compare the stringified objects and ignore
		// whitespace/new line characters
		assertEquals(JsonParser.parseString(resourceActionDeserializedString),
				JsonParser.parseString(VraNgCustomFormSerializer.deserialize(resourceActionSerializedString)));
	}

	@Test
	void testDeserializedJsonDeserialization() {
		assertEquals(JsonParser.parseString(VraNgCustomFormSerializer.deserialize(resourceActionDeserializedString)),
				JsonParser.parseString(VraNgCustomFormSerializer.deserialize(resourceActionSerializedString)));
	}

	@Test
	void testCustomFormSerialization() {
		JsonElement serializedForm = resourceActionSerialized.getAsJsonObject("formDefinition").get("form");
		JsonElement deserializedForm = resourceActionDeserialized.getAsJsonObject("formDefinition").get("form");

		// Validate serialization logic
		assertTrue(serializedForm.isJsonPrimitive());
		assertTrue(serializedForm.getAsJsonPrimitive().isString());
		assertTrue(deserializedForm.isJsonObject());

		VraNgCustomFormSerializer.serialize(resourceActionDeserialized);
		deserializedForm = resourceActionDeserialized.getAsJsonObject("formDefinition").get("form");
		assertTrue(deserializedForm.isJsonPrimitive());
		assertTrue(deserializedForm.getAsJsonPrimitive().isString());

		assertEquals(JsonParser.parseString(serializedForm.getAsString()),
				JsonParser.parseString(deserializedForm.getAsString()));

		// Validate serialization doesn't change an already serialized JSON
		VraNgCustomFormSerializer.serialize(resourceActionSerialized);
		serializedForm = resourceActionSerialized.getAsJsonObject("formDefinition").get("form");
		assertTrue(serializedForm.isJsonPrimitive());
		assertTrue(serializedForm.getAsJsonPrimitive().isString());

		assertEquals(JsonParser.parseString(serializedForm.getAsString()),
				JsonParser.parseString(deserializedForm.getAsString()));
	}
}
