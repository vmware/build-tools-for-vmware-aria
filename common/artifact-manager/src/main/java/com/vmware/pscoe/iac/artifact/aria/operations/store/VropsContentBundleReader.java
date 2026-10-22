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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads a VCF Operations Content Management bundle, i.e. the zip file produced
 * by the {@code /suite-api/api/content/operations/export} REST API, and writes
 * its content in the project format, i.e. a file per item.
 * <p>
 * The fields that change on each export without changing the item (ids,
 * owners, timestamps) are not written, so pulling unchanged items does not
 * change the files. They are not needed for the import either.
 * <p>
 * The bundle holds all items of a content type together (all views in a single
 * {@code views.zip}, all dashboards of a user in a single
 * {@code dashboards/<userId>} zip, etc.), so the items are extracted one by
 * one.
 *
 * @see VropsContentBundle
 */
public final class VropsContentBundleReader implements AutoCloseable {
	/**
	 * Dashboard JSON file inside the dashboard zip.
	 */
	private static final String DASHBOARD_JSON = "dashboard/dashboard.json";
	/**
	 * Dashboard resources directory inside the dashboard zip.
	 */
	private static final String DASHBOARD_RESOURCES_DIR = "dashboard/resources/";
	/**
	 * View / report content inside the views / reports zip.
	 */
	private static final String CONTENT_XML = "content.xml";
	/**
	 * View / report resources directory inside the views / reports zip.
	 */
	private static final String CONTENT_RESOURCES_DIR = "resources/";
	/**
	 * Resources directory in the project.
	 */
	private static final String RESOURCES_DIR = "resources";
	/**
	 * Properties file extension.
	 */
	private static final String PROPERTIES_EXTENSION = ".properties";
	/**
	 * Number of hex digits of a unicode escape in properties files.
	 */
	private static final int UNICODE_ESCAPE_DIGITS = 4;
	/**
	 * Radix of the unicode escapes.
	 */
	private static final int HEX_RADIX = 16;
	/**
	 * Dashboard fields set by the server. The id is kept, as the import skips the
	 * dashboards without id.
	 */
	private static final List<String> DASHBOARD_SERVER_FIELDS = Arrays.asList("userId", "lastUpdateUserId",
			"creationTime", "lastUpdateTime", "importAttempts", "importComplete");
	/**
	 * Super metric fields set by the server.
	 */
	private static final List<String> SUPER_METRIC_SERVER_FIELDS = Arrays.asList("modificationTime", "modifiedBy");
	/**
	 * Dashboard references to the entries of the dashboard file, e.g.
	 * resourceKind:id:3_::_.
	 */
	private static final Pattern DASHBOARD_ENTRY_ID = Pattern.compile("([A-Za-z]+):id:\\d+_::_");

	/**
	 * JSON mapper.
	 */
	private final ObjectMapper mapper = new ObjectMapper();
	/**
	 * The bundle.
	 */
	private final ZipFile bundle;
	/**
	 * Parsed content.xml of the views / reports zip, by zip entry.
	 */
	private final Map<String, String> contentXmls = new HashMap<>();
	/**
	 * Parsed dashboard.json, by user name.
	 */
	private final Map<String, JsonNode> dashboardJsons = new HashMap<>();
	/**
	 * Content of the zips nested in the bundle, by bundle entry.
	 */
	private final Map<String, Map<String, byte[]>> nestedZips = new HashMap<>();

	/**
	 * Opens a content bundle.
	 *
	 * @param bundleFile the content bundle zip.
	 * @throws IOException if the bundle cannot be opened.
	 */
	public VropsContentBundleReader(final File bundleFile) throws IOException {
		this.bundle = new ZipFile(bundleFile);
		// keep the numbers as they are
		this.mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
		this.mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
	}

	@Override
	public void close() throws IOException {
		bundle.close();
	}

	/**
	 * @return names of the super metrics in the bundle.
	 * @throws IOException if the bundle cannot be read.
	 */
	public List<String> getSuperMetricNames() throws IOException {
		List<String> names = new ArrayList<>();
		getSuperMetrics().forEach(superMetric -> names.add(superMetric.path("name").asText()));
		return names;
	}

	/**
	 * Writes a super metric as {@code <name>.json}, holding the super metric
	 * definition keyed by its id.
	 *
	 * @param name super metric name.
	 * @param dir  target directory.
	 * @throws IOException if the super metric is missing or cannot be written.
	 */
	public void writeSuperMetric(final String name, final File dir) throws IOException {
		Iterator<Map.Entry<String, JsonNode>> superMetrics = getSuperMetricsById().fields();
		while (superMetrics.hasNext()) {
			Map.Entry<String, JsonNode> superMetric = superMetrics.next();
			if (name.equals(superMetric.getValue().path("name").asText())) {
				ObjectNode content = mapper.createObjectNode();
				content.set(superMetric.getKey(), ((ObjectNode) superMetric.getValue()).without(SUPER_METRIC_SERVER_FIELDS));
				dir.mkdirs();
				writeJson(content, new File(dir, VropsPackageStore.toFileName(name) + ".json"));
				return;
			}
		}

		throw new IOException(String.format("Super metric '%s' is not present in the exported content", name));
	}

	/**
	 * @return names of the metric configurations in the bundle.
	 */
	public List<String> getMetricConfigNames() {
		List<String> names = new ArrayList<>();
		Collections.list(bundle.entries()).forEach(entry -> {
			if (!entry.isDirectory() && entry.getName().startsWith(VropsContentBundle.METRIC_CONFIGS_DIR)) {
				names.add(entry.getName().substring(VropsContentBundle.METRIC_CONFIGS_DIR.length()));
			}
		});
		return names;
	}

	/**
	 * Writes a metric configuration file.
	 *
	 * @param name metric configuration name.
	 * @param dir  target directory.
	 * @throws IOException if the metric configuration is missing or cannot be
	 *                     written.
	 */
	public void writeMetricConfig(final String name, final File dir) throws IOException {
		dir.mkdirs();
		Files.write(new File(dir, name).toPath(), readEntry(VropsContentBundle.METRIC_CONFIGS_DIR + name));
	}

	/**
	 * Writes a view as {@code <name>.xml} (see {@link VropsPackageStore#toFileName(String)}) and appends its localization to the
	 * resources of the directory.
	 *
	 * @param id   view id.
	 * @param name view name.
	 * @param dir  target directory.
	 * @throws IOException if the view is missing or cannot be written.
	 */
	public void writeView(final String id, final String name, final File dir) throws IOException {
		Map<String, byte[]> views = readNestedZip(VropsContentBundle.VIEWS_ENTRY);
		dir.mkdirs();
		writeContentElement(VropsContentBundle.VIEWS_ENTRY, views, "ViewDef", id,
				new File(dir, VropsPackageStore.toFileName(name) + ".xml"));
		appendResources(views, CONTENT_RESOURCES_DIR, new File(dir, RESOURCES_DIR),
				key -> key.startsWith("view." + id + "."));
	}

	/**
	 * Writes a report as {@code <name>/content.xml} along with its localization.
	 *
	 * @param id   report id.
	 * @param name report name.
	 * @param dir  target directory.
	 * @throws IOException if the report is missing or cannot be written.
	 */
	public void writeReport(final String id, final String name, final File dir) throws IOException {
		Map<String, byte[]> reports = readNestedZip(VropsContentBundle.REPORTS_ENTRY);
		File reportDir = new File(dir, VropsPackageStore.toFileName(name));
		reportDir.mkdirs();
		writeContentElement(VropsContentBundle.REPORTS_ENTRY, reports, "ReportDef", id,
				new File(reportDir, CONTENT_XML));
		appendResources(reports, CONTENT_RESOURCES_DIR, new File(reportDir, RESOURCES_DIR),
				key -> key.startsWith("report." + id + "."));
	}

	/**
	 * @param userName dashboard owner.
	 * @return names of the dashboards of the user in the bundle.
	 * @throws IOException if the bundle cannot be read.
	 */
	public List<String> getDashboardNames(final String userName) throws IOException {
		List<String> names = new ArrayList<>();
		getDashboardJson(userName).path("dashboards").forEach(dashboard -> names.add(dashboard.path("name").asText()));
		return names;
	}

	/**
	 * The import of a dashboard is completed by the server when the dashboard is
	 * opened in the UI. Until then the dashboard is exported without its widget
	 * configurations.
	 *
	 * @param userName dashboard owner.
	 * @return names of the dashboards of the user whose import is not completed.
	 * @throws IOException if the bundle cannot be read.
	 */
	public List<String> getIncompleteDashboardNames(final String userName) throws IOException {
		List<String> names = new ArrayList<>();
		getDashboardJson(userName).path("dashboards").forEach(dashboard -> {
			if (!dashboard.path("importComplete").asBoolean(true)) {
				names.add(dashboard.path("name").asText());
			}
		});
		return names;
	}

	/**
	 * Writes a dashboard as {@code <name>.json} and appends its localization to
	 * the resources of the directory. The dashboards of a user share the entries
	 * (resources, resource kinds, etc.) they refer to, so only the entries used
	 * by the dashboard are kept and renumbered, the same way as in the export of a
	 * single dashboard.
	 *
	 * @param userName dashboard owner.
	 * @param name     dashboard name.
	 * @param dir      target directory.
	 * @throws IOException if the dashboard is missing or cannot be written.
	 */
	public void writeDashboard(final String userName, final String name, final File dir) throws IOException {
		JsonNode dashboardJson = getDashboardJson(userName);
		JsonNode dashboard = null;
		for (JsonNode candidate : dashboardJson.path("dashboards")) {
			if (name.equals(candidate.path("name").asText())) {
				dashboard = candidate;
				break;
			}
		}
		if (dashboard == null) {
			throw new IOException(
					String.format("Dashboard '%s' of user '%s' is not present in the exported content", name, userName));
		}

		String dashboardContent = mapper.writeValueAsString(dashboard);
		List<String> usedEntryIds = new ArrayList<>();
		Matcher matcher = DASHBOARD_ENTRY_ID.matcher(dashboardContent);
		while (matcher.find()) {
			usedEntryIds.add(matcher.group());
		}

		Map<String, String> newEntryIds = new HashMap<>();
		ObjectNode entries = mapper.createObjectNode();
		Iterator<Map.Entry<String, JsonNode>> entryTypes = dashboardJson.path("entries").fields();
		while (entryTypes.hasNext()) {
			Map.Entry<String, JsonNode> entryType = entryTypes.next();
			ArrayNode usedEntries = mapper.createArrayNode();
			for (JsonNode entry : entryType.getValue()) {
				String entryId = entry.path("internalId").asText();
				if (usedEntryIds.contains(entryId)) {
					String newEntryId = String.format("%s:id:%d_::_", entryType.getKey(), usedEntries.size());
					newEntryIds.put(entryId, newEntryId);
					usedEntries.add(((ObjectNode) entry.deepCopy()).put("internalId", newEntryId));
				}
			}
			if (!usedEntries.isEmpty()) {
				entries.set(entryType.getKey(), usedEntries);
			}
		}
		matcher = DASHBOARD_ENTRY_ID.matcher(dashboardContent);
		StringBuffer renumbered = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(renumbered, Matcher.quoteReplacement(newEntryIds.get(matcher.group())));
		}
		matcher.appendTail(renumbered);

		ObjectNode content = mapper.createObjectNode();
		content.set("entries", entries);
		content.set("dashboards", mapper.createArrayNode()
				.add(((ObjectNode) mapper.readTree(renumbered.toString())).without(DASHBOARD_SERVER_FIELDS)));
		dir.mkdirs();
		writeJson(content, new File(dir, VropsPackageStore.toFileName(name) + ".json"));

		appendResources(getDashboardZip(userName), DASHBOARD_RESOURCES_DIR, new File(dir, RESOURCES_DIR),
				key -> key.equals(name) || key.startsWith(name + "."));
	}

	private void writeJson(final JsonNode content, final File target) throws IOException {
		mapper.writerWithDefaultPrettyPrinter().writeValue(target, content);
	}

	private JsonNode getSuperMetricsById() throws IOException {
		if (bundle.getEntry(VropsContentBundle.SUPER_METRICS_ENTRY) == null) {
			return mapper.createObjectNode();
		}
		return mapper.readTree(readEntry(VropsContentBundle.SUPER_METRICS_ENTRY));
	}

	private List<JsonNode> getSuperMetrics() throws IOException {
		List<JsonNode> superMetrics = new ArrayList<>();
		getSuperMetricsById().forEach(superMetrics::add);
		return superMetrics;
	}

	private JsonNode getDashboardJson(final String userName) throws IOException {
		if (!dashboardJsons.containsKey(userName)) {
			Map<String, byte[]> dashboardZip = getDashboardZip(userName);
			dashboardJsons.put(userName, dashboardZip.containsKey(DASHBOARD_JSON)
					? mapper.readTree(dashboardZip.get(DASHBOARD_JSON))
					: mapper.createObjectNode());
		}
		return dashboardJsons.get(userName);
	}

	private Map<String, byte[]> getDashboardZip(final String userName) throws IOException {
		if (bundle.getEntry(VropsContentBundle.USER_MAPPINGS_ENTRY) == null) {
			return Collections.emptyMap();
		}
		JsonNode userMappings = mapper.readTree(readEntry(VropsContentBundle.USER_MAPPINGS_ENTRY));
		for (JsonNode user : userMappings.path("users")) {
			if (userName.equals(user.path("userName").asText())) {
				String entryName = VropsContentBundle.DASHBOARDS_DIR + user.path("userId").asText();
				return bundle.getEntry(entryName) == null ? Collections.emptyMap() : readNestedZip(entryName);
			}
		}

		return Collections.emptyMap();
	}

	/**
	 * Writes a single item (view / report definition) of a content.xml, keeping
	 * the item and the content around the items as they are in the export.
	 */
	private void writeContentElement(final String zipName, final Map<String, byte[]> zip, final String itemTag,
			final String id, final File target) throws IOException {
		String content = getContentXml(zipName, zip);
		String endTag = "</" + itemTag + ">";
		Matcher item = Pattern.compile("<" + itemTag + "\\s[^>]*\\bid=\"" + Pattern.quote(id) + "\"").matcher(content);
		Matcher firstItem = Pattern.compile("<" + itemTag + "[\\s>]").matcher(content);
		int itemEnd = item.find() ? content.indexOf(endTag, item.start()) : -1;
		if (itemEnd < 0 || !firstItem.find()) {
			throw new IOException(String.format("%s with id '%s' is not present in the exported content", itemTag, id));
		}

		String header = content.substring(0, firstItem.start());
		String footer = content.substring(content.lastIndexOf(endTag) + endTag.length());
		String single = header + content.substring(item.start(), itemEnd + endTag.length()) + footer;
		Files.write(target.toPath(), single.getBytes(StandardCharsets.UTF_8));
	}

	private String getContentXml(final String zipName, final Map<String, byte[]> zip) throws IOException {
		if (!contentXmls.containsKey(zipName)) {
			if (!zip.containsKey(CONTENT_XML)) {
				throw new IOException(String.format("%s is missing in %s", CONTENT_XML, zipName));
			}
			contentXmls.put(zipName, new String(zip.get(CONTENT_XML), StandardCharsets.UTF_8));
		}
		return contentXmls.get(zipName);
	}

	/**
	 * Appends the properties of the matching keys from all localization files to
	 * the localization files of the same name in the target directory. The lines
	 * are copied as they are, keeping their escaping and encoding.
	 */
	private static void appendResources(final Map<String, byte[]> zip, final String resourcesDir, final File targetDir,
			final Predicate<String> keyMatches) throws IOException {
		for (Map.Entry<String, byte[]> entry : zip.entrySet()) {
			String fileName = entry.getKey().substring(entry.getKey().lastIndexOf('/') + 1);
			if (!entry.getKey().startsWith(resourcesDir) || !fileName.endsWith(PROPERTIES_EXTENSION)) {
				continue;
			}
			StringBuilder matching = new StringBuilder();
			for (String line : new String(entry.getValue(), StandardCharsets.UTF_8).split("\\r?\\n")) {
				String key = getPropertyKey(line);
				if (key != null && keyMatches.test(key)) {
					matching.append(line).append('\n');
				}
			}
			if (matching.length() > 0) {
				targetDir.mkdirs();
				Files.write(new File(targetDir, fileName).toPath(), matching.toString().getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			}
		}
	}

	/**
	 * Returns the unescaped key of a properties line.
	 *
	 * @param line properties line.
	 * @return the key or null for comments and blank lines.
	 */
	static String getPropertyKey(final String line) {
		String trimmed = line.replaceAll("^\\s+", "");
		if (trimmed.isEmpty() || trimmed.charAt(0) == '#' || trimmed.charAt(0) == '!') {
			return null;
		}
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '\\' && i + 1 < trimmed.length()) {
				char next = trimmed.charAt(++i);
				if (next == 'u' && i + UNICODE_ESCAPE_DIGITS < trimmed.length()) {
					key.append((char) Integer.parseInt(trimmed.substring(i + 1, i + 1 + UNICODE_ESCAPE_DIGITS),
							HEX_RADIX));
					i += UNICODE_ESCAPE_DIGITS;
				} else {
					key.append(next);
				}
			} else if (c == '=' || c == ':' || Character.isWhitespace(c)) {
				break;
			} else {
				key.append(c);
			}
		}
		return key.toString();
	}

	private byte[] readEntry(final String name) throws IOException {
		ZipEntry entry = bundle.getEntry(name);
		if (entry == null) {
			throw new IOException(String.format("'%s' is not present in the exported content", name));
		}
		try (InputStream in = bundle.getInputStream(entry)) {
			return in.readAllBytes();
		}
	}

	private Map<String, byte[]> readNestedZip(final String name) throws IOException {
		if (nestedZips.containsKey(name)) {
			return nestedZips.get(name);
		}
		Map<String, byte[]> entries = new LinkedHashMap<>();
		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(readEntry(name)))) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					zip.transferTo(out);
					entries.put(entry.getName(), out.toByteArray());
				}
			}
		}
		nestedZips.put(name, entries);
		return entries;
	}
}
