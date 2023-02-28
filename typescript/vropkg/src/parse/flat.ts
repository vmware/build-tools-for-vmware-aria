import * as fs from "fs-extra";
import * as glob from "glob";
import * as path from "path";
import * as winston from 'winston';
import * as a from "../packaging";
import * as t from "../types";
import { read, xml, xmlGet, xmlToCategory, xmlToTag, xmlToAction, xmlChildNamed, getCommentFromJavadoc } from "./util";
import { exist } from "../util";

/**
 * Extracts a vRO element of out unziped Package element folder
 * @param elementInfoPath - vro_package/elements/<id>/info
 */
const parseFlatElement = async (elementInfoPath: string): Promise<t.VroNativeElement> => {
    const source = path.dirname(elementInfoPath);

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
    const sourcePath = name => path.join(source, name);

    const elementCategoryPath = sourcePath("categories");
    const elementTagPath = sourcePath("tags");
    const elementDataPath = sourcePath("data");
    const elementInputFormPath = sourcePath("input_form_")

    let infoXml = xml(read(elementInfoPath));
    let categoriesXml = xml(read(elementCategoryPath));
    let categoryPath = xmlToCategory(categoriesXml);
    let description = xmlGet(infoXml, "description");


    if (description == undefined) {
        try {
            const dataXml = xml(read(elementDataPath));
            description = xmlChildNamed(dataXml, "description");
        }
        catch (e) {
            console.log("data is not xml file");
        }
    }

    let comment = xmlChildNamed(infoXml, "comment");
    let type = t.VroElementType[xmlGet(infoXml, "type")];
    let id = xmlGet(infoXml, "id");
    let name;
    let attributes = <t.VroNativeResourceElementAttributes>{};
    let dataFilePath = elementDataPath;


    let tags = [];
    // Tags are optional
    if (exist(elementTagPath)) {
        let tagsXml = exist(elementTagPath) && xml(read(elementTagPath));
        tags = tags.concat(xmlToTag(tagsXml))
    }

    // Input Form only for Workflows
    let form = null;
    if (exist(elementInputFormPath)) {
        form = JSON.parse(read(elementInputFormPath));
    }
    var action: t.VroActionData = null;


    switch (type) {
        case t.VroElementType.Workflow:
        case t.VroElementType.ConfigurationElement:
            name = xml(read(elementDataPath)).valueWithPath("display-name");
            break;
        case t.VroElementType.ScriptModule:
            let elementBundlePath = sourcePath("bundle")
            name = xml(read(elementDataPath)).attr.name;
            action = xmlToAction(elementDataPath, elementBundlePath, name, comment, description, tags);
            comment = getCommentFromJavadoc(comment, action?.bundle?.projectPath, action.returnType, action?.inline?.javadoc);
            break;
        case t.VroElementType.PolicyTemplate:
            name = xml(read(elementDataPath)).attr.name;
            break;
        case t.VroElementType.ResourceElement:
            await a.extract(elementDataPath, source);

            let mapping = t.VroNativeResourceElementAttributesMapping;

            Object.keys(mapping).forEach(key => {
                let filePath = path.join(source, t.VSO_RESOURCE_INF, mapping[key]);
                if (fs.existsSync(filePath)) {
                    attributes[key] = fs.readFileSync(filePath)
                } else {
                    winston.loggers.get("vrbt").debug(`Resource Element data bundle does not specify optional attribute ${mapping[key]}`);
                }
            });

            name = attributes["name"];
            dataFilePath = path.join(source, t.VSO_RESOURCE_INF, "data");
            break;
    }
    return <t.VroNativeElement>{ categoryPath, type, id, name, description, comment, attributes, dataFilePath, tags, action, form };
}

const parseFlat = async (nativePackagePath: string, destDir: string) => {
    let tmp = path.join(destDir, "tmp");
    winston.loggers.get("vrbt").info(`Extracting package ${nativePackagePath} to "${destDir}" folder...`);

    await a.extract(nativePackagePath, tmp);

    const dunesMetaInf = xml(read(path.join(tmp, "dunes-meta-inf")))
    let pkgName = dunesMetaInf.childWithAttribute("key", "pkg-name").val;
    let pkgDescription = dunesMetaInf.childWithAttribute("key", "pkg-description") ? dunesMetaInf.childWithAttribute("key", "pkg-description").val : "";
    let endArtifactIndex = pkgName.endsWith("-SNAPSHOT") ? pkgName.length - "-SNAPSHOT".length : pkgName.lastIndexOf("-");
    let pkgArtifactTokens = pkgName.slice(0, endArtifactIndex).split(".");

    let elements = await Promise.all(
        glob
            .sync(path.join(tmp, "elements", "**", "info"))
            .map(file => parseFlatElement(file))
    );

    let result = <t.VroPackageMetadata>{
        groupId: pkgArtifactTokens.slice(0, -1).join("."),
        artifactId: pkgArtifactTokens.slice(-1).pop(),
        version: pkgName.slice(endArtifactIndex + 1),
        description: pkgDescription,
        packaging: "package",
        elements: elements
    }
    fs.remove(tmp);
    return result;

}

export { parseFlat };
