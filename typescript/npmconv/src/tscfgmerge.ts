/*
 * #%L
 * npmconv
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

const TSCONF_PREFIX_CHARS = ["@", "-"];

function isJsObject(obj) {
	return typeof obj === "object" && !Array.isArray(obj);
}

export default function tscfgmerge(archetypeTsconfig?: any, packageTsconfig?: any, mergeTsconfig?: any): any {
	let mergedValue = undefined;

	let keysHandled = undefined;

	if (isJsObject(packageTsconfig)) {
		mergedValue = {};

		keysHandled = Object.keys(packageTsconfig).filter(key => {
			if (isJsObject(mergeTsconfig)) {
				let mergeTmplObj = <Object>mergeTsconfig;
				if (mergeTmplObj.hasOwnProperty(`@${key}`)) {
					// use the value from archetype tsconfig stays
					mergedValue[key] = archetypeTsconfig[key];
					return true;
				} else if (mergeTmplObj.hasOwnProperty(`-${key}`)) {
					// ensure the value from package is not assigned from both places
					return true;
				} else if (mergeTmplObj.hasOwnProperty(key) && !isJsObject(packageTsconfig[key])) {
					// take the value from merge template as is
					mergedValue[key] = mergeTmplObj[key];
					return true;
				}
			}

			// overwrite from package with nesting
			mergedValue[key] = tscfgmerge(
				archetypeTsconfig && archetypeTsconfig[key],
				packageTsconfig && packageTsconfig[key],
				mergeTsconfig && mergeTsconfig[key]
			);
			return true;
		});
	}

	if (isJsObject(archetypeTsconfig)) {
		mergedValue = mergedValue || {};
		// pick up any keys that were not in package config
		keysHandled = (keysHandled || []).concat(
			Object.keys(archetypeTsconfig).filter(key => {
				if (!keysHandled || keysHandled.indexOf(key) === -1) {
					if (isJsObject(mergeTsconfig)) {
						let mergeTmplObj = <Object>mergeTsconfig;
						if (mergeTmplObj.hasOwnProperty(key) && !isJsObject(archetypeTsconfig[key])) {
							// overwrite with template explicit value
							mergedValue[key] = mergeTmplObj[key];
							return true;
						} else if (mergeTmplObj.hasOwnProperty(`-${key}`)) {
							// skip merging it
							return true;
						}
					}

					mergedValue[key] = tscfgmerge(
						archetypeTsconfig && archetypeTsconfig[key],
						packageTsconfig && packageTsconfig[key],
						mergeTsconfig && mergeTsconfig[key]
					);
					return true;
				}
				return false;
			})
		);
	}

	if (isJsObject(mergeTsconfig)) {
		mergedValue = mergedValue || {};
		Object.keys(mergeTsconfig).forEach(key => {
			if (TSCONF_PREFIX_CHARS.indexOf(key.charAt(0)) === -1 && (!keysHandled || keysHandled.indexOf(key) === -1)) {
				// explicit set of merge template values
				mergedValue[key] = mergeTsconfig[key];
			}
		});
	}

	if (!mergedValue) {
		// wasn't picked up elsewhere, so copy over if present in package config or default to archetype config
		mergedValue = packageTsconfig || archetypeTsconfig;
	}

	return mergedValue;
}
