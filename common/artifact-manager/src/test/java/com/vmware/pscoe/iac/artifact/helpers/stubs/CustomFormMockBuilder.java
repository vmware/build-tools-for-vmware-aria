package com.vmware.pscoe.iac.artifact.helpers.stubs;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;

public class CustomFormMockBuilder {
	private String id = "formId";
	private String name;


	/**
	 * Returns a Custom form mock with form id set to "formId" and the given name
	 *
	 * @param	name
	 *
	 * @return	CustomFormMockBuilder
	 */
	public CustomFormMockBuilder(String name){
		this.name	= name;
	}

	public CustomFormMockBuilder setId(String id){
		this.id = id;
		return this;
	}
  

	/**
	 * Returns a Custom form mock.
	 *
	 * @param	name
	 *
	 * @return	VraNgCustomForm
	 */
	public VraNgCustomForm build() {
		return new VraNgCustomForm( this.id, this.name, "{}", null, "workflow", null, null, null );
	}

}
