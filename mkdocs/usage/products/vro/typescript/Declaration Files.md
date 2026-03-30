---
title: Declaration Files
---

# Declaration Files

## Overview

Declaration files are with `.d.ts` extension. They are used to store **ONLY** type information (types, interfaces, etc.) and contain no actual implementations or property values. They keep your implementation files and {{ products.vro_short_name }} code clean and are excluded from {{ products.vro_short_name }} package contents.

!!! warning
    Information stored in declaration files is **NOT** imported to {{ products.vro_short_name }}. It is only used before and during transpilation for type checking, hinting and autocompletion, transpile time errors, etc. Using declaration files to store actual implementation (e.g. enumerations - they contain actual string values) does not fail import operation but results in `Nullpointer` exception when the code reaches a place that uses this implementation or values.

## Examples

Example type definitions that can be stored in a declaration file:

```ts
export type TransformationResult = Object | string;

export type YesNo = "Yes" | "No";

export interface Attribute {
	name: string;
	description: string;
	type: string;
	value: any;
}

export interface VersionHistoryItem {
	comment: string;
	date: any;
	version: string;
	user: string;
}

export interface ConfigurationElement {
	name: string;
	description: string;
	version: string;
	versionHistoryItems: VersionHistoryItem[];
	attributes: Attribute[];
	configurationElementCategory: any;
	/**
	 * Returns the attribute of the configuration element for the specified key or null if not found.
	 * @param key
	 */
	getAttributeWithKey(key: string): Attribute | null;
	/**
	 * Sets the attribute value of the configuration element for the specified key.
	 * @param key
	 * @param value
	 */
	setAttributeWithKey(key: string, value: any, typeHint?: any): void;
	/**
	 * Reloads the values of the attributes of this configuration element.
	 */
	reload(): void;
	/**
	 * Remove the attribute of the configuration element for the specified key.
	 * @param key
	 */
	removeAttributeWithKey(key: string): void;
	/**
	 * Saves a change set in the local version repository.
	 * This function is available with vRA 8
	 */
	saveToVersionRepository(): void;
}
```
