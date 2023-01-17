import fs from 'fs-extra';
import path from 'path';
import globby from 'globby';
import Zip from 'adm-zip';

import { Logger } from "winston";
import { BaseStrategy } from "./base";
import { getActionManifest, run } from "../lib/utils";
import { PackagerOptions, PlatformDefintion, Events, BundleFileset } from "../lib/model";

export class PythonStrategy extends BaseStrategy {

    constructor(logger: Logger, options: PackagerOptions, phaseCb: Function) { super(logger, options, phaseCb) }

    /**
     * Create a zip bundle with source files (from src directory) and dependencies (generated based on the
     * requirements.txt file in the project). Source files will keep their directory structure. Say there is a file
     * [projectRootDir]/src/handler.py, then in the bundle it would go straight under the root folder of the bundle.
     * If we have a source file like [projectRootDir]/src/dir/file.py, then it would go in the bundle under the
     * directory named "dir".
     * When it comes to the dependencies, they are described in file [projectRootDir]/requirements.txt. That is a text
     * file where each line contains information about one dependency and optionally its version. Example content:
     * ```
     *  jinja2==3.1.2
     *  pyyaml
     * ```
     * Resolution of all these dependencies is done by spawning the command
     * ```
     *  pip3 install -r [projectRootDir]/requirements.txt
     * ```
     * Then all of the files of the dependencies will be bundled below the "lib" subfolder in the zip bundle.
     * @param filesets     - Contains the list of all of the source files.
     * @param dependencies - Contains the list of all files from the dependencies. This is a separate parameter, as
     *              the dependencies are handled slightly differently than normal source files. Dependencies go under
     *              the "lib" subfolder of the bundle, rather than the root folder of the bundle where the source files
     *              go. This is an optional parameter and can be omitted or set to null empty array.
     */
    protected async zipFilesAndDependencies(filesets: Array<BundleFileset>, dependencies?: Array<BundleFileset>) {
        const zip = new Zip();

        filesets.forEach(fileset => {
            const { files: filesToBundle, baseDir } = fileset;
            for (var i = 0; i < filesToBundle.length; i++) {
                const filePath = filesToBundle[i];
                const zipPath = path.dirname(path.relative(baseDir, filePath));
                zip.addLocalFile(filePath, zipPath);
                this.logger.debug(`Packaged: ${filePath}`);
            }
        });
        if (dependencies != null && dependencies.length > 0) {
            dependencies.forEach(dependency => {
                const { files: dependencyFiles, baseDir } = dependency;
                for (var i = 0; i < dependencyFiles.length; i++) {
                    const filePath = dependencyFiles[i];
                    let zipPath : string = path.dirname(path.relative(baseDir, filePath));
                    zipPath = "lib/" + zipPath;
                    zip.addLocalFile(filePath, zipPath);
                    this.logger.debug(`Packaged: ${filePath}`);
                }
            });
        }

        await fs.ensureDir(path.dirname(this.options.bundle));
        zip.writeZip(this.options.bundle);
        this.logger.info(`Created ${this.options.bundle}`);
    }

    /**
     * package project into bundle
     */
    async packageProject() {
        const packageJson = await getActionManifest(this.options.workspace) as PlatformDefintion;
        this.phaseCb(Events.COMPILE_START);
        await this.compile(path.join(this.options.workspace, 'src'), this.options.out);
        this.phaseCb(Events.COMPILE_END);
        this.phaseCb(Events.DEPENDENCIES_START);
        await this.installDependencies();
        this.phaseCb(Events.DEPENDENCIES_END);
        this.phaseCb(Events.BUNDLE_START);
        await this.createBundle(this.options.workspace, packageJson);
        this.phaseCb(Events.BUNDLE_END);
    }

    private async createBundle(workspaceFolderPath: string, packageJson: PlatformDefintion): Promise<void> {
        const patterns = ['package.json'];

        if (Array.isArray(packageJson.files) && packageJson.files.length > 0) {
            patterns.push(...packageJson.files);
        } else {
            patterns.push('!.*', '*.py');
            const outDir = path.relative(workspaceFolderPath, this.options.out);
            patterns.push(`${outDir}/**`);
        }

        const filesToBundle = await globby(patterns, {
            cwd: workspaceFolderPath,
            absolute: true
        });

        const depsToBundle = await globby(`**/*`, {
            cwd: this.DEPENDENCY_TEMP_DIR,
            absolute: true,
        });

        this.logger.info(`Packaging ${filesToBundle.length + depsToBundle.length} files into bundle ${this.options.bundle}...`);
        const actionBase = packageJson.platform.base ? path.resolve(path.join(workspaceFolderPath, packageJson.platform.base)) : workspaceFolderPath;
        this.logger.info(`Action base: ${actionBase}`);
        await this.zipFilesAndDependencies(
            [{ files: filesToBundle, baseDir: actionBase }],
            [{ files: depsToBundle, baseDir: this.DEPENDENCY_TEMP_DIR }]
        );
    }

    private async compile(source: string, destination: string) {
        this.logger.info(`Compiling project...`);
        await fs.copy(source, destination);
        this.logger.info(`Compilation complete`);
    }

    private async installDependencies() {
        const depsManifest = path.join(this.options.workspace, 'requirements.txt');
        const deps = await fs.readFile(depsManifest);
        const hash = this.getHash(deps.toString());
        const existingHash = await this.readDepsHash(this.DEPENDENCY_TEMP_DIR);
        if (existingHash !== hash) {
            this.logger.info("Installing dependencies...");
            await run("pip3", ["install", "-r", `"${depsManifest}"`, "--target", `"${this.DEPENDENCY_TEMP_DIR}"`, "--upgrade"]);
            await this.writeDepsHash(deps.toString());
        } else {
            this.logger.info("No change in dependencies. Skipping installation...");
        }
    }

}
