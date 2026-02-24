import rollupCommonjs from "@rollup/plugin-commonjs";
import { nodeResolve as rollupNodeResolve } from "@rollup/plugin-node-resolve";
import terser from "@rollup/plugin-terser";
import * as ansiColors from "ansi-colors";
import * as childProcess from "child_process";
import * as gulp from "gulp";
import * as path from "path";
import { rollup } from "rollup";
import { rmSync } from "fs";

function toPathArg(...args: string[]) {
    const res = args.length == 1 ? args[0] : path.join(...args).replace(/[\\/]+/, path.posix.sep);
    return !res ? '""' : (res.indexOf(" ") >= 0 && res.indexOf('"') < 0 ? `"${res}"` : res);
}

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
    let error: any = undefined;
    if (0 !== await exec(process.argv[0], [toPathArg("e2e", "build", "runner.js")])) {
        error = "One or more test cases failed.";
    }
    done(error);
});

gulp.task("clean", async done => {
    rmSync("build", { recursive: true, force: true });
    rmSync(path.join("lib", "vrotest.js"), { force: true });
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
    const tscArgs = ["--project", toPathArg(projectName)];
    await exec(tscCommand, tscArgs, undefined, true);
}

async function exec(command: string, args: string[] = [], cwd?: string, checkExitCode?: boolean): Promise<number> {
    command = toPathArg(command);
    const commandLine = `${command} ${args.join(" ")}`;
    log(`Executing '${ansiColors.cyan(commandLine)}'...`);
    const proc = childProcess.spawn(command, args, {
        shell: true,
        stdio: "inherit",
        cwd: cwd || __dirname,
    });
    return await new Promise((resolve, reject) => {
        proc.on("close", (code: number) => {
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
