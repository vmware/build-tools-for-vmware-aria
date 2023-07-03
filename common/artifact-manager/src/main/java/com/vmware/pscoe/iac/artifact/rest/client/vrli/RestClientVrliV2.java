package com.vmware.pscoe.iac.artifact.rest.client.vrli;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrli;
import com.vmware.pscoe.iac.artifact.rest.RestClientVrops;
import com.vmware.pscoe.iac.artifact.rest.client.messages.Errors;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v2.AlertDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v2.ContentPackDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrli.v2.ContentPackMetadataListDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ResourcesDTO;

public class RestClientVrliV2 extends AbstractRestClientVrli {
	/**
	 * API_PREFIX.
	 */
	private static final String API_PREFIX = "/api/v2";
	/**
	 * RESOURCE_KIND_KEY.
	 */
	private static final String RESOURCE_KIND_KEY = "resourceKindKey";
	/**
	 * ADAPTER_KIND_KEY.
	 */
	private static final String ADAPTER_KIND_KEY = "adapterKindKey";
	/**
	 * KEYS_SPLIT_KEY.
	 */
	private static final String KEYS_SPLIT_KEY = "&";
	/**
	 * KEY_SPLIT_KEY.
	 */
	private static final String KEY_SPLIT_KEY = "=";

	/**
	 * RestClientVrliV2.
	 * 
	 * @param configuration
	 * @param restTemplate
	 */
	public RestClientVrliV2(ConfigurationVrli configuration, RestTemplate restTemplate) {
		super(API_PREFIX, configuration, restTemplate);
		logger = LoggerFactory.getLogger(RestClientVrliV2.class);
	}

	/**
	 * Returns all alerts in vRLI.
	 * 
	 * @return list of AlertDTO
	 */
	public List<AlertDTO> getAllAlerts() {
		URI url = getURI(getURIBuilder().setPath(this.apiPrefix + ALERTS_API));
		logger.info("Getting all alerts URL: {}", url);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

		return deserializeAlerts(response.getBody());
	}

	/**
	 * Returns all content packs in vRLI.
	 * 
	 * @return list of ContentPackDTO
	 */
	public List<ContentPackDTO> getAllContentPacks() {
		URI url = getURI(getURIBuilder().setPath(this.apiPrefix + CONTENT_PACKS_API));
		logger.info("Getting all content packs URL: {}", url);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

		logger.debug("Content Packs: {}", response.getBody());
		return deserializeContentPacks(response.getBody());
	}

	/**
	 * Update vRLI alert.
	 * 
	 * @param alertToUpdate
	 * @param existingAlertId
	 */
	public void updateAlert(final AlertDTO alertToUpdate, final String existingAlertId) {
		if (alertToUpdate == null || StringUtils.isEmpty(existingAlertId)) {
			return;
		}

		logger.info("Updating alert '{}'", alertToUpdate.getName());
		deleteAlert(existingAlertId);
		insertAlert(serializeAlert(alertToUpdate));
	}

	/**
	 * Import a vRLI alert.
	 * 
	 * @param alertJson
	 */
	public void importAlert(final String alertJson) {
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

	/**
	 * Delete a vRLI alert.
	 * 
	 * @param alertId
	 */	
	public void deleteAlert(final String alertId) {
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
			logger.info("Deleting alert URL: {}", url);
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

	/**
	 * Insert a vRLI alert.
	 * 
	 * @param alertJson
	 */	
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
			logger.info("Inserting alert URL: {}", url);
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

	/**
	 * Rewrite the vROPs integration data for vROPs enabled vRLI alerts so that the
	 * target vROPs integration matches the one defined by vROPs during push. The
	 * vROPs integration data is retrieved dynamically from vROPs based on the
	 * configured vROPs integration in the settings.xml (<vrli.vrops*> settings)
	 *
	 * @param alert DTO object
	 * @throws Runtime exception
	 */
	private void rewriteVcopsIntegrationInfo(final AlertDTO alert) {
		if (alert == null) {
			return;
		}
		if (alert.getRecipients().getVrops().getVcopsResourceKindKey() == null) {
			return;
		}
		// fetch the adapter type for alert
		String alertAdapterType = this.getAdapterTypeForAlert(alert);
		// fetch the resource type for alert
		String alertResourceType = this.getResourceTypeForAlert(alert);
		// retrieve the resources based on the target adapter type (vROPs adapter kind)
		RestClientVrops restClientVrops = getVropsRestClient();
		ResourcesDTO resourceDto;
		try {
			resourceDto = restClientVrops.getResourcesPerAdapterType(alertAdapterType);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format("Unable to update vCOPs integration for alert '%s', unable to fetch vROPs resources for adapter type '%s': %s",
							alert.getName(), alertAdapterType, e.getMessage()));
		}
		Optional<ResourcesDTO.ResourceList> targetResource = resourceDto.getResourceList().stream()
				.filter(item -> item.getResourceKey().getResourceKindKey().equalsIgnoreCase(alertResourceType)).findFirst();
		if (!targetResource.isPresent()) {
			throw new RuntimeException(String.format(
					"Unable to find resource type '%s' on the target vROPs server for alert: '%s', please check VROPS content pack configuration in VRLI or VROPS configuration",
					alertResourceType, alert.getName()));
		}
		String resourceName = targetResource.get().getResourceKey().getName();
		String adapterKindKey = targetResource.get().getResourceKey().getAdapterKindKey();
		String resourceKindKey = targetResource.get().getResourceKey().getResourceKindKey();
		List<String> baseResourceKindKeys = new ArrayList<>();
		baseResourceKindKeys.add("resourceName=" + resourceName);
		baseResourceKindKeys.add("adapterKindKey=" + adapterKindKey);
		baseResourceKindKeys.add("resourceKindKey=" + resourceKindKey);
		alert.getRecipients().getVrops().setVcopsResourceName(resourceName);

		// create resource identifiers list
		List<String> identifiersList = targetResource.get().getResourceKey().getResourceIdentifiers().stream().map(identifier -> {
			String name = identifier.getIdentifierType().getName();
			String value = identifier.getValue();
			return name + "::" + value;
		}).collect(Collectors.toList());

		if (!identifiersList.isEmpty()) {
			String identifiers = identifiersList.stream().collect(Collectors.joining("$$"));
			baseResourceKindKeys.add("identifiers=" + identifiers);
		}
		logger.info("Rewriting the vCopsResourceKindKey for alert '{}', using resource name '{}'", alert.getName(), resourceName);
		alert.getRecipients().getVrops().setVcopsResourceName(resourceName);
		alert.getRecipients().getVrops().setVcopsResourceKindKey(baseResourceKindKeys.stream().collect(Collectors.joining("&")));
	}

	/**
	 * Return the vROPs adapter type for an alert.
	 * 
	 * @param alert
	 * @return adapterType
	 */	
	private String getAdapterTypeForAlert(final AlertDTO alert) {
		String targetResourceKindKeys = alert.getRecipients().getVrops().getVcopsResourceKindKey();
		List<String> keysSplit = Arrays.asList(targetResourceKindKeys.split(KEYS_SPLIT_KEY));
		Optional<String> adapterTypeKeys = keysSplit.stream().filter(item -> item.contains(ADAPTER_KIND_KEY)).findFirst();

		if (!adapterTypeKeys.isPresent()) {
			throw new RuntimeException(String.format("Unable to find adapter type '%s' for vROPs enabled alert: '%s' on the target vROPs system",
					ADAPTER_KIND_KEY, alert.getName()));
		}
		List<String> adapterTypes = Arrays.asList(adapterTypeKeys.get().split(KEY_SPLIT_KEY));
		if (adapterTypes.isEmpty()) {
			throw new RuntimeException(String.format("Unable to to extract vROPs adapter kind type for vROPs enabled alert '%s'", alert.getName()));
		}

		return adapterTypes.get(adapterTypes.size() - 1);
	}

	/**
	 * Return the vROPs resource type for an alert.
	 * 
	 * @param alert
	 * @return resource type
	 */	
	private String getResourceTypeForAlert(final AlertDTO alert) {
		String targetResourceKindKeys = alert.getRecipients().getVrops().getVcopsResourceKindKey();
		List<String> keysSplit = Arrays.asList(targetResourceKindKeys.split(KEYS_SPLIT_KEY));
		Optional<String> resourceTypes = keysSplit.stream().filter(item -> item.contains(RESOURCE_KIND_KEY)).findFirst();
		if (!resourceTypes.isPresent()) {
			throw new RuntimeException(String.format("Unable to find resource type '%s' for vROPs enabled alert: '%s' on the target vROPs system",
					RESOURCE_KIND_KEY, alert.getName()));
		}
		List<String> types = Arrays.asList(resourceTypes.get().split(KEY_SPLIT_KEY));
		if (types.isEmpty()) {
			throw new RuntimeException(String.format("Unable to to extract vROPs resource kind type for vROPs enabled alert '%s'", alert.getName()));
		}

		return types.get(types.size() - 1);
	}

	/**
	 * Finds alert by name.
	 * 
	 * @param alertName
	 * @return AlertDTO
	 */	
	private AlertDTO findAlertByName(final String alertName) {
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

	/**
	 * Deserialize alert.
	 * 
	 * @param alertJson
	 * @return AlertDTO
	 */	
	private AlertDTO deserializeAlert(final String alertJson) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(alertJson, AlertDTO.class);
		} catch (JsonMappingException e) {
			throw new RuntimeException(Errors.MAP_ALERT_DATA_ERROR, e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(Errors.PROCESS_ALERT_DATA_ERROR, e);
		}
	}

	/**
	 * Deserialize alerts.
	 * 
	 * @param alertsJson
	 * @return list of AlertDTO
	 */	
	private List<AlertDTO> deserializeAlerts(final String alertsJson) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Arrays.asList(mapper.readValue(alertsJson, AlertDTO[].class));
		} catch (JsonMappingException e) {
			throw new RuntimeException(Errors.MAP_ALERT_DATA_ERROR, e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(Errors.PROCESS_ALERT_DATA_ERROR, e);
		}
	}

	/**
	 * Deserialize content packs.
	 * 
	 * @param contentPacksJson
	 * @return list of ContentPackDTO
	 */	
	private List<ContentPackDTO> deserializeContentPacks(final String contentPacksJson) {
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

	/**
	 * Serialize alert.
	 * 
	 * @param alert
	 * @return JSON string
	 */	
	private String serializeAlert(final AlertDTO alert) {
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
