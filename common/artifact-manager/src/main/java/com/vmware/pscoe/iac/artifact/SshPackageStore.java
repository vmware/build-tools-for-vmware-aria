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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationSsh;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageContent;
import com.vmware.pscoe.iac.artifact.model.PackageContent.Content;
import com.vmware.pscoe.iac.artifact.model.basic.BasicPackageDescriptor;
import com.vmware.pscoe.iac.artifact.ssh.SshClient;

public class SshPackageStore extends GenericPackageStore<BasicPackageDescriptor> {
	/**
	 * The content directory.
	 */
    private static final String DIR_CONTENT = "content";

	/**
	 * Variable for logging.
	 */
    private final Logger logger = LoggerFactory.getLogger(SshPackageStore.class);

	/**
	 * The SSH configuration.
	 */
    private final ConfigurationSsh config;

	/**
	 * The session variable.
	 */
    private Session session;

	/**
	 *
	 * @param sshConfig the ssh configuration
	 */
    protected SshPackageStore(final ConfigurationSsh sshConfig) {
        this.config = sshConfig;
    }

	/**
	 * Imports all packages.
	 * @param pkgs the packages to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
    @Override
    public final List<Package> importAllPackages(final List<Package> pkgs, final boolean dryrun, final boolean mergePackages, final boolean enableBackup) {
        this.validateFilesystem(pkgs);

        List<Package> sourceEndpointPackages = pkgs;
        if (sourceEndpointPackages.isEmpty()) {
            return new ArrayList<>();
        }
        List<Package> importedPackages = new ArrayList<>();
        for (Package pkg : sourceEndpointPackages) {
            importedPackages.add(this.importPackage(pkg, dryrun, mergePackages));
        }

        return importedPackages;
    }

	/**
	 * Exports all packages.
	 * @param pkgs the packages to export
	 * @param dryrun whether it should be dry run
	 * @return the exported packages
	 */
    @Override
    public final List<Package> exportAllPackages(final List<Package> pkgs, final boolean dryrun) {
    	throw new RuntimeException("Not implemented!");
    }

	/**
	 * Imports all packages.
	 * @param pkg the packages to import
	 * @param dryrun whether it should be dry run
	 * @param enableBackup whether it should back up the packages on import
	 * @return the imported packages
	 */
	@Override
	public final List<Package> importAllPackages(final List<Package> pkg, final boolean dryrun, final boolean enableBackup) {
		return this.importAllPackages(pkg, dryrun, false, enableBackup);
	}

	/**
	 * Imports a package.
	 * @param pkg the package to import
	 * @param dryrun whether it should be dry run
	 * @param mergePackages whether to merge the packages
	 * @return the imported package
	 */
	@Override
    public final Package importPackage(final Package pkg, final boolean dryrun, final boolean mergePackages) {
        logger.info(String.format(PackageStore.PACKAGE_IMPORT, pkg));

        File tmp;
        try {
            tmp = Files.createTempDirectory("vrbt-temp-import-dir").toFile();
            logger.info("Created temp dir {}", tmp.getAbsolutePath());
            new PackageManager(pkg).unpack(tmp);
        } catch (IOException e) {
            logger.error("Unable to extract package '{}' in temporary directory.", pkg.getFQName());
            throw new RuntimeException("Unable to extract pacakge.", e);
        }

        importFiles(pkg, tmp);

        return pkg;
    }

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param dryrun whether it should be a dry run
	 * @return the exported package
	 */
    @Override
    public final Package exportPackage(final Package pkg, final boolean dryrun) {
        throw new RuntimeException("Not implemented!");
    }

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param exportDescriptor the descriptor of the package to export
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    @Override
    public final Package exportPackage(final Package pkg, final File exportDescriptor, final boolean dryrun) {
        BasicPackageDescriptor descriptor = BasicPackageDescriptor.getInstance(exportDescriptor);

        return this.exportPackage(pkg, descriptor, dryrun);
    }

	/**
	 * Exports a package.
	 * @param pkg the package to export
	 * @param sshPackageDescriptor the package descriptor
	 * @param dryrun whether it should be dry run
	 * @return the exported package
	 */
    @Override
    public final Package exportPackage(final Package pkg, final BasicPackageDescriptor sshPackageDescriptor, final boolean dryrun) {
        logger.info(String.format(PackageStore.PACKAGE_EXPORT, pkg));
        String rootDirectory = this.config.getSshDirectory().endsWith(File.separator) ? this.config.getSshDirectory() : this.config.getSshDirectory() + File.separator;
        List<String> files = sshPackageDescriptor.getContent().stream().map(file -> (rootDirectory + file)).collect(Collectors.toList());

        if (files != null) {
            exportFiles(pkg, files);
        } else {
            logger.info("No files found in content.yaml");
        }

        return pkg;
    }

	/**
	 * Gets the packages.
	 * @return the packages
	 */
    @Override
    public final List<Package> getPackages() {
        throw new UnsupportedOperationException("Getting packages is not supported");
    }

	/**
	 * Deletes a package.
	 * @param pkg the package to delete
	 * @param withContent whether to delete the package with the content
	 * @param dryrun whether it should be dry run
	 * @return the deleted package
	 */
	@Override
    protected final Package deletePackage(final Package pkg, final boolean withContent, final boolean dryrun) {
        throw new UnsupportedOperationException("Deleting packages is not supported");
    }

	/**
	 * Gets package content.
	 * @param pkg the package which content to get
	 * @return the package content
	 */
	@Override
    protected final PackageContent getPackageContent(final Package pkg) {
        throw new UnsupportedOperationException("Parsing package content is not supported");
    }

	/**
	 * Deletes content.
	 * @param content the content to be deleted
	 * @param dryrun whether it should be dry run
	 */
	@Override
    protected final void deleteContent(final Content content, final boolean dryrun) {
        throw new UnsupportedOperationException("Delete content is not supported");
    }

	/**
	 * Exports files from ssh package.
	 * @param sshPackage the ssh package from which to export the files
	 * @param files the exported files
	 */
    private void exportFiles(final Package sshPackage, final List<String> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        try {
            logger.info("Copying files to local Host.");

            File store = new File(sshPackage.getFilesystemPath());
            File sshFile = Paths.get(store.getPath(), DIR_CONTENT).toFile();
            Files.deleteIfExists(sshFile.toPath());
            sshFile.mkdirs();

            reconnect();
            SshClient.copyRemoteToLocal(session, files, sshFile);
        } catch (IOException | JSchException e) {
            String message = "Unable to export content from package '%s', error in connection to '%s'. '%s' : '%s'";
            message = String.format(message, sshPackage.getFQName(), this.config.getHost(), e.getClass().getName(),
                    e.getMessage());

            logger.error(message);
            throw new RuntimeException(message, e);
        } finally {
            close();
        }
    }

	/**
	 * Imports files.
	 * @param sshPackage the ssh package to import files from
	 * @param tmp the temporary file
	 */
    private void importFiles(final Package sshPackage, final File tmp) {
        File contentDirectory = Paths.get(tmp.getPath()).toFile();

        if (contentDirectory.exists()) {
            List<File> files = FileUtils.listFiles(contentDirectory, null, false).stream().collect(Collectors.toList());

            try {
                logger.info("Copying files to target Host.");

                reconnect();
                SshClient.copyLocalToRemote(session, files, config.getSshDirectory());
            } catch (JSchException | SftpException e) {
                String message = "Unable to import content from package '%s', error in connection to '%s'. '%s' : '%s'";
                message = String.format(message, sshPackage.getFQName(), config.getHost(), e.getClass().getName(),
                        e.getMessage());

                logger.error(message);
                throw new RuntimeException(message, e);
            } finally {
                close();
            }
        }
    }

	/**
	 * Connects.
	 * @throws JSchException
	 */
    private void connect() throws JSchException {
        session = SshClient.createSession(config.getUsername(), config.getPassword(), config.getHost(),
                config.getPort());
        session.connect();
        logger.info("SSH Session opened");
    }

	/**
	 * Reconnects.
	 * @throws JSchException
	 */
    private void reconnect() throws JSchException {
        if (session != null && session.isConnected()) {
            return;
        }
        logger.warn("SSH session is closed, trying to reconnect");
        connect();
    }

	/**
	 * Closes.
	 */
    private void close() {
        if (session != null) {
            session.disconnect();
        }

        session = null;
    }
}
