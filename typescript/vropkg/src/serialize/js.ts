/*-
 * #%L
 * vropkg
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import * as Path from "path";
import * as FileSystem from "fs-extra";
import * as XmlBuilder from "xmlbuilder2";
import * as packaging from "../packaging";
import { VroPackageMetadata, VroNativeElement, VroActionData, VroActionParameter, Lang, VroScriptBundle } from "../types";
import { exist, isDirectory } from "../util";
import VroIgnore from "../util/VroIgnore"
import { SAVE_OPTIONS, XML_OPTIONS } from "../constants";

export class VroJsProjRealizer {

    public static realizeElement(element: VroNativeElement, nativeFolder: string, ignorePatterns: string[]) {
        let categoryPath: Array<string> = element.categoryPath;
        let type: string = element.type || "UnknownType";
        let id: string = element.id;
        let name: string = element.name;
        let description: string = element.description == null ? "" : element.description;
        let tags: Array<string> = element.tags;
        let action: VroActionData = element?.action;

        name = name == null ? "" : ("" + name).replace(/\//g, "-");

        if (type == "ScriptModule") {
            let dirs: Array<string> = ["src", "main", "resources"];
            for (let i = 0; i < categoryPath.length; i++) {
                let categories = categoryPath[i].split(".");
                dirs = dirs.concat(categories);
            }
            dirs = dirs.filter(x => x != null).map(x => x.replace(/\//g, "-"));
            dirs.unshift(nativeFolder);
            let basePath = Path.join.apply(null, dirs);
			const resolvedFileName = Path.join(basePath, name + ".js").replace(/[\\]+/gm,"/");
			if (!VroIgnore.filePathMatchesGlob(resolvedFileName, ignorePatterns)) {
				VroJsProjRealizer.writeJsFile(nativeFolder, basePath, name + ".js", id, description, tags, action);
				if (action.bundle != null) {
					VroJsProjRealizer.writeBundleFile(nativeFolder, basePath, action.bundle);
				}
			}
        } else {
            let dirs: Array<string> = ["src", "main", "resources", type].concat(categoryPath);
            dirs = dirs.filter(x => x != null).map(x => x.replace(/\//g, "-"));
            dirs.unshift(nativeFolder);
            let basePath = Path.join.apply(null, dirs);
			const resolvedFileName = Path.join(basePath, name).replace(/[\\]+/gm,"/");
			if (!VroIgnore.filePathMatchesGlob(resolvedFileName, ignorePatterns)) {
            	VroJsProjRealizer.writeOtherResource(basePath, name, element);
			}
        }
    }

    private static writeJsFile(nativeFolder: string, dir: string, filename: string, id: string, description: string, tags: Array<string>, action: VroActionData): void {
        let file = Path.join(dir, filename);
        let params: Array<VroActionParameter> = action.params;
        let returnType: VroActionParameter = action.returnType;
        let actionSource: string = action.inline?.actionSource;
        let sourceFile: string = action.inline?.sourceFile;
        let getActionSource: (action: VroActionData) => string = action.inline?.getActionSource;
        // Backward compatibility: removed @id and @version
        // let version         : string = action.version;

        if (actionSource == null && getActionSource != null) {
            try {
                actionSource = getActionSource(action);
            } catch (e) {
                throw new Error(`ERROR Cannot read action source for "${file}" from source file "${sourceFile}" : ` + JSON.stringify(e));
            }
        }

        let doc: Array<string> = [];
        doc.push("/**");
        if (description.trim() != "") {
            doc = doc.concat(description.split("\n").map(line => " * " + line));
        }
        if (action?.runtime?.lang != null && action?.runtime?.lang != Lang.javascript) {
            doc = doc.concat(" * @runtime " + Lang[action.runtime.lang] + ":" + action.runtime.version);
        }
        let bundleDest: string = VroJsProjRealizer.getScriptBundleDestination(nativeFolder, dir, action.bundle);
        bundleDest = action?.bundle?.projectPath != null ? action.bundle.projectPath : Path.relative(nativeFolder, bundleDest);
        if (action?.bundle != null) {
            doc = doc.concat(" * @bundle " + bundleDest);
            doc = doc.concat(" * @entryhandler " + action.bundle.entry);
        }
        doc = doc.concat(params.map(param =>
            " * @param {" + param.type + "} " + param.name + (param.description.trim() != "" ? " - " : "") + param.description.trim().split("\n").filter(VroJsProjRealizer.isNotLastAndEmpty).join("\n *")
        ));
        // Backward compatibility: added empty comment line if any params exists
        if (params.length > 0) {
            doc.push(" *");
        }
        doc.push(
            " * @return {" + returnType.type + "}"
            + returnType.description.split("\n").filter(VroJsProjRealizer.isNotLastAndEmpty).join("\n *")
        );
        // Backward compatibility: removed @id and @version
        // if (id != null && id != "") {
        //     doc.push(" * @id " + id);
        // }
        // if (version != null && version != "") {
        //     doc.push(" * @version " + version);
        // }
        if (tags != null && tags.length > 0) {
            doc = doc.concat(tags.map(tag => " * @tag " + tag));
        }
        doc.push(" */");
        let docs: string = doc.join("\n");

        let iife: string = "(function (";
        for (let index = 0; index < params.length; index++) {
            let param = params[index];
            iife += param.name;
            let isLast = index + 1 >= params.length;
            if (!isLast) {
                iife += ", ";
            }
        }

        iife += ") {\n";
        if (actionSource != null) {
            let ident: string = VroJsProjRealizer.getPreferredIdent(actionSource);
            iife += actionSource.split("\n").filter(VroJsProjRealizer.isNotLastAndEmpty).map(line => {
                // Backward compatibility: no indentation for empty lines
                return line == "" ? "" : ident + line;
            }).join("\n");
        }
        iife += "\n});"
        actionSource = null;
        let content = docs + "\n" + iife + "\n";
        docs = null;
        iife = null;

        FileSystem.mkdirsSync(dir);
        FileSystem.writeFile(file, content);
    }

    private static async writeBundleFile(nativeFolder: string, basePath: string, bundle: VroScriptBundle) {
        if (bundle == null) {
            return;
        }
        if (!exist(bundle.contentPath)) {
            throw new Error(`Bundle file content not found "${bundle.contentPath}". Cannot copy it under "${bundle?.projectPath}".`);
        }
        let dest: string = VroJsProjRealizer.getScriptBundleDestination(nativeFolder, basePath, bundle);
        if (exist(dest)) {
            FileSystem.remove(dest);
        }

        if (isDirectory(bundle.contentPath)) {
            FileSystem.copy(bundle.contentPath, dest);
        } else {
            FileSystem.mkdirs(dest);
            await packaging.extract(bundle.contentPath, dest);
        }
    }

    private static getScriptBundleDestination(nativeFolder: string, basePath: string, bundle: VroScriptBundle): string {
        let dest: string = bundle?.projectPath == null ? Path.join.apply(null, [Path.resolve(basePath), "bundle"]) : bundle.projectPath;
        if (!Path.isAbsolute(dest)) {
            dest = Path.resolve(nativeFolder, dest);
        }
        return dest;
    }

    private static isNotLastAndEmpty(line: string, index: number, arr: Array<string>) {
        return line != "" || index != arr.length - 1;
    }

    private static getPreferredIdent(source: string): string {
        let lines: Array<string> = source.split("\n");
        let score = lines.reduce((sum, line) => sum + VroJsProjRealizer.getIndent(line), 0);
        return score > 0 ? "    " : "\t";
    }

    private static isSingleSpaceChar(line: string, index: number): boolean {
        let prevChar = line.charAt(index - 1) || "a";
        let nextChar = line.charAt(index + 1) || "a";
        if (prevChar == " " || nextChar == " ") {
            return false;
        }
        return true;
    }

    private static getIndent(line: string): number {
        if (line == null) {
            return 0;
        }
        let tabs: number = 0;
        let spaces: number = 0;
        for (let index: number = 0; index < line.length; index++) {
            let chr: string = line.charAt(index);
            if (chr == "\t") {
                tabs++;
            } else if (chr == " " && !VroJsProjRealizer.isSingleSpaceChar(line, index)) {
                spaces++;
            }
        }
        if (tabs > 0 && spaces > 0) {
            return 0; // Cannot decide for mixed usage of indent.
        }
        if (tabs == 0 && spaces == 0) {
            return 0; // No indentation on that line.
        }
        return spaces > 0 ? 1 : -1;
    }

    private static writeOtherResource(basePath: string, name: string, element: VroNativeElement): void {
        let elementXmlPath = Path.join(basePath, name + ".xml");
        let elementInfoXmlPath = Path.join(basePath, name + ".element_info.xml");
        let elementTagsPath = Path.join(basePath, name + ".tags.xml");
        FileSystem.mkdirsSync(basePath);

        // Content
        FileSystem.copyFile(element.dataFilePath, elementXmlPath);

        // Meta Info.
        let comment: string = "Created by VMware Professional Services Center of Excellence (PSCoE) Toolchain";
        let elementInfoXml = XmlBuilder.create(XML_OPTIONS).ele("properties");
        elementInfoXml.dtd({ pubID: "", sysID: "http://java.sun.com/dtd/properties.dtd" })
        elementInfoXml.ele("comment").dat(comment);

        if (element.type == "ResourceElement") {
            elementInfoXml.ele("entry").att("key", "mimetype").txt(element.attributes["mimetype"])
        }

        elementInfoXml
            .ele("entry").att("key", "categoryPath").txt(element.categoryPath.join(".")).up()
            .ele("entry").att("key", "name").txt(element.name).up()
            .ele("entry").att("key", "type").txt(element.type).up()
            .ele("entry").att("key", "id").txt(element.id).up()
            .ele("entry").att("key", "description").txt(element.description).up();
        FileSystem.writeFile(elementInfoXmlPath, elementInfoXml.end(SAVE_OPTIONS));

        // Tags
        let elementTagsXml = XmlBuilder.create({ version: "1.0", encoding: "UTF-8" }).ele("tags");
        let tags = element?.tags || [];
        for (let index in tags) { elementTagsXml.ele("tag").att("name", tags[index]).att("global", "true").up(); }
        FileSystem.writeFile(elementTagsPath, elementTagsXml.end(SAVE_OPTIONS));
    }

    public async realize(pkg: VroPackageMetadata, nativeFolder: string, ignorePatterns: string[]): Promise<void> {
        pkg.elements.forEach(element => {
            VroJsProjRealizer.realizeElement(element, nativeFolder, ignorePatterns);
        })
    }
}
