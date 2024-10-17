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
package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgCatalogItemType {
	/**
	 * id.
	 */
	private VraNgContentSourceType id;

	/**
	 * VraNgCatalogItemType constructor.
	 * 
	 * @param idIn vra-ng content source type id value
	 * @param linkIn link value
	 * @param nameIn name value
	 */
	public VraNgCatalogItemType(final VraNgContentSourceType idIn, final String linkIn, final String nameIn) {
		this.id = idIn;
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
