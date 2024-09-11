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

public class VraNgItem {
	/**
	 * item id.
	 */
	private String id;
	/**
	 * item name.
	 */
	private String name;
	/**
	 * item type (CATALOG_SOURCE_IDENTIFIER / CATALOG_ITEM_IDENTIFIER).
	 */
	private String type;

	/**
	 * Default constructor.
	 */
	public VraNgItem() {
	}

	/**
	 * Constructor.
	 * 
	 * @param id   id of the item.
	 * @param name name of the item.
	 * @param type type of the item (CATALOG_SOURCE_IDENTIFIER /
	 *             CATALOG_ITEM_IDENTIFIER).
	 */
	public VraNgItem(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

	/**
	 * Get the id of the item.
	 * 
	 * @return item id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of the of the vRA item.
	 * 
	 * @param id item id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the name of the item.
	 * 
	 * @return item name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the of the vRA item.
	 * 
	 * @param name item name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the type of the item.
	 * 
	 * @return item type (CATALOG_SOURCE_IDENTIFIER / CATALOG_ITEM_IDENTIFIER).
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the of the vRA item.
	 * 
	 * @param type item type (CATALOG_SOURCE_IDENTIFIER / CATALOG_ITEM_IDENTIFIER).
	 */
	public void setType(String type) {
		this.type = type;
	}

}
