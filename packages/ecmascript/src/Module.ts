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
const GLOBAL = System.getContext() || (function () {
	return this;
}).call(null);

/**
 * Function to handle errors when loading/importing an action or module.
 * @param {string | Error} err - error (message)
 */
export type ModuleErrorHandler = (error: string | Error) => void;

/** Default module error handling function. Logs a System Error. */
export const DEFAULT_MODULE_ERROR_HANDLER: ModuleErrorHandler = (error) => System.error(error?.toString());

export interface ModuleConstructor extends Function {
	/**
	 * @param {string[]} specifiers - listed names of elements (actions/modules) to import from a Module.
	 * At least 1 is required.
	 * Also accepts "default" and "*" as specifiers for the default exports/full contents of the module.
	 * The module can be specified via chained call to from(modulePath)
	 * @returns {ModuleImport}
	 */
	import(...specifiers: string[]): ModuleImport;
	/**
	 * Initiates the export of an element.
	 * @returns {ModuleExport} - builder instance.
	 */
	export(): ModuleExport;
	/**
	 * Loads a module (namespace) by its name. Error handling (as defined in
	 * {@link ModuleConstructor.setModuleErrorHandler}) is invoked if:
	 * - the path is missing or invalid
	 * - the System cannot load a module for the given path.
	 * @param {string} name - module name:
	 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
	 * separated by '.'
	 * - may not start or end with '.'
	 * @returns {any} - action or module. Null if the path is null/undefined
	 * 					or if System cannot find a module with the given path.
	 */
	load(path: string): ModuleDescriptor;

	/**
	 * Changes the active {@link ModuleErrorHandler} used in the import and load operations
	 * @param {ModuleErrorHandler} eh - a custom module {@link ModuleErrorHandler error handler function}
	 * Default is {@link DefaultModuleErrorHandlers.SYS_ERROR}
	 */
	setModuleErrorHandler(eh: ModuleErrorHandler): void;
}

export interface ModuleDescriptor {
	[name: string]: (Function | { [name: string]: any });
}

export interface ModuleImportConstructor {
	new(specifiers: string[]): ModuleImport;
}

export interface ModuleImport {
	/**
	 * Attemps to import the specified  action or module (namespace).
	 * Error handling (as defined in {@link ModuleConstructor.setModuleErrorHandler}) is invoked if:
	 * - no valid specifiers were provided.
	 * - the path is missing or invalid relative to the provided basePath
	 * - the System cannot load an action or module for the given path.
	 * @param {string} path - path of the module to import from:
	 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
	 * separated by '.' or '/'
	 * - may start with './' or any number of '../', but cannot have consecutive separators or end with a separator
	 * - slashes (/) and backslashes(\) can be used interchangeably
	 * @param {string} [base] - base path of the module to import from. Required when the path is relative.
	 * NOTE: base is not officially part of the VROESModuleImport interface.
	 * @returns {any|Array<any>} the imported module/actions(s).
	 */
	from(path: string, base?: string): any | any[];
}

export interface ModuleExportConstructor {
	new(): ModuleExport;
}

export interface ModuleExport {
	/**
	 * Prepares an element to be exported with the given name upon completion of the export builder.
	 * @param {string} name - name of the exported element 
	 * @param {any} element 
	 * @returns {ModuleExport} the builder instance
	 */
	named(name: string, element: any): ModuleExport;
	/**
	 * Prepares an element to be exported as default upon completion of the export builder.
	 * @param {any} element 
	 * @returns {ModuleExport} the builder instance
	 */
	default(element: any): ModuleExport;
	/**
	 * Completes the export builder.
	 * @returns {ModuleElementList} object containing the prepared elements for export.
	 */
	build(): ModuleElementList;
}

export interface ModuleElementList {
	[name: string]: any;
}

/**
 * Key of the Module attribute that holds the last set error handler via {@link Module.setModuleErrorHandler}.
 */
const MODULE_ERROR_HANDLER_KEY = "__onError";

/**
 * Regular expression for validating a path for action/module import:
 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
 * separated by '.' or '/'
 * - may start with './' or any number of '../', but cannot have consecutive separators or end with a separator
 * - slashes (/) and backslashes(\) can be used interchangeably
 * Note - use of '-' and capital letters is not limited, despite not being recommended.
 * Using non-capturing groups (?:) to reduce overhead.
 * Example:
 * - matches:
	package -> can be a single word
	my.package_3 -> digits and underscores, '.' as separator
	./my/package -> relative path at same folder, '/' as separator
	../my.package -> relative path with 1 step back
	..\..\my.package.com -> supports backslashes - need to be escaped in string!
 * - matches, but not by convention (mix of separators, use of '-', capital letters, names consisting entirely of digits/allowed special characters)
	../still\working._.BUT/not-recommended.333
 * - does not match:
	not#working! -> disallowed special characters
	not/working/ -> separator at end
	.not.working -> separator at start
	not//working
	still..not\working -> adjacent separators within
	./../not.working -> combining same folder and parent folder relative path indicators
	not/./working 
	not/../working -> relative path indicators within
 */
const IMPORT_PATH_REGEX = /^(?:(?:\.\.\/|\.\.\\)+|\.\/|\.\\)?(?:[\w-]+[.\/\\])*[\w-]+$/g;

/**
 * Regular expression for validating a base path for action/module import:
 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
 * separated by '.'
 * - may not start or end with '.'
 * Note - use of '-' and capital letters is not limited, despite not being recommended.
 * Using non-capturing group (?:) to reduce overhead.
 * Example:
 * - matches:
	my.package_3.com // only dot separators
 * - matches, but not recommended:
	still_working._.BUT.not-recommended.333 -> capital letters, names consisting entirely of digits/allowed special characters
	package -> single-word package
 * - does not match:
	not#working! -> disallowed special characters
	does/not\match -> separators other than '.'
	not.working. -> separator at end
	.not.working -> separator at start
	not..working -> duplicate separator
	./../not.working
	not/../working
	not/./working -> any relative path indicators
 */
const IMPORT_BASE_REGEX = /^(?:[\w-]+\.)*[\w-]+$/g;

/**
 * @return {Any}
 */
(function () {
	let Import: ModuleImportConstructor = <any>function (specifiers: string[]) {
		this.specifiers = specifiers || [];
	};

	Import.prototype.from = function (path: string, base?: string): any | any[] {
		try {
			const absolutePath = constructAbsolutePath(path, base);
			const actionResult = loadActionOrModule(absolutePath);
			const result = extractImports(this.specifiers, actionResult);

			return result.length > 1 ? result : result[0];
		}
		catch (err) {
			return onError(`Cannot import from module with ${path?.[0] === "." ? "relative " : ""}path '${path}'! ${err?.toString()}`);
		}
	};

	let Export: ModuleExportConstructor = <any>function () {
		this.elements = {};
	};

	Export.prototype.named = function (name: string, element: any) {
		this.elements[name] = element;
		return this;
	};

	Export.prototype.default = function (element: any) {
		this.elements["default"] = element;
		return this;
	};

	Export.prototype.build = function () {
		return this.elements;
	};

	let Module: ModuleConstructor = <any>function () {
		throw new Error("Private constructor");
	};

	Module.import = function (...specifiers: string[]) {
		return new Import(specifiers);
	};

	Module.export = function () {
		return new Export();
	};

	Module.load = function (name: string): any {
		const moduleInfo = !name ? null : System.getModule(name);
		return moduleInfo ? createModule(moduleInfo) : onError(`Failed to load module '${name}'!`);
	};

	Module.setModuleErrorHandler = function (eh: ModuleErrorHandler) {
		Module[MODULE_ERROR_HANDLER_KEY] = eh;
	};

	/**
	 * Loads and caches an action or module (namespace) by its path.
	 * @param {string} name - module name (path). Used as key for caching.
	 * @returns {any} - (cached) action or module or NULL, if not found or the path is blank or null/undefined.
	 * @throws Error when a circular dependency is detected.
	 * @throws Error when the System cannot find a module with the given path.
	 */
	function loadActionOrModule(path: string): any {
		const actionResults = GLOBAL.__actions__ = (GLOBAL.__actions__ || {});
		let actionResult = actionResults[path];

		if (actionResult === undefined) {
			let loadStack = GLOBAL.__importStack__ = (GLOBAL.__importStack__ || []);
			const indexOfPathInStack = loadStack.indexOf(path);
			if (indexOfPathInStack >= 0) {
				const circPath = loadStack.slice(indexOfPathInStack);
				circPath.push(path);
				loadStack = [];
				throw new Error(`Detected circular dependency in module loading. Path: ${JSON.stringify(circPath)}`);
			}

			loadStack.push(path);
			try {
				actionResult = invokeActionOrModule(path);
			}
			finally {
				loadStack.pop();
			}

			actionResults[path] = actionResult || null; // won't reattempt unsuccessful load
		}

		if (!actionResult) {
			throw new Error(`Failed to load action or module with path '${path}'!`)
		}

		return actionResult;
	}

	/**
	 * Attempts to invoke the action or module (namespace) with the given path.
	 * @param {string} path - absolute path with dot (.) separators. Cannot be blank or null/undefined.
	 * @returns any - action or module:
	 * - If there is an action at the given path, it will be returned.
	 * - if there is a module with an "index" action at the given path, the "index" action will be returned.
	 * - if there is a module at the given path without an "index" action, a module descriptor for it will be returned.
	 * @throws Error if no module exists for the given path.
	 */
	function invokeActionOrModule(path: string): any {
		const classIndex = path.lastIndexOf(".");
		const moduleName = path.substring(0, classIndex);
		const actionName = path.substring(classIndex + 1);
		let moduleInfo: Module = !moduleName ? null : System.getModule(moduleName);
		if (hasAction(moduleInfo, actionName)) {
			return invokeAction(moduleInfo, actionName);
		}
		moduleInfo = System.getModule(path);
		if (!moduleInfo) {
			throw new Error(`No action or module found for paths: '${path}', '${path}/index'!`);
		}
		return hasAction(moduleInfo, "index")
			? invokeAction(moduleInfo, "index")
			: createModule(moduleInfo);
	}

	/**
	 * Checks if a module contains an action with the given name.
	 * @param {Module} moduleInfo - module from System.getModule(). Will return false if null/undefined.
	 * @param {string} actionName - action name. Will return false if blank or null/undefined
	 * @returns {boolean} true if the module's actionDescriptions match the given name
	 */
	function hasAction(moduleInfo: Module, actionName: string): boolean {
		return !!actionName && moduleInfo?.actionDescriptions?.some(a => a?.name === actionName);
	}

	/**
	 * Creates and caches the descriptor (default exports, metadata) of an action, returning the result.
	 * @param {Module} moduleInfo - Module from System.getModule(), containing the action. Required.
	 * @param {string} actionName - name of the action
	 * @returns {any}
	 */
	function invokeAction(moduleInfo: Module, actionName: string): any {
		// Use Class library cache for backward-compatibility.
		const __classes__ = GLOBAL.__classes__ || (GLOBAL.__classes__ = {});
		const classFqdn = `${moduleInfo.name}/${actionName}`;
		let actionResult = __classes__[classFqdn];
		if (!actionResult) {
			actionResult = moduleInfo[actionName]();
			__classes__[classFqdn] = actionResult;
		}

		// Handle default exports
		if (typeof actionResult === "function") {
			actionResult = {
				default: actionResult,
			};
		}

		// Handle class metadata
		for (const propName in actionResult) {
			const exportObject = actionResult[propName];
			if (typeof exportObject === "function") {
				const descriptor = exportObject.__descriptor || (exportObject.__descriptor = {});
				descriptor.name = exportObject.name;
				descriptor.module = moduleInfo.name;
				descriptor.action = actionName;
				descriptor.fullName = `${moduleInfo.name}.${actionName}/${descriptor.name}`;
			}
		}

		return actionResult;
	}

	/**
	 * Creates a new {@link ModuleDescriptor}
	 * @param {Module} moduleInfo - Module info from System.getModule(). Required.
	 * @returns {ModuleDescriptor}
	 */
	function createModule(moduleInfo: Module): ModuleDescriptor {
		const ModuleCtor = <any>function () {
			moduleInfo.actionDescriptions.forEach(actionInfo => {
				Object.defineProperty(ModuleCtor.prototype, actionInfo.name, {
					get: () => {
						return loadActionOrModule(`${moduleInfo.name}.${actionInfo.name}`);
					},
					enumerable: true,
					configurable: true
				});
			});

			System.getAllModules()
				.filter(m => 0 === m.name.indexOf(moduleInfo.name + "."))
				.forEach(childModuleInfo => {
					const childModuleName = childModuleInfo.name.substring(childModuleInfo.name.lastIndexOf(".") + 1);
					Object.defineProperty(ModuleCtor.prototype, childModuleName, {
						get: () => {
							return loadActionOrModule(childModuleInfo.name);
						},
						enumerable: true,
						configurable: true
					});
				});
		};

		return new ModuleCtor();
	}

	/**
	 * Helper function to handle errors when loading/importing an action or module.
	 * Invokes the error handler set via {@link Module.setModuleErrorHandler} or, if not set,
	 * via the {@link DEFAULT_MODULE_ERROR_HANDLER}.
	 * @param {string | Error} err - error (message)
	 * @returns NULL, unless the custom error handler rethrows the error.
	 */
	function onError(error): null {
		let eh: ModuleErrorHandler = Module[MODULE_ERROR_HANDLER_KEY] || DEFAULT_MODULE_ERROR_HANDLER;
		eh(error);
		return null;
	}

	/**
	 * Helper function to construct the absolute path of an action or a module
	 * based on an action name / relative path and (optional) base module name.
	 * @param {string} path - action name or relative path:
	 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
	 * separated by '.' or '/'
	 * - may start with './' or any number of '../', but cannot have consecutive separators or end with a separator
	 * - slashes (/) and backslashes(\) can be used interchangeably
	 * @param {string} base - base module name:
	 * - may consist of one or more words (comprising of numbers, latin lower/upercase letters, '-' or '_'),
	 * separated by '.'
	 * - may not start or end with '.'
	 * Note - use of '-' and capital letters is not limited, despite not being recommended.
	 * @returns {string} path, conforming to the module name convention
	 * @throws Error if the path is missing/invalid
	 * @throws Error if a base path is provided but is invalid
	 * @throws Error if the path is relative and the base path is missing
	 * @throws Error if the relative path goes too many steps back (via ../) in the base path
	 */

	function constructAbsolutePath(path: string, base: string): string {
		if (!path?.match(IMPORT_PATH_REGEX)) {
			throw new Error(`Path is invalid!`);
		}
		if (path[0] !== "." && !base) {
			return path.replace(/[\/\\]/g, ".");
		}
		if (!base?.match(IMPORT_BASE_REGEX)) {
			throw new Error(`Base path is invalid: '${base}'!`);
		}
		path = path.replace(/[\\]/g, "/");
		let pathStartIndex = path.lastIndexOf('./');
		pathStartIndex = pathStartIndex >= 0 ? pathStartIndex + 2 : 0;
		path = path.substring(pathStartIndex).replace(/[\/]/g, ".");
		const backSteps = Math.floor(pathStartIndex / 3);
		if (backSteps) {
			const baseSplit = base.split(".");
			if (backSteps >= baseSplit.length) {
				throw new Error(`Too many steps back for base path '${base}'!`);
			}
			baseSplit.length = baseSplit.length - backSteps;
			base = baseSplit.join('.');
		}
		return `${base}.${path}`;
	}
	(Module as any).constructAbsolutePath = constructAbsolutePath;

	/**
	 * Extracts the imports as objects from the module as an array.
	 * @param {string[]} specifiers - list of specifiers ("default", "*", names of module elements)
	 * @param {any} importedModule - module descriptor
	 * @retruns an array of imports matching the given specifiers
	 * @throws Error if there are no specifiers
	 * @throws Errir containing a list of invalid specifiers (blank, duplicate, missing from the provided module), if any.
	 */
	function extractImports(specifiers: string[], importedModule: any): any[] {
		if (!specifiers?.length) {
			throw new Error("No actions or modules were specified for import!");
		}
		const invalidNames: string[] = [];
		const importNames: string[] = [];

		specifiers.forEach((specifier, index) => {
			if (!specifier) {
				invalidNames.push(`[${index}]: '${specifier}'`);
			}
			else if (importNames.indexOf(specifier) >= 0) {
				invalidNames.push(`[${index}]: '${specifier}' (duplicate)`);
			}
			else if (specifier !== "*" && specifier !== "default" && importedModule && !importedModule[specifier]) {
				invalidNames.push(`[${index}]: '${specifier}' (module contains no such action or namespace)`);
			}
			else {
				importNames.push(specifier);
			}
		});

		if (invalidNames?.length) {
			throw new Error("Some of the specified elements for import are invalid:\n" + invalidNames.join("\n"));
		};

		return specifiers.map(name => (name === "*" ? importedModule : importedModule[name]));
	}
	(Module as any).extractImports = extractImports;

	return Module;
});
