package com.vmware.pscoe.iac.artifact;

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

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.pscoe.iac.artifact.model.Package;

import static org.apache.commons.io.FilenameUtils.getPath;

/**
 * This class is responsible for expanding a package file into a directory structure and
 * packaging a directory structure into a package file.
 */
public class PackageManager {

	/**
	 * An abstraction that represents a package.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Package pkg;

	/**
	 * Create a new package manager that can be used to expand a package file into a directory structure and
	 * packing a directory structure into a package file.
	 * @param pkg An abstraction representing the package.
	 */
	public PackageManager(Package pkg) {
		this.pkg = pkg;
	}

	/**
	 * Take the package represented by the {@link Package} parameter (in the constructor and unpack it into a
	 * directory structure.
	 * The {@link Package#getFilesystemPath} method in the {@link Package} parameter should be pointing to an
	 * actual package file on the file system. The only archive format of the package that is currently supported
	 * is zip, so all package files are also zip archives.
	 * @param outputLocation The directory where the package file will be expanded to a set of files and folders.
	 * @throws IOException In case there is some Input Output error.
	 * @see Package
	 * @see Package#getFilesystemPath() 
	 * @see #pack(File) 
	 */
	public void unpack(File outputLocation) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pkg.getFilesystemPath()))) {
			ZipEntry ze = null;

			while ((ze = zis.getNextEntry()) != null) {

				String fileName = ze.getName();
				File newFile = new File(outputLocation, fileName);

				new File(newFile.getParent()).mkdirs();
				
				if (!ze.isDirectory()) {
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						byte[] buffer = new byte[1024];

						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}

					}
				}
			}
		}
	}

	/**
	 * From a directory structure that contains a set of directories, subdirectories and files inside them, 
	 * create a package file as pointed by the {@link Package} paramter specified in the constructor. 
	 * The location of the package file is determined by the {@link Package#getFilesystemPath()} method of the package 
	 * specified in the constructor.
	 * @param sourceDirectory The source directory that contains the expanded package.
	 * @throws IOException In case there is some Input/Output error.
	 * @see Package
	 * @see Package#getFilesystemPath() 
	 * @see #unpack(File)
	 */
	public void pack(File sourceDirectory) throws IOException {
		List<File> fileList = new ArrayList<>();
		getAllFiles(sourceDirectory, fileList);
		writeZipFile(sourceDirectory, fileList);
	}

	/**
	 * Get a list of all entries from the package file (specified as {@link Package} parameter in the constructor).
	 * As currently the only archive format for packages is zip, this would interpret the package file returned by
	 * {@link Package#getFilesystemPath} as zip archive and will list all of the entries in the that zip.
	 * Each entry will be represented by just the path to that entry based on the zip root.
	 * @return A list of all of the entries packed in the package (provided in the constructor).
	 * @throws IOException If there is an Input/Output error.
	 * @see Package#getFilesystemPath()
	 */
	public List<String> getAllFiles() throws IOException {
		try (ZipFile zipFile = new ZipFile(pkg.getFilesystemPath())) {
			return zipFile.stream()
				.map(ZipEntry::getName)
				.collect(Collectors.toList());
		}
	}

	/**
	 * Utility method that recursively adds all files (and folders) in the given directory to the specified
	 * list.
	 * @param dir The directory to traverse for files and folders including any level deep.
	 * @param fileList Output parameter where the result from the traversal would be stored.
	 *                 Should be a non-null list, but may be an empty list.
	 */
	public void getAllFiles(File dir, List<File> fileList) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			getAllFiles(file, fileList);
		}
	}

	/**
	 *
	 * @param directoryToZip
	 * @param fileList
	 * @throws IOException exception
	 */
	private void writeZipFile(File directoryToZip, List<File> fileList) throws IOException {
		File zipFile = new File(pkg.getFilesystemPath());
		zipFile.getParentFile().mkdirs();
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}
		}
	}

	private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {

			// we want the zipEntry's path to be a relative path that is
			// relative
			// to the directory being zipped, so chop off the rest of the path
			String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
					file.getCanonicalPath().length());
			ZipEntry zipEntry = new ZipEntry(zipFilePath.replaceAll("\\\\", "/"));
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
		}
		zos.closeEntry();
	}

	/**
	 * Add a single file to existing ZIP
	 * @param file the file to add
	 * @param zipDirectory parent directory for the file
	 * @throws IOException exception
	 */
	public void addTextFileToExistingZip(File file, Path zipDirectory) throws IOException {
		File zipFile = new File(pkg.getFilesystemPath());

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

		FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipFile.toURI()), env);
		Path nf = fs.getPath(zipDirectory.toString() + File.separator + file.getName());
		logger.debug("Writing file {} to Zip path {}", file.getCanonicalPath(), nf.toString());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

		String line = null;
		while ((line=reader.readLine()) != null) {
			writer.write(line);
			writer.newLine();
		}
		reader.close();
		writer.close();
		fs.close();
	}

	public void addToExistingZip(List<File> fileList) throws IOException {
		if (fileList.isEmpty()) {
			return;
		}
		File directoryToZip = new File(new File(pkg.getFilesystemPath()).getParent());
		File zipFile = new File(pkg.getFilesystemPath());
		File tempFile = File.createTempFile(zipFile.getName(), null);
		boolean hasExistingFiles = zipFile.exists();
		
		tempFile.delete();
		zipFile.renameTo(tempFile);

		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));

		// Copy Existing Files
		if (hasExistingFiles) {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
			byte[] buf = new byte[1024];
			ZipEntry entry = zin.getNextEntry();
			while (entry != null) {
				zout.putNextEntry(new ZipEntry(entry.getName()));
				int len;
				while ((len = zin.read(buf)) > 0) {
					zout.write(buf, 0, len);
				}
				entry = zin.getNextEntry();
			}
			zin.close();
		}

		// ZIP format does not support duplicate file names.
		// Ensure we are adding unique files
		Set<File> uniqueFileList = new HashSet<>(fileList);

		// Add New Files
		for (File file : uniqueFileList) {
			logger.debug("Archiving file " + file.getName());
			addToZip(directoryToZip, file, zout);
		}

		zout.close();
		tempFile.delete();
	}

	/**
	 * Recursively copy the content of a source directory to a destination directory. Both directories should exist. 
	 * If at least one of them does not exist, then nothing will be copied and this method will exit silently without 
	 * any error.
	 * @param srcDir The source directory whose content would be copied to the destination directory. Should exist. 
	 *               If this directory does not exist, then no exception will be thrown, but nothing will be copied 
	 *               as well.
	 * @param destDir The destination where to copy the content of the source directory. This should be an existing 
	 *               directory. If this directory does not exist, then no error will be thrown, but also nothing will
	 *               be copied.    
	 * @throws IOException in case there is some Input/Output error diring copy operation.
	 * @see #cleanup(File)
	 */
	public static void copyContents(File srcDir, File destDir) throws IOException {
		if (!destDir.exists()) {
			boolean success = destDir.mkdirs();
			if (!success || !destDir.exists()) {
				throw new IOException("Cannot create directory \"" + destDir + "\". Please check file system permissions.");
			}
		}
		if (srcDir.exists()) {
			FileUtils.copyDirectory(srcDir, destDir);
		}
	}

	/**
	 * Deletes a directory recursively. The directory itself will be deleted, together with all its content.
	 *
	 * @param directoryToBeDeleted directory to delete
	 * @throws IOException              in case deletion is unsuccessful
	 * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
	 */
	public static void cleanup(File directoryToBeDeleted) throws IOException {
		FileUtils.deleteDirectory(directoryToBeDeleted);
	}
}
