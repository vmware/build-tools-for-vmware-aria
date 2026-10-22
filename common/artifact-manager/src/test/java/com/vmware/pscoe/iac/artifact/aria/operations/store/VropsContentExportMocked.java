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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates content bundles in the format of the VCF Operations content
 * management export.
 */
final class VropsContentExportMocked {
	static final String ADMIN_ID = "a107e966-6030-4279-8038-aeb0c6aa89fe";
	static final String VIEW_1_ID = "view-1";
	static final String REPORT_1_ID = "report-1";

	static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<Content>\n"
			+ "        \n    <%s>\n                \n        ";
	static final String XML_FOOTER = "\n            \n    </%s>\n    \n</Content>\n";
	static final String VIEW_1 = "<ViewDef id=\"view-1\">\n            <Title>My/View</Title>\n        </ViewDef>";
	static final String VIEW_2 = "<ViewDef id=\"view-2\">\n            <Title>Other View</Title>\n        </ViewDef>";
	static final String REPORT_1 = "<ReportDef id=\"report-1\">\n            <Title localizationKey=\"title\"/>\n"
			+ "        </ReportDef>";
	static final String REPORT_2 = "<ReportDef id=\"report-2\">\n            <Title>Other</Title>\n        </ReportDef>";

	/**
	 * Dashboards of admin sharing the entries: "Dash One" refers to
	 * resourceKind:id:1 and resource:id:0, "Dash Two" refers to resourceKind:id:0.
	 * The import of "Dash Pending" is not completed.
	 */
	static final String DASHBOARDS_JSON = "{\"entries\": {"
			+ "\"resourceKind\": [{\"resourceKindKey\": \"HostSystem\", \"internalId\": \"resourceKind:id:0_::_\", "
			+ "\"adapterKindKey\": \"VMWARE\"}, {\"resourceKindKey\": \"VirtualMachine\", "
			+ "\"internalId\": \"resourceKind:id:1_::_\", \"adapterKindKey\": \"VMWARE\"}],"
			+ "\"adapterKind\": [],"
			+ "\"resource\": [{\"resourceKindKey\": \"Function\", \"internalId\": \"resource:id:0_::_\", "
			+ "\"adapterKindKey\": \"Container\", \"identifiers\": [], \"name\": \"Group\"}]},"
			+ "\"dashboards\": ["
			+ "{\"id\": \"dash-1\", \"name\": \"Dash One\", \"userId\": \"u\", \"lastUpdateUserId\": \"u\", "
			+ "\"creationTime\": 1, \"lastUpdateTime\": 2, \"importAttempts\": 0, \"importComplete\": true, "
			+ "\"rank\": 3, \"widgets\": [{\"id\": \"widget-1\", \"config\": {"
			+ "\"resourceKind\": [{\"id\": \"resourceKind:id:1_::_\"}], \"resource\": [{\"id\": \"resource:id:0_::_\"}],"
			+ "\"ratio\": 2.50}}]},"
			+ "{\"id\": \"dash-2\", \"name\": \"Dash Two\", \"widgets\": [{\"config\": {"
			+ "\"resourceKind\": [{\"id\": \"resourceKind:id:0_::_\"}]}}]},"
			+ "{\"id\": \"dash-3\", \"name\": \"Dash Pending\", \"importComplete\": false, "
			+ "\"widgets\": [{\"config\": {}}]}],"
			+ "\"uuid\": \"1d9271d6-0bd4-41dd-9deb-bdc295736d96\"}";
	static final String SUPER_METRICS_JSON = "{\"sm-1\": {\"name\": \"SM 1\", \"formula\": \"1\"},"
			+ "\"sm-2\": {\"name\": \"SM 2\", \"formula\": \"2\", \"modificationTime\": 1792593913632, "
			+ "\"modifiedBy\": \"a107e966\", \"unitId\": \"percent\"}}";

	private VropsContentExportMocked() {
	}

	/**
	 * Creates an export bundle with two of each: views, reports, dashboards (of
	 * admin), super metrics and metric configurations.
	 *
	 * @param target the bundle zip.
	 * @return the bundle zip.
	 * @throws IOException if writing fails.
	 */
	static File createExportBundle(final File target) throws IOException {
		byte[] views = zip(
				"content.xml", String.format(XML_HEADER, "Views") + VIEW_1 + "\n                \n        " + VIEW_2
						+ String.format(XML_FOOTER, "Views"),
				"resources/content.properties", "view.view-1.title=My View\nview.view-2.title=Other View\n",
				"resources/content_ja.properties", "view.view-1.title=\\u30d3\\u30e5\\u30fc\nview.view-2.title=x\n");
		byte[] reports = zip(
				"content.xml", String.format(XML_HEADER, "Reports") + REPORT_1 + "\n                \n        "
						+ REPORT_2 + String.format(XML_FOOTER, "Reports"),
				"resources/content.properties", "report.report-1.title=Report One\nreport.report-2.title=Other\n");
		byte[] dashboards = zip(
				"dashboard/dashboard.json", DASHBOARDS_JSON,
				"dashboard/resources/resources.properties",
				"Dash\\ One=Dash One\nDash\\ One.widget=Widget\nDash\\ Two=Dash Two\nDash\\ One\\ Copy=Copy\n");

		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target))) {
			put(zip, VropsContentBundle.FORMAT_MARKER_ENTRY, ADMIN_ID.getBytes(StandardCharsets.UTF_8));
			put(zip, VropsContentBundle.VIEWS_ENTRY, views);
			put(zip, VropsContentBundle.REPORTS_ENTRY, reports);
			put(zip, VropsContentBundle.DASHBOARDS_DIR + ADMIN_ID, dashboards);
			put(zip, VropsContentBundle.USER_MAPPINGS_ENTRY, ("{\"sources\": [], \"users\": [{\"userName\": \"admin\", "
					+ "\"userId\": \"" + ADMIN_ID + "\"}]}").getBytes(StandardCharsets.UTF_8));
			put(zip, VropsContentBundle.SUPER_METRICS_ENTRY, SUPER_METRICS_JSON.getBytes(StandardCharsets.UTF_8));
			put(zip, VropsContentBundle.METRIC_CONFIGS_DIR + "Host-Capacity", "<a/>".getBytes(StandardCharsets.UTF_8));
			put(zip, VropsContentBundle.METRIC_CONFIGS_DIR + "sample.xml", "<b/>".getBytes(StandardCharsets.UTF_8));
			put(zip, VropsContentBundle.CONFIGURATION_ENTRY, "{}".getBytes(StandardCharsets.UTF_8));
		}
		return target;
	}

	private static byte[] zip(final String... namesAndContents) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ZipOutputStream zip = new ZipOutputStream(out)) {
			for (int i = 0; i < namesAndContents.length; i += 2) {
				put(zip, namesAndContents[i], namesAndContents[i + 1].getBytes(StandardCharsets.UTF_8));
			}
		}
		return out.toByteArray();
	}

	private static void put(final ZipOutputStream zip, final String name, final byte[] content) throws IOException {
		zip.putNextEntry(new ZipEntry(name));
		zip.write(content);
		zip.closeEntry();
	}
}
