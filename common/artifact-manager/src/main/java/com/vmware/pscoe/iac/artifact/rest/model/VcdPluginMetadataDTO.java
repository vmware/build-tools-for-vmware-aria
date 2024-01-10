package com.vmware.pscoe.iac.artifact.rest.model;

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
	 * tenant_scoped.
	 */
	private boolean tenant_scoped;

	/**
	 * provider_scoped.
	 */
	private boolean provider_scoped;

	/**
	 * enabled.
	 */
	private final boolean enabled;

	public VcdPluginMetadataDTO(VcdNgPackageManifest manifest) {
		pluginName = manifest.getName();
		vendor = manifest.getVendor();
		description = manifest.getDescription();
		version = manifest.getVersion();
		license = manifest.getLicense();
		link = manifest.getLink();
		tenant_scoped = manifest.isTenantScoped();
		provider_scoped = manifest.isProviderScoped();
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
	 * tenant_scoped.
	 * 
	 * @return Tenant scoped.
	 */
	public boolean getTenantScoped() {
		return tenant_scoped;
	}

	/**
	 * setTenantScoped.
	 * 
	 * @param tenant_scoped tenant scoped.
	 */
	public void setTenantScoped(boolean tenant_scoped) {
		this.tenant_scoped = tenant_scoped;
	}

	/**
	 * provider_scoped.
	 * 
	 * @return Provider scoped.
	 */
	public boolean getProviderScoped() {
		return provider_scoped;
	}

	/**
	 * setProviderScoped.
	 * 
	 * @param provider_scoped provider scoped.
	 */
	public void setProviderScoped(boolean provider_scoped) {
		this.provider_scoped = provider_scoped;
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
