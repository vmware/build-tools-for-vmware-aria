import "jasmine";
import * as fs from "fs-extra";
import * as path from "path";
import * as glob from "glob";
import * as child_process from "child_process";
import * as unzipper from "unzipper";

describe("End-to-End Tests", () => {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 300000;

    let childProcLogs = process.env.CHILD_PROC_LOGS == "true" || process.env.CHILD_PROC_LOGS == "1";
    const currentPath = process.cwd();
    const runCase = (caseName, caseArguments) => {
        if (childProcLogs) {
            console.info(
                "Executing case args",
                caseArguments.map(arg => `'${arg}'`).join(" ")
            );
        }

        const childProcess = child_process.spawn("node", caseArguments, {
            cwd: currentPath,
            env: process.env
        });

        childProcess.stdout.on("data", function (data) {
            if (childProcLogs) {
                console.log(`${caseName} stdout: ${data}`);
            }
        });

        return new Promise<void>((resolve, reject) => {
            childProcess.stderr.on("data", function (data) {
                const output = data.toString('utf-8');
                if (childProcLogs) {
                    console.log(`${caseName} stderr: ${output}`);
                }
                // compensate for unhandled promise rejections
                if (output.indexOf("UnhandledPromiseRejectionWarning") !== -1) {
                    reject(output);
                }
            });
            childProcess.on("close", function (code) {
                if (childProcLogs) {
                    console.log(`${caseName} exit code: ${code}`);
                }
                if (code !== 0) {
                    reject(code);
                } else {
                    resolve();
                }
            });
        });
    }

    const compare = (sourcePath: string, destinationPath: string, globExpr: string[]) => {
        const source = ['test', sourcePath];
        const destination = ['test', destinationPath];

        const sourceFilesArray = glob
			.sync(expand(...source, ...globExpr)?.replace(/[\\/]+/gm, path.posix.sep))
            .filter(file => !file.includes('META-INF') && !file.includes('version-history'))
            .map(file => path.normalize(file).replace(expand(...source), ''))
            .sort();
        const destinationFilesArray = glob
			.sync(expand(...destination, ...globExpr)?.replace(/[\\/]+/gm, path.posix.sep))
            .filter(file => !file.includes('META-INF') && !file.includes('version-history'))
            .map(file => path.normalize(file).replace(expand(...destination), ''))
            .sort();

        const sourceFilesSet = new Set(sourceFilesArray.map(file => file.toLowerCase()));
        const destinationFilesSet = new Set(destinationFilesArray.map(file => file.toLowerCase()));

        const srcMissing = sourceFilesArray.filter(srcFile => !destinationFilesSet.has(srcFile.toLowerCase()));
        const destMissing = destinationFilesArray.filter(destfile => !sourceFilesSet.has(destfile.toLowerCase()));

        expect(srcMissing)
            .withContext(`Source path ${sourcePath} have files missing in destination path ${destinationPath}`)
            .toEqual([]);
        expect(destMissing)
            .withContext(`Destination path ${destinationPath} have files missing in source path ${sourcePath}`)
            .toEqual([]);

        // Compare files by content
        for (let index = 0; index < sourceFilesArray.length; index++) {
            const sourceFilePath = path.join(process.cwd(), "test", sourcePath, sourceFilesArray[index]);
            const destFilePath = path.join(process.cwd(), "test", destinationPath, destinationFilesArray[index]);

            if (!fs.statSync(sourceFilePath).isFile() || !fs.statSync(destFilePath).isFile()) {
                continue;
            }
            if (sourceFilesArray[index].indexOf("content-signature") >= 0 || sourceFilesArray[index].indexOf("data") >= 0) {
                continue;
            }
            const sourceFile = readFile(sourceFilePath);
            const destFile = readFile(destFilePath);
            expect(sourceFile)
                .withContext(`Expected file ${sourceFilePath} to match ${destFilePath} `)
                .toEqual(destFile);
        }
    }
    const expand = (...args: string[]) => path.join(currentPath, ...args);

    function toPathArg(...args: string[]) {
        const res = args.length == 1 ? args[0] : path.join(...args).replace(/[\\/]+/, path.posix.sep);
        return !res ? '""' : (res.indexOf(" ") >= 0 && res.indexOf('"') < 0 ? `"${res}"` : res);
    }

    function readFile(path: string): string {
        return fs.readFileSync(path).toString().replace(/\r\n/g, "\n");
    }

    it("Convert XML project from tree to flat structure", async () => {
        try {
            await runCase("Project tree -> flat", [
                toPathArg(path.resolve("bin", "vropkg")),
                '--in', 'tree',
                '--out', 'flat',
                '--srcPath', toPathArg('test', 'com.vmware.pscoe.toolchain-expand'),
                '--destPath', toPathArg('test', 'target-flat'),
                '--privateKeyPEM', toPathArg('test', 'private_key.pem'),
                '--certificatesPEM', toPathArg('test', 'cert.pem'),
                '--version', '1.0.0',
                '--packaging', 'package',
                '--artifactId', 'proj-artifact',
                '--description', '',
                '--groupId', 'test.group',
                '--keyPass', "VMware1!",
                '--vroIgnoreFile', toPathArg('.vroignore')
            ]);
        } catch (error) {
            throw error;
        }

        const fileToExtract = await unzipper.Open.file(expand('test', 'com.vmware.pscoe.toolchain.package'));
        await fileToExtract.extract({ path: expand('test', 'target-flat.tmp') });

        compare('target-flat.tmp', 'target-flat', ['elements', '**']);
        deleteDirectoryRecursive(expand('test', 'target-flat.tmp'));
        deleteDirectoryRecursive(expand('test', 'target-flat'));
    })

    it("Convert XML project from flat to tree structure", async () => {
        try {
            await runCase("Project flat -> tree", [
                toPathArg(path.resolve("bin", "vropkg")),
                '--in', 'flat',
                '--out', 'tree',
                '--srcPath', toPathArg('test', 'com.vmware.pscoe.toolchain.package'),
                '--destPath', toPathArg('test', 'target-tree'),
                '--privateKeyPEM', toPathArg('test', 'private_key.pem'),
                '--certificatesPEM', toPathArg('test', 'cert.pem'),
                '--version', '1.0.0',
                '--packaging', 'package',
                '--artifactId', 'proj-artifact',
                '--description', '',
                '--groupId', 'test.group',
                '--keyPass', "VMware1!",
                '--vroIgnoreFile', toPathArg('.vroignore')
            ]);
        } catch (error) {
            throw error;
        }

        compare('target-tree', 'com.vmware.pscoe.toolchain-expand', ['src', 'main', 'resources', '**']);
        deleteDirectoryRecursive(expand('test', 'target-tree'));
    })

    it("Convert XML project with custom forms from tree to flat structure", async () => {
        try {
            await runCase("Project tree -> flat", [
                toPathArg(path.resolve("bin", "vropkg")),
                '--in', 'tree',
                '--out', 'flat',
                '--srcPath', toPathArg('test', 'com.vmware.pscoe.toolchain-expand'),
                '--destPath', toPathArg('test', 'target-flat-custom-forms'),
                '--privateKeyPEM', toPathArg('test', 'private_key.pem'),
                '--certificatesPEM', toPathArg('test', 'cert.pem'),
                '--version', '1.0.0',
                '--packaging', 'package',
                '--artifactId', 'proj-artifact',
                '--description', '',
                '--groupId', 'test.group',
                '--keyPass', "VMware1!",
                '--vroIgnoreFile', toPathArg('.vroignore')
            ]);
        } catch (error) {
            throw error;
        }

        const fileToExtract = await unzipper.Open.file(expand('test', 'com.vmware.pscoe.toolchain.package'));
        await fileToExtract.extract({ path: expand('test', 'target-flat-custom-forms.tmp') });

        compare('target-flat-custom-forms.tmp', 'target-flat-custom-forms', ['elements', 'input_form*']);
        deleteDirectoryRecursive(expand('test', 'target-flat-custom-forms.tmp'));
        deleteDirectoryRecursive(expand('test', 'target-flat-custom-forms'));
    })

    it("Convert XML project with custom forms from flat to tree structure", async () => {
        try {
            await runCase("Project flat -> tree", [
                toPathArg(path.resolve("bin", "vropkg")),
                '--in', 'flat',
                '--out', 'tree',
                '--srcPath', toPathArg('test', 'com.vmware.pscoe.vrbt.custom.interaction.package'),
                '--destPath', toPathArg('test', 'target-tree-custom-forms'),
                '--privateKeyPEM', toPathArg('test', 'private_key.pem'),
                '--certificatesPEM', toPathArg('test', 'cert.pem'),
                '--version', '1.0.0',
                '--packaging', 'package',
                '--artifactId', 'proj-artifact',
                '--description', '',
                '--groupId', 'test.group',
                '--keyPass', "VMware1!",
                '--vroIgnoreFile', toPathArg('.vroignore')
            ]);
        } catch (error) {
            throw error;
        }

        compare('target-tree', 'target-tree-custom-forms', ['src', 'main', 'resources', 'input_form_*.json']);
        deleteDirectoryRecursive(expand('test', 'target-tree'));
        deleteDirectoryRecursive(expand('test', 'target-tree-custom-forms'));
    })

    function deleteDirectoryRecursive(directoryPath: string): void {
        if (fs.existsSync(directoryPath)) {
            fs.readdirSync(directoryPath).forEach(file => {
                const curPath = path.join(directoryPath, file);
                if (fs.lstatSync(curPath).isDirectory()) {
                    // recurse
                    deleteDirectoryRecursive(curPath);
                } else {
                    // delete file
                    fs.unlinkSync(curPath);
                }
            });
            fs.rmdirSync(directoryPath);
        }
    };
});
