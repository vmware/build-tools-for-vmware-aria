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
 * Defines methods for loading and definition of classes in vRO
 */
(function () {
	var global = System.getContext() || (function () {
		return this;
	}).call(null);

	var loadStack = [];

	var defineClass = function () {
		function Class() {
			throw "private constructor";
		}

		/**
		 * Defines a new concrete Class with specified name and inheritance from a single class
		 * @param name - the name of the class to define
		 * @param constructorFunc - a function which will be assigned as constructor for the class
		 * @param classPrototypeObj - a JS object which properties will be set on the Class prototype
		 * @param extendClass - the class object (loaded from Class#load method) to extend
		 * @return a new Class type
		 */
		Class.define = function (name, constructorFunc, classPrototypeObj, extendClass) {
			var originalConstructor = null;

			if (typeof (name) != "string") {
				extendClass = classPrototypeObj;
				classPrototypeObj = constructorFunc;
				constructorFunc = name;
			} else {
				// wrap constructor annonymous function in a named function
				// so that constructionFunc.constructor.name returns name correctly.
				originalConstructor = constructorFunc;
				constructorFunc = eval( // NOSONAR
					"(function() {\n" +
					"   function " + name + "() {\n" +
					"       originalConstructor.apply(this,arguments);\n" +
					"   }\n" +
					"   return " + name + ";\n" +
					"})();"
				);
			}

			// Copy instance members to class prototype
			if (classPrototypeObj) {
				Object.keys(classPrototypeObj).forEach(function (propName) {
					constructorFunc.prototype[propName] = classPrototypeObj[propName];
				});
			}

			// Convert class prototype to property object
			var propsObj = {};
			Object.keys(constructorFunc.prototype).forEach(function (propName) {
				propsObj[propName] = {
					value: constructorFunc.prototype[propName],
					enumerable: true,
					configurable: true,
					writable: true
				};
			});

			// Setup inheritance
			constructorFunc.prototype = Object.create(extendClass ? extendClass.prototype : constructorFunc.prototype, propsObj);
			constructorFunc.prototype.constructor = constructorFunc;

			if (constructorFunc.prototype.toString === undefined) {
				constructorFunc.prototype.toString = function toString() {
					return "[object " + this.constructor.name + "]";
				}
			}

			// transfer the static members of the input constructor function to the result constructor
			// if they are different functions (the case where the `name` parameter is a string)
			if (originalConstructor && constructorFunc !== originalConstructor) {
				for (var memberName in originalConstructor) {
					constructorFunc[memberName] = originalConstructor[memberName];
				}
			}

			/* copy static functions */
			if (extendClass) {
				for (var memName in extendClass) {
					// add the static member only if it's not already defined in the subclass
					if (!constructorFunc.hasOwnProperty(memName)) {
						constructorFunc[memName] = extendClass[memName];
					}
				}
			}

			// descriptor property definition
			var [module, action] = loadStack.length ? loadStack[loadStack.length - 1].split("/") : ["", ""];
			constructorFunc.descriptor = {
				loader: Class,
				module: module,
				action: action,
				name: constructorFunc.name,
				fullName: module + "." + action + "/" + constructorFunc.name
			}

			return constructorFunc;
		};

		/**
		 * Loads a class and ensures there is a single version of this class access child scripting contexts
		 * created by vRO. This is important in the cases you want to compare instances with "instancceof" operator.
		 *
		 * @param {string} module - refers to the vRO module path where script is located (e.g. "com.vmware.pscoe.customer")
		 * @param {string} name - refers to the vRO action name to load from.
		 * @return the loaded Class or the shared version of it previously loaded.
		 */
		Class.load = function (module, name) {
			if (!global.__classes__) {
				global.__classes__ = {};
			}

			var classFqdn = module + "/" + name;

			var cls = global.__classes__[classFqdn];
			if (!cls) {
				var clsIndex = loadStack.indexOf(classFqdn);

				if (clsIndex > -1) {
					var circPath = loadStack.slice(clsIndex);
					circPath.push(classFqdn);

					/* clean up loading stack to not leave "dirty" global state. */
					loadStack = [];
					throw "Detected circular dependency in Class loading. Path: " +
					JSON.stringify(circPath) +
					" You can resolve by moving the Class.load() right before " +
					"you refer to the class being loaded (in class constructor/prototype methos).";
				}

				loadStack.push(classFqdn);
				var action = System.getModule(module)[name];
				if(!action) {
					throw new Error("Missing reference: " + module + "/" + name);
				}
				cls = action();
				loadStack.pop();

				if (typeof cls !== "function") {
					var shapeReport = Class.validateExportShape(cls, module, name);
					System.error("[Class][W_EXPORT_SHAPE_MISMATCH] Class.load('" + module + "', '" + name + "')" +
						" returned a " + shapeReport.exportType + " instead of a constructor function." +
						(shapeReport.exportKeys.length ? " Export keys: [" + shapeReport.exportKeys.join(", ") + "]." : "") +
						" If this is unexpected, check for module path misconfiguration (e.g. 'com.vmware.pscoe.library.ts.*' aliases)." +
						" Use Class.validateExportShape() to inspect the export.");
				}

				global.__classes__[classFqdn] = cls;
			}
			return cls;
		};

		/**
		 * This function allows for overriding of a class by it's modul/name reference with
		 * an extended class.
		 */
		Class.mock = function (module, name, constructorFunc, classPrototypeObj) {
			var ActualClass = Class.load(module, name);
			var MockClass = Class.define(constructorFunc, classPrototypeObj, ActualClass);

			var actualClassFqdn = module + "/" + name;
			global.__classes__[actualClassFqdn] = MockClass;

			return MockClass;
		};

		/**
		 * Validates the export shape of a loaded class or module export.
		 * Useful for diagnosing export shape mismatches from Class.load() or System.getModule().
		 *
		 * @param {any} exportValue - the exported value to inspect (constructor, function, object, etc.)
		 * @param {string} module - module path for context in the report
		 * @param {string} name - action/class name for context in the report
		 * @return {Object} diagnostic report with isFunction, exportKeys, issues array, and recommendations
		 */
		Class.validateExportShape = function (exportValue, module, name) {
			var report = {
				timestamp: new Date().toISOString(),
				module: module,
				name: name,
				exportType: typeof exportValue,
				isFunction: typeof exportValue === "function",
				exportKeys: [],
				issues: []
			};

			if (typeof exportValue === "function") {
				report.isConstructor = true;
				return report;
			}

			if (typeof exportValue === "object" && exportValue !== null) {
				var keys = [];
				for (var key in exportValue) {
					if (exportValue.hasOwnProperty(key)) {
						keys.push(key);
					}
				}
				report.exportKeys = keys;
				report.issues.push(
					"[Class][E_EXPORT_SHAPE_MISMATCH] Expected a constructor function, but got an object with keys: [" +
					keys.join(", ") + "]. This typically occurs when module loading returns a wrapper object instead of the target constructor. " +
					"Verify the action export shape is correct and module paths are not misconfigured."
				);
			} else {
				report.issues.push(
					"[Class][E_EXPORT_SHAPE_INVALID] Expected a constructor function, but got type '" +
					typeof exportValue + "'. Module: " + module + ", action: " + name
				);
			}

			return report;
		};

		/**
		 * Traverses all modules and actions in vRO and loads only the classes
		 * which module name (and optionally class name) match a criteria.
		 *
		 * Modules that match the criteria are sorted alphabetically, then
		 * actions from each module are loaded in alphabetical order as well.
		 *
		 * @param {RegExp|String|String[]} moduleFilter - Either a pattern to match module names against,
		 * 									  		      or an ordered list of module names to be loaded
		 * @param {RegExp|String|undefined} classFilter - Either a pattern to match class names against,.
		 * 									  		      or an exact class name to be loaded from all found modules.
		 * @return {Function[]} - returns array of actions loaded using Class.load().
		 * 						  Note that instances of these classes are NOT created.
		 */
		Class.find = function (moduleFilter, classFilter) {
			var modules = [];

			if (typeof moduleFilter === "string") {
				var mod = System.getModule(moduleFilter)
				if(mod != null){
					modules.push(mod);	
				}
			} else if (Array.isArray(moduleFilter) && moduleFilter.length > 0) {
				modules = moduleFilter.map(function (mod) {
					return System.getModule(mod);
				}).filter(function(mod){ return !!mod; });
			} else if (moduleFilter instanceof RegExp) {
				var regexFilter = new RegExp(moduleFilter);
				modules = System.getAllModules()
					.filter(function (mod) {
						return regexFilter.test(mod.name);
					})
					.sort();
			}

			return modules.reduce(function (result, mod) {
				mod.actionDescriptions
					.filter(function (act) {
						if (typeof classFilter === "string") {
							return act.name === classFilter;
						} else if (classFilter instanceof RegExp) {
							return new RegExp(classFilter).test(act.name);
						}

						return true;
					})
					.sort()
					.forEach(function (act) {
						result.push(Class.load(mod.name, act.name))
					});

				return result;
			}, []);
		};

		return Class;
	};

	/* don't redeclare class global var, if already set */
	global.__classes__ = global.__classes__ || {};

	if (!global.__classes__["com.vmware.pscoe.library.class/Class"]) {
		global.__classes__["com.vmware.pscoe.library.class/Class"] = defineClass();
	}
	return global.__classes__["com.vmware.pscoe.library.class/Class"];
});
