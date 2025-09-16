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
import * as path from "path";
import * as archiver from 'archiver';
import { archive as createArchive } from "../packaging";
import type { ArchiverWithDone } from "../packaging";
import * as t from "../types";
import * as xmlbuilder from "xmlbuilder";
import { serialize, xmlOptions, complexActionComment, getActionXml } from "./util"
import { exist, isDirectory } from "../util";
import { decode } from "../encoding";
import { JSON_DEFAULT_IDENT, VRO_CUSTOM_FORMS_FILENAME_TEMPLATE, VRO_FORM_TEMPLATE, ZLIB_COMPRESS_LEVEL } from "../constants";

const buildContext = (target: string) => {
    return {
        target: target,
        elements: (category: string, name: string) => serializeTreeElementContext(path.join(target, category), name)
    }
}

const serializeTreeElementContext = (target: string, elementName: string) => {
    const store = serialize(target);
    return {
        target: target,
        data: (element: t.VroNativeElement, sourceFile: string, type: t.VroElementType) => {
            switch (type) {
                case t.VroElementType.ResourceElement: {
                    return fs.copyFileSync(sourceFile, path.join(target, `${elementName}`));
                }
                case t.VroElementType.ScriptModule: {
                    let elementXmlPath = path.join(target, `${elementName}.xml`)
					let actionXml = getActionXml(element.id, element.name, element.description, element.action);
					fs.mkdirsSync(path.dirname(elementXmlPath));
                    return fs.writeFileSync(elementXmlPath, actionXml);
                }
				case t.VroElementType.ActionEnvironment: {
                    return fs.copyFileSync(sourceFile, path.join(target, `${elementName}`));
				}
                default: {
                    // Re-encode the content to UTF-8
                    let buffer = fs.readFileSync(sourceFile);
                    return fs.writeFileSync(path.join(target, `${elementName}.xml`), decode(buffer));
                }
            }
        },
        bundle: (element: t.VroNativeElement, bundle: t.VroScriptBundle) => {
            if (bundle == null) {
                // Empty promise that does nothing. Nothing needs to be done since bundle file does not exist.
                return new Promise<void>((resolve, reject) => { });
            }
            let bundleFilePathSrc = bundle.contentPath;
            if (!exist(bundleFilePathSrc)) {
                throw new Error(`Bundle path "${bundleFilePathSrc}" does not exist. Cannot get script bundle for element `
                    + `"${element.name}" of type "${element.type}"; category "${element.categoryPath}"; id: "${element.id}"`);
            }
            let bundleFilePathDest = path.join(target, `${elementName}.bundle.zip`);
            if (isDirectory(bundleFilePathSrc)) {
                const arch: ArchiverWithDone = createArchive(bundleFilePathDest);
                arch.directory(bundleFilePathSrc, false);
                arch.finalize();
                return arch.done;
            } else {
                return fs.copyFile(bundleFilePathSrc, bundleFilePathDest);
            }
        },
        info: store(`${elementName}.element_info.xml`),
        tags: store(`${elementName}.tags.xml`),
        // if the object contains custom form then store it on the file system
        form: (element: t.VroNativeElement) => {
            if (element.form?.data) {
                const formFileName = VRO_FORM_TEMPLATE.replace("{{elementName}}", elementName);
                fs.writeFile(path.join(target, formFileName), JSON.stringify(element.form?.data, null, JSON_DEFAULT_IDENT));
            }
        },
        // if the object contains more forms (i.e. custom interaction enabled workflow) then store them on the file system
        formItems: (element: t.VroNativeElement) => {
            if (element.formItems && Array.isArray(element.formItems)) {
                element.formItems.forEach((formItem: t.VroNativeFormElement) => {
                    const customFormFileName = VRO_CUSTOM_FORMS_FILENAME_TEMPLATE.replace("{{elementName}}", elementName).replace("{{formName}}", formItem.name);
                    fs.writeFile(path.join(target, customFormFileName), JSON.stringify(formItem.data, null, JSON_DEFAULT_IDENT));
                });
            }
        }
    }
}

const serializeTreeElement = async (context: any, element: t.VroNativeElement): Promise<void[]> => {
    const xInfo = xmlbuilder.create("properties", xmlOptions);
    xInfo.dtd("", "http://java.sun.com/dtd/properties.dtd");
    if (element?.type != t.VroElementType.ScriptModule) {
        xInfo.ele("comment").text(element?.comment);
    } else {
        xInfo.ele("comment").cdata(complexActionComment(element));
    }
    const categoryPathKey = element.type == t.VroElementType.ScriptModule
        ? element?.categoryPath
        : element?.categoryPath.map(c => c.replace(/\./g, "/."))
    let pathKey: string = categoryPathKey.join(".");

    const categoryPath = element.type == t.VroElementType.ScriptModule
        ? element?.categoryPath.pop().split('.')
        : element?.categoryPath;

	// @NOTE: The below code will not support empty values for version, mimetype, and description attributes
    if (element.type == t.VroElementType.ResourceElement && element.attributes["version"]) {
        xInfo.ele("entry").att("key", "version").text(element.attributes["version"].toString());
    }
    xInfo.ele("entry").att("key", "categoryPath").text(pathKey)
    if (element.type == t.VroElementType.ResourceElement && element.attributes["mimetype"]) {
        xInfo.ele("entry").att("key", "mimetype").text(element.attributes["mimetype"].toString());
    }
    xInfo.ele("entry").att("key", "name").text(element.name.toString());
    xInfo.ele("entry").att("key", "type").text(element.type.toString());
    if (element.type == t.VroElementType.ResourceElement && element.attributes["description"]) {
        xInfo.ele("entry").att("key", "description").text(element.attributes["description"].toString());
    }
    xInfo.ele("entry").att("key", "id").text(element.id);

    const xTags = xmlbuilder.create("tags", xmlOptions);
    element.tags.forEach(name => {
        xTags.ele("tag").att("name", name).att("global", "true").up();
    })

    const elementContext = context.elements(path.join(
        ...["src", "main", "resources", element.type, ...categoryPath]
    ), element.name);


    return Promise.all([
        elementContext.data(element, element.dataFilePath, element.type),
        elementContext.bundle(element, element?.action?.bundle),
        elementContext.info(xInfo.end({ pretty: true })),
        elementContext.tags(xTags.end({ pretty: true })),
        elementContext.form(element),
        elementContext.formItems(element)
    ]);
}

const serializeTree = (pkg: t.VroPackageMetadata, target: string) => {
    const context = buildContext(target);
    pkg.elements.forEach(element => serializeTreeElement(context, element));
}

export { serializeTree }
