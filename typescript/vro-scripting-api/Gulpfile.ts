import * as childProcess from "child_process";
import * as gulp from "gulp";
import * as path from "path";
import * as fs from "fs-extra";
import * as ansiColors from "ansi-colors";
import { rollup } from "rollup";
import { nodeResolve as rollupNodeResolve } from "@rollup/plugin-node-resolve";
import rollupCommonjs from "@rollup/plugin-commonjs";
import { terser as rollupTerser } from "rollup-plugin-terser";
import Jasmine from "jasmine";

const ROLLUP_IGNORE = [];

gulp.task("compile", async done => {
    await tsc("tsconfig.json");
    done()
});

gulp.task("test", async done => {
    tsc(path.join("e2e", "tsconfig.json"));
    let error = undefined;
    if (0 !== await exec(process.argv[0], [path.join("e2e", "build", "runner.js")])) {
        error = "One or more test cases failed.";
    }
    done(error);
});

gulp.task("clean", async done => {
    await fs.remove("build");
    await fs.remove(path.join("lib", "vro-runtime.js"));
    done();
});

gulp.task("bundle", async done => {
    const build = await rollup({
        input: "build/vro-runtime.js",
        onwarn: _ => { },
        plugins: [
            rollupNodeResolve({
                preferBuiltins: true,
                exportConditions: ["node"],
            }),
            rollupCommonjs({
                ignore: ROLLUP_IGNORE,
                ignoreDynamicRequires: true,
                sourceMap: false,
            }),
            rollupTerser()
        ]
    });

    await build.write({
        file: "lib/vro-runtime.js",
        format: "commonjs",
    });

    done();
});

gulp.task("test", async done => {
    process.chdir("test");
    const jasmine = new Jasmine();
    jasmine.clearReporters();
    jasmine.addReporter(new ConsoleReporter());
    jasmine.loadConfig({
        spec_files: [
            "test/*.test.js"
        ],
        helpers: [
            "../lib/vro-runtime.js"
        ],
        failSpecWithNoExpectations: false,
        stopSpecOnExpectationFailure: false,
        stopOnSpecFailure: false,
        random: false
    });
    jasmine.execute();
    done();
});

gulp.task("build", gulp.series(["clean", "compile", "bundle", "test"]));

async function tsc(projectName: string): Promise<void> {
    const tscCommand = path.join("node_modules", ".bin", "tsc");
    const tscArgs = ["--project", projectName];
    await exec(tscCommand, tscArgs, undefined, true);
}

async function exec(command: string, args: string[] = [], cwd?: string, checkExitCode?: boolean): Promise<number> {
    const commandLine = `${command} ${args.join(" ")}`;
    log(`Executing '${ansiColors.cyan(commandLine)}'...`);
    const proc = childProcess.spawn(`"${command}"`, args, {
        shell: true,
        stdio: "inherit",
        cwd: cwd || __dirname,
    });
    return await new Promise((resolve, reject) => {
        proc.on("close", code => {
            if (checkExitCode && code !== 0) {
                reject(`Command "${commandLine}" exited with code ${code}`);
            }
            else {
                resolve(code);
            }
        });
    });
}

function log(message: string): void {
    const format = (n: number) => ("0" + n).slice(-2);
    const now = new Date();
    const timeString = `${format(now.getHours())}:${format(now.getMinutes())}:${format(now.getSeconds())}`;
    console.log(`[${timeString}] ${message}`);
}

class ConsoleReporter implements jasmine.CustomReporter {
    private readonly indentationText = " ".repeat(2);
    private totalSpecs = 0;
    private failedSpecs = 0;
    private indent = 0;

    suiteStarted(result: jasmine.SuiteResult, done?: () => void): void | Promise<void> {
        this.log(result.description);
        this.indent++;
        done();
    }

    suiteDone?(_: jasmine.SuiteResult, done?: () => void): void | Promise<void> {
        this.indent--;
        done();
    }

    specStarted(_: jasmine.SpecResult, done?: () => void): void | Promise<void> {
        this.totalSpecs++;
        this.indent++;
        done();
    }

    specDone(result: jasmine.SpecResult, done?: () => void): void | Promise<void> {
        this.indent--;
        if (result.status === "passed") {
            this.log(ansiColors.green(result.description));
        }
        else {
            this.failedSpecs++;
            this.error(ansiColors.red(result.description));
            this.indent++;
            (result.failedExpectations || []).filter(e => !e.passed).forEach(e => {
                if (e.message) {
                    this.log(e.message);
                }
                if (e.stack) {
                    this.log(e.stack);
                }
            });
            this.indent--;
        }
        done();
    }

    jasmineDone(_: jasmine.JasmineDoneInfo, done?: () => void): void | Promise<void> {
        console.log(`${this.totalSpecs} specs, ${this.failedSpecs} failures`);
        done();
    }

    private log(s: string) {
        s = s.split("\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\n")
        console.log(s);
    }

    private error(s: string) {
        s = s.split("\n").map(line => `${this.indentationText.repeat(this.indent)}${line}`).join("\n")
        console.error(s);
    }
}
