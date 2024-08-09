declare var VROES: VROES;

declare type ModuleErrorHandler = (error: string | Error) => null;

declare enum DefaultModuleErrorHandlers { SYS_ERROR, SYS_WARN, SYS_INFO, SYS_DEBUG, SILENT, THROW_ERROR }

declare interface VROES {
	import(...specifiers: string[]): VROESModuleImport;
	export(): VROESModuleExport;
	load(path: string): VROESModuleDescriptor;
	setModuleErrorHandler(eh: DefaultModuleErrorHandlers | ModuleErrorHandler): void;
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
