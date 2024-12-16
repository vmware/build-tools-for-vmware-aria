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
package com.vmware.pscoe.iac.artifact.aria.model;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Deserialized Custom Form in a form that is suitable to store on the file system (repository).
 * If you need to use the custom form data with REST API, please first convert it to VraNgCustomForm object.
 * @see VraNgCustomForm
 */
public class VraNgCustomFormAndData {
	/**
	 * id.
	 */
	private String id;

	/**
	 * name.
	 */
	private String name;

	/**
	 * The form represented as JSON object which has its own structure. So when the whole VraNgCustomFormAndData is
	 * serialized on the file system repo, the form will look as natural part of the whole object. <br/><br/>
	 * Example:
	 * <code>
	 *     {
	 *   "id": "e694a748-7067-47d1-91a4-614da73dda03",
	 *   "name": "Test",
	 *   "form": {
	 *     "layout": {
	 *     ...
	 *     },
	 *     "schema": {
	 *     ...
	 *     },
	 *     "options": {
	 *     ...
	 *     }
	 *   },
	 *   "styles": null,
	 *   "sourceType": "com.vmw.blueprint",
	 *   "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
	 *   "type": "requestForm",
	 *   "status": "ON",
	 *   "formFormat": "JSON"
	 * }
	 * </code>
	 * and not as double serialized string:
	 * <code>
	 * {
	 *   "id": "e694a748-7067-47d1-91a4-614da73dda03",
	 *   "name": "Test",
	 *   "form": "{\"layout\": {...},\"schema\": {...},\"options\": {...}}",
	 *   "styles": null,
	 *   "sourceType": "com.vmw.blueprint",
	 *   "sourceId": "71ac6ebc-6a94-3c5a-8c00-2a44ddf81bce",
	 *   "type": "requestForm",
	 *   "status": "ON",
	 *   "formFormat": "JSON"
	 * }
	 * </code>
	 */
	private JsonElement form;

	/**
	 * style.
	 */
	private String styles;

	/**
	 * sourceType.
	 */
	private String sourceType;

	/**
	 * sourceId.
	 */
	private String sourceId;

	/**
	 * type.
	 */
	private String type;

	/**
	 * status.
	 */
	private String status;

	/**
	 * formFormat.
	 */
	private String formFormat;

	/**
	 * Constructor function for VraNgCustomForm.
	 * @param idIn Id value
	 * @param nameIn name value
	 * @param formIn form value in the form of a deserialized object.
	 * @param stylesIn form styles
	 * @param sourceIdIn form source id value
	 * @param sourceTypeIn form source type value
	 * @param typeIn form type value
	 * @param statusIn form status
	 * @param formFormatIn form format
	 * @see VraNgCustomFormAndData#form
	 * @see VraNgCustomForm
	 * @see VraNgCustomForm#form
	 */
	public VraNgCustomFormAndData(final String idIn, final String nameIn, final JsonElement formIn, final String stylesIn, final String sourceIdIn, final String sourceTypeIn,
						   final String typeIn, final String statusIn, final String formFormatIn) {
		this.id = idIn;
		this.name = nameIn;
		this.form = formIn;
		this.styles = stylesIn;
		this.sourceType = sourceTypeIn;
		this.sourceId = sourceIdIn;
		this.type = typeIn;
		this.status = statusIn;
		this.formFormat = formFormatIn;
	}

	/**
	 * Construct new VraNgCustomFormAndData from VraNgCustomForm which is received as part of the REST API calls.
	 * @param restForm the form as returned by REST API calls.
	 * @see VraNgCustomFormAndData#form
	 * @see VraNgCustomForm
	 * @see VraNgCustomForm#form
	 */
	public VraNgCustomFormAndData(VraNgCustomForm restForm) {
		this.id = restForm.getId();
		this.name = restForm.getName();
		this.styles = restForm.getStyles();
		this.sourceType = restForm.getSourceType();
		this.sourceId = restForm.getSourceId();
		this.type = restForm.getType();
		this.status = restForm.getStatus();
		this.formFormat = restForm.getFormFormat();
		setForm(restForm.getForm());
	}

	/**
	 * Getter for id.
	 * @return id String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Setter for name.
	 * @param nameIn String
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Getter for name.
	 * @return name String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter for form.
	 * @return form String
	 */
	public JsonElement getForm() {
		return this.form;
	}

	/**
	 * Getter for styles.
	 * @return styles String
	 */
	public String getStyles() {
		return this.styles;
	}

	/**
	 * Getter for sourceType.
	 * @return sourceType String
	 */
	public String getSourceType() {
		return this.sourceType;
	}

	/**
	 * Getter for sourceId.
	 * @return sourceId String
	 */
	public String getSourceId() {
		return this.sourceId;
	}

	/**
	 * Setter for sourceId. Override in case of 812 blueprint form version.
	 * @param sourceIdIn String
	 */
	public void setSourceId(final String sourceIdIn) {
		this.sourceId = sourceIdIn;
	}

	/**
	 * Getter for type.
	 * @return type String
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * sourceType.
	 *
	 * @param sourceTypeIn source type
	 */
	public void setSourceType(final String sourceTypeIn) {
		this.sourceType = sourceTypeIn;
	}

	/**
	 * Getter for status.
	 * @return status String
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Getter for formFormat.
	 * @return formFormat String
	 */
	public String getFormFormat() {
		return this.formFormat;
	}

	/**
	 * Setter for form.
	 * @param formIn String
	 */
	public void setForm(final JsonObject formIn) {
		this.form = formIn;
	}

	/**
	 * Set the form data for the custom form using a string representation of the form.
	 * The string is expected to contain a serialized JSON object that represents the actual form data.
	 * So this method will try to deserialized the input string into JSON Element, then store it in.
	 * If deserialization fails, then a
	 * @param formIn form data serialized as string.
	 */
	public void setForm(final String formIn) {
		if (formIn == null) {
			return;
		}
		JsonElement json = JsonParser.parseString(formIn);
		this.form = json;
	}
}
