package com.vmware.pscoe.iac.artifact.rest;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.rest.helpers.VcdApiHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RestClientVcdBasicAuthInterceptor extends RestClientRequestInterceptor<ConfigurationVcd> {
	private final Logger logger = LoggerFactory.getLogger(RestClientVcdBasicAuthInterceptor.class);

	private String vcloudToken;
	private String bearerToken;
	private MediaType contentType;

	private static final String URL_SESSION = "/api/sessions";
	private static final String URL_VERSION = "/api/versions";
	private static final String HEADER_VCLOUD_TOKEN = "x-vcloud-authorization";
	private static final String HEADER_BEARER_TOKEN = "x-vmware-vcloud-access-token";
	private static final String HEADER_AUTHORIZATION = "Authorization";

	protected RestClientVcdBasicAuthInterceptor(ConfigurationVcd configuration, RestTemplate restTemplate,
			String apiVersion) {
		super(configuration, restTemplate);
		this.contentType = VcdApiHelper.buildMediaType("application/*+json", apiVersion);
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
		try {
			if (!request.getURI().getPath().contains(URL_SESSION) && !request.getURI().getPath().contains(URL_VERSION)) {
				if (this.vcloudToken == null) {
					logger.info("Aquiring vCD auth token...");
					acquireToken(request);
					logger.info("vCD auth token aquired");
				}
				
				request.getHeaders().add(HEADER_VCLOUD_TOKEN, this.vcloudToken);
				request.getHeaders().add(HEADER_AUTHORIZATION, VcdApiHelper.buildBearerToken(this.bearerToken));
			}
			
			return execution.execute(request, body);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void acquireToken(HttpRequest request) throws JsonProcessingException {
		final URI tokenUri = UriComponentsBuilder.newInstance().scheme(request.getURI().getScheme())
				.host(request.getURI().getHost()).port(request.getURI().getPort()).path(URL_SESSION).build().toUri();

		// Prepare Headers
		final HttpHeaders headers = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(contentType);

		headers.setAccept(acceptableMediaTypes);
		headers.add(HEADER_AUTHORIZATION, VcdApiHelper.buildBasicAuth(this.getConfiguration().getUsername(),
				this.getConfiguration().getDomain(), this.getConfiguration().getPassword()));
		final HttpEntity<String> entity = new HttpEntity<>(headers);
		
		final ResponseEntity<String> response = getRestTemplate().exchange(tokenUri, HttpMethod.POST, entity,
				String.class);
		this.vcloudToken = response.getHeaders().get(HEADER_VCLOUD_TOKEN).get(0);
		this.bearerToken = response.getHeaders().get(HEADER_BEARER_TOKEN).get(0);
	}

}
