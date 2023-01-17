package com.vmware.pscoe.iac.artifact.helpers.filesystem;

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
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCatalogItem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.vmware.pscoe.iac.artifact.store.vrang.VraNgDirs.DIR_CATALOG_ITEMS;

public class CatalogItemFsMocks extends VraNgFsMock {
	private final static String WORKDIR = "catalog-items";
	public CatalogItemFsMocks(File tempDir) {
		super( tempDir );
	}

	@Override
	public File getWorkdir() {
		return Paths.get( this.tempDir.getPath(), WORKDIR ).toFile();
	}

	@Override
	protected void ensureWorkdirExists() {
		super.ensureWorkdirExists();

		File iconsDir	= getIconDir();

		if ( iconsDir.exists() ) {
			return ;
		}

		if ( ! iconsDir.mkdirs() ) {
			throw new RuntimeException( "Error while creating " + iconsDir.getAbsolutePath() + " dir" );
		}

		File formsDir	= getFormDir();
		if ( formsDir.exists() ) {
			return;
		}

		if ( ! formsDir.mkdirs() ) {
			throw new RuntimeException( "Error while creating " + formsDir.getAbsolutePath() + " dir" );
		}
	}

	/**
	 * @return	File
	 */
	public File getCatalogItemsDir() {
		return Paths.get( tempDir.getPath(), DIR_CATALOG_ITEMS ).toFile();
	}

	/**
	 * JSON encodes a catalog item and adds it to the DIR_CATALOG_ITEMS.
	 * Adds the catalog items dir if it does not exist
	 *
	 * @param	catalogItem - The catalog item to add
	 */
	public void addCatalogItem( VraNgCatalogItem catalogItem ) {
		File customCatalogItemFile	= Paths.get(
			getCatalogItemsDir().getAbsolutePath(),
			getCatalogItemResourceName( catalogItem ) + ".json"
		).toFile();

		Gson gson		= new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		Path itemName	= Paths.get( customCatalogItemFile.getPath() );
		writeFileToPath( itemName, gson.toJson( catalogItem ).getBytes() );
	}

	/**
	 * @return	File
	 */
	private File getIconDir() {
		return Paths.get( getCatalogItemsDir().getAbsolutePath(), "icons" ).toFile();
	}

	/**
	 * Adds an icon with a body with the given information in the catalog item
	 *
	 * @param	catalogItem -> must contain an icon id and icon extension
	 */
	public void addCatalogItemIcon( VraNgCatalogItem catalogItem ) {
		if ( ! catalogItem.hasIcon() ) {
			throw new RuntimeException(
				String.format( "Trying to store an icon for catalog item %s but no icon found", catalogItem.getName() )
			);
		}

		File customIconFile	= Paths.get(
			getIconDir().getAbsolutePath(),
			getCatalogItemResourceName( catalogItem ) + "." + catalogItem.getIconExtension()
		).toFile();

		Path itemName	= Paths.get( customIconFile.getPath() );
		writeFileToPath( itemName, "iconBody".getBytes() );
	}

	/**
	 * @return	File
	 */
	private File getFormDir() {
		return Paths.get( getCatalogItemsDir().getAbsolutePath(), "forms" ).toFile();
	}

	/**
	 * Adds a form with a body with the given information in the catalog item
	 *
	 * @param	catalogItem -> must contain a formId
	 */
	public void addCatalogItemForm( VraNgCatalogItem catalogItem ) {
		if ( ! catalogItem.hasForm() ) {
			throw new RuntimeException(
				String.format( "Trying to store a a form for catalog item %s but no form found", catalogItem.getName() )
			);
		}

		File customFormItem	= Paths.get(
			getFormDir().getAbsolutePath(),
			getCatalogItemResourceName( catalogItem ) + ".json"
		).toFile();

		Path itemName	= Paths.get( customFormItem.getPath() );
		writeFileToPath( itemName,
			(
				"{\"id\": \"342f2e18-ff1d-40cd-a1ba-8ec4ce8c3c87\",\"name\": \"Sum two numbers\",\"form\": \"{\\\"test\\\":" +
					"\\\"test\\\"}\",\"styles\": null,\"sourceType\": \"com.vmw.vro.workflow\",\"type\": \"requestForm\"," +
					"\"status\": \"ON\",\"formFormat\": \"JSON\"}"
			).getBytes()
		);
	}


	/**
	 * Returns custom catalog item resource name that can be used for catalog items, forms and icons
	 *
	 * @param	catalogItem
	 *
	 * @return	String
	 */
	private String getCatalogItemResourceName( VraNgCatalogItem catalogItem ) {
		return catalogItem.getSourceName() + "__" + catalogItem.getName();
	}
}
