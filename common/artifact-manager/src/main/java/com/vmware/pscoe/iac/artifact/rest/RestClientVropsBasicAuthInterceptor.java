package com.vmware.pscoe.iac.artifact.rest;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;

public class RestClientVropsBasicAuthInterceptor extends RestClientRequestInterceptor<ConfigurationVrops> {

    protected RestClientVropsBasicAuthInterceptor(ConfigurationVrops configuration, RestTemplate restTemplate) {
        super(configuration, restTemplate);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            return new BasicAuthorizationInterceptor(getConfiguration().getVropsRestUser(), getConfiguration().getVropsRestPassword()).intercept(request, body,
                    execution);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}