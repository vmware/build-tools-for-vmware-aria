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