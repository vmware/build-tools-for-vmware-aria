import { Workflow, SwitchItem } from "vrotsc-annotations";

@Workflow({
	name: "Switch Edge Cases",
	path: "VMware/PSCoE",
	description: "Switch test covering edge cases - no default target, single case, and complex conditions",
	attributes: {
		errorCode: {
			type: "number"
		},
	}
})
export class SwitchEdgeCases {

	@SwitchItem({
		cases: [
			{ condition: 404, target: "handleNotFound", variable: "errorCode", type: "number", comparator: "0" },
			{ condition: 500, target: "handleServerError", variable: "errorCode", type: "number", comparator: "0" },
			{ condition: 403, target: "handleForbidden", variable: "errorCode", type: "number", comparator: "0" }
		],
		// No default target - will fall through to next item
	})
	public switchErrorCodes(errorCode: number) {
		// Error code switch without default
		System.log("Processing error code: " + errorCode);
	}

	public handleNotFound() {
		System.log("404 - Resource not found");
	}

	public handleServerError() {
		System.log("500 - Internal server error");
	}

	public handleForbidden() {
		System.log("403 - Access forbidden");
	}

	public fallbackHandler() {
		System.log("Unhandled error code - using fallback");
	}
}