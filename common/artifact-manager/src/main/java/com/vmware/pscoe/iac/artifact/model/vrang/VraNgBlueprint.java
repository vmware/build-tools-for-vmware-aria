package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgBlueprint {

    private String id;
    private String name;
    private String content;
    private String description;
    private Boolean requestScopeOrg;

    public VraNgBlueprint(String id, String name, String content, String description, Boolean requestScopeOrg) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.description = description;
        this.requestScopeOrg = requestScopeOrg;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
        return this.description;
    }

    public Boolean getRequestScopeOrg() {
        return this.requestScopeOrg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        VraNgBlueprint other = (VraNgBlueprint) obj;
        return this.id.equals(other.getId());
    }
}
