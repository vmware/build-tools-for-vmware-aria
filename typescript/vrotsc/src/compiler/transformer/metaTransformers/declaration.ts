import { FileDescriptor, FileTransformationContext, ScriptFileDescriptor, HierarchyFacts } from "../../../types";
import { system } from "../../../system/system";
import { StringBuilderClass } from "../../../utilities/stringBuilder";

const isKeyword: (name: string) => boolean = require("is-keyword-js");

/**
 * Transforms the content of a file into TypeScript declaration file.
 * @param {FileDescriptor} file - The file to be transformed.
 * @param {FileTransformationContext} context - The context of the file transformation.
 * @returns {Function} - The transform function.
 */
export function getDeclarationTransformer(file: FileDescriptor, context: FileTransformationContext): () => void {
	return transform;

	function transform() {
		const content = system.readFile(file.filePath);
		const targetDtsFilePath = system.resolvePath(context.outputs.types, file.relativeFilePath);
		context.writeFile(targetDtsFilePath, content);
	}
}

/**
 * Generates index TypeScript declaration files for a given path.
 *
 * Brief:
 * This function is responsible for creating an index.d.ts file in each directory
 * of the project that exports all the TypeScript declarations of that directory.
 *
 * Expanded:
 * This is useful in large projects where you want to import multiple declarations
 * from a single directory. Instead of importing each declaration individually,
 * you can import them all at once from the index.d.ts file.
 *
 * @param {FileTransformationContext} context - The context of the file transformation.
 * This context object contains information about the current state of the file
 * transformation, such as the path of the file being transformed and the output
 * directory for the transformed files.
 */
export function generateIndexTypes(context: FileTransformationContext): void {
	if (system.directoryExists(context.outputs.types)) {
		generateIndexTypesForPath(context.outputs.types);
	}

	function generateIndexTypesForPath(path: string): boolean {
		if (system.fileExists(system.joinPath(path, "index.d.ts"))) {
			return true;
		}
		const relativeDirPath = system.relativePath(context.outputs.types, path);
		const importFiles: string[] = [];
		const importGlobalFiles: string[] = [];
		const importFolders: string[] = [];

		system.getFiles(path).forEach(filePath => {
			const fileName = system.basename(filePath);
			if (fileName.toLowerCase().endsWith(".d.ts")) {
				const importName = system.changeFileExt(fileName, "", [".d.ts"]);
				const sourceFilePath = system.joinPath(context.rootDir, relativeDirPath, `${importName}.ts`);
				const file = context.getFile(sourceFilePath) as ScriptFileDescriptor;
				if (file) {
					if (file.hierarchyFacts & HierarchyFacts.ContainsGlobalNamespace) {
						importGlobalFiles.push(importName);
					}
					else {
						importFiles.push(importName);
					}
				}
			}
		});

		system.getDirectories(path).forEach(dirName => {
			if (generateIndexTypesForPath(system.joinPath(path, dirName))) {
				importFolders.push(dirName);
			}
		});

		if (importFiles.length || importGlobalFiles.length || importFolders.length) {
			const dtsContent = printIndexTypes(importFiles, importGlobalFiles, importFolders);
			const dtsFilePath = system.resolvePath(path, "index.d.ts");
			context.writeFile(dtsFilePath, dtsContent);
			return true;
		}

		return false;
	}

	function printIndexTypes(importFiles: string[], importGlobalFiles: string[], importFolders: string[]): string {
		const stringBuilder = new StringBuilderClass;
		const exportNames: string[] = [];

		importGlobalFiles.forEach(name => {
			stringBuilder.append(`import "./${name}";`).appendLine();
		});

		importFiles.forEach(name => {
			const safeName = getSafeExportName(name);
			exportNames.push(safeName);
			stringBuilder.append(`import * as ${safeName} from "./${name}";`).appendLine();
		});

		importFolders.forEach(name => {
			const safeName = getSafeExportName(name);
			exportNames.push(safeName);
			stringBuilder.append(`import * as ${safeName} from "./${name}/index";`).appendLine();
		});

		if (exportNames.length) {
			stringBuilder.append(`export { ${exportNames.join(", ")} };`).appendLine();
		}

		return stringBuilder.toString();
	}

	function getSafeExportName(name: string): string {
		let safeName = name.replace(/[^\$\_\w]/g, "");
		safeName = isNaN(parseInt("" + safeName.charAt(0))) && !isKeyword(safeName) ? safeName : ("_" + safeName);
		return safeName;
	}
}

/**
 * Checks if a TypeScript declaration file can be created for a given file.
 * @param {FileDescriptor} file - The file to be checked.
 * @param {string} rootDir - The root directory of the project.
 * @returns {boolean} - True if a declaration file can be created, false otherwise.
 */
export function canCreateDeclarationForFile(file: FileDescriptor, rootDir: string): boolean {
	if (!file.fileName.toLowerCase().endsWith(".ts")) {
		return false;
	}

	if (file.fileName.toLowerCase().endsWith(".helper.ts")) {
		return false;
	}

	if (system.fileExists(system.changeFileExt(file.filePath, ".d.ts"))) {
		return false;
	}

	let stop = false;
	let relativeDirPath = file.relativeDirPath;
	do {
		const dirPath = relativeDirPath ? system.joinPath(rootDir, relativeDirPath) : rootDir;
		if (system.fileExists(system.joinPath(dirPath, "index.d.ts"))) {
			return false;
		}

		if (relativeDirPath) {
			relativeDirPath = relativeDirPath.indexOf(system.pathSeparator) > -1 ? system.dirname(relativeDirPath) : undefined;
		}
		else {
			stop = true;
		}
	}
	while (!stop);

	return true;
}
