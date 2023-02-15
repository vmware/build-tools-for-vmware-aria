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
 function formatName(name:string) {
	return name.replace(/([^\\\/]+$)/, function ( match ){
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

		vroes.require = function (name: string) {
			return Module.import("*").from(formatName( name ));
		};

		vroes.importLazy = function (name: string) {
			const result: any = {};
			Object.defineProperty(result, "_", {
				get: () => {
					return result.__lazyAction__ || (result.__lazyAction__ = Module.import("*").from(formatName( name )))
				},
				enumerable: true,
				configurable: true
			});
			return result;
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
