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
import * as t from "../types";
import * as xmlbuilder from "xmlbuilder";
import { saveOptions, serialize, xmlOptions, complexActionComment, getActionXml} from "./util"
import { exist, isDirectory} from "../util";
import { decode } from "../encoding";

const buildContext = (target: string) => {
    return {
        target: target,
        elements: (category, name) => serializeTreeElementContext(path.join(target, category), name)
    }
}


const serializeTreeElementContext = (target: string, elementName: string) => {

    const store = serialize(target);

    return {
        target: target,
        data: (element: t.VroNativeElement, sourceFile : string, type : t.VroElementType) => {
            if(type == t.VroElementType.ResourceElement){
                return fs.copyFile(sourceFile, path.join(target, `${elementName}`))
            } else if (type == t.VroElementType.ScriptModule) {
                let elementXmlPath = path.join(target, `${elementName}.xml`)
                let actionXml = getActionXml(element.id, element.name, element.description, element.action);
                return fs.writeFile(elementXmlPath, actionXml);
            }
			// Re-encode the content to UTF-8
			var buffer = fs.readFileSync(sourceFile);
			return fs.writeFile(path.join(target, `${elementName}.xml`), decode(buffer));
        },
        bundle: (element: t.VroNativeElement, bundle: t.VroScriptBundle) => {
            if (bundle == null) {
                return new Promise<void>((resolve, reject) => {}); // Empty promise that does nothing. Nothing needs to be done since bundle file does not exist.
            }
            let bundleFilePathSrc = bundle.contentPath;
            if (!exist(bundleFilePathSrc)) {
                throw new Error(`Bundle path "${bundleFilePathSrc}" does not exist. Cannot get script bundle for element `
                    + `"${element.name}" of type "${element.type}"; category "${element.categoryPath}"; id: "${element.id}"`);
            }
            let bundleFilePathDest = path.join(target, `${elementName}.bundle.zip`);
            if (isDirectory(bundleFilePathSrc)) {
                var output = fs.createWriteStream(bundleFilePathDest);
                var archive = archiver('zip', { zlib: { level: 9 } });
                archive.directory(bundleFilePathSrc, false);
                archive.pipe(output);
                archive.finalize();
            } else {
                return fs.copyFile(bundleFilePathSrc, bundleFilePathDest);
            }
        },
        info: store(`${elementName}.element_info.xml`),
        tags: store(`${elementName}.tags.xml`),
        form: (element: t.VroNativeElement) => {
            if(element.form) {
                fs.writeFile(path.join(target, `${elementName}.form.json`), JSON.stringify(element.form, null, 4));
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

    const categoryPathKay = element.type == t.VroElementType.ScriptModule
        ? element.categoryPath
        : element.categoryPath.map(c => c.replace(/\./g, "/."))
    let pathKey : string = categoryPathKay.join(".");

    const categoryPath    = element.type == t.VroElementType.ScriptModule
        ? element.categoryPath.pop().split('.')
        : element.categoryPath;

	if (element.type == t.VroElementType.ResourceElement && element.attributes["version"]) {
		xInfo.ele("entry").att("key", "version").text(element.attributes["version"]);
	}
	xInfo.ele("entry").att("key", "categoryPath").text(pathKey)
	if (element.type == t.VroElementType.ResourceElement) {
        xInfo.ele("entry").att("key", "mimetype").text(element.attributes["mimetype"]);
    }
	xInfo.ele("entry").att("key", "name").text(element.name);
    xInfo.ele("entry").att("key", "type").text(element.type.toString());
	if (element.type == t.VroElementType.ResourceElement && element.attributes["description"]) {
		xInfo.ele("entry").att("key", "description").text(element.attributes["description"]);
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
        elementContext.form(element)
    ]);
}

const serializeTree = (pkg: t.VroPackageMetadata, target: string) => {
    const context = buildContext(target);
    pkg.elements.forEach(element => serializeTreeElement(context, element));
}

export { serializeTree }
