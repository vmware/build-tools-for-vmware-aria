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
	value: any;
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
	type?: VroPolicyTemplateType;
	version?: string;
	templateVersion?: "v1" | "v2";
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
}

interface VroWaitingTimerItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}
// <workflow-item name="item2" out-name="item0" type="waiting-timer">
// 	<display-name>
// 		<![CDATA[waitForEvent]]>
// 	</display-name>
// 	<in-binding>
// 		<bind name="waitingTimer" type="Date" export-name="waitingTimer" />
// 	</in-binding>
// 	<position x="385.0" y="55.40909090909091" />
// </workflow-item>

// ---------------------------------------------- Workflow Canvas Decision Item ------------------------------------------------

export declare const DecisionItem: VroDecisionItemDecorator;

interface VroDecisionItemDecorator {
	(obj?: VroDecisionItemConfiguration): VroDecisionItemMethodDecorator;
	new(obj?: VroDecisionItemConfiguration): VroDecisionItemConfiguration;
}

interface VroDecisionItemConfiguration {
	target?: string;
	else?: string;
}

interface VroDecisionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// <workflow-item name="item3" out-name="item4" type="custom-condition" alt-out-name="item2">
//   <display-name><![CDATA[Decision]]></display-name>
//   <script encoded="false"><![CDATA[return waitingTimer !== null]]></script>
//   <in-binding>
//     <bind name="waitingTimer" type="Date" export-name="waitingTimer"/>
//   </in-binding>
//   <out-binding/>
//   <description><![CDATA[Custom decision based on a custom script.]]></description>
//   <position y="40.0" x="380.0"/>
// </workflow-item>

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

// <workflow-item name="item4" out-name="item1" type="waiting-timer">
// 	<display-name><![CDATA[Waiting timer]]></display-name>
// 	<in-binding>
// 		<bind name="timer.date" type="Date" export-name="waitingTimer">
// 			<description><![CDATA[This timer item will wait until date and will continue workflow execution.]]></description>
// 		</bind>
// 	</in-binding>
// 	<out-binding />
// 	<position y="110.0" x="340.0" />
// </workflow-item>

//--------------------------------------------- POLYGLOT -------------------------------------------------------------------------------
export declare const Polyglot: VroPolyglotDecorator;

interface VroPolyglotDecorator {
	(obj?: VroPolyglotConfiguration): PolyglotMethodDecorator;
	new(obj?: VroPolyglotConfiguration): VroPolyglotConfiguration;
}

interface VroPolyglotConfiguration { package: string, method: string; }
interface PolyglotMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}
