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
import * as winston from "winston";
import { read, stringToCategory, xml, xmlGet, xmlToAction, xmlChildNamed, xmlToTag, getWorkflowItems } from "./util";
import { exist } from "../util";
import { FORM_SUFFIX, RESOURCE_ELEMENT_DEFAULT_VERSION, VRO_CUSTOM_FORMS_FILENAME_TEMPLATE, WINSTON_CONFIGURATION, WORKFLOW_ITEM_INPUT_TYPE } from "../constants";

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
    let infoXml = xml(read(elementInfoPath));
    let description = xmlGet(infoXml, "description");
    let comment = xmlChildNamed(infoXml, "comment");
    let tags: string[] = [];
    let action: t.VroActionData | null = null;
    // input forms (applicable for vRO workflows only)
    let formData: any = { form: null, formItems: [] };
    let form: t.VroNativeFormElement | null = null;
    let formItems: t.VroNativeFormElement[] = [];

    // Tags are optional
    if (exist(elementTagPath)) {
        const tagsContent = read(elementTagPath);
        if (tagsContent.trim() !== '') {
            const tagsXml = exist(elementTagPath) && xml(tagsContent);
            tags = tags.concat(xmlToTag(tagsXml));
        }
    }

    switch (type) {
        case t.VroElementType.ResourceElement: {
            attributes = <t.VroNativeResourceElementAttributes>{
                id: xmlGet(info, "id"),
                name: xmlGet(info, "name"),
                version: xmlGet(info, "version") || RESOURCE_ELEMENT_DEFAULT_VERSION,
                mimetype: xmlGet(info, "mimetype"),
                description: xmlGet(info, "description") || "",
                allowedOperations: "evf" // There is no information in NativeFolder. Using defaults
            }
            dataFilePath = dataFilePath.replace(".xml", "");
            break;
        }
        case t.VroElementType.ScriptModule: {
            name = xml(read(dataFilePath)).attr.name;
            action = xmlToAction(dataFilePath, bundleFilePath, name, comment, description, tags);
            break;
        }
		case t.VroElementType.ActionEnvironment: {
			name = JSON.parse(read(dataFilePath)).name;
			break;
		}
        default: {
            // parse input forms (applicable for vRO workflows only)
            formData = parseInputForms(type, name, elementInfoPath);
            form = formData.form as t.VroNativeFormElement;
            formItems = formData.formItems as t.VroNativeFormElement[];
        }
    }

    return <t.VroNativeElement>{ categoryPath, type, id, name, description, attributes, dataFilePath, tags, action, form, formItems };
}

function parseInputForms(elementType: t.VroElementType, workflowName: string, elementInputFormPath: string): any {
    if (elementType !== t.VroElementType.Workflow) {
        return {
            form: null,
            formItems: null
        };
    }
    let form: t.VroNativeFormElement | undefined;
    // extract base dir for forms
    const formFileDir = elementInputFormPath.replace(".element_info.xml", "").replace(workflowName, "");
    const formFileName = path.join(formFileDir, `${workflowName}${FORM_SUFFIX}`).replace(/[\\/]+/gm, path.posix.sep)
    // Input form is only applicable for vRO workflows
    if (exist(formFileName)) {
        winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).info(`Parsing form '${workflowName}' from file ${formFileName}`);
        form = {
            data: JSON.parse(read(formFileName)),
            name: workflowName
        };
    }
    let formNames: string[] = getWorkflowItems(path.join(formFileDir, `${workflowName}.xml`).replace(/[\\/]+/gm ,path.posix.sep), WORKFLOW_ITEM_INPUT_TYPE);
    let formItems: t.VroNativeFormElement[] = [];
    formNames.forEach((item: string) => {
        const formItemFile = VRO_CUSTOM_FORMS_FILENAME_TEMPLATE.replace("{{elementName}}", workflowName).replace("{{formName}}", item);
        const formItemFileName = path.join(formFileDir, formItemFile).replace(/[\\/]+/gm, path.posix.sep);
        if (!exist(formItemFileName)) {
            return;
        }
        const formItem: t.VroNativeFormElement = JSON.parse(read(formItemFileName));
        if (!formItem) {
            return;
        }
        winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).info(`Parsing form item '${item}' from file '${formItemFileName}'`);
        formItems.push({ name: item, data: formItem });
    });

    return {
        form: form,
        formItems: formItems
    };
}

async function parseTree(nativeFolderPath: string, groupId: string, artifactId: string, version: string, packaging: string, description: string): Promise<t.VroPackageMetadata> {
    let elements = glob
		.sync(path.join(nativeFolderPath, "**", "*.element_info.xml").replace(/[\\/]+/gm, path.posix.sep))
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
