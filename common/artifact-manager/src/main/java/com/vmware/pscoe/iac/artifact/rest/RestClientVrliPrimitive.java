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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.model.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.ContentPackDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.ContentPackMetadataListDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ResourcesDTO;

public class RestClientVrliPrimitive extends RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClientVrliPrimitive.class);
    private static final String API_PREFIX = "/api/v1";
    private static final String VRLI_VERSION = API_PREFIX + "/version";
    private static final String ALERTS_API = API_PREFIX + "/alerts";
    private static final String CONTENT_PACKS_API = API_PREFIX + "/content/contentpack";
    private static final String CONTENT_PACKS_LIST_API = API_PREFIX + "/content/contentpack/list";

    private static final String MAP_ALERT_DATA_ERROR = "Unable to map alert data";
    private static final String PROCESS_ALERT_DATA_ERROR = "Unable to process alert data";
    private static final String MAP_CONTENT_PACK_DATA_ERROR = "Unable to map content pack data";
    private static final String PROCESS_CONTENT_PACK_DATA_ERROR = "Unable to process content pack data";

    private static final String VRLI_RESOURCE_KEY_TYPE = "LogInsight Server";
    private static final String VRLI_88_VERSION = "8.8";

    private ConfigurationVrli configuration;
    private RestTemplate restTemplate;
    private String vrliVersion;
    private RestClientVrops vropsRestClient;

    protected RestClientVrliPrimitive(ConfigurationVrli configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
        this.vropsRestClient = getVropsRestClient();
    }

    @Override
    protected Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public String getVersion() {
        if (this.vrliVersion != null && !this.vrliVersion.isEmpty()) {
            return this.vrliVersion;
        }

        URI url = getURI(getURIBuilder().setPath(VRLI_VERSION));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        this.vrliVersion = JsonPath.parse(response.getBody()).read("$.version");

        return this.vrliVersion;
    }

    protected String getContentPackPrimitive(String contentPackNamespace) {
        String uriPattern = CONTENT_PACKS_API + "/%s";
        String contentPackUriString = String.format(uriPattern, contentPackNamespace);
        URI url = getURI(getURIBuilder().setPath(contentPackUriString));
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new RuntimeException(String.format("Content pack '%s' does not exist: %s", contentPackNamespace, e.getMessage()));
            }
            throw new RuntimeException(String.format("Error fetching content pack data for content pack '%s': %s", contentPackNamespace, e.getMessage()));
        }

        // reformat the output JSON to be readable
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(String.format("Error verifying data of content pack '%s': %s", contentPackNamespace, e.getMessage()));
        } catch (JsonIOException e) {
            throw new RuntimeException(String.format("Error reading and verifying data of content pack '%s': %s", contentPackNamespace, e.getMessage()));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().serializeNulls().create();

        return gson.toJson(jsonObject);
    }

    protected void importContentPackPrimitive(String contentPackName, String contentPackJson) {
        URI url = getURI(getURIBuilder().setPath(CONTENT_PACKS_API));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(contentPackJson, headers);
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.CONFLICT.equals(e.getStatusCode())) {
                logger.warn("The content pack '{}' already exists on the target system, please uninstall it before importing again.", contentPackName);
                return;
            }
            if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
                throw new RuntimeException(String.format("Data validation error of the content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
            }
            throw new RuntimeException(String.format("Error importing content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("REST client error during import of content pack '%s' to VRLI: %s", contentPackName, e.getMessage()));
        }
    }

    protected List<AlertDTO> getAllAlertsPrimitive() {
        URI url = getURI(getURIBuilder().setPath(ALERTS_API));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        return deserializeAlerts(response.getBody());
    }

    protected List<ContentPackDTO> getAllContentPacksPrimitive() {
        String version = this.getVersion();
        URI url;
        if (!StringUtils.isEmpty(version) && Version.compareSemanticVersions(version, VRLI_88_VERSION) > -1) {
            url = getURI(getURIBuilder().setPath(CONTENT_PACKS_API));
        } else {
            url = getURI(getURIBuilder().setPath(CONTENT_PACKS_LIST_API));
        }
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        return deserializeContentPacks(response.getBody());
    }

    protected void updateAlert(AlertDTO alertToUpdate, String existingAlertId) {
        if (alertToUpdate == null || StringUtils.isEmpty(existingAlertId)) {
            return;
        }

        logger.info("Updating alert '{}'", alertToUpdate.getName());
        deleteAlert(existingAlertId);
        insertAlert(serializeAlert(alertToUpdate));
    }

    protected void importAlertPrimitive(String alertJson) {
        if (StringUtils.isEmpty(alertJson)) {
            return;
        }

        AlertDTO alertToUpdate = deserializeAlert(alertJson);
        if (alertToUpdate == null) {
            return;
        }
        AlertDTO existingAlert = findAlertByName(alertToUpdate.getName());
        if (existingAlert != null) {
            updateAlert(alertToUpdate, existingAlert.getId());
            return;
        }

        insertAlert(alertJson);
    }

    private void deleteAlert(String alertId) {
        if (StringUtils.isEmpty(alertId)) {
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        String deleteAlertUri = String.format(ALERTS_API + "/%s", alertId);
        logger.info("Deleting existing alert {}", alertId);
        try {
            URI url = getURIBuilder().setPath(deleteAlertUri).build();
            response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Unable to delete alert, error: %s", e.getMessage()), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Unable to determine REST endpoint for deleting alert, error: %s", e.getMessage()), e);
        }
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            return;
        }
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Unable to delete alert, Remote REST service returned status code %s, with message %s",
                    response.getStatusCode(), response.getBody()));
        }
    }

    private void insertAlert(String alertJson) {
        if (StringUtils.isEmpty(alertJson)) {
            return;
        }

        AlertDTO alert = deserializeAlert(alertJson);
        // rewrite the vcops integration data if needed
        rewriteVcopsIntegrationInfo(alert);
        alertJson = serializeAlert(alert);

        logger.info("Inserting a new alert '{}'", alert.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(alertJson, headers);
        ResponseEntity<String> response;
        try {
            URI url = getURIBuilder().setPath(ALERTS_API).build();
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Unable to insert/update alert, error: %s", e.getMessage()), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Unable to determine REST endpoint for alert, error: %s", e.getMessage()), e);
        }
        if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Unable to insert/update alert, Remote REST service returned status code %s, with message %s",
                    response.getStatusCode(), response.getBody()));
        }
    }

    private void rewriteVcopsIntegrationInfo(AlertDTO alert) {
        if (alert == null) {
            return;
        }
        if (!alert.isVcopsEnabled()) {
            return;
        }
        if (StringUtils.isEmpty(alert.getVcopsResourceName()) || StringUtils.isEmpty(alert.getVcopsResourceKindKey())) {
            return;
        }

        RestClientVrops restClientVrops = getVropsRestClient();
        ResourcesDTO resourceDto = new ResourcesDTO();
        try {
            resourceDto = restClientVrops.getResources();
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Unable to update vCOPs integration for alert '%s', unable to fetch vROPs resources: %s", alert.getName(), e.getMessage()));
        }
        if (resourceDto == null) {
            return;
        }

        Optional<ResourcesDTO.ResourceList> vrliResource = resourceDto.getResourceList().stream()
                .filter(item -> item.getResourceKey().getResourceKindKey().equalsIgnoreCase(VRLI_RESOURCE_KEY_TYPE)).findFirst();
        if (!vrliResource.isPresent()) {
            throw new RuntimeException(String.format(
                    "Unable to find resource type '%s' on the target vROPs server for alert: '%s', please check VROPS content pack configuration in VRLI",
                    VRLI_RESOURCE_KEY_TYPE, alert.getName()));
        }

        String resourceName = vrliResource.get().getResourceKey().getName();
        String adapterKindKey = vrliResource.get().getResourceKey().getAdapterKindKey();
        String resourceKindKey = vrliResource.get().getResourceKey().getResourceKindKey();

        List<String> baseResourceKindKeys = new ArrayList<>();
        baseResourceKindKeys.add("resourceName=" + resourceName);
        baseResourceKindKeys.add("adapterKindKey=" + adapterKindKey);
        baseResourceKindKeys.add("resourceKindKey=" + resourceKindKey);

        // create resource identifiers list
        List<String> identifiersList = vrliResource.get().getResourceKey().getResourceIdentifiers().stream().map(identifier -> {
            String name = identifier.getIdentifierType().getName();
            String value = identifier.getValue();

            return name + "::" + value;
        }).collect(Collectors.toList());

        if (!identifiersList.isEmpty()) {
            String identifiers = identifiersList.stream().collect(Collectors.joining("$$"));
            baseResourceKindKeys.add("identifiers=" + identifiers);
        }

        logger.info("Rewriting the vCopsResourceKindKey for alert '{}', using resource name '{}'", alert.getName(), resourceName);
        alert.setVcopsResourceName(resourceName);
        alert.setVcopsResourceKindKey(baseResourceKindKeys.stream().collect(Collectors.joining("&")));
    }

    private RestClientVrops getVropsRestClient() {
        if (this.vropsRestClient != null) {
            return this.vropsRestClient;
        }

        ConfigurationVrops vropsConfiguration;
        Properties vropsConfigProperties = new Properties();
        vropsConfigProperties.put(Configuration.HOST, configuration.getIntegrationVropsAuthHost());
        vropsConfigProperties.put(Configuration.PORT, configuration.getIntegrationVropsAuthPort());
        vropsConfigProperties.put(Configuration.USERNAME, configuration.getIntegrationVropsAuthUser());
        vropsConfigProperties.put(Configuration.PASSWORD, configuration.getIntegrationVropsAuthPassword());
        vropsConfigProperties.put(ConfigurationVrli.INTEGRATION_VROPS_AUTH_SOURCE, configuration.getIntegrationVropsAuthSource());
        vropsConfigProperties.put(ConfigurationVrops.VROPS_DASHBOARD_USER, "admin");
        vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_USER, configuration.getIntegrationVropsAuthUser());
        vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_PASSWORD, configuration.getIntegrationVropsAuthPassword());
        vropsConfigProperties.put(ConfigurationVrops.VROPS_REST_AUTH_SOURCE, configuration.getIntegrationVropsAuthSource());
        vropsConfigProperties.put(ConfigurationVrops.SSH_PORT, "22");

        try {
            vropsConfiguration = ConfigurationVrops.fromProperties(vropsConfigProperties);
        } catch (ConfigurationException e) {
            throw new RuntimeException(
                    String.format("Unable to update vCOPs integration for alert, vROPs integration configuration failed with message: %s", e.getMessage()));
        }

        return new RestClientVrops(vropsConfiguration, restTemplate);
    }

    private AlertDTO findAlertByName(String alertName) {
        List<AlertDTO> alerts = getAllAlertsPrimitive();
        if (alerts == null || alerts.isEmpty()) {
            return null;
        }
        List<AlertDTO> foundAlerts = alerts.stream().filter(alert -> alert.getName().equalsIgnoreCase(alertName)).distinct().collect(Collectors.toList());
        if (foundAlerts == null || foundAlerts.isEmpty()) {
            return null;
        }
        Optional<AlertDTO> foundAlert = foundAlerts.stream().findFirst();
        if (foundAlert.isPresent()) {
            return foundAlert.get();
        }

        return null;
    }

    private AlertDTO deserializeAlert(String alertJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(alertJson, AlertDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(PROCESS_ALERT_DATA_ERROR, e);
        }
    }

    private List<AlertDTO> deserializeAlerts(String alertsJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(alertsJson, AlertDTO[].class));
        } catch (JsonMappingException e) {
            throw new RuntimeException(MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(PROCESS_ALERT_DATA_ERROR, e);
        }
    }

    private List<ContentPackDTO> deserializeContentPacks(String contentPacksJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ContentPackMetadataListDTO contentPackMetadata = mapper.readValue(contentPacksJson, ContentPackMetadataListDTO.class);
            return contentPackMetadata.getContentPackMetadataList();
        } catch (JsonMappingException e) {
            throw new RuntimeException(MAP_CONTENT_PACK_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(PROCESS_CONTENT_PACK_DATA_ERROR, e);
        }
    }

    private String serializeAlert(AlertDTO alert) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(alert);
        } catch (JsonMappingException e) {
            throw new RuntimeException(MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(PROCESS_ALERT_DATA_ERROR, e);
        }
    }
}
