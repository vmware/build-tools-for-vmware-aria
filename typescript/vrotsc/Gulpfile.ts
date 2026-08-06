import * as childProcess from "child_process";
import * as path from "path";
import * as fs from "fs-extra";
import * as gulp from "gulp";
import * as ansiColors from "ansi-colors";
import { rollup } from "rollup";

const rollupResolve = require("@rollup/plugin-node-resolve");
const rollupCommonjs = require("@rollup/plugin-commonjs");
const ROLLUP_IGNORE = [
	"fs",
	"path",
	"os",
	"crypto",
	"buffer",
	"module",
	"inspector",
	"constants",
	"stream",
	"util",
	"assert",
	"events",
	"string_decoder",
	"@microsoft/typescript-etw",
];

function toPathArg(...args: string[]) {
    const res = args.length == 1 ? args[0] : path.join(...args).replace(/[\\/]+/, path.posix.sep);
    return !res ? '""' : (res.indexOf(" ") >= 0 && res.indexOf('"') < 0 ? `"${res}"` : res);
}


gulp.task("compile", done => {
	tsc();
	done();
});

gulp.task("test", done => {
	tsc("tests");
	let error = "";
    if (0 !== exec(process.argv[0], [toPathArg("dist", "src", "tests", "e2e.js")])) {
		error = "One or more test cases failed.";
	}
	done(error ? new Error(error): null);
});

gulp.task("clean", done => {
	fs.removeSync("build");
	fs.removeSync(path.join("lib", "vrotsc.js"));
	getLibFiles("lib").forEach(fileName => {
		fs.removeSync(path.join("lib", fileName));
	});
	done();
});

gulp.task("copyLibs", async done => {
	const tsLibPath = path.join("node_modules", "typescript", "lib");
	getLibFiles(tsLibPath).forEach(fileName => {
		fs.copyFileSync(path.join(tsLibPath, fileName), path.join("lib", fileName));
	});
	done();
});

gulp.task("bundle", async done => {
	const build = await rollup({
		input: "dist/src/index.js",
		onwarn: _ => { },
		plugins: [
			rollupResolve({
				preferBuiltins: true
			}),
			rollupCommonjs({
				ignore: ROLLUP_IGNORE,
				sourceMap: false,
			}),
			// This is responsible for minifying the output
			// rollupTerser({
			// 	sourcemap: false,
			// })
		]
	});

	await build.write({
		file: "lib/vrotsc.js",
		format: "cjs",
	});

	done();
});

const buildTasks = ["clean", "compile", "bundle", "copyLibs"];
if (!(process.env.SKIP_NPM_TESTS && process.env.SKIP_NPM_TESTS.toLowerCase() === 'true')) {
    buildTasks.push("test");
}
gulp.task("build", gulp.series(buildTasks));
// gulp.task("build", gulp.series("clean", "compile", "copyLibs", "test"));

function getLibFiles(path: string): string[] {
	if (fs.pathExistsSync(path)) {
		return fs.readdirSync(path, { withFileTypes: true })
			.filter(ent => !ent.isDirectory())
			.filter(ent => ent.name.startsWith("lib.") && ent.name.endsWith(".d.ts"))
			.map(ent => ent.name);
	}
	return [];
}

function tsc(projectName?: string): void {
	const tscCommand = path.join("node_modules", ".bin", "tsc");
	let tscArgs: string[] = [];
	if (projectName) {
        tscArgs = ["--build", toPathArg("src", projectName)];
	}
	exec(tscCommand, tscArgs, undefined, true);
}

function exec(command: string, args: string[] = [], cwd?: string, checkExitCode?: boolean): number | null {
    command = toPathArg(command);
	const commandLine = `${command} ${args.join(" ")}`;
	log(`Executing '${ansiColors.cyan(commandLine)}'...`);
	const result = childProcess.spawnSync(command, args, {
		shell: true,
		stdio: "inherit",
		cwd: cwd || __dirname,
	});
	if (checkExitCode && result.status !== 0) {
		throw new Error(`Command "${commandLine}" exited with code ${result.status}`);
	}
	return result.status;
}

function log(message: string): void {
	const format = (n: number) => ("0" + n).slice(-2);
	const now = new Date();
	const timeString = `${format(now.getHours())}:${format(now.getMinutes())}:${format(now.getSeconds())}`;
	console.log(`[${timeString}] ${message}`);
}
