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
package com.vmware.pscoe.iac.artifact.ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshClient {
	private static final int CONNECT_TIMEOUT = 30000;
	private static final int INPUT_BUFFER_SIZE = 1024;
	private static final int THREAD_SLEEP_TIME = 1000;
	private static final String FILE_EXISTS_MESSAGE = "File exists";
	private static final String STRICT_HOST_CHECK = "no";
	private static final String CHANNEL_TYPE_EXEC = "exec";
	private static final String CHANNEL_TYPE_SFTP = "sftp";

	private static final Logger logger = LoggerFactory.getLogger(SshClient.class);

	private SshClient() {
	}

	public static Session createSession(String user, String password, String host, int port) {
		Session session = null;
		try {
			JSch jsch = new JSch();

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", STRICT_HOST_CHECK);

			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig(config);
		} catch (JSchException e) {
			String message = "JSch: Failed to create SSH Session with user '{}' at host '{}' on port '{}': {}."
					+ "Troubleshooting hint: try to execute 'ssh {}@{} -p {}' or check vROPs properties in maven settings.xml";
			logger.error(message, user, host, port, e.getMessage(), user, host, port);
		}

		return session;
	}

	public static List<String> execute(Session session, String command) {
		logger.debug("Execute | Session is connected: '{}'", session.isConnected());
		List<String> output = new ArrayList<>();
		Channel channel = null;
		InputStream in = null;
		try {
			channel = session.openChannel(CHANNEL_TYPE_EXEC);
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);

			final ByteArrayOutputStream errs = new ByteArrayOutputStream();
			((ChannelExec) channel).setErrStream(errs);
			final ByteArrayOutputStream outs = new ByteArrayOutputStream();
			((ChannelExec) channel).setOutputStream(outs);

			reconnectChannel(channel);
			in = channel.getInputStream();

			logger.debug("Execute | Channel is connected: '{}'", channel.isConnected());
			byte[] buffer = new byte[INPUT_BUFFER_SIZE];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(buffer, 0, INPUT_BUFFER_SIZE);
					if (i < 0) {
						break;
					}
					Arrays.stream(new String(buffer, 0, i).split("\\r?\\n")).forEach(line -> output.add(line));
				}

				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(THREAD_SLEEP_TIME);
				} catch (InterruptedException ee) {
					logger.warn("Sleep interrupted: {}.", ee.getMessage());
				}
			}

			output.add(new String(outs.toByteArray()));
			output.add(new String(errs.toByteArray()));
		} catch (Exception e) {
			logger.error("SSH: Failed to execute remote command: '{}' : '{}' : {}", command, e.getClass().getName(), e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					logger.warn("SSH: Failed to close reading from stdout for command '{}'. {}. Ignoring this error", command, ioe.getMessage());
				}
			}
			if (channel != null) {
				channel.disconnect();
			}
		}

		return output;
	}

	public static void copyLocalToRemote(Session session, List<File> fileList, String dest) throws JSchException, SftpException {
		final ChannelSftp sftpChannel = (ChannelSftp) session.openChannel(CHANNEL_TYPE_SFTP);
		try {
			reconnectChannel(sftpChannel);
			logger.info("Destination path: '{}'", dest);
			createDirectory(sftpChannel, dest, false);
			sftpChannel.cd(dest);
			fileList.forEach(file -> {
				String destinationFile = dest + "/" + file.getName();
				logger.info("Copy file with path '{}' to '{}'", file.getAbsolutePath(), destinationFile);
				try {
					sftpChannel.put(file.getAbsolutePath(), destinationFile);
				} catch (SftpException e) {
					logger.error("Failed to put file '{}' as remote file '{}' via SFTP session: {}", file.getAbsolutePath(), destinationFile, e.getMessage());
				}
			});
		} finally {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
			}
		}
	}

	public static void copyRemoteToLocal(Session session, List<String> remoteFiles, File localDir) throws JSchException {
		ChannelSftp sftpChannel = (ChannelSftp) session.openChannel(CHANNEL_TYPE_SFTP);
		try {
			reconnectChannel(sftpChannel);
			remoteFiles.forEach(remoteFile -> {
				try {
					logger.info("Copying file '{}' to '{}'", remoteFile, localDir.getAbsoluteFile());
					if (isFileExisting(sftpChannel, remoteFile)) {
						sftpChannel.get(remoteFile, localDir.getAbsolutePath());
					} else {
						logger.warn("Unable to get remote file '{}' as it does not exist", remoteFile);
					}
				} catch (SftpException e) {
					logger.error("Failed to get remote file '{}' into local directory '{}' via SFTP session: {}", remoteFile, localDir.getAbsolutePath(),
							e.getMessage());
				}
			});
		} finally {
			if (sftpChannel != null) {
				sftpChannel.disconnect();
			}
		}
	}

	public static void createDirectory(ChannelSftp sftpChannel, String directory, boolean forceDisconnect) {
		if (sftpChannel == null) {
			logger.error("Unable to open SFTP channel");
			return;
		}
		try {
			reconnectChannel(sftpChannel);
			StringBuilder path = new StringBuilder();
			for (String item : directory.split("[\\\\/]+")) {
				if (!StringUtils.isEmpty(item)) {
					path.append("/" + item);
					if (!isFileExisting(sftpChannel, path.toString())) {
						sftpChannel.mkdir(path.toString());
					}
				}
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (!message.contains(FILE_EXISTS_MESSAGE)) {
				logger.error("Failed to create directory '{}', {}", directory, e.getMessage());
			}
		} finally {
			if (forceDisconnect) {
				sftpChannel.disconnect();
			}
		}
	}

	private static boolean isFileExisting(ChannelSftp channel, String file) {
		try {
			reconnectChannel(channel);
			SftpATTRS fileStat = channel.stat(file);
			return (fileStat != null && fileStat.getATime() > 0);
		} catch (SftpException e) {
			return false;
		}
	}

	private static void reconnectChannel(Channel channel) {
		if (channel != null && !channel.isConnected()) {
			try {
				if (channel.getSession() != null && !channel.getSession().isConnected()) {
					channel.getSession().connect(CONNECT_TIMEOUT);
				}
				channel.connect(CONNECT_TIMEOUT);
			} catch (JSchException e) {
				logger.error("Unable to reconnect channel '{}' : {}", channel.getClass().getName(), e.getMessage());
			}
		}
	}
}
