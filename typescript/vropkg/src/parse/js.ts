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
import * as Path            from "path";
import * as FileSystem      from "fs-extra";
import {XmlDocument}        from "xmldoc";
import {VroPackageMetadata, VroNativeElement, VroActionData, VroElementType, VroNativeElementAttributes, VroScriptBundle} from "../types";
import {getCommentFromJavadoc, getScriptRuntime} from "./util";
import {decode}           from "../encoding";
import * as AbstractSyntaxTree from "abstract-syntax-tree";
import * as Comments from "parse-comments";
import * as uuidv4 from 'uuid/v4';
import * as winston from 'winston';
import * as glob from "glob";

export class VroJsProjParser {

    private lazy : boolean;
    constructor(lazy:boolean = true) {
        this.lazy = lazy;
    }

    public async parse(vroJsFolderPath: string, groupId: string, artifactId: string, version: string, packaging: string): Promise<VroPackageMetadata> {
        winston.loggers.get("vrbt").info(`Parsing vro javascript project folder path "${vroJsFolderPath}"...`);

        let elements: Array<VroNativeElement> = [];

        // let parser = new VroNativeFolderElementParser();
        const JS_EXTENSION = ".js";
        let baseDir = Path.join(vroJsFolderPath, "src", "main", "resources");
        glob.sync(Path.join(baseDir, "**", "*" + JS_EXTENSION)).forEach(jsFile => {
            var content = FileSystem.readFileSync(jsFile);
            let vroPath = jsFile.substring(baseDir.length + 1);
            let moduleIndex = vroPath.lastIndexOf("/");
            let moduleName  = vroPath.substring(0, moduleIndex).replace(/\//g, ".");
            let name   = vroPath.substring(moduleIndex + 1, vroPath.length - JS_EXTENSION.length);
            let source = content.toString();
            let javadoc       : any           = VroJsProjParser.getFirstCommentInSource(source, jsFile);
            let description   : string        = VroJsProjParser.getDescriptionFromJavascriptDoc(javadoc);
            let tags          : Array<string> = VroJsProjParser.getTagsFromJavascriptDoc(javadoc);
            let returns       : any           = VroJsProjParser.getReturnStatementFromJavascriptDoc(javadoc);
            let id            : string        = VroJsProjParser.getTagFromJavascriptDoc(javadoc, ["id", "Id", "ID"], uuidv4());
            let version       : string        = VroJsProjParser.getTagFromJavascriptDoc(javadoc, ["version", "Version"], "1.0.0");
            let runtime       : string        = VroJsProjParser.getTagFromJavascriptDoc(javadoc, ["runtime"], "javascript:ECMA5");
            let bundle        : string        = VroJsProjParser.getTagFromJavascriptDoc(javadoc, ["bundle"], null);
            let entryHandler  : string        = VroJsProjParser.getTagFromJavascriptDoc(javadoc, ["entryhandler", "EntryHandler", "Entryhandler"], null);
            let paramsFromDoc : Array<any>    = VroJsProjParser.getParamsFromJavascriptDoc(javadoc, name);
            let sourceTree    : any           = VroJsProjParser.getSourceTreeFromSource(source, jsFile);
            let paramsFromSrc : Array<any>    = VroJsProjParser.getParamsFromSource(sourceTree);

            let params : Array<any> = new Array<any>();
            VroJsProjParser.mergeSetWithUniqueNameKey(params, paramsFromDoc);
            VroJsProjParser.mergeSetWithUniqueNameKey(params, paramsFromSrc);

            let metadata : VroNativeElementAttributes = {
                id: "",
                name: name,
                mimetype: "text/javascript",
                description: description
            };

            let startLine  = sourceTree.expression?.body?.loc?.start?.line;     // 1 - based
            let startIndex = sourceTree.expression?.body?.loc?.start?.column;   // 0 - based
            let endLine    = sourceTree.expression?.body?.loc?.end?.line;
            let endIndex   = sourceTree.expression?.body?.loc?.end?.column;

            startLine = startLine ? startLine -1 : 0;
            endLine   = endLine   ? endLine   -1 : 0;

            let element : VroNativeElement =
                {
                        categoryPath: [moduleName],
                        type:         VroElementType.ScriptModule,
                        id:           id,
                        name:         name,
                        description:  description,
                        comment:      getCommentFromJavadoc("", bundle, returns, javadoc),
                        attributes:     metadata,
                        dataFilePath: jsFile,
                        tags: tags,
                        action:        {
                            version:      version,
                            description:  description,
                            params:       params,
                            returnType:   returns,
                            runtime: getScriptRuntime(runtime),
                            inline: {
                                actionSource: this.lazy ? null : VroJsProjParser.getActionBodyFromSource(sourceTree, source, jsFile),
                                sourceFile:   jsFile,
                                sourceStartLine:  startLine,
                                sourceStartIndex: startIndex,
                                sourceEndLine:    endLine,
                                sourceEndIndex:   endIndex,
                                getActionSource: function (action : VroActionData):string {
                                    let jsFile = action.inline.sourceFile;
                                    let startLine = action.inline.sourceStartLine;
                                    let startIndex = action.inline.sourceStartIndex;
                                    let endLine = action.inline.sourceEndLine;
                                    let endIndex = action.inline.sourceEndIndex;
                                    let fileContent = FileSystem.readFileSync(jsFile)?.toString();
                                    let actionSource = VroJsProjParser.getActionBodyFromSourceIndexes(fileContent, startLine, startIndex, endLine, endIndex);
                                    return actionSource;
                                },
                                javadoc: javadoc?.value
                            },
                            bundle: VroJsProjParser.getScriptBundle(vroJsFolderPath, bundle, entryHandler),
                        }
                } as VroNativeElement;
             elements.push(element);
        }, this);

        return <VroPackageMetadata>{
            groupId:    groupId,
            artifactId: artifactId,
            version:    version,
            packaging:  packaging,
            elements:   elements,
        }
    }

    private static getFirstCommentInSource(source:string, jsFile : string) : any {
        const comments = new Comments({
            "parse": {
                "type": function (type:string, parsed : any, opts : any) : any {
                    return {
						"type": "NameExpression",
						"name": type
					};
                }
            }
        });
        comments.parse(source.toString());
        let comment : Array<any> = comments.ast ? comments.ast : [];
        comment = comment.filter(a=>VroJsProjParser.isTopLevelComment(a) && !VroJsProjParser.isLicenseComment(a.raw)).sort((a, b) => a.loc?.start?.line - b.loc?.start?.line);
		let details : any = comment.length ? comment[0] : {};
        return details;
    }

    private static isTopLevelComment(comment: any) : boolean {
        const context : any = comment?.code?.context;
        return context == null || Object.keys(context).length <= 0;
    }

    private static isLicenseComment(commentString: string): boolean {
        try {
            const regexFilter = new RegExp('#\\s*%\\s*L.*#\\s*L\\s*%', 's');
            return regexFilter.test(commentString);
        } catch (e) {
            return false;
        }
    }

    private static getDescriptionFromJavascriptDoc(details:any) : string {
        return details.description ? details.description : "";
    }

    private static getParamsFromJavascriptDoc(details:any, name:string) : Array<any> {
        var params = new Array<any>();
        let annotations : Array<any> = details.tags as Array<any>;
        if (annotations == null) {
            return [];
        }
        annotations.filter(a => a.title == "param" && /^[a-zA-Z0-9_$]+$/.test( a.name ) ).forEach(annotation => {
            let param = {
                name: annotation.name,
                type: VroJsProjParser.parseType(annotation.type),
                description: annotation.description
            };
            if (!params.find(e=>e.name == param.name)) {
                params.push(param);
            }
         });
         return params;
    }

    private static getReturnStatementFromJavascriptDoc(details:any) : any {
        let deflt = { type: "Any", description: ""};
        let annotations : Array<any> = details.tags as Array<any>;
        if (annotations == null) {
            return deflt;
        }
        let returns : any  = annotations.find(a=>a.title == "return" || a.title == "returns");
        if (returns) {
           return  {
               type: VroJsProjParser.parseType(returns.type),
               description: returns.description
           };
        } else {
           return deflt;
        }
    }

    private static getTagFromJavascriptDoc(details:any, possibleTags:Array<string>, deflt : string) : string {
        let annotations : Array<any> = details.tags as Array<any>;
        if (annotations == null) {
            return deflt;
        }
        let valueAnnotation : any  = annotations.find(a=>possibleTags.includes(a.title));
        let value : string = valueAnnotation?.description;
        value = !!value ? value : deflt;
        return value;
    }

    private static getTagsFromJavascriptDoc(details:any) : Array<string> {
        let annotations : Array<any> = details.tags as Array<any>;
        if (annotations == null) {
            return [];
        }
        return annotations.filter(a=>a.title == "tag").map(a=>a.description);
    }

    private static getSourceTreeFromSource(source:string, jsFile : string) : any {
        let tree : any;
        try{
            tree = new AbstractSyntaxTree(source);
        } catch(ex) {
            throw new Error(`${jsFile} ${ex.message}\n ${ex.stack}`);
        }
        tree = tree._tree ? tree?._tree : tree;
        if (tree?.type != "Program" || tree?.sourceType != "module") {
            throw new Error(`Wrong source type (${tree?.type}) or subtype (${tree.sourceType}) for source file : "${jsFile}". `
               + `Expected type "Program" with subtype "module". Please check that you have just a single Immediately Invoked Function Expression (IIFE) like this: \`(funciton (param1, param2,...){...});\``);
        }
        if (!tree?.body || !tree?.body?.length || tree?.body[0].type != "ExpressionStatement" || !tree?.body[0]?.expression || tree?.body[0]?.expression.type != "FunctionExpression") {
            throw new Error(jsFile + ": First source element does not seem to be Immediately Invoked Function Expression (IIFE). Please make sure it looks like this `(function (params...){...});`");
        }
        return tree.body[0];
    }

    private static getParamsFromSource(tree:any) : Array<any> {
        var params : Array<any> = new Array<any>();
        let paramElements : Array<any> = tree.expression.params ? tree.expression.params as Array<any> : [];
        paramElements.forEach(element => {
            let param = {
                name: element.name,
                type: "Any",
                description: ""
            };
            if (!params.find(e=>e.name == param.name)) {
                params.push(param);
            }
        });
        return params;
    }

    private static getActionBodyFromSource(tree: any, source : string, jsFile : string) : string {
        let startLine  = tree.expression?.body?.loc?.start?.line;     // 1 - based
        let startIndex = tree.expression?.body?.loc?.start?.column;   // 0 - based
        let endLine    = tree.expression?.body?.loc?.end?.line;
        let endIndex   = tree.expression?.body?.loc?.end?.column;

        startLine = startLine ? startLine -1 : 0;
        endLine   = endLine   ? endLine   -1 : 0;
        return this.getActionBodyFromSourceIndexes(source, startLine, startIndex, endLine, endIndex);
    }

    private static getActionBodyFromSourceIndexes(source : string, startLine : number, startIndex : number, endLine : number, endIndex : number) : string {
        let filtered : Array<string> = [];
        let lines = source.split("\n");
        let len = lines.length;
        let maxIdent : number = Number.MAX_VALUE;
        for (let index = 0; index < len; index++) {
            let line = lines.shift();
            if (index < startLine || index > endLine) {
                continue;
            }
            let tabequivalent = "    ";
            let filteredLine : string;
            if (index != startLine && index != endLine) {
                filteredLine = line;
            } else if (index == startLine && index == endLine) {
                filteredLine = line.substring(startIndex, endIndex);
                filteredLine = filteredLine.charAt(0) == '{' ? filteredLine.substring(1) : filteredLine;
                filteredLine = filteredLine.charAt(filteredLine.length-1) == '}' ? filteredLine.substring(0, filteredLine.length-1) : filteredLine;
            } else if (index == startLine) {
                filteredLine = line.substring(startIndex);
                filteredLine = filteredLine.charAt(0) == '{' ? filteredLine.substring(1) : filteredLine;
            } else {
                filteredLine = line.substring(0, endIndex);
                filteredLine = filteredLine.charAt(filteredLine.length-1) == '}' ? filteredLine.substring(0, filteredLine.length-1) : filteredLine;
            }
            filteredLine = filteredLine.replace("\t", tabequivalent);
            if (maxIdent > 0 && !!filteredLine && filteredLine.length > 0) {
                let index = filteredLine.search(/\S/);
                if (index != -1 && index < maxIdent) {
                    maxIdent = index;
                }
            }
            filtered.push(filteredLine);
        }
        lines = filtered; filtered = null;
		// Remove first empty line
		if (lines[0] == ""){
			lines.shift();
		}
        let actionSource = lines.map( (line, index) => {
            if (!!line && line.length > maxIdent) {
                return line.substring(maxIdent);
            } else {
                return line.search(/\S/) == -1 ? "" : line;
            }
        }).join("\n");
        return actionSource;
    }

    private static mergeSetWithUniqueNameKey(destination : Array<any>, extra : Array<any>) : Array<any> {
        if (!extra) {
            return destination;
        }
        extra.forEach(element=>{
            if (!destination.find(a=>a.name == element.name)) {
                destination.push(element);
            }
        });
        return destination;
    }

    private static parseType(type:any) : string {
        const primitiveTypes : Array<string> = ["boolean", "number", "string", "Any", "undefined", "null", "object"];
        if (type?.type == "NameExpression") {
            let typeName:string = type.name ? type.name : "" + JSON.stringify(type);
            for (let index in primitiveTypes) {
                if (typeName.toLowerCase() == primitiveTypes[index]) {
                    return primitiveTypes[index];
                }
            }
            return typeName;
        }
        return "Any";
    }

    private static getScriptBundle(vroJsFolderPath: string, bundle : string, entryhandler : string) : VroScriptBundle {
        if (bundle == null) {
            if (entryhandler != null) {
                throw new Error(`Cannot specify entryhandler (${entryhandler}), without also specifying a bundle. Use @bundle tag to specify path to zip file or dir.`);
            }
            return null;
        }
        if (entryhandler == null) {
            throw new Error(`Need to specify an entryhandler for a bundle ("${bundle}"). Please use @entryhandler tag to specify entrypoint inside the bundle.`);
        }
        let absolutePath = Path.isAbsolute(bundle) ? bundle : Path.resolve(vroJsFolderPath, bundle)
        return { contentPath: absolutePath, projectPath: bundle, entry: entryhandler};
    }

	private static filterParams( paramName: string, name: string ): boolean {
		const shouldFilter	= /^[a-zA-Z0-9_$]+$/.test( paramName );

		if ( ! shouldFilter ) {
			winston.loggers.get("vrbt").warn(
				`Parameter: ${paramName} will not be set in action: ${name} due to illegal characters.` +
				'All parameter names should match: ^[a-zA-Z0-9_$]+$'
			);
		}

		return shouldFilter;
	}
}
