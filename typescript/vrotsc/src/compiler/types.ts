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
declare const ts: typeof import("typescript/lib/tsserverlibrary");

namespace vrotsc {
    export interface FileDescriptor {
        filePath: string;
        fileName: string;
        relativeFilePath: string;
        relativeDirPath: string;
        type: FileType;
    }

    export interface ScriptFileDescriptor extends FileDescriptor {
        hierarchyFacts: HierarchyFacts;
    }

    export enum FileType {
        Action, // .[ts|js]
        Workflow, // .wf.ts
        PolicyTemplate, // .pl.ts
        ConfigurationTS, // .conf.ts
        ConfigurationYAML, // .conf.yaml
        JasmineTest, // .test.[ts|js]
        Resource, // * & ~FileType
        NativeContent, // .xml && .element_info.xml
        TypeDef, // .d.ts
        Saga, // .saga.yaml
        WorkflowForm, //.wf.form.json
    }

    export interface FileTransformationContext {
        rootDir: string;
        emitHeader: boolean;
        actionsNamespace: string;
        workflowsNamespace: string;
        outputs: ProgramOutputs;
        diagnostics: DiagnosticCollection;
        sourceFiles: ts.SourceFile[];
        getFile(fileName: string): FileDescriptor | undefined;
        readFile(fileName: string): string | undefined;
        writeFile(fileName: string, data: string | Buffer): void;
        getScriptProgram(): ts.Program;
    }

    export type TransformerFactory = (file: FileDescriptor, context: FileTransformationContext) => () => void;

    export type ScriptTransformer = (sourceFile: ts.SourceFile, context: ScriptTransformationContext) => ts.SourceFile;

    export interface ScriptTransformers {
        before?: ScriptTransformer[];
        after?: ScriptTransformer[];
    }

    export interface ScriptTransformationContext extends ts.TransformationContext {
        emitHeader: boolean;
        actionsNamespace: string;
        workflowsNamespace: string;
        file: ScriptFileDescriptor;
        typeChecker: ts.TypeChecker;
        diagnostics: DiagnosticCollection;
        globalIdentifiers: string[];
    }

    export interface Visitor {
        visitNode(node: ts.Node): ts.VisitResult<ts.Node>;
        visitNodes<T extends ts.Node>(nodes: ts.NodeArray<T> | undefined): ts.NodeArray<T>;
        visitEachChild<T extends ts.Node>(node: T): T;
        getParent(index?: number): ts.Node;
        hasParents(...kinds: ts.SyntaxKind[]): boolean;
    }

    export interface ProgramOptions {
        rootDir: string;
        emitHeader?: boolean;
        skipValidate?: boolean;
        project?: string;
        files?: string[];
        actionsNamespace?: string;
        workflowsNamespace?: string;
        outputs?: ProgramOutputs;
    }

    export interface ProgramOutputs {
        actions: string;
        workflows: string;
        configs: string;
        resources: string;
        testHelpers: string;
        policyTemplates: string;
        tests: string;
        types: string;
        maps: string;
    }

    export interface Program {
        getFiles(): readonly FileDescriptor[];
        emit(writeFileCallback: WriteFileCallback): EmitResult;
    }

    export type WriteFileCallback = (fileName: string, data: string | Buffer) => void;

    export interface EmitResult {
        diagnostics: readonly Diagnostic[];
        emittedFiles: readonly string[];
    }

    export interface Diagnostic {
        file?: string;
        line?: number;
        col?: number;
        messageText: string;
        category: DiagnosticCategory;
    }

    export enum DiagnosticCategory {
        Warning = 0,
        Error = 1,
        Suggestion = 2,
        Message = 3
    }

    export interface Comment {
        text: string;
        hasTrailingNewLine?: boolean;
        kind: ts.CommentKind;
        pos: number;
        end: number;
    }

    export interface StringBuilder {
        indent(): StringBuilder;
        unindent(): StringBuilder;
        append(value: string): StringBuilder;
        appendLine(): StringBuilder;
        toString(): string;
    }

    export type XmlNode = XmlElement | XmlTextNode | XmlCDataNode | XmlCommentNode;

    export interface XmlElement {
        type: 'element';
        name: string;
        attr: XmlAttributes;
        val: string;
        children: XmlNode[];
        firstChild: XmlNode | null;
        lastChild: XmlNode | null;
        line: number;
        column: number;
        position: number;
        startTagPosition: number;
        toString(): string;
    }

    export interface XmlTextNode {
        type: 'text';
        text: string;
        toString(): string;
    }

    export interface XmlCDataNode {
        type: 'cdata';
        cdata: string;
        toString(): string;
    }

    export interface XmlCommentNode {
        type: 'comment';
        comment: string;
        toString(): string;
    }

    export interface XmlAttributes {
        [key: string]: string;
    }
}
