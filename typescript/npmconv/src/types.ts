export enum SourceSpecType {
	Npm = "npm",
	FileSystem = "fs"
}

export interface SourceSpec {
	type: SourceSpecType;
	directory: string;
}

export interface NpmPackageSourceSpec extends SourceSpec {
	type: SourceSpecType.Npm;
	packageName: string;
	packageVersion: string;
}

export interface LocalSourceSpec extends SourceSpec {
	type: SourceSpecType.FileSystem;
}

export interface IncludesSpec {
	sourceGlobs: string[];
	auxGlobs: string[];
}

export interface NpmPackageRef {
	name: string;
	version?: string;
}

export interface MvnPackageRef {
	groupId: string;
	artifactId: string;
	version?: string;
	packaging?: string;
	scope?: string;
}

export interface NpmMvnPackagePair {
	npm: NpmPackageRef;
	mvn: MvnPackageRef;
}

export interface OuputFormatSpec {
	directory: string;
	toolchainVersion: string;
	mvnGroupId: string;
	mvnArtifactId: string;
	mvnVersion?: string;
	mvnDependencies: MvnPackageRef[];
	tsConfigMergeTemplate: Object;
	skipUnmappedNpmDeps: boolean;
}

export interface NpmConverterConfig {
	source: NpmPackageSourceSpec | LocalSourceSpec;
	include: IncludesSpec;
	output: OuputFormatSpec;
}
