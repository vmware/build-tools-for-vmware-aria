package com.vmware.pscoe.iac.artifact.store.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class CustomFolderFileFilter implements FilenameFilter {
	private final List<String> descriptionNames;
	private final Logger logger;

	public CustomFolderFileFilter(List<String> descriptionNames) {
		super();
		this.descriptionNames = descriptionNames;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public boolean accept(File dir, String name) {
		logger.debug("Process file for filtering '{}/{}'", dir.getAbsolutePath(), name);
		String items = descriptionNames != null ? String.join(", ", descriptionNames) : "NULL";
		logger.debug("Items in descriptor (content.yaml): {}", items);
		File currentFile = new File(dir, name);
		if (currentFile.isDirectory()) { //There are cases where in the main item folder, there is a sub-directory, e.g. catalog-item/forms/
			return false;
		}
		return descriptionNames == null || descriptionNames.contains(name.split("\\.")[0]);
	}
}
