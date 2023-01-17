namespace vroapi {
    const fs: typeof import("fs-extra") = require("fs-extra");

    export function parseJsonFile<T>(filePath: string): T {
        const content = fs.readFileSync(filePath).toString("utf-8");
        return JSON.parse(content);
    }
}
