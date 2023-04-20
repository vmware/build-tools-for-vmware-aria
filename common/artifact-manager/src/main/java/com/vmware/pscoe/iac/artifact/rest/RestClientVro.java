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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.vmware.pscoe.iac.artifact.configuration.*;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vro.VroPackageContent;
import com.vmware.pscoe.iac.artifact.model.vro.VroPackageContent.ContentType;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowExecution;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.Parameter;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.StringValue;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.ArrayElements;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.ArrayStringValue;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.BooleanValue;
import com.vmware.pscoe.iac.artifact.model.vro.WorkflowParameters.NumberValue;

public class RestClientVro extends RestClient {
    private static final PackageType packageType = PackageType.VRO;
    private static final List<String> VRA_CLOUD_HOSTS = Arrays.asList("console.cloud.vmware.com", "api.mgmt.cloud.vmware.com");
    private static final String VRA_CLOUD_VERSION = "cloud";
    private static final Logger logger = LoggerFactory.getLogger(RestClientVro.class);

    private ConfigurationNg configuration;
    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    protected Configuration getConfiguration() {
        return (Configuration) this.configuration;
    }

    protected RestClientVro(ConfigurationNg configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getVersion() {
        URI url = getURI(getURIBuilder().setPath("vco/api/about"));

        if (isVraCloud(url)) {
            return VRA_CLOUD_VERSION;
        }
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);

        return JsonPath.parse(response.getBody()).read("$.version");
    }

    public String getHost() {
        return ((Configuration) this.configuration).getHost();
    }

    public List<Package> getPackages() {

        URI url;
        try {
            url = getURIBuilder().setPath("/vco/api/packages").build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<String> packagesNames = JsonPath.parse(response.getBody()).read("$..[?(@.name == 'name')].value");

        return packagesNames
                .stream()
                .map(name -> PackageFactory.getInstance(packageType, new File(name + "." + packageType.getPackageExtention())))
                .collect(Collectors.toList());
    }

    private List<Package> getPackages(List<Package> filesystemPackages) {
        List<Package> srvPackages = getPackages();

        for (Package fsPkg : filesystemPackages) {
            Optional<Package> srvPkgOptional = srvPackages.stream().filter(srvPkg -> fsPkg.equals(srvPkg)).findFirst();
            if (!srvPkgOptional.isPresent()) {

            }
        }
        return getPackages().stream().filter(srvPackage -> {
            boolean found = filesystemPackages.contains(srvPackage);
            if (!found) {
                throw new RuntimeException("Cannot find Package " + srvPackage);
            }
            return found;
        }).collect(Collectors.toList());
    }

	public List<Package> exportAllPackages(List<Package> vroPackages, 
										   boolean dryrun, 
										   boolean exportConfigAttributeValues, 
										   boolean exportConfigSecureStringValues) {
		return vroPackages.stream().map(vroPackage -> this.exportPackage(vroPackage,
																		 dryrun,
																		 exportConfigAttributeValues,
																		 exportConfigSecureStringValues)).collect(Collectors.toList());
    }

    public File downloadResource(String id, Path destination) {
        RequestCallback requestCallback = request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        ResponseExtractor<Void> responseExtractor = response -> {
            Files.copy(response.getBody(), destination, StandardCopyOption.REPLACE_EXISTING);
            return null;
        };

        URI url;
        try {
            url = getURIBuilder().setPath("/vco/api/resources/" + id)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);

        return destination.toFile();
    }

	public Package exportPackage(Package pkg, 
								 boolean dryrun, 
								 boolean exportConfigAttributeValues, 
								 boolean exportConfigSecureStringValues) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest request) throws IOException {
                request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            }
        };

        ResponseExtractor<Void> responseExtractor = new ResponseExtractor<Void>() {
            @Override
            public Void extractData(ClientHttpResponse response) throws IOException {
                if (!dryrun) {
					Path path = Paths.get(pkg.getFilesystemPath());

					System.out.println("About to copy the file");
					System.out.println(path.toString());
					
                    Files.copy(response.getBody(), path, StandardCopyOption.REPLACE_EXISTING);

					System.out.print("File copied.");
                }
                return null;
            }
        };

        URI url;
        try {
			logger.debug("exportConfigurationAttributeValues: " + String.valueOf(configuration.isPackageExportConfigurationAttributeValues()));
			logger.debug("exportConfigSecureStringAttributeValues: " + String.valueOf(configuration.isPackageExportConfigSecureStringAttributeValues()));

			//if set to true in the properties this value will be taken with priority
			boolean packageExportConfigurationValuesFinal = configuration.isPackageExportConfigurationAttributeValues();
			if (exportConfigAttributeValues) {
				packageExportConfigurationValuesFinal = true;
			}
			//if set to true in the properties this value will be taken with priority
			boolean exportConfigurationSecureStringValuesFinal = configuration.isPackageExportConfigSecureStringAttributeValues();
			if (exportConfigSecureStringValues) {
				exportConfigurationSecureStringValuesFinal = true;
			}

			url = getURIBuilder().setPath("/vco/api/packages/" + pkg.getFQName() + "/")
					.setParameter("exportConfigurationAttributeValues", String.valueOf(packageExportConfigurationValuesFinal))
                    .setParameter("exportGlobalTags", String.valueOf(configuration.isPackageExportGlobalTags()))
                    .setParameter("exportVersionHistory", String.valueOf(configuration.isPackageExportVersionHistory()))
                    .setParameter("exportAsZip", String.valueOf(configuration.isPackgeExportAsZip()))
					.setParameter("exportGlobalTags", String.valueOf(Boolean.TRUE))
					.setParameter("exportConfigSecureStringAttributeValues", String.valueOf(exportConfigurationSecureStringValuesFinal))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);

        return pkg;
    }

	private void importPackageWithMerge(Package filesystemPackage, boolean mergePackages) {
		String overwritePackages = mergePackages ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
		if(mergePackages) {
			logger.warn("Overwrite Package: {}", overwritePackages);
			logger.warn("The {} package will be updated ONLY with the partial content. Use this option only for faster development", filesystemPackage.getFQName());
		}
		importPackageForce(filesystemPackage,overwritePackages);
	}

    private void importPackageForce(Package filesystemPackage,String overwritePackage) {
		URI url;
        try {
            url = getURIBuilder()
                    .setPath("/vco/api/packages")
                    .setParameter("overwrite", overwritePackage)
                    .setParameter("tagImportMode", configuration.getPackageTagsImportMode())
                    .setParameter("importConfigurationAttributeValues", String.valueOf(configuration.isPackageImportConfigurationAttributeValues()))
                    .setParameter("importConfigSecureStringAttributeValues", String.valueOf(configuration.isPackageImportConfigSecureStringAttributeValues()))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            FileSystemResource f = new FileSystemResource(filesystemPackage.getFilesystemPath());
            logger.debug(String.format("Package to imports: %s", filesystemPackage.getFilesystemPath()));
            logger.debug(String.format("File length: %s", f.contentLength()));
            logger.debug(String.format("File is null: %s", f.getFile() == null));
            logger.debug(String.format("File filename: %s", f.getFilename()));
            logger.debug(String.format("File exists: %s", f.exists()));

        } catch (Exception e) {
            logger.debug(String.format("Exception throw: %s", e));
        }

        LinkedMultiValueMap<String, Object> contentMap = new LinkedMultiValueMap<>();
        contentMap.add("name", filesystemPackage);
        contentMap.add("Content-Type", "application/octet-stream");
        contentMap.add("file", new FileSystemResource(filesystemPackage.getFilesystemPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(contentMap, headers);


        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (!HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
            throw new RuntimeException(String.format("Error during import of package %s, REST API call returned %s", filesystemPackage, response.getStatusCode()));
        }
    }

    public Package importPackage(Package filesystemPackage, boolean dryrun, boolean mergePackages) {
        if (!dryrun) {
            importPackageWithMerge(filesystemPackage,mergePackages);
        }

        return filesystemPackage;
    }

    public List<Package> importAllPackages(List<Package> filesystemPackages, boolean dryrun, boolean mergePackages) {
        return filesystemPackages.stream().map(pkg -> importPackage(pkg, dryrun, mergePackages)).collect(Collectors.toList());
    }

    public Package deletePackage(Package pkg, boolean withContent, boolean dryrun){
        URIBuilder builder = getURIBuilder().setPath("/vco/api/packages/" + pkg.getFQName());
        if(withContent) {
            builder.setParameter("option", "deletePackageWithContent");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if(!dryrun) {
            restTemplate.exchange(this.getURI(builder), HttpMethod.DELETE, entity, String.class);
        }

        return pkg;
    }

    public void deleteContent(Content<VroPackageContent.ContentType> content, boolean dryrun) {
        String deletePath = null;

        switch (content.getType()) {
            case WORKFLOW: deletePath = "/vco/api/workflows/"; break;
            case ACTION: deletePath = "/vco/api/actions/"; break;
            case RESOURCE: deletePath = "/vco/api/resources/"; break;
            case CONFIGURATION: deletePath = "/vco/api/configurations/"; break;
        }

        URI url = getURI(getURIBuilder().setPath(deletePath + content.getId()).setParameter("force", "true"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        if(!dryrun) {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        }
    }

    public VroPackageContent getPackageContent(Package pkg) {
        URI url;
        try {
            url = getURIBuilder().setPath("/vco/api/packages/" + pkg.getFQName()).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return this.getPackageContent(JsonPath.parse(response.getBody()));
    }

    private VroPackageContent getPackageContent(DocumentContext context) {
        List<Content<VroPackageContent.ContentType>> content = new ArrayList<>();
        content.addAll(getContent(context, ContentType.WORKFLOW));
        content.addAll(getContent(context, ContentType.ACTION));
        content.addAll(getContent(context, ContentType.CONFIGURATION));
        content.addAll(getContent(context, ContentType.RESOURCE));

        return new VroPackageContent(content);
    }

    private List<Content<VroPackageContent.ContentType>> getContent(DocumentContext context, ContentType type) {
        String category = null;
        String name = null;

        switch (type) {
            case WORKFLOW: category = "workflows"; name = "workflow"; break;
            case ACTION: category = "actions"; name = "actions"; break;
            case RESOURCE: category = "resources"; name = "resource"; break;
            case CONFIGURATION: category = "configurations"; name = "configuration"; break;
        }

        String template = "$.%s[*].%s.attributes[?(@.name == '%s')].value";
        List<String> ids = context.read(String.format(template, category, name, "id"));
        List<String> names = context.read(String.format(template, category, name, "name"));

        if (ids.size() != names.size()) {
            logger.error("Broken package. Count of conttent IDs does not match the count of the names.");
            throw new RuntimeException("Broken vRO package");
        }

        List<Content<VroPackageContent.ContentType>> content = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            content.add(new Content<>(type, ids.get(i), names.get(i)));
        }

        return content;
    }

    /**
     * Start an asynchronous execution of a workflow and returns a token.
     *
     * @param workflowId workflow id to start
     * @param params     workflow parameters of type string
     * @param inputParametersTypes containing info about workflow input parameters types
     * @return the execution token id of the started workflow
     */
    public String startWorkflow(String workflowId, Properties params, Properties inputParametersTypes) {
        URI executionsUrl = this.buildUri("/vco/api/workflows/", workflowId, "/executions");
        String requestBody = this.buildParametersJson(params, inputParametersTypes);
        RequestEntity<String> request = RequestEntity.post(executionsUrl).accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(requestBody);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(request, String.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to fetch execution data for workflow id '%s' : %s", workflowId, e.getMessage()));
        }
        DocumentContext responseBody = JsonPath.parse(response.getBody());
        if (!responseBody.jsonString().contains("id")) {
            throw new RuntimeException(String.format("Unable to fetch execution data for workflow id '%s' : No execution ID provided", workflowId));
        }

        return responseBody.read("$.id");
    }


    /**
     * Get workflow input paramaters types.
     *
     * @param workflowId workflow id to check
     * @return Properties containing all input parameters types of the workflow
     */
    public Properties getInputParametersTypes(String workflowId) {
        Properties parametersTypes = new Properties();

        URI workflowContentUri = this.buildUri("/vco/api/workflows/", workflowId, "/content");

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(workflowContentUri, HttpMethod.GET, this.buildHttpEntry(), String.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to fetch input parameters types for workflow id '%s' : %s", workflowId, e.getMessage()));
        }

        final JsonArray params = new Gson().fromJson(response.getBody(), JsonObject.class).getAsJsonObject("input").getAsJsonArray("param");
        params.iterator().forEachRemaining((element) -> {
            final JsonObject entry = element.getAsJsonObject();
            final String name = entry.getAsJsonPrimitive("name").getAsString();
            final String type = entry.getAsJsonPrimitive("type").getAsString();
            parametersTypes.setProperty(name, type);
        });
        
        return parametersTypes;
    }

    /**
     * Checks whether given workflow exists.
     *
     * @param workflowId workflow id to check
     * @return boolean - true if workflow exists, otherwise false
     */
    public boolean isWorkflowExisting(String workflowId) {
        URI workflowContentUri = this.buildUri("/vco/api/workflows/", workflowId, "/content");

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(workflowContentUri, HttpMethod.GET, this.buildHttpEntry(), String.class);
        } catch (Exception e) {
            return false;
        }
        DocumentContext responseBody = JsonPath.parse(response.getBody());
        String targetWorkflowId = responseBody.jsonString().contains("id") ? responseBody.read("$.id") : null;

        return !StringUtils.isEmpty(targetWorkflowId);
    }

    /**
     * Retrieves a workflow execution state, either running, completed or failed.
     *
     * @param workflowId  the workflow ID
     * @param executionId the id of the workflow execution token as returned by startWorkflow
     * @return running|completed|failed|canceled
     */
    public String getExecutionState(String workflowId, String executionId) {
        URI executionStateUri = this.buildUri("/vco/api/workflows/", workflowId, "/executions/", executionId, "/state");
        HttpEntity<String> executionEntity = this.buildHttpEntry();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(executionStateUri, HttpMethod.GET, executionEntity, String.class);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to fetch execution state for workflow execution '%s' of workfow '%s' : %s", executionId, workflowId, e.getMessage()));
        }
        DocumentContext responseBody = JsonPath.parse(response.getBody());

        return responseBody.jsonString().contains("value") ? responseBody.read("$.value") : null;
    }

    /**
     * Gets the workflow execution logs.
     *
     * @param workflowId  the workflow ID
     * @param executionId the id of the workflow execution token as returned by startWorkflow
     * @param severity the minimum severity of the returned log messages (debug, info, warn or error)
     * @param sinceTimestamp a timestamp filter (in milliseconds)
     * @return the execution of the started workflow, including state, error (if any), string input and output parameters
     */
    public List<String> getWorkflowLogs(String workflowId, String executionId, String severity, long sinceTimestamp) {
        URI syslogsUri = this.buildUri("/vco/api/workflows/", workflowId, "/executions/", executionId, "/syslogs");

        String vroVersion = this.getVersion();
        ResponseEntity<String> response = null;
        if(vroVersion.startsWith("6")
                || vroVersion.startsWith("7.0")
                || vroVersion.startsWith("7.1")
                || vroVersion.startsWith("7.2")
                || vroVersion.startsWith("7.3")
                || vroVersion.startsWith("7.4")
                || vroVersion.startsWith("7.5")) {
            // The REST API accepts older-than parameter that actually returns only log messages printed since that timestamp
            String requestBody = String.format("{\"severity\": \"%s\",\"older-than\": %d}", severity, sinceTimestamp);
            RequestEntity<String> request = RequestEntity.post(syslogsUri)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .header("Content-Type", "application/json")
                    .body(requestBody);
            response = restTemplate.exchange(request, String.class);
        } else {
            // API change in vRO 7.6 - GET instead of POST
            response = restTemplate.exchange(syslogsUri, HttpMethod.GET, this.buildHttpEntry(), String.class);
        }

        final List<String> result = new LinkedList<>();
        final JsonArray logs = new Gson().fromJson(response.getBody(), JsonObject.class).getAsJsonArray("logs");
        logs.iterator().forEachRemaining((element) -> {
            final JsonObject entry = element.getAsJsonObject().getAsJsonObject("entry");
            final String origin = entry.getAsJsonPrimitive("origin").getAsString();
            final String timestamp = entry.getAsJsonPrimitive("time-stamp").getAsString();
            final String logSeverity = entry.getAsJsonPrimitive("severity").getAsString();
            final String shortDescr = entry.getAsJsonPrimitive("short-description").getAsString();
            final String longDescr = entry.has("long-description") ? entry.getAsJsonPrimitive("long-description").getAsString() : null;

            if (!"server".equals(origin)) { // skip server messages, as they are always included in the result
                final String message = String.format("[%s] [%s] %s",
                        timestamp, logSeverity, StringUtils.isEmpty(longDescr) ? shortDescr : longDescr);
                result.add(message);
            }
        });

        return result;
    }

    /**
     * Gets the workflow execution information.
     *
     * @param workflowId  the workflow ID
     * @param executionId the id of the workflow execution token as returned by startWorkflow
     * @return the execution of the started workflow, including state, error (if any), string input and output parameters
     */
    public WorkflowExecution getExecution(String workflowId, String executionId) {
        URI executionUri = this.buildUri("/vco/api/workflows/", workflowId, "/executions/", executionId);
        HttpEntity<String> executionEntity = this.buildHttpEntry();

        ResponseEntity<String> response = restTemplate.exchange(executionUri, HttpMethod.GET, executionEntity, String.class);
        DocumentContext json = JsonPath.parse(response.getBody());
        Properties input = parseWorkflowStringParameters(json, "$.input-parameters.*");
        Properties output = parseWorkflowStringParameters(json, "$.output-parameters.*");
        String state = json.read("$.state");
        String error = null;

        if (state.equalsIgnoreCase("failed")) {
            try {
                error = json.read("$.content-exception");
            } catch (PathNotFoundException ex) {
                error = "Unknown error: workflow exception information is missing.";
            }
        }

        return new WorkflowExecution(input, output, state, error);
    }

    /**
     * Parses the string parameters of a workflow execution at the given path, i.e. input or output
     *
     * @param ctx  document context, e.g. JsonPath.parse(response.getBody());
     * @param path the path to read parameters from, e.g. "$.output-parameters.*"
     */
    private Properties parseWorkflowStringParameters(DocumentContext ctx, String path) {
        Properties output = new Properties();
        List<Map<String, Object>> outputParams = ctx.read(path, List.class);
        for (Map<String, Object> param : outputParams) {
            String paramName = (String) param.get("name");
            if (param.containsKey("value")) {
                Map<String, Object> value = (Map<String, Object>) param.get("value");
                if (value.containsKey("string")) {
                    Map<String, String> stringValue = (Map<String, String>) value.get("string");
                    output.setProperty(paramName, stringValue.get("value"));
                }
            }
        }

        return output;
    }

    private HttpEntity<String> buildHttpEntry() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new HttpEntity<>(headers);
    }

    private URI buildUri(String... paths) {
        StringBuilder pathBuilder = new StringBuilder();
        for (String path : paths)
            pathBuilder.append(path);

        try {
            return getURIBuilder().setPath(pathBuilder.toString()).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected String buildParametersJson(Properties params, Properties inputParametersTypes) {
        WorkflowParameters workflowParameters = new WorkflowParameters();
        List<Parameter> paramsList = new ArrayList<>();

		for (Map.Entry<Object, Object> entry : params.entrySet()) {
			Parameter parameter = new Parameter();
			parameter.setScope("local");
			parameter.setName(entry.getKey().toString());
			String keyType = null;

			if (inputParametersTypes.containsKey(entry.getKey())) {
				keyType = inputParametersTypes.get(entry.getKey()).toString();
			}

            switch(keyType) {
                case "Array/string": 
                    ArrayStringValue arrayStringValue = new ArrayStringValue();
                    String jsonArray = StringEscapeUtils.unescapeJava(entry.getValue().toString());
                    String[] arrName = new Gson().fromJson(jsonArray, String[].class);
                    parameter.setType("Array/string");
                    List<WorkflowParameters.StringValue> stringValueList = new ArrayList<>();

                    for (String name : arrName) {
                        WorkflowParameters.StringValue stringValue = new WorkflowParameters.StringValue();
                        WorkflowParameters.String value = new WorkflowParameters.String();
                        value.setValue(name);
                        stringValue.setString(value);
                        stringValueList.add(stringValue);
                    }
                    ArrayElements elements = new ArrayElements();
                    elements.setElements(stringValueList);
                    arrayStringValue.setArray(elements);
                    parameter.setValue(arrayStringValue);
                    break;

                case "number":
                    NumberValue numberValue = new NumberValue();
                    WorkflowParameters.Number wfPNumber = new WorkflowParameters.Number();
                    wfPNumber.setValue(entry.getValue().toString());
                    parameter.setType("number");
                    numberValue.setNumber(wfPNumber);
                    parameter.setValue(numberValue);
                    break;

                case "boolean":
                    BooleanValue booleanValue = new BooleanValue();
                    WorkflowParameters.Bool value = new WorkflowParameters.Bool();
                    value.setValue(entry.getValue().toString());
                    parameter.setType("boolean");
                    booleanValue.setBoolean(value);
                    parameter.setValue(booleanValue);
                    break;

                case "string":
                default:
                    StringValue stringValue = new StringValue();
                    WorkflowParameters.String wfPString = new WorkflowParameters.String();
                    wfPString.setValue(entry.getValue().toString());
                    parameter.setType("string");
                    stringValue.setString(wfPString);
                    parameter.setValue(stringValue);
                    break; 
            }
			paramsList.add(parameter);
		}

        workflowParameters.setParameters(paramsList);
        Gson gson = new Gson();        
        return gson.toJson(workflowParameters);
    }

    private static boolean isVraCloud(URI uri) {
        return VRA_CLOUD_HOSTS.stream().filter(host -> uri.getHost().contains(host)).count() > 0;
   }

}
