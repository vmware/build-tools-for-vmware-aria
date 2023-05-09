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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgContentSourceBase;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.store.filters.CustomFolderFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for exporting/importing catalog items' icons and custom forms.
 *
 * NOTE: When reading through this file blueprint and Custom Form are one and
 * the same
 */
public class VraNgCatalogItemStore extends AbstractVraNgStore {

	/**
	 * Suffix that is applied on the name of blueprint catalog item version.
	 */
	protected static final String CATALOG_ITEM_BLUEPRINT_VERSION_NAME_SEPARATOR = " / ";

	/**
	 * Suffix that is applied on the id of blueprint catalog item version.
	 */
	protected static final String CATALOG_ITEM_BLUEPRINT_VERSION_SOURCE_ID_SEPARATOR = "/";

	/**
	 * Separator for the SourceName and the Catalog Item Name.
	 * Used so we can have unique names even if we have
	 * two catalog items with the same names from different sources. You can have a
	 * catalog item with __ in the name
	 * but not Source name with it.
	 */
	protected static final String CATALOG_ITEM_SEPARATOR = "__";

	/**
	 * Sub directory of.
	 * com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs#DIR_CATALOG_ITEMS where
	 * custom forms are stored
	 */
	protected static final String CUSTOM_FORMS_SUBDIR = "forms";

	/**
	 * Suffix of the custom form data file that is stored in the same directory.
	 */
	protected static final String CUSTOM_FORM_DATA_SUFFIX = "FormData";

	/**
	 * Sub directory of.
	 * com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs#DIR_CATALOG_ITEMS where
	 * icons are stored
	 */
	private static final String ICONS_SUBDIR = "icons";

	/**
	 * Suffix used for all of the resources saved by this store.
	 */
	protected static final String CUSTOM_RESOURCE_SUFFIX = ".json";

	/**
	 * Amount of time in MS to wait before retrying to lookup if the Custom Forms
	 * are synced.
	 */
	private static final int CUSTOM_FORM_SYNC_WAIT_TIME = 500;

	/**
	 * Mapping catalog items to content sources.
	 */
	private final Map<String, ArrayList<VraNgCatalogItem>> itemsMap = new HashMap<String, ArrayList<VraNgCatalogItem>>();

	// =================================================
	// CATALOG ITEMS EXPORT
	// =================================================

	/**
	 * Used to fetch the store's data from the package descriptor.
	 *
	 * @return list of catalog items
	 */
	@Override
	protected List<String> getItemListFromDescriptor() {
		return this.vraNgPackageDescriptor.getCatalogItem();
	}

	/**
	 * Export all catalog items from the server.
	 */
	@Override
	protected void exportStoreContent() {
		List<VraNgCatalogItem> allCatalogItems = this.restClient
				.getCatalogItemsForProject(this.restClient.getProjectId());

		allCatalogItems.forEach(catalogItem -> storeCatalogItemOnFileSystem(vraNgPackage, catalogItem));
	}

	/**
	 * Export specific catalogItemIds.
	 * Note: maybe add a configuration to skip misnamed Catalog items
	 *
	 * @param catalogItemNames list of item names
	 */
	@Override
	protected void exportStoreContent(final List<String> catalogItemNames) {
		List<VraNgCatalogItem> allCatalogItems = this.restClient
				.getCatalogItemsForProject(this.restClient.getProjectId());
		catalogItemNames.forEach(complexName -> {
			String[] nameParts = complexName.split(CATALOG_ITEM_SEPARATOR, 2);

			if (nameParts.length != 2) {
				throw new RuntimeException(
						String.format(
								"Incorrect catalogItem name convention. Use: SOURCE_NAME__CATALOG_ITEM_NAME, actual %s",
								complexName));
			}

			String sourceName = nameParts[0];
			String catalogItemName = nameParts[1];
			logger.debug("Searching for catalog item with name {} from source {}", catalogItemName, sourceName);

			List<VraNgCatalogItem> catalogItems = allCatalogItems.stream()
					.filter(item -> item.getName().equalsIgnoreCase(catalogItemName)
							&& item.getSourceName().equalsIgnoreCase(sourceName))
					.collect(Collectors.toList());

			// This should always be either one or empty
			if (!catalogItems.isEmpty()) {
				catalogItems.forEach(catalogItem -> storeCatalogItemOnFileSystem(vraNgPackage, catalogItem));
			} else {
				throw new IllegalStateException(
						String.format(
								"Catalog Item [%s] not found for Source Name [%s] not found on the server.",
								catalogItemName,
								sourceName));
			}
		});
	}

	/**
	 * Stores a catalog item on the file system.
	 * This will also fetch and store any icons associated with the catalog item and
	 * add their extensions to the catalog item.
	 * This will also fetch and store any custom forms associated with the catalog
	 * item
	 *
	 * @throws RuntimeException if the file cannot be created for some reason
	 *
	 * @param serverPackage
	 * @param catalogItem
	 */
	private void storeCatalogItemOnFileSystem(final Package serverPackage, final VraNgCatalogItem catalogItem)
			throws RuntimeException {
		VraNgCustomForm form = storeCustomFormOnFileSystem(serverPackage, catalogItem);
		if (form != null) {
			catalogItem.setFormId(form.getId());
		}

		File iconFile = storeCatalogItemIconOnFileSystem(serverPackage, catalogItem);
		if (iconFile != null) {
			catalogItem.setIconExtension(FilenameUtils.getExtension(iconFile.getName()));
		}

		storeCatalogItemOnFileSystemRaw(serverPackage, catalogItem);
	}

	/**
	 * Directly store the catalog item, does not fetch icons or forms expects them.
	 * to be set already
	 *
	 * @param serverPackage
	 * @param catalogItem
	 */
	private void storeCatalogItemOnFileSystemRaw(final Package serverPackage, final VraNgCatalogItem catalogItem) {
		logger.info("Storing catalogItem {}", catalogItem.getName());
		logger.debug("Catalog Item to store: {}", new Gson().toJson(catalogItem));

		File store = new File(serverPackage.getFilesystemPath());
		File customCatalogItemFile = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_CATALOG_ITEMS,
				getName(catalogItem) + CUSTOM_RESOURCE_SUFFIX).toFile();

		logger.info("Creating folder: {}", customCatalogItemFile.getParentFile().getAbsolutePath());
		if (!customCatalogItemFile.getParentFile().isDirectory() && !customCatalogItemFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", customCatalogItemFile.getParentFile().getAbsolutePath());
		}

		try {
			Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
			logger.info("Created file {}", Files.write(
					Paths.get(customCatalogItemFile.getPath()), gson.toJson(catalogItem).getBytes(),
					StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create catalog item {} {}", catalogItem.getName(), customCatalogItemFile.getPath());
			throw new RuntimeException(
					String.format(
							"Unable to store catalog item %s to file %s.",
							catalogItem.getName(),
							serverPackage.getFilesystemPath()),
					e);
		}
	}

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
	protected VraNgCustomForm storeCustomFormOnFileSystem(final Package serverPackage,
			final VraNgCatalogItem catalogItem) {
		VraNgContentSourceBase contentSource = this.restClient.getContentSource(catalogItem.getSourceId());
		if (contentSource == null) {
			throw new RuntimeException(
					String.format("Content source %s does not exist", catalogItem.getSourceId()));
		}

		logger.debug("Found content source '{}'", contentSource.getName());
		VraNgCustomForm form = this.restClient.getCustomFormByTypeAndSource(
				contentSource.getType().toString(),
				catalogItem.getId());

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
	 * Attempts to store an icon to the file system under {{catalog_item_dir}}/icons
	 * The correct file extension will be fetched from the content-type header.
	 *
	 * @NOTE Due to issue when GET .../icon/api/icons/iconId for vRA 8.4 (IAC-482),
	 *       we are not going to save svgs
	 *       Also this will result in the iconId to be stored in the file, but the
	 *       check in:
	 *       com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem#hasIcon() is
	 *       iconExtension dependent,
	 *       so logic should hold fine.
	 *
	 * @throws RuntimeException if the file cannot be created for some reason
	 *
	 * @param serverPackage
	 * @param catalogItem
	 *
	 * @return File
	 */
	private File storeCatalogItemIconOnFileSystem(final Package serverPackage, final VraNgCatalogItem catalogItem)
			throws RuntimeException {
		if (catalogItem.getIconId() == null) {
			return null;
		}

		ResponseEntity<byte[]> response = this.restClient.downloadIcon(catalogItem.getIconId());

		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException(
					String.format("Icon with id %s not found.", catalogItem.getIconId()));
		}

		File store = new File(serverPackage.getFilesystemPath());
		String iconExtension = extractIconExtensionFromResponseEntity(response);

		// SEE @NOTE
		if (iconExtension.equalsIgnoreCase("svg")) {
			logger.warn("Icon is SVG, skipping...");
			return null;
		}

		File customIconFile = Paths.get(
				store.getPath(),
				VraNgDirs.DIR_CATALOG_ITEMS,
				ICONS_SUBDIR,
				(getName(catalogItem) + "." + iconExtension)).toFile();

		if (!customIconFile.getParentFile().mkdirs()) {
			logger.warn("Could not create folder: {}", customIconFile.getParentFile().getAbsolutePath());
		}

		try {
			logger.info("Created icon file {}", Files.write(Paths.get(customIconFile.getPath()), response.getBody(),
					StandardOpenOption.CREATE));
		} catch (IOException e) {
			logger.error("Unable to create icon {}", customIconFile.getAbsolutePath());
			throw new RuntimeException(
					String.format(
							"Unable to store custom catalog item icon to file %s.",
							customIconFile.getAbsolutePath()),
					e);
		}

		return customIconFile;
	}

	/**
	 * All of the file types Service Broker lists as supported.
	 * Used https://cdn.jsdelivr.net/gh/jshttp/mime-db@master/db.json to fetch the
	 * correct mime type and extension
	 *
	 * @param response
	 *
	 * @return String
	 */
	private String extractIconExtensionFromResponseEntity(final ResponseEntity<byte[]> response) {
		HttpHeaders headers = response.getHeaders();

		switch (headers.getContentType().toString()) {
			case "image/pjpeg":
				return "pjpeg";
			case "image/jpeg":
				return "jpeg";
			case "image/jpg":
				return "jpg";
			case "image/svg+xml":
				return "svg";
			case "image/bmp":
				return "bmp";
			case "image/png":
			default:
				return "png";
			// Service broker supports pjp but I found no mention of this file extension
			// anywhere
			// case "image/pjp":
			// return "pjp";
			// Service broker supports jfif but I found no mention of this file extension
			// anywhere
			// case "image/jfif":
			// return "jfif";
		}
	}

	// =================================================
	// CATALOG ITEMS IMPORT
	// =================================================

	/**
	 * Import all catalog items.
	 * !NOTE This will skip svg icon upload due to issue described in IAC-482
	 * 
	 * @param sourceDirectory target path
	 */
	public void importContent(final File sourceDirectory) {
		logger.info("Importing files from the '{}' directory", VraNgDirs.DIR_CATALOG_ITEMS);

		// verify directory exists
		File catalogItemFolder = Paths.get(sourceDirectory.getPath(), VraNgDirs.DIR_CATALOG_ITEMS).toFile();
		if (!catalogItemFolder.exists()) {
			logger.info("Catalog Items Dir not found.");
			return;
		}

		File[] catalogItemFiles = this.filterBasedOnConfiguration(catalogItemFolder,
				new CustomFolderFileFilter(this.getItemListFromDescriptor()));
		if (catalogItemFiles != null && catalogItemFiles.length == 0) {
			logger.info("Could not find any catalog items.");
			return;
		}

		logger.info("Found catalog items. Importing...");
		List<VraNgCatalogItem> catalogItems = new ArrayList<>();
		for (File catalogItemFile : catalogItemFiles) {
			catalogItems.add(jsonFileToVraCatalogItem(catalogItemFile));
		}

		// Newly added blueprints may have not yet be available in the content source,
		// so we're making an API call to trigger inventory update and checking if the
		// catalog item appears
		syncContentSourceInventoryForCatalogItems(catalogItems);

		logger.info("Fetching latest ids from server.");
		fetchLatestCatalogItemIds(catalogItems);

		catalogItems.forEach(catalogItem -> {
			if (catalogItem.hasForm()) {
				importCustomForm(catalogItem, catalogItemFolder);
			}

			// See @NOTE
			if (catalogItem.hasIcon() && !catalogItem.getIconExtension().equalsIgnoreCase("svg")) {
				String iconId = importIcon(catalogItem, catalogItemFolder);
				setCatalogItemIcon(catalogItem, iconId);
			}
		});
	}

	/**
	 * Triggering content source inventory update, lookup if the catalog items.
	 * appear
	 *
	 * @param catalogItems List<VraNgCatalogItem>
	 */
	private void syncContentSourceInventoryForCatalogItems(final List<VraNgCatalogItem> catalogItems) {
		// Building a map of content sources (by ID) to catalog items that should be
		// available for them
		VrangContentSourceUtils utils = new VrangContentSourceUtils(restClient, vraNgPackage);
		List<String> contentSourceNames = catalogItems.stream().map(VraNgCatalogItem::getSourceName)
				.collect(Collectors.toList());
		restClient.getContentSourcesForProject(restClient.getProjectId()).forEach(contentSource -> {
			String contentSourceName = contentSource.getName();
			if (contentSourceNames.contains(contentSourceName)) {
				String contentSourceID = contentSource.getId();
				itemsMap.put(contentSourceID, new ArrayList<VraNgCatalogItem>());
				// Triggering content source update, refreshing content source's inventory
				logger.debug("Triggering sync for content source '{}' /ID: '{}'", contentSource.getName(),
						contentSourceID);
				utils.syncContentSource(contentSource, this.config.getImportTimeout());
			}
		});
	}

	/**
	 * Does a patch call to update the catalog item with the correct iconId.
	 *
	 * @param catalogItem
	 * @param newIconId
	 */
	private void setCatalogItemIcon(final VraNgCatalogItem catalogItem, final String newIconId) {
		logger.info("Patching catalog item {} with icon {}", catalogItem.getId(), newIconId);
		this.restClient.patchCatalogItemIcon(catalogItem, newIconId);
	}

	/**
	 * Imports the icon to vRA.
	 * If the icon already exists, the icon name will be returned in the "Location"
	 * header. In this case the file should be renamed
	 * and the catalogItem modified with the new iconId
	 *
	 * @param catalogItem
	 * @param catalogItemFolder
	 *
	 * @return String - The icon ID
	 */
	private String importIcon(final VraNgCatalogItem catalogItem, final File catalogItemFolder) {
		String iconName = getName(catalogItem) + "." + catalogItem.getIconExtension();
		File customIconFile = Paths.get(catalogItemFolder.getPath(), ICONS_SUBDIR, iconName).toFile();

		logger.info("Importing icon: {}", customIconFile.getAbsolutePath());

		if (this.waitForCatalogItemToAppear(catalogItem.getName())) {
			logger.info("Importing icon '{}'", iconName);
			ResponseEntity<String> response = this.restClient.uploadIcon(customIconFile);
			String headersLocationPath = response.getHeaders().getLocation().getPath();

			return headersLocationPath.substring(headersLocationPath.lastIndexOf("/") + 1);

		} else {
			logger.warn("Could not import icon {}", iconName);

			throw new RuntimeException(String.format("Failed to import Icon: %s", iconName));
		}
	}

	/**
	 * Import a custom form given the catalog item.
	 * ( form Id is extracted from the item and the fs is queried )
	 *
	 * @param catalogItem
	 * @param catalogItemFolder
	 */
	protected void importCustomForm(final VraNgCatalogItem catalogItem, final File catalogItemFolder) {
		String formName = getName(catalogItem) + CUSTOM_RESOURCE_SUFFIX;
		String formDataName = getName(catalogItem) + CATALOG_ITEM_SEPARATOR + CUSTOM_FORM_DATA_SUFFIX
				+ CUSTOM_RESOURCE_SUFFIX;
		File customFormFile = Paths.get(catalogItemFolder.getPath(), CUSTOM_FORMS_SUBDIR, formName).toFile();
		File customFormDataFile = Paths.get(catalogItemFolder.getPath(), CUSTOM_FORMS_SUBDIR, formDataName).toFile();

		logger.info("Importing custom form: {}", customFormFile.getAbsolutePath());

		VraNgCustomForm customForm = jsonFileToVraCustomForm(customFormFile, customFormDataFile);
		// from 8.12 to < 8.12
		if (customForm.getName() != null && customForm.getSourceType().equals("com.vmw.blueprint.version")) {
			customForm.setName(catalogItem.getName());
		}
		// from 8.12 to < 8.12
		if (customForm.getSourceId() != null) {
			customForm.setSourceId(customForm.getSourceId().split(CATALOG_ITEM_BLUEPRINT_VERSION_SOURCE_ID_SEPARATOR, 2)[0]);
		}
		// from 8.12 to < 8.12
		customForm.setSourceType("com.vmw.blueprint");

		logger.debug("Custom Form to import: {}", new Gson().toJson(customForm));
		// wait 250 ms between each custom form import in order catalog item to be
		// retrievable by the VRA REST API
		if (this.waitForCatalogItemToAppear(customForm.getName())) {
			logger.info("Importing custom form '{}'", customForm.getName());
			this.restClient.importCustomForm(customForm, catalogItem.getId());
		} else {
			logger.warn(
					"Custom form with name '{} (type {})' is missing from vRA content source.",
					customForm.getName(),
					customForm.getSourceType());

			throw new RuntimeException(String.format("Failed to import Custom Form: %s", customForm.getName()));
		}
	}

	/**
	 * Waits a variable amount of time for the catalog item to appear.
	 * Returns a boolean if at the end it has appeared or not
	 *
	 * @param customFormName
	 *
	 * @return boolean
	 */
	protected boolean waitForCatalogItemToAppear(final String customFormName) {
		long finish = System.currentTimeMillis() + this.config.getImportTimeout();
		VraNgCatalogItem retVal = this.restClient.getCatalogItemByBlueprintName(customFormName);
		boolean isAvailable = retVal != null;

		try {
			logger.info("Waiting (max {} ms) catalog item '{}' to appear on target system",
					this.config.getImportTimeout(), customFormName);
			while (!isAvailable && System.currentTimeMillis() < finish) {
				Thread.sleep(CUSTOM_FORM_SYNC_WAIT_TIME);
				retVal = this.restClient.getCatalogItemByBlueprintName(customFormName);
				isAvailable = retVal != null;
			}
			if (retVal == null) {
				logger.warn("Timed out {} ms waiting catalog item '{}' to appear on target system",
						this.config.getImportTimeout(), customFormName);
			}
		} catch (InterruptedException e) {
			logger.debug("Interrupted exception during invoke of waitForCatalogItemToAppear({}, {}) : {}",
					customFormName, CUSTOM_FORM_SYNC_WAIT_TIME, e.getMessage());
		} catch (Exception e) {
			logger.debug("General error during invoke of waitForCatalogItemToAppear({}, {}) : {}", customFormName,
					CUSTOM_FORM_SYNC_WAIT_TIME, e.getMessage());
		}

		return isAvailable;
	}

	/**
	 * Converts a json catalog item file to VraNgCatalogItem.
	 *
	 * @TODO Try to combine both? Can't really get the class of a Generic tho
	 *
	 * @param jsonFile
	 *
	 * @return VraNgCatalogItem
	 */
	private VraNgCatalogItem jsonFileToVraCatalogItem(final File jsonFile) {
		logger.debug("Converting catalog item file to VraNgCatalogItem. Name: '{}'", jsonFile.getName());

		try (JsonReader reader = new JsonReader(new FileReader(jsonFile.getPath()))) {
			return new Gson().fromJson(reader, VraNgCatalogItem.class);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFile.getPath()), e);
		}
	}

	/**
	 * Converts a json custom form file to VraNgCustomForm
	 * if the jsonFormDataFile exist then the form data contents will be read from
	 * it.
	 *
	 * @param jsonFormFile     - JSON form metadata file
	 * @param jsonFormDataFile - JSON form data file (file suffixed with
	 *                         CUSTOM_FORM_DATA_SUFFIX)
	 *
	 * @return VraNgCustomForm
	 */
	protected VraNgCustomForm jsonFileToVraCustomForm(final File jsonFormFile, final File jsonFormDataFile) {
		logger.debug("Converting custom form file to VraNgCustomForm. Name: '{}'", jsonFormFile.getName());

		VraNgCustomForm retVal;
		try (JsonReader reader = new JsonReader(new FileReader(jsonFormFile.getPath()))) {
			retVal = new Gson().fromJson(reader, VraNgCustomForm.class);
			// if there is a separate form data file then set the form content from it,
			// otherwise use the one stored in the JSON form file
			if (jsonFormDataFile.exists()) {
				logger.info("Found custom form data file '{}' for form name '{}'", jsonFormDataFile.getPath(),
						jsonFormFile.getName());
				retVal.setForm(FileUtils.readFileToString(jsonFormDataFile, Charset.defaultCharset()));
			}
			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error reading from file: %s", jsonFormFile.getPath()), e);
		}
	}

	/**
	 * Returns custom catalog item resource name that can be used for catalog items,
	 * forms and icons.
	 *
	 * @param catalogItem
	 *
	 * @return String
	 */
	protected String getName(final VraNgCatalogItem catalogItem) {
		return catalogItem.getSourceName() + CATALOG_ITEM_SEPARATOR + catalogItem.getName();
	}

	/**
	 * Fetches the latest catalog item ids from the server to be used.
	 * Done at once for all to save fetching all catalog items for every item.
	 *
	 * @param catalogItems - catalog items to be updated
	 */
	private void fetchLatestCatalogItemIds(final List<VraNgCatalogItem> catalogItems) {
		// Waiting for catalog items to become available in the content source
		for (String contentSourceID : itemsMap.keySet()) {
			itemsMap.get(contentSourceID).forEach(item -> {
				String itemName = item.getName();
				// wait 250 ms between each custom form import in order catalog item to be
				// retrievable by the VRA REST API
				logger.debug("Checking if catalog item is now available, {}", itemName);
				boolean catalogItemAvailable = waitForCatalogItemToAppear(itemName);
				if (!catalogItemAvailable) {
					logger.warn("Catalog item with name '{}' is missing from vRA content source with ID '{}'.",
							itemName, contentSourceID);
				}
			});
		}

		List<VraNgCatalogItem> allCatalogItems = this.restClient.getCatalogItemsForProject(
				this.restClient.getProjectId());

		// return here if there are no catalog items currently on the server
		if (allCatalogItems == null || allCatalogItems.isEmpty()) {
			return;
		}

		if (!catalogItems.isEmpty()) {
			catalogItems.forEach(catalogItem -> {
				for (VraNgCatalogItem serverCatalogItem : allCatalogItems) {
					if (catalogItem != null && serverCatalogItem != null
							&& catalogItem.getSourceName().equals(serverCatalogItem.getSourceName())
							&& catalogItem.getName().equals(serverCatalogItem.getName())) {
						logger.debug("Updating catalog item {} with id {}", catalogItem.getId(),
								serverCatalogItem.getId());
						catalogItem.setId(serverCatalogItem.getId());
						return;
					}
				}

				throw new RuntimeException(
						String.format("Catalog Item %s__%s not found", catalogItem.getSourceName(),
								catalogItem.getName()));
			});
		}
	}
}
