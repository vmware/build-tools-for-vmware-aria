package com.vmware.pscoe.iac.artifact.rest.model;

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
