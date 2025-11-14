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
package com.vmware.pscoe.iac.artifact.common.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.aria.automation.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.aria.automation.rest.RestClientVraNgAuthNInterceptor;
import com.vmware.pscoe.iac.artifact.aria.codestream.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.aria.codestream.rest.RestClientCs;
import com.vmware.pscoe.iac.artifact.aria.logs.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.RestClientVrliAuthInterceptor;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.v1.RestClientVrliV1;
import com.vmware.pscoe.iac.artifact.aria.logs.rest.v2.RestClientVrliV2;
import com.vmware.pscoe.iac.artifact.aria.operations.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.aria.operations.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.aria.operations.rest.RestClientVropsAuthNInterceptor;
import com.vmware.pscoe.iac.artifact.aria.operations.rest.RestClientVropsBasicAuthInterceptor;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVro.AuthProvider;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.configuration.ConfigurationVroNg;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.RestClientVroBasicAuthNInterceptor;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.RestClientVroSsoAuthNInterceptor;
import com.vmware.pscoe.iac.artifact.aria.orchestrator.rest.VraSsoAuth;
import com.vmware.pscoe.iac.artifact.common.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.vcd.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.vcd.rest.RestClientVcd;
import com.vmware.pscoe.iac.artifact.vcd.rest.RestClientVcdBasicAuthInterceptor;

public final class RestClientFactory {
	/**
	 * IGNORE_SSL_CERTIFICATE_VERIFICATION: Indicates whether to ignore SSL
	 * certificate verification.
	 */
	public static final String IGNORE_SSL_CERTIFICATE_VERIFICATION = "vrealize.ssl.ignore.certificate";

	/**
	 * IGNORE_SSL_HOSTNAME_VERIFICATION: Indicates whether to ignore SSL hostname
	 * verification.
	 */
	public static final String IGNORE_SSL_HOSTNAME_VERIFICATION = "vrealize.ssl.ignore.hostname";

	/**
	 * CONNECTION_TIMEOUT: Indicates the maximum time (in milliseconds) allowed for
	 * the client to establish a connection.
	 */
	public static final String CONNECTION_TIMEOUT = "vrealize.connection.timeout";

	/**
	 * SOCKET_TIMEOUT: Indicates the maximum time (in milliseconds) allowed for the
	 * client to wait for a response from the server.
	 */
	public static final String SOCKET_TIMEOUT = "vrealize.socket.timeout";

	/**
	 * The number of milliseconds in a second.
	 */
	public static final Integer TO_MILISECONDS_MULTIPLIER = 1000;

	/**
	 * This logger is used to log messages and exceptions related to the creation
	 * and usage of REST clients.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientFactory.class);

	/**
	 * The beginning of the HTTP status code range for successful responses.
	 */
	private static final Integer STATUS_CODE_2XX_RANGE_BEGIN = 200;

	/**
	 * The end of the HTTP status code range for successful responses.
	 */
	private static final Integer STATUS_CODE_2XX_RANGE_END = 299;

	/**
	 * VCF Automation version 9 prefix
	 */
	private static final String VRA_9_VERSION_PREFIX = "9.";

	/**
	 * Default provider admin domain
	 */
	private static final String SYSTEM_DOMAIN = "System";

	private RestClientFactory() {
		throw new IllegalStateException("Cannot instantiate the factory class: RestClientFactory");
	}

	private static boolean ignoreCertificate() {
		return Boolean.parseBoolean(System.getProperty(IGNORE_SSL_CERTIFICATE_VERIFICATION));
	}

	private static boolean ignoreHostname() {
		return Boolean.parseBoolean(System.getProperty(IGNORE_SSL_HOSTNAME_VERIFICATION));
	}

	private static Timeout getConnectionTimeout() {
		Integer retValMillis = parseTimeoutValue(System.getProperty(CONNECTION_TIMEOUT), TimeoutType.CONNECTION);
		if (retValMillis == null) {
			retValMillis = Configuration.DEFAULT_CONNECTION_TIMEOUT * TO_MILISECONDS_MULTIPLIER;
		}
		return Timeout.ofMilliseconds(retValMillis);
	}

	private static Timeout getSocketTimeout() {
		Integer retVal = parseTimeoutValue(System.getProperty(SOCKET_TIMEOUT), TimeoutType.SOCKET);
		if (retVal == null) {
			retVal = Configuration.DEFAULT_SOCKET_TIMEOUT * TO_MILISECONDS_MULTIPLIER;
		}
		return Timeout.ofMilliseconds(retVal);
	}

	private static Integer parseTimeoutValue(String value, TimeoutType type) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value) * TO_MILISECONDS_MULTIPLIER;
		} catch (NumberFormatException e) {
			LOGGER.warn("Unable to parse {} timeout value '{}', error: '{}'", type, value, e.getMessage());
		}
		return null;
	}

	private static RestTemplate getInsecureRestTemplate() {
		return RestClientFactory.getInsecureRestTemplate(null);
	}

	private static RestTemplate getInsecureRestTemplate(HttpHost proxy) {
		SSLContext sslContext;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}

		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder
				.create();

		if (ignoreCertificate() || ignoreHostname()) {
			SSLConnectionSocketFactoryBuilder socketConfig = SSLConnectionSocketFactoryBuilder.create();
			if (ignoreCertificate()) {
				socketConfig.setSslContext(sslContext);
				LOGGER.warn("SSL: You are now ignoring certificate verification.");
			}
			if (ignoreHostname()) {
				socketConfig.setHostnameVerifier(new NoopHostnameVerifier());
				LOGGER.warn("SSL: You are now ignoring hostname verification.");
			}
			// setTlsVersions?
			connectionManagerBuilder.setSSLSocketFactory(socketConfig.build());
		}

		httpClientBuilder.setConnectionManager(connectionManagerBuilder
				.setDefaultConnectionConfig(ConnectionConfig.custom()
						.setSocketTimeout(getSocketTimeout())
						.setConnectTimeout(getConnectionTimeout())
						.build())
				// setPoolConcurrencyPolicy, setConnPoolPolicy?
				.build());

		if (proxy != null) {
			httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
			LOGGER.info("Will use proxy {}", proxy.toURI());
		}

		CloseableHttpClient httpClient = httpClientBuilder.build();
		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		restTemplate.getMessageConverters()
				.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return response.getStatusCode().value() < STATUS_CODE_2XX_RANGE_BEGIN
						|| response.getStatusCode().value() > STATUS_CODE_2XX_RANGE_END;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				StringBuilder messageBuilder = new StringBuilder();
				HttpHeaders headers = response.getHeaders();
				messageBuilder.append(response.getStatusCode().value()).append(" ").append(response.getStatusText())
						.append("\n");
				headers.forEach((k, values) -> values
						.forEach(v -> messageBuilder.append(k).append(": ").append(v).append("\n")));

				if (response.getBody() != null && !response.getBody().equals("")) {
					messageBuilder.append("\n\n");
					String message = org.apache.commons.io.IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
					messageBuilder.append(message);
				}

				throw new HttpClientErrorException(response.getStatusCode(),
						"Error (" + messageBuilder.toString()
								+ ") while processing request, FQDN or IP may be incorrect or the server may be down.");
			}

		});

		return restTemplate;
	}

	private static String getVraApiVersion(Configuration configuration, RestTemplate restTemplate) {
		Properties properties = new Properties();

		properties.setProperty(Configuration.USERNAME, configuration.getUsername());
		properties.setProperty(Configuration.PASSWORD, configuration.getPassword());
		properties.setProperty(Configuration.PORT, configuration.getPort() + "");
		properties.setProperty(Configuration.HOST, configuration.getHost());

		ConfigurationVro configurationVro = ConfigurationVro.fromProperties(properties);

		VraSsoAuth vraClient = new VraSsoAuth(configurationVro, restTemplate);
		return vraClient.getVersion();
	}

	private static String getVcdApiVersion(ConfigurationVcd configuration, RestTemplate restTemplate) {
		RestClientVcd versionRestClient = new RestClientVcd(configuration, restTemplate);
		// VCD API version is passed to Content-Type headers.
		// No authentication is required to obtain API version
		return versionRestClient.getVersion();
	}

	private static ConfigurationVcd createConfigurationVcd(Configuration configuraiton) {
		Properties properties = new Properties();

		String fullUsername;

		if ((configuraiton instanceof ConfigurationVro)
				&& ((ConfigurationVro) configuraiton).getAuth() == AuthProvider.BASIC) {
			// In case of external Orchestrator with BASIC auth the domain is extracted as
			// part of the username so we need this check to prevent duplication
			fullUsername = configuraiton.getUsername();
		} else {
			fullUsername = String.format(ConfigurationVcd.USER_AT_DOMAIN_STRING_FORMAT, configuraiton.getUsername(),
					configuraiton.getDomain());
		}

		properties.setProperty(Configuration.USERNAME, fullUsername);
		properties.setProperty(Configuration.PASSWORD, configuraiton.getPassword());
		properties.setProperty(Configuration.PORT, configuraiton.getPort() + "");
		properties.setProperty(Configuration.HOST, configuraiton.getHost());

		if (configuraiton instanceof ConfigurationVraNg && ((ConfigurationVraNg) configuraiton).getOrgName() != null) {
			// Set organization in case provider admin (user@System) is executing a vra-ng
			// push for specific organization. Note that in ConfigurationVroNg organization
			// is missing and not required
			properties.setProperty(ConfigurationVraNg.ORGANIZATION_NAME,
					((ConfigurationVraNg) configuraiton).getOrgName());
		}

		return ConfigurationVcd.fromProperties(properties);
	}

	private static void attachVcdInterceptor(ConfigurationVcd configuration, RestTemplate restTemplate,
			Boolean useProviderAuth) {
		String vcdApiVersion = getVcdApiVersion(configuration, restTemplate);
		RestClientRequestInterceptor<ConfigurationVcd> vcdInterceptor = new RestClientVcdBasicAuthInterceptor(
				configuration, restTemplate, vcdApiVersion, useProviderAuth);
		restTemplate.getInterceptors().add(vcdInterceptor);
	}

	/**
	 * The function returns a RestClientVro object with a configured RestTemplate
	 * and
	 * RestClientVraNgAuthNInterceptor.
	 * 
	 * @param configuration The configuration parameter is an object of type
	 *                      ConfigurationVroNg. It is used
	 *                      to provide the necessary configuration settings for the
	 *                      RestClientVroNg class.
	 * @return The method is returning an instance of the RestClientVro class.
	 */
	public static RestClientVro getClientVroNg(ConfigurationVroNg configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();
		String apiVersion = getVraApiVersion(configuration, restTemplate);

		if (apiVersion.startsWith(VRA_9_VERSION_PREFIX)) {
			ConfigurationVcd vcdConfiguration = createConfigurationVcd(configuration);
			attachVcdInterceptor(vcdConfiguration, restTemplate, configuration.getDomain().equals(SYSTEM_DOMAIN));
		} else {
			RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(
					configuration, restTemplate);
			restTemplate.getInterceptors().add(interceptor);
		}

		return new RestClientVro(configuration, restTemplate);
	}

	/**
	 * The function `getClientVro` returns a `RestClientVro` object based on the
	 * provided
	 * `ConfigurationVro` object and authentication strategy.
	 * 
	 * @param configuration The "configuration" parameter is an object of type
	 *                      ConfigurationVro. It
	 *                      contains the necessary configuration settings for
	 *                      creating the RestClientVro object.
	 * @return The method is returning an instance of the RestClientVro class.
	 */
	public static RestClientVro getClientVro(ConfigurationVro configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());
		String apiVersion = getVraApiVersion(configuration, restTemplate);

		// For VCF 9 use VCD based interceptor to authenticate
		if (apiVersion.startsWith(VRA_9_VERSION_PREFIX)) {
			ConfigurationVcd vcdConfiguration = createConfigurationVcd(configuration);
			attachVcdInterceptor(vcdConfiguration, restTemplate, configuration.getDomain().equals(SYSTEM_DOMAIN));
		} else {
			RestClientRequestInterceptor<ConfigurationVro> interceptor;

			LOGGER.info("Authentication strategy: '{}'", configuration.getAuth());
			switch (configuration.getAuth()) {
				case VRA:
					interceptor = new RestClientVroSsoAuthNInterceptor(configuration, restTemplate);
					break;
				case BASIC:
					interceptor = new RestClientVroBasicAuthNInterceptor(configuration, restTemplate);
					break;
				default:
					throw new UnsupportedOperationException("Unsupported authentication provider");
			}
			restTemplate.getInterceptors().add(interceptor);
		}

		return new RestClientVro(configuration, restTemplate);
	}

	/**
	 * The function `getClientVrops` returns a `RestClientVrops` object based on the
	 * provided
	 * `ConfigurationVrops` object and authentication provider.
	 * 
	 * @param configuration The `configuration` parameter is an object of type
	 *                      `ConfigurationVrops`. It
	 *                      contains the configuration settings for the vRealize
	 *                      Operations (vROps) REST client. This includes
	 *                      information such as the vROps server URL, authentication
	 *                      provider, and credentials.
	 * @return The method is returning an instance of the RestClientVrops class.
	 */
	public static RestClientVrops getClientVrops(ConfigurationVrops configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

		RestClientRequestInterceptor<ConfigurationVrops> interceptor;
		switch (configuration.getAuthProvider()) {
			case BASIC:
				interceptor = new RestClientVropsBasicAuthInterceptor(configuration, restTemplate);
				break;
			case AUTH_N:
				interceptor = new RestClientVropsAuthNInterceptor(configuration, restTemplate);
				break;
			default:
				throw new UnsupportedOperationException(
						"Unsupported authentication provider, supported providers: BASIC, AUTH_N");
		}
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVrops(configuration, restTemplate);
	}

	/**
	 * The function returns a RestClientVraNg object with a configured RestTemplate
	 * and
	 * RestClientVraNgAuthNInterceptor.
	 * 
	 * @param configuration The "configuration" parameter is an instance of the
	 *                      ConfigurationVraNg class.
	 *                      It contains the necessary information and settings for
	 *                      creating the RestClientVraNg object.
	 * @return The method is returning an instance of the RestClientVraNg class.
	 */
	public static RestClientVraNg getClientVraNg(ConfigurationVraNg configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());
		String apiVersion = getVraApiVersion(configuration, restTemplate);

		// For VCF 9 use VCD based interceptor to authenticate
		if (apiVersion.startsWith(VRA_9_VERSION_PREFIX)) {
			ConfigurationVcd vcdConfiguration = createConfigurationVcd(configuration);
			attachVcdInterceptor(vcdConfiguration, restTemplate, configuration.getDomain().equals(SYSTEM_DOMAIN));
		} else {
			RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(
					configuration, restTemplate);
			restTemplate.getInterceptors().add(interceptor);
		}

		return new RestClientVraNg(configuration, restTemplate);
	}

	/**
	 * The function getClientVcd returns a RestClientVcd object with the specified
	 * configuration and
	 * authentication.
	 * 
	 * @param configuration The "configuration" parameter is an object of type
	 *                      ConfigurationVcd. It
	 *                      contains the necessary configuration settings for
	 *                      connecting to a VMware Cloud Director (VCD) instance.
	 *                      This could include information such as the VCD server
	 *                      URL, credentials, and any other required
	 *                      settings.
	 * @return The method is returning an instance of the RestClientVcd class.
	 */
	public static RestClientVcd getClientVcd(ConfigurationVcd configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();
		attachVcdInterceptor(configuration, restTemplate, true);

		return new RestClientVcd(configuration, restTemplate);
	}

	/**
	 * The function returns a RestClientVrliV1 object with a configured RestTemplate
	 * and
	 * RestClientVrliAuthInterceptor.
	 * 
	 * @param configuration The "configuration" parameter is an object of type
	 *                      ConfigurationVrli. It is
	 *                      used to provide the necessary configuration settings for
	 *                      the RestClientVrliV1 class.
	 * @return The method is returning an instance of the RestClientVrliV1 class.
	 */
	public static RestClientVrliV1 getClientVrliV1(ConfigurationVrli configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

		RestClientRequestInterceptor<ConfigurationVrli> interceptor = new RestClientVrliAuthInterceptor(configuration,
				restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVrliV1(configuration, restTemplate);
	}

	/**
	 * The function returns a RestClientVrliV2 object with a configured RestTemplate
	 * and
	 * RestClientVrliAuthInterceptor.
	 * 
	 * @param configuration The configuration parameter is an object of type
	 *                      ConfigurationVrli. It contains
	 *                      the necessary information and settings required to
	 *                      establish a connection with the VRli API.
	 * @return The method is returning an instance of the RestClientVrliV2 class.
	 */
	public static RestClientVrliV2 getClientVrliV2(ConfigurationVrli configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

		RestClientRequestInterceptor<ConfigurationVrli> interceptor = new RestClientVrliAuthInterceptor(configuration,
				restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVrliV2(configuration, restTemplate);
	}

	/**
	 * An enumeration of the different types of timeouts that can be configured for
	 * a connection.
	 */
	public enum TimeoutType {
		/**
		 * The connection timeout type.
		 * This represents the maximum time in milliseconds to wait for a connection to
		 * be established before giving up.
		 */
		CONNECTION,

		/**
		 * The socket timeout type.
		 * This represents the maximum time in milliseconds to wait for data to be
		 * received after a connection has been established before giving up.
		 */
		SOCKET
	}

	/**
	 * The function returns a RestClientCs object with a configured RestTemplate and
	 * RestClientRequestInterceptor.
	 * 
	 * @param configuration An object of type ConfigurationCs, which contains the
	 *                      configuration settings
	 *                      for the RestClientCs.
	 * @return The method is returning an instance of the RestClientCs class.
	 */
	public static RestClientCs getClientCs(ConfigurationCs configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());
		RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(
				configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);
		return new RestClientCs(configuration, restTemplate);
	}
}
