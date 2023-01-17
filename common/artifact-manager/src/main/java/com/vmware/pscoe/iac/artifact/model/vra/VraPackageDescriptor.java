package com.vmware.pscoe.iac.artifact.model.vra;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vmware.pscoe.iac.artifact.model.PackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;

public class VraPackageDescriptor extends PackageDescriptor {

    private List<String> propertyGroup;
    private List<String> propertyDefinition;
    private List<String> softwareComponent;
    private List<String> compositeBlueprint;
    private List<String> xaasBlueprint;
    private List<String> xaasResourceAction;
    private List<String> xaasResourceType;
    private List<String> xaasResourceMapping;
    private List<String> componentProfileValue;
    private List<String> workflowSubscription;
    private List<String> globalPropertyDefinition;
    private List<String> globalPropertyGroup;

    public List<String> getPropertyGroup() {
        return propertyGroup;
    }

    public void setPropertyGroup(List<String> propertyGroup) {
        this.propertyGroup = propertyGroup;
    }

    public List<String> getPropertyDefinition() {
        return propertyDefinition;
    }

    public void setPropertyDefinition(List<String> propertyDefinition) {
        this.propertyDefinition = propertyDefinition;
    }

    public List<String> getSoftwareComponent() {
        return softwareComponent;
    }

    public void setSoftwareComponent(List<String> softwareComponent) {
        this.softwareComponent = softwareComponent;
    }

    public List<String> getCompositeBlueprint() {
        return compositeBlueprint;
    }

    public void setCompositeBlueprint(List<String> compositeBlueprint) {
        this.compositeBlueprint = compositeBlueprint;
    }

    public List<String> getXaasBlueprint() {
        return xaasBlueprint;
    }

    public void setXaasBlueprint(List<String> xaasBlueprint) {
        this.xaasBlueprint = xaasBlueprint;
    }

    public List<String> getXaasResourceAction() {
        return xaasResourceAction;
    }

    public void setXaasResourceAction(List<String> xaasResourceAction) {
        this.xaasResourceAction = xaasResourceAction;
    }

    public List<String> getXaasResourceType() {
        return xaasResourceType;
    }

    public void setXaasResourceType(List<String> xaasResourceType) {
        this.xaasResourceType = xaasResourceType;
    }

    public List<String> getXaasResourceMapping() {
        return xaasResourceMapping;
    }

    public void setXaasResourceMapping(List<String> xaasResourceMapping) {
        this.xaasResourceMapping = xaasResourceMapping;
    }

    public List<String> getComponentProfileValue() {
        return componentProfileValue;
    }

    public void setComponentProfileValue(List<String> componentProfileValue) {
        this.componentProfileValue = componentProfileValue;
    }

    public List<String> getWorkflowSubscription() {
        return workflowSubscription;
    }

    public void setWorkflowSubscription(List<String> workflowSubscription) {
        this.workflowSubscription = workflowSubscription;
    }

    public List<String> getGlobalPropertyDefinition() {
        return globalPropertyDefinition;
    }

    public void setGlobalPropertyDefinition(List<String> globalPropertyDefinition) {
        this.globalPropertyDefinition = globalPropertyDefinition;
    }

    public List<String> getGlobalPropertyGroup() {
        return globalPropertyGroup;
    }

    public void setGlobalPropertyGroup(List<String> globalPropertyGroup) {
        this.globalPropertyGroup = globalPropertyGroup;
    }

    public List<String> getMembersForType(VraPackageMemberType type) {
        switch (type) {
        case COMPOSITE_BLUEPRINT:
            return getCompositeBlueprint();
        case PROPERTY_DEFINITION:
            return getPropertyDefinition();
        case PROPERTY_GROUP:
            return getPropertyGroup();
        case SOFTWARE_COMPONENT:
            return getSoftwareComponent();
        case XAAS_BLUEPRINT:
            return getXaasBlueprint();
        case XAAS_RESOURCE_ACTION:
            return getXaasResourceAction();
        case XAAS_RESOURCE_MAPPING:
            return getXaasResourceMapping();
        case XAAS_RESOURCE_TYPE:
            return getXaasResourceType();
        case COMPONENT_PROFILE_VALUE:
            return getComponentProfileValue();
        case WORKFLOW_SUBSCRIPTION:
            return getWorkflowSubscription();
        case GLOBAL_PROPERTY_DEFINITION:
            return getGlobalPropertyDefinition();
        case GLOBAL_PROPERTY_GROUP:
            return getGlobalPropertyGroup();
        default:
            throw new RuntimeException("ContentType is not supported!");
        }
    }

    public boolean hasNativeContent() {
        return Arrays.stream(VraPackageMemberType.values())
            .filter(type -> type.isNativeContent())
            .anyMatch(type -> {
                List<String> memberNames = this.getMembersForType(type);
                return memberNames != null && !memberNames.isEmpty();
            });
    }

    public static VraPackageDescriptor getInstance(File filesystemPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        try {
            return mapper.readValue(filesystemPath, VraPackageDescriptor.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to load vRA Package Descriptor[" + filesystemPath.getAbsolutePath() + "]", e);
        }
    }

    public static VraPackageDescriptor getInstance(VraPackageContent content) {
        HashMap<VraPackageContent.ContentType, List<String>> map = new HashMap<>();
        for (VraPackageContent.ContentType type : VraPackageContent.ContentType.values()) {
            map.put(type, new ArrayList<>());
        }

        for (Content c : content.getContent()) {
            map.get(c.getType()).add(c.getName());
        }

        VraPackageDescriptor pd = new VraPackageDescriptor();
        pd.propertyGroup = map.get(VraPackageContent.ContentType.PROPERTY_GROUP);
        pd.propertyDefinition = map.get(VraPackageContent.ContentType.PROPERTY_DICTIONARY);
        pd.softwareComponent = map.get(VraPackageContent.ContentType.SOFTWARE_COMPONENT);
        pd.compositeBlueprint = map.get(VraPackageContent.ContentType.COMPOSITE_BLUEPRINT);
        pd.xaasBlueprint = map.get(VraPackageContent.ContentType.XAAS_BLUEPRINT);
        pd.xaasResourceAction = map.get(VraPackageContent.ContentType.XAAS_RESOURCE_ACTION);
        pd.xaasResourceType = map.get(VraPackageContent.ContentType.XAAS_RESOURCE_TYPE);
        pd.xaasResourceMapping = map.get(VraPackageContent.ContentType.XAAS_RESOURCE_MAPPING);
        pd.workflowSubscription = map.get(VraPackageContent.ContentType.WORKFLOW_SUBSCRIPTION);
        pd.globalPropertyDefinition = map.get(VraPackageContent.ContentType.GLOBAL_PROPERTY_DEFINITION);
        pd.globalPropertyGroup = map.get(VraPackageContent.ContentType.GLOBAL_PROPERTY_GROUP);

        // Not supported
        // pd.componentProfileValue = map.get(VraPackageContent.ContentType);

        return pd;
    }

}
