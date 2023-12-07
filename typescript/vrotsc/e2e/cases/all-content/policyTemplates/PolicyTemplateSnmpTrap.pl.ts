import { PolicyTemplate } from "vrotsc-annotations";

@PolicyTemplate({
	name: "Policy Template SNMP Trap",
	path: "MyOrg/MyProject",
	elements: {
		SNMPSnmpDevice: {
			type: "SNMP:SnmpDevice",
			events: {
				OnTrap: "onTrap"
			}
		}
	}
})
export class PolicyTemplateSnmpTrap {
	onTrap(self: SNMPSnmpDevice, event: any) {
		System.log("onTrap");
	}
}
