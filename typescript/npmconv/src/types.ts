/*
 * #%L
 * npmconv
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
