describe("sample", function() {
    var sample = System.getModule("${groupId}.${artifactId}.integration-service-1").sample;
    it("should add two numbers", function() {
        expect(sample(5, 2)).toBe(7);
    });
});
