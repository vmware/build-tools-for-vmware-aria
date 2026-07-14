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
package com.vmware.pscoe.iac.artifact.aria.automation.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgContentSourceType;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.aria.automation.models.VraNgCustomFormAndData;
import com.vmware.pscoe.iac.artifact.common.store.Package;

/**
 * Responsible for exporting/importing catalog items' icons and custom forms.
 *
 * NOTE: When reading through this file blueprint and Custom Form are one and
 * the same
 */
public class VraNgCatalogItemStore812 extends VraNgCatalogItemStore {

	// =================================================
	// UTILITY FORMATTERS AND MIGRATORS
	// =================================================

	/**
	 * Expands minified one-liner CSS rulesets into a standardized,
	 * reader-friendly layout block with clear 4-space indentations.
	 * 
	 * @param css Raw minified string
	 * @return Formatted multi-line stylesheet text
	 */
	private String formatCss(String css) {
		if (StringUtils.isBlank(css)) {
			return "";
		}
		String tokenized = css.replaceAll("\\s+", " ").trim();
		return tokenized
				.replaceAll("\\s*\\{\\s*", " {\n    ")
				.replaceAll(";\\s*", ";\n    ")
				.replaceAll("\\s*\\}\\s*", "\n}\n\n")
				.trim();
	}

	/**
	 * DECOUPLED STATE MIGRATOR: Executes independent rulesets for form definitions
	 * and CSS styles to ensure master file sanitation and fallback file creation.
	 */
	private void migrateLegacyFormFiles(File jsonFormFile, File jsonFormDataFile, File jsonFormStylesFile) {
		if (jsonFormFile == null || !jsonFormFile.exists()) {
			return;
		}

		try {
			String content = FileUtils.readFileToString(jsonFormFile, StandardCharsets.UTF_8);
			if (StringUtils.isBlank(content)) {
				return;
			}

			JsonElement root = JsonParser.parseString(content);
			if (!root.isJsonObject()) {
				return;
			}

			JsonObject jsonFormObj = root.getAsJsonObject();
			boolean mutated = false;
			Gson prettyGson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

			// =================================================
			// 1. FORM SCHEMA LOGIC (ISOLATED)
			// =================================================
			if (jsonFormDataFile.exists()) {
				if (jsonFormObj.has("form")) {
					jsonFormObj.remove("form");
					mutated = true;
					logger.debug("Master file clean: Removed legacy 'form' property since __FormData exists.");
				}
			} else {
				if (jsonFormObj.has("form") && !jsonFormObj.get("form").isJsonNull()) {
					JsonElement formEl = jsonFormObj.get("form");
					JsonElement parsedForm = formEl;

					if (formEl.isJsonPrimitive() && formEl.getAsJsonPrimitive().isString()) {
						String formStr = formEl.getAsString();
						if (StringUtils.isNotBlank(formStr)) {
							parsedForm = JsonParser.parseString(formStr);
						}
					}
					FileUtils.writeStringToFile(jsonFormDataFile, prettyGson.toJson(parsedForm),
							StandardCharsets.UTF_8);
					logger.info("Migration Action: Created missing __FormData from master property: {}",
							jsonFormDataFile.getName());
				} else {
					// Fallback: Property missing -> Create empty JSON object file
					FileUtils.writeStringToFile(jsonFormDataFile, "{}", StandardCharsets.UTF_8);
					logger.info("Migration Action: Form property missing. Created empty object fallback: {}",
							jsonFormDataFile.getName());
				}

				if (jsonFormObj.has("form")) {
					jsonFormObj.remove("form");
					mutated = true;
				}
			}

			// =================================================
			// 2. CSS STYLES LOGIC (ISOLATED)
			// =================================================
			if (jsonFormStylesFile.exists()) {
				if (jsonFormObj.has("styles")) {
					jsonFormObj.remove("styles");
					mutated = true;
				}
				if (jsonFormObj.has("formStyles")) {
					jsonFormObj.remove("formStyles");
					mutated = true;
				}
				logger.debug("Master file clean: Removed legacy style properties since __FormStyles sheet exists.");
			} else {
				String extractedStyles = null;

				if (jsonFormObj.has("styles") && !jsonFormObj.get("styles").isJsonNull()) {
					extractedStyles = jsonFormObj.get("styles").getAsString();
				} else if (jsonFormObj.has("formStyles") && !jsonFormObj.get("formStyles").isJsonNull()) {
					extractedStyles = jsonFormObj.get("formStyles").getAsString();
				}

				if (extractedStyles != null) {
					FileUtils.writeStringToFile(jsonFormStylesFile, formatCss(extractedStyles), StandardCharsets.UTF_8);
					logger.info("Migration Action: Created missing __FormStyles from master property: {}",
							jsonFormStylesFile.getName());
				} else {
					// Fallback: Property missing -> Create empty layout stylesheet
					FileUtils.writeStringToFile(jsonFormStylesFile, "", StandardCharsets.UTF_8);
					logger.info("Migration Action: Style property missing. Created empty stylesheet fallback: {}",
							jsonFormStylesFile.getName());
				}

				if (jsonFormObj.has("styles")) {
					jsonFormObj.remove("styles");
					mutated = true;
				}
				if (jsonFormObj.has("formStyles")) {
					jsonFormObj.remove("formStyles");
					mutated = true;
				}
			}

			// =================================================
			// 3. PERSIST SANITIZED MASTER FILE
			// =================================================
			if (mutated) {
				FileUtils.writeStringToFile(jsonFormFile, prettyGson.toJson(jsonFormObj), StandardCharsets.UTF_8);
				logger.info("Migration Action: Successfully flushed clean sanitized master structure to disk: {}",
						jsonFormFile.getName());
			}

		} catch (Exception e) {
			logger.error("Failed executing decoupled migration logic for layout components matching: "
					+ jsonFormFile.getAbsolutePath(), e);
		}
	}

	// =================================================
	// CATALOG ITEMS EXPORT (PULL)
	// =================================================

	/**
	 * Stores a custom form on the file system.
	 * The Form is returned, so we can set it's id in the catalog item
	 * Stores the forms under {{catalog_item_dir}}/forms
	 *
	 * @param serverPackage server package
	 * @param catalogItem   catalog item
	 *
	 * @return VraNgCustomForm
	 */
	@Override
	protected VraNgCustomForm storeCustomFormOnFileSystem(final Package serverPackage,
			final VraNgCatalogItem catalogItem) {
		VraNgContentSourceBase contentSource = this.restClient.getContentSource(catalogItem.getSourceId());
		if (contentSource == null) {
			throw new RuntimeException(String.format("Content source %s does not exist", catalogItem.getSourceId()));
		}

		logger.debug("Found content source '{}'", contentSource.getName());
		VraNgCustomForm form = this.restClient.getCustomFormByTypeAndSource(contentSource.getType().toString(),
				catalogItem.getId());
		if (form == null) {
			logger.debug("No form found for catalogItem: {} with source {}", catalogItem.getName(),
					catalogItem.getSourceName());
			return null;
		}

		File store = new File(serverPackage.getFilesystemPath());
		File customFormFile = Paths.get(store.getPath(), VraNgDirs.DIR_CATALOG_ITEMS, CUSTOM_FORMS_SUBDIR,
				(getName(catalogItem) + CUSTOM_RESOURCE_SUFFIX)).toFile();
		File customFormDataFile = Paths.get(store.getPath(), VraNgDirs.DIR_CATALOG_ITEMS, CUSTOM_FORMS_SUBDIR,
				(getName(catalogItem) + CATALOG_ITEM_SEPARATOR + CUSTOM_FORM_DATA_SUFFIX + CUSTOM_RESOURCE_SUFFIX))
				.toFile();
		File customFormStylesFile = Paths.get(store.getPath(), VraNgDirs.DIR_CATALOG_ITEMS, CUSTOM_FORMS_SUBDIR,
				(getName(catalogItem) + CATALOG_ITEM_SEPARATOR + CUSTOM_FORM_STYLES_SUFFIX + CSS_RESOURCE_SUFFIX))
				.toFile();

		if (!customFormFile.getParentFile().isDirectory() && !customFormFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", customFormFile.getParentFile().getAbsolutePath());
		}

		this.migrateLegacyFormFiles(customFormFile, customFormDataFile, customFormStylesFile);

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();

			String stylesContent = form.getStyles();
			if (StringUtils.isNotBlank(stylesContent)) {
				FileUtils.writeStringToFile(customFormStylesFile, formatCss(stylesContent), StandardCharsets.UTF_8);
				logger.info("Extracted form custom styles directly to companion sheet: {}",
						customFormStylesFile.getName());
			} else {
				FileUtils.writeStringToFile(customFormStylesFile, "", StandardCharsets.UTF_8);
				logger.info("No custom styles found. Generated an empty companion sheet: {}",
						customFormStylesFile.getName());
			}

			if (!StringUtils.isEmpty(form.getForm())) {
				JsonElement je = JsonParser.parseString(form.getForm());

				if (je.isJsonObject()) {
					JsonObject jo = je.getAsJsonObject();
					if (jo.has("styles") && !jo.get("styles").isJsonNull()) {
						String embeddedStyles = jo.get("styles").getAsString();
						if (StringUtils.isNotBlank(embeddedStyles) && StringUtils.isBlank(stylesContent)) {
							FileUtils.writeStringToFile(customFormStylesFile, formatCss(embeddedStyles),
									StandardCharsets.UTF_8);
							logger.info("Extracted nested form custom styles to companion sheet: {}",
									customFormStylesFile.getName());
						}
						jo.remove("styles");
					}
				}

				logger.info("Created custom form data file {}",
						Files.write(Paths.get(customFormDataFile.getPath()),
								gson.toJson(je).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
								StandardOpenOption.TRUNCATE_EXISTING));
			}

			VraNgCustomFormAndData repoForm = new VraNgCustomFormAndData(form);
			JsonElement repoFormElement = gson.toJsonTree(repoForm);

			if (repoFormElement.isJsonObject()) {
				JsonObject repoFormObj = repoFormElement.getAsJsonObject();
				repoFormObj.remove("form");
				repoFormObj.remove("styles");

				logger.info("Created custom form metadata file {}",
						Files.write(Paths.get(customFormFile.getPath()),
								gson.toJson(repoFormObj).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
								StandardOpenOption.TRUNCATE_EXISTING));
			}

		} catch (IOException e) {
			logger.error("Unable to create custom form {}", customFormFile.getAbsolutePath());
			throw new RuntimeException(
					String.format("Unable to store custom form to file %s.", customFormFile.getAbsolutePath()), e);
		}

		return form;
	}

	/**
	 * Sorts the versions of the catalog items blueprints and returns the latest one
	 * sorted by date.
	 *
	 * @param catalogItem catalog item
	 * @return JsonElement
	 */
	private JsonElement getCatalogItemLatestVersion(final VraNgCatalogItem catalogItem) {
		JsonArray versions = this.restClient.getCatalogItemVersions(catalogItem.getId());
		ArrayList<JsonElement> newList = new ArrayList<>();

		try {
			versions.forEach(newList::add);
			newList.sort((one, two) -> {
				String creationDateOne = one.getAsJsonObject().get("createdAt").toString().replaceAll("^\"|\"$", "");
				String creationDateTwo = two.getAsJsonObject().get("createdAt").toString().replaceAll("^\"|\"$", "");
				return Instant.parse(creationDateOne).compareTo(Instant.parse(creationDateTwo));
			});
		} catch (NullPointerException npe) {
			logger.error("Provided versions array is null");
		}

		return newList.isEmpty() ? null : newList.get(newList.size() - 1);
	}

	// =================================================
	// CATALOG ITEMS IMPORT (PUSH)
	// =================================================

	/**
	 * Import a custom form given the catalog item.
	 * ( form Id is extracted from the item and the fs is queried )
	 *
	 * @param catalogItem       catalog item
	 * @param catalogItemFolder catalog item folder
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

		if (this.waitForCatalogItemToAppear(catalogItem.getName())) {
			if (catalogItem.getType().equals(VraNgContentSourceType.BLUEPRINT)) {
				JsonElement version = this.getCatalogItemLatestVersion(catalogItem);
				String versionName = "";
				if (version != null) {
					JsonObject versionObj = version.getAsJsonObject();
					if (versionObj != null && versionObj.get("id") != null) {
						versionName = versionObj.get("id").getAsString();
					}
				}
				String newFormName = catalogItem.getId() + CATALOG_ITEM_BLUEPRINT_VERSION_NAME_SEPARATOR + versionName;
				String newSourceId = catalogItem.getId() + CATALOG_ITEM_BLUEPRINT_VERSION_SOURCE_ID_SEPARATOR
						+ versionName;

				customForm.setSourceId(newSourceId);
				customForm.setSourceType(BLUEPRINT_VERSION);
				customForm.setName(newFormName);
				logger.info("Importing custom form for blueprint version '{}'", customForm.getName());
				this.restClient.importCustomForm(customForm, customForm.getSourceId());
			} else {
				logger.info("Importing custom form '{}'", customForm.getName());
				this.restClient.importCustomForm(customForm, catalogItem.getId());
			}
		} else {
			logger.warn("Custom form with name '{} (type {})' is missing from vRA content source.",
					customForm.getName(), customForm.getSourceType());
			throw new RuntimeException(String.format("Failed to import Custom Form: %s", customForm.getName()));
		}
	}

	@Override
	protected VraNgCustomForm jsonFileToVraCustomForm(final File jsonFormFile, final File jsonFormDataFile) {
		logger.debug("Converting custom form file to VraNgCustomForm. Name: '{}'", jsonFormFile.getName());

		File jsonFormStylesFile = new File(jsonFormDataFile.getParentFile(),
				FilenameUtils.getBaseName(jsonFormDataFile.getName())
						.replace(CUSTOM_FORM_DATA_SUFFIX, CUSTOM_FORM_STYLES_SUFFIX) + CSS_RESOURCE_SUFFIX);

		// Step 1: Fire dynamic checking layer before reconstructing runtime model
		// fields
		this.migrateLegacyFormFiles(jsonFormFile, jsonFormDataFile, jsonFormStylesFile);

		VraNgCustomFormAndData repoForm;
		VraNgCustomForm restForm;

		try (JsonReader reader = new JsonReader(
				new InputStreamReader(new FileInputStream(jsonFormFile.getPath()), StandardCharsets.UTF_8))) {
			repoForm = new Gson().fromJson(reader, VraNgCustomFormAndData.class);
			restForm = new VraNgCustomForm(repoForm);

			// Step 2: Read verified companion form block directly into model definition
			if (jsonFormDataFile.exists()) {
				String formDataContent = FileUtils.readFileToString(jsonFormDataFile, StandardCharsets.UTF_8);

				if (StringUtils.isBlank(formDataContent)) {
					restForm.setForm(null);
				} else {
					JsonElement je = JsonParser.parseString(formDataContent);
					restForm.setForm(new Gson().toJson(je));
				}
			} else {
				restForm.setForm(null);
			}

			// Step 3: Read verified companion stylesheet layout values directly into
			// outbound properties
			if (jsonFormStylesFile.exists()) {
				String cssContent = FileUtils.readFileToString(jsonFormStylesFile, StandardCharsets.UTF_8);

				if (StringUtils.isBlank(cssContent)) {
					restForm.setStyles(null);
				} else {
					restForm.setStyles(cssContent);
					logger.info("Successfully appended companion custom styles from '{}' into payload properties.",
							jsonFormStylesFile.getName());
				}
			} else {
				restForm.setStyles(null);
			}

			return restForm;
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFormFile.getPath()), e);
		}
	}
}