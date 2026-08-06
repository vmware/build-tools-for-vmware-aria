/*-
 * #%L
 * ecmascript
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
/**
 * @brief	replaces ending dots with underscores and replaces all / with a dot
 *
 * @details	Example 1:
 * 			com.vmware.pscoe.onboarding.sgenov.actions/ing/excludeTest/testHelper.helper
 * 			->
 * 			com.vmware.pscoe.onboarding.sgenov.actions.ing.excludeTest.testHelper_helper
 * 			Example 2:
 * 			com.vmware.pscoe.sgenov.actions.testHelper.helper
 * 			->
 * 			com.vmware.pscoe.sgenov.actions.testHelper.helper
 * 			Example 3:
 * 			com.vmware.pscoe.library.ts.util/ConfigElementAccessor
 * 			->
 * 			com.vmware.pscoe.library.ts.util.ConfigElementAccessor
 *
 * @param	{String} name
 */
function formatName(name: string) {
	return name.replace(/([^\\\/]+$)/, function (match) {
		const repeatingDots = /([.]).*(\1)/g
		return repeatingDots.test(match) ? match : match.replace(/[.]([^.]*)$/, '_$1');
	}).replace(/[\\\/]/g, ".");
}

/**
 * @return {Any}
 */
(function () {
	const GLOBAL = System.getContext() || (function () {
		return this;
	}).call(null);
	if (!GLOBAL.__vroes__) {
		const vroes: any = GLOBAL.__vroes__ = {};
		const moduleName = "com.vmware.pscoe.library.ecmascript";
		const actions = ["Shims", "Map", "Set", "Promise", "tslib"];
		const Module = System.getModule(moduleName).Module();
		vroes.import = Module.import;
		vroes.export = Module.export;
		vroes.load = Module.load;
		vroes.class = Module.import("default").from(`${moduleName}.Class`);
		vroes.setModuleErrorHandler = Module.setModuleErrorHandler;

		vroes.require = function (name: string) {
			const loaded = Module.import("*").from(formatName(name));
			
			// Track constructors in loaded module for identity validation
			if (loaded && typeof loaded === "object") {
				for (const exportName in loaded) {
					if (Object.prototype.hasOwnProperty.call(loaded, exportName)) {
						const exported = loaded[exportName];
						if (typeof exported === "function") {
							vroes.registerConstructor(`${name}.${exportName}`, exported);
						}
					}
				}
			}
			
			return loaded;
		};

		vroes.importLazy = function (name: string) {
			const result: any = {};
			Object.defineProperty(result, "_", {
				get: () => {
					return result.__lazyAction__ || (result.__lazyAction__ = Module.import("*").from(formatName(name)))
				},
				enumerable: true,
				configurable: true
			});
			return result;
		};

		vroes.__constructorRegistry = {};
		vroes.__constructorIdSeq = 0;

		vroes.normalizeConstructorKey = function (name: string) {
			// Strip everything except the core module name
			// "com.vmware.pscoe.library.Foo" -> "Foo"
			// "com.vmware.pscoe.library.ts.Foo" -> "Foo"
			return name.split('.').pop() || name;
		};

		vroes.registerConstructor = function (name: string, ctor: any) {
			if (typeof ctor !== "function") {
				return { registered: false };
			}
			
			const key = vroes.normalizeConstructorKey(name);
			const existing = vroes.__constructorRegistry[key];
			
			if (existing && existing.id !== ctor.__id__) {
				System.warn(
					`[VROES][E_CONSTRUCTOR_IDENTITY_MISMATCH] Constructor identity mismatch for '${key}'. ` +
					`Detected multiple constructor objects for the same logical export. ` +
					`Likely cause: module loaded through different aliases/sources. ` +
					`Summary: this indicates a module configuration issue (e.g., loading from multiple sources). ` +
					`Recommended actions: ` +
					`1) ensure all imports resolve to one canonical module path, ` +
					`2) verify logger/module resolver configuration does not redirect to a second namespace, ` +
					`3) run __vroes__.validateModuleConfiguration() and inspect __vroes__.diagnoseModule(instance).`
				);
				return {
					registered: false,
					isIdentityMismatch: true,
					first: existing,
					current: ctor,
					name: name
				};
			}
			
			if (!ctor.__id__) {
				vroes.__constructorIdSeq += 1;
				ctor.__id__ = `ctor:${key}:${vroes.__constructorIdSeq}`;
			}
			
			vroes.__constructorRegistry[key] = { id: ctor.__id__, ctor: ctor, name: name };
			return { registered: true };
		};

		vroes.validateConstructorIdentity = function (instance: any, ctor: any, context?: string) {
			if (typeof ctor !== "function") {
				return { valid: false, reason: "ctor is not a function" };
			}
			
			if (!(instance instanceof ctor)) {
				const instanceCtor = instance?.constructor?.name || typeof instance;
				const expectedCtor = ctor?.name || typeof ctor;
				
				System.warn(
					`[VROES][E_INSTANCEOF_IDENTITY_SPLIT] instanceof validation failed${context ? ` (${context})` : ''}. ` +
					`instance constructor='${instanceCtor}', expected='${expectedCtor}'. ` +
					`This usually means constructor identity split across module load sources. ` +
					`Summary: this suggests the module was loaded from different sources. ` +
					`Recommended actions: normalize import source for this type, avoid duplicate module aliases, and compare constructor ids via __vroes__.diagnoseModule(instance).`
				);
				return { valid: false, reason: "instanceof check failed", instanceCtor, expectedCtor };
			}
			return { valid: true };
		};

		vroes.diagnoseModule = function (instance: any) {
			const ctor = instance?.constructor;
			if (!ctor) return { error: "Instance has no constructor" };
			
			const diagnostics: any = {
				instanceConstructorName: ctor.name,
				instanceConstructorId: ctor.__id__ || "unregistered",
				registeredConstructors: []
			};
			
			// List all registered constructors
			for (const key in vroes.__constructorRegistry) {
				if (Object.prototype.hasOwnProperty.call(vroes.__constructorRegistry, key)) {
					const entry = vroes.__constructorRegistry[key];
					diagnostics.registeredConstructors.push({
						key,
						name: entry.ctor?.name,
						id: entry.id,
						fullName: entry.name
					});
				}
			}
			
			// Check if this constructor was registered
			let registered = null;
			for (const registryKey in vroes.__constructorRegistry) {
				if (Object.prototype.hasOwnProperty.call(vroes.__constructorRegistry, registryKey)) {
					const entry = vroes.__constructorRegistry[registryKey];
					if (entry?.ctor === ctor) {
						registered = entry;
						break;
					}
				}
			}
			
			if (!registered) {
				diagnostics.warning = 
					"[VROES][W_CONSTRUCTOR_NOT_REGISTERED] Instance constructor not found in registry. " +
					"It may have been created from a non-canonical or unmanaged module load path. " +
					"Summary: diagnostics may be incomplete for this instance. " +
					"Register constructors via __vroes__.require(...) or inspect active entries in diagnostics.registeredConstructors.";
			}
			
			return diagnostics;
		};

		vroes.validateModuleConfiguration = function () {
			const report: any = {
				timestamp: new Date().toISOString(),
				modules: [],
				issues: []
			};
			
			// Test: load the same module via same path and check consistency
			const testModules = [
				"com.vmware.pscoe.library.Shims",
				"com.vmware.pscoe.library.Map"
			];
			
			for (const moduleName of testModules) {
				try {
					const loaded1 = Module.import("*").from(moduleName);
					const loaded2 = Module.import("*").from(moduleName);
					
					// Check if loading the same path twice gives same identity
					const match = loaded1 === loaded2;
					report.modules.push({
						name: moduleName,
						identitiesConsistent: match
					});
					
					if (!match) {
						report.issues.push(
							`[VROES][E_MODULE_IDENTITY_UNSTABLE] Module '${moduleName}' returned different object identities on repeated imports. ` +
							`This indicates unstable module caching/resolution. ` +
							`Summary: this indicates a module loading/caching issue. ` +
							`Recommended actions: verify resolver/cache configuration and ensure all call sites use a single canonical import name.`
						);
					}
				} catch (e) {
					report.modules.push({
						name: moduleName,
						loadable: false,
						error: e?.toString()
					});
				}
			}
			
			return report;
		};

		vroes.validateExportShape = function (moduleExport: any, modulePath?: string, exportName?: string) {
			const report: any = {
				timestamp: new Date().toISOString(),
				modulePath: modulePath || "unknown",
				exportName: exportName || "unknown",
				exportType: typeof moduleExport,
				isFunction: typeof moduleExport === "function",
				isConstructor: false,
				exportKeys: [],
				issues: []
			};
			
			// Check if it's a function (constructor)
			if (typeof moduleExport === "function") {
				report.isConstructor = true;
			} 
			// If it's an object, list all keys
			else if (typeof moduleExport === "object" && moduleExport !== null) {
				const keys = [];
				for (const key in moduleExport) {
					if (moduleExport.hasOwnProperty(key)) {
						keys.push(key);
					}
				}
				report.exportKeys = keys;
				report.issues.push(
					`[VROES][E_EXPORT_SHAPE_MISMATCH] Expected a constructor function, but got an object with keys: [${keys.join(", ")}]. ` +
					`This typically occurs when module loading returns a wrapper object instead of the target constructor. ` +
					`Recommended actions: verify the module export is a direct constructor (not wrapped), ` +
					`check for path misconfiguration in dynamic Class.load() calls, and ensure no parallel aliases are used.`
				);
			} else {
				report.issues.push(
					`[VROES][E_EXPORT_SHAPE_INVALID] Expected a constructor function, but got type '${typeof moduleExport}'. ` +
					`Module path: ${modulePath}, export name: ${exportName}.`
				);
			}
			
			return report;
		};

		for (let actionName of actions) {
			Object.defineProperty(vroes, actionName, {
				get: () => Module.import("default").from(`${moduleName}.${actionName}`),
				enumerable: true,
				configurable: true
			});
		}
	}
	return GLOBAL.__vroes__;
});
