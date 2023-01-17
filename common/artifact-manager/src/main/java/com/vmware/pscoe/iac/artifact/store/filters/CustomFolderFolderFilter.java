package com.vmware.pscoe.iac.artifact.store.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class CustomFolderFolderFilter implements FilenameFilter {
	private final List<String> descriptionNames;
	private final Logger logger;

	public CustomFolderFolderFilter(List<String> descriptionNames) {
		super();
		this.descriptionNames = descriptionNames;
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * Method that filters files based on what is defined in the content.yaml.
	 * Walks trough the files in a specific directory and decides if the the file should be chosen or not
	 * 
	 * @param dir File to filter
	 * @param name name of the file
	 * @return boolean
	 */
	@Override
	public boolean accept(File dir, String name) {
		logger.debug("Process file for filtering '{}/{}'", dir.getAbsolutePath(), name);
		String items = descriptionNames != null ? String.join(", ", descriptionNames) : "NULL";
		logger.debug("Items in descriptor (content.yaml): {}", items);
		File currentFile = new File(dir, name);
		if (!currentFile.isDirectory()) {
			return false;
		}
		return descriptionNames == null || descriptionNames.contains(name);
	}
}
