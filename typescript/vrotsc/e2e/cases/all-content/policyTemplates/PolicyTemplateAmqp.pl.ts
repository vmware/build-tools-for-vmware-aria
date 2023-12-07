import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
	name: "Policy Template Amqp",
	path: "MyOrg/MyProject",
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
