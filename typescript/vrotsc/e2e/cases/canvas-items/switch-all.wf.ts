import { Workflow, SwitchItem } from "vrotsc-annotations";

@Workflow({
	name: "switch all",
	path: "VMware/PSCoE",
	description: "Comprehensive switch test with all comparator types",
	attributes: {
		var_str: {
			type: "string"
		},
		var_num: {
			type: "number"
		},
		var_bool: {
			type: "boolean"
		},
	}
})
export class SwitchAll {

	@SwitchItem({
		cases: [
			// String comparisons
			{ condition: "", target: "handleEmpty", variable: "var_str", type: "string", comparator: "equals" },
			{ condition: "test", target: "handleStringOp", variable: "var_str", type: "string", comparator: "contains" },
			{ condition: "pattern", target: "handleStringOp", variable: "var_str", type: "string", comparator: "match" },
			{ condition: "value", target: "handleStringOp", variable: "var_str", type: "string", comparator: "different" },
			
			// Number comparisons
			{ condition: 1, target: "handleNumeric", variable: "var_num", type: "number", comparator: "different" },
			{ condition: 5, target: "handleNumeric", variable: "var_num", type: "number", comparator: "greater" },
			{ condition: 10, target: "handleNumeric", variable: "var_num", type: "number", comparator: "smaller" },
			{ condition: 0, target: "handleNumeric", variable: "var_num", type: "number", comparator: "greater or equals" },
			{ condition: 100, target: "handleNumeric", variable: "var_num", type: "number", comparator: "smaller or equals" },
			
			// Boolean comparisons
			{ condition: true, target: "handleBoolean", variable: "var_bool", type: "boolean", comparator: "equals" },
			{ condition: false, target: "handleBoolean", variable: "var_bool", type: "boolean", comparator: "different" }
		],
		target: "handleDefault"
	})
	public switchElement() {
		// Switch logic will be generated automatically
	}

	public handleEmpty() {
		System.log("Handling empty string");
	}

	public handleStringOp() {
		System.log("Handling string operation");
	}

	public handleNumeric() {
		System.log("Handling numeric operation");
	}

	public handleBoolean() {
		System.log("Handling boolean operation");
	}

	public handleDefault() {
		System.log("Handling default case");
	}
}