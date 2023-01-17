/**
 * MQTT Broker
 */
declare interface MQTTBroker {
	readonly id: string;
	readonly name: string;
	readonly displayName: string;
	readonly serverURI: string;
	readonly authType: "basic" | "vcd";
	readonly username: string;
	readonly useSSL: boolean;
	readonly acceptAllCertificates: boolean;
	readonly cleanSession: boolean;
	readonly connectionTimeout: number;

	subscribe(topic: string, props: any): MQTTSubscription;
	/**
	 * Removes this broker.
	 */
	remove(): void;
	/**
	 * Updates this broker properties.
	 * @param props 
	 */
	update(props: any): MQTTBroker;
	/**
	 * Validate connection for this broker
	 */
	validate(): void;
}

/**
 * Manager that enables MQTT broker creation and configuration reload.
 */
declare class MQTTBrokerManager {
	/**
	 * Creates a new broker.
	 * @param props 
	 */
	static addBroker(props: any): MQTTBroker;
}

/**
 * MQTT Subscription
 */
declare interface MQTTSubscription {
	readonly id: string;
	readonly name: string;
	readonly displayName: string;
	readonly topic: string;

	/**
	 * Removes this subscription
	 */
	remove(): void;
	/**
	 * Retrieves a trigger properties
	 * @param trigger 
	 */
	 retrieveMessage(trigger: any): MQTTMessage;
}

/**
 * MQTT Message
 */
declare class MQTTMessage {
	id: string;
	broker: string;
	topic: string;
	payload: string;
	/**
	 * Constructs new message object
	 */
	constructor();
}
