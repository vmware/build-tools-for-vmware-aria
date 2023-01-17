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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationCs;
import com.vmware.pscoe.iac.artifact.rest.model.cs.CustomIntegrationVersion;
import com.vmware.pscoe.iac.artifact.rest.model.cs.Endpoint;
import com.vmware.pscoe.iac.artifact.rest.model.cs.Variable;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestClientCs extends RestClient {
    private final Logger logger = LoggerFactory.getLogger(RestClientCs.class);

    private static final String BASE = "/codestream/api";
    private static final String API_VERSION = BASE + "/about";
    private static final String VARIABLES_PATH = BASE + "/variables";
    private static final String VARIABLE_PROJECT_NAME_PATH = BASE + "/variables/{0}/{1}";
    private static final String CUSTOM_INTEGRATION_PATH = BASE + "/custom-integrations";
    private static final String CUSTOM_INTEGRATION_VERSION_PATH = BASE + "/custom-integrations/{0}/versions";
    private static final String ENDPOINTS_PATH = BASE + "/endpoints";
    private static final String ENDPOINT_UPDATE_PATH = BASE + "/endpoints/{0}/{1}";
    private static final String PIPELINE_PATH = BASE + "/pipelines";
    private static final String PIPELINE_UPDATE_PATH = BASE + "/pipelines/{0}/{1}";
    private static final String CLOUD_PROXY_PATH = BASE + "/cloud-proxy";
    private static final String GET_PROJECT_INFO = "/iaas/api/projects";

    private static final String GIT_PATH = BASE + "/git-webhooks";
    private static final String GIT_PROJECT_NAME_PATH = GIT_PATH + "/{0}/{1}";
    private static final String DOCKER_PATH = BASE + "/registry-webhooks";
    private static final String DOCKER_PROJECT_NAME_PATH = DOCKER_PATH + "/{0}/{1}";
    private static final String GERRIT_LISTENER_PATH = BASE + "/gerrit-listeners";
    private static final String GERRIT_LISTENER_PROJECT_NAME_PATH = GERRIT_LISTENER_PATH + "/{0}/{1}";
    private static final String GERRIT_TRIGGER_PATH = BASE + "/gerrit-triggers";
    private static final String GERRIT_TRIGGER_PROJECT_NAME_PATH = GERRIT_TRIGGER_PATH + "/{0}/{1}";

    private static final Integer PAGE_SIZE = 100;
    private ConfigurationCs configuration;
    private RestTemplate restTemplate;
    private String apiVersion;
    private String projectName;
    private String projectId;
    private String cloudProxyId;

    protected RestClientCs(ConfigurationCs configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
        initProjectNameAndId();
        initCloudProxyId();
    }

    private void initProjectNameAndId() {
        String constraint = StringUtils.isNotEmpty(configuration.getProjectName())
                ? String.format("name eq '%s'", configuration.getProjectName())
                : String.format("id eq '%s'", configuration.getProjectId());

        URI url = getURI(getURIBuilder()
                .setPath(GET_PROJECT_INFO)
                .setParameter("$filter", constraint));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        JsonElement jsonObj = JsonParser.parseString(response.getBody());
        JsonArray projectJsonArray = jsonObj.getAsJsonObject().get("content").getAsJsonArray();
        if (projectJsonArray.size() != 1) {
            throw new RuntimeException("Project not found with given configuration!");
        }
        this.projectName = projectJsonArray.get(0).getAsJsonObject().get("name").getAsString();
        this.projectId = projectJsonArray.get(0).getAsJsonObject().get("id").getAsString();

    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getCloudProxyId() {
        return cloudProxyId;
    }


    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    protected Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String getVersion() {
        if (this.apiVersion != null && !this.apiVersion.isEmpty()) {
            return this.apiVersion;
        }
        URI url = getURI(getURIBuilder().setPath(API_VERSION));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        this.apiVersion = JsonPath.parse(response.getBody()).read("$.supportedApis[0].apiVersion");
        logger.info("Detected API Version " + this.apiVersion);
        return this.apiVersion;
    }


    public Variable getProjectVariableByName(String variableName) {
        Gson gson = new Gson();
        logger.info("Get variable  " + variableName);
        String projectName = this.getProjectName();
        URI url = getURI(getURIBuilder()
                .setPath(MessageFormat.format(VARIABLE_PROJECT_NAME_PATH, projectName, variableName)));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        logger.debug("Body: " + response.getBody());
        return gson.fromJson(response.getBody(), Variable.class);
    }

    public List<Variable> getProjectVariables() {
        logger.info("Get Variables");
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("$filter", String.format("project eq '%s'", getProjectName()));
        List<JsonObject> result = getPagedResult(VARIABLES_PATH, params);
        Gson gson = gson();
        return result.stream()
                .map(obj -> gson.fromJson(obj, Variable.class))
                .collect(Collectors.toList());
    }

    public String updateVariable(Variable var) {
        logger.info("Importing pipeline " + var.getName());
        URI url = getURI(getURIBuilder().setPath(MessageFormat.format(VARIABLE_PROJECT_NAME_PATH, getProjectName(), var.getName())));
        HttpEntity<String> entity = getJsonEntity(var);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        logger.debug("Body: " + response.getBody());
        return response.getBody();
    }

    public String createVariable(Variable var) {
        logger.info("Importing variable " + var.getName());
        URI url = getURI(getURIBuilder().setPath(VARIABLES_PATH));
        HttpEntity<String> entity = getJsonEntity(var);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        logger.debug("Body: " + response.getBody());
        return response.getBody();
    }

    public List<Endpoint> getProjectEndpoints() {
        logger.info("Get Endpoints");
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("$filter", String.format("project eq '%s'", getProjectName()));
        List<JsonObject> result = getPagedResult(ENDPOINTS_PATH, params);
        Gson gson = gson();
        return result.stream()
                .map(obj -> gson.fromJson(obj, Endpoint.class))
                .collect(Collectors.toList());
    }

    public String updateEndpoint(Endpoint var) {
        logger.info("Update endpoint: {}  ", var.getName());
        URI url = getURI(getURIBuilder().setPath(MessageFormat.format(ENDPOINT_UPDATE_PATH, getProjectName(), var.getName())));
        HttpEntity<String> entity = getJsonEntity(var);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        logger.debug("Body: " + response.getBody());
        return response.getBody();
    }

    public String createEndpoint(Endpoint var) {
        logger.info("Create endpoint " + var.getName());
        URI url = getURI(getURIBuilder().setPath(ENDPOINTS_PATH));
        HttpEntity<String> entity = getJsonEntity(var);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        logger.debug("Body: " + response.getBody());
        return response.getBody();
    }

    public Optional<CustomIntegrationVersion> getCustomIntegrationByName(String ciName) {
        logger.info("Get Custom Integration: " + ciName);
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("$filter", String.format("name eq '%s'", ciName));
        List<JsonObject> result = getPagedResult(CUSTOM_INTEGRATION_PATH, params);
        Gson gson = gson();
        return result.stream()
                .map(obj -> gson.fromJson(obj, CustomIntegrationVersion.class))
                .findFirst();
    }

    public List<CustomIntegrationVersion> getCustomIntegrations() {
        logger.info("Get Custom Integrations");
        List<JsonObject> result = getPagedResult(CUSTOM_INTEGRATION_PATH);
        Gson gson = gson();
        return result.stream()
                .map(obj -> gson.fromJson(obj, CustomIntegrationVersion.class))
                .collect(Collectors.toList());
    }

    public List<CustomIntegrationVersion> getCustomIntegrationVersions(String id) {
        logger.info("Get Custom Integration Versions for: " + id);
        List<JsonObject> result = getPagedResult(MessageFormat.format(CUSTOM_INTEGRATION_VERSION_PATH, id));
        Gson gson = gson();
        return result.stream()
                .map(obj -> gson.fromJson(obj, CustomIntegrationVersion.class))
                .collect(Collectors.toList());
    }

    public void deleteCustomIntegration(String id) {
        logger.info("Delete custom integration" + id);
        URI url = getURI(getURIBuilder().setPath(CUSTOM_INTEGRATION_PATH + "/" + id));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
        logger.debug("Body: " + response.getBody());
    }

    public CustomIntegrationVersion createCustomIntegration(CustomIntegrationVersion ci) {
        logger.info("Create Custom Integration: " + ci.getName());
        URI url = getURI(getURIBuilder().setPath(CUSTOM_INTEGRATION_PATH));
        HttpEntity<String> entity = getJsonEntity(ci);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        logger.debug("Body: " + response.getBody());
        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
        return gson.fromJson(response.getBody(), CustomIntegrationVersion.class);
    }


    public CustomIntegrationVersion updateCustomIntegration(CustomIntegrationVersion ci) {
        logger.info("Update Custom Integration: " + ci.getName());
        URI url = getURI(getURIBuilder().setPath(CUSTOM_INTEGRATION_PATH + "/" + ci.getId()));

        HttpEntity<String> entity = getJsonEntity(ci);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        logger.debug("Body: " + response.getBody());
        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
        return gson.fromJson(response.getBody(), CustomIntegrationVersion.class);
    }

    public Object createCustomIntegrationVersion(String id, CustomIntegrationVersion version) {
        logger.info(String.format("Create Custom Integration Version: %s : %s ", version.getName(), version.getVersion()));
        URI url = getURI(getURIBuilder().setPath(MessageFormat.format(CUSTOM_INTEGRATION_VERSION_PATH, id)));

        HttpEntity<String> entity = getJsonEntity(version);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        logger.debug("Body: " + response.getBody());
        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
        return gson.fromJson(response.getBody(), CustomIntegrationVersion.class);
    }

    public List<JsonObject> getProjectPipelines() {
        logger.info("Get Pipelines");
        return getPagedResult(PIPELINE_PATH, getProjectFilterParam());
    }

    public JsonObject getProjectPipelineByName(String name) {
        logger.info("Get pipeline: " + name);
        return requestForPath(getProjectNameUrl(PIPELINE_UPDATE_PATH, name), HttpMethod.GET, null);
    }

    public JsonObject updatePipeline(String name, JsonObject var) {
        logger.info("Update pipeline: {}", name);
        return requestForPath(getProjectNameUrl(PIPELINE_UPDATE_PATH, name), HttpMethod.PUT, var);
    }

    public JsonObject patchPipeline(String name, JsonObject var) {
        logger.info("PATCH pipeline: {}", name);
        return requestForPath(getProjectNameUrl(PIPELINE_UPDATE_PATH, name), HttpMethod.PATCH, var);
    }

    public JsonObject createPipeline(JsonObject var) {
        String name = var.get("name").getAsString();
        logger.info("Create pipeline: {}", name);
        return requestForPath(getURI(getURIBuilder().setPath(PIPELINE_PATH)), HttpMethod.POST, var);
    }


    public List<JsonObject> getProjectGitWebhooks() {
        logger.info("Get git Webhooks for project");
        return getPagedResult(GIT_PATH, getProjectFilterParam());
    }

    public JsonObject getProjectGitWebhook(String name) {
        logger.info("Get Git webhook: " + name);
        return requestForPath(getProjectNameUrl(GIT_PROJECT_NAME_PATH, name), HttpMethod.GET, null);
    }

    public JsonObject updateGitWebhook(String name, JsonObject var) {
        logger.info("Update git Webhook: {}", name);
        return requestForPath(getProjectNameUrl(GIT_PROJECT_NAME_PATH, name), HttpMethod.PUT, var);
    }

    public JsonObject createGitWebhook(JsonObject var) {
        String name = var.get("name").getAsString();
        logger.info("Create Git Webhook: {}", name);
        return requestForPath(getURI(getURIBuilder().setPath(GIT_PATH)), HttpMethod.POST, var);
    }


    public List<JsonObject> getProjectDockerWebhooks() {
        logger.info("Get git Webhooks for project");
        return getPagedResult(DOCKER_PATH, getProjectFilterParam());
    }

    public JsonObject getProjectDockerWebhook(String name) {
        logger.info("Get Docker webhook: " + name);
        return requestForPath(getProjectNameUrl(DOCKER_PROJECT_NAME_PATH, name), HttpMethod.GET, null);
    }

    public JsonObject updateDockerWebhook(String name, JsonObject var) {
        logger.info("Update Docker Webhook: {}", name);
        return requestForPath(getProjectNameUrl(DOCKER_PROJECT_NAME_PATH, name), HttpMethod.PUT, var);
    }

    public JsonObject createDockerWebhook(JsonObject var) {
        String name = var.get("name").getAsString();
        logger.info("Create Docker Webhook: {}", name);
        return requestForPath(getURI(getURIBuilder().setPath(DOCKER_PATH)), HttpMethod.POST, var);
    }


    public List<JsonObject> getProjectGerritListeners() {
        logger.info("Get Gerrit Listener for project");
        return getPagedResult(GERRIT_LISTENER_PATH, getProjectFilterParam());
    }

    public JsonObject getProjectGerritListener(String name) {
        logger.info("Get  Gerrit Listener: " + name);
        return requestForPath(getProjectNameUrl(GERRIT_LISTENER_PROJECT_NAME_PATH, name), HttpMethod.GET, null);
    }

    public JsonObject updateGerritListener(String name, JsonObject var) {
        logger.info("Update  Gerrit Listener: {}", name);
        return requestForPath(getProjectNameUrl(GERRIT_LISTENER_PROJECT_NAME_PATH, name), HttpMethod.PUT, var);
    }

    public JsonObject createGerritListener(JsonObject var) {
        String name = var.get("name").getAsString();
        logger.info("Create  Gerrit Trigger: {}", name);
        return requestForPath(getURI(getURIBuilder().setPath(GERRIT_LISTENER_PATH)), HttpMethod.POST, var);
    }

    public List<JsonObject> getProjectGerritTriggers() {
        logger.info("Get Gerrit Triggers for project");
        return getPagedResult(GERRIT_TRIGGER_PATH, getProjectFilterParam());
    }

    public JsonObject getProjectGerritTrigger(String name) {
        logger.info("Get Gerrit Trigger: " + name);
        return requestForPath(getProjectNameUrl(GERRIT_TRIGGER_PROJECT_NAME_PATH, name), HttpMethod.GET, null);
    }

    public JsonObject updateGerritTrigger(String name, JsonObject var) {
        logger.info("Update  Gerrit Trigger: {}", name);
        return requestForPath(getProjectNameUrl(GERRIT_TRIGGER_PROJECT_NAME_PATH, name), HttpMethod.PUT, var);
    }

    public JsonObject createGerritTrigger(JsonObject var) {
        String name = var.get("name").getAsString();
        logger.info("Create  Gerrit Trigger: {}", name);
        return requestForPath(getURI(getURIBuilder().setPath(GERRIT_TRIGGER_PATH)), HttpMethod.POST, var);
    }


    private void initCloudProxyId() {
        if (StringUtils.isEmpty(configuration.getCloudProxyName())) {
            return;
        }

        List<JsonObject> result = getPagedResult(CLOUD_PROXY_PATH);
        Optional<JsonObject> optional = result.stream()
                .peek(el -> logger.info(el
                        .get("customProperties")
                        .getAsJsonObject()
                        .get("proxyName")
                        .getAsString()))
                .filter(el -> el
                        .get("customProperties")
                        .getAsJsonObject()
                        .get("proxyName")
                        .getAsString()
                        .equals(configuration.getCloudProxyName()))
                .findFirst();

        if (!optional.isPresent()) {
            throw new RuntimeException("No cloud proxy id found for name: " + configuration.getCloudProxyName());
        }

        this.cloudProxyId = optional.get().get("id").getAsString();

    }

    public List<JsonObject> getPagedResult(String variablePath) {
        return getPagedResult(variablePath, new LinkedHashMap<String, String>());
    }

    private List<JsonObject> getPagedResult(String variablePath, Map<String, String> parameters) {
        List<JsonObject> result = new ArrayList<>();
        int page = 0;
        boolean hasMorePages = true;
        do {
            URIBuilder builder = getURIBuilder().setPath(variablePath)
                    .setParameter("$top", PAGE_SIZE.toString())
                    .setParameter("$skip", (PAGE_SIZE * page + ""));
            parameters.forEach((key, value) -> builder.setParameter(key, value));
            URI url = getURI(builder);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
            logger.debug("Body: " + response.getBody());
            JsonObject root = JsonParser.parseString(response.getBody()).getAsJsonObject();
            root.get("documents").getAsJsonObject()
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getValue().getAsJsonObject())
                    .forEachOrdered(result::add);

            int requestCount = root.get("count").getAsInt();
            page++;
            hasMorePages = requestCount == PAGE_SIZE;
        } while (hasMorePages);
        return result;
    }

    private Gson gson() {
        return new GsonBuilder().setLenient().serializeNulls().create();
    }

    private HttpEntity<String> getJsonEntity(Object body) {
        Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new HttpEntity<String>(gson.toJson(body), headers);
    }


    private URI getProjectNameUrl(String path, String name) {
        return getURI(getURIBuilder().setPath(MessageFormat.format(path, getProjectName(), name)));
    }

    Map<String, String> getProjectFilterParam() {
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("$filter", String.format("project eq '%s'", getProjectName()));
        return params;
    }

    private JsonObject requestForPath(URI url, HttpMethod method, JsonObject var) {
        HttpEntity<String> entity = var == null ? getDefaultHttpEntity() : getJsonEntity(var);
        ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
        logger.debug("Body: " + response.getBody());
        return JsonParser.parseString(response.getBody()).getAsJsonObject();
    }

}
