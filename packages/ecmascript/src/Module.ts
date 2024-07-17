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

export type ErrorHandler = (errorMessage: string) => null;

/** Default function to handle errors when importing a module/action. Logs the error and returns null. */
export const SILENT_ERR_HANDLER: ErrorHandler = (err) => { System.error(err); return null; }

export interface ModuleConstructor extends Function {
	import(...specifiers: string[]): ModuleImport;
	export(): ModuleExport;
	load(path: string, onError?: ErrorHandler): ModuleDescriptor;
}

export interface ModuleDescriptor {
	[name: string]: (Function | { [name: string]: any });
}

export interface ModuleImportConstructor {
	new(specifiers: string[]): ModuleImport;
}

export interface ModuleImport {
	from(path: string, base?: string, onError?: ErrorHandler): any | any[];
}

export interface ModuleExportConstructor {
	new(): ModuleExport;
}

export interface ModuleExport {
	named(name: string, element: any): ModuleExport;
	default(element: any): ModuleExport;
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

	Import.prototype.from = function (path: string, base?: string, onError: ErrorHandler = SILENT_ERR_HANDLER): any | any[] {
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

	Module.load = function (name: string, onError: ErrorHandler = SILENT_ERR_HANDLER): any {
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

	function loadActionOrModule(path: string, onError: ErrorHandler = SILENT_ERR_HANDLER): any {
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
			} else {
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

	function invokeActionOrModule(path: string, onError: ErrorHandler = SILENT_ERR_HANDLER): any {
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

	function hasAction(moduleInfo: Module, actionName: string): boolean {
		return !!actionName && moduleInfo?.actionDescriptions?.some(a => a?.name === actionName);
	}

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

	function createModule(moduleInfo: Module, onError: ErrorHandler = SILENT_ERR_HANDLER): ModuleDescriptor {
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
