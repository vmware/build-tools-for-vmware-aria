import ts from "typescript";
import fs from 'fs-extra';
import path from 'path';
import globby from 'globby';

import { Logger } from "winston";
import { BaseStrategy } from "./base";
import { notUndefined, getActionManifest, run } from "../lib/utils";
import { PackagerOptions, PlatformDefintion, Events } from "../lib/model";

export class NodejsStrategy extends BaseStrategy {

    constructor(logger: Logger, options: PackagerOptions, phaseCb: Function) { super(logger, options, phaseCb) }

    /**
     * package project into bundle
     */
    async packageProject() {

        const tsconfigPath = path.join(this.options.workspace, "tsconfig.json");
        if (!fs.existsSync(tsconfigPath)) {
            throw new Error('Could not find tsconfig.json in the project root');
        }

        this.phaseCb(Events.COMPILE_START);
        const tsconfig = this.readConfigFile(tsconfigPath);
        this.compile(tsconfigPath, tsconfig);
        this.phaseCb(Events.COMPILE_END);
        this.phaseCb(Events.DEPENDENCIES_START);
        const packageJson = (await getActionManifest(this.options.workspace)) as PlatformDefintion;
        await this.installDependencies(packageJson, this.DEPENDENCY_TEMP_DIR);
        this.phaseCb(Events.DEPENDENCIES_END);
        this.phaseCb(Events.BUNDLE_START);
        await this.createBundle(this.options.workspace, tsconfig);
        this.phaseCb(Events.BUNDLE_END);
    }

    private async createBundle(workspaceFolderPath: string, tsconfig: ts.ParsedCommandLine): Promise<void> {
        const baseDir = tsconfig.options.baseUrl || workspaceFolderPath;
        const packageJson = await getActionManifest(workspaceFolderPath) as PlatformDefintion;
        const patterns = ['package.json'];

        if (Array.isArray(packageJson.files) && packageJson.files.length > 0) {
            patterns.push(...packageJson.files);
        } else {
            patterns.push('!.*', '*.js');

            if (tsconfig.options.outDir) {
                const outDir = path.relative(baseDir, tsconfig.options.outDir);
                patterns.push(`${outDir}/**`);
            }

            if (tsconfig.options.rootDir) {
                const rootDir = path.relative(baseDir, tsconfig.options.rootDir);
                patterns.push(`${rootDir}/**`);
            }
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
        const actionBase = packageJson.platform.base ? path.resolve(packageJson.platform.base) : baseDir;
        this.logger.info(`Action base: ${actionBase}`);
        await this.zipFiles([
            { files: filesToBundle, baseDir: actionBase },
            { files: depsToBundle, baseDir: this.DEPENDENCY_TEMP_DIR }
        ]);
    }


    private compile(tsconfigPath: string, config?: ts.ParsedCommandLine): void {
        this.logger.info(`Compiling project ${tsconfigPath}...`);

        if (!config) {
            config = this.readConfigFile(tsconfigPath);
        }

        const diagnostics: ts.Diagnostic[] = [];
        const buildHost = ts.createSolutionBuilderHost(
            ts.sys,
            undefined,
            (diagnostic: ts.Diagnostic) => {
                this.reportDiagnostics([diagnostic])
                diagnostics.push(diagnostic)
            },
            (diagnostic: ts.Diagnostic) => {
                this.reportDiagnostics([diagnostic])
                diagnostics.push(diagnostic)
            }
        );
        const builder = ts.createSolutionBuilder(buildHost, [tsconfigPath], {
            verbose: true,
        });
        const exitStatus = builder.build();

        this.logger.info(`Exit status: ${exitStatus}`);
        this.logger.info(`Compilation complete`);
        if (diagnostics.some(val => val.category === ts.DiagnosticCategory.Error)) {
            throw new Error('Found compilation errors');
        }
    }

    private readConfigFile(tsconfigPath: string): ts.ParsedCommandLine {
        const configFileText = fs.readFileSync(tsconfigPath).toString();
        const result = ts.parseConfigFileTextToJson(tsconfigPath, configFileText);
        const configObject = result.config;
        if (!configObject) {
            this.reportDiagnostics([result.error]);
            throw new Error(`Could not parse ${tsconfigPath}`);
        }

        const configParseResult = ts.parseJsonConfigFileContent(configObject, ts.sys, path.dirname(tsconfigPath));
        const numberOfErrors = configParseResult.errors.length;
        if (numberOfErrors > 0) {
            this.reportDiagnostics(configParseResult.errors);
            throw new Error(`Found ${numberOfErrors} errors in ${tsconfigPath}`);
        }
        this.logger.info(`TS compiler options: ${JSON.stringify(configParseResult.options, null, 2)}`);
        return configParseResult;
    }

    private reportDiagnostics(optionalDiagnostics: (ts.Diagnostic | undefined)[]): void {
        const diagnostics = optionalDiagnostics.filter(notUndefined);

        if (diagnostics.length <= 0) {
            return;
        }

        diagnostics.forEach((diagnostic) => {
            let message = `   ${ts.DiagnosticCategory[diagnostic.category]} ts(${diagnostic.code})`;
            if (diagnostic.file && diagnostic.start) {
                let { line, character } = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
                message += ` ${diagnostic.file.fileName} (${line + 1},${character + 1})`;
            }
            message += `: ${ts.flattenDiagnosticMessageText(diagnostic.messageText, "\n")}`;
            this.logger.info(message);
        });
    }

    private async installDependencies(packageJson: PlatformDefintion, depsCacheDir: string) {
        await fs.ensureDir(depsCacheDir);

        for (const [key, value] of Object.entries(packageJson.dependencies as {[key:string]:string})) {
            if (value.startsWith("file:")) {
                const localLibPath = path.join(path.resolve(this.options.workspace), value.replace("file:", ""));
                const localLibPackageJson = (await getActionManifest(localLibPath)) as PlatformDefintion;
                const localLibDistPath = path.join(localLibPath, "polyglot-cache")
                await this.installDependencies(localLibPackageJson, localLibDistPath)
                packageJson.dependencies[key] = path.relative(depsCacheDir, localLibDistPath);
                const filesToBundle = await globby(localLibPackageJson.files || ["out"], {
                    cwd: localLibPath,
                    absolute: true,
                });
                await this.copyFiles(
                    [{
                        files: filesToBundle,
                        baseDir: localLibPath,
                    }],
                    localLibDistPath
                );
            }
        };

        const deps = JSON.stringify(packageJson.dependencies);
        const hash = this.getHash(deps);
        const existingHash = await this.readDepsHash(depsCacheDir);
        if (existingHash !== hash) {
            this.logger.info(`Installing dependencies at ${depsCacheDir}`);
            await fs.writeJSON(path.join(depsCacheDir, 'package.json'), packageJson);
            await run("npm", ["install", "--production"], depsCacheDir);
            await this.writeDepsHash(deps);
        } else {
            this.logger.info("No change in dependencies. Skipping installation...");
        }
    }

}
