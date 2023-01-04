package com.vmware.pscoe.iac.artifact.rest;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.Configuration;

public abstract class RestClientRequestInterceptor<T extends Configuration> implements ClientHttpRequestInterceptor {

    private RestTemplate restTemplate;
    private T operationsContext;

    private RestClientRequestInterceptor() {
    }

    protected RestClientRequestInterceptor(T operationsContext, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.operationsContext = operationsContext;
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected T getConfiguration() {
        return operationsContext;
    }

}
