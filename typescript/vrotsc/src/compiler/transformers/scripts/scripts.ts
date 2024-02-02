import * as ts from "typescript";
import { FileTransformationContext, ScriptTransformers, FileDescriptor, ScriptFileDescriptor, HierarchyFacts, ScriptTransformer, Comment } from "../../../types";
import { system } from "../../../system/system";

export const SCRIPT_VRO_GLOBAL = "__global";
export const SCRIPT_VROES_VAR = "VROES";
export const SCRIPT_VROES_MODULE = "VROES";
export const SCRIPT_VROES_CACHE = "__VROES";
export const SCRIPT_HELPER_MODULE = "tslib";
export const SCRIPT_LAZY_IMPORT_NAME = "importLazy";
export const SCRIPT_VRO_MODULE_PACKAGE = "com.vmware.pscoe.library.ecmascript";

// @TODO: This was not needed in the original code
// export const nullScriptContext: ts.TransformationContext = {
// 	getCompilerOptions: notImplemented,
// 	startLexicalEnvironment: noop,
// 	suspendLexicalEnvironment: noop,
// 	resumeLexicalEnvironment: noop,
// 	endLexicalEnvironment: returnUndefined,
// 	hoistFunctionDeclaration: noop,
// 	hoistVariableDeclaration: noop,
// 	requestEmitHelper: noop,
// 	readEmitHelpers: notImplemented,
// 	enableSubstitution: noop,
// 	isSubstitutionEnabled: notImplemented,
// 	onSubstituteNode: notImplemented,
// 	enableEmitNotification: noop,
// 	isEmitNotificationEnabled: notImplemented,
// 	onEmitNode: noop,
// };

/**
 * Transforms a source file or path using provided context and transformers.
 *
 * This TypeScript code is using the `emit` method of the TypeScript program object to generate output files
 * from the source files in the program. The `emit` method traverses the AST of the source files and transforms it into JavaScript code,
 * type declarations (.d.ts files), and source maps (.js.map files).
 *
 * @returns the transformed source text, types text and map text.
 */
export function transformSourceFile(
	sourceFileOrPath: string | ts.SourceFile,
	context: FileTransformationContext,
	transformers?: ScriptTransformers,
	file?: FileDescriptor
): [string, string, string] {
	const program = context.getScriptProgram();
	const sourceFile = typeof sourceFileOrPath === "string"
		? program.getSourceFile(sourceFileOrPath)
		: sourceFileOrPath;
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
			before: [createTransformers(beforeTransformers, program, contextExt)],
			after: [createTransformers(afterTransformers, program, contextExt)],
		});

	emitResult.diagnostics.forEach(d => context.diagnostics.addNative(d));

	return [sourceText, typesText, mapText];
}

/**
 * Creates a transformer factory from a list of transformers.
 *
 * In TypeScript, a transformer is a function that takes a SourceFile and a TransformationContext and returns a transformed SourceFile.
 * Transformers are used to manipulate the AST representation of TypeScript code.
 *
 * @param transformers The list of transformers.
*/
function createTransformers(transformers: ScriptTransformer[], program: ts.Program, contextExt: any): ts.TransformerFactory<ts.SourceFile> {
	return ctx => sourceFile => {
		const scriptContext = Object.assign(contextExt, ctx, {
			typeChecker: program.getTypeChecker(),
		});

		return transformers.reduce((s, t) => t(s, scriptContext), sourceFile);
	};
}

/**
* Removes the trailing source map comment from the source text.
*/
function removeTrailingMapComment(sourceText: string, fileName: string): string {
	if (fileName) {
		var mapFileName = system.changeFileExt(system.basename(fileName), ".js.map");
		if (sourceText.endsWith(`//# sourceMappingURL=${mapFileName}`)) {
			sourceText = sourceText.substring(0, sourceText.lastIndexOf(`//# sourceMappingURL=${mapFileName}`));
		}
	}
	return sourceText;
}


