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
import java.util.zip.ZipOutputStream;

public final class ZipUtilities {
	private static final int PRIME_NUMBER_1024 = 1024;

	private ZipUtilities() {
	}

	public static void unzip(File zipFile, File outputLocation) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getAbsolutePath()))) {
			ZipEntry ze = null;

			while ((ze = zis.getNextEntry()) != null) {

				String fileName = ze.getName();
				File newFile = new File(outputLocation, fileName);

				new File(newFile.getParent()).mkdirs();

				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					byte[] buffer = new byte[PRIME_NUMBER_1024];

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

	public static void zip(File file, String destinationDir) {

        try (FileOutputStream fos = new FileOutputStream(destinationDir);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file.getPath())) {

            ZipEntry zipEntry = new ZipEntry(new File(file.getPath()).getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[PRIME_NUMBER_1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            System.out.println("File zipped successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
