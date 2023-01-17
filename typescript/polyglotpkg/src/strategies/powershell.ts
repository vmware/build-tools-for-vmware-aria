import fs from 'fs-extra';
import path from 'path';
import globby from 'globby';

import { Logger } from "winston";
import { BaseStrategy } from "./base";
import { getActionManifest, run } from "../lib/utils";
import { PackagerOptions, PlatformDefintion, Events } from "../lib/model";

export class PowershellStrategy extends BaseStrategy {

    constructor(logger: Logger, options: PackagerOptions, phaseCb: Function) { super(logger, options, phaseCb) }

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
            patterns.push('!.*', '*.ps1');
            const outDir = path.relative(workspaceFolderPath, this.options.out);
            patterns.push(`${outDir}/**`);
        }

        const filesToBundle = await globby(patterns, {
            cwd: workspaceFolderPath,
            absolute: true
        });

        this.logger.info(`Packaging ${filesToBundle.length} files into bundle ${this.options.bundle}...`);
        const actionBase = packageJson.platform.base ? path.resolve(packageJson.platform.base) : workspaceFolderPath;
        this.logger.info(`Action base: ${actionBase}`);
        await this.zipFiles([
            { files: filesToBundle, baseDir: actionBase }
        ]);
    }

    private async compile(source: string, destination: string) {
        this.logger.info(`Compiling project...`);
        await fs.copy(source, destination);
        this.logger.info(`Compilation complete`);
    }

    private async installDependencies() {

        const packageJson = await getActionManifest(this.options.workspace) as PlatformDefintion;
        const psScriptName: string = packageJson.platform.entrypoint.split("/")[1].split(".")[0];
        const depsManifest: string = path.join(this.options.workspace, `src/${psScriptName}.ps1`);
        const deps: string = fs.readFileSync(depsManifest, "utf-8");
        const modulesPath: string = path.join(this.options.out, "Modules");
        const modules: string[] = [];

        deps.split(/\r?\n/).forEach(line => {
            if (line.indexOf("Import-Module") !== -1 && line.indexOf("#@ignore") === -1 && !line.startsWith("#")) {
                const module: string = line.replace("Import-Module", "").trim();
                modules.push(module);
            }
        });

        this.logger.info(`Powershell modules included: ${modules}`);

        if (modules.length > 0) {
            this.logger.info(`Downloading and saving dependencies in "${modulesPath}..."`);
            await run("pwsh", ["-c", "Save-Module", "-Name", `"${modules.toString()}"`, "-Path", `"${modulesPath}"`, "-Repository PSGallery"]);
        } else {
            this.logger.info("No change in dependencies. Skipping installation...");
        }
    }

}
