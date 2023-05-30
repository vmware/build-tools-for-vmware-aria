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

import com.vmware.pscoe.iac.artifact.configuration.ConfigurationIdem;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.ssh.SshClient;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class IdemPackageStore extends SshPackageStore {

    protected static final Logger logger = LoggerFactory.getLogger(IdemPackageStore.class);

    protected IdemPackageStore(ConfigurationIdem config) {
        super(config);
    }

    @Override
    protected void importFiles(Package sshPackage, File tmp) {
        File contentDirectory = Paths.get(tmp.getPath()).toFile();

        if (contentDirectory.exists()) {
            List<File> files = FileUtils.listFiles(contentDirectory, null, false).stream().collect(Collectors.toList());

            try {
                logger.info("Copying files to target Host.");

                reconnect();
                SshClient.copyLocalToRemote(session, files, config.getSshDirectory());
                for (File file : files) {
                    if (ConfigurationIdem.IDEM_PLUGIN_INSTALL_FILE.equals(file.getName())) {
                        String filePath = String.format("%s/%s", config.getSshDirectory(), file.getName());
                        executeInstallScript(filePath);
                        return;
                    }
                }
                throw new RuntimeException(
                        String.format("Plugin installation file ('%s') not found",
                                ConfigurationIdem.IDEM_PLUGIN_INSTALL_FILE));
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

    private void executeInstallScript(String filePath) {
        SshClient.execute(session, "chmod +x " + filePath);
        List<String> res = SshClient.execute(session, filePath);
        boolean success = false;
        for (String line : res) {
            logger.info(line);
            success = success || "SUCCESS_360".equals(line);
        }
        if (!success) {
            throw new RuntimeException("Plugin installation failed");
        }
    }
}