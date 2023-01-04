namespace vrotsc {
    const ts: typeof import("typescript") = require("typescript");

    export const SCRIPT_VRO_GLOBAL = "__global";
    export const SCRIPT_VROES_VAR = "VROES";
    export const SCRIPT_VROES_MODULE = "VROES";
    export const SCRIPT_VROES_CACHE = "__VROES";
    export const SCRIPT_HELPER_MODULE = "tslib";
    export const SCRIPT_LAZY_IMPORT_NAME = "importLazy";
    export const SCRIPT_VRO_MODULE_PACKAGE = "com.vmware.pscoe.library.ecmascript";

    export const enum HierarchyFacts {
        None = 0,

        ContainsVroesReference = 1 << 0,
        ContainsActionClosure = 1 << 1,
        ContainsEcmaScriptExport = 1 << 2,
        ContainsGlobalNamespace = 1 << 3,
        ContainsGlobalReference = 1 << 4,
        ContainsTSLib = 1 << 5,
        ContainsRequire = 1 << 6,

        // Shims
        ContainsPolyfills = 1 << 11,
        ContainsSpreadArray = 1 << 12,
        ContainsSpreadOperator = 1 << 13,
        ContainsMap = 1 << 14,
        ContainsWeakMap = 1 << 15,
        ContainsSet = 1 << 16,
        ContainsWeakSet = 1 << 17,
        ContainsPromise = 1 << 18,
        ContainsSetTimeout = 1 << 19,

        // Views
        ContainsShims = (
            ContainsPolyfills | ContainsSpreadArray | ContainsSpreadOperator | ContainsMap | ContainsWeakMap |
            ContainsSet | ContainsWeakSet | ContainsPromise | ContainsSetTimeout),
        VROES = ContainsRequire | ContainsVroesReference | ContainsShims,
        GlobalScope = VROES | ContainsGlobalReference,
    }

    export const nullScriptContext: ts.TransformationContext = {
        getCompilerOptions: notImplemented,
        startLexicalEnvironment: noop,
        suspendLexicalEnvironment: noop,
        resumeLexicalEnvironment: noop,
        endLexicalEnvironment: returnUndefined,
        hoistFunctionDeclaration: noop,
        hoistVariableDeclaration: noop,
        requestEmitHelper: noop,
        readEmitHelpers: notImplemented,
        enableSubstitution: noop,
        isSubstitutionEnabled: notImplemented,
        onSubstituteNode: notImplemented,
        enableEmitNotification: noop,
        isEmitNotificationEnabled: notImplemented,
        onEmitNode: noop,
    };

    export function transformSourceFile(sourceFileOrPath: string | ts.SourceFile, context: FileTransformationContext, transformers?: ScriptTransformers, file?: FileDescriptor): [string, string, string] {
        const program = context.getScriptProgram();
        const sourceFile = typeof sourceFileOrPath === "string" ? program.getSourceFile(sourceFileOrPath) : sourceFileOrPath;
        let sourceText: string = undefined;
        let typesText: string = undefined;
        let mapText: string = undefined;
        transformers = transformers || {};
        file = file || context.getFile(sourceFile.fileName);
        const contextExt = {
            emitHeader: context.emitHeader,
            actionsNamespace: context.actionsNamespace,
            workflowsNamespace: context.workflowsNamespace,
            diagnostics: context.diagnostics,
            file: file as ScriptFileDescriptor,
            globalIdentifiers: [],
        };
        contextExt.file.hierarchyFacts = HierarchyFacts.None;
        const beforeTransformers = transformers.before || [];
        const afterTransformers = transformers.after || [];

        const emitResult = program.emit(
            sourceFile,
            (fileName, text) => {
                if (fileName.endsWith(".d.ts")) {
                    if ((<ScriptFileDescriptor>file).hierarchyFacts & HierarchyFacts.ContainsEcmaScriptExport) {
                        typesText = text;
                    }
                }
                else if (fileName.endsWith(".js.map")) {
                    mapText = text;
                }
                else {
                    sourceText = removeTrailingMapComment(text, sourceFile.fileName);
                }
            },
            undefined,
            false,
            {
                before: [createTransformers(beforeTransformers)],
                after: [createTransformers(afterTransformers)],
            });

        emitResult.diagnostics.forEach(d => context.diagnostics.addNative(d));

        return [sourceText, typesText, mapText];

        function createTransformers(transformers: ScriptTransformer[]): ts.TransformerFactory<ts.SourceFile> {
            return ctx => sourceFile => {
                let scriptContext = createContext(ctx, program.getTypeChecker());
                return transformers.reduce((s, t) => t(s, scriptContext), sourceFile);
            };
        }

        function createContext(ctx: ts.TransformationContext, typeChecker?: ts.TypeChecker): ScriptTransformationContext {
            return Object.assign(contextExt, ctx, {
                typeChecker: typeChecker,
            });
        }

        function removeTrailingMapComment(sourceText: string, fileName: string): string {
            if (fileName) {
                var mapFileName = system.changeFileExt(system.basename(fileName), ".js.map");
                if (sourceText.endsWith(`//# sourceMappingURL=${mapFileName}`)) {
                    sourceText = sourceText.substring(0, sourceText.lastIndexOf(`//# sourceMappingURL=${mapFileName}`));
                }
            }
            return sourceText;
        }
    }

    export function printSourceFile(sourceFile: ts.SourceFile): string {
        const printer = ts.createPrinter({
            newLine: ts.NewLineKind.LineFeed,
            omitTrailingSemicolon: false
        });
        return printer.printFile(sourceFile);
    }

    export function hasModifier(modifiers: ts.NodeArray<ts.Modifier>, kind: ts.SyntaxKind): boolean {
        return modifiers != null && modifiers.some(x => x.kind === kind);
    }

    export function hasAnyModifier(modifiers: ts.NodeArray<ts.Modifier>, ...kinds: ts.SyntaxKind[]): boolean {
        return modifiers != null && modifiers.some(x => kinds.some(k => k === x.kind));
    }

    export function isRequireCall(callExpression: ts.Node): boolean {
        if (callExpression.kind !== ts.SyntaxKind.CallExpression) {
            return false;
        }

        const { expression, arguments: args } = callExpression as ts.CallExpression;

        if (expression.kind !== ts.SyntaxKind.Identifier || (expression as ts.Identifier).escapedText !== "require") {
            return false;
        }

        if (args.length !== 1) {
            return false;
        }

        return true;
    }

    export function isTypeReference(node: ts.Node, typeName: string): boolean {
        if (node.kind !== ts.SyntaxKind.TypeReference) {
            return false;
        }

        let typeRef = node as ts.TypeReferenceNode;
        if (!isIdentifier(typeRef.typeName, typeName)) {
            return false;
        }

        return true;
    }

    export function isIdentifier(node: ts.Node, text: string): boolean {
        return getIdentifierTextOrNull(node) === text;
    }

    export function isStringLiteral(node: ts.Node, text: string): boolean {
        return getStringTextOrNull(node) === text;
    }

    export function isActionClosure(statement: ts.Statement): boolean {
        if (statement.kind !== ts.SyntaxKind.ExpressionStatement) {
            return false;
        }

        const { expression } = statement as ts.ExpressionStatement;

        if (expression.kind !== ts.SyntaxKind.ParenthesizedExpression) {
            return false;
        }

        const { expression: funcExpression } = expression as ts.ParenthesizedExpression;

        if (funcExpression.kind !== ts.SyntaxKind.FunctionExpression) {
            return false;
        }

        return true;
    }

    export function getIdentifierTextOrNull(node: ts.Node): string | undefined {
        if (node && node.kind === ts.SyntaxKind.Identifier) {
            return (<ts.Identifier>node).text;
        }
    }

    export function getStringTextOrNull(node: ts.Node): string | undefined {
        if (node && node.kind === ts.SyntaxKind.StringLiteral) {
            return (<ts.StringLiteral>node).text;
        }
    }

    export function getLeadingComments(sourceFile: ts.SourceFile, node: ts.Node): Comment[] {
        let text = sourceFile.text.substring(node.pos, node.end);
        let comments = ts.getLeadingCommentRanges(text, 0) || [];
        return comments.map(c => <Comment>{
            text: text.substring(c.pos, c.end),
            hasTrailingNewLine: c.hasTrailingNewLine,
            kind: c.kind,
            pos: c.pos,
            end: c.end,
        });
    }

    export function getPropertyName(node: ts.PropertyName): string {
        switch (node.kind) {
            case ts.SyntaxKind.Identifier:
                return (<ts.Identifier>node).text;
            case ts.SyntaxKind.StringLiteral:
                return (<ts.StringLiteral>node).text;
            case ts.SyntaxKind.ComputedPropertyName:
                return (<ts.ComputedPropertyName>node).getFullText();
        }
    }

    export function getVroType(typeNode: ts.TypeNode): string {
        switch (typeNode.kind) {
            case ts.SyntaxKind.StringKeyword:
                return "string";
            case ts.SyntaxKind.NumberKeyword:
                return "number";
            case ts.SyntaxKind.BooleanKeyword:
                return "boolean";
            case ts.SyntaxKind.ArrayType:
                return "Array/" + getVroType((<ts.ArrayTypeNode>typeNode).elementType);
            default:
                return "Any";
        }
    }

    export function getDecoratorNames(decorators: ts.NodeArray<ts.Decorator>): string[] {
        if (decorators && decorators.length) {
            return decorators
                .filter(decoratorNode => decoratorNode.expression.kind === ts.SyntaxKind.Identifier)
                .map(decoratorNode => (<ts.Identifier>decoratorNode.expression).text);
        }
        return [];
    }

    export function addHeaderComment(statements: ts.Statement[]): void {
        if (statements.length) {
            const AUTO_GENERATED_COMMENT =
                "----------------------------------------------------------*" + system.newLine +
                " * CHANGES MADE DIRECTLY IN VRO CLIENT MIGHT BE OVERWRITTEN *" + system.newLine +
                " *----------------------------------------------------------";
            statements[0] = ts.addSyntheticLeadingComment(
                statements[0],
                ts.SyntaxKind.MultiLineCommentTrivia,
                AUTO_GENERATED_COMMENT,
                true);
        }
    }
}
