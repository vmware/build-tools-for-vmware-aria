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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.vmware.pscoe.iac.artifact.model.Version;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSharingPolicy;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgFlavorMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgImageMapping;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgStorageProfile;

public class RestClientVraNgPrimitiveTestDouble extends RestClientVraNgPrimitive {

	/**
	 * RestClientVraNgPrimitiveTestDouble.
	 *
	 * @param configuration
	 * @param restTemplate
	 */
	public RestClientVraNgPrimitiveTestDouble(final ConfigurationVraNg configuration, final RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

	/**
	 * testGetProjectsPrimitive.
	 *
	 * @return project
	 */
	public List<VraNgProject> testGetProjectsPrimitive() {
		return super.getProjectsPrimitive();
	}

	/**
	 * getProjectId.
	 *
	 * @return projectId String
	 */
	@Override
	public String getProjectId() {
		return "";
	}

	/**
	 * getProductVersion.
	 *
	 * @return version Version
	 */
	@Override
	public Version getProductVersion() {
		return new Version("8.11");
	}

	/**
	 * jsonObjectValid.
	 *
	 * @param ob JsonObject
	 * @return isValid boolean
	 */
	@Override
	public boolean jsonObjectValid(final JsonObject ob) {
		return super.jsonObjectValid(ob);
	}

	/**
	 * getAllImageMappingsByRegionPrimitive.
	 *
	 * @return imageMappingsByRegionPrimitive Map<String, List<VraNgImageMapping>>
	 */
	@Override
	public Map<String, List<VraNgImageMapping>> getAllImageMappingsByRegionPrimitive() {
		return super.getAllImageMappingsByRegionPrimitive();
	}

	/**
	 * getURIBuilder.
	 *
	 * @return urlBuilder URIBuilder
	 */
	@Override
	public URIBuilder getURIBuilder() {
		return super.getURIBuilder();
	}

	/**
	 * getURI.
	 *
	 * @param builder
	 * @return url URI
	 */
	@Override
	public URI getURI(final URIBuilder builder) {
		return super.getURI(builder);
	}

	/**
	 * getAllFlavorProfilesByRegionPrimitive.
	 *
	 * @return flavorProfilesByRegionPrimitive Map<String, List<String>>
	 */
	@Override
	public Map<String, List<String>> getAllFlavorProfilesByRegionPrimitive() {
		return super.getAllFlavorProfilesByRegionPrimitive();
	}

	/**
	 * getAllStorageProfilesByRegionPrimitive.
	 *
	 * @return storageProfilesByRegionPrimitive Map<String, List<VraNgStorageProfile>>
	 */
	@Override
	public Map<String, List<VraNgStorageProfile>> getAllStorageProfilesByRegionPrimitive() {
		return super.getAllStorageProfilesByRegionPrimitive();
	}

	/**
	 * getAllImageProfilesByRegionPrimitive.
	 *
	 * @return imageProfilesByRegionPrimitive Map<String, List<String>>
	 */
	@Override
	public Map<String, List<String>> getAllImageProfilesByRegionPrimitive() {
		return super.getAllImageProfilesByRegionPrimitive();
	}

	/**
	 * createAbxActionMap.
	 *
	 * @param action AbxAction
	 * @return abxAction Map<String, Object>
	 */
	@Override
	public Map<String, Object> createAbxActionMap(final AbxAction action) throws IOException {
		return super.createAbxActionMap(action);
	}

	/**
	 * getAllFlavorMappingsByRegionPrimitive.
	 *
	 * @return mappingsByRegionPrimitive Map<String, List<VraNgFlavorMapping>>
	 */
	public Map<String, List<VraNgFlavorMapping>> getAllFlavorMappingsByRegionPrimitive() {
		return super.getAllFlavorMappingsByRegionPrimitive();
	}

	/**
	 * getContentSharingPolicyPrimitive.
	 *
	 * @param policyId String
	 * @return policy VraNgContentSharingPolicy
	 */
	public VraNgContentSharingPolicy getContentSharingPolicyPrimitive(final String policyId) {
		return super.getContentSharingPolicyPrimitive(policyId);
	}

	/** 
	 * importCustomResourcePrimitive.
	 *
	 * @param customResourceJson String
	 */
	public void importCustomResourcePrimitive(final String customResourceJson) throws URISyntaxException {
		super.importCustomResourcePrimitive(customResourceJson);
	}

	/**
	 * getDefaultHttpEntity.
	 *
	 * @return entity HttpEntity<String>
	 */
	public static HttpEntity<String> getDefaultHttpEntity() {
		return RestClientVraNgPrimitive.getDefaultHttpEntity();
	}
}
