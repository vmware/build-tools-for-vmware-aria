package com.vmware.pscoe.iac.artifact.helpers;

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
