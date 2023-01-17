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

public class VcdPluginMetadataDTO {
    private final String pluginName;
    private final String vendor;
    private final String description;
    private final String version;
    private final String license;
    private final String link;
    private final boolean tenant_scoped;
    private final boolean provider_scoped;
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

	public String getPluginName() {
		return pluginName;
	}

	public String getVendor() {
		return vendor;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

	public String getLicense() {
		return license;
	}

	public String getLink() {
		return link;
	}

	public boolean isTenant_scoped() {
		return tenant_scoped;
	}

	public boolean isProvider_scoped() {
		return provider_scoped;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
