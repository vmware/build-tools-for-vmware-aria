package com.vmware.pscoe.iac.artifact.helpers.vrang;

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
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNgPrimitive;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import com.vmware.pscoe.iac.artifact.model.vrang.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;

public class RestClientVraNgPrimitiveTestDouble extends RestClientVraNgPrimitive {

	public RestClientVraNgPrimitiveTestDouble(ConfigurationVraNg configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}
	public List<VraNgProject> testGetProjectsPrimitive() {
		return super.getProjectsPrimitive();
	}

	@Override 
	public String getProjectId() {
		return "";
	}

	@Override
	public Version getProductVersion() {
		return new Version("");
	}

	@Override
	public boolean jsonObjectValid(JsonObject ob){
		return super.jsonObjectValid(ob);
	}

	@Override
	public Map<String, List<VraNgImageMapping>> getAllImageMappingsByRegionPrimitive() {
		return super.getAllImageMappingsByRegionPrimitive();
	}

	@Override
	public URIBuilder getURIBuilder(){
		return super.getURIBuilder();
	}

	@Override
	public URI getURI(URIBuilder builder){
		return super.getURI(builder);
	}

	@Override
	public Map<String, List<String>> getAllFlavorProfilesByRegionPrimitive() {
		return super.getAllFlavorProfilesByRegionPrimitive();
	}

	@Override
	public Map<String, List<VraNgStorageProfile>> getAllStorageProfilesByRegionPrimitive() {
		return super.getAllStorageProfilesByRegionPrimitive();
	}

	@Override
	public Map<String, List<String>> getAllImageProfilesByRegionPrimitive() {
		return super.getAllImageProfilesByRegionPrimitive();
	}
	
	@Override
	public Map<String, Object> createAbxActionMap(AbxAction action) throws IOException {
		return super.createAbxActionMap(action);
	}

	public Map<String, List<VraNgFlavorMapping>> getAllFlavorMappingsByRegionPrimitive() {
		return super.getAllFlavorMappingsByRegionPrimitive();
	}

	public VraNgContentSharingPolicy getContentSharingPolicyPrimitive(String policyId) {
		return super.getContentSharingPolicyPrimitive(policyId);
	}

	public void importCustomResourcePrimitive( String customResourceJson ) throws URISyntaxException {
		super.importCustomResourcePrimitive( customResourceJson );
	}

	public static HttpEntity<String> getDefaultHttpEntity(){
		return RestClientVraNgPrimitive.getDefaultHttpEntity();
	}
}
