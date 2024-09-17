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
package com.vmware.pscoe.iac.artifact.model.vcd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vmware.pscoe.iac.artifact.PackageManager;

public class VcdNgPackageManifest {
	private String jsonData;
	
	private VcdNgPackageManifest(String jsonData) {
		this.jsonData = jsonData;
	}

	public class Scope {
		public static final String TENANT = "tenant";
		public static final String PROVIDER = "service-provider";
	}

	private JsonElement getJsonMember(String key) {
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
		JsonObject obj = gson.fromJson(this.jsonData, JsonObject.class);
		JsonElement member = obj.get(key);
		if (member == null) {
			throw new RuntimeException("Can't find key " + key + " in metadata.");
		}

		return member;
	}

	public String[] getAsArray(String key) {
		JsonElement member = getJsonMember(key);

		if (!member.isJsonArray()) {
			throw new RuntimeException("Expected metadata member [" + key + "] to be an array, but is not");
		}
		
		JsonArray jsonArray = member.getAsJsonArray();
		String[] list = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			list[i] = jsonArray.get(i).getAsString();
		}

		return list;
	}

	public String getAsString(String key) {
		return getJsonMember(key).getAsString();
	}

	public String getJsonData() {
		return jsonData;
	}

	public String getUrn() {
		return this.getAsString("urn");
	}

	public String getName() {
		return this.getAsString("name");
	}

	public String getContainerVersion() {
		return this.getAsString("containerVersion");
	}

	public String getVersion() {
		return this.getAsString("version");
	}

	public boolean isTenantScoped() {
		return Arrays.asList(this.getAsArray("scope")).contains(Scope.TENANT);
	}

	public boolean isProviderScoped() {
		return Arrays.asList(this.getAsArray("scope")).contains(Scope.PROVIDER);
	}

	public String[] getPermissions() {
		return this.getAsArray("permissions");
	}

	public String getDescription() {
		return this.getAsString("description");
	}

	public String getVendor() {
		return this.getAsString("vendor");
	}

	public String getLicense() {
		return this.getAsString("license");
	}

	public String getLink() {
		return this.getAsString("link");
	}

	public String getModule() {
		return this.getAsString("module");
	}

	public String getRoute() {
		return this.getAsString("route");
	}

	public static VcdNgPackageManifest getInstance(String manifestContent) {
		return new VcdNgPackageManifest(manifestContent);
	}

	public static VcdNgPackageManifest getInstance(com.vmware.pscoe.iac.artifact.model.Package vcdNgPackage) {
		PackageManager pm = new PackageManager(vcdNgPackage);

		String manifestContent;
		Path parentFolder = Paths.get(vcdNgPackage.getFilesystemPath()).getParent();
		Path unpackFolder = Paths.get(parentFolder.toString(), vcdNgPackage.getName() + "-manifest");
		Path manifestPath = Paths.get(parentFolder.toString(), vcdNgPackage.getName() + "-manifest", "manifest.json");

		try {
			pm.unpack(unpackFolder.toFile());
			manifestContent = VcdNgPackageManifest.readFileLineByLine(manifestPath);
			pm.cleanup(unpackFolder.toFile());
		} catch (IOException e) {
			throw new RuntimeException(
					"Error occurred while loading vCD Package Manifest for package [" + vcdNgPackage.getFQName() + "]",
					e);
		}

		return VcdNgPackageManifest.getInstance(manifestContent);
	}

	private static String readFileLineByLine(Path filePath) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			throw new IOException("Error while parsing manifest file.", e);
		}

		return contentBuilder.toString();
	}
}
