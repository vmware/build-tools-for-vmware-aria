package com.vmware.pscoe.iac.artifact.model.vrops;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VropsPackageContent extends PackageContent<VropsPackageContent.ContentType> {

	public enum ContentType implements PackageContent.ContentType {
        VIEW, DASHBOARD, REPORT, ALERT_DEFINITION, SYMPTOM_DEFINITION, POLICY, SUPERMETRIC, RECOMMENDATION, METRICCONFIG
	}

	public VropsPackageContent(List<Content<ContentType>> content) {
		super(content);
	}

}
