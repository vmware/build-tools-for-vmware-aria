package com.vmware.pscoe.iac.artifact.model.vrops;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;

public class VropsPackageDescriptor extends PackageDescriptor {
    private List<String> view;
    private List<String> dashboard;
    private List<String> report;
    private List<String> alertDefinition;
    private List<String> symptomDefinition;
    private List<String> policy;
    private List<String> recommendation;
    private List<String> supermetric;
    private List<String> metricConfig;
    private List<String> customGroup;

    public List<String> getView() {
        return this.view;
    }

    public void setView(List<String> view) {
        this.view = view;
    }

    public List<String> getDashboard() {
        return this.dashboard;
    }

    public void setDashboard(List<String> dashboard) {
        this.dashboard = dashboard;
    }

    public List<String> getReport() {
        return this.report;
    }

    public void setReport(List<String> report) {
        this.report = report;
    }

    public List<String> getAlertDefinition() {
        return this.alertDefinition;
    }

    public void setAlertDefinition(List<String> alertDefinition) {
        this.alertDefinition = alertDefinition;
    }

    public List<String> getSymptomDefinition() {
        return this.symptomDefinition;
    }

    public void setSymptomDefinition(List<String> symptomDefinition) {
        this.symptomDefinition = symptomDefinition;
    }

    public List<String> getPolicy() {
        return this.policy;
    }

    public void setPolicy(List<String> policy) {
        this.policy = policy;
    }
    
    public List<String> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(List<String> recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getSuperMetric() {
        return this.supermetric;
    }

    public void setSuperMetric(List<String> supermetric) {
        this.supermetric = supermetric;
    }

    public List<String> getMetricConfig() {
        return this.metricConfig;
    }

    public void setMetricConfig(List<String> metricConfig) {
        this.metricConfig = metricConfig;
    }

    public List<String> getCustomGroup() {
        return this.customGroup;
    }

    public void setCustomGroup(List<String> customGroup) {
        this.customGroup = customGroup;
    }

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
