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

export default class ImportsRewriter {
	constructor(private readonly source: string, private readonly pathMap: {moduleName:string, moduleRewrite:string}[]) {}

	rewrite() {
		let result = this.source;
		this.pathMap.forEach(({ moduleName, moduleRewrite }) => {
			const moduleNameEscaped = moduleName.replace(/([\[\\^\$\.\|\?\*\(\]\/\{\}])/g, "\\$1");
			const re = new RegExp(String.raw`(^|[;\s]+)?(import\s+.*from\s*['"])(${moduleNameEscaped})(.*)(['"]);?`, 'gi');
			const replaceValue = `$1$2${moduleRewrite}$4$5`;
			console.debug(`Regex: ${re}`);
			console.debug(`New value: ${replaceValue}`);
			result = result.replace(re, replaceValue);
			console.debug(`Rewriting ${moduleName} to content:`);
			console.log(result);
		});
		return result;
	}
}
