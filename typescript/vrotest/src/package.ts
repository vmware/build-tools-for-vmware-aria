/*
 * #%L
 * vrotest
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
import * as path from "path";
import * as fs from "fs-extra";
import * as util from "./util";

export type ElementType = "ScriptModule" | "Workflow" | "ConfigurationElement" | "ResourceElement" | "Unknown";

export interface VroElement {
	id: string;
	type: ElementType;
	path: string;
}

export interface VroScriptElement extends VroElement {
	type: "ScriptModule";
	name: string;
	module: string;
}

export interface VroWorkflow extends VroElement {
	type: "Workflow";
}

export interface VroConfigElement extends VroElement {
	type: "ConfigurationElement";
	name: string;
	version: string;
	category: string[];
	configPath: string;
}

export interface VroResourceElement extends VroElement {
	type: "ResourceElement";
	name: string;
	version: string;
	category: string[];
	mimetype: string;
	resourcePath: string;
}

export interface ConfigDescriptor {
	id: string;
	name: string;
	version: string;
	attributes: ConfigAttributeDescriptor[];
}

export interface ConfigAttributeDescriptor {
	name: string;
	description: string;
	type: string;
	value: string;
	readOnly?: boolean;
}

interface ElementCategories {
	categories: {
		category: ElementCategory | ElementCategory[];
	}
}

interface ElementCategory {
	name: string;
	"@_name": string;
}

interface ScriptModule {
	"dunes-script-module": {
		"@_id": string;
		"@_name": string;
		"@_version": string;
	}
}

interface Configuration {
	"config-element": {
		"@_id": string;
		"@_version": string;
		"display-name": string;
	}
}

interface ElementInfo {
	properties: {
		comment: string;
		entry: ElementInfoEntry | ElementInfoEntry[];
	}
}

interface ElementInfoEntry {
	"@_key": string;
	"#text": string;
}

export class VroPackage {
	constructor(
		readonly name: string,
		private readonly packageDir: string) {
	}

	private _elements: Promise<VroElement[]>;

	get elements(): Promise<VroElement[]> {
		return this._elements || (this._elements = loadElements(this.packageDir));
	}

	static async load(packageFilePath: string, packagesDir: string): Promise<VroPackage> {
		const packageName = path.basename(packageFilePath, path.extname(packageFilePath));
		const packageDir = path.join(packagesDir, packageName);
		await util.extractZipToFolder(packageFilePath, packageDir);
		return new VroPackage(packageName, packageDir);
	}
}

export async function parseElementInfo(filePath: string): Promise<Record<string, string>> {
	const elementInfo = await util.parseXmlFile<ElementInfo>(filePath);
	const entries = Array.isArray(elementInfo.properties.entry) ? elementInfo.properties.entry : [elementInfo.properties.entry];
	return entries.reduce((obj, entry) => {
		obj[entry["@_key"]] = entry["#text"];
		return obj;
	}, {} as Record<string, string>);
}

export async function parseConfigElement(filePath: string): Promise<ConfigDescriptor> {
	const config = await util.parseXmlFile<Configuration>(filePath);
	const name = config["config-element"]["display-name"];
	const id = config["config-element"]["@_id"];
	const version = config["config-element"]["@_version"];
	let atts = config["config-element"]["atts"]["att"];
	atts = Array.isArray(atts) ? atts : [atts];
	return {
		id,
		name,
		version,
		attributes: atts.map(att => <Record<string, any>>{
			name: att["@_name"],
			description: att["@_description"],
			type: att["@_type"],
			value: (att["value"] || {})["#text"],
			readOnly: att["@_read-only"],
		}),
	};
}

async function loadElements(packagePath: string): Promise<VroElement[]> {
	const elementsPath = path.join(packagePath, "elements");
	const entries = await fs.readdir(elementsPath);
	const stats = await Promise.all(entries.map(p => path.join(elementsPath, p)).map(p => fs.lstat(p)));
	const elementDirs = entries.filter((_, i) => stats[i].isDirectory()).map(p => path.join(elementsPath, p));
	return await Promise.all(elementDirs.map(loadElement));
}

async function loadElement(elemPath: string): Promise<any> {
	const elementInfo = await parseElementInfo(path.join(elemPath, "info"));
	const type = elementInfo["type"] as ElementType;
	switch (type || "") {
		case "ScriptModule":
			return await loadScriptElement(elemPath);
		case "Workflow":
			return await loadWorkflow(elemPath);
		case "ConfigurationElement":
			return await loadConfigElement(elemPath);
		case "ResourceElement":
			return await loadResourceElement(elemPath);
		default:
			console.warn(`Unknown type: ${type} for ${JSON.stringify(elementInfo)}. There may be further failures due to this.`);
			return await loadUnknownElement(elemPath);
	}
}

async function loadUnknownElement(elemPath: string): Promise<VroElement> {
	const id = path.basename(elemPath);
	return {
		id,
		type: "Unknown",
		path: path.join("elements", id),
	};
}

async function loadScriptElement(elemPath: string): Promise<VroScriptElement> {
	const categoriesRoot = await util.parseXmlFile<ElementCategories>(path.join(elemPath, "categories"));
	const category = (Array.isArray(categoriesRoot.categories.category) ? categoriesRoot.categories.category : [categoriesRoot.categories.category])[0];
	if (!category) {
		throw new Error(`Cannot find category of element at ${elemPath}`);
	}
	const module = category.name || category["@_name"];
	const scriptModule = await util.parseXmlFile<ScriptModule>(path.join(elemPath, "data"));
	const name = scriptModule["dunes-script-module"]["@_name"];
	const id = path.basename(elemPath);

	// Save script
	const scriptInfo = scriptModule["dunes-script-module"]["script"];
	const scriptBody = scriptInfo ? scriptInfo["#text"] : "";
	let params = scriptModule["dunes-script-module"]["param"] || [];
	params = Array.isArray(params) ? params : [params];
	const closureParams = params.map(p => p["@_n"]).join(", ");
	const scriptContent = `module.exports = (function (${closureParams}) {\n${scriptBody}\n});`
	await fs.writeFile(path.join(elemPath, `${name}.js`), scriptContent);

	return {
		id,
		type: "ScriptModule",
		path: path.join("elements", id, `${name}.js`),
		name,
		module,
	};
}

async function loadWorkflow(elemPath: string): Promise<VroWorkflow> {
	const id = path.basename(elemPath);
	return {
		id: path.basename(elemPath),
		type: "Workflow",
		path: path.join("elements", id),
	};
}

async function loadConfigElement(elemPath: string): Promise<VroConfigElement> {
	const categoriesRoot = await util.parseXmlFile<ElementCategories>(path.join(elemPath, "categories"));
	const category = (Array.isArray(categoriesRoot.categories.category) ? categoriesRoot.categories.category : [categoriesRoot.categories.category]).map(c => c.name || c["@_name"]);
	const config = await parseConfigElement(path.join(elemPath, "data"));
	const id = path.basename(elemPath);

	// Save attributes as JSON
	await fs.writeFile(path.join(elemPath, `${config.name}.json`), JSON.stringify(config.attributes, null, 2));

	return {
		id,
		type: "ConfigurationElement",
		path: path.join("elements", id),
		name: config.name,
		version: config.version,
		category,
		configPath: path.join("elements", id, `${config.name}.json`),
	};
}

async function loadResourceElement(elemPath: string): Promise<VroResourceElement> {
	const categoriesRoot = await util.parseXmlFile<ElementCategories>(path.join(elemPath, "categories"));
	const category = (Array.isArray(categoriesRoot.categories.category) ? categoriesRoot.categories.category : [categoriesRoot.categories.category]).map(c => c.name || c["@_name"]);

	// Unzip data
	let dataDir = path.join(elemPath, "_data");
	await util.extractZipToFolder(path.join(elemPath, "data"), dataDir);

	// Load metadata
	const id = path.basename(elemPath);
	if (await fs.pathExists(path.join(dataDir, "VSO-RESOURCE-INF"))) {
		dataDir = path.join(dataDir, "VSO-RESOURCE-INF");
	}

	const name = util.decodeBuffer(await fs.readFile(path.join(dataDir, "attribute_name")));

	let version = "";
	const versionFilePath = path.join(dataDir, "attribute_version");
	if (await fs.pathExists(versionFilePath)) {
		version = util.decodeBuffer(await fs.readFile(versionFilePath));
	}

	const mimetypeFilePath = path.join(dataDir, "attribute_mimetype")
	let mimetype = "";
	if (await fs.pathExists(mimetypeFilePath)) {
		mimetype = util.decodeBuffer(await fs.readFile(mimetypeFilePath));
	}

	return {
		id,
		type: "ResourceElement",
		path: path.join("elements", id),
		name,
		version,
		category,
		mimetype,
		resourcePath: path.join("elements", id, "_data", "VSO-RESOURCE-INF", "data"),
	};
}
