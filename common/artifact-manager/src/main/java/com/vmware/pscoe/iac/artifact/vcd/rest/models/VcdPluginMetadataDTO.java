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
package com.vmware.pscoe.iac.artifact.vcd.rest.models;

import com.vmware.pscoe.iac.artifact.model.vcd.VcdNgPackageManifest;

public final class VcdPluginMetadataDTO {

	/**
	 * pluginName.
	 */
	private final String pluginName;

	/**
	 * vendor.
	 */
	private final String vendor;

	/**
	 * description.
	 */
	private final String description;

	/**
	 * version.
	 */
	private final String version;

	/**
	 * license.
	 */
	private final String license;

	/**
	 * link.
	 */
	private final String link;

	/**
	 * tenantScoped.
	 */
	private boolean tenantScoped;

	/**
	 * providerScoped.
	 */
	private boolean providerScoped;

	/**
	 * enabled.
	 */
	private final boolean enabled;

	/**
	 * VcdPluginMetadataDTO.
	 * 
	 * @param manifest vcd package manifest.
	 */
	public VcdPluginMetadataDTO(VcdNgPackageManifest manifest) {
		pluginName = manifest.getName();
		vendor = manifest.getVendor();
		description = manifest.getDescription();
		version = manifest.getVersion();
		license = manifest.getLicense();
		link = manifest.getLink();
		tenantScoped = manifest.isTenantScoped();
		providerScoped = manifest.isProviderScoped();
		enabled = true;
	}

	/**
	 * getPluginName.
	 * 
	 * @return Plugin name.
	 */
	public String getPluginName() {
		return pluginName;
	}

	/**
	 * vendor.
	 * 
	 * @return Vendor.
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * description.
	 * 
	 * @return Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * version.
	 * 
	 * @return Version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * license.
	 * 
	 * @return License.
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * link.
	 * 
	 * @return Link.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * tenantScoped.
	 * 
	 * @return Tenant scoped.
	 */
	public boolean getTenantScoped() {
		return tenantScoped;
	}

	/**
	 * setTenantScoped.
	 * 
	 * @param tenantScoped tenant scoped.
	 */
	public void setTenantScoped(boolean tenantScoped) {
		this.tenantScoped = tenantScoped;
	}

	/**
	 * providerScoped.
	 * 
	 * @return Provider scoped.
	 */
	public boolean getProviderScoped() {
		return providerScoped;
	}

	/**
	 * setProviderScoped.
	 * 
	 * @param providerScoped provider scoped.
	 */
	public void setProviderScoped(boolean providerScoped) {
		this.providerScoped = providerScoped;
	}

	/**
	 * enabled.
	 * 
	 * @return Enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
}
