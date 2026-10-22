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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class VropsContentBundleReaderTest {
	@TempDir
	File tempDir;

	private VropsContentBundleReader reader;
	private File outDir;

	@BeforeEach
	void init() throws IOException {
		reader = new VropsContentBundleReader(
				VropsContentExportMocked.createExportBundle(new File(tempDir, "export.zip")));
		outDir = new File(tempDir, "out");
	}

	@AfterEach
	void close() throws IOException {
		reader.close();
	}

	@Test
	void writeViewKeepsOnlyTheViewAndItsLocalization() throws IOException {
		// WHEN
		reader.writeView(VropsContentExportMocked.VIEW_1_ID, "My/View", outDir);

		// THEN
		String view = read(new File(outDir, "My%2FView.xml"));
		assertEquals(String.format(VropsContentExportMocked.XML_HEADER, "Views") + VropsContentExportMocked.VIEW_1
				+ String.format(VropsContentExportMocked.XML_FOOTER, "Views"), view);
		assertEquals("view.view-1.title=My View\n", read(new File(outDir, "resources/content.properties")));
		// the escaping of the localization is kept
		assertEquals("view.view-1.title=\\u30d3\\u30e5\\u30fc\n",
				read(new File(outDir, "resources/content_ja.properties")));
	}

	@Test
	void writeViewFailsWhenViewIsMissing() {
		assertThrows(IOException.class, () -> reader.writeView("missing", "Missing", outDir));
	}

	@Test
	void writeReportKeepsOnlyTheReportAndItsLocalization() throws IOException {
		// WHEN
		reader.writeReport(VropsContentExportMocked.REPORT_1_ID, "Report One", outDir);

		// THEN
		File reportDir = new File(outDir, "Report One");
		assertEquals(String.format(VropsContentExportMocked.XML_HEADER, "Reports") + VropsContentExportMocked.REPORT_1
				+ String.format(VropsContentExportMocked.XML_FOOTER, "Reports"), read(new File(reportDir, "content.xml")));
		assertEquals("report.report-1.title=Report One\n", read(new File(reportDir, "resources/content.properties")));
	}

	@Test
	void getDashboardNamesOfUser() throws IOException {
		assertEquals(Arrays.asList("Dash One", "Dash Two", "Dash Pending"), reader.getDashboardNames("admin"));
		assertEquals(Collections.emptyList(), reader.getDashboardNames("unknown"));
	}

	@Test
	void getIncompleteDashboardNamesOfUser() throws IOException {
		// dashboards without importComplete are considered completed
		assertEquals(Arrays.asList("Dash Pending"), reader.getIncompleteDashboardNames("admin"));
		assertEquals(Collections.emptyList(), reader.getIncompleteDashboardNames("unknown"));
	}

	@Test
	void writeDashboardKeepsAndRenumbersOnlyTheUsedEntries() throws IOException {
		// WHEN
		reader.writeDashboard("admin", "Dash One", outDir);

		// THEN
		File dashboardFile = new File(outDir, "Dash One.json");
		JsonNode dashboardJson = new ObjectMapper().readTree(dashboardFile);
		JsonNode resourceKinds = dashboardJson.path("entries").path("resourceKind");
		assertEquals(1, resourceKinds.size());
		assertEquals("VirtualMachine", resourceKinds.get(0).path("resourceKindKey").asText());
		assertEquals("resourceKind:id:0_::_", resourceKinds.get(0).path("internalId").asText());
		assertEquals("resource:id:0_::_",
				dashboardJson.path("entries").path("resource").get(0).path("internalId").asText());
		assertTrue(dashboardJson.path("entries").path("adapterKind").isMissingNode());

		JsonNode dashboard = dashboardJson.path("dashboards").get(0);
		JsonNode config = dashboard.path("widgets").get(0).path("config");
		assertEquals("resourceKind:id:0_::_", config.path("resourceKind").get(0).path("id").asText());
		// numbers are kept as they are
		assertTrue(read(dashboardFile).contains("\"ratio\" : 2.50"), read(dashboardFile));
		assertEquals(1, dashboardJson.path("dashboards").size());

		// the fields set by the server are not written
		assertEquals(Arrays.asList("entries", "dashboards"), fieldNames(dashboardJson));
		assertEquals(Arrays.asList("id", "name", "rank", "widgets"), fieldNames(dashboard));
		assertEquals("widget-1", dashboard.path("widgets").get(0).path("id").asText());
		assertTrue(read(dashboardFile).startsWith("{\n  \"entries\" : {\n"), read(dashboardFile));

		assertEquals("Dash\\ One=Dash One\nDash\\ One.widget=Widget\n",
				read(new File(outDir, "resources/resources.properties")));
	}

	@Test
	void writeSuperMetricInOpsCliFormat() throws IOException {
		// WHEN
		reader.writeSuperMetric("SM 2", outDir);

		// THEN
		assertEquals(Arrays.asList("SM 1", "SM 2"), reader.getSuperMetricNames());
		JsonNode superMetric = new ObjectMapper().readTree(new File(outDir, "SM 2.json"));
		assertEquals(Arrays.asList("sm-2"), fieldNames(superMetric));
		// the fields set by the server are not written
		assertEquals(Arrays.asList("name", "formula", "unitId"), fieldNames(superMetric.path("sm-2")));
	}

	@Test
	void writeMetricConfig() throws IOException {
		// WHEN
		reader.writeMetricConfig("sample.xml", outDir);

		// THEN
		assertEquals(Arrays.asList("Host-Capacity", "sample.xml"), reader.getMetricConfigNames());
		assertEquals("<b/>", read(new File(outDir, "sample.xml")));
	}

	@Test
	void getPropertyKeyUnescapesTheKey() {
		assertEquals("Dash One.widget", VropsContentBundleReader.getPropertyKey("Dash\\ One.widget=Widget"));
		assertEquals("view.a", VropsContentBundleReader.getPropertyKey("  view.a : b"));
		assertEquals("keyA", VropsContentBundleReader.getPropertyKey("key\\u0041=b"));
		assertNull(VropsContentBundleReader.getPropertyKey("# comment"));
		assertNull(VropsContentBundleReader.getPropertyKey("   "));
	}

	private static List<String> fieldNames(final JsonNode node) {
		List<String> names = new ArrayList<>();
		node.fieldNames().forEachRemaining(names::add);
		return names;
	}

	private static String read(final File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}
}
