package com.vmware.pscoe.iac.artifact.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.vmware.pscoe.iac.artifact.configuration.Configuration;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVcd;
import com.vmware.pscoe.iac.artifact.model.Package;
import com.vmware.pscoe.iac.artifact.model.PackageFactory;
import com.vmware.pscoe.iac.artifact.model.PackageType;
import com.vmware.pscoe.iac.artifact.model.vcd.VcdNgPackageManifest;
import com.vmware.pscoe.iac.artifact.rest.helpers.VcdApiHelper;
import com.vmware.pscoe.iac.artifact.rest.model.VcdPluginMetadataDTO;
import com.vmware.pscoe.iac.artifact.rest.model.VcdPluginResourceDTO;

import net.minidev.json.JSONArray;

public class RestClientVcd extends RestClient {
	private static final PackageType packageType = PackageType.VCDNG;

	private final Logger logger = LoggerFactory.getLogger(RestClientVcd.class);

	private ConfigurationVcd configuration;
	private RestTemplate restTemplate;
	private String apiVersion;

	private static final String URL_VERSIONS = "api/versions";
	private static final String URL_UI_EXTENSION_BASE = "cloudapi/extensions/ui";
	private static final String URL_UI_EXTENSION_BY_ID = URL_UI_EXTENSION_BASE + "/%s";
	private static final String URL_UI_PLUGIN_BY_ID = URL_UI_EXTENSION_BASE + "/%s/plugin";
	private static final String URL_UI_PLUGIN_PUBLISH_ALL = URL_UI_EXTENSION_BASE + "/%s/tenants/publishAll";

	protected RestClientVcd(ConfigurationVcd configuration, RestTemplate restTemplate) {
		this.configuration = configuration;
		this.restTemplate = restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@Override
	protected Configuration getConfiguration() {
		return this.configuration;
	}

	private HttpEntity<String> getVcdHttpEntity() {
		HttpHeaders headers = getCommonVcdHeaders();
		return new HttpEntity<String>(headers);
	}

	private HttpHeaders getCommonVcdHeaders() {
		HttpHeaders headers = new HttpHeaders();
		MediaType contentType = VcdApiHelper.buildMediaType("application/json", apiVersion);

		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(contentType);
		headers.setAccept(acceptableMediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);

		return headers;
	}

	@Override
	public String getVersion() {
		if (this.apiVersion == null) {
			URI url = getURI(getURIBuilder().setPath(URL_VERSIONS));

			HttpHeaders headers = new HttpHeaders();
			MediaType contentType = VcdApiHelper.buildMediaType("application/*+json", null);

			List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
			acceptableMediaTypes.add(contentType);
			headers.setAccept(acceptableMediaTypes);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<String>(headers), String.class);
			JSONArray versionArray = JsonPath.parse(response.getBody()).read("$.versionInfo[*].version");
			this.apiVersion = versionArray.get(versionArray.size() - 1).toString();
			logger.debug("API version is: " + this.apiVersion);
		}

		return this.apiVersion;
	}

	public List<Package> getAllUiExtensions() {
		URI url = getURI(getURIBuilder().setPath(URL_UI_EXTENSION_BASE));
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getVcdHttpEntity(), String.class);
		List<String> ids = JsonPath.parse(response.getBody()).read("$[*].id", List.class);
		List<Package> extensions = new ArrayList<Package>();

		for (String id : ids) {
			extensions.add(getUiExtension(id));
		}

		return extensions;
	}
	
	public Package getUiExtension(String id) {
		logger.debug("Getting UI extension for ID [" + id + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_EXTENSION_BY_ID, id)));

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getVcdHttpEntity(), String.class);
		String pluginId = JsonPath.parse(response.getBody()).read("$.id");
		String pluginName = JsonPath.parse(response.getBody()).read("$.pluginName");
		String pluginVersion = JsonPath.parse(response.getBody()).read("$.version");

		logger.debug("UI extension for ID [" + id + "] retrieved.");
		return PackageFactory.getInstance(packageType, pluginId,
				new File(pluginName + "-" + pluginVersion + "." + packageType));
	}

	public Package getUiExtension(Package localPkg) {
		List<Package> extensions = getAllUiExtensions();
		for (Package remotePkg : extensions) {
			if (localPkg.getName().equals(remotePkg.getName())) {
				return remotePkg;
			}
		}

		return null;
	}

	public Package addUiExtension(Package pkg) {
		logger.debug("Adding UI extension for [" + pkg + "]...");
		VcdNgPackageManifest manifest = VcdNgPackageManifest.getInstance(pkg);
		String requestBody = new Gson().toJson(new VcdPluginMetadataDTO(manifest));

		URI url = getURI(getURIBuilder().setPath(URL_UI_EXTENSION_BASE));
		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		String pluginId = JsonPath.parse(response.getBody()).read("$.id");
		logger.debug("UI extension for [" + pkg + "] added.");
		return getUiExtension(pluginId);
	}

	public void removeUiExtension(Package remotePkg) {
		logger.debug("Removing UI extension for [" + remotePkg + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_EXTENSION_BY_ID, remotePkg.getId())));
		restTemplate.exchange(url, HttpMethod.DELETE, this.getVcdHttpEntity(), String.class);
		logger.debug("UI extension for [" + remotePkg + "] removed.");
	}

	public void uploadUiPlugin(Package localPkg, Package remotePkg) {
		logger.debug("Uploading plugin resource for [" + remotePkg + "].");
		File pkgFile = new File(localPkg.getFilesystemPath());
		String requestBody = new Gson().toJson(new VcdPluginResourceDTO(pkgFile.getName(), pkgFile.length()));

		// Get upload link
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_BY_ID, remotePkg.getId())));
		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String linkHeader = response.getHeaders().get("link").get(0);
		String uploadLink = linkHeader.substring(1, linkHeader.indexOf(">"));

		// Upload package
		url = getURI(getURIBuilder().setPath(uploadLink.substring(uploadLink.indexOf("transfer"))));
		headers = getCommonVcdHeaders();
		headers.setContentType(VcdApiHelper.buildMediaType("application/zip", null));

		byte[] pkgContent;
		try {
			pkgContent = IOUtils.toByteArray(new FileInputStream(pkgFile));
		} catch (IOException e) {
			throw new RuntimeException("Unable to find plugin file " + localPkg.getFilesystemPath(), e);
		}
		HttpEntity<byte[]> pluginEntity = new HttpEntity<>(pkgContent, headers);

		restTemplate.exchange(url, HttpMethod.PUT, pluginEntity, String.class);
		logger.debug("Plugin resource for [" + remotePkg + "] uploaded.");
	}

	public void deleteUiPlugin(Package remotePkg) {
		logger.debug("Deleting plugin resource for [" + remotePkg + "]...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_BY_ID, remotePkg.getId())));
		restTemplate.exchange(url, HttpMethod.DELETE, this.getVcdHttpEntity(), String.class);
		logger.debug("Plugin resource for [" + remotePkg + "] deleted.");
	}
	
	public Package addOrReplaceUiPlugin(Package pkg) {
		Package remotePkg = this.getUiExtension(pkg);
		if (remotePkg != null) {
			this.deleteUiPlugin(remotePkg);
			this.removeUiExtension(remotePkg);
		}

		remotePkg = this.addUiExtension(pkg);
		this.uploadUiPlugin(pkg, remotePkg);
		this.publishUiPlugin(remotePkg);
		
		return remotePkg;
	}
	
	public void publishUiPlugin(Package remotePkg) {
		logger.debug("Publishing UI extension [" + remotePkg + "] to all tenants...");
		URI url = getURI(getURIBuilder().setPath(String.format(URL_UI_PLUGIN_PUBLISH_ALL, remotePkg.getId())));

		HttpHeaders headers = getCommonVcdHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		logger.debug("UI extension [" + remotePkg + "] published to all tenants.");
	}
}
