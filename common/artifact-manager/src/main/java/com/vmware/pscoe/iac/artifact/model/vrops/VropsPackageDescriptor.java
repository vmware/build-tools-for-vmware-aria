package com.vmware.pscoe.iac.artifact.model.vrops;

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
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

/**
 * VropsPackageDescriptor.
 */
public class VropsPackageDescriptor extends PackageDescriptor {
	/**
	 * view.
	 */
	private List<String> view;
	/**
	 * dashboard.
	 */
	private List<String> dashboard;
	/**
	 * report.
	 */
	private List<String> report;
	/**
	 * alertDefinition.
	 */
	private List<String> alertDefinition;
	/**
	 * alertDefinition.
	 */
	private List<String> symptomDefinition;
	/**
	 * symptomDefinition.
	 */
	private List<String> recommendation;
	/**
	 * supermetric.
	 */
	private List<String> supermetric;
	/**
	 * metricConfig.
	 */
	private List<String> metricConfig;
	/**
	 * customGroup.
	 */
	private List<String> customGroup;
	/**
	 * policy.
	 */
	private List<String> policy;
	/**
	 * defaultPolicy.
	 */
	private String defaultPolicy;

	/**
	 * getView().
	 * 
	 * @return list of views.
	 */
	public List<String> getView() {
		return this.view;
	}

	/**
	 * setView().
	 * 
	 * @param view list of views.
	 */
	public void setView(List<String> view) {
		this.view = view;
	}

	/**
	 * getDashboard().
	 * 
	 * @return list of dashboards.
	 */
	public List<String> getDashboard() {
		return this.dashboard;
	}

	/**
	 * setDashboard().
	 * 
	 * @param dashboard list of dashboards.
	 */
	public void setDashboard(List<String> dashboard) {
		this.dashboard = dashboard;
	}

	/**
	 * getReport().
	 * 
	 * @return list of reports.
	 */
	public List<String> getReport() {
		return this.report;
	}

	/**
	 * setReport().
	 * 
	 * @param report list of reports.
	 */
	public void setReport(List<String> report) {
		this.report = report;
	}

	/**
	 * getAlertDefinition().
	 * 
	 * @return list of alert definitions.
	 */
	public List<String> getAlertDefinition() {
		return this.alertDefinition;
	}

	/**
	 * setAlertDefinition().
	 * 
	 * @param alertDefinition list of alert definitions.
	 */
	public void setAlertDefinition(List<String> alertDefinition) {
		this.alertDefinition = alertDefinition;
	}

	/**
	 * getSymptomDefinition().
	 * 
	 * @return list of symptom definitions.
	 */
	public List<String> getSymptomDefinition() {
		return this.symptomDefinition;
	}

	/**
	 * setSymptomDefinition().
	 * 
	 * @param symptomDefinition list of symptom definitions.
	 */
	public void setSymptomDefinition(List<String> symptomDefinition) {
		this.symptomDefinition = symptomDefinition;
	}

	/**
	 * getPolicy().
	 * 
	 * @return list of policies.
	 */
	public List<String> getPolicy() {
		return policy;
	}

	/**
	 * setPolicy().
	 * 
	 * @param policy list of symptom definitions.
	 */
	public void setPolicy(List<String> policy) {
		this.policy = policy;
	}

	/**
	 * getDefaultPolicy().
	 * 
	 * @return string with default policy.
	 */
	public String getDefaultPolicy() {
		return defaultPolicy;
	}

	/**
	 * setDefaultPolicy().
	 * 
	 * @param defaultPolicy default policy to be set.
	 */
	public void setDefaultPolicy(String defaultPolicy) {
		this.defaultPolicy = defaultPolicy;
	}

	/**
	 * getSupermetric().
	 * 
	 * @return list of super metrics.
	 */
	public List<String> getSupermetric() {
		return supermetric;
	}

	/**
	 * setSupermetric().
	 * 
	 * @param supermetric list of super metrics.
	 */
	public void setSupermetric(List<String> supermetric) {
		this.supermetric = supermetric;
	}

	/**
	 * getRecommendation().
	 * 
	 * @return list of recommendations.
	 */
	public List<String> getRecommendation() {
		return recommendation;
	}

	/**
	 * setRecommendation().
	 * 
	 * @param recommendation list of recommendations.
	 */
	public void setRecommendation(List<String> recommendation) {
		this.recommendation = recommendation;
	}

	/**
	 * getSuperMetric().
	 * 
	 * @return list of super metrics.
	 */
	public List<String> getSuperMetric() {
		return this.supermetric;
	}

	/**
	 * setSuperMetric().
	 * 
	 * @param sm list of super metrics.
	 */
	public void setSuperMetric(List<String> sm) {
		this.supermetric = sm;
	}

	/**
	 * getMetricConfig().
	 * 
	 * @return list of metric configs.
	 */
	public List<String> getMetricConfig() {
		return this.metricConfig;
	}

	/**
	 * setMetricConfig().
	 * 
	 * @param metricConfig of metric configs.
	 */
	public void setMetricConfig(List<String> metricConfig) {
		this.metricConfig = metricConfig;
	}

	/**
	 * getCustomGroup().
	 * 
	 * @return list of custom groups.
	 */
	public List<String> getCustomGroup() {
		return this.customGroup;
	}

	/**
	 * setCustomGroup().
	 * 
	 * @param customGroup of custom groups.
	 */
	public void setCustomGroup(List<String> customGroup) {
		this.customGroup = customGroup;
	}

	/**
	 * Returns the members based on the member type.
	 * 
	 * @param type the type of the vrops package member.
	 * @return List of the members for given member type.
	 */
	public List<String> getMembersForType(VropsPackageMemberType type) {
		switch (type) {
			case VIEW:
				return getView();
			case DASHBOARD:
				return getDashboard();
			case REPORT:
				return getReport();
			case ALERT_DEFINITION:
				return getAlertDefinition();
			case SYMPTOM_DEFINITION:
				return getSymptomDefinition();
			case POLICY:
				return getPolicy();
			case RECOMMENDATION:
				return getRecommendation();
			case SUPERMETRIC:
				return getSuperMetric();
			case METRICCONFIG:
				return getMetricConfig();
			case CUSTOM_GROUP:
				return getCustomGroup();
			default:
				throw new RuntimeException("ContentType is not supported!");
		}
	}

	/**
	 * Returns the vrops package descriptor based on the contents of the
	 * content.yaml file.
	 * 
	 * @param filesystemPath path to the content.yaml file.
	 * @return instance of the descriptor based on the contents of the content.yaml
	 *         file.
	 */
	@SuppressWarnings("deprecation")
	public static VropsPackageDescriptor getInstance(File filesystemPath) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
		try {
			return mapper.readValue(filesystemPath, VropsPackageDescriptor.class);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load vROps Package Descriptor [" + filesystemPath.getAbsolutePath() + "]", e);
		}
	}

}
