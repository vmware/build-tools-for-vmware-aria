import { IntegrationService1 } from "./IntegrationService1"

describe("Tests", () => {
	it("should sum two numbers", () => {
		expect(new IntegrationService1().sum(1, 2)).toBe(3)
	})
})
