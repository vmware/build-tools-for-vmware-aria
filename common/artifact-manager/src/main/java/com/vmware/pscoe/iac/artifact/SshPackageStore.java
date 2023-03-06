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
    private static final String DIR_CONTENT = "content";
    private static final Logger logger = LoggerFactory.getLogger(SshPackageStore.class);

    private final ConfigurationSsh config;
    private Session session;

    protected SshPackageStore(ConfigurationSsh config) {
        this.config = config;
    }

    @Override
    public List<Package> importAllPackages(List<Package> pkgs, boolean dryrun, boolean mergePackages) {
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

    @Override
    public List<Package> exportAllPackages(List<Package> pkgs, boolean dryrun) {
    	throw new RuntimeException("Not implemented!");
    }

	@Override
	public List<Package> importAllPackages(List<Package> pkg, boolean dryrun) {
		return this.importAllPackages(pkg,dryrun, false);
	}

	@Override
    public Package importPackage(Package pkg, boolean dryrun, boolean mergePackages) {
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

    @Override
    public Package exportPackage(Package pkg, boolean dryrun) {
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public Package exportPackage(Package pkg, File exportDescriptor, boolean dryrun) {
        BasicPackageDescriptor descriptor = BasicPackageDescriptor.getInstance(exportDescriptor);
        return this.exportPackage(pkg, descriptor, dryrun);
    }

    @Override
    public Package exportPackage(Package pkg, BasicPackageDescriptor sshPackageDescriptor, boolean dryrun) {
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

    @Override
    public List<Package> getPackages() {
        throw new UnsupportedOperationException("Getting packages is not supported");
    }

    @Override
    protected Package deletePackage(Package pkg, boolean withContent, boolean dryrun) {
        throw new UnsupportedOperationException("Deleting packages is not supported");
    }

    @Override
    protected PackageContent getPackageContent(Package pkg) {
        throw new UnsupportedOperationException("Parsing package content is not supported");
    }

    @Override
    protected void deleteContent(Content content, boolean dryrun) {
        throw new UnsupportedOperationException("Delete content is not supported");
    }

    private void exportFiles(Package sshPackage, List<String> files) {
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

    private void importFiles(Package sshPackage, File tmp) {
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

    private void connect() throws JSchException {
        session = SshClient.createSession(config.getUsername(), config.getPassword(), config.getHost(),
                config.getPort());
        session.connect();
        logger.info("SSH Session opened");
    }

    private void reconnect() throws JSchException {
        if (session != null && session.isConnected()) {
            return;
        }
        logger.warn("SSH session is closed, trying to reconnect");
        connect();
    }

    private void close() {
        if (session != null) {
            session.disconnect();
        }

        session = null;
    }
}
