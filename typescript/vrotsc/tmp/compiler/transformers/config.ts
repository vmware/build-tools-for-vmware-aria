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

    interface ConfigurationDescriptor {
        id: string;
        version: string;
        name: string;
        path: string;
        attributes: Record<string, string | ConfigurationAttribute>;
    }

    interface ConfigurationAttribute {
        type: string;
        value?: any;
        description?: string;
    }

    export function getConfigTypeScriptTransformer(file: FileDescriptor, context: FileTransformationContext) {
        return transform;

        function transform() {
            const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
            sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration).forEach(classNode => {
                transformConfigClass(classNode as ts.ClassDeclaration);
            });
        }

        function transformConfigClass(classNode: ts.ClassDeclaration): void {
            const configInfo: ConfigurationDescriptor = {
                id: undefined,
                path: undefined,
                version: "1.0.0",
                name: classNode.name.text,
                attributes: {}
            };

            if (classNode.decorators && classNode.decorators.length) {
                buildConfigDecorators(configInfo, classNode);
            }

            classNode.members
                .filter(m => m.kind === ts.SyntaxKind.PropertyDeclaration)
                .forEach((fieldNode: ts.PropertyDeclaration) => {
                    buildConfigAttribute(configInfo, fieldNode);
                });

            configInfo.name = configInfo.name || classNode.name.text;
            configInfo.path = configInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
            configInfo.id = configInfo.id || generateElementId(FileType.ConfigurationTS, `${configInfo.path}/${configInfo.name}`);

            const targetFilePath = system.changeFileExt(
                system.resolvePath(context.outputs.configs, configInfo.path, configInfo.name),
                "",
                [".conf.ts"]);

            context.writeFile(`${targetFilePath}.xml`, printConfigXml(configInfo));
            context.writeFile(`${targetFilePath}.element_info.xml`, printElementInfo({
                categoryPath: configInfo.path.replace(/(\\|\/)/g, "."),
                name: configInfo.name,
                type: "ConfigurationElement",
                id: configInfo.id,
            }));
        }

        function buildConfigAttribute(configInfo: ConfigurationDescriptor, fieldNode: ts.PropertyDeclaration): void {
            let attName = getPropertyName(fieldNode.name);
            let configAtt = configInfo.attributes[attName] as ConfigurationAttribute;
            if (!configAtt) {
                configAtt = {
                    type: "Any"
                };
                if (fieldNode.initializer) {
                    switch (fieldNode.initializer.kind) {
                        case ts.SyntaxKind.StringLiteral:
                            configAtt.type = "string";
                            configAtt.value = (<ts.StringLiteral>(fieldNode.initializer)).text;
                            break;
                        case ts.SyntaxKind.NumericLiteral:
                            configAtt.type = "number";
                            configAtt.value = parseInt((<ts.NumericLiteral>fieldNode.initializer).text);
                            break;
                        case ts.SyntaxKind.TrueKeyword:
                            configAtt.type = "boolean";
                            configAtt.value = true;
                            break;
                        case ts.SyntaxKind.FalseKeyword:
                            configAtt.type = "boolean";
                            configAtt.value = false;
                            break;
                    }
                }
                if (fieldNode.type) {
                    configAtt.type = getVroType(fieldNode.type);
                }

                configInfo.attributes[attName] = configAtt;
            }
        }

        function buildConfigDecorators(configInfo: ConfigurationDescriptor, classNode: ts.ClassDeclaration): void {
            classNode.decorators
                .filter(decoratorNode => {
                    let callExpNode = decoratorNode.expression as ts.CallExpression;
                    if (callExpNode && callExpNode.expression.kind === ts.SyntaxKind.Identifier) {
                        return (<ts.Identifier>callExpNode.expression).text === "Configuration";
                    }
                })
                .forEach(decoratorNode => {
                    buildConfigDecorator(configInfo, <ts.CallExpression>decoratorNode.expression);
                });
        }

        function buildConfigDecorator(configInfo: ConfigurationDescriptor, decoratorCallExp: ts.CallExpression): void {
            let objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
            if (objLiteralNode) {
                objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                    const propName = getPropertyName(property.name);
                    switch (propName) {
                        case "id":
                            configInfo.id = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "name":
                            configInfo.name = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "path":
                            configInfo.path = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "version":
                            configInfo.version = (<ts.StringLiteral>(property.initializer)).text;
                            break;
                        case "attributes":
                            buildConfigAttributes(configInfo, <ts.ObjectLiteralExpression>property.initializer);
                            break;
                        default:
                            throw new Error(`Configuration attribute '${propName}' is not suported.`);
                    }
                });
            }
        }

        function buildConfigAttributes(configInfo: ConfigurationDescriptor, objLiteralNode: ts.ObjectLiteralExpression): void {
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                let attrInfo: ConfigurationAttribute = { type: "Any", value: null, description: null };
                switch (property.initializer.kind) {
                    case ts.SyntaxKind.StringLiteral:
                        let type: string = (<ts.StringLiteral>property.initializer).text;
                        attrInfo.type = type;
                        break;
                    case ts.SyntaxKind.ObjectLiteralExpression:
                        attrInfo = getAttrInfo(property.initializer as ts.ObjectLiteralExpression);
                        break;
                };
                let configAtt = configInfo.attributes[name] as ConfigurationAttribute;
                if (configAtt) {
                    configAtt.type = attrInfo.type != null ? attrInfo.type : configAtt.type;
                    configAtt.value = attrInfo.value != null ? attrInfo.value : configAtt.value;
                    configAtt.description = attrInfo.description != null ? attrInfo.description : configAtt.description;
                }
                else {
                    configInfo.attributes[name] = attrInfo;
                }
            });
        }

        function getAttrInfo(exp: ts.ObjectLiteralExpression): ConfigurationAttribute {
            let result: ConfigurationAttribute = { type: "Any", value: null, description: null };
            exp.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                if (name == "type") {
                    result.type = (<ts.StringLiteral>property.initializer).text;
                } else if (name == "value") {
                    result.value = getValue(property.initializer);
                } else if (name == "description") {
                    result.description = (<ts.StringLiteral>property.initializer).text;
                }
            });
            return result;
        }

        /**
         * @brief   Returns the value of a given Node.
         *
         * @details This function is called when we want to retrieve the value of a config element. We need to first
         *          extract the data in a way that can be processed later on in `printConfigXml`
         *          Types of data we can process:
         *              1. Strings: NoSubstitutionTemplateLiteral, StringLiteral
         *                  * Examples:  'example', "example", `example`
         *                  * Limitations: `test${i}` or similar do not work
         *              2. Numeric: NumericLiteral
         *                  * Examples:  1
         *              3. Booleans: TrueKeyword, FalseKeyword
         *                  * Examples: true, false
         *              4. Arrays: ArrayLiteralExpression
         *                  * An array of the other types only with the same limitations
         *              5. Objects: ObjectLiteralExpression
         *                  * Object with values of the other type only
         *                  * Limitations: Does not support adding a CompositeType inside of another CompositeType
         * @param literal
         */
        function getValue(literal: ts.Node) {
            if (literal) {
                switch (literal.kind) {
                    case ts.SyntaxKind.NoSubstitutionTemplateLiteral:
                    case ts.SyntaxKind.StringLiteral:
                        return (<ts.StringLiteral>(literal)).text;
                    case ts.SyntaxKind.NumericLiteral:
                        return parseInt((<ts.NumericLiteral>literal).text);
                    case ts.SyntaxKind.TrueKeyword:
                        return true;
                    case ts.SyntaxKind.FalseKeyword:
                        return false;
                    case ts.SyntaxKind.ArrayLiteralExpression:
                        return (<ts.ArrayLiteralExpression>literal).elements.map(element => getValue(element)).filter(element => element !== null);
                    case ts.SyntaxKind.ObjectLiteralExpression:
                        const resultingObjectLiteral = {};
                        (<ts.ObjectLiteralExpression>literal).properties.forEach((property: ts.PropertyAssignment) => {
                            const key	= property.getChildAt( 0 ).getText()
                            const value = getValue( property.initializer );
                            if ( value !== null )
                                resultingObjectLiteral[key] = value;
                        });

                        return resultingObjectLiteral;
                    default:
                        return null;
                }
            }
        }
    }

    export function getConfigYamlTransformer(file: FileDescriptor, context: FileTransformationContext) {
        return transform;

        function transform() {
            const configInfo: ConfigurationDescriptor = yaml.safeLoad(system.readFile(file.filePath).toString());
            configInfo.name = configInfo.name || system.changeFileExt(file.fileName, "");
            configInfo.path = configInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
            configInfo.id = configInfo.id || generateElementId(FileType.ConfigurationYAML, `${configInfo.path}/${configInfo.name}`);
            configInfo.version = configInfo.version || "1.0.0";
            configInfo.attributes = configInfo.attributes || {};

            const outFilePath = system.changeFileExt(
                system.resolvePath(context.outputs.configs, configInfo.path, configInfo.name),
                "",
                [".conf.yaml"]);

            context.writeFile(`${outFilePath}.xml`, printConfigXml(configInfo));
            context.writeFile(`${outFilePath}.element_info.xml`, printElementInfo({
                categoryPath: configInfo.path.replace(/(\\|\/)/g, "."),
                name: configInfo.name,
                type: "ConfigurationElement",
                id: configInfo.id,
            }));
        }
    }

    function printConfigXml(config: ConfigurationDescriptor): string {
        const stringBuilder = createStringBuilder("", "");
        stringBuilder.append(`<?xml version="1.0" encoding="utf-8" ?>`).appendLine();
        stringBuilder.append(`<config-element id="${config.id}" version="${config.version}">`).appendLine();
        stringBuilder.indent();
        stringBuilder.append(`<display-name><![CDATA[${config.name}]]></display-name>`).appendLine();
        stringBuilder.append(`<atts>`).appendLine();
        stringBuilder.indent();

        if (config.attributes) {
            Object.keys(config.attributes).forEach(attName => {
                const attOrType = config.attributes[attName];
                const att: ConfigurationAttribute = typeof attOrType === "string" ? { type: attOrType } : attOrType;
                stringBuilder.append(`<att name="${attName}" type="${att.type}" read-only="false"`);

                if (att.value != null && att.type.indexOf("Array/") === 0) {
                    att.value = printAttributeArrayValue(att.value, att.type);
                }

                if (att.value != null && att.type.indexOf("CompositeType(") === 0) {
                    att.value = printAttributeCompositeValue( att.value, att.type );
                }

                if (att.value != null || att.description != null) {
                    stringBuilder.append(`>`).appendLine();
                    stringBuilder.indent();
                    if (att.value != null) {
                        stringBuilder.append(`<value encoded="n"><![CDATA[${att.value}]]></value>`).appendLine();
                    }
                    if (att.description != null) {
                        stringBuilder.append(`<description><![CDATA[${att.description}]]></description>`).appendLine();
                    }
                    stringBuilder.unindent();
                    stringBuilder.append(`</att>`).appendLine();
                }
                else {
                    stringBuilder.append(` />`).appendLine();
                }
            });
        }

        stringBuilder.unindent();
        stringBuilder.append(`</atts>`).appendLine();
        stringBuilder.unindent();
        stringBuilder.append(`</config-element>`).appendLine();
        return stringBuilder.toString();
    }

    /**
     * @brief Prints out arrays
     *
     * @details THIS PRINTS THE ARRAYS IN vRO7 FORMAT
     *          vRO8 is backwards compatible, but future versions may not be.
     *          Array in vRO8
     *          [16:string#123312321,16:string#123132312,16:string#qffwfqwfw]
     *
     *          Array in vRO7
     *          #{#field4:string#test#;#field4:string#test2#;#field4:string#test4#}#
     *
     * @param value
     * @param type
     */
    function printAttributeArrayValue(value: Array<any>, type: string) {
        type = type.replace("Array/", "");
        let output = "#{";

        value.forEach(element => {
            output += "#" + type + "#" + element + "#;";
        });

        output = output.slice(0, -1) + "}#";
        return output;
    }

    /**
     * @brief   Prints out composite type values.
     *
     * @details THIS PRINTS THE COMPOSITE TYPE IN vRO7 FORMAT
     *          vRO8 is backwards compatible, but future versions may not be.
     *          Composite type in vRO8: {5:13:field=string#Stefan
     *              7:12:field_1=number#123.0
     *          }
     *
     *          Composite type in vRO7:
     *          #[#field#=#string#Stefan#+#field_1#=#number#123.0#]#
     *          #[#field1#=#string#rabbit#+#field2#=#Array##{#string#Rebecca#;#string#Pedro#;#string#Peppa#}##]#
     *
     * @param   {any} compositeValue
     * @param   {String} compositeType
     * @private
     */
    function printAttributeCompositeValue( compositeValue: any, compositeType: string ) {
        let output = "#[";

        for ( let [key, value] of Object.entries( compositeValue ) ) {
            const isArray = Array.isArray( value );

            if ( isArray ){
                const valueType = getArrayTypeFromCompositeType( compositeType, key );

                if ( valueType === null ) {
                    throw new Error( `Composite Type Array is in invalid format for ${key}!` );
                }

                value = `Array#${printAttributeArrayValue( value as [], valueType )}`;
            }
            else {
                value = `${typeof value}#${value}`;
            }

            output += `#${key}#=#${value}#+`;
        }

        // Cut the last +
        output = output.slice( 0, -1 ) + "]#";

        return output;
    }

    /**
     * @brief   Extracts the Array Type from the Composite Type
     *
     * @details Example CompositeType: CompositeType(field1:number,field2:boolean,field3:string,field4:Array/string):ITest
     *          If the key passed is field4 will extract `Array/string`
     *
     * @param   {String} compositeType
     * @param   {String} key
     *
     * @private
     */
    function getArrayTypeFromCompositeType( compositeType: string, key: string ): string|null {
        const result = compositeType.match( new RegExp( `${key}:(Array\\/[^,)]+)` ) );

        return result === null ? null : result[1];
    }
}
