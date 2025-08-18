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
package com.vmware.pscoe.iac.artifact.aria.codestream.store.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.common.store.models.PackageDescriptor;

public class CsPackageDescriptor extends PackageDescriptor {
	private static final Logger logger = LoggerFactory.getLogger(CsPackageDescriptor.class);

	private List<String> pipeline;
	private List<String> variable;
	private List<String> customIntegration;
	private List<String> endpoint;
	private List<String> gitWebhook;
	private List<String> dockerWebhook;
	private List<String> gerritTrigger;
	private List<String> gerritListener;

	public List<String> getPipeline() {
		return pipeline;
	}

	public void setPipeline(List<String> pipeline) {
		this.pipeline = pipeline;
	}

	public List<String> getVariable() {
		return variable == null ? variable = new ArrayList<>() : variable;
	}

	public void setVariable(List<String> variable) {
		this.variable = variable;
	}

	public List<String> getCustomIntegration() {
		return customIntegration;
	}

	public void setCustomIntegration(List<String> customIntegration) {
		this.customIntegration = customIntegration;
	}

	public List<String> getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(List<String> endpoint) {
		this.endpoint = endpoint;
	}

	public List<String> getGitWebhook() {
		return gitWebhook;
	}

	public void setGitWebhook(List<String> gitWebhook) {
		this.gitWebhook = gitWebhook;
	}

	public List<String> getDockerWebhook() {
		return dockerWebhook;
	}

	public void setDockerWebhook(List<String> dockerWebhook) {
		this.dockerWebhook = dockerWebhook;
	}

	public List<String> getGerritTrigger() {
		return gerritTrigger;
	}

	public void setGerritTrigger(List<String> gerritTrigger) {
		this.gerritTrigger = gerritTrigger;
	}

	public List<String> getGerritListener() {
		return gerritListener;
	}

	public void setGerritListener(List<String> gerritListener) {
		this.gerritListener = gerritListener;
	}

	public List<String> getMembersForType(CsPackageMemberType type) {
		switch (type) {
			case PIPELINE:
				return getPipeline();
			case VARIABLE:
				return getVariable();
			case CUSTOM_INTEGRATION:
				return getCustomIntegration();
			case ENDPOINT:
				return getEndpoint();
			case GIT_WEBHOOK:
				return getGitWebhook();
			case DOCKER_WEBHOOK:
				return getDockerWebhook();
			case GERRIT_TRIGGER:
				return getGerritTrigger();
			case GERRIT_LISTENER:
				return getGerritListener();
			default:
				throw new RuntimeException("ContentType is not supported!");
		}
	}

	public boolean hasNativeContent() {
		return Arrays.stream(CsPackageMemberType.values()).filter(type -> type.isNativeContent()).anyMatch(type -> {
			List<String> memberNames = this.getMembersForType(type);
			return memberNames != null && !memberNames.isEmpty();
		});
	}

	public static CsPackageDescriptor getInstance(File filesystemPath) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
		try {
			CsPackageDescriptor pkgDescriptor = mapper.readValue(filesystemPath, CsPackageDescriptor.class);
			logger.debug(ReflectionToStringBuilder.toString(pkgDescriptor, ToStringStyle.MULTI_LINE_STYLE));
			return pkgDescriptor;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load CS Package Descriptor[" + filesystemPath.getAbsolutePath() + "]",
					e);
		}
	}

	public static CsPackageDescriptor getInstance(CsPackageContent content) {
		HashMap<CsPackageContent.ContentType, List<String>> map = new HashMap<>();
		for (CsPackageContent.ContentType type : CsPackageContent.ContentType.values()) {
			map.put(type, new ArrayList<>());
		}

		for (Content c : content.getContent()) {
			map.get(c.getType()).add(c.getName());
		}

		CsPackageDescriptor pd = new CsPackageDescriptor();
		pd.pipeline = map.get(CsPackageContent.ContentType.PIPELINE);
		logger.debug(ReflectionToStringBuilder.toString(pd, ToStringStyle.MULTI_LINE_STYLE));
		return pd;
	}
}
