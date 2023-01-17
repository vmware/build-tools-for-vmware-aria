package com.vmware.pscoe.iac.artifact.helpers;

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

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionsHelper {
	/**
	 * Checks if the given folder contains the given item names
	 *
	 * @param	folder
	 * @param	itemNames
	 */
	public static void assertFolderContainsFiles( File folder, String[] itemNames ) {
		assertEquals(
			itemNames.length,
			folder.list().length,
			String.format(
				"Folder %s doesn't have the correct amount of elements, %s expected",
				folder.getName(),
				itemNames.length
			)
		);

		int found	= 0;
		for ( File item: folder.listFiles() ) {
			if ( Arrays.asList( itemNames ).contains( item.getName() ) ) {
				found++;
			}
		}

		assertEquals(
			itemNames.length,
			found,
			String.format(
				"Folder %s was expected to contain %s, but actually contains %s",
				folder.getName(),
				new Gson().toJson( itemNames ),
				new Gson().toJson( folder.list() )
			)
		);
	}
}
