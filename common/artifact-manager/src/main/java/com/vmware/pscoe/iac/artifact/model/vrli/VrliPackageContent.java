package com.vmware.pscoe.iac.artifact.model.vrli;

import java.util.List;

import com.vmware.pscoe.iac.artifact.model.PackageContent;

public class VrliPackageContent extends PackageContent<VrliPackageContent.ContentType> {
	public enum ContentType implements PackageContent.ContentType {
        ALERT, CONTENT_PACK
	}

	public VrliPackageContent(List<Content<ContentType>> content) {
		super(content);
	}
}
