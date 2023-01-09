package com.vmware.pscoe.iac.artifact.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVra;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.vra.VraPackageContent;
import com.vmware.pscoe.iac.artifact.rest.helpers.JsonHelper;
import com.vmware.pscoe.iac.artifact.rest.model.VraPackageDTO;

import org.apache.commons.io.IOUtils;
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
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class RestClientVraPrimitive extends RestClient {

	private final Logger logger = LoggerFactory.getLogger(RestClientVraPrimitive.class);

	private final String SERVICE_CONTENT = "/content-management-service/api/contents";
	private final String SERVICE_PACKAGE = "/content-management-service/api/packages";
	// TODO rename it
	private final String BLUEPRINT_PACKAGE = "/composition-service/api/blueprints";
	
	private final String SERVICE_PROPERETY_DEFINITION = "/properties-service/api/propertydefinitions";
    private final String SERVICE_PROPERTY_GROUP = "/properties-service/api/propertygroups";
    private final String SERVICE_XAAS_OPERATION = "/advanced-designer-service/api/resourceOperations";
    private final String SERVICE_XAAS_BLUEPRINT = "/advanced-designer-service/api/tenants/%s/blueprints";
    private final String SERVICE_XAAS_TYPE = "/advanced-designer-service/api/tenants/%s/types";
	private final String SERVICE_SOFTWARE = "/software-service/api/softwarecomponenttypes";
	private final String SERVICE_WORKFLOW_SUBSCRIPTION = "/advanced-designer-service/api/tenants/%s/event-broker/subscriptions";
	private final String CATALOG_ITEM = "/catalog-service/api/catalogItems";
	private final String CATALOG_SERVICE = "/catalog-service/api/services";
	private final String CATALOG_ICON = "/catalog-service/api/icons";

	private ConfigurationVra configuration;
	private RestTemplate restTemplate;

	protected RestClientVraPrimitive(ConfigurationVra configuration, RestTemplate restTemplate) {
		this.configuration = configuration;
		this.restTemplate = restTemplate;
	}
	
    @Override
    protected Configuration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public String getVersion() {
        URI url = getURI(getURIBuilder().setPath("identity/api/about"));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        return JsonPath.parse(response.getBody()).read("$.productVersion");
    }

	private List<Map<String, String>> getContentTypePrimitive(String url, StringJoiner filter) {

		URIBuilder uriBuilder = getURIBuilder().setPath(url).setParameter("page", "1").setParameter("limit", "100");
		if (filter.length() != 0) {
			uriBuilder.setParameter("$filter", filter.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity,
				String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, String>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result;
	}

	private ResponseEntity<String> postJsonPrimitive(String urlString, String jsonBody) throws URISyntaxException {
		URI url = getURIBuilder().setPath(urlString).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);
		return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	}

	private ResponseEntity<String> putJsonPrimitive(String urlString, String jsonBody) throws URISyntaxException {
		URI url = getURIBuilder().setPath(urlString).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonBody, headers);
		return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
	}

	protected List<Map<String, String>> getContentPrimitive(String contentTypeId, String name)
			throws URISyntaxException {
		StringJoiner filter = new StringJoiner(" and ");

		if (contentTypeId != null) {
			filter.add("contentTypeId eq '" + contentTypeId + "'");
		}
		if (name != null) {
			filter.add("name eq '" + name + "'");
		}

		return getContentTypePrimitive(SERVICE_CONTENT, filter);
	}

	protected List<Map<String, String>> getPackagesPrimitive() throws URISyntaxException {
		return getContentTypePrimitive(SERVICE_PACKAGE, new StringJoiner(""));
	}

	protected List<Map<String, String>> getPackageContentsPrimitive(String pkgId) {
		String url = SERVICE_PACKAGE + "/" + pkgId + "/contents";
		return getContentTypePrimitive(url, new StringJoiner(""));
	}

	protected List<Map<String, Object>> getWorkflowSubscriptionsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()))
			.setParameter("page", "1")
			.setParameter("limit", "100");
		URI url = getURI(uriBuilder);		

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result;
	}

	protected Map<String, Object> getWorkflowSubscriptionByNamePrimitive(String subscriptionName) {
		URIBuilder uriBuilder = getURIBuilder()
		.setPath(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()))
			.setParameter("$filter", "name eq '" + subscriptionName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> subscriptions = context.read("$..content.*");
		
		if (subscriptions.size() > 0) {
			return subscriptions.get(0);
		}
		
		logger.debug(String.format("Workflow Subscription with name '%s' not found.", subscriptionName));
		return null;
	}

	protected void importSubscriptionPrimitive(String subscriptionName, String jsonBody) throws URISyntaxException {
		logger.info(String.format("Updating Workflow Subscription with name '%s'.", subscriptionName));
		// If subscription with such name already exists use it's ID to update it. Otherwise just remove the ID to create a new subscription
		Map<String, Object> subscription = getWorkflowSubscriptionByNamePrimitive(subscriptionName);

		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> subscriptionJson = context.read("$");

		// Update the subscription tenant ID with the target tenant ID
		subscriptionJson.put("tenantId", configuration.getTenant());

		if (subscription != null) {
			logger.debug(String.format("Workflow Subscription with name '%s' already exist. Updating existing one.", subscriptionName));
			subscriptionJson.put("id", (String)subscription.get("id"));
		} else {
			logger.debug(String.format("Workflow Subscription with name '%s' not found. Creating a new one.", subscriptionName));
		}

		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		jsonBody = gson.toJson(subscriptionJson);
		postJsonPrimitive(String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()), jsonBody);
	}

	protected String getBlueprintCustomFormPrimitive(String bpId) {
		URI url = getURI(getURIBuilder().setPath(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,String.class);

			// CustomForms API returns the form as Json primitive string having value the stringified json form
			return new Gson().fromJson(response.getBody(), JsonPrimitive.class).getAsString();
		} catch (RuntimeException e) {
			if (e.getMessage().contains("404")) {
			    logger.info("Custom form for Blueprint '{}' not found.", bpId);
				return null;
			} else {
				throw new RuntimeException(e);
			}
		}

	}

	protected void activateBlueprintCustomFormPrimitive(String bpId) throws URISyntaxException {
		URIBuilder uriBuilder = getURIBuilder().setPath(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform/enable");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, entity, String.class);
	}

	protected void setBlueprintCustomFormPrimitive(String bpId, String jsonBody) throws URISyntaxException {
		postJsonPrimitive(BLUEPRINT_PACKAGE + "/" + bpId + "/forms/requestform", jsonBody);
	}

	protected Map<String, Object> getCatalogItemByNamePrimitive(String catalogItemName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(CATALOG_ITEM)
			.setParameter("$filter", "name eq '" + catalogItemName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> catalogItems = context.read("$..content.*");
		
		if (catalogItems.size() > 0) {
			return catalogItems.get(0);
		}
		
		logger.debug(String.format("Catalog Item with name '%s' not found.", catalogItemName));
		return null;
	}

	protected void setCatalogItemPrimitive(Map<String, Object> catalogItem) throws URISyntaxException {
		String catalogItemJson = JsonHelper.toJson(catalogItem);
		String catalogItemId = (String)catalogItem.get("id");
		putJsonPrimitive(CATALOG_ITEM + "/" + catalogItemId, catalogItemJson);
	}

	public Map<String, Object> getCatalogServiceByNamePrimitive(String serviceName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(CATALOG_SERVICE)
			.setParameter("$filter", "name eq '" + serviceName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> services = context.read("$..content.*");
		
		if (services.size() > 0) {
			return services.get(0);
		}
		
		logger.debug(String.format("Catalog service with name '%s' not found.", serviceName));
		return null;
	}

	protected Map<String, Object> getIconPrimitive(String iconId) {
		URIBuilder uriBuilder = getURIBuilder().setPath(CATALOG_ICON + "/" + iconId);
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		Map<String, Object> icon = context.read("$");
		
		if (icon == null) {
			logger.debug(String.format("Icon with ID '%s' not found.", iconId));
			return null;
		}

		return icon;
	}

	protected void setIconPrimitive(Map<String, Object> icon) throws URISyntaxException {
		String iconJson = JsonHelper.toJson(icon);
		postJsonPrimitive(CATALOG_ICON, iconJson);
	}

	protected List<Map<String, Object>> getGlobalPropertyDefinitionsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERETY_DEFINITION).setParameter("page", "1").setParameter("limit", "100");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result.stream().filter(pg -> pg.get("tenantId") == null).collect(Collectors.toList());
	}

	protected Map<String, Object> getGlobalPropertyDefinitionByNamePrimitive(String propertyDefinitionName) {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERETY_DEFINITION + "/" + propertyDefinitionName);
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
				throw e;
			}
		}

		if (response != null) {
			DocumentContext context = JsonPath.parse(response.getBody());
			Map<String, Object> propertyDefinition = context.read("$");
			if (propertyDefinition.get("tenantId") == null) {
				return propertyDefinition;
			}	
		}

		logger.debug(String.format("Global Property Definition with name '%s' not found.", propertyDefinitionName));
		return null;
	}

	protected void importGlobalPropertyDefinitionPrimitive(String propertyDefinitionName, String jsonBody) throws URISyntaxException {
		logger.debug(String.format("Updating Global Property Definition with name '%s'.", propertyDefinitionName));

		Map<String, Object> propertyDefinition = getGlobalPropertyDefinitionByNamePrimitive(propertyDefinitionName);
		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> propertyDefinitionJson = context.read("$");
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		jsonBody = gson.toJson(propertyDefinitionJson);

		if (propertyDefinition != null) {
			logger.debug(String.format("Global Property Definition with name '%s' already exist. Updating existing one.", propertyDefinitionName));
			putJsonPrimitive(SERVICE_PROPERETY_DEFINITION + "/" + propertyDefinitionName, jsonBody);
		} else {
			logger.debug(String.format("Global Property Definition with name '%s' not found. Creating a new one.", propertyDefinitionName));
			postJsonPrimitive(SERVICE_PROPERETY_DEFINITION, jsonBody);
		}
	}

	protected List<Map<String, Object>> getGlobalPropertyGroupsPrimitive() {
		URIBuilder uriBuilder = getURIBuilder().setPath(SERVICE_PROPERTY_GROUP).setParameter("page", "1").setParameter("limit", "100");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> result = context.read("$..content.*");
		int totalPages = context.read("$.metadata.totalPages");
		for (int pageNumber = 2; pageNumber <= totalPages; pageNumber++) {
			uriBuilder.setParameter("page", String.valueOf(pageNumber));
			response = restTemplate.exchange(getURI(uriBuilder), HttpMethod.GET, entity, String.class);
			result.addAll(JsonPath.parse(response.getBody()).read("$..content.*"));
		}
		return result.stream().filter(pg -> pg.get("tenantId") == null).collect(Collectors.toList());
	}

	protected Map<String, Object> getGlobalPropertyGroupByNamePrimitive(String propertyGroupName) {
		URIBuilder uriBuilder = getURIBuilder()
			.setPath(SERVICE_PROPERTY_GROUP)
			.setParameter("$filter", "label eq '" + propertyGroupName + "'");
		URI url = getURI(uriBuilder);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		DocumentContext context = JsonPath.parse(response.getBody());
		List<Map<String, Object>> propertyGroups = context.read("$..content.*");
		
		if (propertyGroups.size() > 0) {
			return propertyGroups.get(0);
		}
		
		logger.debug(String.format("Global Property Group with name '%s' not found.", propertyGroupName));
		return null;
	}

	protected void importGlobalPropertyGroupPrimitive(String propertyGroupName, String jsonBody) throws URISyntaxException {
		logger.debug(String.format("Updating Global Property Group with name '%s'.", propertyGroupName));

		Map<String, Object> propertyGroup = getGlobalPropertyGroupByNamePrimitive(propertyGroupName);
		DocumentContext context = JsonPath.parse(jsonBody);
		Map<String, Object> propertyGroupJson = context.read("$");
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		
		if (propertyGroup != null) {
			logger.debug(String.format("Global Property Group with name '%s' already exist. Updating existing one.", propertyGroupName));
			String propertyGroupId = (String)propertyGroup.get("id");
			propertyGroupJson.put("id", propertyGroupId);
			jsonBody = gson.toJson(propertyGroupJson);
			putJsonPrimitive(SERVICE_PROPERTY_GROUP + "/" + propertyGroupId, jsonBody);			
		} else {
			logger.debug(String.format("Global Property Group with name '%s' not found. Creating a new one.", propertyGroupName));
			jsonBody = gson.toJson(propertyGroupJson);
			postJsonPrimitive(SERVICE_PROPERTY_GROUP, jsonBody);
		}
	}

	protected String createPackagePrimitive(Package vraPackage, List<String> contentIds) throws URISyntaxException {
		String requestJson = new Gson().toJson(new VraPackageDTO(vraPackage.getFQName(), contentIds));
		ResponseEntity<String> response;
		try {
			response = postJsonPrimitive(SERVICE_PACKAGE, requestJson);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("400")) {
				throw new RuntimeException("Probably package with the same name already exist.", e);
			} else {
				throw new RuntimeException(e);
			}

		}
		List<String> locationHeader = response.getHeaders().get("Location");

		String locationUrl = locationHeader.get(0);
		String packageId = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
		logger.debug("Create Package[" + vraPackage.getFQName() + "] ID[" + packageId + "]");
		return packageId;
	}

	protected void deletePackagePrimitive(Package vraPackage) {
		URI url = getURI(getURIBuilder().setPath(SERVICE_PACKAGE + "/" + vraPackage.getId()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
	}

	protected void exportPackagePrimitive(Package vraPackage, boolean validate)
			throws NumberFormatException, URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_PACKAGE + "/" + vraPackage.getId() + (validate ? "/validate" : ""))
				.build();

		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
			}
		};

		ResponseExtractor<Void> responseExtractor = new ResponseExtractor<Void>() {
			public Void extractData(ClientHttpResponse response) throws IOException {
				if (validate) {
					String json = IOUtils.toString(response.getBody(),
							response.getHeaders().getContentType().getCharset().name());
					logger.info(JsonHelper.getPrettyJson(json));

				} else {
					Files.copy(response.getBody(), Paths.get(vraPackage.getFilesystemPath()),
							StandardCopyOption.REPLACE_EXISTING);
					logger.debug(String.format("EXPORT  | %s to file: %s", vraPackage, vraPackage.getFilesystemPath()));
				}
				return null;
			}
		};

		try {
			restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("400")) {
				throw new RuntimeException("Probably vRA package has no contents.", e);
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	protected VraPackageContent importPackagePrimitive(Package vraPackage, boolean dryrun) throws URISyntaxException {
		URI url = getURIBuilder().setPath(SERVICE_PACKAGE + (dryrun ? "/validate" : ""))
				// Ex: /validate?resolution mode=SKIP,OVERWRITE
				.setParameter("resolution mode", configuration.getPackageImportOverwriteMode()).build();

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("name", new File(vraPackage.getFilesystemPath()).getName());
		map.add("Content-Type", "application/octet-stream");
		map.add("file", new FileSystemResource(vraPackage.getFilesystemPath()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				map, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		String operationStatus = JsonPath.parse(response.getBody()).read("$.operationStatus");
		if (operationStatus.equalsIgnoreCase("FAILED")) {
			logger.error(JsonHelper.getPrettyJson(response.getBody()));
			throw new RuntimeException("Import of vRA Package[" + vraPackage.getFilesystemPath() + "] has Failed.");
		} else {
			logger.debug(JsonHelper.getPrettyJson(response.getBody()));
			logger.debug(String.format("IMPORT  | %s %s", vraPackage, (dryrun ? "DRYRUN " : "")));
		}
		
		return this.getPackageContentPrimitive(response.getBody());
	}
    
    protected void deleteContentPrimitive(Content<VraPackageContent.ContentType> content, boolean dryrun) {
        String deletePath = null;
        
        switch (content.getType()) {
            case XAAS_BLUEPRINT: deletePath = String.format(SERVICE_XAAS_BLUEPRINT, configuration.getTenant()); break;
            case XAAS_RESOURCE_TYPE: deletePath = String.format(SERVICE_XAAS_TYPE, configuration.getTenant()); break;
            case XAAS_RESOURCE_MAPPING: deletePath = String.format(SERVICE_XAAS_TYPE, configuration.getTenant()); break;
            case XAAS_RESOURCE_ACTION: deletePath = SERVICE_XAAS_OPERATION; break;
            case COMPOSITE_BLUEPRINT: deletePath = BLUEPRINT_PACKAGE; break;
            case SOFTWARE_COMPONENT: deletePath = SERVICE_SOFTWARE; break;
            case PROPERTY_GROUP: deletePath = SERVICE_PROPERTY_GROUP; break;
			case PROPERTY_DICTIONARY: deletePath = SERVICE_PROPERETY_DEFINITION; break;
			case WORKFLOW_SUBSCRIPTION: deletePath = String.format(SERVICE_WORKFLOW_SUBSCRIPTION, configuration.getTenant()); break;
			case GLOBAL_PROPERTY_DEFINITION: deletePath = SERVICE_PROPERETY_DEFINITION; break;
			case GLOBAL_PROPERTY_GROUP: deletePath = SERVICE_PROPERTY_GROUP; break;
        }
        
        URI url = getURI(getURIBuilder().setPath(String.format("%s/%s", deletePath, content.getId())));
        
        if(!dryrun) {
            restTemplate.exchange(url, HttpMethod.DELETE, getDefaultHttpEntity(), String.class);
        }
    }
    
    protected VraPackageContent getPackageContentPrimitive(Package pkg) {
        URI url = getURI(getURIBuilder().setPath(String.format("%s/%s/contents", SERVICE_PACKAGE, pkg.getId())));

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getDefaultHttpEntity(), String.class);
        
        JsonElement root = new JsonParser().parse(response.getBody());
        
        List<Content<VraPackageContent.ContentType>> content = new ArrayList<>();

        if(root.isJsonObject()){
            root.getAsJsonObject().getAsJsonArray("content").forEach(o -> {
                JsonObject ob = o.getAsJsonObject();
                
                content.add(new Content<>(
                    VraPackageContent.ContentType.getInstance(ob.get("contentTypeId").getAsString()),
                    ob.get("contentId").getAsString(),
                    ob.get("name").getAsString()));
                
            });

        }
        return new VraPackageContent(content);
    }
    
    private VraPackageContent getPackageContentPrimitive(String packageImportedResponse) {
        JsonElement root = new JsonParser().parse(packageImportedResponse);
        
        List<Content<VraPackageContent.ContentType>> content = new ArrayList<>();

        if(root.isJsonObject()){
            root.getAsJsonObject().getAsJsonArray("operationResults").forEach(o -> {
                JsonObject ob = o.getAsJsonObject();
                
                content.add(new Content<>(
                    VraPackageContent.ContentType.getInstance(ob.get("contentTypeId").getAsString()),
                    ob.get("contentId").getAsString(),
                    ob.get("contentName").getAsString()));
                
            });

        }
        return new VraPackageContent(content);
    }

}
