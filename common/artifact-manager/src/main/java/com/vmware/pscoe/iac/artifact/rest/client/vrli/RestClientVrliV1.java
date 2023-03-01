package com.vmware.pscoe.iac.artifact.rest.client.vrli;

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
import java.util.stream.Collectors;

import com.vmware.pscoe.iac.artifact.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.rest.client.messages.Errors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.ContentPackDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v1.ContentPackMetadataListDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ResourcesDTO;

public class RestClientVrliV1 extends AbstractRestClientVrli {
    private static final String API_PREFIX = "/api/v1";

    public RestClientVrliV1(ConfigurationVrli configuration, RestTemplate restTemplate) {
        super(API_PREFIX, configuration, restTemplate);
        logger = LoggerFactory.getLogger(RestClientVrliV1.class);
    }

    public List<AlertDTO> getAllAlerts() {
        URI url = getURI(getURIBuilder().setPath(this.apiPrefix + ALERTS_API));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        return deserializeAlerts(response.getBody());
    }

    public List<ContentPackDTO> getAllContentPacks() {
        URI url = getURI(getURIBuilder().setPath(this.apiPrefix + CONTENT_PACKS_LIST_API));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        return deserializeContentPacks(response.getBody());
    }

    public void updateAlert(AlertDTO alertToUpdate, String existingAlertId) {
        if (alertToUpdate == null || StringUtils.isEmpty(existingAlertId)) {
            return;
        }

        logger.info("Updating alert '{}'", alertToUpdate.getName());
        deleteAlert(existingAlertId);
        insertAlert(serializeAlert(alertToUpdate));
    }

    public void importAlert(String alertJson) {
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

    public void deleteAlert(String alertId) {
        if (StringUtils.isEmpty(alertId)) {
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        String deleteAlertUri = String.format(this.apiPrefix + ALERTS_API + "/%s", alertId);
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

    public void insertAlert(String alertJson) {
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
            URI url = getURIBuilder().setPath(this.apiPrefix + ALERTS_API).build();
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

    private AlertDTO findAlertByName(String alertName) {
        List<AlertDTO> alerts = getAllAlerts();
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
            throw new RuntimeException(Errors.MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(Errors.PROCESS_ALERT_DATA_ERROR, e);
        }
    }

    private List<AlertDTO> deserializeAlerts(String alertsJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(alertsJson, AlertDTO[].class));
        } catch (JsonMappingException e) {
            throw new RuntimeException(Errors.MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(Errors.PROCESS_ALERT_DATA_ERROR, e);
        }
    }

    private List<ContentPackDTO> deserializeContentPacks(String contentPacksJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ContentPackMetadataListDTO contentPackMetadata = mapper.readValue(contentPacksJson, ContentPackMetadataListDTO.class);
            return contentPackMetadata.getContentPackMetadataList();
        } catch (JsonMappingException e) {
            throw new RuntimeException(Errors.MAP_CONTENT_PACK_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(Errors.PROCESS_CONTENT_PACK_DATA_ERROR, e);
        }
    }

    private String serializeAlert(AlertDTO alert) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(alert);
        } catch (JsonMappingException e) {
            throw new RuntimeException(Errors.MAP_ALERT_DATA_ERROR, e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(Errors.PROCESS_ALERT_DATA_ERROR, e);
        }
    }
}