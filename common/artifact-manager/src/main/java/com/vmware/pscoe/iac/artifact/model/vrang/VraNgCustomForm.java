package com.vmware.pscoe.iac.artifact.model.vrang;

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

/**
 * Deserialized Custom Form.
 */
public class VraNgCustomForm {
	/**
	 * id.
	 */
	private String id;

	/**
	 * name.
	 */
	private String name;

	/**
	 * form.
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
	 * Constructor function for VraNgCustomForm.
	 * @param idIn
	 * @param nameIn
	 * @param formIn
	 * @param stylesIn
	 * @param sourceIdIn
	 * @param sourceTypeIn
	 * @param typeIn
	 * @param statusIn
	 * @param formFormatIn
	 */
	public VraNgCustomForm(final String idIn, final String nameIn, final String formIn, final String stylesIn, final String sourceIdIn, final String sourceTypeIn,
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
	public String getForm() {
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
	 * @param sourceTypeIn
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
	public void setForm(final String formIn) {
		this.form = formIn;
	}
}
