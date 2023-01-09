
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
