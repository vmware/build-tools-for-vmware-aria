package com.vmware.pscoe.iac.artifact.model.vrang;

/*-
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

public class VraNgCatalogItemType {
	/**
	 * id.
	 */
	private VraNgContentSourceType id;

	/**
	 * link.
	 */
	private String link;

	/**
	 * name.
	 */
	private String name;

	/**
	 * VraNgCatalogItemType constructor.
	 * 
	 * @param idIn
	 * @param linkIn
	 * @param nameIn
	 */
	public VraNgCatalogItemType(final VraNgContentSourceType idIn, final String linkIn, final String nameIn) {
		this.id = idIn;
		this.link = linkIn;
		this.name = nameIn;
	}

	/**
	 * getId.
	 * 
	 * @return id VraNgContentSourceType
	 */
	public VraNgContentSourceType getId() {
		return this.id;
	}

}
