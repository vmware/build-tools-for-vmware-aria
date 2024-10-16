/*-
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
/**
 * Validates attributes are supported for a given version. Assumes attributes won't be reintroduced in a later varsion.
 */
export interface AttributeVersionValidator {
	/**
	 * Registers an attribute with its supported versions for validation
	 * @param {string} propName - attribute name
	 * @param {string} supportedFromVersion - version it's supported from; if omitted, will be the first supported version
	 * @param {string} supportedToVersion  - version it's supported until; if omitted, will be the last supported version
	 * @param {string} deprecationReason - reason for deprecation; will be listed if an attribute is no longer supported
	 * @throws Error if supportedFromVersion/supportedToVersion is not actually supported.
	 */
	push(propName: string, supportedFromVersion: string, supportedToVersion?: string, deprecationReason?: string);
	/**
	 * Validates the registered attribute versions against the current one
	 * @param {string} currentVersion - version to validate against
	 * @throws Error if currentVersion is not actually supported.
	 * @throws Error with all unssupported (no longer supported, not yet supported) attributes+ details:
	 * version they are supported from/to, deprecation reason (for attributes no longer supported)
	 */
	validate(currentVersion: string);
}
/**
 * Returns an attribute version validator instance
 * @param {string} validatedObjectName - name of the validated object (for error handling)
 * @param {string[]} supportedVersions - supported versions in ascending order (note)
 * @returns {AttributeVersionValidator}
 * @throws Error if the supported versions are missing/invalid (not unique)
 */
export function getAttributeVersionValidator(validatedObjectName: string, supportedVersions: string[]): AttributeVersionValidator {
	if (!supportedVersions?.length || supportedVersions.length > new Set(supportedVersions).size) {
		throw new Error(`Invalid supported versions. The supported versions must be unique and in ascending order.`);
	}
	const validatedProps: Record<string, [number, number, string]> = {};
	function push(propName: string, supportedFromVersion: string, supportedToVersion?: string, deprecationReason: string = "") {
		validatedProps[propName] = [
			!supportedFromVersion ? 0 : supportedVersions.indexOf(supportedFromVersion),
			!supportedToVersion ? supportedVersions.length - 1 : supportedToVersion.indexOf(supportedToVersion),
			deprecationReason
		];
		const unsupported = [supportedFromVersion, supportedToVersion].filter((v, ind) => <number>validatedProps[propName][ind] < 0).join();
		if (unsupported) {
			throw new Error(`Unsupported versions: [${unsupported}]. Must be one of [${supportedVersions}].`);
		}
	}
	function validate(currentVersion: string) {
		const currentVersionInd = supportedVersions.indexOf(currentVersion);
		if (currentVersionInd < 0) {
			throw new Error(`Unsupported version: [${currentVersion}]. Must be one of [${supportedVersions}].`);
		}
		const unsupported: string[] = [];
		Object.entries(validatedProps).forEach(([propName, [supportedFromVersion, supportedToVersion, deprecationReason]]) => {
			if (currentVersionInd < supportedFromVersion) {
				unsupported.push(`${propName} (supported from version '${supportedVersions[supportedFromVersion]}')`);
			}
			if (currentVersionInd > supportedToVersion) {
				unsupported.push(`${propName} (supported up to version '${supportedVersions[supportedToVersion]}'; ${deprecationReason})`);
			}
		});
		if (unsupported.length) {
			throw new Error(`The following attributes are not supported in ${validatedObjectName || ""}`
				+ ` version '${currentVersion}':\n${unsupported.join("\n")}`);
		}
	}
	return { push, validate };
}
