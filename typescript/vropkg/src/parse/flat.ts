
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
import * as fs from "fs-extra";
import * as glob from "glob";
import * as path from "path";
import getLogger from "../logger";
import * as a from "../packaging";
import * as t from "../types";
import { read, xml, xmlGet, xmlToCategory, xmlToTag, xmlToAction, xmlChildNamed, getCommentFromJavadoc, getWorkflowItems, validateWorkflowPath} from "./util";
import { exist } from "../util";
import { FORM_ITEM_TEMPLATE, WORKFLOW_ITEM_INPUT_TYPE, DEFAULT_FORM_NAME, DEFAULT_FORM_FILE_NAME, VSO_RESOURCE_INF } from "../constants";

// Simple concurrency limiter to avoid too many concurrent open files
const limitConcurrency = (concurrency: number) => {
    let active = 0;
    const queue: Array<() => void> = [];
    const next = () => {
        active--;
        const fn = queue.shift();
        if (fn) fn();
    };
    return <T>(fn: () => Promise<T>): Promise<T> => new Promise<T>((resolve, reject) => {
        const run = () => {
            active++;
            fn().then((v) => { resolve(v); next(); }).catch((e) => { reject(e); next(); });
        };
        if (active < concurrency) run(); else queue.push(run);
    });
};

/**
 * Extracts a vRO element of out unziped Package element folder
 * @param elementInfoPath - vro_package/elements/<id>/info
 */
const parseFlatElement = async (elementInfoPath: string): Promise<t.VroNativeElement> => {
    const baseDirectory = path.dirname(elementInfoPath);
    const sourcePath = (name: string) => path.join(baseDirectory, name);

    const elementCategoryPath = sourcePath("categories");
    const elementTagPath = sourcePath("tags");
    const elementDataPath = sourcePath("data");

    let infoXml = xml(read(elementInfoPath));
    let categoriesXml = xml(read(elementCategoryPath));
    validateWorkflowPath(categoriesXml);
    let categoryPath = xmlToCategory(categoriesXml);
    let description = xmlGet(infoXml, "description");
    // input forms (applicable for vRO workflows only)
    let formData: any = { form: null, formItems: [] };
    let form: t.VroNativeFormElement | null = null;
    let formItems: t.VroNativeFormElement[] = [];

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
    let name: string | undefined;
    let attributes = <t.VroNativeResourceElementAttributes>{};
    let dataFilePath = elementDataPath;

    let tags: string[] = [];
    // Tags are optional
    if (exist(elementTagPath)) {
        let tagsXml = exist(elementTagPath) && xml(read(elementTagPath));
        tags = tags.concat(xmlToTag(tagsXml))
    }

    let action: t.VroActionData | null = null;
    switch (type) {
        case t.VroElementType.Workflow: {
            name = xml(read(elementDataPath)).valueWithPath("display-name");
            // parse input forms (applicable for vRO workflows only)
            formData = parseInputForms(baseDirectory);
            form = formData.form;
            formItems = formData.formItems;
            break;
        }
        case t.VroElementType.ConfigurationElement: {
            name = xml(read(elementDataPath)).valueWithPath("display-name");
            break;
        }
        case t.VroElementType.ScriptModule: {
            let elementBundlePath = sourcePath("bundle")
            name = xml(read(elementDataPath)).attr.name;
            action = xmlToAction(elementDataPath, elementBundlePath, name, comment, description, tags);
            comment = getCommentFromJavadoc(comment, action?.bundle?.projectPath, action.returnType, action?.inline?.javadoc);
            break;
        }
		case t.VroElementType.ActionEnvironment: {
			name = JSON.parse(read(dataFilePath)).name;
			break;
		}
        case t.VroElementType.PolicyTemplate: {
            name = xml(read(elementDataPath)).attr.name;
            break;
        }
        case t.VroElementType.ResourceElement: {
            await a.extract(elementDataPath, baseDirectory);
            let mapping = t.VroNativeResourceElementAttributesMapping;
            Object.keys(mapping).forEach(key => {
                let filePath = path.join(baseDirectory, VSO_RESOURCE_INF, mapping[key]);
                if (fs.existsSync(filePath)) {
                    attributes[key] = fs.readFileSync(filePath)
                } else {
					getLogger().debug(`Resource Element data bundle does not specify optional attribute ${mapping[key]}`);
                }
            });
            name = attributes["name"];
            dataFilePath = path.join(baseDirectory, VSO_RESOURCE_INF, "data");
            break;
        }
        default: {
            // noop
        }
    }

    return <t.VroNativeElement>{ categoryPath, type, id, name, description, comment, attributes, dataFilePath, tags, action, form, formItems };
}

function parseInputForms(baseDirectory: string): any {
    let form: t.VroNativeFormElement = { name: null, data: null };

    // Input form is only applicable for vRO workflows
    const elementInputFormPath = path.join(baseDirectory, DEFAULT_FORM_FILE_NAME);
    if (exist(elementInputFormPath)) {
        getLogger().info(`Parsing form '${DEFAULT_FORM_NAME}' from file '${elementInputFormPath}'`);
        form = {
            data: JSON.parse(read(elementInputFormPath)),
            name: DEFAULT_FORM_NAME
        };
    }
    let formNames: string[] = getWorkflowItems(path.join(baseDirectory, "data"), WORKFLOW_ITEM_INPUT_TYPE);
    let formItems: t.VroNativeFormElement[] = [];
    formNames.forEach((formName: string) => {
        const inputFormItemPath = path.join(baseDirectory, FORM_ITEM_TEMPLATE.replace("{{formName}}", formName)).replace(/[\\/]+/gm, path.posix.sep);
        if (!exist(inputFormItemPath)) {
            return;
        }
        const formItem: t.VroNativeFormElement = JSON.parse(read(inputFormItemPath));
        if (!formItem) {
            return;
        }
        getLogger().info(`Parsing form item '${formName}' from file '${inputFormItemPath}'`);
        formItems.push({ name: formName, data: formItem });
    });

    return {
        form: form,
        formItems: formItems
    };
}

const parseFlat = async (nativePackagePath: string, destDir: string): Promise<t.VroPackageMetadata> => {
    let tmp = path.join(destDir, "tmp");
    getLogger().info(`Extracting package ${nativePackagePath} to "${destDir}" folder...`);

    await a.extract(nativePackagePath, tmp);

    const dunesMetaInf = xml(read(path.join(tmp, "dunes-meta-inf")))
    let pkgName = dunesMetaInf.childWithAttribute("key", "pkg-name")?.val;
    let pkgDescription = dunesMetaInf.childWithAttribute("key", "pkg-description") ? dunesMetaInf.childWithAttribute("key", "pkg-description")?.val : "";
    let endArtifactIndex = pkgName?.endsWith("-SNAPSHOT") ? pkgName.length - "-SNAPSHOT".length : pkgName?.lastIndexOf("-") || 0;
    let pkgArtifactTokens = pkgName?.slice(0, endArtifactIndex).split(".");

    // Use concurrency limiting to avoid too many open files
    const infoFiles = glob.sync(path.join(tmp, "elements", "**", "info")?.replace(/[\\/]+/gm, path.posix.sep));
    const defaultConc = process.platform === 'win32' ? 1 : 1;
    const concurrency = Math.max(1, Number(process.env.VROPKG_IO_CONCURRENCY) || defaultConc);
    const limit = limitConcurrency(concurrency);
    
    getLogger().debug(`Processing ${infoFiles.length} element info files with concurrency: ${concurrency}`);
    
    let elements = await Promise.all(
        infoFiles.map(file => limit(async () => parseFlatElement(file)))
    );
    let result = <t.VroPackageMetadata>{
        groupId: pkgArtifactTokens?.slice(0, -1).join("."),
        artifactId: pkgArtifactTokens?.slice(-1).pop(),
        version: pkgName?.slice(endArtifactIndex + 1),
        description: pkgDescription,
        packaging: "package",
        elements: elements
    }
    fs.remove(tmp);

    return result;
}

export { parseFlat };
