package com.vmware.pscoe.iac.artifact.rest;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVroNg;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;

public class RestClientFactory {
    public static final String IGNORE_SSL_CERTIFICATE_VERIFICATION = "vrealize.ssl.ignore.certificate";
    public static final String IGNORE_SSL_HOSTNAME_VERIFICATION = "vrealize.ssl.ignore.hostname";
    public static final String CONNECTION_TIMEOUT = "vrealize.connection.timeout";
    public static final String SOCKET_TIMEOUT = "vrealize.socket.timeout";

    private static final Logger logger = LoggerFactory.getLogger(RestClientFactory.class);

    private RestClientFactory() {
        throw new IllegalStateException("Cannot instantiate the factory class: RestClientFactory");
    }

    private static boolean ignoreCertificate() {
        return Boolean.parseBoolean(System.getProperty(IGNORE_SSL_CERTIFICATE_VERIFICATION));
    }

    private static boolean ignoreHostname() {
        return Boolean.parseBoolean(System.getProperty(IGNORE_SSL_HOSTNAME_VERIFICATION));
    }

    private static Integer getConnectionTimeout() {
        Integer retVal = parseTimeoutValue(System.getProperty(CONNECTION_TIMEOUT), TimeoutType.CONNECTION);
        if (retVal == null) {
            return Configuration.DEFAULT_CONNECTION_TIMEOUT * 1000;
        }
        return retVal;
    }

    private static Integer getSocketTimeout() {
        Integer retVal = parseTimeoutValue(System.getProperty(SOCKET_TIMEOUT), TimeoutType.SOCKET);
        if (retVal == null) {
            return Configuration.DEFAULT_SOCKET_TIMEOUT * 1000;
        }
        return retVal;
    }

    private static Integer parseTimeoutValue(String value, TimeoutType type) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value) * 1000;
        } catch (NumberFormatException e) {
            logger.warn("Unable to parse {} timeout value '{}', error: '{}'", type, value, e.getMessage());
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
		if (ignoreCertificate()) {
			httpClientBuilder.setSSLContext(sslContext);
			logger.warn("SSL: You are now ignoring certificate verification.");
		}
		if (ignoreHostname()) {
			httpClientBuilder.setSSLHostnameVerifier(new NoopHostnameVerifier());
			logger.warn("SSL: You are now ignoring hostname verification.");
		}

		if (proxy != null) {
		    httpClientBuilder.setProxy(proxy);
		    logger.info("Will use proxy " + proxy.toURI());
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(getConnectionTimeout())
                .setSocketTimeout(getSocketTimeout())
                .build();
        httpClientBuilder.setDefaultRequestConfig(config);

        CloseableHttpClient httpClient = httpClientBuilder.build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return response.getRawStatusCode() < 200 || response.getRawStatusCode() > 299;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append(response.getStatusText() + " ");
                if (response.getBody() != null) {
                    String message = org.apache.commons.io.IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
                    messageBuilder.append(message);
                }

                throw new HttpClientErrorException(response.getStatusCode(), messageBuilder.toString());
			}

		});

		return restTemplate;
	}

	public static RestClientVro getClientVroNg(ConfigurationVroNg configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

        RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVro(configuration, restTemplate);
	}

	public static RestClientVro getClientVro(ConfigurationVro configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());
		RestClientRequestInterceptor<ConfigurationVro> interceptor;

        logger.info("Authentication strategy: '{}'", configuration.getAuth());
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

		return new RestClientVro(configuration, restTemplate);
	}

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
                throw new UnsupportedOperationException("Unsupported authentication provider, supported providers: BASIC, AUTH_N");
        }
        restTemplate.getInterceptors().add(interceptor);

		return new RestClientVrops(configuration, restTemplate);
	}

	public static RestClientVra getClientVra(ConfigurationVra configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

		// Default Authentication is Basic
		// When other authentication mechanisms are introduced and interceptor
        // has to be instantiated based on the configuration property
        RestClientRequestInterceptor<ConfigurationVra> interceptor = new RestClientVraCafeAuthNInterceptor(configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVra(configuration, restTemplate);
	}

	public static RestClientVraNg getClientVraNg(ConfigurationVraNg configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());

        RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVraNg(configuration, restTemplate);
	}

	public static RestClientVcd getClientVcd(ConfigurationVcd configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

		RestClientVcd versionRestClient = new RestClientVcd(configuration, restTemplate);
		// vCD API version is passed to Content-Type headers.
		// No authentication is required to obtain API version
		String apiVersion = versionRestClient.getVersion();
        RestClientRequestInterceptor<ConfigurationVcd> interceptor = new RestClientVcdBasicAuthInterceptor(configuration, restTemplate, apiVersion);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVcd(configuration, restTemplate);
	}

	public static RestClientVrli getClientVrli(ConfigurationVrli configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate();

        RestClientRequestInterceptor<ConfigurationVrli> interceptor = new RestClientVrliAuthInterceptor(configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);

		return new RestClientVrli(configuration, restTemplate);
	}

    private enum TimeoutType {
        CONNECTION, SOCKET
    }
	
	public static RestClientCs getClientCs(ConfigurationCs configuration) {
		RestTemplate restTemplate = getInsecureRestTemplate(configuration.getProxy());
		RestClientRequestInterceptor<ConfigurationVraNg> interceptor = new RestClientVraNgAuthNInterceptor(configuration, restTemplate);
		restTemplate.getInterceptors().add(interceptor);
		return new RestClientCs(configuration, restTemplate);
	}
}
