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
	await build();
	await run();
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

async function build(): Promise<void> {
	await executeScript(outProjectPath, [
		cliPath,
		"build",
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
	]);
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
		stdio: "inherit", 
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
