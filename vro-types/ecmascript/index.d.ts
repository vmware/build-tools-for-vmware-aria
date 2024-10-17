declare var VROES: VROES;

declare type ModuleErrorHandler = (error: string | Error) => null;

declare const DEFAULT_MODULE_ERROR_HANDLER: ModuleErrorHandler;

declare interface VROES {
	import(...specifiers: string[]): VROESModuleImport;
	export(): VROESModuleExport;
	load(path: string): VROESModuleDescriptor;
	setModuleErrorHandler(eh: ModuleErrorHandler): void;
}

declare interface VROESModuleDescriptor {
	[name: string]: (Function | { [name: string]: any });
}

declare interface VROESModuleImport {
	from(path: string): any | any[];
}

declare interface VROESModuleExport {
	named(name: string, element: any): VROESModuleExport;
	default(element: any): VROESModuleExport;
	build(): { [name: string]: any };
}

declare interface ClassConstructor extends Function {
	__descriptor: ClassDescriptor;
}

declare interface ClassDescriptor {
	module: string,
	action: string,
	name: string,
	fullName: string,
}
