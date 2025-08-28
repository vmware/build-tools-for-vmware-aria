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
package com.vmware.pscoe.iac.artifact.aria.automation.store.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class VraNgCustomFormSerializer {
	private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	/**
	 * Takes as input double serialized JSON string returned by vRA API and converts
	 * it to a prettified JSON string suitable for storing on the file system after
	 * a "pull operation".
	 * 
	 * Sample source:
	 * 
	 * <code>
	 * {
	 * 	"id": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
	 * 	"name": "add_disk_to_vm",
	 * 	"runnableItem": {...},
	 * 	"formDefinition": {
	 * 	"name": "add_disk_to_vm",
	 * 	"form": "{\"layout\":{{\"pages\":[...]},\"schema\":{...},\"options\":{...}",
	 * 	"sourceType": "resource.action",
	 * 	"sourceId": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
	 * 	"type": "requestForm",
	 * 	"status": "ON",
	 * 	"formFormat": "JSON"
	 *  }
	 * }
	 * </code>
	 * 
	 * @param json
	 * @return
	 */
	public static String deserialize(String json) {
		JsonObject resourceActionJson = gson.fromJson(json, JsonObject.class);
		JsonObject formDefinition = resourceActionJson.getAsJsonObject("formDefinition");

		if (formDefinition == null) {
			return json;
		}

		JsonElement form = formDefinition.get("form");

		if (form == null) {
			return json;
		}

		// Prettify the nested stringified JSON
		if (form.isJsonPrimitive() && form.getAsJsonPrimitive().isString()) {
			String stringifiedForm = form.getAsString();
			JsonObject formJson = gson.fromJson(stringifiedForm, JsonObject.class);
			// replace the existing form with the prettified version
			formDefinition.add("form", formJson);
			return gson.toJson(resourceActionJson);
		}
		return json;
	}

	/**
	 * Takes as prettified JSON string and converts it to a double serialized JSON
	 * string expected by vRA API (usually during a "push" operation).
	 * 
	 * Sample source:
	 * <code>
	 * {
	 * 	"id": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
	 * 	"name": "add_disk_to_vm",
	 * 	"runnableItem": {...},
	 * 	"formDefinition": {
	 * 		"name": "add_disk_to_vm",
	 * 		"form": {
	 * 			"layout": {...},
	 * 			"schema": {...},
	 * 			"options": {
	 * 				"externalValidations": [...]
	 * 			}
	 * 		},
	 * 		"sourceType": "resource.action",
	 * 		"sourceId": "Cloud.vSphere.Machine.custom.add_disk_to_vm",
	 * 		"type": "requestForm",
	 * 		"status": "ON",
	 * 		"formFormat": "JSON"
	 * 	}
	 * }
	 * </code>
	 * 
	 * @param sourceJsonObject
	 */
	public static void serialize(JsonObject sourceJsonObject) {
		JsonObject sourceForm = sourceJsonObject.getAsJsonObject("formDefinition");
		if (sourceForm == null) {
			return;
		}

		JsonElement form = sourceForm.get("form");

		// If form is prettified JSON object convert it to a stringified JSON that the
		// API expects
		if (form != null && form.isJsonObject()) {
			String formAsString = gson.toJson(form);
			sourceForm.addProperty("form", formAsString);
		}
	}
}
