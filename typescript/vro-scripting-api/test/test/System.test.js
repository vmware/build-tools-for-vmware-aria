describe("vRO System API", () => {
    it("Global class definition is available", () => {
        expect(System).toBeDefined();
        expect(System).not.toBeNull();
    });

    describe("MathClass action", () => {
        const moduleName = "com.vmware.pscoe.test";
        it("Loading modules works", () => {
            const mod = System.getModule(moduleName);
            expect(mod).toBeDefined();
            expect(mod).not.toBeNull();
        });
        it("Action definitions from modules to be available", () => {
            const mod = System.getModule(moduleName);
            expect(mod.MathClass).toBeDefined();
            expect(mod.MathClass).not.toBeNull();
        });
        it("Using actions from modules works", () => {
            const mod = System.getModule(moduleName);
            expect(mod.MathClass()).toBeDefined();
            expect(mod.MathClass()).not.toBeNull();
        });
        it("Instantiating classes from actions from modules works", () => {
            const MathClass = System.getModule(moduleName).MathClass();
            const obj = new MathClass();
            expect(obj).toBeDefined();
            expect(obj).not.toBeNull();
        });
        it("Class insances from actions from modules to have method definitions works", () => {
            const MathClass = System.getModule(moduleName).MathClass();
            const obj = new MathClass();
            expect(obj.sum).toBeDefined();
            expect(obj.sum).not.toBeNull();
            expect(typeof obj.sum).toEqual("function");
        });
        it("Using methods of class insances from actions from modules works", () => {
            const MathClass = System.getModule(moduleName).MathClass();
            const result = new MathClass().sum(1, 2);
            expect(result).toBe(3);
        });
        it("Validate nextUUID() function result", () => {
            const uuid = System.nextUUID();
            expect(uuid).toMatch(/\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b/);
        });
        it('Validate if sleep() function is accurate', () => {
            const start = new Date().getTime();
            const delay = 1000;
            const delta = 500;
            System.sleep(delay);
            const finish = new Date().getTime();
            expect(finish).toBeGreaterThan(start + delay);
            expect(finish).toBeLessThan(start + delay + delta);
        });
        it('Validate if waitUntil() function is accurate', () => {
            const start = new Date().getTime();
            const delay = 1000;
            const delta = 500;
            System.waitUntil(new Date(start + delay));
            const finish = new Date().getTime();
            expect(finish).toBeGreaterThan(start + delay);
            expect(finish).toBeLessThan(start + delay + delta);
        });
    });
});
