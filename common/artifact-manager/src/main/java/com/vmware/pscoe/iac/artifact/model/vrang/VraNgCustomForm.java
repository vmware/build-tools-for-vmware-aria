package com.vmware.pscoe.iac.artifact.model.vrang;

public class VraNgCustomForm {
    private String id;
    private String name;
    private String form;
    private String styles;
    private String sourceType;
    private String type;
    private String status;
    private String formFormat;

    public VraNgCustomForm(String id, String name, String form, String styles, String sourceType, String type, String status, String formFormat) {
        this.id = id;
        this.name = name;
        this.form = form;
        this.styles = styles;
        this.sourceType = sourceType;
        this.type = type;
        this.status = status;
        this.formFormat = formFormat;
    }

    public String getId() {
        return this.id;
    }

	public String getName() {
		return this.name;
	}

    public String getForm() {
		return this.form;
    }
    
    public String getStyles() {
		return this.styles;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public String getType() {
        return this.type;
    }
    
    public String getStatus() {
		return this.status;
    }
    
    public String getFormFormat() {
		return this.formFormat;
	}
    
    public void setForm(String form) {
    	this.form = form;		
	}
}
