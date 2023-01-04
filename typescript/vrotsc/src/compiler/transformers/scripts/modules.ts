namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");

    interface Closure {
        readonly parent: Closure;
        getIdentifier(name: string): ClosureIdentifierType;
        addIdentifier(name: string, type: ClosureIdentifierType,): void;
        newClosure(): Closure;
    }

    enum ClosureIdentifierType {
        Import,
        TSLib,
        MethodParameter,
        Variable,
        Function,
    }

    interface ImportSpec {
        var: string;
        prop?: string;
        default?: boolean;
        star?: boolean;
    }

    function createClosure(parent?: Closure): Closure {
        const identifiers: Record<string, ClosureIdentifierType> = {};
        const closure: Closure = {
            parent,
            getIdentifier: function (name: string): ClosureIdentifierType {
                if (name !== undefined && name !== null) {
                    let type = identifiers[name];
                    if (type === undefined && parent) {
                        type = parent.getIdentifier(name);
                    }
                    return type;
                }
            },
            addIdentifier: function (name: string, type: ClosureIdentifierType): void {
                if (name !== undefined && name !== null) {
                    identifiers[name] = type;
                }
            },
            newClosure: function (): Closure {
                return createClosure(closure);
            }
        };
        return closure;
    }

    export function transformModuleSystem(sourceFile: ts.SourceFile, context: ScriptTransformationContext): ts.SourceFile {
        let closure = createClosure();
        const visitor = createVisitor(visitNode, context);
        const identifiers = collectIdentifiers();
        const importMap: Record<string, ImportSpec> = {};
        const tslibVarName = buildGlobalVarName("tslib");

        return visitSourceFile(sourceFile);

        function visitNode(node: ts.Node): ts.VisitResult<ts.Node> {
            switch (node.kind) {
                case ts.SyntaxKind.Identifier:
                    return visitIdentifier(<ts.Identifier>node);
                case ts.SyntaxKind.ImportDeclaration:
                    return visitImportDeclaration(<ts.ImportDeclaration>node);
                case ts.SyntaxKind.ExportDeclaration:
                    return visitExportDeclaration(<ts.ExportDeclaration>node);
                case ts.SyntaxKind.ExportAssignment:
                    return visitExportAssignment(<ts.ExportAssignment>node);
                case ts.SyntaxKind.FunctionDeclaration:
                    return visitFunctionDeclaration(node as ts.FunctionDeclaration);
                case ts.SyntaxKind.FunctionExpression:
                    return visitFunctionExpression(node as ts.FunctionExpression);
                case ts.SyntaxKind.CallExpression:
                    return visitCallExpression(node as ts.CallExpression);
                case ts.SyntaxKind.BinaryExpression:
                    return visitBinaryExpression(node as ts.BinaryExpression);
                case ts.SyntaxKind.VariableStatement:
                    return visitVariableStatement(node as ts.VariableStatement);
                case ts.SyntaxKind.VariableDeclaration:
                    return visitVariableDeclaration(node as ts.VariableDeclaration);
            }
        }

        function visitSourceFile(node: ts.SourceFile): ts.SourceFile {
            const statements = visitor.visitNodes(node.statements);
            const prologue = createPrologueStatements();
            const epilogue = createEpilogueStatements();

            return ts.updateSourceFileNode(
                node,
                ts.setTextRange(
                    ts.createNodeArray([
                        ...prologue,
                        ...statements,
                        ...epilogue
                    ]),
                    node.statements));
        }

        function visitIdentifier(node: ts.Identifier): ts.Node {
            const identifierType = closure.getIdentifier(node.text);
            if (node.text === SCRIPT_VRO_GLOBAL && identifierType === undefined) {
                context.file.hierarchyFacts |= HierarchyFacts.ContainsGlobalReference;
            } else if (identifierType === ClosureIdentifierType.Import && isLazyImportedIdentifier(node)) {
                context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference;
                const importSpec = importMap[node.text];
                let lazyAccessor = ts.createPropertyAccess(ts.createIdentifier(importSpec.var), "_")
                if (!importSpec.star) {
                    const importPropName = importSpec.default ? "default" : importSpec.prop || node.text;
                    lazyAccessor = ts.createPropertyAccess(lazyAccessor, importPropName);
                }
                return lazyAccessor;
            } else if (identifierType === ClosureIdentifierType.TSLib && isTSLibImportedIdentifier(node)) {
                return ts.createPropertyAccess(ts.createIdentifier(tslibVarName), node);
            }

            return visitor.visitEachChild(node);
        }

        function visitImportDeclaration(node: ts.ImportDeclaration): ts.VisitResult<ts.Node> {
            if (!node.importClause) {
                return null;
            }

            const moduleSpecifier = getStringTextOrNull(node.moduleSpecifier);
            if (!moduleSpecifier) {
                return null;
            }

            context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference;
            if (moduleSpecifier === SCRIPT_HELPER_MODULE) {
                const namedImports = node.importClause.namedBindings as ts.NamedImports;
                namedImports.elements.forEach(importSpecifier => {
                    closure.addIdentifier(getIdentifierTextOrNull(importSpecifier.name), ClosureIdentifierType.TSLib);
                });
                context.file.hierarchyFacts |= HierarchyFacts.ContainsTSLib;
                return null;
            } else {
                const moduleName = resolveFullModuleName(moduleSpecifier);
                const importVarName = buildImportVarName(moduleName);
                const moduleNameLiteral = ts.setSourceMapRange(
                    ts.createLiteral(moduleName),
                    ts.getSourceMapRange(node.moduleSpecifier));
                const varList: ts.VariableDeclaration[] = [
                    ts.createVariableDeclaration(
                        importVarName,
                        undefined,
                        ts.createCall(
                            ts.createPropertyAccess(ts.createIdentifier(SCRIPT_VROES_VAR), SCRIPT_LAZY_IMPORT_NAME),
                            /*typeArguments*/ undefined,
                            /*argumentsArray*/[moduleNameLiteral]),
                    )
                ];

                if (node.importClause.name) {
                    closure.addIdentifier(node.importClause.name.text, ClosureIdentifierType.Import);

                    importMap[node.importClause.name.text] = {var: importVarName, default: true};
                }

                if (node.importClause?.namedBindings) {
                    if (ts.isNamedImports(node.importClause.namedBindings)) {
                        node.importClause.namedBindings.elements.forEach(importSpecifier => {
                            const propertyName = importSpecifier.propertyName || importSpecifier.name;
                            closure.addIdentifier(importSpecifier.name.text, ClosureIdentifierType.Import);
                            importMap[importSpecifier.name.text] = {var: importVarName, prop: propertyName.text};
                        });
                    } else if (ts.isNamespaceImport(node.importClause.namedBindings)) {
                        const nsName = node.importClause.namedBindings.name.text;
                        closure.addIdentifier(nsName, ClosureIdentifierType.Import);
                        importMap[nsName] = {var: importVarName, star: true};

                    } else {
                        return null;
                    }
                }

                return ts.setSourceMapRange(ts.createVariableStatement(undefined, varList), ts.getSourceMapRange(node));
            }

            function buildImportVarName(moduleName: string) {
                const importVarPrefix = moduleName.substring(1 + Math.max(moduleName.lastIndexOf("."), moduleName.lastIndexOf("/")));
                return buildGlobalVarName(importVarPrefix);
            }
        }

        function visitExportDeclaration(node: ts.ExportDeclaration): ts.VisitResult<ts.Node> {
            // export Foo
            if (node.exportClause && ts.isNamedExports(node.exportClause)) {
                const result: ts.Node[] = [];
                let importVarName: string = null;

                // export { Foo } from "..."
                if (node.moduleSpecifier && ts.isStringLiteral(node.moduleSpecifier)) {
                    context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference | HierarchyFacts.ContainsRequire;
                    const moduleName = resolveFullModuleName(node.moduleSpecifier.text);
                    const requireCall = ts.setSourceMapRange(ts.createCall(
                        ts.createIdentifier("require"),
                        /*typeArguments*/ undefined,
                        [ts.createStringLiteral(moduleName)]
                    ), ts.getSourceMapRange(node));
                    const importVarPrefix = moduleName.substring(1 + Math.max(moduleName.lastIndexOf("."), moduleName.lastIndexOf("/")));
                    importVarName = buildGlobalVarName(importVarPrefix);
                    const importVarStatement = ts.setSourceMapRange(ts.createVariableStatement(undefined, [
                        ts.createVariableDeclaration(
                            importVarName,
                            undefined,
                            requireCall)]
                    ), ts.getSourceMapRange(node))
                    result.push(importVarStatement);
                }

                node.exportClause.elements.forEach(exportSpecifier => {
                    const exportPropertyName = exportSpecifier.propertyName || exportSpecifier.name;
                    let exportValue = importVarName ? ts.createPropertyAccess(ts.createIdentifier(importVarName), exportPropertyName) : exportPropertyName;
                    const exportStatement = visitor.visitEachChild(ts.createExpressionStatement(ts.createBinary(
                        ts.createPropertyAccess(ts.createIdentifier("exports"), exportSpecifier.name),
                        ts.createToken(ts.SyntaxKind.EqualsToken),
                        exportValue,
                    )));
                    result.push(exportStatement);
                });

                return result;
            }

            // export * from "..."
            if (node.moduleSpecifier && ts.isStringLiteral(node.moduleSpecifier)) {
                context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference | HierarchyFacts.ContainsTSLib | HierarchyFacts.ContainsRequire;
                const moduleName = resolveFullModuleName(node.moduleSpecifier.text);
                const requireCall = ts.setSourceMapRange(ts.createCall(
                    ts.createIdentifier("require"),
                    /*typeArguments*/ undefined,
                    [ts.createStringLiteral(moduleName)]
                ), ts.getSourceMapRange(node));
                if (node.exportClause && ts.isNamespaceExport(node.exportClause)) {
                    // export * as foo from "..."
                    const exportAssignmentNode = ts.createBinary(
                        ts.createPropertyAccess(ts.createIdentifier("exports"), node.exportClause.name),
                        ts.createToken(ts.SyntaxKind.EqualsToken),
                        ts.createCall(
                            ts.createPropertyAccess(ts.createIdentifier(tslibVarName), "__importStar"),
                            /*typeArguments*/ undefined,
                            [requireCall, ts.createIdentifier("exports")]
                        ),
                    );
                    return ts.setSourceMapRange(ts.createExpressionStatement(exportAssignmentNode), ts.getSourceMapRange(node));
                } else {
                    const exportCall = ts.setSourceMapRange(ts.createCall(
                        ts.createPropertyAccess(ts.createIdentifier(tslibVarName), "__exportStar"),
                        /*typeArguments*/ undefined,
                        [requireCall, ts.createIdentifier("exports")]
                    ), ts.getSourceMapRange(node));
                    return ts.setSourceMapRange(ts.createExpressionStatement(exportCall), ts.getSourceMapRange(node));
                }
            }
            return visitor.visitEachChild(node);
        }

        function visitExportAssignment(node: ts.ExportAssignment): ts.VisitResult<ts.Node> {
            return ts.setSourceMapRange(ts.createExpressionStatement(ts.createBinary(
                ts.createPropertyAccess(ts.createIdentifier("exports"), "default"),
                ts.createToken(ts.SyntaxKind.EqualsToken),
                visitor.visitNode(node.expression) as ts.Expression,
            )), ts.getSourceMapRange(node));
        }

        function visitFunctionDeclaration(node: ts.FunctionDeclaration): ts.VisitResult<ts.Node> {
            closure = closure.newClosure();
            node.parameters.forEach(param => {
                registerFunctionParameter(param.name);
            });
            const body = node.body ? visitor.visitNode(node.body) as ts.Block : null;
            closure = closure.parent;

            if (body) {
                const result: ts.Node[] = [];
                const isExported = hasModifier(node.modifiers, ts.SyntaxKind.ExportKeyword);
                if (isExported && !node.name) {
                    const isDefault = hasModifier(node.modifiers, ts.SyntaxKind.DefaultKeyword)
                    const exportName = isDefault || !node.name ? "default" : node.name.text;
                    const funcExp = ts.setSourceMapRange(ts.createFunctionExpression(
                        /*modifiers*/undefined,
                        /*asteriskToken*/undefined,
                        /*name*/undefined,
                        /*typeParameters*/undefined,
                        node.parameters,
                        /*type*/undefined,
                        body
                    ), ts.getSourceMapRange(node));
                    result.push(ts.setSourceMapRange(ts.createExpressionStatement(ts.createBinary(
                        ts.createPropertyAccess(ts.createIdentifier("exports"), exportName),
                        ts.createToken(ts.SyntaxKind.EqualsToken),
                        funcExp,
                    )), ts.getSourceMapRange(node)));
                } else {
                    result.push(ts.updateFunctionDeclaration(
                        node,
                        node.decorators,
                        /*modifiers*/undefined,
                        /*asteriskToken*/undefined,
                        node.name,
                        /*typeParameters*/undefined,
                        node.parameters,
                        /*type*/undefined,
                        body));
                    if (isExported) {
                        const isDefault = hasModifier(node.modifiers, ts.SyntaxKind.DefaultKeyword)
                        const exportName = isDefault || !node.name ? "default" : node.name.text;
                        result.push(ts.createExpressionStatement(ts.createBinary(
                            ts.createPropertyAccess(ts.createIdentifier("exports"), exportName),
                            ts.createToken(ts.SyntaxKind.EqualsToken),
                            node.name,
                        )));
                    }
                }
                return result;
            }

            return visitor.visitEachChild(node);
        }

        function visitFunctionExpression(node: ts.FunctionExpression): ts.VisitResult<ts.Node> {
            closure = closure.newClosure();
            node.parameters.forEach(param => {
                registerFunctionParameter(param.name);
            });
            if (node.body) {
                const body = visitor.visitNode(node.body) as ts.Block;
                node = ts.updateFunctionExpression(
                    node,
                    node.modifiers,
                    node.asteriskToken,
                    node.name,
                    node.typeParameters,
                    node.parameters,
                    node.type,
                    body);
            }
            closure = closure.parent;
            return node;
        }

        function visitCallExpression(node: ts.CallExpression): ts.VisitResult<ts.Node> {
            if (isRequireCall(node)) {
                node = tryUpdateLocalRequireCall(node);
                context.file.hierarchyFacts |= HierarchyFacts.ContainsRequire;
            }
            return visitor.visitEachChild(node);
        }

        function visitBinaryExpression(node: ts.BinaryExpression): ts.VisitResult<ts.Node> {
            if (node.parent && node.parent.kind === ts.SyntaxKind.ExpressionStatement && isFirstAssignedOnImport(node)) {
                node = visitor.visitEachChild(node);
                const varName = getIdentifierTextOrNull(node.left);
                closure.addIdentifier(varName, ClosureIdentifierType.Variable);
                return node;
            }
            return visitor.visitEachChild(node);

            function isFirstAssignedOnImport(node: ts.BinaryExpression): boolean {
                if (!node.operatorToken || node.operatorToken.kind !== ts.SyntaxKind.FirstAssignment) {
                    return false;
                }
                if (!node.left || node.left.kind !== ts.SyntaxKind.Identifier) {
                    return false;
                }
                const idenitfier = node.left as ts.Identifier;
                return closure.getIdentifier(idenitfier.text) === ClosureIdentifierType.Import;
            }
        }

        /**
         * @brief   Visitor for variable statements.
         *
         * @details Here lie definitions for variable creations.
         *          Examples:
         *          const test = '123';
         *          let test = '123';
         *          export const test = '123'
         *          export default test;
         *
         *          There is also some extra logic added when it comes to handling things like:
         *          ```ts
         *          import * as key from "something"
         *
         *          export const METADATA = key;
         *          export const TEST = { METADATA };
         *          ```
         *
         * @param node
         */
        function visitVariableStatement(node: ts.VariableStatement): ts.VisitResult<ts.Node> {
            if (hasModifier(node.modifiers, ts.SyntaxKind.ExportKeyword)) {
                const result: ts.Node[] = [];
                node.declarationList.declarations.forEach(varNode => {
                    if (varNode.name && ts.isIdentifier(varNode.name)) {
                        let initializer = varNode.initializer;

                        if (!initializer) {
                            result.push(ts.setSourceMapRange(ts.createVariableStatement(undefined, [
                                ts.createVariableDeclaration(varNode.name, undefined, ts.createObjectLiteral())
                            ]), ts.getSourceMapRange(node)));
                            initializer = ts.createIdentifier(varNode.name.text);
                        }

                        const propertyAccess = ts.createPropertyAccess(ts.createIdentifier("exports"), varNode.name);

                        result.push(ts.setSourceMapRange(ts.createExpressionStatement(ts.createBinary(
                            propertyAccess,
                            ts.createToken(ts.SyntaxKind.EqualsToken),
                            visitor.visitNode(initializer) as ts.Expression,
                        )), ts.getSourceMapRange(varNode)));

                        // Add a local variable for the export, so that it can be used
                        result.push(ts.setSourceMapRange(ts.createVariableStatement(undefined, [ts.createVariableDeclaration(
                                ts.createIdentifier(varNode.name.text), varNode.type, propertyAccess
                            )]), ts.getSourceMapRange(varNode))
                        );
                    }
                });
                return result;
            }
            return visitor.visitEachChild(node);
        }

        function visitVariableDeclaration(node: ts.VariableDeclaration): ts.VisitResult<ts.Node> {
            const varName = getIdentifierTextOrNull(node.name);
            if (varName) {
                if (node.initializer && isRequireCall(node.initializer)) {
                    let requireCallNode = tryUpdateLocalRequireCall(node.initializer as ts.CallExpression);
                    requireCallNode = ts.updateCall(
                        requireCallNode,
                        ts.createPropertyAccess(ts.createIdentifier(SCRIPT_VROES_VAR), SCRIPT_LAZY_IMPORT_NAME),
                        /* typeArguments */ undefined,
                        requireCallNode.arguments);
                    node = ts.createVariableDeclaration(
                        node.name,
                        node.type,
                        requireCallNode);
                    closure.addIdentifier(varName, ClosureIdentifierType.Import);
                    importMap[varName] = {var: varName, star: true};
                    context.file.hierarchyFacts |= HierarchyFacts.ContainsVroesReference;
                    return node;
                } else {
                    node = visitor.visitEachChild(node);
                    closure.addIdentifier(varName, ClosureIdentifierType.Variable);
                    return node;
                }
            }
            return visitor.visitEachChild(node);
        }

        function collectIdentifiers(): Record<string, boolean> {
            const identifiers: Record<string, boolean> = {};
            const visitor = createVisitor(node => {
                if (node && ts.isIdentifier(node)) {
                    identifiers[node.text] = true
                }
                return visitor.visitEachChild(node);
            }, context);
            visitor.visitNodes(sourceFile.statements);
            return identifiers;
        }

        function buildGlobalVarName(prefix: string): string {
            let index = 1;
            let name: string;
            do {
                name = `${prefix}_${index++}`;
            }
            while (identifiers[name]);
            identifiers[name] = true;
            return name;
        }

        function resolveFullModuleName(moduleName: string): string {
            if (moduleName && moduleName[0] === ".") {
                moduleName = system.normalizePath(system.joinPath(context.file.relativeDirPath, moduleName));
                if (context.actionsNamespace) {
                    moduleName = `${context.actionsNamespace}.${moduleName}`;
                }
            }
            return moduleName;
        }

        function createPrologueStatements(): ts.Statement[] {
            const statements: ts.Statement[] = [];
            const variableDeclarations: ts.VariableDeclaration[] = [];
            if (context.file.hierarchyFacts & HierarchyFacts.GlobalScope || context.globalIdentifiers.length) {
                // Create the following statement:
                // var __global = (function () {
                //     return this;
                // }).call(null);
                statements.push(ts.createVariableStatement(
                    /*modifiers*/ undefined,
                    [
                        ts.createVariableDeclaration(
                            /*name*/ SCRIPT_VRO_GLOBAL,
                            /*type*/ undefined,
                            ts.createBinary(
                                ts.createCall(
                                    ts.createPropertyAccess(ts.createIdentifier("System"), "getContext"),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/ undefined),
                                ts.createToken(ts.SyntaxKind.BarBarToken),
                                ts.createCall(
                                    ts.createPropertyAccess(
                                        ts.createParen(
                                            ts.createFunctionExpression(
                                                /*modifiers*/ undefined,
                                                /*asteriskToken*/ undefined,
                                                /*name*/ undefined,
                                                /*typeParameters*/ undefined,
                                                /*parameters*/ undefined,
                                                /*modifiers*/ undefined,
                                                /*body*/ ts.createBlock([ts.createReturn(ts.createThis())], true))),
                                        "call"
                                    ),
                                    /*typeArguments*/ undefined,
                                    /*argumentsArray*/[ts.createNull()]
                                )
                            )
                        )
                    ]));
            }

            if (context.file.hierarchyFacts & HierarchyFacts.VROES) {
                // var VROES = __global.VROES || (__global.VROES = System.getModule("com.vmware.pscoe.library.ecmascript").VROES())
                variableDeclarations.push(
                    ts.createVariableDeclaration(
                        /*name*/ SCRIPT_VROES_VAR,
                        /*type*/ undefined,
                        ts.createBinary(
                            ts.createPropertyAccess(
                                ts.createIdentifier(SCRIPT_VRO_GLOBAL),
                                SCRIPT_VROES_CACHE
                            ),
                            ts.createToken(ts.SyntaxKind.BarBarToken),
                            ts.createParen(
                                ts.createBinary(
                                    ts.createPropertyAccess(
                                        ts.createIdentifier(SCRIPT_VRO_GLOBAL),
                                        SCRIPT_VROES_CACHE
                                    ),
                                    ts.createToken(ts.SyntaxKind.EqualsToken),
                                    ts.createCall(
                                        ts.createPropertyAccess(
                                            ts.createCall(
                                                ts.createPropertyAccess(ts.createIdentifier("System"), "getModule"),
                                                /* typeArguments */ undefined,
                                                [
                                                    ts.createLiteral(SCRIPT_VRO_MODULE_PACKAGE)
                                                ]),
                                            SCRIPT_VROES_MODULE
                                        ),
                                        /* typeArguments */ undefined,
                                        /* argumentsArray */ undefined
                                    )
                                )
                            )
                        )
                    ));
            }

            if (context.file.hierarchyFacts & HierarchyFacts.ContainsRequire) {
                // var require = VROES.require
                variableDeclarations.push(
                    ts.createVariableDeclaration(
                        "require",
                        /*type*/ undefined,
                        ts.createPropertyAccess(
                            ts.createIdentifier(SCRIPT_VROES_VAR),
                            "require"
                        )
                    ));
            }

            if (context.file.hierarchyFacts & HierarchyFacts.ContainsTSLib) {
                variableDeclarations.push(
                    ts.createVariableDeclaration(tslibVarName,
                        undefined,
                        ts.createPropertyAccess(ts.createIdentifier(SCRIPT_VROES_VAR), SCRIPT_HELPER_MODULE)
                    ));
            }

            if (!(context.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) && context.file.type === FileType.Action) {
                // var exports = {}
                variableDeclarations.push(
                    ts.createVariableDeclaration(
                        "exports",
                        /*type*/ undefined,
                        ts.createObjectLiteral([])
                    ));
            }

            if (variableDeclarations.length) {
                statements.push(ts.createVariableStatement(/*modifiers*/ undefined, variableDeclarations));
            }

            return statements;
        }

        function createEpilogueStatements(): ts.Statement[] {
            const statements: ts.Statement[] = [];

            if (!(context.file.hierarchyFacts & HierarchyFacts.ContainsActionClosure) && context.file.type === FileType.Action) {
                statements.push(ts.createReturn(ts.createIdentifier("exports")));
            }

            return statements;
        }

        function tryUpdateLocalRequireCall(requireCallNode: ts.CallExpression): ts.CallExpression {
            if (requireCallNode.arguments[0].kind === ts.SyntaxKind.StringLiteral) {
                const importSpecifierNode = requireCallNode.arguments[0] as ts.StringLiteral;
                if (importSpecifierNode.text && importSpecifierNode.text[0] === ".") {
                    const moduleName = resolveFullModuleName(importSpecifierNode.text);
                    requireCallNode = ts.updateCall(
                        requireCallNode,
                        requireCallNode.expression,
                        /* typeArguments */ undefined,
                        [ts.createStringLiteral(moduleName)]);
                }
            }
            return requireCallNode;
        }

        function registerFunctionParameter(name: ts.BindingName): void {
            if (!name) {
                return;
            }
            switch (name.kind) {
                case ts.SyntaxKind.Identifier:
                    closure.addIdentifier((name as ts.Identifier).text, ClosureIdentifierType.MethodParameter);
                    break;
                case ts.SyntaxKind.ObjectBindingPattern: {
                    const bindingPattern = name as ts.ObjectBindingPattern;
                    if (bindingPattern.elements) {
                        bindingPattern.elements.forEach(ele => {
                            registerFunctionParameter(ele.name);
                        });
                    }
                }
                    break;
                case ts.SyntaxKind.ArrayBindingPattern: {
                    const bindingPattern = name as ts.ArrayBindingPattern;
                    if (bindingPattern.elements) {
                        bindingPattern.elements
                            .filter(ele => ele.kind === ts.SyntaxKind.BindingElement)
                            .forEach((ele: ts.BindingElement) => {
                                registerFunctionParameter(ele.name);
                            });
                    }
                }
                    break;
            }
        }

        function isTSLibImportedIdentifier(node: ts.Identifier): boolean {
            const parent = visitor.getParent();
            return parent && ts.isCallExpression(parent);
        }

        function isLazyImportedIdentifier(node: ts.Identifier): boolean {
            if (!importMap[node.text]) {
                return false;
            }

            const parent = visitor.getParent();
            if (!parent) {
                return false;
            }

            switch (parent.kind) {
                case ts.SyntaxKind.BinaryExpression: {
                    const binaryExpressionNode = parent as ts.BinaryExpression;
                    if (binaryExpressionNode.operatorToken.kind === ts.SyntaxKind.FirstAssignment && node === binaryExpressionNode.left) {
                        return false;
                    }
                }
                    break;
                case ts.SyntaxKind.VariableDeclaration: {
                    const variableDeclarationNode = parent as ts.VariableDeclaration;
                    if (node === variableDeclarationNode.name) {
                        return false;
                    }
                }
                    break;
                case ts.SyntaxKind.PropertyAssignment: {
                    const propertyAssignmentNode = parent as ts.PropertyAssignment;
                    if (node === propertyAssignmentNode.name) {
                        return false;
                    }
                }
                    break;
                case ts.SyntaxKind.PropertyAccessExpression: {
                    const propertyAccessNode = parent as ts.PropertyAccessExpression;
                    if (node === propertyAccessNode.name) {
                        return false;
                    }
                }
                    break;
                case ts.SyntaxKind.ExportSpecifier:
                    return false;
            }

            return true;
        }
    }
}
