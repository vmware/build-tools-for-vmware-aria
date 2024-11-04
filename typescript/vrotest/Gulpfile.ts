import rollupCommonjs from "@rollup/plugin-commonjs";
import { nodeResolve as rollupNodeResolve } from "@rollup/plugin-node-resolve";
import terser from "@rollup/plugin-terser";
import * as ansiColors from "ansi-colors";
import * as childProcess from "child_process";
import * as fs from "fs-extra";
import * as gulp from "gulp";
import * as path from "path";
import { rollup } from "rollup";

const ROLLUP_IGNORE = [
    "iconv-lite",
    "jasmine",
];

gulp.task("compile", async done => {
    await tsc("tsconfig.json");
    done()
});

gulp.task("test", async done => {
    await tsc(path.join("e2e", "tsconfig.json"));
    let error;
    if (0 !== await exec(process.argv[0], [path.join("e2e", "build", "runner.js")])) {
        error = "One or more test cases failed.";
    }
    done(error);
});

gulp.task("clean", async done => {
    await fs.remove("build");
    await fs.remove(path.join("lib", "vrotest.js"));
    done();
});

gulp.task("bundle", async done => {
    const build = await rollup({
        input: "build/vrotest.js",
        onwarn: _ => { },
        plugins: [
            rollupNodeResolve({
                preferBuiltins: true,
                exportConditions: ["node"],
            }),
            rollupCommonjs({
                ignore: ROLLUP_IGNORE,
                sourceMap: false,
            }),
            terser()
        ]
    });

    await build.write({
        file: "lib/vrotest.js",
        format: "cjs",
    });

    done();
});

gulp.task("build", gulp.series("clean", "compile", "bundle", "test"));

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
