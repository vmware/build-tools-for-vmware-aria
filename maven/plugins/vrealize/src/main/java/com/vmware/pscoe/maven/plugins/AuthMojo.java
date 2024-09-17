/*
 * #%L
 * vrealize-package-maven-plugin
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
package com.vmware.pscoe.maven.plugins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationException;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro.AuthProvider;
import com.vmware.pscoe.iac.artifact.rest.auth.VraSsoAuth;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.time.Instant;

@Mojo(name = "auth", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class AuthMojo extends AbstractIacMojo {
	@Parameter(required = true, property = "outputDir")
	private File outputDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		try {
			final Path tokenFolder = outputDir.toPath();
			if (!tokenFolder.toFile().isDirectory()) {
				throw new NotDirectoryException(tokenFolder.toString());
			}

			final ConfigurationVro vroConfig = getConfigurationForVro();
			final Path tokenFilePath = tokenFolder.resolve(vroConfig.getHost());
			final AuthProvider auth = vroConfig.getAuth();

			if (!AuthProvider.VRA.equals(auth)) {
				throw new MojoExecutionException("Unsupported authentication provider: " + auth);
			}

			final RestTemplate restTemplate = getVroRestClient().getRestTemplate();
			final VraSsoAuth vraSsoAuth = new VraSsoAuth(vroConfig, restTemplate);
			final VraSsoAuth.SsoToken ssoToken = vraSsoAuth.acquireToken();

			Files.createDirectories(tokenFolder);
			Files.deleteIfExists(tokenFilePath);

			try (Writer writer = Files.newBufferedWriter(tokenFilePath, StandardCharsets.UTF_8)) {
				final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class,
						(JsonSerializer<Instant>) (instant, type, ctx) ->
								new JsonPrimitive(instant.toString())).create();
				gson.toJson(ssoToken, writer);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write token file to disk.", e);
		} catch (ConfigurationException e) {
			throw new MojoExecutionException("Could not process the configuration", e);
		}
	}

}
