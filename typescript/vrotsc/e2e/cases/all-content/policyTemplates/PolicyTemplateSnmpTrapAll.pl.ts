import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
	name: "Policy Template SNMP Trap All",
	path: "MyOrg/MyProject",
	elements: {
		SNMPTrapHost: {
			type: "SNMP:TrapHost",
			events: {
				OnTrapAll: "onTrapAll"
			}
		}
	}
})
export class PolicyTemplateSnmpTrapAll {
	onTrapAll(self: SNMPTrapHost, event: any) {
		System.log("onTrapAll");
	}
}
