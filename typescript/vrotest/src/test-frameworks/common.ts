/*-
 * #%L
 * vrotest
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 *
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import { join as joinPath } from "path";
import { XMLParser } from "fast-xml-parser";
import { readFileSync, existsSync } from "fs-extra";

export function extractProjectPomDetails(projectPath: string) {
    const details = {
        name: "vro-test",
        version: "1.0.0",
    };

    const pomPath = joinPath(projectPath, "pom.xml");
    if (!existsSync(pomPath)) {
        return details;
    }

    const pomContent = readFileSync(pomPath).toString("utf8");
    const parser = new XMLParser();
    const pomXml = parser.parse(pomContent);

    details.name = `${pomXml.project.groupId}.${pomXml.project.artifactId}`;
    details.version = pomXml.project.version;

    return details;
}
