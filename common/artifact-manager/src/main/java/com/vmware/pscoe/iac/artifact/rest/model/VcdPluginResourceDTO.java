package com.vmware.pscoe.iac.artifact.rest.model;

public class VcdPluginResourceDTO {
    private final String fileName;
    private final long size;

    public VcdPluginResourceDTO(String fileName, long size) {
    	this.fileName = fileName;
    	this.size = size;
    }

	public String getFileName() {
		return fileName;
	}

	public long getSize() {
		return size;
	}
}
