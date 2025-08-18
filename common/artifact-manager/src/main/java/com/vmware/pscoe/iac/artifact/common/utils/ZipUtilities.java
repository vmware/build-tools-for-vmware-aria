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
package com.vmware.pscoe.iac.artifact.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtilities {

	public static void unzip(File zipFile, File outputLocation) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()))) {
			ZipEntry ze = null;

			while ((ze = zis.getNextEntry()) != null) {

				String fileName = ze.getName();
				File newFile = new File(outputLocation, fileName);

				new File(newFile.getParent()).mkdirs();

				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					byte[] buffer = new byte[1024];

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
