package com.vmware.pscoe.iac.artifact.cli;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVrops;
import com.vmware.pscoe.iac.artifact.ssh.SshClient;

public class CliManagerVrops implements AutoCloseable {
    private static final String VIEW = "view";
    private static final String DASHBOARD = "dashboard";
    private static final String SUPER_METRIC = "supermetric";
    private static final String REPORT = "report";
    private static final String FILE = "file";
    private static final String METRIC_CONFIG = "reskndmetric";
    private static final String EXPORT    = "export";
    private static final String IMPORT = "import";
    private static final String SHARE = "share";
    private static final String UNSHARE = "unshare";
    private static final String ACTIVATE = "show";
    private static final String DEACTIVATE = "hide";

    private static final String SHARING_DEFAULT_USER= "admin";

    private static final String FORCE = "--force";
    private static final String ALL_POLICIES = "--policies all";
    private static final String CHECK_FALSE = "--check false";

    private static final String OPSCLI_PATH = "/usr/lib/vmware-vcops/tools/opscli/ops-cli.py";
    private static final String UNIX_PATH_SEPARATOR = "/";
    private static final String VROPS_SSH_COMMAND_INFO = "Executing vROps SSH command: {} ";
    private static final String VROPS_SSH_COMMAND_0 = "$VMWARE_PYTHON_3_BIN %s %s %s %s";
    private static final String VROPS_SSH_COMMAND_1 = "$VMWARE_PYTHON_3_BIN %s %s %s %s %s";
    private static final String VROPS_SSH_COMMAND_2 = "$VMWARE_PYTHON_3_BIN %s %s %s %s %s %s";
    private static final String VROPS_SSH_COMMAND_3 = "$VMWARE_PYTHON_3_BIN %s %s %s %s %s %s %s";
    private static final String VROPS_SSH_COMMAND_4 = "$VMWARE_PYTHON_3_BIN %s %s %s %s %s %s %s %s";

    private final Logger logger = LoggerFactory.getLogger(CliManagerVrops.class);

    private ConfigurationVrops config;
    private Session session;
    private List<String> cmdList;
    private List<File> fileList;
    private final String exportRemotePath;
    private final String importRemotePath;

    public CliManagerVrops(ConfigurationVrops config) {
        this.config = config;
        cmdList = new ArrayList<>();
        fileList = new ArrayList<>();
        String runId = System.currentTimeMillis() + "-" + UUID.randomUUID();
        exportRemotePath = "/tmp/vrops-export/" + runId;
        importRemotePath = "/tmp/vrops-import/" + runId;
    }

    public void connect() throws JSchException {
        session = SshClient.createSession(getSshUsername(), getSshPassword(), config.getHost(), getSshPort());
        session.connect();
        logger.info("SSH Session opened");
    }

    public String getSshUsername() {
        return config.getUsername();
    }

    public String getSshPassword() {
        return config.getPassword();
    }

    public int getSshPort() {
        return config.getSshPort();
    }

    public boolean hasAnyCommands() {
        return !this.cmdList.isEmpty();
    }

    @Override
    public void close() {
        fileList = new ArrayList<>();
        cmdList = new ArrayList<>();
        if (session != null) {
            session.disconnect();
        }
        session = null;
    }

    public void cleanup() {
        if (session != null) {
            StringBuilder commands = new StringBuilder();
            commands.append("rm -r " + exportRemotePath + ";");
            commands.append("rm -r " + importRemotePath + ";");
            SshClient.execute(session, commands.toString());
        }
    }

    // IMPORT
    public void addViewToImportList(File file) {
        String remoteFilePath = importRemotePath + UNIX_PATH_SEPARATOR + file.getName();
        String command = String.format(VROPS_SSH_COMMAND_1,
                escapeShellCharacters(OPSCLI_PATH),    escapeShellCharacters(VIEW),
                escapeShellCharacters(IMPORT),         escapeShellCharacters(remoteFilePath),
                escapeShellCharacters(FORCE));

        cmdList.add(command);
        fileList.add(file);
    }

    public void addDashboardToImportList(File file) {
        String remoteFilePath = importRemotePath + UNIX_PATH_SEPARATOR + file.getName();
        String command = String.format(VROPS_SSH_COMMAND_2,
                escapeShellCharacters(OPSCLI_PATH),    escapeShellCharacters(DASHBOARD),
                escapeShellCharacters(IMPORT),         "all",
                escapeShellCharacters(remoteFilePath), escapeShellCharacters(FORCE));

        cmdList.add(command);
        fileList.add(file);
    }

    public void shareDashboard(String dashboard, String[] groups) {
        String command = String.format(VROPS_SSH_COMMAND_2,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                escapeShellCharacters(SHARE), escapeShellCharacters(SHARING_DEFAULT_USER),
                escapeShellCharacters(dashboard), escapeShellCharacters(String.join(", ", groups)));
        try {
            logger.info("Sharing dashboards using command:\n{}", command);
            List<String> output = SshClient.execute(session, command);
            output.stream().forEach(logger::info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unshareDashboard(String dashboard, String[] groups) {
        String command = String.format(VROPS_SSH_COMMAND_2,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                escapeShellCharacters(UNSHARE), escapeShellCharacters(SHARING_DEFAULT_USER),
                escapeShellCharacters(dashboard), escapeShellCharacters(String.join(", ", groups)));
        try {
            logger.info("Unsharing dashboards using command:\n{}", command);
            List<String> output = SshClient.execute(session, command);
            output.stream().forEach(logger::info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void activateDashboard(String dashboard, List<String> resources, boolean isGroupResource) {
    	List<String> commands = new ArrayList<String>();
    	for (String resource : resources) {
        	if (isGroupResource) {
                commands.add(String.format(VROPS_SSH_COMMAND_1,
                        escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                        escapeShellCharacters(ACTIVATE), "group:"+escapeShellCharacters(resource),
                        escapeShellCharacters(dashboard)));
        	} else {
                commands.add(String.format(VROPS_SSH_COMMAND_1,
                        escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                        escapeShellCharacters(ACTIVATE), escapeShellCharacters(resource),
                        escapeShellCharacters(dashboard)));
        	}
    	}
    	try {
            logger.info("Activating dashboards using command(s):\n{}", String.join(";\n", commands));
            List<String> output = SshClient.execute(session, String.join(";", commands));
            output.stream().forEach(logger::info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deactivateDashboard(String dashboard, List<String> resources, boolean isGroupResource) {
    	List<String> commands = new ArrayList<String>();
    	for (String resource : resources) {
        	if (isGroupResource) {
                commands.add(String.format(VROPS_SSH_COMMAND_1,
                        escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                        escapeShellCharacters(DEACTIVATE), "group:"+escapeShellCharacters(resource),
                        escapeShellCharacters(dashboard)));
        	} else {
                commands.add(String.format(VROPS_SSH_COMMAND_1,
                        escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(DASHBOARD),
                        escapeShellCharacters(DEACTIVATE), escapeShellCharacters(resource),
                        escapeShellCharacters(dashboard)));
        	}
    	}

    	try {
            logger.info("Deactivating dashboards using command(s):\n{}", String.join(";\n", commands));
            List<String> output = SshClient.execute(session, String.join(";", commands));
            output.stream().forEach(logger::info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addReportToImportList(File file) {
        String remoteFilePath = importRemotePath + UNIX_PATH_SEPARATOR + file.getName();
        String command = String.format(VROPS_SSH_COMMAND_1,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(REPORT),
                escapeShellCharacters(IMPORT),      escapeShellCharacters(remoteFilePath),
                escapeShellCharacters(FORCE));

        cmdList.add(command);
        fileList.add(file);
    }

    public void addSuperMetricsToImportList(File file) {
        String remoteFilePath = importRemotePath + UNIX_PATH_SEPARATOR + file.getName();
        String command = String.format(VROPS_SSH_COMMAND_3,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(SUPER_METRIC),
                escapeShellCharacters(IMPORT), escapeShellCharacters(remoteFilePath), escapeShellCharacters(FORCE), ALL_POLICIES, CHECK_FALSE);

        this.cmdList.add(command);
        this.fileList.add(file);
    }

    public void addMetricConfigsToImportList(File file) {
        String remoteFilePath = importRemotePath + UNIX_PATH_SEPARATOR + file.getName();
        String command = String.format(VROPS_SSH_COMMAND_4,
                escapeShellCharacters(OPSCLI_PATH),
                escapeShellCharacters(FILE),
                escapeShellCharacters(IMPORT), 
                escapeShellCharacters(METRIC_CONFIG),
                escapeShellCharacters(remoteFilePath), 
                escapeShellCharacters(FORCE), 
                ALL_POLICIES, 
                CHECK_FALSE);

        this.cmdList.add(command);
        this.fileList.add(file);
    }

    public void importFilesToVrops() {
        try {
            logger.info("Copying files to vrops appliance");

            reconnect();
            SshClient.copyLocalToRemote(session, fileList, importRemotePath);

            String remoteCommands = String.join(";", cmdList);
            logger.info("Executing vROps SSH remote command(s):\n{}", String.join("\n", cmdList));

            List<String> output = SshClient.execute(session, remoteCommands);
            output.stream().forEach(logger::info);
        } catch (JSchException | SftpException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void reconnect() throws JSchException {
        if (session != null && session.isConnected()) {
            return;
        }
        logger.warn("SSH session is closed, trying to reconnect");
        connect();
    }

    // EXPORT
	public void exportView(String viewName, File localDir) throws JSchException {
        String remoteFilePath = exportRemotePath + UNIX_PATH_SEPARATOR + "views";
        String command = String.format(VROPS_SSH_COMMAND_1,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(VIEW),
                escapeShellCharacters(EXPORT), escapeShellCharacters(viewName),
                escapeShellCharacters(remoteFilePath));
        logger.info(VROPS_SSH_COMMAND_INFO, command);

        reconnect();
        List<String> output = SshClient.execute(session, command);
        output.stream().forEach(logger::info);

        List<String> files = new ArrayList<>();
        files.add(remoteFilePath + UNIX_PATH_SEPARATOR + viewName + ".zip");

        SshClient.copyRemoteToLocal(session, files, localDir);
    }

    public void exportDashboard(String dashboardName, File localDir) throws JSchException {
        String remoteFilePath = exportRemotePath + UNIX_PATH_SEPARATOR + "dashboards";
        String command = String.format(VROPS_SSH_COMMAND_2,
                escapeShellCharacters(OPSCLI_PATH),   escapeShellCharacters(DASHBOARD),
                escapeShellCharacters(EXPORT),        escapeShellCharacters(config.getVropsDashboardUser()),
                escapeShellCharacters(dashboardName), escapeShellCharacters(remoteFilePath));
        logger.info(VROPS_SSH_COMMAND_INFO, command);

        reconnect();
        List<String> output = SshClient.execute(session, command);
        output.stream().forEach(logger::info);

        List<String> files = new ArrayList<>();
        files.add(remoteFilePath + UNIX_PATH_SEPARATOR + dashboardName + ".zip");

        SshClient.copyRemoteToLocal(session, files, localDir);
	}

    public void exportSuperMetric(String superMetricName, File localDir) throws JSchException {
        String remoteFilePath = exportRemotePath + UNIX_PATH_SEPARATOR + "supermetrics";
        String command = String.format(VROPS_SSH_COMMAND_1,
                escapeShellCharacters(OPSCLI_PATH), escapeShellCharacters(SUPER_METRIC),
                escapeShellCharacters(EXPORT), escapeShellCharacters(superMetricName), escapeShellCharacters(remoteFilePath));
        logger.info(VROPS_SSH_COMMAND_INFO, command);

        reconnect();
        List<String> output = SshClient.execute(this.session, command);
        output.stream().forEach(logger::info);

        List<String> files = new ArrayList<>();
        files.add(remoteFilePath + UNIX_PATH_SEPARATOR + superMetricName + ".json");

        SshClient.copyRemoteToLocal(session, files, localDir);
    }

    public void exportMetricConfig(String metricConfigName, File localDir) throws JSchException {
        String remoteFilePath = exportRemotePath + UNIX_PATH_SEPARATOR + "metricconfigs";

        String command = String.format(VROPS_SSH_COMMAND_2,
                escapeShellCharacters(OPSCLI_PATH),
                escapeShellCharacters(FILE),
                escapeShellCharacters(EXPORT),
                escapeShellCharacters(METRIC_CONFIG),
                escapeShellCharacters(metricConfigName),
                escapeShellCharacters(remoteFilePath));
        logger.info(VROPS_SSH_COMMAND_INFO, command);

        reconnect();
        List<String> output = SshClient.execute(this.session, command);
        output.stream().forEach(logger::info);

        List<String> files = new ArrayList<>();
        files.add(remoteFilePath + UNIX_PATH_SEPARATOR + metricConfigName);
        
        SshClient.copyRemoteToLocal(session, files, localDir);
    }

    public void exportReport(String reportName, File localDir) throws JSchException {
        String remoteFilePath = exportRemotePath + UNIX_PATH_SEPARATOR + "reports";
        String command = String.format(VROPS_SSH_COMMAND_1,
                escapeShellCharacters(OPSCLI_PATH),
                escapeShellCharacters(REPORT), escapeShellCharacters(EXPORT),
                escapeShellCharacters(reportName), escapeShellCharacters(remoteFilePath));
        logger.info(VROPS_SSH_COMMAND_INFO, command);

        reconnect();
        List<String> output = SshClient.execute(session, command);
        output.stream().forEach(logger::info);

        List<String> files = new ArrayList<>();
        files.add(remoteFilePath + UNIX_PATH_SEPARATOR + reportName + ".zip");

        SshClient.copyRemoteToLocal(session, files, localDir);
	}

	@Override
	public String toString() {
        return getSshUsername() + "@" + config.getHost() + ":" + getSshPort();
    }

    public String toSshComand() {
        return "ssh \'" + getSshUsername() + "@" + config.getHost() + "' -p " + getSshPort();
    }


    private String escapeShellCharacters(String str) {
        if (str == null) {
            return null;
        }
        if (str.indexOf('\'') == -1) {
            return "'" + str + "'";
        }
        final char[] special = {'\'', '~', '`', '#', '$', '&', '*', '(', ')', '\\', '|', '[', ']', '{', '}', ';', '"',
                '<', '>', '?', '!' };
        StringBuilder buffer = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            for (char element : special) {
                if (chr == element) {
                    buffer.append("\\");
                }
                buffer.append(chr);
            }
        }
        buffer.append("\"");

        return buffer.toString();
    }

}
