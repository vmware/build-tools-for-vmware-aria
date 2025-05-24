import "jasmine";
import * as fs from "fs-extra";
import * as path from "path";
import * as parser from "../../dist/parse/flat.js";
import * as cleaner from "../../dist/cleaner/definitionCleaner.js";

describe("Unit Tests", () => {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 300000;

    const currentPath = process.cwd();
    const expand = (...args: string[]) => path.join(currentPath, ...args);

    it("Check comments inside data file ", async () => {
        try {
            const path = expand('test', "target-with-description");
            const result = await parser.parseFlat(path, currentPath, expand(".vroignore"));

            expect(result).toBeDefined();
            expect(result.description).toBeDefined();
            console.log(result.description);

            if (fs.existsSync(path + "/dunes-meta-inf")) {
                fs.remove(path + "/dunes-meta-inf");
            }
            if (fs.existsSync(path + "/elements")) {
                fs.remove(path + "/elements");
            }

        } catch (error) {
            throw error;
        }
    })

    it("Check empty vro action inside js files ", async () => {
        try {
            const path = expand('test');
            const dirCreationPath = `${path}/empty-js-definition/src/main/resources`;
            await fs.mkdirs(dirCreationPath);
            fs.copySync(`${path}/emptyDefinition.js`, `${dirCreationPath}/emptyDefinition.js`, { overwrite: true, dereference: true, errorOnExist: false })

            const result = new cleaner.CleanDefinition();
            result.removeEmptyDefinitions(`${path}/empty-js-definition`);

            await fs.rmdir(`${path}/empty-js-definition/src/main/resources`);
            await fs.rmdir(`${path}/empty-js-definition/src/main`);
            await fs.rmdir(`${path}/empty-js-definition/src`);
            await fs.rmdir(`${path}/empty-js-definition`);
        } catch (error) {
            throw error;
        }
    })
});
