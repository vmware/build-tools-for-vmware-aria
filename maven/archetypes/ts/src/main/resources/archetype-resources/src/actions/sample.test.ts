import { SampleClass } from "./sample"

describe("Tests", () => {
    it("should sum two numbers", () => {
        expect(new SampleClass().sum(1, 2)).toBe(3)
    })
})
