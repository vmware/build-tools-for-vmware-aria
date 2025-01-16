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
package com.vmware.pscoe.iac.artifact.aria.automation.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Deserialized Custom Form in a format suitabe for the REST API to work with.
 * Still to store this form on the file system, please convert it to
 * VraNgCustomFormAndData object first.
 * 
 * @see VraNgCustomForm
 */
public class VraNgCustomForm implements Identifiable {
	/**
	 * id.
	 */
	private String id;

	/**
	 * name.
	 */
	private String name;

	/**
	 * The form serialized into a string, so when the whole VraNgCustomForm object
	 * is serealized on the
	 * file system repo, it will look as double serialized object such as" <br/>
	 * <br/>
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
	 * rather than
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
	 */
	private String form;

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
	 * Constructor function for VraNgCustomForm. This object is used by the REST API
	 * to export/import custom forms
	 * from/to Aria Automation. Still this is not suitable for serializing and
	 * storeing it on the file system as then
	 * the form field (which has its own internal structure) will be serialized as
	 * one line string making it very
	 * difficult for a human to work with it.
	 * 
	 * @param idIn         Id value
	 * @param nameIn       name value
	 * @param formIn       form value. This is expected to be a form converted in
	 *                     string format event if it represents a
	 *                     (YAML or JSON) object it will be converted into string.
	 *                     If you serialize this VraNgCustomForm
	 *                     object, it will contain the form field properly
	 *                     serialized as one line string and any new lines
	 *                     properly escaped (\n).
	 * @param stylesIn     form styles
	 * @param sourceIdIn   form source id value
	 * @param sourceTypeIn form source type value
	 * @param typeIn       form type value
	 * @param statusIn     form status
	 * @param formFormatIn form format
	 * @see VraNgCustomForm#form
	 * @see VraNgCustomFormAndData
	 * @see VraNgCustomFormAndData#form
	 */
	public VraNgCustomForm(final String idIn, final String nameIn, final String formIn, final String stylesIn,
			final String sourceIdIn, final String sourceTypeIn,
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
	 * Construct new VraNgCustomForm suoitable for the REST API, from
	 * VraNgCustomFormAndData object which is
	 * read from the file system repository.
	 * 
	 * @param repoForm Form as represented in the file system repository.
	 * @see VraNgCustomForm#form
	 * @see VraNgCustomFormAndData
	 * @see VraNgCustomFormAndData#form
	 */
	public VraNgCustomForm(VraNgCustomFormAndData repoForm) {
		this.id = repoForm.getId();
		this.name = repoForm.getName();
		this.styles = repoForm.getStyles();
		this.sourceType = repoForm.getSourceType();
		this.sourceId = repoForm.getSourceId();
		this.type = repoForm.getType();
		this.status = repoForm.getStatus();
		this.formFormat = repoForm.getFormFormat();
		JsonElement json = repoForm.getForm();
		if (json == null || json.isJsonNull()) {
			return;
		}
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
			this.form = json.getAsJsonPrimitive().getAsString();
		} else if (json.isJsonObject()) {
			Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
			this.form = gson.toJson(json);
		} else {
			this.form = json.toString();
		}
	}

	/**
	 * Getter for id.
	 * 
	 * @return id String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Setter for name.
	 * 
	 * @param nameIn String
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 * Getter for name.
	 * 
	 * @return name String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter for form.
	 * 
	 * @return form String
	 */
	public String getForm() {
		return this.form;
	}

	/**
	 * Getter for styles.
	 * 
	 * @return styles String
	 */
	public String getStyles() {
		return this.styles;
	}

	/**
	 * Getter for sourceType.
	 * 
	 * @return sourceType String
	 */
	public String getSourceType() {
		return this.sourceType;
	}

	/**
	 * Getter for sourceId.
	 * 
	 * @return sourceId String
	 */
	public String getSourceId() {
		return this.sourceId;
	}

	/**
	 * Setter for sourceId. Override in case of 812 blueprint form version.
	 * 
	 * @param sourceIdIn String
	 */
	public void setSourceId(final String sourceIdIn) {
		this.sourceId = sourceIdIn;
	}

	/**
	 * Getter for type.
	 * 
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
	 * 
	 * @return status String
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Getter for formFormat.
	 * 
	 * @return formFormat String
	 */
	public String getFormFormat() {
		return this.formFormat;
	}

	/**
	 * Setter for form.
	 * 
	 * @param formIn String
	 */
	public void setForm(final String formIn) {
		this.form = formIn;
	}
}
