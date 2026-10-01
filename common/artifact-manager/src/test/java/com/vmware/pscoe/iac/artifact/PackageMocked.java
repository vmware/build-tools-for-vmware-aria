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
package com.vmware.pscoe.iac.artifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * PackageMocked mock class for vrops packages.
 */
public final class PackageMocked {

	/**
	 * PackageMocked() private constructor.
	 */
	private PackageMocked() {
	}

	/**
	 * createSampleViewsZip() create a zip file with views.
	 * 
	 * @param dir directory where to put file.
	 * @return file handle to the generated file.
	 * @throws IOException if creation fails.
	 */
	public static File createSampleViewsZip(File dir) throws IOException {
		String contentXml = "<Content><Views><ViewDef id=\"123\"></ViewDef></Views></Content>";
		String resourceProp = "view.123.title: value\nview.123.something: value2\n";

		File tempZip = new File(dir, UUID.randomUUID() + ".zip");
		FileOutputStream fos = new FileOutputStream(tempZip);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		ZipEntry contentZipEntry = new ZipEntry("content.xml");
		zipOut.putNextEntry(contentZipEntry);
		zipOut.write(contentXml.getBytes(StandardCharsets.UTF_8));

		ZipEntry resourcePropZipEntry = new ZipEntry("resources/resources.properties");
		zipOut.putNextEntry(resourcePropZipEntry);
		zipOut.write(resourceProp.getBytes(StandardCharsets.UTF_8));

		zipOut.close();
		fos.close();
		return tempZip;
	}

	/**
	 * createSamplePackageZip() create a mocked package zip file.
	 * 
	 * @param dir                 directory where to put the package file.
	 * @param viewName            name to be generated.
	 * @param viewId              id to be generated.
	 * @param dashboardName       name of the dashboard to be generated.
	 * @param alertDefinitionName name of the alert definition to be generated.
	 * @return file handle to the generated file.
	 * @throws IOException if creation fails.
	 */
	public static File createSamplePackageZip(File dir, String viewName, String viewId, String dashboardName, String alertDefinitionName) throws IOException {
		String contentXml = "<Content><Views><ViewDef id=\"" + viewId + "\"></ViewDef></Views></Content>";
		// content.yaml must list exactly what the package contains (view + alert-definition).
		// No policy entry because there is no policies/<name>.zip file in the package.
		String contentYaml = "---\nview:\n  - " + viewName + "\nalert-definition:\n  - " + alertDefinitionName + "\ndefault-policy: policy1\n";

		String resourceProp = "view." + viewId + ".title: value\nview." + viewId + ".something: value2\n";

		String dashboardsJson = "{\"dashboards\": [{\"autoswitchEnabled\": false}]}";
		String dashResourceProp = dashboardName + "=" + dashboardName + "\n" + dashboardName + ".Something=Somevalue";

		String alertDefsJson = "{\"id\": \"1\"}";
		String shareMetadata = "{ \"share\": {\"" + dashboardName + "\" : [\"group1\"]}, \"unshare\" : {} }";
		String activateUserMetadata = "{ \"activate\": {\"" + dashboardName + "\" : [\"user1\"]}, \"deactivate\" : {} }";
		String activateGroupMetadata = "{ \"activate\": {\"" + dashboardName + "\" : [\"group1\"]}, \"deactivate\" : {} }";

		File tempZip = new File(dir, UUID.randomUUID() + ".zip");
		FileOutputStream fos = new FileOutputStream(tempZip);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		ZipEntry contentZipEntry = new ZipEntry("/views/" + viewName + ".xml");
		zipOut.putNextEntry(contentZipEntry);
		zipOut.write(contentXml.getBytes(StandardCharsets.UTF_8));

		ZipEntry contentYamlZipEntry = new ZipEntry("/content.yaml");
		zipOut.putNextEntry(contentYamlZipEntry);
		zipOut.write(contentYaml.getBytes(StandardCharsets.UTF_8));

		ZipEntry resourcePropZipEntry = new ZipEntry("/views/resources/content.properties");
		zipOut.putNextEntry(resourcePropZipEntry);
		zipOut.write(resourceProp.getBytes(StandardCharsets.UTF_8));

		ZipEntry dashboardZipEntry = new ZipEntry("/dashboards/" + dashboardName + ".xml");
		zipOut.putNextEntry(dashboardZipEntry);
		zipOut.write(dashboardsJson.getBytes(StandardCharsets.UTF_8));

		ZipEntry dashboardShareMetadataZipEntry = new ZipEntry("/dashboards/metadata/dashboardSharingMetadata.vrops.json");
		zipOut.putNextEntry(dashboardShareMetadataZipEntry);
		zipOut.write(shareMetadata.getBytes(StandardCharsets.UTF_8));

		ZipEntry dashboardUserActivateMetadataZipEntry = new ZipEntry("/dashboards/metadata/dashboardUserActivationMetadata.vrops.json");
		zipOut.putNextEntry(dashboardUserActivateMetadataZipEntry);
		zipOut.write(activateUserMetadata.getBytes(StandardCharsets.UTF_8));

		ZipEntry dashboardGroupActivateMetadataZipEntry = new ZipEntry("/dashboards/metadata/dashboardGroupActivationMetadata.vrops.json");
		zipOut.putNextEntry(dashboardGroupActivateMetadataZipEntry);
		zipOut.write(activateGroupMetadata.getBytes(StandardCharsets.UTF_8));

		resourcePropZipEntry = new ZipEntry("/dashboards/resources/resources.properties");
		zipOut.putNextEntry(resourcePropZipEntry);
		zipOut.write(resourceProp.getBytes(StandardCharsets.UTF_8));

		ZipEntry alertDefsZipEntry = new ZipEntry("/alert_definitions/" + alertDefinitionName + ".json");
		zipOut.putNextEntry(alertDefsZipEntry);
		zipOut.write(alertDefsJson.getBytes(StandardCharsets.UTF_8));

		zipOut.close();
		fos.close();

		return tempZip;
	}

	/**
	 * Creates a vROps package ZIP with the given content.yaml and arbitrary file entries.
	 * Use this for precise control over the package structure in validation tests.
	 *
	 * @param dir         directory where to write the generated ZIP file
	 * @param contentYaml content of the content.yaml file, or {@code null} to omit it
	 * @param entries     map of zip-entry path (e.g. "views/MyView.xml") to UTF-8 file content
	 * @return file handle to the generated ZIP
	 * @throws IOException if ZIP creation fails
	 */
	public static File createVropsPackageZip(File dir, String contentYaml,
			Map<String, String> entries) throws IOException {
		File tempZip = new File(dir, UUID.randomUUID() + ".zip");
		try (FileOutputStream fos = new FileOutputStream(tempZip);
				ZipOutputStream zipOut = new ZipOutputStream(fos)) {

			if (contentYaml != null) {
				ZipEntry yamlEntry = new ZipEntry("content.yaml");
				zipOut.putNextEntry(yamlEntry);
				zipOut.write(contentYaml.getBytes(StandardCharsets.UTF_8));
			}

			for (Map.Entry<String, String> entry : entries.entrySet()) {
				ZipEntry zipEntry = new ZipEntry(entry.getKey());
				zipOut.putNextEntry(zipEntry);
				zipOut.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
			}
		}
		return tempZip;
	}
}
