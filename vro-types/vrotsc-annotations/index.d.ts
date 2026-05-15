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

/**
 * Binds a workflow input parameter to a method argument.
 *
 * Use {@link In} on a method parameter within a decorated workflow item to declare an input binding.
 * See {@link WorkflowItem}, {@link Item}, {@link ScheduledWorkflowItem} and other decorators for usage.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#argument-decorators|Argument Decorators}
 */
export declare function In(target: Object, propertyKey: string | symbol, parameterIndex: number): void;
/**
 * Binds a workflow output parameter to a method argument.
 *
 * Use {@link Out} on a method parameter within a decorated workflow item to declare an output binding.
 * Often used alongside {@link In} when a parameter is both read and written.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#argument-decorators|Argument Decorators}
 */
export declare function Out(target: Object, propertyKey: string | symbol, parameterIndex: number): void;
/**
 * Binds an error (exception) output to a method argument.
 *
 * Use {@link Err} to capture error information when an item raises an exception,
 * typically in conjunction with {@link DefaultErrorHandler} or {@link WorkflowEndItem}.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#argument-decorators|Argument Decorators}
 */
export declare function Err(target: Object, propertyKey: string | symbol, parameterIndex: number): void;

// Workflow
/**
 * Declares a workflow and its schema.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#workflow-decorator|Workflow Decorator}
 */
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
	/** Server restart behaviour (0: do not resume workflow run, 1: resume workflow run). */
	restartMode?: 0 | 1;
	/** Resume from failed behaviour (0: system default, 1: enabled, 2: disabled). */
	resumeFromFailedMode?: 0 | 1 | 2;
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

/** 0 = success, 1 = error. */
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

/**
 * Scriptable task canvas item.
 *
 * Use {@link Item} to declare a standard task in the workflow canvas.
 *
 * Bind inputs/outputs via {@link In} and {@link Out}.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#item|Item}
 */
export declare const Item: VroItemDecorator;

interface VroItemDecorator {
	(obj?: VroItemConfiguration): VroItemMethodDecorator;
	new(obj?: VroItemConfiguration): VroItemConfiguration;
}

interface VroItemConfiguration {
	/**
	 * Name of the next in line item.
	 *
	 * - If set to `"end"`, points to the end of the workflow.
	 * - If `null`, points to the next item (or end if none).
	 * - If a non-existing name is provided, points to the end.
	 */
	target?: string;
	/**
	 * Name of the next item to route to when the current item throws an exception.
	 *
	 * - If this is set to `null` or empty string, the parameter is ignored.
	 * - If this is set to a string, but it does not exist in the workflow, it will point to the end of the workflow.
	 */
	exception?: string;
}
interface VroItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Waiting Timer Item ------------------------------------------------

/**
 * Waiting timer canvas item.
 *
 * Expects an {@link In} parameter that represents the timer variable (e.g., a `Date`).
 * Without the bound input, the workflow will not function correctly.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#waitingtimeritem|Waiting Timer Item}
 */
export declare const WaitingTimerItem: VroWaitingTimerItemDecorator;

interface VroWaitingTimerItemDecorator {
	(obj?: VroWaitingTimerItemConfiguration): VroWaitingTimerItemMethodDecorator;
	new(obj?: VroWaitingTimerItemConfiguration): VroWaitingTimerItemConfiguration;
}

interface VroWaitingTimerItemConfiguration {
	/** Name of the next in line item. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** Next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroWaitingTimerItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Decision Item ------------------------------------------------

/**
 * Decision canvas item.
 *
 * Implement the decorated method to return a boolean. `true` routes to `target`, `false` routes to `else`.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#decisionitem|Decision Item}
 */
export declare const DecisionItem: VroDecisionItemDecorator;

interface VroDecisionItemDecorator {
	(obj?: VroDecisionItemConfiguration): VroDecisionItemMethodDecorator;
	new(obj?: VroDecisionItemConfiguration): VroDecisionItemConfiguration;
}

interface VroDecisionItemConfiguration {
	/** Name of the next item when the decision evaluates to `true`. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** Name of the next item when the decision evaluates to `false`. Same semantics as {@link VroItemConfiguration.target}. */
	else?: string;
	/** Name of the next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroDecisionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Canvas Root Item ------------------------------------------------

/** This is a meta decorator. Add this to whichever function you want to be the entry point of the workflow.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#rootitem|Root Item}
 */
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

/**
 * Workflow-call canvas item.
 *
 * Calls another workflow by ID and supports binding of inputs/outputs via {@link In} and {@link Out}.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#workflowitem|Workflow Item}
 */
export declare const WorkflowItem: VroWorkflowItemDecorator;

interface VroWorkflowItemDecorator {
	(obj?: VroWorkflowItemConfiguration): VroWorkflowItemMethodDecorator;
	new(obj?: VroWorkflowItemConfiguration): VroWorkflowItemConfiguration;
}

interface VroWorkflowItemConfiguration {
	/** Name of the next in line item. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** The ID of the workflow to call. */
	linkedItem: string;
	/** Next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Workflow Default Error Handler Canvas Item ------------------------------------------------

/**
 * Default error handler canvas item.
 *
 * Can be attached either to a workflow item or workflow end. Use {@link Err} and {@link Out} to bind the error message.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#defaulterrorhandler|Default Error Handler}
 */
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
	/** Target item to be attached to the default error handler; could be a workflow item or workflow end. */
	target?: string;
	/** Name of the variable that will hold the exception data when triggered. */
	exceptionBinding?: string;
}

// ---------------------------------------------- Workflow Canvas End Item ------------------------------------------------

/**
 * Workflow end canvas item.
 *
 * You can bind error output via {@link Err} and other outputs via {@link Out}.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#workflowenditem|Workflow End Item}
 */
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

/**
 * Scheduled workflow-call canvas item.
 *
 * Schedules another workflow for future execution. Bind inputs/outputs via {@link In} and {@link Out}.
 *
 * Special bindings:
 * - Input `workflowScheduleDate: Date` is required; must be named exactly `workflowScheduleDate`.
 * - Output `scheduledTask: Task` is optional; if present, must be named exactly `scheduledTask`.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#scheduledworkflowitem|Scheduled Workflow Item}
 */
export declare const ScheduledWorkflowItem: VroScheduledWorkflowItemDecorator;

interface VroScheduledWorkflowItemDecorator {
	(obj?: VroScheduledWorkflowItemConfiguration): VroScheduledWorkflowItemMethodDecorator;
	new(obj?: VroScheduledWorkflowItemConfiguration): VroScheduledWorkflowItemConfiguration;
}

interface VroScheduledWorkflowItemConfiguration {
	/** Name of the next in line item. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** The ID of the workflow to schedule. */
	linkedItem: string;
	/** Name of the next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroScheduledWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Async Workflow Canvas Item ------------------------------------------------

/**
 * Asynchronous workflow-call canvas item.
 *
 * Starts another workflow asynchronously and returns a token via output binding.
 *
 * Special bindings:
 * - Output `wfToken: WorkflowToken` is required; must be named exactly `wfToken`.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#asyncworkflowitem|Async Workflow Item}
 */
export declare const AsyncWorkflowItem: VroAsyncWorkflowItemDecorator;

interface VroAsyncWorkflowItemDecorator {
	(obj?: VroAsyncWorkflowItemConfiguration): VroAsyncWorkflowItemMethodDecorator;
	new(obj?: VroAsyncWorkflowItemConfiguration): VroAsyncWorkflowItemConfiguration;
}

interface VroAsyncWorkflowItemConfiguration {
	/** Name of the next in line item. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** The ID of the workflow to call asynchronously. */
	linkedItem: string;
	/** Name of the next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroAsyncWorkflowItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Action Canvas Item ------------------------------------------------

/**
 * Action-call canvas item.
 *
 * Calls a vRO Action by module path and name. Bind inputs via {@link In}; a single output of type `ActionResult` is expected.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#actionitem|Action Item}
 */
export declare const ActionItem: VroActionItemDecorator;

interface VroActionItemDecorator {
	(obj?: VroActionItemConfiguration): VroActionItemMethodDecorator;
	new(obj?: VroActionItemConfiguration): VroActionItemConfiguration;
}

interface VroActionItemConfiguration {
	/** Name of the next in line item. Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** Module/action path in the form `modulePath/actionName`, e.g. `com.vmware.pscoe.library.general/echo`. */
	scriptModule: string;
	/** Name of the next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroActionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- User Interaction Canvas Item ------------------------------------------------

/**
 * User Interaction canvas item.
 *
 * Allows form-driven user input with optional access restrictions.
 *
 * Optional inputs (names must match exactly):
 * - `security_assignees: Array/LdapUser`
 * - `security_assignee_groups: Array/LdapGroup`
 * - `security_group: LdapGroup`
 * - `timeout_date: Date`
 *
 * Multiple outputs can be bound to capture user answers.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#userinteractionitem|User Interaction Item}
 */
export declare const UserInteractionItem: VroUserInteractionItemDecorator;

interface VroUserInteractionItemDecorator {
	(obj?: VroUserInteractionItemConfiguration): VroUserInteractionItemMethodDecorator;
	new(obj?: VroUserInteractionItemConfiguration): VroUserInteractionItemConfiguration;
}

interface VroUserInteractionItemConfiguration {
	/**
	 * Name of the next item.
	 *
	 * You can specify another {@link UserInteractionItem} as a `target`, thus chaining multiple user interaction items.
	 */
	target: string;
	/** Name of the next item on exception or timeout. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroUserInteractionItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | VroUserInteractionItemConfiguration, descriptor?: TypedPropertyDescriptor<Function>): void;
}

// ---------------------------------------------- Switch Canvas Item ------------------------------------------------

/**
 * Switch canvas item.
 *
 * Routes execution based on a variable or expression using case definitions.
 *
 * @see {@link https://github.com/vmware/build-tools-for-vmware-aria/blob/main/docs/versions/latest/Components/Archetypes/typescript/Components/Workflows.md#switchitem|Switch Item}
 */
export declare const SwitchItem: VroSwitchItemDecorator;

interface VroSwitchItemDecorator {
	(obj?: VroSwitchItemConfiguration): VroSwitchItemMethodDecorator;
	new(obj?: VroSwitchItemConfiguration): VroSwitchItemConfiguration;
}

interface VroSwitchCase {
	/** The value to match/compare against the switch variable or expression. */
	condition: any;
	/** The name of the next item to execute when this case matches. */
	target: string;
	/** Name of the variable to evaluate (optional; can be inferred from method parameters). */
	variable?: string;
	/** Data type of the variable being switched on (e.g., "number", "string", "boolean"). */
	type?: string;
	/** Comparison operator for case evaluation (defaults to `equals` if omitted). */
	comparator?: "equals" | "different" | "smaller" | "smaller or equals" | "greater" | "greater or equals" | "is true" | "is false" | "contains" | "match" | "is defined";
}

interface VroSwitchItemConfiguration {
	/** Name of the next item after the switch (can chain switches). Same semantics as {@link VroItemConfiguration.target}. */
	target?: string;
	/** Array of case definitions controlling routing logic. */
	cases: VroSwitchCase[];
	/** Name of the next item when none of the cases match. Same semantics as {@link VroItemConfiguration.target}. */
	defaultTarget?: string;
	/** Name of the next item on exception. Same semantics as {@link VroItemConfiguration.exception}. */
	exception?: string;
}

interface VroSwitchItemMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | VroSwitchItemConfiguration, descriptor?: TypedPropertyDescriptor<Function>): void;
}

//--------------------------------------------- POLYGLOT -------------------------------------------------------------------------------
export declare const Polyglot: VroPolyglotDecorator;

interface VroPolyglotDecorator {
	(obj?: VroPolyglotConfiguration): PolyglotMethodDecorator;
	new(obj?: VroPolyglotConfiguration): VroPolyglotConfiguration;
}

interface VroPolyglotConfiguration {
	package: string;
	method: string;
}

interface PolyglotMethodDecorator {
	<T extends Type<any>>(type: T): T;
	(target: Object, propertyKey?: string | symbol, descriptor?: TypedPropertyDescriptor<Function>): void;
}