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
export enum ProjectType {
	tree,
    flat,
    js
}

export enum Lang {
    javascript,
    node,
    powercli,
    python
}

export interface Source {
	directory: string;
}

export interface Destination {
	directory: string;
}

export type SUBJECT = string;

export type PEM = string;

export interface Certificate {
	subject: string;
    privateKey: PEM;
    privateKeyPassword: string
	publicKey: PEM;
	chain: Map<SUBJECT, PEM>;
}

export interface BuildContext {
	source: Source,
	destination: Destination,
	certificate: Certificate,
}

export const VSO_RESOURCE_INF = "VSO-RESOURCE-INF";

export const VroNativeResourceElementAttributesMapping = {
    id: "attribute_id",
    name: "attribute_name",
    version: "attribute_version",
    description: "attribute_description",
    mimetype: "attribute_mimetype",
    allowedOperations: "attribute_allowedOperations",
}

export enum VroElementType {
    ScriptModule = "ScriptModule",
    Workflow = "Workflow",
    PolicyTemplate = "PolicyTemplate",
    ResourceElement = "ResourceElement",
    ConfigurationElement = "ConfigurationElement",
}

export class VroActionParameter {
    name: string
    type: string
    description: string
}

export class VroScriptRuntime {
    lang: Lang      // javascript, node, powercli (Powershell) or python
    version: string // Supported "" for javascript (ECMA5), "12" for node, "11-powershell-6.2" for powercli and "3.7" for python
}

export class VroScriptBundle {
    contentPath: string     // Path on the file system to the dir or zip file containing the bundled script package.
    projectPath: string
    entry: string    // The main entry point inside the bundle package.
}

export class VroScriptInline {
    actionSource: string
    sourceFile: string
    sourceStartLine: number
    sourceStartIndex: number
    sourceEndLine: number
    sourceEndIndex: number
    getActionSource: (action : VroActionData) => string
    javadoc: any
}

export class VroActionData {
    version: string
    params: Array<VroActionParameter>
    returnType: VroActionParameter
    runtime: VroScriptRuntime
    timeout?: string
    memoryLimit?: string
    inline: VroScriptInline         // Inline Script. bundle will be null
    bundle: VroScriptBundle         // Bundled Script. inline will be null
}

export interface VroNativeElement {
    categoryPath: Array<string>
    type: VroElementType
    id: string
    name: string
    description: string
    comment: string,
    attributes: VroNativeElementAttributes
    dataFilePath: string
    tags: Array<string>
    action: VroActionData
    form?: any
}

export interface VroNativeElementAttributes {}

export interface VroNativeResourceElementAttributes extends VroNativeElementAttributes {
    id: string
    name: string
    version: string
    description: string
    mimetype: string
    allowedOperations: string
}

export interface VroPackageMetadata {
    certificate: Certificate
    groupId: string
    artifactId: string
    version: string
    packaging: string
    description: string
    elements: Array<VroNativeElement>
}
