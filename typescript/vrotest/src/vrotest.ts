import * as path from "path";
import * as fs from "fs-extra";
import minimist from "minimist";
import build from "./build";
import run from "./run";

interface RootCommandFlags {
    help?: boolean;
    version?: boolean;
    _: string[];
}

type Command<TFlags> = (flags: TFlags) => Promise<void>;

const commands: Record<string, Command<unknown>> = { build, run };

(async () => {
    const flags = parseCommandLine();
    if (flags.version) {
        printVersion();
        return;
    }

    if (flags.help) {
        printVersion();
        printUsage(flags._[0]);
        return;
    }

    const cmdName = flags._[0] || "";
    if (!cmdName) {
        printUsage();
        process.exit(1);
    }

    const cmd = commands[cmdName];
    if (!cmd) {
        printUsage();
        process.exit(1);
    }

    await cmd(flags);
})();

function parseCommandLine(): RootCommandFlags {
    const cmdOptions = {
        boolean: ["help", "version", "instrument"],
        string: [
            "output", "actions", "testHelpers", "tests", "maps", "resources",
            "configurations", "dependencies", "helpers", "output",
            "ts-src", "ts-namespace", "coverage-thresholds", "coverage-reports", "per-file"
        ],
        alias: {
            "h": "help",
            "v": "version",
        }
    } as minimist.Opts;
    return minimist(process.argv.slice(2), cmdOptions) as RootCommandFlags;
}

function printVersion(): void {
    const packageJsonPath = path.join(__dirname, "..", "package.json");
    if (fs.existsSync(packageJsonPath)) {
        const packageConfig = JSON.parse(fs.readFileSync(packageJsonPath).toString());
        console.log(`Version ${packageConfig.version}`);
    }
}

function printUsage(command?: string): void {
    command = command && commands[command] ? command : "root";
    const usageFilePath = path.join(__dirname, "..", "usage", `${command}.txt`);
    if (fs.existsSync(usageFilePath)) {
        console.log(fs.readFileSync(usageFilePath).toString());
    }
}
