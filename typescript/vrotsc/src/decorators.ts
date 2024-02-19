/////////////////////////////////// Configuration Decorator ///////////////////////////////////

/**
* Describes the configuration element.
* This corresponds to the `VroConfiguration` decorator in `vrotsc-annotations`.
*
* Search or `Configuration/VroConfigurationDecorator` in the `vrotsc-annotations` for more information.
*/
export interface ConfigurationDescriptor {
	id: string;
	version: string;
	name: string;
	path: string;
	attributes: Record<string, string | ConfigurationAttribute>;
}

export interface ConfigurationAttribute {
	type: string;
	value?: any;
	description?: string;
}

/////////////////////////////////// Workflow Decorator ///////////////////////////////////

/**
* Describes a Workflow element.
* This corresponds to the `VroWorkflow` decorator in `vrotsc-annotations`.
*
* Search for `Workflow/VroWorkflowDecorator` in the `vrotsc-annotations` for more information.
*/
export interface WorkflowDescriptor {
	id: string;
	name: string;
	path: string;
	version: string;
	presentation: string;
	parameters: WorkflowParameter[];
	items: WorkflowItemDescriptor[];
	description: string;
}

export interface WorkflowItemDescriptor {
	name: string;
	input: string[];
	output: string[];
	sourceText: string;
}

export interface WorkflowParameter {
	name: string;
	type: string;
	title?: string;
	required?: boolean;
	description?: string;
	multiLine?: boolean;
	hidden?: boolean;
	maxStringLength?: number;
	minStringLength?: number;
	numberFormat?: string;
	defaultValue?: string;
	availableValues?: string[];
	parameterType: WorkflowParameterType;
	isAttribute?: boolean;
}

export enum WorkflowParameterType {
	Default = 0,
	Input = 1 << 0,
	Output = 2 << 1,
}

/////////////////////////////////// Polyglot Decorator ///////////////////////////////////


/**
* Describes a Polyglot element to execute
*
* This corresponds to the `VroPolyglotConfiguration` decorator in `vrotsc-annotations`.
*
* Search for `Polyglot/VroPolyglotDecorator` in the `vrotsc-annotations` for more information.
*/
export interface PolyglotDescriptor {
	package: string;
	method: string;
}


/////////////////////////////////// Policy Template Decorator ///////////////////////////////////

export interface PolicyTemplateDescriptor {
	id: string;
	name: string;
	description?: string;
	path: string;
	tag: string;
	type: string;
	version: string;
	schedule?: PolicyTemplateScheduleDescriptor;
	events: PolicyTemplateEventDescriptor[];
}

export interface PolicyTemplateScheduleDescriptor {
	periode: string;
	when: string;
	timezone: string;
}

export interface PolicyTemplateEventDescriptor {
	type: string;
	sourceText: string;
}
