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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Builds a VCF Operations Content Management bundle, i.e. the zip file accepted
 * by the {@code /suite-api/api/content/operations/import} REST API.
 * <p>
 * The bundle layout mirrors the one produced by the content export:
 * <ul>
 * <li>{@code 6844548499441080431L.v1} - mandatory format marker</li>
 * <li>{@code configuration.json} - summary of the bundled content</li>
 * <li>{@code usermappings.json} - maps the dashboard owner ids to user
 * names</li>
 * <li>{@code dashboards/<ownerId>} - dashboard zip (same format as the
 * ops-cli one) per owner</li>
 * <li>{@code views.zip} / {@code reports.zip} - view / report zip (same format
 * as the ops-cli one)</li>
 * <li>{@code supermetrics.json} - super metrics keyed by id</li>
 * <li>{@code reskndmetric/<name>} - metric configuration files</li>
 * </ul>
 * A bundle holds a single dashboard, view and report because their definitions
 * cannot be merged safely, so each of them is imported with a separate bundle.
 */
public final class VropsContentBundle {
	/**
	 * Format marker entry required by the content import, otherwise it fails
	 * with INVALID_FILE_FORMAT.
	 */
	static final String FORMAT_MARKER_ENTRY = "6844548499441080431L.v1";
	/**
	 * Configuration entry.
	 */
	static final String CONFIGURATION_ENTRY = "configuration.json";
	/**
	 * User mappings entry.
	 */
	static final String USER_MAPPINGS_ENTRY = "usermappings.json";
	/**
	 * Dashboards directory.
	 */
	static final String DASHBOARDS_DIR = "dashboards/";
	/**
	 * Views entry.
	 */
	static final String VIEWS_ENTRY = "views.zip";
	/**
	 * Reports entry.
	 */
	static final String REPORTS_ENTRY = "reports.zip";
	/**
	 * Super metrics entry.
	 */
	static final String SUPER_METRICS_ENTRY = "supermetrics.json";
	/**
	 * Metric configurations directory.
	 */
	static final String METRIC_CONFIGS_DIR = "reskndmetric/";

	/**
	 * JSON mapper.
	 */
	private final ObjectMapper mapper = new ObjectMapper();
	/**
	 * Super metrics keyed by id.
	 */
	private final Map<String, Object> superMetrics = new LinkedHashMap<>();
	/**
	 * Metric configuration files.
	 */
	private final List<File> metricConfigs = new ArrayList<>();
	/**
	 * View zip.
	 */
	private File view;
	/**
	 * Report zip.
	 */
	private File report;
	/**
	 * Dashboard zip.
	 */
	private File dashboard;
	/**
	 * Names of the users the dashboard is imported for.
	 */
	private List<String> dashboardOwners = Collections.emptyList();

	/**
	 * Adds super metrics from an ops-cli formatted super metric file, i.e. a JSON
	 * object with the super metric definitions keyed by id.
	 *
	 * @param superMetricFile super metric file
	 * @return this bundle
	 * @throws IOException if the file cannot be read or parsed
	 */
	public VropsContentBundle addSuperMetrics(final File superMetricFile) throws IOException {
		Map<String, Object> definitions = mapper.readValue(superMetricFile, new TypeReference<Map<String, Object>>() {
		});
		superMetrics.putAll(definitions);
		return this;
	}

	/**
	 * Adds a metric configuration file. The file name is used as the metric
	 * configuration name.
	 *
	 * @param metricConfigFile metric configuration file
	 * @return this bundle
	 */
	public VropsContentBundle addMetricConfig(final File metricConfigFile) {
		metricConfigs.add(metricConfigFile);
		return this;
	}

	/**
	 * Sets the view to import.
	 *
	 * @param viewZip view zip (content.xml and resources)
	 * @return this bundle
	 */
	public VropsContentBundle setView(final File viewZip) {
		this.view = viewZip;
		return this;
	}

	/**
	 * Sets the report to import.
	 *
	 * @param reportZip report zip (content.xml and resources)
	 * @return this bundle
	 */
	public VropsContentBundle setReport(final File reportZip) {
		this.report = reportZip;
		return this;
	}

	/**
	 * Sets the dashboard to import.
	 *
	 * @param dashboardZip dashboard zip (dashboard/dashboard.json and resources)
	 * @param owners       names of the users the dashboard is imported for
	 * @return this bundle
	 */
	public VropsContentBundle setDashboard(final File dashboardZip, final List<String> owners) {
		if (owners == null || owners.isEmpty()) {
			throw new IllegalArgumentException("At least one dashboard owner is required");
		}
		this.dashboard = dashboardZip;
		this.dashboardOwners = new ArrayList<>(owners);
		return this;
	}

	/**
	 * @return true if nothing has been added to the bundle
	 */
	public boolean isEmpty() {
		return superMetrics.isEmpty() && metricConfigs.isEmpty() && view == null && report == null
				&& dashboard == null;
	}

	/**
	 * Writes the bundle.
	 *
	 * @param target the zip file to write
	 * @return the written zip file
	 * @throws IOException if writing fails
	 */
	public File write(final File target) throws IOException {
		Map<String, Object> configuration = new LinkedHashMap<>();
		configuration.put("type", "CUSTOM");

		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target))) {
			// the content of the marker does not matter, but the export puts an user id
			// there
			putEntry(zip, FORMAT_MARKER_ENTRY, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

			if (dashboard != null) {
				byte[] dashboardContent = Files.readAllBytes(dashboard.toPath());
				List<Map<String, Object>> users = new ArrayList<>();
				List<Map<String, Object>> dashboardsByOwner = new ArrayList<>();
				for (String owner : dashboardOwners) {
					// the target vROPs resolves the owner by user name, so any stable id works
					String ownerId = getOwnerId(owner);
					putEntry(zip, DASHBOARDS_DIR + ownerId, dashboardContent);
					users.add(mapOf("userName", owner, "userId", ownerId));
					dashboardsByOwner.add(mapOf("owner", ownerId, "count", 1));
				}
				// without user mappings the dashboards are skipped by the import
				putEntry(zip, USER_MAPPINGS_ENTRY, mapper.writeValueAsBytes(
						mapOf("sources", Collections.emptyList(), "users", users)));
				configuration.put("dashboardsByOwner", dashboardsByOwner);
				configuration.put("dashboards", dashboardOwners.size());
			}
			if (view != null) {
				putEntry(zip, VIEWS_ENTRY, Files.readAllBytes(view.toPath()));
				configuration.put("views", 1);
			}
			if (report != null) {
				putEntry(zip, REPORTS_ENTRY, Files.readAllBytes(report.toPath()));
				configuration.put("reports", 1);
			}
			if (!superMetrics.isEmpty()) {
				putEntry(zip, SUPER_METRICS_ENTRY, mapper.writeValueAsBytes(superMetrics));
				configuration.put("superMetrics", superMetrics.size());
			}
			for (File metricConfig : metricConfigs) {
				putEntry(zip, METRIC_CONFIGS_DIR + metricConfig.getName(), Files.readAllBytes(metricConfig.toPath()));
			}
			if (!metricConfigs.isEmpty()) {
				configuration.put("configFiles", metricConfigs.size());
			}

			putEntry(zip, CONFIGURATION_ENTRY, mapper.writeValueAsBytes(configuration));
		}

		return target;
	}

	/**
	 * Returns a stable id for the dashboard owner.
	 *
	 * @param owner user name
	 * @return the owner id
	 */
	static String getOwnerId(final String owner) {
		return UUID.nameUUIDFromBytes(owner.getBytes(StandardCharsets.UTF_8)).toString();
	}

	private static void putEntry(final ZipOutputStream zip, final String name, final byte[] content)
			throws IOException {
		zip.putNextEntry(new ZipEntry(name));
		zip.write(content);
		zip.closeEntry();
	}

	private static Map<String, Object> mapOf(final String key1, final Object value1, final String key2,
			final Object value2) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(key1, value1);
		map.put(key2, value2);
		return map;
	}
}
