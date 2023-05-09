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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Responsible for exporting/importing catalog items' icons and custom forms.
 *
 * NOTE: When reading through this file blueprint and Custom Form are one and
 * the same
 */
public class VraNgCatalogItemStore812 extends VraNgCatalogItemStore {
	// =================================================
	// CATALOG ITEMS EXPORT
	// =================================================

	/**
	 * Stores a custom form on the file system.
	 * The Form is returned, so we can set it's id in the catalog item
	 * Stores the forms under {{catalog_item_dir}}/forms
	 *
	 * @param serverPackage
	 * @param catalogItem
	 *
	 * @return VraNgCustomForm
	 */
	@Override
	protected VraNgCustomForm storeCustomFormOnFileSystem(final Package serverPackage, final VraNgCatalogItem catalogItem) {
		logger.info(this.getClass().toString());
		VraNgContentSourceBase contentSource = this.restClient.getContentSource(catalogItem.getSourceId());
		if (contentSource == null) {
			throw new RuntimeException(
					String.format("Content source %s does not exist", catalogItem.getSourceId()));
		}

		logger.debug("Found content source '{}'", contentSource.getName());
		VraNgCustomForm form = null;
		if (contentSource.getType().equals(VraNgContentSourceType.BLUEPRINT)) {
			JsonElement version = this.getCatalogItemLatestVersion(catalogItem);
			JsonObject versionObj = version.getAsJsonObject();
			String versionName = versionObj.get("id").getAsString();
			String sourceId = catalogItem.getId() + "/" + versionName;
			JsonElement formId = versionObj.get("formId");
			if (formId != null) {
				form = this.restClient.fetchRequestForm("com.vmw.blueprint.version", sourceId, formId.getAsString());
				catalogItem.setFormId(form.getId());

			} else {
				logger.info(
						"Catalog items '{}'', latest version '{}'', has NO Custom Form. We only pull latest custom form.",
						catalogItem.getName(), versionName);
			}
		} else {
			form = this.restClient.getCustomFormByTypeAndSource(
					contentSource.getType().toString(),
					catalogItem.getId());
		}

		if (form == null) {
			logger.debug(
					"No form found for catalogItem: {} with source {}",
					catalogItem.getName(),
					catalogItem.getSourceName());
			return null;
		}

		File store = new File(serverPackage.getFilesystemPath());
		File customFormFile = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_CATALOG_ITEMS,
				CUSTOM_FORMS_SUBDIR,
				(getName(catalogItem) + CUSTOM_RESOURCE_SUFFIX)).toFile();
		File customFormDataFile = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_CATALOG_ITEMS,
				CUSTOM_FORMS_SUBDIR,
				(getName(catalogItem) + CATALOG_ITEM_SEPARATOR + CUSTOM_FORM_DATA_SUFFIX + CUSTOM_RESOURCE_SUFFIX))
				.toFile();

		if (!customFormFile.getParentFile().isDirectory() && !customFormFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", customFormFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			// write form metadata file
			logger.info("Created custom form metadata file {}",
					Files.write(Paths.get(customFormFile.getPath()), gson.toJson(form).getBytes(),
							StandardOpenOption.CREATE));
			// write form data file
			if (!StringUtils.isEmpty(form.getForm())) {
				JsonElement je = JsonParser.parseString(form.getForm());
				logger.info("Created custom form data file {}",
						Files.write(Paths.get(customFormDataFile.getPath()), gson.toJson(je).getBytes(),
								StandardOpenOption.CREATE));
			}
		} catch (IOException e) {
			logger.error("Unable to create custom form {}", customFormFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store custom form to file %s.", customFormFile.getAbsolutePath()),
					e);
		}

		return form;
	}

	/**
	 * Sorts the versions of the catalog items blueprints and returns the latest one
	 * sorted by date.
	 *
	 * @param catalogItem
	 * @return JsonElement
	 */
	private JsonElement getCatalogItemLatestVersion(final VraNgCatalogItem catalogItem) {
		JsonArray versions = this.restClient.getCatalogItemVersions(catalogItem.getId());
		ArrayList<JsonElement> newList = new ArrayList<>();

		try {
			versions.forEach(newList::add);
			// Implementing a custom comparator for the Versions based on createdAt, so we
			// can guarantee the order of export and therefore import.
			newList.sort((one, two) -> {
				// Extract the createdAt attribute and remove the start and end quote from it
				String creationDateOne = one.getAsJsonObject().get("createdAt").toString()
						.replaceAll("^\"|\"$", "");
				String creationDateTwo = two.getAsJsonObject().get("createdAt").toString()
						.replaceAll("^\"|\"$", "");

				// Date is returned in ISO-8601, e.g. "2022-08-22T14:17:00.073876Z";
				return Instant.parse(creationDateOne).compareTo(Instant.parse(creationDateTwo));
			});
		} catch (NullPointerException npe) {
			logger.error("Provided versions array is null");
		}
		return newList.get(newList.size() - 1);
	}

	// =================================================
	// CATALOG ITEMS IMPORT
	// =================================================

	/**
	 * Import a custom form given the catalog item. 
	 * ( form Id is extracted from the item and the fs is queried )
	 *
	 * @param catalogItem
	 * @param catalogItemFolder
	 */
	@Override
	protected void importCustomForm(final VraNgCatalogItem catalogItem, final File catalogItemFolder) {
		String formName = getName(catalogItem) + CUSTOM_RESOURCE_SUFFIX;
		String formDataName = getName(catalogItem) + CATALOG_ITEM_SEPARATOR + CUSTOM_FORM_DATA_SUFFIX
				+ CUSTOM_RESOURCE_SUFFIX;
		File customFormFile = Paths.get(catalogItemFolder.getPath(), CUSTOM_FORMS_SUBDIR, formName).toFile();
		File customFormDataFile = Paths.get(catalogItemFolder.getPath(), CUSTOM_FORMS_SUBDIR, formDataName).toFile();

		logger.info("Importing custom form: '{}'' with type '{}'", customFormFile.getAbsolutePath(),
				catalogItem.getType());
		VraNgCustomForm customForm = this.jsonFileToVraCustomForm(customFormFile, customFormDataFile);

		// wait 250 ms between each custom form import in order catalog item to be
		// retrievable by the VRA REST API
		if (this.waitForCatalogItemToAppear(catalogItem.getName())) {
			if (catalogItem.getType().equals(VraNgContentSourceType.BLUEPRINT)) {
				JsonElement version = this.getCatalogItemLatestVersion(catalogItem);
				JsonObject versionObj = version.getAsJsonObject();
				String versionName = versionObj.get("id").getAsString();
				String newFormName = catalogItem.getId() + CATALOG_ITEM_BLUEPRINT_VERSION_NAME_SEPARATOR + versionName;
				String newSourceId = catalogItem.getId() + CATALOG_ITEM_BLUEPRINT_VERSION_SOURCE_ID_SEPARATOR + versionName;
				// normalize here to accommodate < 8.12 to >=8.12
				customForm.setSourceId(newSourceId);
				customForm.setSourceType("com.vmw.blueprint.version");
				customForm.setName(newFormName);
				logger.info("Importing custom form for blueprint version '{}'", customForm.getName());
				this.restClient.importCustomForm(customForm, customForm.getSourceId());
			} else {
				logger.info("Importing custom form '{}'", customForm.getName());
				this.restClient.importCustomForm(customForm, catalogItem.getId());
			}
		} else {
			logger.warn(
					"Custom form with name '{} (type {})' is missing from vRA content source.",
					customForm.getName(),
					customForm.getSourceType());

			throw new RuntimeException(String.format("Failed to import Custom Form: %s", customForm.getName()));
		}
	}

}
