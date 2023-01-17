import { Simple } from "./simple"

describe("echo", function () {
    it("should concat inputs ", function () {
        let simple = new Simple("echo")
        expect(simple.print()).toBe("echo")
    });
    xit("should do nothing as it is x-ed", function () {
        expect(true).toBe(false)
    });
});