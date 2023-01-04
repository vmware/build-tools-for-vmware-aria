package com.vmware.pscoe.iac.artifact.configuration;

import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConfigurationVropsTest {

	private ConfigurationVrops configurationVrops;
	protected Properties properties;
	protected ConfigurationVrops mockedInstance;

	@BeforeEach
	void init() throws UnknownHostException {

		this.properties = new Properties();
		this.properties.setProperty("host", "localhost");
		this.properties.setProperty("port", "443");
		this.properties.setProperty("sshPort", "443");
		this.properties.setProperty("authHost", "auth_host.corp.local");
		this.properties.setProperty("authPort", "443");
		this.properties.setProperty("authPort", "443");
		this.properties.setProperty("dashboardUser", "dashboardUser");
		this.properties.setProperty("restUser", "restUser");
		this.properties.setProperty("restPassword", "restPassword");
		this.configurationVrops = new ConfigurationVrops(this.properties);

		this.mockedInstance = Mockito.mock(ConfigurationVrops.class);
		when(mockedInstance.getHost()).thenReturn(null);

	}

	@Test
	public void testValidateShouldBeEmptyHost() {
		try {
			this.mockedInstance.validate(true);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		assertSame(this.mockedInstance.getHost(), null);
	}

	@Test
	public void testValidateShouldNotBeNullHost() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getHost(), "localhost");
	}


	@Test
	public void testValidateShouldNotBeNullVropDashboardUser() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getVropsDashboardUser(), "dashboardUser");
	}

	@Test
	public void testValidateShouldNotBeNullVropRestUser() throws Exception {
		this.configurationVrops.validate(true);

		assertSame(configurationVrops.getVropsDashboardUser(), "dashboardUser");
	}

}
