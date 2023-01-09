package com.vmware.pscoe.iac.artifact.helpers.stubs;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgBlueprint;

public class BlueprintMockBuilder {
	private String id;
	private final String name;
	private final String contentSourceId = "mockedContentSourceId";
	private final String description = "mockedDescription";
	private boolean	requestScopeOrg;

	public BlueprintMockBuilder(String name) {
		this.name = name;
		this.id = name + "_id";
	}

	public BlueprintMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public BlueprintMockBuilder setRequestScopeOrg(boolean requestScopeOrg) {
		this.requestScopeOrg = requestScopeOrg;
		return this;
	}

	/**
	 * Return a Blueprint with mocked id, source id and description
	 *
	 * @return VraNgBlueprint
	 */
	public VraNgBlueprint build() {
		return new VraNgBlueprint(this.id, this.name, this.contentSourceId, this.description, this.requestScopeOrg);
	}
}
