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
 * Function to handle errors when importing a module/action.
 * @param {string} err - error message
 * @returns null - indicates an unsuccessful attempt to load the module was made (as opposed to undefined)
 */
export type ErrorHandler = (errorMessage: string) => null;

/** Default function to handle errors when importing a module/action. Logs an error and returns null. */
export const DEFAULT_ERR_HANDLER: ErrorHandler = (err) => { System.error(err); return null; }

export interface ModuleConstructor extends Function {
	/**
	 * @param {string[]} specifiers - listed names of elements (actions/modules) to import from a Module. At least 1 is required.
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
	 * Loads an action or module (namespace) by its path.
	 * @param {string} name - module/action name (path)
	 * @param {ErrorHandler} [onError] - function to handle errors when loading a module/action.
	 * @returns {any} - action or module. Null if the path is null/undefined
	 * 					or if System cannot find a module with the given path.
	 */
	load(path: string, onError?: ErrorHandler): ModuleDescriptor;
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
	 * @param {string} path - path of the module to import from
	 * @param {string} [base] - base path of the module to import from. Required when the path is relative.
	 * @param {ErrorHandler} [onError] - function to handle errors when importing a module/action. Invoked if:
	 * - no valid specifiers were provided.
	 * - the path is missing or invalid relative to the provided basePath
	 * - the System cannot load an action or module for the given path.
	 * @returns {any|Array<any>} the imported module/actions(s).
	 */
	from(path: string, base?: string, onError?: ErrorHandler): any | any[];
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
 * @return {Any}
 */
(function () {
	let Import: ModuleImportConstructor = <any>function (specifiers: string[]) {
		this.specifiers = specifiers || [];
	};

	/**
	 * Attemps to import the specified  action or module (namespace).
	 * @param {string} path - path of the module to import from
	 * @param {string} [base] - base path of the module to import from. Required when the path is relative.
	 * @param {ErrorHandler} [onError = DEFAULT_ERR_HANDLER] - function to handle errors when importing a module/action. Invoked if:
	 * - no valid specifiers were provided.
	 * - the path is missing or invalid relative to the provided basePath
	 * - the System cannot load an action or module for the given path.
	 * Passed on recursively to any child modules of the loaded one.
	 * @returns {any|Array<any>} the imported module/actions(s).
	 */
	Import.prototype.from = function (path: string, base?: string, onError: ErrorHandler = DEFAULT_ERR_HANDLER): any | any[] {
		let error = !this.specifiers?.length ? "No actions or modules were specified for import! "
			: (!base && path?.[0] === "." ? `Cannot resolve relative path '${path}' without a base path!` : "");

		if (error) {
			return onError(error);
		}
		if (path?.[0] === ".") {
			error = `Relative path '${path}' is not valid for base path '${base}'!`;
			path = path.replace(/[\\]/g, "/");
			if (path.indexOf("./") === 0) {
				path = path.substring(2);
			}
			else {
				while (base && path.indexOf("../") === 0) {
					path = path.substring(3);
					base = base.substring(0, base.lastIndexOf("."));
				}
			}
			if (!path || !base || path.indexOf("./") >= 0) {
				return onError(error);
			}
			path = `${base}.${path}`;
		}
		path = path && path.replace(/[\/]/g, ".");
		const actionResult = loadActionOrModule(path, onError);
		if (!actionResult) {
			return onError(`Cannot import from module with path '${path}'!`);
		}

		const invalidNames = checkForInvalidImportSpecifiers(this.specifiers, actionResult);
		if (invalidNames.length) {
			return onError("Some of the specified elements for import are invalid:\n" + invalidNames.join("\n"));
		}

		let result = this.specifiers.map(name => (name === "*" ? actionResult : actionResult[name]));
		return result.length > 1 ? result : result[0];
	};

	/**
	 * Checks for invalid specifiers
	 * @param {string[]} specifiers - list of specifiers ("default", "*", names of module elements)
	 * @param {any} [importedModule] - module descriptor
	 * @returns {string[]} - list of invalid specifiers (blank, duplicate, missing from the provided module)
	 */
	function checkForInvalidImportSpecifiers(specifiers: string[], importedModule?: any): string[] {
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

		return invalidNames;
	}

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

	/** @deprecated - unused and not part of {@link ModuleExport} */
	Export.prototype.from = function (moduleName: string, ...specifiers: (string | [string, string])[]) {
		let importNames: string[] = [];
		let exportNames: string[] = [];

		specifiers.forEach(specifier => {
			if (Array.isArray(specifier)) {
				exportNames.push(specifier[0]);
				importNames.push(specifier[1]);
			}
			else {
				exportNames.push(specifier);
				importNames.push(specifier);
			}
		});

		let values = loadActionOrModule(moduleName);

		for (let i = 0; i < importNames.length; i++) {
			let importName = importNames[i];
			if (importName == "*") {
				for (let propName in values) {
					this.elements[propName] = values && values[propName];
				}
			}
			else {
				this.elements[exportNames[i]] = values && values[importName];
			}
		}

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

	/**
	 * Loads an action or module (namespace) by its path.
	 * @param {string} name - module name (path)
	 * @param {ErrorHandler} [onError = DEFAULT_ERR_HANDLER] - function to handle errors when loading a module/action.
	 * Invoked on failure to create the module or (JIT) its child actions/modules.
	 * @returns {any} - action or module. Null if the path is null/undefined
	 * 					or if System cannot find a module with the given path. 
	 */
	Module.load = function (name: string, onError: ErrorHandler = DEFAULT_ERR_HANDLER): any {
		let result: any;
		let error: any = "";
		try {
			const moduleInfo = !name ? null : System.getModule(name);
			result = !moduleInfo ? null : createModule(moduleInfo, onError);
		} catch (err) {
			error = err;
		}
		return result || onError(`Failed to load module '${name}'! ${error}`);
	};

	/**
	 * Loads and caches an action or module (namespace) by its path.
	 * @param {string} name - module name (path). Used as key for caching.
	 * @param {ErrorHandler} [onError = DEFAULT_ERR_HANDLER] - function to handle errors when loading a module/action. Invoked if:
	 * - a circular dependency is detected.
	 * - the System cannot find a module with the given path.
	 * @returns {any} - (cached) action or module or NULL, if not found or the path is blank or null/undefined.
	 */
	function loadActionOrModule(path: string, onError: ErrorHandler = DEFAULT_ERR_HANDLER): any {
		const actionResults = GLOBAL.__actions__ = (GLOBAL.__actions__ || {});
		let actionResult = !path ? null : actionResults[path];
		let error: any = "";

		if (actionResult === undefined) {
			let loadStack = GLOBAL.__importStack__ = (GLOBAL.__importStack__ || []);
			const indexOfPathInStack = loadStack.indexOf(path);
			if (indexOfPathInStack >= 0) {
				const circPath = loadStack.slice(indexOfPathInStack);
				circPath.push(path);
				loadStack = [];
				error = `Detected circular dependency in module loading. Path: ${JSON.stringify(circPath)}`;
			}
			else {
				loadStack.push(path);
				try {
					actionResult = invokeActionOrModule(path, onError);
				}
				catch (err) {
					error = err?.message || err;
				}
				finally {
					loadStack.pop();
				}
			}
			actionResults[path] = actionResult || null; // won't reattempt unsuccessful load
		}

		return actionResult || onError(`Failed to load action or module with path '${path}'! ${error}`);
	}

	/**
	 * Attempts to invoke the action or module (namespace) with the given path.
	 * @param {string} path - absolute path with dot (.) separators. Cannot be blank or null/undefined.
	 * @param {ErrorHandler} [onError = DEFAULT_ERR_HANDLER] - function to handle errors when loading a module/action.
	 * Passed on recursively to a module's child modules - to be invoked on creation.
	 * @returns any - action or module:
	 * - If there is an action at the given path, it will be returned.
	 * - if there is a module with an "index" action at the given path, the "index" action will be returned.
	 * - if there is a module at the given path without an "index" action, a module descriptor for it will be returned.
	 * @throws Error if no module exists for the given path.
	 */
	function invokeActionOrModule(path: string, onError: ErrorHandler = DEFAULT_ERR_HANDLER): any {
		const classIndex = path.lastIndexOf(".");
		const moduleName = path.substring(0, classIndex);
		const actionName = path.substring(classIndex + 1);
		let moduleInfo: Module = !moduleName ? null : System.getModule(moduleName);
		if (hasAction(moduleInfo, actionName)) {
			return invokeAction(moduleInfo, actionName);
		}
		moduleInfo = !path ? null : System.getModule(path);
		if (!moduleInfo) {
			throw new Error(`No action or module found for paths: '${path}', '${path}/index'!`);
		}
		return hasAction(moduleInfo, "index")
			? invokeAction(moduleInfo, "index")
			: createModule(moduleInfo, onError);
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
	 * @param {ErrorHandler} [onError = DEFAULT_ERR_HANDLER]  - function to handle errors when loading a module/action.
	 * Passed on recursively to a module's child modules - to be invoked on creation.
	 * @returns {ModuleDescriptor}
	 */
	function createModule(moduleInfo: Module, onError: ErrorHandler = DEFAULT_ERR_HANDLER): ModuleDescriptor {
		const ModuleCtor = <any>function () {
			moduleInfo.actionDescriptions.forEach(actionInfo => {
				Object.defineProperty(ModuleCtor.prototype, actionInfo.name, {
					get: () => {
						return loadActionOrModule(`${moduleInfo.name}.${actionInfo.name}`, onError);
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
							return loadActionOrModule(childModuleInfo.name, onError);
						},
						enumerable: true,
						configurable: true
					});
				});
		};

		return new ModuleCtor();
	}

	return Module;
});
