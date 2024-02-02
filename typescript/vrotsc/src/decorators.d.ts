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
interface WorkflowDescriptor {
	id: string;
	name: string;
	path: string;
	version: string;
	presentation: string;
	parameters: WorkflowParameter[];
	items: WorkflowItemDescriptor[];
	description: string;
}

interface WorkflowItemDescriptor {
	name: string;
	input: string[];
	output: string[];
	sourceText: string;
}

interface WorkflowParameter {
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
