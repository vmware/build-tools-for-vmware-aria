package com.vmware.pscoe.iac.artifact.store.cs;

/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.vmware.pscoe.iac.artifact.model.cs.CsPackageMemberType;
import com.vmware.pscoe.iac.artifact.rest.model.cs.Variable;


public class CsVariableStore extends AbstractCsStore {
	private static final String DIR_VAR = "variables";
	private final Logger logger = LoggerFactory.getLogger(CsPipelineStore.class);

	/**
	* Exporting the contents of variables
	*/
	public void exportContent() {
		List<String> variableNames = this.descriptor.getMembersForType(CsPackageMemberType.VARIABLE);
		if (variableNames != null) {
			this.exportVariables(variableNames);
		} else {
			logger.info("No variables found in content.yaml or exported objects");
		}
	}

	/**
	 * Importing content into vRA target environment
	 * @param sourceDirectory sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File varFolder = Paths.get(sourceDirectory.getPath(), DIR_VAR).toFile();
		if (!varFolder.exists()) {
			return;
		}
		Collection<File> varFiles = FileUtils.listFiles(varFolder, new String[] {"yaml"}, false);
		if (varFiles == null || varFiles.isEmpty()) {
			return;
		}
		importVariables(varFiles);
	}


	private void exportVariables(List<String> variableNames) {
		List<Variable> varList = this.restClient.getProjectVariables()
				.stream()
				.filter(var -> variableNames.contains(var.getName()))
				.peek(var -> var.setProject(null))
				.peek(var -> logger.info("Exporting variable: {}", var.getName()))
				.sorted(Comparator.comparing(Variable::getName))
				.collect(Collectors.toList());
		CsStoreHelper.storeToYamlFile(csPackage.getFilesystemPath(), DIR_VAR, "variables", varList);

	}



	private void importVariables(Collection<File> varFiles) {

		List<Variable> existingVariables = this.restClient.getProjectVariables();
		// Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
		varFiles.stream()
				.map(this::readSingleFile)
				// .map(varFile -> gson.fromJson(readFile(varFile), Variable.class))
				.forEach(vars -> {
					vars.forEach(var -> {
						var.setProject(restClient.getProjectName());
						Optional<Variable> optional = existingVariables
								.stream()
								.filter(ex -> ex.getName().equals(var.getName()))
								.findFirst();
						if (!optional.isPresent()) {
							logger.info(String.format("Create variable '%s'", var.getName()));
							restClient.createVariable(var);
							return;
						}
						Variable existingVar = optional.get();
						if (existingVar.getType().equals(var.getType())
								&& existingVar.getDescription().equals(var.getDescription())) {
							logger.info(String.format("No updates for variable '%s'", existingVar.getName()));
							return;
						}
						if (!existingVar.getType().equals("REGULAR")) {
							logger.warn(String.format("Change of SECRET/RESTRICTED variable '%s'. Secret value is lost.", existingVar
									.getName()));
						}

						var.setValue(existingVar.getValue());
						logger.info(String.format("Update variable '%s'", var.getName()));
						restClient.updateVariable(var);


					});
				});
	}

	private List<Variable> readSingleFile(File varFile) {
		YAMLMapper yamlMapper = new YAMLMapper();
		try {
			return Arrays.asList(yamlMapper.readValue(varFile, Variable[].class));
		} catch (IOException e) {
			throw new RuntimeException("Error reading variables file.", e);
		}
	}

}
