package com.vmware.pscoe.iac.artifact.rest;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.vmware.pscoe.iac.artifact.configuration.Configuration;

public abstract class RestClient {

    protected abstract Configuration getConfiguration();

    protected URIBuilder getURIBuilder() {
        URIBuilder uriBuilder = new URIBuilder().setScheme("https").setHost(getConfiguration().getHost());
        int port = getConfiguration().getPort();
        if (port != 443) {
            // set the port only if not default, otherwise vRA Cloud complains about invalid Host header
            uriBuilder = uriBuilder.setPort(port);
        }

        return uriBuilder;
    }

    protected URI getURI(URIBuilder builder) {
        try {
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getVersion();

    protected static HttpEntity<String> getDefaultHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<String>(headers);
    }

}
