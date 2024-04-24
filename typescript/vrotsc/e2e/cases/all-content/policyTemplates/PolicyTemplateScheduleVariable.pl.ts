import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
	name: "Policy Template Variable",
	path: "MyOrg/MyProject",
	templateVersion: "v2",
	variables: {
		sample: {
			type: "string",
			value: "a string value with type mentioned",
			description: "A variable created with type and description"
		},
		config: {
			type: "Properties",
			configId: "8e2d3ba0-4e2c-4d4c-ad82-76de4967bf9f",
			configKey: "props",
			description: "A variable created with configuration binding"
		}
	},
	elements: {
		AMQPSubscription: {
			type: "AMQP:Subscription",
			events: {
				OnMessage: "onMessage"
			}
		}
	}
})
export class PolicyTemplateAmqp {
	onMessage(self: AMQPSubscription, event: any) {
		System.log("onMessage");
	}
}
