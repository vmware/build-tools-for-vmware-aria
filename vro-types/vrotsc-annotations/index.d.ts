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
}

interface Type<T> extends Function { new(...args: any[]): T; }

interface TypeDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, parameterIndex?: number): void;
}

export declare function In(target: Object, propertyKey: string | symbol, parameterIndex: number);
export declare function Out(target: Object, propertyKey: string | symbol, parameterIndex: number);

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
	version?: string;
	variables?: {
		[name: string]: string | PolicyAttribute;
	};
	elements?: {
		[name: string]: PolicyElement
	}
}

type PolicyElement = {
	type: VroPolicyTemplateType;
	events?: {
		OnMessage?: string | PolicyWorkflowInfo;
		OnInit?: string | PolicyWorkflowInfo;
		OnExit?: string | PolicyWorkflowInfo;
		OnExecute?: string | PolicyWorkflowInfo;
		OnTrap?: string | PolicyWorkflowInfo;
		OnTrapAll?: string | PolicyWorkflowInfo;
		OnConnect?: string | PolicyWorkflowInfo;
		OnDisconnect?: string | PolicyWorkflowInfo;
	};
	schedule?: {
		periode: VroPolicyTemplateScehdulePeriod;
		when: string;
		timezone: string;
	};
}

type PolicyWorkflowInfo = {
	workflowId: string;
	bindings?: {
		[name: string]: {
			type: string;
			variable: string;
		}
	}
}

type PolicyAttribute = {
	type: string,
	value?: AttributeValue;
	description?: string;
	configId?: string;
	configKey?: string;
}

type VroPolicyTemplateType = "AMQP:Subscription" | "MQTT:Subscription" | "SNMP:SnmpDevice" | "SNMP:TrapHost" | "Periodic Event";

type VroPolicyTemplateScehdulePeriod = "every-minutes" | "every-hours" | "every-days" | "every-weeks" | "every-months";

interface VroPolicyTemplateDecorator {
	(obj?: VroPolicyTemplate): TypeDecorator;
	new(obj?: VroPolicyTemplate): VroPolicyTemplate;
}

export declare const PolicyTemplate: VroPolicyTemplateDecorator;

type SupportedValues = string | boolean | number;

type CompositeType = {
	[name: string]: SupportedValues | SupportedValues[];
}

type AttributeValue = CompositeType | SupportedValues | SupportedValues[];

type Attribute = {
	type: string,
	value?: AttributeValue;
	description?: string;
}

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

//--------------------------------------------- POLYGLOT -------------------------------------------------------------------------------
export declare const Polyglot: VroPolyglotDecorator;

interface VroPolyglotDecorator {
	(obj?: VroPolyglotConfiguration): PolyglotMethodDecorator;
	new(obj?: VroPolyglotConfiguration): VroPolyglotConfiguration;
}

interface VroPolyglotConfiguration { package: string, method: string }
interface PolyglotMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}
