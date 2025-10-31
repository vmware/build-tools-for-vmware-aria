interface VroParameterList {
	[name: string]: VroParameter;
}

interface VroParameter {
	type: string;
	title?: string;
	required?: boolean;
	multiLine?: boolean;
	maxStringLength?: number;
	minStringLength?: number;
	numberFormat?: string;
	defaultValue?: string | number | boolean;
	availableValues?: (string | number)[];
	description?: string;
}

interface VroAttributeList {
	[name: string]: VroAttribute;
}

interface VroAttribute {
	type: string;
	bind?: boolean;
	value?: any;
	exportName?: string;
}

interface Type<T> extends Function { new(...args: any[]): T; }

interface TypeDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, parameterIndex?: number): void;
}

export declare function In(target: Object, propertyKey: string | symbol, parameterIndex: number);
export declare function Out(target: Object, propertyKey: string | symbol, parameterIndex: number);
export declare function Err(target: Object, propertyKey: string | symbol, parameterIndex: number);

// Workflow
export declare const Workflow: VroWorkflowDecorator;

interface VroWorkflow {
	id?: string;
	name?: string;
	path?: string;
	description?: string;
	version?: string;
	input?: VroParameterList;
	output?: VroParameterList;
	attributes?: VroAttributeList;
	presentation?: string;
	restartMode?: number;
	resumeFromFailedMode?: number;
}

interface VroWorkflowDecorator {
	(obj?: VroWorkflow): TypeDecorator;
	new(obj?: VroWorkflow): VroWorkflow;
}

// Policy Template
interface VroPolicyTemplate {
	id?: string;
	name?: string;
	description?: string;
	path?: string;
	type?: VroPolicyTemplateType;
	version?: string;
	templateVersion: "v1" | "v2";
	variables?: {
		[name: string]: string | PolicyAttribute;
	};
	elements?: {
		[name: string]: PolicyElement;
	};
	schedule?: {
		periode: VroPolicyTemplateSchedulePeriod;
		when: string;
		timezone: string;
	};
}

type PolicyElement = {
	type: VroPolicyTemplateTypeV2;
	events?: {
		[event: string]: string | PolicyWorkflowInfo;
	};
	schedule?: {
		periode: VroPolicyTemplateSchedulePeriod;
		when: string;
		timezone: string;
	};
};

type PolicyWorkflowInfo = {
	workflowId: string;
	bindings?: {
		[name: string]: {
			type: string;
			variable: string;
		};
	};
};

type PolicyAttribute = {
	type: string,
	value?: AttributeValue;
	description?: string;
	configId?: string;
	configKey?: string;
};

type VroPolicyTemplateType = "AMQP:Subscription" | "MQTT:Subscription" | "SNMP:SnmpDevice" | "SNMP:TrapHost";
type VroPolicyTemplateTypeV2 = string | VroPolicyTemplateType | "Periodic Event";

type VroPolicyTemplateSchedulePeriod = "every-minutes" | "every-hours" | "every-days" | "every-weeks" | "every-months";

interface VroPolicyTemplateDecorator {
	(obj?: VroPolicyTemplate): TypeDecorator;
	new(obj?: VroPolicyTemplate): VroPolicyTemplate;
}

type WorkflowEndMode = 0 | 1;

export declare const PolicyTemplate: VroPolicyTemplateDecorator;

type SupportedValues = string | boolean | number;

type CompositeType = {
	[name: string]: SupportedValues | SupportedValues[];
};

type AttributeValue = CompositeType | SupportedValues | SupportedValues[];

type Attribute = {
	type: string,
	value?: AttributeValue;
	description?: string;
};

// Configuration
interface VroConfiguration {
	id?: string;
	name?: string;
	path?: string;
	version?: string;
	attributes?: {
		[name: string]: string | Attribute;
	};
}

interface VroConfigurationDecorator {
	(obj?: VroConfiguration): TypeDecorator;
	new(obj?: VroConfiguration): VroConfiguration;
}

export declare const Configuration: VroConfigurationDecorator;

// ---------------------------------------------- Workflow Canvas Item ------------------------------------------------

export declare const Item: VroItemDecorator;

interface VroItemDecorator {
	(obj?: VroItemConfiguration): VroItemMethodDecorator;
	new(obj?: VroItemConfiguration): VroItemConfiguration;
}

interface VroItemConfiguration {
	target?: string;
	exception?: string;
}
interface VroItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Waiting Timer Item ------------------------------------------------

export declare const WaitingTimerItem: VroWaitingTimerItemDecorator;

interface VroWaitingTimerItemDecorator {
	(obj?: VroWaitingTimerItemConfiguration): VroWaitingTimerItemMethodDecorator;
	new(obj?: VroWaitingTimerItemConfiguration): VroWaitingTimerItemConfiguration;
}

interface VroWaitingTimerItemConfiguration {
	target?: string;
	exception?: string;
}

interface VroWaitingTimerItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Decision Item ------------------------------------------------

export declare const DecisionItem: VroDecisionItemDecorator;

interface VroDecisionItemDecorator {
	(obj?: VroDecisionItemConfiguration): VroDecisionItemMethodDecorator;
	new(obj?: VroDecisionItemConfiguration): VroDecisionItemConfiguration;
}

interface VroDecisionItemConfiguration {
	target?: string;
	else?: string;
	exception?: string;
}

interface VroDecisionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Root Item ------------------------------------------------

export declare const RootItem: VroRootItemDecorator;

interface VroRootItemDecorator {
	(obj?: VroRootItemConfiguration): VroRootItemMethodDecorator;
	new(obj?: VroRootItemConfiguration): VroRootItemConfiguration;
}

interface VroRootItemConfiguration {
}

interface VroRootItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Item ------------------------------------------------

export declare const WorkflowItem: VroWorkflowItemDecorator;

interface VroWorkflowItemDecorator {
	(obj?: VroWorkflowItemConfiguration): VroWorkflowItemMethodDecorator;
	new(obj?: VroWorkflowItemConfiguration): VroWorkflowItemConfiguration;
}

interface VroWorkflowItemConfiguration {
	target?: string;
	linkedItem: string;
	exception?: string;
}

interface VroWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Default Error Handler Canvas Item ------------------------------------------------

export declare const DefaultErrorHandler: VroWorkflowDefaultErrorHandlerDecorator;

interface VroWorkflowDefaultErrorHandlerDecorator {
	(obj?: VroWorkflowDefaultErrorHandlerConfiguration): VroWorkflowDefaultErrorHandlerMethodDecorator;
	new(obj?: VroWorkflowDefaultErrorHandlerConfiguration): VroWorkflowDefaultErrorHandlerConfiguration;
}

interface VroWorkflowDefaultErrorHandlerMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

interface VroWorkflowDefaultErrorHandlerConfiguration {
	target?: string;
	exceptionBinding?: string;
}

// ---------------------------------------------- Workflow Canvas End Item ------------------------------------------------

export declare const WorkflowEndItem: VroWorkflowEndItemDecorator;

interface VroWorkflowEndItemDecorator {
	(obj?: VroWorkflowEndItemConfiguration): VroWorkflowEndItemMethodDecorator;
	new(obj?: VroWorkflowEndItemConfiguration): VroWorkflowEndItemConfiguration;
}

interface VroWorkflowEndItemConfiguration {
	endMode?: WorkflowEndMode;
	businessStatus?: string;
}

interface VroWorkflowEndItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Scheduled Workflow Canvas Item ------------------------------------------------

export declare const ScheduledWorkflowItem: VroScheduledWorkflowItemDecorator;

interface VroScheduledWorkflowItemDecorator {
	(obj?: VroScheduledWorkflowItemConfiguration): VroScheduledWorkflowItemMethodDecorator;
	new(obj?: VroScheduledWorkflowItemConfiguration): VroScheduledWorkflowItemConfiguration;
}

interface VroScheduledWorkflowItemConfiguration {
	target?: string;
	linkedItem: string;
	exception?: string;
}

interface VroScheduledWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Async Workflow Canvas Item ------------------------------------------------

export declare const AsyncWorkflowItem: VroAsyncWorkflowItemDecorator;

interface VroAsyncWorkflowItemDecorator {
	(obj?: VroAsyncWorkflowItemConfiguration): VroAsyncWorkflowItemMethodDecorator;
	new(obj?: VroAsyncWorkflowItemConfiguration): VroAsyncWorkflowItemConfiguration;
}

interface VroAsyncWorkflowItemConfiguration {
	target?: string;
	linkedItem: string;
	exception?: string;
}

interface VroAsyncWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Action Canvas Item ------------------------------------------------

export declare const ActionItem: VroActionItemDecorator;

interface VroActionItemDecorator {
	(obj?: VroActionItemConfiguration): VroActionItemMethodDecorator;
	new(obj?: VroActionItemConfiguration): VroActionItemConfiguration;
}

interface VroActionItemConfiguration {
	target?: string;
	scriptModule: string;
	exception?: string;
}

interface VroActionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- User Interaction Canvas Item ------------------------------------------------

export declare const UserInteractionItem: VroUserInteractionItemDecorator;

interface VroUserInteractionItemDecorator {
	(obj?: VroUserInteractionItemConfiguration): VroUserInteractionItemMethodDecorator;
	new(obj?: VroUserInteractionItemConfiguration): VroUserInteractionItemConfiguration;
}

interface VroUserInteractionItemConfiguration {
	target: string;
}

interface VroUserInteractionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | VroUserInteractionItemConfiguration, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Switch Canvas Item ------------------------------------------------

export declare const SwitchItem: VroSwitchItemDecorator;

interface VroSwitchItemDecorator {
	(obj?: VroSwitchItemConfiguration): VroSwitchItemMethodDecorator;
	new(obj?: VroSwitchItemConfiguration): VroSwitchItemConfiguration;
}

interface VroSwitchItemConfiguration {
	target?: string;
	cases?: Array<{
		condition: any;
		target: string;
		variable?: string;
		type?: string;
		comparator?: string;
	}>;
	defaultTarget?: string;
	exception?: string;
}

interface VroSwitchItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

//--------------------------------------------- POLYGLOT -------------------------------------------------------------------------------
export declare const Polyglot: VroPolyglotDecorator;

interface VroPolyglotDecorator {
	(obj?: VroPolyglotConfiguration): PolyglotMethodDecorator;
	new(obj?: VroPolyglotConfiguration): VroPolyglotConfiguration;
}

interface VroPolyglotConfiguration {
	package: string,
	method: string;
}

interface PolyglotMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}