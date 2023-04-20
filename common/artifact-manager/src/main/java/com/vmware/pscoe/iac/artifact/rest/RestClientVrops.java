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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.vrops.VropsPackageMemberType;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AdapterKindDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AlertDefinitionDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AlertDefinitionDTO.AlertDefinition.State;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AlertDefinitionDTO.AlertDefinition.SymptomSet;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AuthGroupDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AuthGroupsDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AuthUsersDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.AuthUserDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.CustomGroupDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.CustomGroupTypeDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.PolicyDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.RecommendationDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ReportDefinitionDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ResourceKindDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ResourcesDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.SupermetricDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.SymptomDefinitionDTO;
import com.vmware.pscoe.iac.artifact.rest.model.vrops.ViewDefinitionDTO;

@SuppressWarnings("deprecation")
public class RestClientVrops extends RestClient {
    private final Logger logger = LoggerFactory.getLogger(RestClientVrops.class);

    private static final String PUBLIC_API_PREFIX = "/suite-api/api/";
    private static final String INTERNAL_API_PREFIX = "/suite-api/internal/";
    private static final String ALERT_DEFS_API = PUBLIC_API_PREFIX + "alertdefinitions/";
    private static final String SYMPTOM_DEFS_API = PUBLIC_API_PREFIX + "symptomdefinitions/";
    private static final String POLICIES_API = INTERNAL_API_PREFIX + "policies/";
    private static final String RECOMMENDATIONS_API = PUBLIC_API_PREFIX + "recommendations/";
    private static final String RESOURCES_API = PUBLIC_API_PREFIX + "resources/";
    private static final String CUSTOM_GROUPS_FETCH_API = RESOURCES_API + "groups";
    private static final String CUSTOM_GROUPS_UPDATE_API = PUBLIC_API_PREFIX + "resources/groups";
    private static final String CUSTOM_GROUP_TYPES_API = PUBLIC_API_PREFIX + "resources/groups/types";
    private static final String RESOURCES_LIST_API = PUBLIC_API_PREFIX + "resources";
    private static final String ADAPTER_KINDS_API = PUBLIC_API_PREFIX + "adapterkinds";
    private static final String RESOURCE_KINDS_API = PUBLIC_API_PREFIX + "adapterkinds/%s/resourcekinds";
    private static final String SUPERMETRICS_LIST_API = PUBLIC_API_PREFIX + "supermetrics";
    private static final String REPORT_DEFINITIONS_LIST_API = PUBLIC_API_PREFIX + "reportdefinitions";
    private static final String VIEW_DEFINITIONS_LIST_API = INTERNAL_API_PREFIX + "viewdefinitions";
    private static final String INTERNAL_API_HEADER_NAME = "X-vRealizeOps-API-use-unsupported";
    private static final String AUTH_GROUPS_API = PUBLIC_API_PREFIX + "/auth/usergroups";
    private static final String AUTH_USERS_API = PUBLIC_API_PREFIX + "/auth/users";
    private static final int DEFAULT_PAGE_SIZE = 10000;
    private static final int VROPS_MAJOR_VERSION = 8;
    private static final int VROPS_MINOR_VERSION = 2;
    private static final String VROPS_KIND_ALL = "ALL";

    private ConfigurationVrops configuration;
    private RestTemplate restTemplate;
    private ObjectMapper mapper = new ObjectMapper();
    private Version productVersion = null;

    public RestClientVrops(ConfigurationVrops configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    protected Configuration getConfiguration() {
        return configuration;
    }

    @Override
    protected URIBuilder getURIBuilder() {
        ConfigurationVrops config = (ConfigurationVrops) getConfiguration();
        return new URIBuilder().setScheme("https").setHost(config.getHost()).setPort(config.getHttpPort());
    }

    @Override
    public String getVersion() {
        URI url = getURI(getURIBuilder().setPath(PUBLIC_API_PREFIX + "versions/current"));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        String versionString = JsonPath.parse(response.getBody()).read("$.releaseName");
        Integer buildNumber = JsonPath.parse(response.getBody()).read("buildNumber");

        Pattern versionNumberRegex = Pattern.compile("([\\d]+)\\.([\\d]+)\\.([\\d]+)");
        Matcher matcher = versionNumberRegex.matcher(versionString);

        StringBuilder retVal = new StringBuilder("");
        if (matcher.find()) {
            retVal.append(matcher.group(0));
            retVal.append("." + buildNumber);
        }

        return retVal.toString();
    }

    /**
     * Import policies from a zip file
     * 
     * @param file             policy zip file as byte[]
     * @param policyName List of strings that represent the custom groups
     *                         policy will be assigned to.
     * @param force      true to overwrite the existing policies, false to
     *                         skip importing when there is a conflict
	 * @throws Exception exception
     */
    public void importPolicyFromZip(String policyName, File file, Boolean force) throws Exception {
        URI uri;
        try {
            uri = getURI(getURIBuilder().setPath(POLICIES_API + "import"));
        } catch (RuntimeException e) {
            throw e;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // Required header for internal API
        headers.set(INTERNAL_API_HEADER_NAME, Boolean.TRUE.toString());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("policy", new FileSystemResource(file));
        body.add("forceImport", force);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(uri, requestEntity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("The policy '%s' could not be imported : %s.", policyName, e.getMessage()), e);
        }
    }

    /**
     * Export a zip file per policies, filtered by name
     * 
     * @param policyEntries Names of the policies to be exported
     * @return a list of policies containing a zip file as byte[], name and id of
     *         the policy
     */
    public List<PolicyDTO.Policy> exportPoliciesFromVrops(List<String> policyEntries) {
        List<PolicyDTO.Policy> policies = filterPoliciesByName(policyEntries);
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        for (PolicyDTO.Policy policy : policies) {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(getURI(getURIBuilder().setPath(POLICIES_API + "export")));
            uriBuilder.queryParam("id", policy.getId());

            HttpHeaders exportHeader = new HttpHeaders();
            exportHeader.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            // internal header is needed for vROPs internal API
            exportHeader.set(INTERNAL_API_HEADER_NAME, Boolean.TRUE.toString());
            HttpEntity<String> exportEntity = new HttpEntity<>(exportHeader);
            ResponseEntity<byte[]> exportResponse = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, exportEntity, byte[].class);

            policy.setZipFile(exportResponse.getBody());
        }

        return policies;
    }

    public List<PolicyDTO.Policy> getAllPolicies() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // Required header for internal API
        headers.set(INTERNAL_API_HEADER_NAME, Boolean.TRUE.toString());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(POLICIES_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return new ArrayList<>();
            }
            throw new RuntimeException(String.format("Error ocurred trying to fetching policies. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error building REST URI to fetch policies. Message: %s", e.getMessage()));
        }
        PolicyDTO policyDto = deserializePolicies(response.getBody());

        return policyDto == null ? Collections.emptyList() : policyDto.getPolicies();
    }

    public PolicyDTO.Policy getPolicyContent(PolicyDTO.Policy policy) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(getURI(getURIBuilder().setPath(POLICIES_API + "export")));
        uriBuilder.queryParam("id", policy.getId());

        HttpHeaders exportHeader = new HttpHeaders();
        if (isVrops82OrLater()) {
            // The API vROPs 8.2 or later expects accept to be MediaType.ALL
            exportHeader.setAccept(Arrays.asList(MediaType.ALL));
        } else {
            exportHeader.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        }
        // internal header is needed for the internal API of vROPs
        exportHeader.set(INTERNAL_API_HEADER_NAME, Boolean.TRUE.toString());

        HttpEntity<String> exportEntity = new HttpEntity<>(exportHeader);
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, exportEntity, byte[].class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error exporting all policy %s from vROPS : %s", policy.getName(), e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(
                    String.format("Error exporting all policy %s from vROPS : remote REST service returned: %s", policy.getName(), response.getStatusCode()));
        }
        policy.setZipFile(response.getBody());

        return policy;
    }

    /**
     * Export custom groups filtered by list of custom group names.
     * 
     * @param customGroupNames Names of the custom groups to be exported
     * @return a list of custom groups
     */
    public List<CustomGroupDTO.Group> exportCustomGroupsFromVrops(List<String> customGroupNames) {
        return findCustomGroupsByNames(customGroupNames);
    }

    /**
     * Export all available custom groups from vROPs.
     * 
     * @return a list of custom groups
     */
    public List<CustomGroupDTO.Group> getAllCustomGroups() {
        return findCustomGroupsByNames(null);
    }

    /**
     * Export definition of given type from vROPS server and return it as string.
     * 
     * @param definitionName - the name of the definition that has to be exported.
     * @param definitionType - the VropsPackageMemberType type of the definition
     *                       (currently ALERT_DEFINITION, SYMPTOM_DEFINITION and
     *                       RECOMMENDATION are supported only).
     * @return JSON String containing the definition data.
     */
    public String exportDefinitionFromVrops(String definitionName, VropsPackageMemberType definitionType) {
        String definitionId = getDefinitionIdByName(definitionName, definitionType);
        if (StringUtils.isEmpty(definitionId)) {
            throw new RuntimeException(String.format("Error exporting %s of type %s from vROPS , unable to find definition", definitionName, definitionType));
        }
        URI restUri = getDefinitionUri(definitionType, definitionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error exporting %s of type %s from vROPS : %s", definitionId, definitionType, e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Error exporting %s of type %s from vROPS : remote REST service returned: %s", definitionType,
                    definitionId, response.getStatusCode()));
        }

        return response.getBody();
    }

    /**
     * Export all definitions of given type from vROPs.
     * 
     * @param definitionType - the VropsPackageMemberType type of the definition
     *                       (currently ALERT_DEFINITION, SYMPTOM_DEFINITION and
     *                       RECOMMENDATION are supported only).
     * @return Object - the object that is instance of AlertDefinitionDTO or
     *         SymptomDefinitionsDTO or RecommendationsDTO.
     */
    public Object exportDefinitionsFromVrops(VropsPackageMemberType definitionType) {
        URI restUri = getDefinitionUri(definitionType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error exporting all definitions of type %s from vROPS : %s", definitionType, e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Error exporting all definitions of type %s from vROPS : remote REST service returned: %s", definitionType,
                    response.getStatusCode()));
        }

        return deserializeDefinitions(definitionType, response.getBody());
    }

    /**
     * Import definitions to the vROPS based on their type.
     * 
     * @param definitionType - the VropsPackageMemberType type of the definition
     *                       (currently ALERT_DEFINITION, SYMPTOM_DEFINITION and
     *                       RECOMMENDATION are supported only).
	 * @param definitions - definitions
	 * @param dependentDefinitionsMap - dependentDefinitionsMap
     */
    public void importDefinitionsInVrops(Map<String, Object> definitions, VropsPackageMemberType definitionType, Map<String, Object> dependentDefinitionsMap) {
        if (definitions.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> definitionEntry : definitions.entrySet()) {
            if (definitionEntry.getValue() == null) {
                continue;
            }
            String definitionName = getDefinitionName(definitionEntry.getValue(), definitionType);
            logger.info("Importing {} '{}' to vROPs", definitionType, definitionName);
            importDefinitionToVrops(definitionEntry.getValue(), definitionType, dependentDefinitionsMap);
        }
    }

    /**
     * Import custom group in vROPs.
     * 
     * @param customGroupName    - the custom group name.
     * @param customGroupPayload - the payload of the custom group as json.
	 * @param policyIdMap - the policy mappings.
     */
    public void importCustomGroupInVrops(String customGroupName, String customGroupPayload, Map<String, String> policyIdMap) {
        if (StringUtils.isEmpty(customGroupPayload)) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = customGroupExists(customGroupPayload) ? HttpMethod.PUT : HttpMethod.POST;
        CustomGroupDTO.Group customGroup = serializeCustomGroup(customGroupPayload);

        // vROPs requires the group type to exists prior to creating
        createMissingGroupTypes(customGroup);

        // vROPs requires the group id to be set to null prior creating it
        if (HttpMethod.POST.equals(method)) {
            customGroupPayload = setCustomGroupIdToNull(customGroupPayload);
        } else {
            customGroupPayload = updateCustomGroupId(serializeCustomGroup(customGroupPayload), customGroupPayload);
        }

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(customGroupPayload, headers);
        ResponseEntity<String> responseEntity;
        try {
            URI restUri = new URI(getURIBuilder().setPath(CUSTOM_GROUPS_UPDATE_API).toString());
            responseEntity = restTemplate.exchange(restUri, method, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Unable to import custom group %s to vROPS : %s", customGroupName, e.getMessage()), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Unable to determine vROPs REST endpoint for custom group %s : %s", customGroupName, e.getMessage()), e);
        }

        HttpStatus status = responseEntity.getStatusCode();
        if (HttpMethod.POST.equals(method)) {
            if (HttpStatus.BAD_REQUEST.equals(status)) {
                throw new RuntimeException(String.format("Error creating custom group %s: Validation error in the group data", customGroupName));
            }
            if (!HttpStatus.CREATED.equals(status)) {
                throw new RuntimeException(
                        String.format("Error creating custom group %s: Remote REST service returned status code %s", customGroupName, status));
            }
        }
        if (HttpMethod.PUT.equals(method)) {
            if (HttpStatus.BAD_REQUEST.equals(status)) {
                throw new RuntimeException(String.format("Error updating custom group %s: Validation error in the group data", customGroupName));
            }
            if (!HttpStatus.OK.equals(status)) {
                throw new RuntimeException(
                        String.format("Error creating custom group %s: Remote REST service returned status code %s", customGroupName, status));
            }
        }

        // Update policy for the custom group (if any)
        // Note: due to bug in the API of vROPs the updating for the policy of
        // a custom group should be done via separate call to the vROPs public API
        updateCustomGroupPolicy(customGroupPayload, policyIdMap);
    }

    public void createMissingGroupTypes(CustomGroupDTO.Group customGroup) {
        String customGroupResourceKind = customGroup.getResourceKey().getResourceKindKey();
        if (!this.resourceKindExists(customGroupResourceKind, customGroup.getResourceKey().getAdapterKindKey())) {
            this.createCustomGroupType(customGroupResourceKind);
        }

        customGroup.getMembershipDefinition().getRules().forEach(rule -> {
            String resourceKindKey = rule.getResourceKindKey().getResourceKind();
            String adapterKindKey = rule.getResourceKindKey().getAdapterKind();
            if (!resourceKindExists(resourceKindKey, adapterKindKey)) {
                this.createCustomGroupType(resourceKindKey);
            }
        });
    }

    public void createCustomGroupType(String customGroupType) {
        logger.info(String.format("Custom group type doesn't exist. Creating custom group type '%s'", customGroupType));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        CustomGroupTypeDTO customGroupTypeDto = new CustomGroupTypeDTO();
        customGroupTypeDto.setName(customGroupType);
        String customGroupTypePayload = deserializeCustomGroupType(customGroupTypeDto);
        HttpEntity<String> entity = new HttpEntity<>(customGroupTypePayload, headers);
        ResponseEntity<String> responseEntity;
        try {
            URI restUri = new URI(getURIBuilder().setPath(CUSTOM_GROUP_TYPES_API).toString());
            responseEntity = restTemplate.exchange(restUri, HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Unable to create custom group type %s to vROPS : %s", customGroupType, e.getMessage()), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Unable to determine vROPs REST endpoint for custom group type %s : %s", customGroupType, e.getMessage()), e);
        }

        HttpStatus status = responseEntity.getStatusCode();
        if (HttpStatus.BAD_REQUEST.equals(status)) {
            throw new RuntimeException(String.format("Error creating custom group type %s: Validation error in the group data", customGroupType));
        }
        if (!HttpStatus.CREATED.equals(status)) {
            throw new RuntimeException(
                    String.format("Error creating custom group type %s: Remote REST service returned status code %s", customGroupType, status));
        }
    }

    public ResourcesDTO getResources() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(RESOURCES_LIST_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return new ResourcesDTO();
            }
            throw new RuntimeException(String.format("Error occurred when trying to fetch resources. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error occurred when trying to build REST URI to fetch resources. Message: %s", e.getMessage()));
        }
        try {
            return mapper.readValue(response.getBody(), ResourcesDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the resources response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error while parsing the resources response. Message: %s", e.getMessage()));
        }
    }

    public SupermetricDTO getAllSupermetrics() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        SupermetricDTO retVal = new SupermetricDTO();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(SUPERMETRICS_LIST_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return retVal;
            }
            throw new RuntimeException(String.format("Error occurred when trying to fetch supermetrics list. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error occurred when trying to build REST URI to fetch supermetrics. Message: %s", e.getMessage()));
        }
        try {
            retVal = mapper.readValue(response.getBody(), SupermetricDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the supermetrics list response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    String.format("JSON processing error while parsing the supermetrics list resources response. Message: %s", e.getMessage()));
        }
        // Unescape special html characters in the names (returned by rest service)
        retVal.getSuperMetrics().forEach(supermetric -> supermetric.setName(StringEscapeUtils.unescapeHtml4(supermetric.getName())));

        return retVal;
    }

    public ViewDefinitionDTO getAllViewDefinitions() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        // Required header for internal API
        headers.set(INTERNAL_API_HEADER_NAME, Boolean.TRUE.toString());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        ViewDefinitionDTO retVal = new ViewDefinitionDTO();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(VIEW_DEFINITIONS_LIST_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return retVal;
            }
            throw new RuntimeException(String.format("Error ocurred trying to fetching view definitions. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error building REST URI to fetch view definitions. Message: %s", e.getMessage()));
        }
        try {
            retVal = mapper.readValue(response.getBody(), ViewDefinitionDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the view definitions response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error while parsing the view definitions response. Message: %s", e.getMessage()));
        }
        // Unescape special html characters in the names (returned by rest service)
        retVal.getViewDefinitions().forEach(view -> view.setName(StringEscapeUtils.unescapeHtml4(view.getName())));

        return retVal;
    }

    public ReportDefinitionDTO getAllReportDefinitions() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        ReportDefinitionDTO retVal = new ReportDefinitionDTO();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(REPORT_DEFINITIONS_LIST_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return retVal;
            }
            throw new RuntimeException(String.format("Error occurred when trying to fetch report definitions. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error occurred when trying to build REST URI to fetch report definitions. Message: %s", e.getMessage()));
        }
        try {
            retVal = mapper.readValue(response.getBody(), ReportDefinitionDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the report definitions response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    String.format("JSON processing error while parsing the supermetrics list report definitions response. Message: %s", e.getMessage()));
        }
        // Unescape special html characters in the names (returned by rest service)
        retVal.getReportDefinitions().forEach(report -> report.setName(StringEscapeUtils.unescapeHtml4(report.getName())));

        return retVal;
    }

    public List<AuthGroupDTO> findAllAuthGroups() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        List<AuthGroupDTO> retVal = new ArrayList<>();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(AUTH_GROUPS_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return retVal;
            }
            throw new RuntimeException(String.format("Error occurred when trying to fetch auth groups. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error occurred when trying to build REST URI to auth groups. Message: %s", e.getMessage()));
        }
        try {
            AuthGroupsDTO authGroupDto = mapper.readValue(response.getBody(), AuthGroupsDTO.class);
            return authGroupDto == null ? retVal : authGroupDto.getUserGroups();

        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the auth groups response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error while parsing the auth groups response. Message: %s", e.getMessage()));
        }
    }

    public List<AuthUserDTO> findAllAuthUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response;
        List<AuthUserDTO> retVal = new ArrayList<>();
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(AUTH_USERS_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return retVal;
            }
            throw new RuntimeException(String.format("Error occurred when trying to fetch auth users. Message: %s", e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error occurred when trying to build REST URI to auth users. Message: %s", e.getMessage()));
        }
        try {
        	AuthUsersDTO authUsersDto = mapper.readValue(response.getBody(), AuthUsersDTO.class);
            return authUsersDto == null ? retVal : authUsersDto.getUsers();

        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing the auth users response. Message: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error while parsing the auth users response. Message: %s", e.getMessage()));
        }
    }
    
    public AuthUserDTO findAllAuthUserByName(String name) {
        List<AuthUserDTO> allAuthUsers = this.findAllAuthUsers();

        return allAuthUsers.stream().filter(item -> name.equals(item.getUsername())).findAny().orElse(null);
    }
    
    public AuthGroupDTO findAuthGroupByName(String name) {
        List<AuthGroupDTO> allAuthGroups = this.findAllAuthGroups();

        return allAuthGroups.stream().filter(item -> name.equals(item.getDisplayName())).findAny().orElse(null);
    }

    public List<AuthGroupDTO> findAuthGroupsByNames(List<String> names) {
        List<AuthGroupDTO> allAuthGroups = this.findAllAuthGroups();

        return allAuthGroups.stream().filter(item -> names.contains(item.getDisplayName())).collect(Collectors.toList());
    }
    
    public List<AuthUserDTO> findAuthUsersByNames(List<String> names) {
    	List<AuthUserDTO> allAuthUsers = this.findAllAuthUsers();

        return allAuthUsers.stream().filter(item -> names.contains(item.getUsername())).collect(Collectors.toList());
    }    

    private Version getProductVersion() {
        if (this.productVersion == null) {
            this.productVersion = new Version(getVersion());
        }

        return this.productVersion;
    }

    private boolean isVrops82OrLater() {
        Integer major = this.getProductVersion().getMajorVersion();
        Integer minor = this.getProductVersion().getMinorVersion();

        return (major != null && major >= VROPS_MAJOR_VERSION) && (minor != null && minor >= VROPS_MINOR_VERSION);
    }

    private PolicyDTO.Policy findPolicyByName(String policyName) {
        // get all available policies in the target system
        List<PolicyDTO.Policy> policies = getAllPolicies();

        if (policies == null || policies.isEmpty()) {
            throw new RuntimeException("Unable to retrieve policies from the target system");
        }
        Optional<PolicyDTO.Policy> foundPolicy = policies.stream().filter(item -> item.getName().equalsIgnoreCase(policyName)).findFirst();
        if (!foundPolicy.isPresent()) {
            throw new RuntimeException(String.format("Policy '%s' could not be found on the target system", policyName));
        }

        return foundPolicy.get();
    }

    private List<PolicyDTO.Policy> filterPoliciesByName(List<String> policyEntries) {
        List<PolicyDTO.Policy> policies = getAllPolicies();
        if (policies == null || policies.isEmpty()) {
            return Collections.emptyList();
        }

        return policies.stream().filter(policy -> policyEntries.stream().anyMatch(entry -> entry.equalsIgnoreCase(policy.getName())))
                .collect(Collectors.toList());
    }

    private void updateCustomGroupPolicy(String customGroupPayload, Map<String, String> policyIdMap) {
        CustomGroupDTO.Group customGroup = serializeCustomGroup(customGroupPayload);
        if (customGroup == null) {
            return;
        }

        String policyId = customGroup.getPolicy();
        if (StringUtils.isEmpty(policyId)) {
            return;
        }

        String customGroupName = customGroup.getResourceKey().getName();
        // throw an exception if the custom group has a policy but it cannot be found in
        // the mapping data
        if (!policyIdMap.containsKey(policyId)) {
            throw new RuntimeException(String.format("The policy for custom group '%s' could not be found in the policy metadata file", customGroupName));
        }

        // find the custom group in the target system
        customGroup = findCustomGroupByName(customGroupName);
        if (customGroup == null) {
            throw new RuntimeException(String.format("Custom group '%s' cannot be found on the target system", customGroupName));
        }

        // find policy in the target system
        String policyName = policyIdMap.get(policyId);
        PolicyDTO.Policy policy = findPolicyByName(policyName);
        customGroup.setPolicy(policy.getId());

        // prepare rest call payload
        customGroupPayload = deserializeCustomGroup(customGroup);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(customGroupPayload, headers);
        ResponseEntity<String> responseEntity;
        logger.info("Setting policy for custom group '{}' to '{}'", customGroupName, policyName);
        try {
            URI restUri = new URI(getURIBuilder().setPath(CUSTOM_GROUPS_UPDATE_API).toString());
            responseEntity = restTemplate.exchange(restUri, HttpMethod.PUT, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Unable to update policy for custom group '%s' : %s", customGroupName, e.getMessage()), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(
                    String.format("Unable to determine REST endpoint for updating policy for custom group '%s' : %s", customGroupName, e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(String.format("Error updating policy for custom group '%s': Remote REST service returned status code %s",
                    customGroupName, responseEntity.getStatusCode()));
        }
    }

    private String setCustomGroupIdToNull(final String customGroupPayload) {
        CustomGroupDTO.Group customGroup = serializeCustomGroup(customGroupPayload);
        if (customGroup == null) {
            return customGroupPayload;
        }
        customGroup.setId(null);
        customGroup.setIdentifier(null);

        return deserializeCustomGroup(customGroup);
    }

    private String updateCustomGroupId(CustomGroupDTO.Group customGroup, String customGroupPayload) {
        CustomGroupDTO.Group serializedCustomGroup = serializeCustomGroup(customGroupPayload);
        if (customGroup == null || serializedCustomGroup == null) {
            return customGroupPayload;
        }
        CustomGroupDTO.Group existingCustomGroup = findCustomGroupByName(customGroup.getResourceKey().getName());
        if (existingCustomGroup != null) {
            // vROPs complains if in the membership rules' stat condition rules both
            // stringValue and doubleValue are set in this case set the doubleValue to null
            // and leave the stringValue set only
            // (used for bacward compatibility with older versions of Build Tools for VMware Aria)
            if (serializedCustomGroup.getMembershipDefinition() != null) {
                serializedCustomGroup.getMembershipDefinition().getRules().stream().forEach(rule -> {
                    rule.getStatConditionRules().forEach(statCondition -> {
                        if (statCondition.getDoubleValue() != null && !StringUtils.isEmpty(statCondition.getStringValue())) {
                            statCondition.setDoubleValue(null);
                        }
                    });
                });
            }

            serializedCustomGroup.setId(existingCustomGroup.getId());
            serializedCustomGroup.setIdentifier(existingCustomGroup.getId());
        }

        return deserializeCustomGroup(serializedCustomGroup);
    }

    private CustomGroupDTO.Group serializeCustomGroup(String customGroupPayload) {
        try {
            return mapper.readValue(customGroupPayload, CustomGroupDTO.Group.class);
        } catch (JsonMappingException e) {
            logger.warn("Failed to serialize custom group, mapping error: {}", e.getMessage());
            return null;
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize custom group, JSON processing error: {}", e.getMessage());
            return null;
        }
    }

    private String deserializeCustomGroup(CustomGroupDTO.Group customGroup) {
        try {
            return mapper.writeValueAsString(customGroup);
        } catch (JsonMappingException e) {
            logger.warn("Failed to de-serialize custom group, JSON mapping error: {}", e.getMessage());
            return null;
        } catch (JsonProcessingException e) {
            logger.warn("Failed to de-serialize custom group, JSON processing error: {}", e.getMessage());
            return null;
        }
    }

    private String deserializeCustomGroupType(CustomGroupTypeDTO customGroupType) {
        try {
            return mapper.writeValueAsString(customGroupType);
        } catch (JsonMappingException e) {
            logger.warn("Failed to de-serialize custom group type, JSON mapping error: {}", e.getMessage());
            return null;
        } catch (JsonProcessingException e) {
            logger.warn("Failed to de-serialize custom group type, JSON processing error: {}", e.getMessage());
            return null;
        }
    }

    private boolean customGroupExists(String customGroupPayload) {
        CustomGroupDTO.Group customGroup = serializeCustomGroup(customGroupPayload);
        if (customGroup == null) {
            return false;
        }

        return findCustomGroupByName(customGroup.getResourceKey().getName()) != null;
    }

    /**
     * Find vROPS custom group by name.
     * 
     * @param List<String> - names of the custom groups.
     * @return List<CustomGroupDTO.CustomGroup> - list of the found custom groups.
     */
    private List<CustomGroupDTO.Group> findCustomGroupsByNames(List<String> customGroupNames) {
        List<CustomGroupDTO.Group> retVal = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        String groupInfo = customGroupNames != null && !customGroupNames.isEmpty() ? customGroupNames.stream().collect(Collectors.joining(",")) : "";
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(CUSTOM_GROUPS_FETCH_API).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            uriBuilder.queryParam("includePolicy", Boolean.TRUE.toString());
            URI restUri = uriBuilder.build().toUri();
            response = restTemplate.exchange(restUri, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return new ArrayList<>();
            }
            throw new RuntimeException(String.format("Error ocurred trying to find custom groups %s. Message: %s", groupInfo, e.getMessage()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Error building REST URI to find custom groups %s. Message: %s", groupInfo, e.getMessage()));
        }
        CustomGroupDTO customGroup = new CustomGroupDTO();
        try {
            customGroup = mapper.readValue(response.getBody(), CustomGroupDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error while parsing custom group response: %s", e.getMessage()), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error while parsing custom group response: %s", e.getMessage()), e);
        }

        // if no filtering groups are specified return all
        if (customGroupNames == null || customGroupNames.isEmpty()) {
            return customGroup.getGroups();
        }

        for (String name : customGroupNames) {
            List<CustomGroupDTO.Group> foundGroups = customGroup.getGroups().stream().filter(item -> name.equalsIgnoreCase(item.getResourceKey().getName()))
                    .collect(Collectors.toList());
            if (foundGroups.isEmpty()) {
                continue;
            }
            retVal.addAll(foundGroups);
        }

        return retVal;
    }

    private CustomGroupDTO.Group findCustomGroupByName(String customGroupName) {
        if (StringUtils.isEmpty(customGroupName)) {
            return null;
        }
        List<CustomGroupDTO.Group> foundGroups = findCustomGroupsByNames(Arrays.asList(new String[] { customGroupName }));

        return foundGroups.stream().findFirst().isPresent() ? foundGroups.stream().findFirst().get() : null;
    }

    private void importDefinitionToVrops(Object definition, VropsPackageMemberType definitionType, Map<String, Object> dependentDefinitions) {
        // validate definition sanity
        if (!validateDefinition(definition, definitionType, dependentDefinitions)) {
            logger.warn("Invalid definition of type {}, import will be skipped", definitionType);
            return;
        }

        URI restUri = getDefinitionUri(definitionType, "");

        // get definition Id on the target system
        String definitionId = getDefinitionId(definition, definitionType);
        HttpMethod method = HttpMethod.POST;
        if (!StringUtils.isEmpty(definitionId)) {
            updateDefinitionId(definition, definitionType, definitionId);
            method = HttpMethod.PUT;
        }

        // update dependent definitions if any
        updateDependentDefinitions(definition, definitionType, dependentDefinitions);

        // If HTTP method is POST the id has to be removed from the pay load (limitation
        // of the vROps REST API)
        if (method.equals(HttpMethod.POST)) {
            removeIdFromDefinitionPayload(definition, definitionType);
        }

        // If the resourceKindKey is missing definition cannot be created.
        // In case of custom resourceKindKey it must be created as a new custom group type
        String adapterKindKey = getDefinitionAdapterKindKey(definition, definitionType);
        String resourceKindKey = getDefinitionResourceKindKey(definition, definitionType);
        if (adapterKindKey != null && resourceKindKey != null && !resourceKindExists(resourceKindKey, adapterKindKey)) {
            createCustomGroupType(resourceKindKey);
        }

        String definitionPayload;
        try {
            definitionPayload = mapper.writeValueAsString(definition);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("Error serializing definition type %s JSON mapping error : %s", definitionType, e.getMessage()), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Error serializing definition type %s JSON processing error: %s", definitionType, e.getMessage()), e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(definitionPayload, headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        String definitionName = getDefinitionName(definition, definitionType);

        try {
            responseEntity = restTemplate.exchange(restUri, method, entity, String.class);
        } catch (RestClientException e) {
            if (e.getMessage().contains(HttpStatus.NOT_FOUND.toString())) {
                throw new RuntimeException(String.format("Error importing %s of type %s to vROPS, the REST service could not validate definition data : %s",
                        definitionName, definitionType, e.getMessage()), e);

            }
            throw new RuntimeException(String.format("Error importing %s of type %s to vROPS : %s", definitionName, definitionType, e.getMessage()), e);
        }

        if (HttpMethod.POST.equals(method) && !HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(String.format("Error creating %s %s: remote REST service returned status code %s", definitionType, definitionName,
                    responseEntity.getStatusCode()));
        }
        if (HttpMethod.PUT.equals(method) && !HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(String.format("Error updating %s %s: remote REST service returned status code %s", definitionType, definitionName,
                    responseEntity.getStatusCode()));
        }
    }

    private String getDefinitionAdapterKindKey(Object definition, VropsPackageMemberType definitionType) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                return ((AlertDefinitionDTO.AlertDefinition) definition).getAdapterKindKey();
            }
            case SYMPTOM_DEFINITION: {
                return ((SymptomDefinitionDTO.SymptomDefinition) definition).getAdapterKindKey();
            }
            case RECOMMENDATION: {
                return null;
            }
            default: {
                logger.warn("Invalid definition type {}", definitionType);
            }
        }

        return null;
    }

    private String getDefinitionResourceKindKey(Object definition, VropsPackageMemberType definitionType) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                return ((AlertDefinitionDTO.AlertDefinition) definition).getResourceKindKey();
            }
            case SYMPTOM_DEFINITION: {
                return ((SymptomDefinitionDTO.SymptomDefinition) definition).getResourceKindKey();
            }
            case RECOMMENDATION: {
                return null;
            }
            default: {
                logger.warn("Invalid definition type {}", definitionType);
            }
        }

        return null;
    }

    private String getDefinitionName(Object definition, VropsPackageMemberType definitionType) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                return ((AlertDefinitionDTO.AlertDefinition) definition).getName();
            }
            case SYMPTOM_DEFINITION: {
                return ((SymptomDefinitionDTO.SymptomDefinition) definition).getName();
            }
            case RECOMMENDATION: {
                return ((RecommendationDTO.Recommendation) definition).getDescription();
            }
            default: {
                logger.warn("Invalid definition type {}", definitionType);
            }
        }

        return null;
    }

    private boolean validateDefinition(Object definition, VropsPackageMemberType definitionType, Map<String, Object> dependentDefinitions) {
        if (VropsPackageMemberType.SYMPTOM_DEFINITION.equals(definitionType)) {
            return validateSymptomDefinition(definition);
        }
        if (VropsPackageMemberType.ALERT_DEFINITION.equals(definitionType)) {
            return validateAlertDefinition(definition, dependentDefinitions);
        }

        return true;
    }

    private boolean validateSymptomDefinition(Object definition) {
        if (!(definition instanceof SymptomDefinitionDTO.SymptomDefinition)) {
            return false;
        }
        String adapterKind = ((SymptomDefinitionDTO.SymptomDefinition) definition).getAdapterKindKey() != null
                ? ((SymptomDefinitionDTO.SymptomDefinition) definition).getAdapterKindKey().trim()
                : null;
        String resourceKind = ((SymptomDefinitionDTO.SymptomDefinition) definition).getResourceKindKey() != null
                ? ((SymptomDefinitionDTO.SymptomDefinition) definition).getResourceKindKey().trim()
                : null;

        // check whether adapter kind is present
        if (adapterKind == null) {
            logger.error("Invalid Symptom definition: adapterKindKeyNode field is required and must be a string value. Symptom definition: '{}'",
                    ((SymptomDefinitionDTO.SymptomDefinition) definition).getName());
            return false;
        }

        // adapterKindKeyNode equals to all is not supported by the vROps Rest API
        // (version 8.0.0.14857692), it responds with httpStatusCode: 404, "message":"No
        // such Adapter Kind - All."
        if ("all".equalsIgnoreCase(adapterKind)) {
            logger.error("Symptoms definitions for all the adapter types are not supported. Symptom definition: '{}'",
                    ((SymptomDefinitionDTO.SymptomDefinition) definition).getName());
            return false;
        }

        // check whether the adapter kind exists on the target system
        if (!adapterKindKeyExists(adapterKind)) {
            logger.error(
                    "Adapter kind key '{}' for symptom definition '{}' is not present on the target system, please check whether content pack containing adapter kind '{}' is installed there",
                    adapterKind, ((SymptomDefinitionDTO.SymptomDefinition) definition).getName(), adapterKind);
            return false;
        }

        // check whether the resource kind exists on the target system
        if (!resourceKindExists(resourceKind, adapterKind)) {
            logger.warn(
                    "Resource kind key '{}' for symptom definition '{}' with adapter kind '{}' is not present on the target system, please check whether content pack containing resource kind '{}' is installed there",
                    resourceKind, ((SymptomDefinitionDTO.SymptomDefinition) definition).getName(), adapterKind, resourceKind);
        }

        return true;
    }

    private boolean validateAlertDefinition(Object definition, Map<String, Object> dependentDefinitions) {
        if (!(definition instanceof AlertDefinitionDTO.AlertDefinition)) {
            return false;
        }
        String resourceKind = ((AlertDefinitionDTO.AlertDefinition) definition).getResourceKindKey() != null
                ? ((AlertDefinitionDTO.AlertDefinition) definition).getResourceKindKey().trim()
                : null;
        String adapterKind = ((AlertDefinitionDTO.AlertDefinition) definition).getAdapterKindKey() != null
                ? ((AlertDefinitionDTO.AlertDefinition) definition).getAdapterKindKey().trim()
                : null;

        // check whether the adapter kind exists on the target system
        if (!adapterKindKeyExists(adapterKind)) {
            logger.error(
                    "Adapter kind key '{}' for alert definition '{}' is not present on the target system, please check whether content pack containing adapter kind '{}' is installed there",
                    adapterKind, ((AlertDefinitionDTO.AlertDefinition) definition).getName(), adapterKind);
            return false;
        }

                // check whether the resource kind exists on the target system
        if (!resourceKindExists(resourceKind, adapterKind)) {
            logger.warn(
                    "Resource kind key '{}' for alert definition '{}' with adapter kind '{}' is not present on the target system, please check whether content pack containing resource kind '{}' is installed there",
                    resourceKind, ((AlertDefinitionDTO.AlertDefinition) definition).getName(), adapterKind, resourceKind);
        }

        return validateDependentDefinitions(definition, dependentDefinitions);
    }

    private boolean validateDependentDefinitions(Object definition, Map<String, Object> dependentDefinitions) {
        if (!(definition instanceof AlertDefinitionDTO.AlertDefinition)) {
            return false;
        }
        if (dependentDefinitions == null || dependentDefinitions.isEmpty()) {
            return true;
        }
        String alertDefName = ((AlertDefinitionDTO.AlertDefinition) definition).getName();
        for (State state : ((AlertDefinitionDTO.AlertDefinition) definition).getStates()) {
            if (state.getBaseSymptomSet() == null) {
                continue;
            }
            // check base symptom set's symptom definition ids
            for (String symptomDefinitionId : state.getBaseSymptomSet().getSymptomDefinitionIds()) {
                if (!dependentDefinitions.containsKey(symptomDefinitionId)) {
                    continue;
                }
                Object dependentSymptomDef = dependentDefinitions.get(symptomDefinitionId);
                if (!(dependentSymptomDef instanceof SymptomDefinitionDTO.SymptomDefinition)) {
                    continue;
                }
                String dependentSymptomDefName = ((SymptomDefinitionDTO.SymptomDefinition) dependentSymptomDef).getName();
                String dependentSymptomDefId = getDefinitionIdByName(dependentSymptomDefName, VropsPackageMemberType.SYMPTOM_DEFINITION);
                if (StringUtils.isEmpty(dependentSymptomDefId)) {
                    logger.error("Dependent base symptom definition '{}' for alert definition '{}' is missing on the target system", dependentSymptomDefName,
                            alertDefName);
                    return false;
                }
            }

            // check symptom set's symptom definition ids
            for (SymptomSet symptomSet : state.getBaseSymptomSet().getSymptomSets()) {
                for (String symptomDefinitionId : symptomSet.getSymptomDefinitionIds()) {
                    if (!dependentDefinitions.containsKey(symptomDefinitionId)) {
                        continue;
                    }
                    Object dependentSymptomDef = dependentDefinitions.get(symptomDefinitionId);
                    if (!(dependentSymptomDef instanceof SymptomDefinitionDTO.SymptomDefinition)) {
                        continue;
                    }
                    String dependentSymptomDefName = ((SymptomDefinitionDTO.SymptomDefinition) dependentSymptomDef).getName();
                    String dependentSymptomDefId = getDefinitionIdByName(dependentSymptomDefName, VropsPackageMemberType.SYMPTOM_DEFINITION);
                    if (StringUtils.isEmpty(dependentSymptomDefId)) {
                        logger.error("Dependent symptom definition '{}' for alert definition '{}' is missing on the target system", dependentSymptomDefName,
                                alertDefName);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean adapterKindKeyExists(String adapterKindKey) {
        // get all adapter kinds on the target system
        AdapterKindDTO adapterKindOject = getAllAdapterKindData();
        if (adapterKindOject == null) {
            return false;
        }
        if (StringUtils.isEmpty(adapterKindKey)) {
            return false;
        }

        return adapterKindOject.getAdapterKind().stream().anyMatch(item -> adapterKindKey.equalsIgnoreCase(item.getKey()));
    }

    private AdapterKindDTO getAllAdapterKindData() {
        URI uri;
        try {
            uri = new URI(getURIBuilder().setPath(ADAPTER_KINDS_API).addParameter("pageSize", String.valueOf(DEFAULT_PAGE_SIZE)).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error fetching adapter kinds from target vROPs: %s", e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(
                    String.format("Error locating adapter kinds from target vROPs, remote REST service returned: %s", response.getStatusCode()));
        }
        try {
            return mapper.readValue(response.getBody(), AdapterKindDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error during parsing of adapter kind data: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error during parsing of adapter kind data: %s", e.getMessage()));
        }
    }

    private boolean resourceKindExists(String resourceKindKey, String adapterKindKey) {
        if (resourceKindKey.equals(VROPS_KIND_ALL) && adapterKindKey.equals(VROPS_KIND_ALL)) {
            return true;
        }

        if (StringUtils.isEmpty(adapterKindKey)) {
            return false;
        }

        // get all resource kinds on the target system
        ResourceKindDTO resourceKindObject = getAllResourceKinds(adapterKindKey);
        if (resourceKindObject == null) {
            return false;
        }

        return resourceKindObject.getResourceKind().stream().anyMatch(item -> resourceKindKey.equalsIgnoreCase(item.getKey()));
    }

    private ResourceKindDTO getAllResourceKinds(String adapterKindKey) {
        if (StringUtils.isEmpty(adapterKindKey)) {
            return new ResourceKindDTO();
        }

        String endpointName = String.format(RESOURCE_KINDS_API, adapterKindKey);
        URI uri;
        try {
            uri = new URI(getURIBuilder().setPath(endpointName).addParameter("pageSize", String.valueOf(DEFAULT_PAGE_SIZE)).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error fetching resource kinds for adapter kind %s from target vROPs: %s", adapterKindKey, e.getMessage()),
                    e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Error locating resource kinds for adapter kind %s from target vROPs, remote REST service returned: %s",
                    adapterKindKey, response.getStatusCode()));
        }
        try {
            return mapper.readValue(response.getBody(), ResourceKindDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error during parsing of data for adapter kind %s : %s", adapterKindKey, e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error during parsing of data for adapter kind %s : %s", adapterKindKey, e.getMessage()));
        }
    }

    private void updateDependentDefinitions(Object definition, VropsPackageMemberType definitionType, Map<String, Object> dependentDefinitions) {
        // Only for alert definitions need to update dependencies
        if (!VropsPackageMemberType.ALERT_DEFINITION.equals(definitionType)) {
            return;
        }
        if (definition == null) {
            return;
        }
        if (!(definition instanceof AlertDefinitionDTO.AlertDefinition)) {
            return;
        }

        if (dependentDefinitions == null || dependentDefinitions.isEmpty()) {
            return;
        }

        for (State state : ((AlertDefinitionDTO.AlertDefinition) definition).getStates()) {
            if (state.getBaseSymptomSet() == null) {
                continue;
            }
            // update base symptom set's symptom definition ids
            List<String> symptomDefIds = new ArrayList<>();
            for (String symptomDefinitionId : state.getBaseSymptomSet().getSymptomDefinitionIds()) {
                if (!dependentDefinitions.containsKey(symptomDefinitionId)) {
                    continue;
                }
                Object dependentSymptomDef = dependentDefinitions.get(symptomDefinitionId);
                if (!(dependentSymptomDef instanceof SymptomDefinitionDTO.SymptomDefinition)) {
                    continue;
                }
                String dependentSymptomDefName = ((SymptomDefinitionDTO.SymptomDefinition) dependentSymptomDef).getName();
                String dependentSymptomDefId = getDefinitionIdByName(dependentSymptomDefName, VropsPackageMemberType.SYMPTOM_DEFINITION);
                symptomDefIds.add(dependentSymptomDefId);
            }
            if (!symptomDefIds.isEmpty()) {
                state.getBaseSymptomSet().setSymptomDefinitionIds(symptomDefIds);
            }

            // update symptom set's symptom definition ids
            List<SymptomSet> targetSymptomSets = new ArrayList<>();
            for (SymptomSet symptomSet : state.getBaseSymptomSet().getSymptomSets()) {
                symptomDefIds = new ArrayList<>();
                for (String symptomDefinitionId : symptomSet.getSymptomDefinitionIds()) {
                    if (!dependentDefinitions.containsKey(symptomDefinitionId)) {
                        continue;
                    }
                    Object dependentSymptomDef = dependentDefinitions.get(symptomDefinitionId);
                    if (!(dependentSymptomDef instanceof SymptomDefinitionDTO.SymptomDefinition)) {
                        continue;
                    }
                    String dependentSymptomDefName = ((SymptomDefinitionDTO.SymptomDefinition) dependentSymptomDef).getName();
                    String dependentSymptomDefId = getDefinitionIdByName(dependentSymptomDefName, VropsPackageMemberType.SYMPTOM_DEFINITION);
                    symptomDefIds.add(dependentSymptomDefId);
                }
                if (!symptomDefIds.isEmpty()) {
                    symptomSet.setSymptomDefinitionIds(symptomDefIds);
                }
                targetSymptomSets.add(symptomSet);
            }
            if (!targetSymptomSets.isEmpty()) {
                state.getBaseSymptomSet().setSymptomSets(targetSymptomSets);
            }
        }
    }

    private void removeIdFromDefinitionPayload(Object definition, VropsPackageMemberType definitionType) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                ((AlertDefinitionDTO.AlertDefinition) definition).setId(null);
                break;
            }
            case SYMPTOM_DEFINITION: {
                ((SymptomDefinitionDTO.SymptomDefinition) definition).setId(null);
                break;
            }
            case RECOMMENDATION: {
                ((RecommendationDTO.Recommendation) definition).setId(null);
                break;
            }
            default: {
                logger.warn("Invalid definition type {}", definitionType);
            }
        }
    }

    private String getDefinitionId(Object definition, VropsPackageMemberType definitionType) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                return getDefinitionIdByName(((AlertDefinitionDTO.AlertDefinition) definition).getName(), definitionType);
            }
            case SYMPTOM_DEFINITION: {
                return getDefinitionIdByName(((SymptomDefinitionDTO.SymptomDefinition) definition).getName(), definitionType);
            }
            case RECOMMENDATION: {
                return getDefinitionIdByName(((RecommendationDTO.Recommendation) definition).getDescription(), definitionType);
            }
            default: {
                return null;
            }
        }
    }

    private void updateDefinitionId(Object definition, VropsPackageMemberType definitionType, String definitionId) {
        switch (definitionType) {
            case ALERT_DEFINITION: {
                ((AlertDefinitionDTO.AlertDefinition) definition).setId(definitionId);
                break;
            }
            case SYMPTOM_DEFINITION: {
                ((SymptomDefinitionDTO.SymptomDefinition) definition).setId(definitionId);
                break;
            }
            case RECOMMENDATION: {
                ((RecommendationDTO.Recommendation) definition).setId(definitionId);
                break;
            }
            default: {
                logger.warn("Unknown definition type {}", definitionType);
            }
        }
    }

    private String getDefinitionIdByName(String definitionName, VropsPackageMemberType definitionType) {
        String restUri;
        switch (definitionType) {
            case ALERT_DEFINITION: {
                restUri = ALERT_DEFS_API;
                break;
            }
            case SYMPTOM_DEFINITION: {
                restUri = SYMPTOM_DEFS_API;
                break;
            }
            case RECOMMENDATION: {
                restUri = RECOMMENDATIONS_API;
                break;
            }
            default: {
                throw new RuntimeException(String.format("Unsupported definition type %s", definitionType.name()));
            }
        }
        URI uri;
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(new URI(getURIBuilder().setPath(restUri).toString()));
            uriBuilder.queryParam("pageSize", DEFAULT_PAGE_SIZE);
            uri = uriBuilder.build().toUri();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(String.format("Error finding %s of %s: %s", definitionName, definitionType, e.getMessage()), e);
        }

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RuntimeException(
                    String.format("Error finding %s of type %s: remote REST service returned %s", definitionType, definitionName, response.getStatusCode()));
        }

        try {
            switch (definitionType) {
                case ALERT_DEFINITION: {
                    AlertDefinitionDTO alertDefinitions = mapper.readValue(response.getBody(), AlertDefinitionDTO.class);
                    List<AlertDefinitionDTO.AlertDefinition> filtered = alertDefinitions.getAlertDefinitions().stream()
                            .filter(item -> definitionName.equalsIgnoreCase(item.getName())).collect(Collectors.toList());
                    Optional<AlertDefinitionDTO.AlertDefinition> value = filtered.stream().findFirst();

                    return value.isPresent() ? value.get().getId() : null;
                }
                case SYMPTOM_DEFINITION: {
                    SymptomDefinitionDTO symptomDefinitions = mapper.readValue(response.getBody(), SymptomDefinitionDTO.class);
                    List<SymptomDefinitionDTO.SymptomDefinition> filtered = symptomDefinitions.getSymptomDefinitions().stream()
                            .filter(item -> definitionName.equalsIgnoreCase(item.getName())).collect(Collectors.toList());
                    Optional<SymptomDefinitionDTO.SymptomDefinition> value = filtered.stream().findFirst();

                    return value.isPresent() ? value.get().getId() : null;
                }
                case RECOMMENDATION: {
                    RecommendationDTO recommendations = mapper.readValue(response.getBody(), RecommendationDTO.class);
                    List<RecommendationDTO.Recommendation> filtered = recommendations.getRecommendations().stream()
                            .filter(item -> definitionName.equalsIgnoreCase(item.getDescription())).collect(Collectors.toList());
                    Optional<RecommendationDTO.Recommendation> value = filtered.stream().findFirst();

                    return value.isPresent() ? value.get().getId() : null;
                }
                default: {
                    throw new RuntimeException(String.format("Unsupported definition type %s", definitionType.name()));
                }
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("JSON mapping error during mapping of definitions data: %s", e.getMessage()), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("JSON processing error during processing of definitions data: %s", e.getMessage()), e);
        }
    }

    private URI getDefinitionUri(VropsPackageMemberType definitionType, String id) {
        String restUri;
        switch (definitionType) {
            case ALERT_DEFINITION: {
                restUri = ALERT_DEFS_API;
                break;
            }
            case SYMPTOM_DEFINITION: {
                restUri = SYMPTOM_DEFS_API;
                break;
            }
            case RECOMMENDATION: {
                restUri = RECOMMENDATIONS_API;
                break;
            }
            default: {
                throw new RuntimeException(String.format("Unsupported definition type %s", definitionType.name()));
            }
        }
        restUri = restUri + id;
        try {
            return new URI(getURIBuilder().setPath(restUri).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getDefinitionUri(VropsPackageMemberType definitionType) {
        String restUri;
        switch (definitionType) {
            case ALERT_DEFINITION: {
                restUri = ALERT_DEFS_API;
                break;
            }
            case SYMPTOM_DEFINITION: {
                restUri = SYMPTOM_DEFS_API;
                break;
            }
            case RECOMMENDATION: {
                restUri = RECOMMENDATIONS_API;
                break;
            }
            default: {
                throw new RuntimeException(String.format("Unsupported definition type %s", definitionType.name()));
            }
        }
        try {
            return new URI(getURIBuilder().setPath(restUri).addParameter("pageSize", String.valueOf(DEFAULT_PAGE_SIZE)).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private PolicyDTO deserializePolicies(String policiesJson) {
        if (StringUtils.isEmpty(policiesJson)) {
            return null;
        }

        try {
            return mapper.readValue(policiesJson, PolicyDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("Unable to deserialize policies from JSON string, JSON mapping error: %s", e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to deserialize policies from JSON string, JSON processing error: %s", e.getMessage()));
        }
    }

    private Object deserializeDefinitions(VropsPackageMemberType definitionType, String definitionJson) {
        if (StringUtils.isEmpty(definitionJson)) {
            return null;
        }
        try {
            switch (definitionType) {
                case ALERT_DEFINITION: {
                    return mapper.readValue(definitionJson, AlertDefinitionDTO.class);
                }
                case SYMPTOM_DEFINITION: {
                    return mapper.readValue(definitionJson, SymptomDefinitionDTO.class);
                }
                case RECOMMENDATION: {
                    return mapper.readValue(definitionJson, RecommendationDTO.class);
                }
                default: {
                    throw new RuntimeException(String.format("Unsupported definition type %s", definitionType.name()));
                }
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(String.format("Unable to deserialize definition of type %s from JSON string, JSON mapping error: %s",
                    definitionType.name(), e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to deserialize definition of type %s from JSON string, JSON processing error: %s",
                    definitionType.name(), e.getMessage()));
        }
    }

}
