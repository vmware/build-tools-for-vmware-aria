package com.vmware.pscoe.iac.artifact.configuration;

import com.vmware.pscoe.iac.artifact.model.PackageType;

import java.util.Properties;

public abstract class ConfigurationWithRefreshToken extends Configuration{

	public static final String REFRESH_TOKEN = "refresh.token";

	protected ConfigurationWithRefreshToken(PackageType type, Properties props) {
		super(type, props);
	}

	public abstract String getRefreshToken();
}
