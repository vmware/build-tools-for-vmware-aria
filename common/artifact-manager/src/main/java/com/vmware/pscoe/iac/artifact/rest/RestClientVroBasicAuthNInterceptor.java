package com.vmware.pscoe.iac.artifact.rest;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;

public class RestClientVroBasicAuthNInterceptor extends RestClientRequestInterceptor<ConfigurationVro> {

    protected RestClientVroBasicAuthNInterceptor(ConfigurationVro configuration, RestTemplate restTemplate) {
        super(configuration, restTemplate);
    }

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            String username = this.getConfiguration().getUsername();

            // Basic authentication could work with user without domain thus handling this case here.
            if (!StringUtils.isEmpty(this.getConfiguration().getDomain()) && !username.contains("@")) {
                username += "@" + this.getConfiguration().getDomain();
            }

            return new BasicAuthorizationInterceptor(username, this.getConfiguration().getPassword()).intercept(request, body, execution);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
