import * as ts from "typescript";
import { FileDescriptor, FileTransformationContext, DiagnosticCategory } from "../../../types";
import { system } from "../../../system/system";
import { transformSourceFile } from "../scripts/scripts";
import { collectFactsBefore } from "../metaTransformers/facts";
import { transformShimsBefore, transformShims } from "../codeTransformers/shims";
import { remediateTypeScript } from "../codeTransformers/remediate";
import { transformModuleSystem } from "../codeTransformers/modules";
import { isIdentifier } from "../helpers/node";

export function getTestTransformer(file: FileDescriptor, context: FileTransformationContext) {
	const sourceFile = ts.createSourceFile(file.filePath, system.readFile(file.filePath).toString(), ts.ScriptTarget.Latest, true);
	context.sourceFiles.push(sourceFile);
	return transform;

	function transform() {
		const [sourceFileText] = transformSourceFile(
			sourceFile,
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
					validateJasmineTest,
				],
			});

		let targetFilePath = system.changeFileExt(
			system.resolvePath(context.outputs.tests, file.relativeFilePath),
			"",
			[".test.js", ".test.ts"]);

		if (!targetFilePath.endsWith("Test")) {
			targetFilePath += "Test";
		}

		targetFilePath += ".js";

		context.writeFile(targetFilePath, sourceFileText);
	}

	function validateJasmineTest(sourceFile: ts.SourceFile): ts.SourceFile {
		if (!sourceFile.statements.some(isDescribeCall)) {
			context.diagnostics.add({
				file: system.relativePath(system.getCurrentDirectory(), sourceFile.fileName),
				messageText: `Jasmine test file should have describe function`,
				category: DiagnosticCategory.Error,
			});
		}
		return sourceFile;
	}

	function isDescribeCall(statement: ts.Statement): boolean {
		if (statement.kind !== ts.SyntaxKind.ExpressionStatement) {
			return false;
		}

		const { expression } = statement as ts.ExpressionStatement;

		if (expression.kind !== ts.SyntaxKind.CallExpression) {
			return false;
		}

		const { expression: callExp, arguments: callArgs } = expression as ts.CallExpression;

		if (!isIdentifier(callExp, "describe")) {
			return false;
		}

		if (callArgs.length !== 2) {
			return false;
		}

		if (callArgs[0].kind !== ts.SyntaxKind.StringLiteral) {
			return false;
		}

		if (callArgs[1].kind !== ts.SyntaxKind.ArrowFunction && callArgs[1].kind !== ts.SyntaxKind.FunctionExpression) {
			return false;
		}

		return true;
	}
}
