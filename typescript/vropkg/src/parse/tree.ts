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
import * as glob from "glob";
import * as path from "path";
import * as t from "../types";
import { read, stringToCategory, xml, xmlGet, xmlToAction, xmlChildNamed, xmlToTag } from "./util";
import { exist} from "../util";


function parseTreeElement(elementInfoPath: string): t.VroNativeElement {
    let info = xml(read(elementInfoPath));

    let categoryPath = stringToCategory(xmlGet(info, "categoryPath"));
    let id = xmlGet(info, "id");
    let type = t.VroElementType[xmlGet(info, "type")];
    let name = xmlGet(info, "name");
    let attributes = {};
    let dataFilePath = elementInfoPath.replace(".element_info.xml", ".xml");
    let bundleFilePath = elementInfoPath.replace(".element_info.xml", ".bundle.zip");
    let elementTagPath = elementInfoPath.replace(".element_info.xml", ".tags.xml");
    let elementInputFormPath = elementInfoPath.replace(".element_info.xml", ".form.json");
    let infoXml = xml(read(elementInfoPath));
    let description  = xmlGet(infoXml, "description");
    let comment = xmlChildNamed(infoXml, "comment");
    let form = null;
    let tags : Array<string>= [];
    let action : t.VroActionData = null;

    // Tags are optional
    if(exist(elementTagPath)){
        const tagsContent = read(elementTagPath);
        if (tagsContent.trim() !== '') {
            const tagsXml = exist(elementTagPath) && xml(tagsContent);
            tags = tags.concat(xmlToTag(tagsXml));
        }
    }

    // Form only for WF
    if(exist(elementInputFormPath)){
        form = JSON.parse(read(elementInputFormPath));
    }

    if (type == t.VroElementType.ResourceElement) {
        attributes = <t.VroNativeResourceElementAttributes>{
            id: xmlGet(info, "id"),
            name: xmlGet(info, "name"),
            version: xmlGet(info, "version") || "0.0.0",
            mimetype: xmlGet(info, "mimetype"),
            description: xmlGet(info, "description") || "",
            allowedOperations: "evf" // There is no information in NativeFolder. Using defaults
        }
        dataFilePath = dataFilePath.replace(".xml", "");
    } else if (type == t.VroElementType.ScriptModule) {
        name = xml(read(dataFilePath)).attr.name;
        action = xmlToAction(dataFilePath, bundleFilePath, name, comment, description, tags);
    }

    return <t.VroNativeElement>{ categoryPath, type, id, name, description, attributes, dataFilePath, tags, action, form };
}


async function parseTree(nativeFolderPath: string, groupId: string, artifactId: string, version: string, packaging: string, description: string): Promise<t.VroPackageMetadata> {

    let elements = glob
        .sync(path.join(nativeFolderPath, "**", "*.element_info.xml"))
        .map(file => parseTreeElement(file)
        );

    let result = <t.VroPackageMetadata>{
        groupId: groupId,
        artifactId: artifactId,
        version: version,
        packaging: packaging,
        description: description || "",
        elements: elements
    }
    return result;
}

export { parseTree };
