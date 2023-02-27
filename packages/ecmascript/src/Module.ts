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

export interface ModuleConstructor extends Function {
	import(...specifiers: string[]): ModuleImport;
	export(path: string): ModuleExport;
	load(path: string): ModuleDescriptor;
	require(path: string): any;
}

export interface ModuleDescriptor {
	[name: string]: (Function | { [name: string]: any });
}

export interface ModuleImportConstructor {
	new(specifiers: string[]): ModuleImport;
}

export interface ModuleImport {
	from(path: string, base?: string): any | any[];
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

	Import.prototype.from = function (path: string, base?: string): any | any[] {
		if (base) {
			if (path[0] == ".") {
				path = path.replace(/[\\]/g, "/");
				if (path.indexOf("./") == 0) {
					path = path.substring(2);
				}
				else {
					while (path.indexOf("../") == 0) {
						path = path.substring(3);
						base = base.substring(0, base.lastIndexOf("."));
					}
				}

				path = `${base}.${path}`;
			}
			path = path.replace(/[\/]/g, ".");
		}
		let actionResult = loadActionOrModule(path);
		let result = this.specifiers.map(name => (name == "*" ? actionResult : actionResult && actionResult[name]));
		return result.length > 1 ? result : result[0];
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

	Module.load = function (name: string) {
		let moduleInfo = System.getModule(name);
		return moduleInfo ? createModule(moduleInfo) : null;
	};

	function loadActionOrModule(path: string): any {
		let actionResults = GLOBAL.__actions__ = (GLOBAL.__actions__ || {});
		let actionResult = actionResults[path];

		if (actionResult === undefined) {
			let loadStack = GLOBAL.__importStack__ = (GLOBAL.__importStack__ || []);
			let indexOfPathInStack = loadStack.indexOf(path);
			if (indexOfPathInStack >= 0) {
				let circPath = loadStack.slice(indexOfPathInStack);
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

			actionResults[path] = actionResult;
		}

		return actionResult;
	}

	function invokeActionOrModule(path: string): any {
		let classIndex = path.lastIndexOf(".");
		let moduleName = path.substring(0, classIndex);
		let actionName = path.substring(classIndex + 1);
		let moduleInfo = <Module>System.getModule(moduleName);
		if (moduleInfo && moduleInfo.actionDescriptions.some(a => a.name == actionName)) {
			return invokeAction(moduleInfo, actionName);
		}
		else {
			moduleInfo = <Module>System.getModule(path);
			if (moduleInfo) {
				if (moduleInfo.actionDescriptions.some(a => a.name == "index")) {
					return invokeAction(moduleInfo, "index")
				}
				else {
					return createModule(moduleInfo);
				}
			}
		}
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
		if (typeof actionResult == "function") {
			actionResult = {
				default: actionResult,
			};
		}

		// Handle class metadata
		for (let propName in actionResult) {
			const exportObject = actionResult[propName];
			if (typeof exportObject == "function") {
				const descriptor = exportObject.__descriptor || (exportObject.__descriptor = {});
				descriptor.name = exportObject.name;
				descriptor.module = moduleInfo.name;
				descriptor.action = actionName;
				descriptor.fullName = `${moduleInfo.name}.${actionName}/${descriptor.name}`;
			}
		}

		return actionResult;
	}

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
				.filter(m => 0 == m.name.indexOf(moduleInfo.name + "."))
				.forEach(childModuleInfo => {
					let childModuleName = childModuleInfo.name.substring(childModuleInfo.name.lastIndexOf(".") + 1);
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

	return Module;
});
