/*
 * #%L
 * vrotsc
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
namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");
    const yaml: typeof import("js-yaml") = require("js-yaml");

    interface ResourceElementInfo {
        id: string;
        version: string;
        path: string;
        mimeType: string;
    }

    export function getResourceTransformer(file: FileDescriptor, context: FileTransformationContext) {
        return transform;

        function transform() {
            const content = system.readFile(file.filePath);
            const resourceInfo =
                getYamlElementInfo(`${file.filePath}.element_info.yaml`) ||
                getJsonElementInfo(`${file.filePath}.element_info.json`) ||
                getYamlElementInfo(system.changeFileExt(file.filePath, ".element_info.yaml")) ||
                getJsonElementInfo(system.changeFileExt(file.filePath, ".element_info.json")) ||
                <ResourceElementInfo>{};
            resourceInfo.path = resourceInfo.path || system.joinPath(context.workflowsNamespace || "", file.relativeDirPath);
            resourceInfo.mimeType = resourceInfo.mimeType || guessVroMimeType();
            resourceInfo.version = resourceInfo.version || "1.0.0";
            resourceInfo.id = resourceInfo.id || generateElementId(FileType.Resource, `${resourceInfo.path}/${file.fileName}`);

            const targetFilePath = system.resolvePath(context.outputs.resources, resourceInfo.path, file.fileName);

            context.writeFile(targetFilePath, content);
            context.writeFile(`${targetFilePath}.element_info.xml`, printElementInfo({
                categoryPath: resourceInfo.path.replace(/(\\|\/)/g, "."),
                name: file.fileName,
                type: "ResourceElement",
                id: resourceInfo.id,
                mimetype: resourceInfo.mimeType,
            }));
        }

        function getJsonElementInfo(filePath: string): ResourceElementInfo {
            if (system.fileExists(filePath)) {
                return JSON.parse(system.readFile(filePath).toString());
            }
        }

        function getYamlElementInfo(filePath: string): ResourceElementInfo {
            if (system.fileExists(filePath)) {
                return yaml.safeLoad(system.readFile(filePath).toString());
            }
        }

        function guessVroMimeType(): string {
            switch (system.extname(file.fileName).toLowerCase()) {
                case ".json":
                case ".js":
                case ".txt":
                case ".log":
                case ".md":
                case ".yaml":
                    return "text/plain";
                case ".xml":
                case ".html":
                    return "text/xml";
                default:
                    return "application/octet-stream";
            }
        }
    }
}
