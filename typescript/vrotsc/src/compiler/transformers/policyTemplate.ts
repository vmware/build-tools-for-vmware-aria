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
    const xmldoc: typeof import("xmldoc") = require("xmldoc");

    interface PolicyTemplateDescriptor {
        id: string;
        name: string;
        description?: string;
        path: string;
        version: string;
        variables: Record<string, PolicyAttribute>;
        events: PolicyTemplateEventDescriptor[];
        elements: Record<string, PolicyElement>;
    }

    interface PolicyElement {
        type: string;
        events?: Record<string, string | PolicyWorkflowInfo>;
        schedule?: PolicyTemplateScheduleDescriptor;
    }

    interface PolicyWorkflowInfo {
        workflowId: string;
        bindings?: Record<string, WorkflowBindingInfo>;
    }

    interface WorkflowBindingInfo {
        type: string;
        variable: string;
    }

    interface PolicyAttribute {
        type: string,
        value?: any;
        description?: string;
        configId?: string;
        configKey?: string;
    }

    interface PolicyTemplateScheduleDescriptor {
        periode: string;
        when: string;
        timezone: string;
    }

    interface PolicyTemplateEventDescriptor {
        type: string;
        sourceText: string;
    }

    export function getPolicyTemplateTransformer(file: FileDescriptor, context: FileTransformationContext) {
        const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
        const policyTemplates: PolicyTemplateDescriptor[] = [];
        const eventSourceFiles: ts.SourceFile[] = [];

        sourceFile.statements.filter(n => n.kind === ts.SyntaxKind.ClassDeclaration).forEach(classNode => {
            registerPolicyTemplateClass(classNode as ts.ClassDeclaration);
        });
        eventSourceFiles.forEach(sf => context.sourceFiles.push(sf));

        return transform;

        function transform() {
            transpilePolicyEvents();

            policyTemplates.forEach(policyTemplateInfo => {
                const targetFilePath = system.changeFileExt(
                    system.resolvePath(context.outputs.policyTemplates, policyTemplateInfo.path, policyTemplateInfo.name),
                    "",
                    [".pl.ts"]);

                const xmlTemplateFilePath = system.changeFileExt(file.filePath, ".xml");
                const xmlTemplate = system.fileExists(xmlTemplateFilePath) ? system.readFile(xmlTemplateFilePath).toString() : undefined;

                context.writeFile(`${targetFilePath}.xml`, xmlTemplate ? mergePolicyTemplateXml(policyTemplateInfo, xmlTemplate) : printPolicyTemplateXml(policyTemplateInfo));
                context.writeFile(`${targetFilePath}.element_info.xml`, printElementInfo({
                    categoryPath: policyTemplateInfo.path.replace(/(\\|\/)/g, "."),
                    name: policyTemplateInfo.name,
                    type: "PolicyTemplate",
                    id: policyTemplateInfo.id,
                }));
            });
        }

        function transpilePolicyEvents(): void {
            const events = policyTemplates.reduce((events, pl) => events.concat(pl.events), <PolicyTemplateEventDescriptor[]>[]);
            eventSourceFiles.forEach((eventSourceFile, i) => {
                const [sourceText] = transformSourceFile(
                    eventSourceFile,
                    context,
                    {
                        before: [
                            collectFactsBefore,
                            transformShimsBefore,
                        ],
                        after: [
                            transformShims,
                            remediateTypeScript,
                            transformModuleSystem,
                            emitHeaderComment,
                        ],
                    },
                    file);
                events[i].sourceText = sourceText;
            });
        }

        function emitHeaderComment(sourceFile: ts.SourceFile): ts.SourceFile {
            if (context.emitHeader) {
                addHeaderComment(<ts.Statement[]><unknown>sourceFile.statements);
            }
            return sourceFile;
        }

        function registerPolicyTemplateClass(classNode: ts.ClassDeclaration): void {
            let policyTemplateInfo: PolicyTemplateDescriptor = {
                id: undefined,
                name: classNode.name.text,
                path: undefined,
                version: "1.0.0",
                variables: {},
                elements: {},
                events: [],
            };

            if (classNode.decorators && classNode.decorators.length) {
                buildPolicyTemplateDecorators(policyTemplateInfo, classNode);
            }

            classNode.members
                .filter(member => member.kind === ts.SyntaxKind.MethodDeclaration)
                .forEach((methodNode: ts.MethodDeclaration) => {
                    registerPolicyTemplateItem(policyTemplateInfo, methodNode);
                });

            policyTemplateInfo.name = policyTemplateInfo.name || classNode.name.text;
            policyTemplateInfo.path = policyTemplateInfo.path || system.joinPath(context.workflowsNamespace || "", system.dirname(file.relativeFilePath));
            policyTemplateInfo.id = policyTemplateInfo.id || generateElementId(FileType.PolicyTemplate, `${policyTemplateInfo.path}/${policyTemplateInfo.name}`);

            policyTemplates.push(policyTemplateInfo);
        }

        function registerPolicyTemplateItem(policyTemplateInfo: PolicyTemplateDescriptor, methodNode: ts.MethodDeclaration): void {
            const eventInfo: PolicyTemplateEventDescriptor = {
                type: getPropertyName(methodNode.name),
                sourceText: undefined,
            };

            const eventSourceFilePath = system.changeFileExt(sourceFile.fileName, `.${eventInfo.type}.pl.ts`, [".pl.ts"]);
            const eventSourceText = printSourceFile(
                ts.updateSourceFileNode(
                    sourceFile,
                    [
                        ...sourceFile.statements.filter(n => n.kind !== ts.SyntaxKind.ClassDeclaration),
                        ...createPolicyTemplateItemPrologueStatements(methodNode),
                        ...methodNode.body.statements
                    ]));
            const eventSourceFile = ts.createSourceFile(
                eventSourceFilePath,
                eventSourceText,
                ts.ScriptTarget.Latest,
                true);

            policyTemplateInfo.events.push(eventInfo);
            eventSourceFiles.push(eventSourceFile);
        }

        function createPolicyTemplateItemPrologueStatements(methodNode: ts.MethodDeclaration): ts.Statement[] {
            const statements: ts.Statement[] = [];

            if (methodNode.parameters.length) {
                const variableDeclarations: ts.VariableDeclaration[] = [];
                methodNode.parameters.forEach(paramNode => {
                    const paramName = (<ts.Identifier>paramNode.name).text;
                    variableDeclarations.push(ts.createVariableDeclaration(
                        paramName,
                        paramNode.type,
                        /*initializer*/ undefined
                    ));
                });

                if (variableDeclarations.length) {
                    statements.push(ts.createVariableStatement(
                        [ts.createModifier(ts.SyntaxKind.DeclareKeyword)],
                        variableDeclarations));
                }
            }

            return statements;
        }

        function buildPolicyTemplateDecorators(policyTemplateInfo: PolicyTemplateDescriptor, classNode: ts.ClassDeclaration): void {
            classNode.decorators
                .filter(decoratorNode => {
                    const callExpNode = decoratorNode.expression as ts.CallExpression;
                    if (callExpNode && callExpNode.expression.kind === ts.SyntaxKind.Identifier) {
                        return (<ts.Identifier>callExpNode.expression).text === "PolicyTemplate";
                    }
                })
                .forEach(decoratorNode => {
                    buildPolicyTemplateDecorator(policyTemplateInfo, <ts.CallExpression>decoratorNode.expression);
                });
        }

        function buildPolicyTemplateDecorator(policyTemplateInfo: PolicyTemplateDescriptor, decoratorCallExp: ts.CallExpression): void {
            const objLiteralNode = decoratorCallExp.arguments[0] as ts.ObjectLiteralExpression;
            if (objLiteralNode) {
                objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                    const propName = getPropertyName(property.name);
                    switch (propName) {
                        case "id":
                            policyTemplateInfo.id = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "name":
                            policyTemplateInfo.name = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "description":
                            policyTemplateInfo.description = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "path":
                            policyTemplateInfo.path = (<ts.StringLiteral>property.initializer).text;
                            break;
                        case "version":
                            policyTemplateInfo.version = (<ts.StringLiteral>(property.initializer)).text;
                            break;
                        case "variables":
                            buildPolicyVariables(policyTemplateInfo, <ts.ObjectLiteralExpression>property.initializer);
                            break;
                        case "elements":
                            buildPolicyElements(policyTemplateInfo, <ts.ObjectLiteralExpression>property.initializer);
                            break;
                        default:
                            throw new Error(`PolicyTemplate attribute '${propName}' is not suported.`);
                    }
                });
            }
        }

        function buildPolicyElements(policyInfo: PolicyTemplateDescriptor, objLiteralNode: ts.ObjectLiteralExpression): void {
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                policyInfo.elements[name] = buildPolicyElement(property.initializer as ts.ObjectLiteralExpression);
            });
        }

        function buildPolicyElement(objLiteralNode: ts.ObjectLiteralExpression): PolicyElement {
            let elementInfo: PolicyElement = { type: "", events: undefined, schedule: undefined };

            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                switch (name) {
                    case "type":
                        elementInfo.type = (<ts.StringLiteral>property.initializer).text;
                        break;
                    case "events":
                        buildPolicyElementEvents(elementInfo, property.initializer as ts.ObjectLiteralExpression);
                        break;
                    case "schedule":
                        buildPolicyElementSchedule(elementInfo, property.initializer as ts.ObjectLiteralExpression);
                        break;
                }
            });

            return elementInfo;
        }

        function buildPolicyElementSchedule(elementInfo: PolicyElement, objLiteralNode: ts.ObjectLiteralExpression) {
            elementInfo.schedule = { periode: "", when: "", timezone: "" };
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                switch (name) {
                    case "periode":
                        elementInfo.schedule.periode = (<ts.StringLiteral>property.initializer).text;
                        break;
                    case "when":
                        elementInfo.schedule.when = (<ts.StringLiteral>property.initializer).text;
                        break;
                    case "timezone":
                        elementInfo.schedule.timezone = (<ts.StringLiteral>property.initializer).text;
                        break;
                }
            })
        }

        function buildPolicyElementEvents(elementInfo: PolicyElement, objLiteralNode: ts.ObjectLiteralExpression) {
            let events = {};
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);

                if (property.initializer.kind === ts.SyntaxKind.StringLiteral) {
                    events[name] = (<ts.StringLiteral>property.initializer).text;
                } else if (property.initializer.kind === ts.SyntaxKind.ObjectLiteralExpression) {
                    events[name] = buildWorkflowInfo(property.initializer as ts.ObjectLiteralExpression);
                }
            });
            elementInfo.events = events;
        }

        function buildWorkflowInfo(objLiteralNode: ts.ObjectLiteralExpression): PolicyWorkflowInfo {
            let workflowInfo: PolicyWorkflowInfo = { workflowId: "", bindings: {} };
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                if (name === "workflowId") {
                    workflowInfo.workflowId = (<ts.StringLiteral>property.initializer).text;
                } else if (name === "bindings") {
                    (property.initializer as ts.ObjectLiteralExpression).properties.forEach((bindings: ts.PropertyAssignment) => {
                        let bindingName = getPropertyName(bindings.name);
                        workflowInfo.bindings[bindingName] = { type: "", variable: "" };
                        (bindings.initializer as ts.ObjectLiteralExpression).properties.forEach(((binding: ts.PropertyAssignment) => {
                            let key = getPropertyName(binding.name);
                            workflowInfo.bindings[bindingName][key] = (<ts.StringLiteral>binding.initializer).text;
                        }))
                    });
                }
            })
            return workflowInfo;
        }


        function buildPolicyVariables(policyInfo: PolicyTemplateDescriptor, objLiteralNode: ts.ObjectLiteralExpression): void {
            objLiteralNode.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                let variableInfo: PolicyAttribute = { type: "Any", value: null, description: null, configId: null, configKey: null };
                switch (property.initializer.kind) {
                    case ts.SyntaxKind.StringLiteral:
                        variableInfo.value = (<ts.StringLiteral>property.initializer).text;
                        variableInfo.type = "string";
                        break;
                    case ts.SyntaxKind.ObjectLiteralExpression:
                        variableInfo = getVariableInfo(property.initializer as ts.ObjectLiteralExpression);
                        break;
                };
                let configAtt = policyInfo.variables[name] as PolicyAttribute;
                if (configAtt) {
                    configAtt.type = variableInfo.type != null ? variableInfo.type : configAtt.type;
                    configAtt.value = variableInfo.value != null ? variableInfo.value : configAtt.value;
                    configAtt.description = variableInfo.description != null ? variableInfo.description : configAtt.description;
                    configAtt.configId = variableInfo.configId != null ? variableInfo.configId : configAtt.configId;
                    configAtt.configKey = variableInfo.configKey != null ? variableInfo.configKey : configAtt.configKey;
                }
                else {
                    policyInfo.variables[name] = variableInfo;
                }
            });
        }

        function getVariableInfo(exp: ts.ObjectLiteralExpression): PolicyAttribute {
            let result: PolicyAttribute = { type: "Any", value: null, description: null, configId: null, configKey: null };
            exp.properties.forEach((property: ts.PropertyAssignment) => {
                let name = getPropertyName(property.name);
                if (name == "type") {
                    result.type = (<ts.StringLiteral>property.initializer).text;
                } else if (name == "configId") {
                    result.configId = (<ts.StringLiteral>property.initializer).text;
                } else if (name == "configKey") {
                    result.configKey = (<ts.StringLiteral>property.initializer).text;
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
                            const key = property.getChildAt(0).getText()
                            const value = getValue(property.initializer);
                            if (value !== null)
                                resultingObjectLiteral[key] = value;
                        });

                        return resultingObjectLiteral;
                    default:
                        return null;
                }
            }
        }

        function mergePolicyTemplateXml(policyTemplate: PolicyTemplateDescriptor, xmlTemplate: string): string {
            const xmlDoc = new xmldoc.XmlDocument(xmlTemplate);
            const stringBuilder = createStringBuilder();
            let scriptIndex = 0;
            let xmlLevel = 0;

            stringBuilder.append(`<?xml version="1.0" encoding="UTF-8"?>`).appendLine();
            mergeNode(xmlDoc);

            return stringBuilder.toString();

            function mergeNode(node: XmlNode): void {
                switch (node.type) {
                    case "element":
                        mergeElement(<XmlElement>node);
                        break;
                    case "text":
                    case "cdata":
                        stringBuilder.append(node.toString().trim());
                        break;
                }
            }

            function mergeElement(ele: XmlElement): void {
                stringBuilder.append(`<${ele.name}`);
                for (let attName in ele.attr || {}) {
                    if (xmlLevel === 0 && attName === "id") {
                        stringBuilder.append(` ${attName}="${policyTemplate.id}"`);
                    }
                    else {
                        stringBuilder.append(` ${attName}="${ele.attr[attName]}"`);
                    }
                }
                stringBuilder.append(">");
                mergeChildren(ele);
                stringBuilder.append(`</${ele.name}>`);
            }

            function mergeChildren(ele: XmlElement): void {
                if (ele.name === "script") {
                    stringBuilder.append(`<![CDATA[${policyTemplate.events[scriptIndex++].sourceText}]]>`);
                }
                else if (ele.name === "description" && xmlLevel === 1 && policyTemplate.description != null) {
                    stringBuilder.append(`<![CDATA[${policyTemplate.description}]]>`);
                }
                else if (ele.children && ele.children.length) {
                    xmlLevel++;
                    (ele.children || []).forEach(childNode => {
                        mergeNode(childNode);
                    });
                    xmlLevel--;
                }
                else if (ele.val != null) {
                    stringBuilder.append(ele.val);
                }
            }
        }

        function printPolicyTemplateXml(policyTemplate: PolicyTemplateDescriptor): string {
            const stringBuilder = createStringBuilder("", "");
            stringBuilder.append(`<?xml version="1.0" encoding="utf-8" ?>`).appendLine();
            stringBuilder.append(`<dunes-policy`
                + ` name="${policyTemplate.name}"`
                + ` id="${policyTemplate.id}"`
                + ` version="${policyTemplate.version}"`
                + ` api-version="6.0.0"`
                + ` type="0"`
                + ` allowed-operations="vef"`
                + `>`).appendLine();
            stringBuilder.indent();
            if (policyTemplate.description) {
                stringBuilder.append(`<description><![CDATA[${policyTemplate.description}]]></description>`).appendLine();
            }
            if (policyTemplate.variables) {
                buildVariables(policyTemplate.variables);
            }
            if (policyTemplate.elements) {
                printElements(policyTemplate.elements);
            }
            stringBuilder.unindent();
            stringBuilder.append(`</dunes-policy>`).appendLine();
            return stringBuilder.toString();

            function printElements(elements: Record<string, PolicyElement>) {
                Object.keys(elements).forEach(elementName => {
                    printElement(elementName, elements[elementName]);
                })
            }

            function printElement(name: string, element: PolicyElement) {
                if (element.type === "Periodic Event") {
                    printPeriodicEvent(name, element.events, element.schedule);
                } else {
                    printOtherEvents(name, element.type, element.events);
                }
            }

            function printPeriodicEvent(name: string, events: Record<string, string | PolicyWorkflowInfo>, schedule: PolicyTemplateScheduleDescriptor) {
                stringBuilder.append(`<item`
                    + ` tag="${name}"`
                    + ` type="0"`
                    + ` periode="${schedule.periode}"`
                    + ` when="${schedule.when}"`
                    + ` timezone="${schedule.timezone}"`
                    + `>`).appendLine();
                if (events) {
                    stringBuilder.indent();
                    printEvents(events);
                    stringBuilder.unindent();
                }
                stringBuilder.append(`</item>`).appendLine();
            }

            function printOtherEvents(name: string, type: string, events: Record<string, string | PolicyWorkflowInfo>) {
                stringBuilder.append(`<item`
                    + ` tag="${name}"`
                    + ` type="0"`
                    + ` sdkType="${type}"`
                    + `>`).appendLine();
                if (events) {
                    stringBuilder.indent();
                    printEvents(events);
                    stringBuilder.unindent();
                }
                stringBuilder.append(`</item>`).appendLine();
            }

            function printEvents(events: Record<string, string | PolicyWorkflowInfo>) {
                Object.keys(events).forEach(event => {
                    stringBuilder.append(`<event type="${event}" kind="trigger" active="false">`).appendLine();
                    stringBuilder.indent();
                    if (typeof events[event] === "string") {
                        let sourceText = policyTemplate.events.find(e => e.type === events[event]).sourceText;
                        stringBuilder.append(`<script encoded="false"><![CDATA[${sourceText}]]></script>`).appendLine();
                    } else {
                        printWorkflowInfo(events[event] as PolicyWorkflowInfo);
                    }
                    stringBuilder.unindent();
                    stringBuilder.append(`</event>`).appendLine();
                })
            }

            function printWorkflowInfo(workflowInfo: PolicyWorkflowInfo) {
                stringBuilder.append(`<workflow id="${workflowInfo.workflowId}" >`);
                if (workflowInfo.bindings) {
                    stringBuilder.indent();
                    stringBuilder.append(`<in-bindings >`);
                    stringBuilder.indent();
                    Object.keys(workflowInfo.bindings).forEach(name => {
                        stringBuilder.append(`<bind name="${name}"`
                            + ` type = "${workflowInfo.bindings[name].type}"`
                            + ` export-name="${workflowInfo.bindings[name].variable}" />`);
                    })
                    stringBuilder.unindent();
                    stringBuilder.append(`</in-bindings>`);
                    stringBuilder.unindent();
                }
                stringBuilder.append(`</workflow>`);
            }

            function buildVariables(variables: Record<string, PolicyAttribute>) {
                Object.keys(variables).forEach(name => {
                    buildVariable(name, variables[name]);
                });
            }

            function buildVariable(name: string, variable: PolicyAttribute) {
                stringBuilder.append(`<attribute`
                    + ` type="${variable.type}"`
                    + ` name="${name}"`
                    + ` read-only="false"`);
                if (variable.configId !== null) {
                    stringBuilder.append(` conf-id="${variable.configId}"`
                        + ` conf-key="${variable.configKey}"`);
                }

                if ((variable.configId === null && variable.value !== null) || variable.description !== null) {
                    stringBuilder.append(`>`).appendLine();
                    stringBuilder.indent();
                    if (variable.description !== null) {
                        stringBuilder.append(`<description><![CDATA[${variable.description}]]></description>`);
                    }
                    if (variable.configId === null && variable.value !== null) {
                        if (variable.value != null && variable.type.indexOf("Array/") === 0) {
                            variable.value = printAttributeArrayValue(variable.value, variable.type);
                        }

                        if (variable.value != null && variable.type.indexOf("CompositeType(") === 0) {
                            variable.value = printAttributeCompositeValue(variable.value, variable.type);
                        }
                        else {
                            stringBuilder.append(`<value encoded="n"><![CDATA[${variable.value}]]></value>`).appendLine();
                        }
                    }
                    stringBuilder.appendLine();
                    stringBuilder.unindent();
                    stringBuilder.append(`</attribute>`).appendLine();
                } else {
                    stringBuilder.append(` />`).appendLine();
                }
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
            function printAttributeCompositeValue(compositeValue: any, compositeType: string) {
                let output = "#[";

                for (let [key, value] of Object.entries(compositeValue)) {
                    const isArray = Array.isArray(value);

                    if (isArray) {
                        const valueType = getArrayTypeFromCompositeType(compositeType, key);

                        if (valueType === null) {
                            throw new Error(`Composite Type Array is in invalid format for ${key}!`);
                        }

                        value = `Array#${printAttributeArrayValue(value as [], valueType)}`;
                    }
                    else {
                        value = `${typeof value}#${value}`;
                    }

                    output += `#${key}#=#${value}#+`;
                }

                // Cut the last +
                output = output.slice(0, -1) + "]#";

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
            function getArrayTypeFromCompositeType(compositeType: string, key: string): string | null {
                const result = compositeType.match(new RegExp(`${key}:(Array\\/[^,)]+)`));

                return result === null ? null : result[1];
            }
        }
    }
}
