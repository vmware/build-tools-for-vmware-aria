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
package com.vmware.pscoe.iac.artifact.aria.operations.store;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class VropsContentBundleTest {
	private final ObjectMapper mapper = new ObjectMapper();

	@TempDir
	File tempDir;

	@Test
	void writeDashboardBundleForEachOwner() throws IOException {
		// GIVEN
		File dashboardZip = writeFile("dashboard.zip", "dashboard-zip-content");
		VropsContentBundle bundle = new VropsContentBundle().setDashboard(dashboardZip, Arrays.asList("admin", "user1"));

		// WHEN
		File bundleZip = bundle.write(new File(tempDir, "bundle.zip"));

		// THEN
		try (ZipFile zip = new ZipFile(bundleZip)) {
			assertTrue(zip.getEntry(VropsContentBundle.FORMAT_MARKER_ENTRY) != null);
			String adminId = VropsContentBundle.getOwnerId("admin");
			String userId = VropsContentBundle.getOwnerId("user1");
			assertArrayEquals("dashboard-zip-content".getBytes(StandardCharsets.UTF_8),
					read(zip, VropsContentBundle.DASHBOARDS_DIR + adminId));
			assertArrayEquals("dashboard-zip-content".getBytes(StandardCharsets.UTF_8),
					read(zip, VropsContentBundle.DASHBOARDS_DIR + userId));

			Map<String, Object> userMappings = readJson(zip, VropsContentBundle.USER_MAPPINGS_ENTRY);
			List<?> users = (List<?>) userMappings.get("users");
			assertEquals(2, users.size());
			assertEquals("admin", ((Map<?, ?>) users.get(0)).get("userName"));
			assertEquals(adminId, ((Map<?, ?>) users.get(0)).get("userId"));
			assertEquals("user1", ((Map<?, ?>) users.get(1)).get("userName"));

			Map<String, Object> configuration = readJson(zip, VropsContentBundle.CONFIGURATION_ENTRY);
			assertEquals(2, configuration.get("dashboards"));
			assertNull(zip.getEntry(VropsContentBundle.SUPER_METRICS_ENTRY));
			assertNull(zip.getEntry(VropsContentBundle.VIEWS_ENTRY));
		}
	}

	@Test
	void writeMergesSuperMetricsAndAddsMetricConfigs() throws IOException {
		// GIVEN
		File superMetric1 = writeFile("sm1.json", "{\"id-1\": {\"name\": \"SM 1\", \"formula\": \"1\"}}");
		File superMetric2 = writeFile("sm2.json", "{\"id-2\": {\"name\": \"SM 2\", \"formula\": \"2\"}}");
		File metricConfig = writeFile("Host-Capacity", "<AdapterKinds/>");
		VropsContentBundle bundle = new VropsContentBundle().addSuperMetrics(superMetric1).addSuperMetrics(superMetric2)
				.addMetricConfig(metricConfig);

		// WHEN
		File bundleZip = bundle.write(new File(tempDir, "bundle.zip"));

		// THEN
		try (ZipFile zip = new ZipFile(bundleZip)) {
			Map<String, Object> superMetrics = readJson(zip, VropsContentBundle.SUPER_METRICS_ENTRY);
			assertEquals(2, superMetrics.size());
			assertEquals("SM 2", ((Map<?, ?>) superMetrics.get("id-2")).get("name"));
			assertArrayEquals("<AdapterKinds/>".getBytes(StandardCharsets.UTF_8),
					read(zip, VropsContentBundle.METRIC_CONFIGS_DIR + "Host-Capacity"));

			Map<String, Object> configuration = readJson(zip, VropsContentBundle.CONFIGURATION_ENTRY);
			assertEquals(2, configuration.get("superMetrics"));
			assertEquals(1, configuration.get("configFiles"));
			assertNull(zip.getEntry(VropsContentBundle.USER_MAPPINGS_ENTRY));
		}
	}

	@Test
	void writeViewAndReport() throws IOException {
		// GIVEN
		File viewZip = writeFile("view.zip", "view-zip-content");
		File reportZip = writeFile("report.zip", "report-zip-content");
		VropsContentBundle bundle = new VropsContentBundle().setView(viewZip).setReport(reportZip);

		// WHEN
		File bundleZip = bundle.write(new File(tempDir, "bundle.zip"));

		// THEN
		try (ZipFile zip = new ZipFile(bundleZip)) {
			assertArrayEquals("view-zip-content".getBytes(StandardCharsets.UTF_8),
					read(zip, VropsContentBundle.VIEWS_ENTRY));
			assertArrayEquals("report-zip-content".getBytes(StandardCharsets.UTF_8),
					read(zip, VropsContentBundle.REPORTS_ENTRY));
		}
	}

	@Test
	void isEmptyOnlyWhenNothingIsAdded() throws IOException {
		assertTrue(new VropsContentBundle().isEmpty());
		assertFalse(new VropsContentBundle().setView(writeFile("view.zip", "x")).isEmpty());
	}

	@Test
	void setDashboardRequiresOwner() throws IOException {
		File dashboardZip = writeFile("dashboard.zip", "x");
		assertThrows(IllegalArgumentException.class,
				() -> new VropsContentBundle().setDashboard(dashboardZip, Collections.emptyList()));
	}

	private File writeFile(final String name, final String content) throws IOException {
		File file = new File(tempDir, name);
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	private static byte[] read(final ZipFile zip, final String entryName) throws IOException {
		ZipEntry entry = zip.getEntry(entryName);
		assertTrue(entry != null, "Missing bundle entry " + entryName);
		return zip.getInputStream(entry).readAllBytes();
	}

	private Map<String, Object> readJson(final ZipFile zip, final String entryName) throws IOException {
		Map<String, Object> json = mapper.readValue(read(zip, entryName), new TypeReference<HashMap<String, Object>>() {
		});
		return json;
	}
}
