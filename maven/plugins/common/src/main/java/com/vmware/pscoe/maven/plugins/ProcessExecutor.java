package com.vmware.pscoe.maven.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

public class ProcessExecutor {
	private String name;
	private boolean throwOnError;
	private String errorMessage;
	private boolean silent;
	private final ProcessBuilder processBuilder;

	public ProcessExecutor() {
		name = "Process";
		processBuilder = new ProcessBuilder();
	}

	public ProcessExecutor name(String name) {
		this.name = name;
		return this;
	}

	public ProcessExecutor directory(File directory) {
		processBuilder.directory(directory);
		return this;
	}

	public ProcessExecutor throwOnError(Boolean value) {
		return throwOnError(value, null);
	}

	public ProcessExecutor throwOnError(Boolean value, String message) {
		throwOnError = value;
		errorMessage = message;
		return this;
	}

	public ProcessExecutor silent(Boolean value) {
		silent = value;
		return this;
	}

	public ProcessExecutor command(String... command) {
		processBuilder.command(command);
		return this;
	}

	public ProcessExecutor command(List<String> command) {
		processBuilder.command(command);
		return this;
	}

	public int execute(Log logger) throws MojoExecutionException, MojoFailureException {
		if (!silent) {
			logger.info(name + " started");
		}
		int exitCode = 0;
		try {
			Process process = processBuilder.start();
			AsyncStreamLogger inputStreamLogger = new AsyncStreamLogger(process.getInputStream(), logger::info);
			AsyncStreamLogger errorStreamLogger = new AsyncStreamLogger(process.getErrorStream(),
					throwOnError ? logger::error : logger::debug);
			inputStreamLogger.start();
			errorStreamLogger.start();
			exitCode = process.waitFor();
			inputStreamLogger.join();
			errorStreamLogger.join();
		} catch (InterruptedException e) {
			logger.error(String.format("%s was interrupted.", name), e);
		} catch (IOException e) {
			throw new MojoExecutionException(String.format("%s failed.", name), e);
		}
		if (!silent) {
			logger.info(name + " finished");
		}
		if (throwOnError && exitCode != 0) {
			throw new MojoFailureException(
					errorMessage != null ? errorMessage : String.format("%s failed with code %s", name, exitCode));
		}
		return exitCode;
	}

	interface Logger {
		void log(CharSequence content);
	}

	class AsyncStreamLogger extends Thread {
		private final InputStream stream;
		private final Logger logger;

		AsyncStreamLogger(InputStream stream, Logger logger) {
			this.stream = stream;
			this.logger = logger;
		}

		@Override
		public void run() {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(stream));
				String line = null;
				while ((line = reader.readLine()) != null) {
					logger.log(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
