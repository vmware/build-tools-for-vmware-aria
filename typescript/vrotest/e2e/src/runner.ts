import * as path from "path";
import * as fs from "fs-extra";
import * as childProc from "child_process";
import JSZip from "jszip";

const cliPath = path.resolve(__dirname, "..", "..", "lib", "vrotest.js");
const projectPath = path.resolve(__dirname, "..", "project");
const outPath = path.resolve(__dirname, "..", "out");
const outProjectPath = path.join(outPath, "project");
const outTestPath = path.join(outPath, "vro-test");

(async () => {
	await prepare();
	await runTests();
	await runTests({ testFrameworkPackage: "jasmine" });
	await runTests({ testFrameworkPackage: "jasmine", testFrameworkVersion: "5.4.0" });
	await runTests({ testFrameworkPackage: "jasmine", testFrameworkVersion: "latest" });
	await runTests({ testFrameworkPackage: "jest" });
	await runTests({ testFrameworkPackage: "jest", testFrameworkVersion: "29.7.0" });
	await runTests({ testFrameworkPackage: "jest", testFrameworkVersion: "latest" });
	await runTests({ testFrameworkPackage: "jest", testFrameworkVersion: "29.7.0" });
	await runTests({ testFrameworkPackage: "jest", testFrameworkVersion: "latest", runner: "swc" });
	await runTests({ testFrameworkPackage: "jest", runner: "swc" });
})();

async function prepare(): Promise<void> {
	await fs.remove(outPath);
	await fs.copy(projectPath, outProjectPath);

	const depsPath = path.join(outProjectPath, "dependencies");
	for (const depName of await fs.readdir(depsPath)) {
		await zipFolder(path.join(depsPath, depName), path.join(depsPath, `${depName}.package`));
		await fs.remove(path.join(depsPath, depName));
	}
}

async function zipFolder(dir: string, targetPath: string): Promise<void> {
	const zip = new JSZip();
	await zipFiles(zip, dir);
	const zipData = await zip.generateAsync({ type: "uint8array" });
	await fs.writeFile(targetPath, zipData);
	async function zipFiles(zip: JSZip, dir: string): Promise<void> {
		const entries = await fs.readdir(dir);
		const stats = await Promise.all(entries.map(p => fs.lstat(path.join(dir, p))));
		const fileNames = entries.filter((_, i) => stats[i].isFile());
		const childDirNames = entries.filter((_, i) => stats[i].isDirectory());
		const files = await Promise.all(fileNames.map(fileName => fs.readFile(path.join(dir, fileName))));
		fileNames.forEach((fileName, i) => zip.file(fileName, files[i]));
		await Promise.all(childDirNames.map(childDirName => zipFiles(zip.folder(childDirName), path.join(dir, childDirName))));
	}
}

async function runTests(options = {}) {
	await build(options);
	await run();
}

async function build({ testFrameworkPackage, testFrameworkVersion, runner, jasmineReportersVerion, ansiColorsVersion }: any): Promise<void> {
    // TODO: Add introduced properties for Jest support
    const params = [
		cliPath,
		"build",
		"--projectRoot",
		outProjectPath,
		"--actions",
		"src",
		"--tests",
		"test",
		"--resources",
		"resources",
		"--configurations",
		"configurations",
		"--dependencies",
		"dependencies",
		"--helpers",
        "helpers",
		"--output",
		outTestPath,
		"--coverage-reports",
		"text,lcov",
    ];
    if (testFrameworkPackage) {
        params.push("--testFrameworkPackage");
        params.push(testFrameworkPackage);
    }
    if (testFrameworkVersion) {
        params.push("--testFrameworkVersion");
        params.push(testFrameworkVersion);
    }
    if (runner) {
        params.push("--runner");
        params.push(runner);
    }
    if (jasmineReportersVerion) {
        params.push("--jasmineReportersVerion");
        params.push(jasmineReportersVerion);
    }
    if (ansiColorsVersion) {
        params.push("--ansiColorsVersion");
        params.push(ansiColorsVersion);
    }
	await executeScript(outProjectPath, params);
}

async function run(): Promise<void> {
	await executeScript(outTestPath, [
		cliPath,
		"run",
		"--instrument",
	]);
}

async function executeScript(cwd: string, args: string[]): Promise<void> {
	const proc = childProc.spawn("node", args, {
		cwd,
		env: process.env,
		shell: true,
		stdio: "inherit"
	});
	await new Promise<void>((resolve, _) => {
		proc.on("close", code => {
			if (code === 0) {
				resolve();
			}
			else {
				process.exit(code);
			}
		});
	});
}
