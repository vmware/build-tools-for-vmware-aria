import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
	name: "Policy Template Amqp V1",
	path: "MyOrg/MyProject",
	type: "AMQP:Subscription",
	templateVersion: "v1"
})
export class PolicyTemplateAmqp {
	onMessage(self: AMQPSubscription, event: any) {
		System.log("onMessage");
	}
}
