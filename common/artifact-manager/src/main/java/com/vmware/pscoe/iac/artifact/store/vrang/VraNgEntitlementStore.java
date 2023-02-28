package com.vmware.pscoe.iac.artifact.store.vrang;

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

import static com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlementType.CATALOG_ITEM_IDENTIFIER;
import static com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlementType.CATALOG_SOURCE_IDENTIFIER;
import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_ENTITLEMENTS;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlement;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogEntitlementType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPackageDescriptor;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;
import com.vmware.pscoe.iac.artifact.rest.RestClientVraNg;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class VraNgEntitlementStore extends AbstractVraNgStore {
    private List<VraNgProject> projects;
    private String configuredProjectId;
    private Map<String, List<VraNgContentSourceBase>> contentSources;
    private Map<String, List<VraNgCatalogItem>> catalogItems;
    private static final String PROJECTS_DELIMITER = ",";

    @Override
    public void init(RestClientVraNg restClient, Package vraNgPackage, ConfigurationVraNg config, VraNgPackageDescriptor vraNgPackageDescriptor) {
        super.init(restClient, vraNgPackage, config, vraNgPackageDescriptor);
        this.projects = this.restClient.getProjects();
        this.configuredProjectId = this.restClient.getProjectId();
    }

    @Override
    public void importContent(File sourceDirectory) {
        this.importCatalogEntitlements(sourceDirectory);
    }

	/**
	 * Used to fetch the store's data from the package descriptor
	 *
	 * @return list of entitlements
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getCatalogEntitlement();
	}

    /**
     * Export all catalog entitlements from a server and store them in YAML files.
     *
     */
	protected void exportStoreContent() {
        List<VraNgCatalogEntitlement> entitlements = this.restClient.getAllCatalogEntitlements();
        entitlements.forEach(entitlement -> storeEntitlementOnFilesystem(vraNgPackage, entitlement));
    }

    /**
     * Export catalog entitlements from a list of entitlement names and store them
     * in YAML files.
     *
     * @param catalogEntitlementNames list of entitlement names to export
     */
    protected void exportStoreContent( List<String> catalogEntitlementNames ) {
        List<VraNgCatalogEntitlement> allEntitlements = this.restClient.getAllCatalogEntitlements();
        catalogEntitlementNames.forEach(name -> {
            // find the catalog entitlement by name on the target system (must be shared
            // with at least 1 project)
            List<VraNgCatalogEntitlement> entitlements = allEntitlements.stream()
                    .filter(item -> item.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
            if (entitlements != null && !entitlements.isEmpty()) {
                entitlements.removeIf(entitlement -> !catalogEntitlementNames.contains(entitlement.getName()));
                entitlements.forEach(entitlement -> storeEntitlementOnFilesystem(vraNgPackage, entitlement));
			} else {
				throw new IllegalStateException(
						String.format(
								"Entitlement [%s] not found on the server.",
								name));
			}
		});
    }

    /**
     * Store entitlement in YAML file. The entitlement representation not tied to
     * the environment allowing portability.
     *
     * @param serverPackage vra package
     * @param entitlement   entitlement representation
     */
    private void storeEntitlementOnFilesystem(Package serverPackage, VraNgCatalogEntitlement entitlement) {
        logger.debug("Storing entitlement {}", entitlement.getName());

        Map<String, String> data = new LinkedHashMap<String, String>();
		data.put("id", entitlement.getId() != null ? entitlement.getId(): "");
		data.put("name", entitlement.getName()!= null ? entitlement.getName() : "");
		data.put("type", entitlement.getType() != null ?entitlement.getType().toString(): VraNgCatalogEntitlementType.DEFAULT.toString());
		data.put("sourceType", entitlement.getSourceType() != null ? entitlement.getSourceType().toString(): VraNgContentSourceType.UNKNOWN.toString());
        data.put("projectId", entitlement.getProjects().stream().collect(Collectors.joining(PROJECTS_DELIMITER)));
        data.put("projectName", entitlement.getProjects().stream().map(this::projectIdToName).collect(Collectors.joining(PROJECTS_DELIMITER)));
        data.put( "iconId", entitlement.getIconId() == null ? null : entitlement.getIconId() );

        DumperOptions options = new DumperOptions();
        options.setExplicitStart(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        StringWriter writer = new StringWriter();
        yaml.dump(data, writer);

        File store = new File(serverPackage.getFilesystemPath());
        File entitlementsFile = Paths.get(store.getPath(), DIR_ENTITLEMENTS, entitlement.getName() + ".yaml").toFile();
        entitlementsFile.getParentFile().mkdirs();

        try {
            Files.write(Paths.get(entitlementsFile.getPath()), writer.toString().getBytes(), StandardOpenOption.CREATE);
            logger.info("Created entitlement file '{}' for entitlement '{}'", entitlementsFile.getAbsoluteFile(),
                    entitlement.getName());
        } catch (IOException e) {
            logger.error("Unable to store entitlement '{}' to file '{}' : {}", entitlement.getName(),
                    entitlementsFile.getPath(), e.getMessage());
            throw new RuntimeException(String.format("Unable to store entitlement '%s' to file '%s'. : %s",
                    entitlement.getName(), entitlementsFile.getName(), e.getMessage()), e);
        }
    }

    private String projectIdToName(String id) {
        return this.projects
                .stream()
                .filter(project -> project.getId().equals(id))
                .map(project -> project.getName())
                .findFirst()
                .orElse(null);
    }

    private String projectNameToId(String name) {
        return this.projects
                .stream()
                .filter(project -> project.getName().equals(name))
                .map(project -> project.getId())
                .findFirst()
                .orElse(null);
    }

 	/**
	 * Import all catalog entitlements from a package.
	 *
	 * @param sourceDirectory temporary directory containing the files
	 */
	private void importCatalogEntitlements(File sourceDirectory) {
		// verify directory
		File entitlementsFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_ENTITLEMENTS).toFile();
		if (!entitlementsFolder.exists()) {
			logger.info("Catalog Entitlements folder is missing '{}' ", VraNgDirs.DIR_ENTITLEMENTS);
			return;
		}
		// collect local state - entitlement files
		// Check if there are any blueprints to import
		File[] localList = this.filterBasedOnConfiguration(entitlementsFolder, new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (localList == null || localList.length == 0) {
			logger.info("No Catalog Entitlements available - skip import");
			return;
		}

		List<VraNgCatalogEntitlement> entitlements = Arrays.stream(localList)
				.map(this::readCatalogEntitlementFromYaml)
				.collect(Collectors.toList());
		List<String> projectIds = entitlements.stream().map(ent -> ent.getProjects())
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
		if (!projectIds.contains(this.configuredProjectId)) {
			projectIds.add(this.configuredProjectId);
		}
		this.contentSources = this.restClient.getContentSourcesForProjects(projectIds);
		this.catalogItems = this.restClient.getCatalogItemsForProjects(projectIds);

		StringBuilder messages = new StringBuilder();
		// import entitlements to VRA
		entitlements.forEach(entitlement -> {
			try {
				// import catalog entitlement
				logger.debug("Importing entitlements '{}'", entitlement.getName());
				this.importCatalogEntitlement(entitlement);
			} catch (RuntimeException e) {
				messages.append(String.format("Unable to import catalog entitlement '%s' : %s", entitlement.getName(),
						e.getMessage()));
			}
		});

		// throw an error if one or more entitlements could not be read / created
		if (messages.length() > 0) {
			throw new RuntimeException(messages.toString());
		}
	}

    /**
     * Deserialize YAML representation of a catalog entitlement
     *
     * @param yamlFile source entitlement file
     * @return an entitlement representation
     * @throws FileNotFoundException
     */
    private VraNgCatalogEntitlement readCatalogEntitlementFromYaml(File yamlFile) {
        Yaml yaml = new Yaml();
        try (FileInputStream file = new FileInputStream(yamlFile)) {
            Map<String, Object> yamlContent = yaml.load(file);

            String id = yamlContent.get("id") == null ? "" : yamlContent.getOrDefault("id", "").toString();
            String name = yamlContent.get("name") == null ? "" : yamlContent.getOrDefault("name", "").toString();
            String projectName = yamlContent.get("projectName") == null ? ""
                    : yamlContent.getOrDefault("projectName", "").toString();
            String projectId = yamlContent.get("projectId") == null ? ""
                    : yamlContent.getOrDefault("projectId", "").toString();
            VraNgCatalogEntitlementType entitlementType = VraNgCatalogEntitlementType
                    .fromString(yamlContent.getOrDefault("type", "").toString());
            VraNgContentSourceType sourceType = VraNgContentSourceType
                    .fromString(yamlContent.getOrDefault("sourceType", "").toString());
            // project name takes precedence over project id
            List<String> projects = StringUtils.isNotEmpty(projectName)
                    ? Arrays.stream(projectName.split(",")).map(this::projectNameToId).filter(prjId -> prjId != null).collect(Collectors.toList())
                    : Arrays.asList(projectId.split(PROJECTS_DELIMITER));

            return new VraNgCatalogEntitlement(id, null, name, projects, entitlementType, sourceType);
        } catch (Exception e) {
            throw new RuntimeException("Error loading entitlement files", e);
        }
    }

    private void importCatalogEntitlement(VraNgCatalogEntitlement entitlement) {
        /*
         * Reconcile remote state using the following rules: 1. If an entitlement with
         * the same name exists remotely - recreate the entitlement 2. If an entitlement
         * with the same name doesn't exist remotely and respective target exists -
         * create the entitlement 3. If an entitlement with the same name doesn't exist
         * remotely and there's no target - do not create the entitlement and log a
         * warning
         */
        if (!entitlement.getProjects().contains(this.configuredProjectId)) {
            List<String> additionalProjects = new ArrayList<>();
            additionalProjects.add(this.configuredProjectId);
            additionalProjects.addAll(entitlement.getProjects());
            entitlement.setProjects(additionalProjects);
        }
        entitlement.setProjects(entitlement
                .getProjects()
                .stream()
                .filter(projectId -> this.projects.stream().anyMatch(proj -> proj.getId().equals(projectId)))
                .collect(Collectors.toList()));

        try {
            switch (entitlement.getType()) {
                case CATALOG_SOURCE_IDENTIFIER: {
                    logger.debug("Importing '{}'", CATALOG_SOURCE_IDENTIFIER);
                    this.importContentSourceEntitlement(entitlement);
                    break;
                }
                case CATALOG_ITEM_IDENTIFIER: {
                    logger.debug("Importing {}", CATALOG_ITEM_IDENTIFIER);
                    this.importCatalogItemEntitlement(entitlement);
                    break;
                }
                default: {
                    logger.warn("Unknown entitlement type '{}'", entitlement.getType());
                    break;
                }
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    private void importContentSourceEntitlement(VraNgCatalogEntitlement entitlement) {
        // fetch existing content sources

        for (String project : entitlement.getProjects()) {
            String projectName = this.projectIdToName(project);
            if (!this.contentSources.containsKey(project)) {
                logger.warn("No catalog source could be found for entitlement '{}' and project '{}'", entitlement.getName(), projectName);
                continue;
            }
            List<VraNgContentSourceBase> contentSourcesPerProject = contentSources.get(project);
            if (contentSourcesPerProject != null) {
                // Definition for contentSourcesPerProject.contains(entitlement.getName())
                boolean entitlementNotExists = contentSourcesPerProject.stream().
                        noneMatch(contentSource -> contentSource.getName().equalsIgnoreCase(entitlement.getName()));
                logger.debug("Entitlement not exists: {}", entitlementNotExists);

                if (entitlementNotExists) {
                    throw new RuntimeException(String.format("The entitlement %s don't exists.", entitlement.getName()));
                }
                logger.debug("contentSourcesPerProject list size: {}", contentSourcesPerProject.size());

                for (VraNgContentSourceBase contentSource : contentSourcesPerProject) {
                    if (contentSource.getName().equalsIgnoreCase(entitlement.getName())) {
                        entitlement.setId(null);
                        entitlement.setSourceId(contentSource.getId());
                        this.restClient.createCatalogEntitlement(entitlement, project);
                        logger.info("Imported catalog source entitlement '{}' for project '{}'", entitlement.getName(), projectName);
                    }
                }
            }
        }

    }

    private void importCatalogItemEntitlement(VraNgCatalogEntitlement entitlement) {
        // fetch existing catalog items

        for (String project : entitlement.getProjects()) {
            String projectName = this.restClient.getProjectNameById(project);
            if (!catalogItems.containsKey(project)) {
                logger.warn("No catalog items could be found for entitlement '{}' and project '{}'",
                        entitlement.getName(), projectName);
                continue;
            }
            List<VraNgCatalogItem> catalogItemsPerProject = catalogItems.get(project);
            if (catalogItemsPerProject != null) {
                for (VraNgCatalogItem catalogItem : catalogItemsPerProject) {
                    if (catalogItem.getName().equalsIgnoreCase(entitlement.getName())) {
                        entitlement.setId(null);
                        entitlement.setSourceId(catalogItem.getId());
                        this.restClient.createCatalogEntitlement(entitlement, project);
                        logger.info("Imported catalog item entitlement '{}' for project '{}'", entitlement.getName(),
                                projectName);
                    }
                }
            }
        }
    }
}
