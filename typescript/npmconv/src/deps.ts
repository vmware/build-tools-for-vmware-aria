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
import * as npm from "npm";
import * as t from "./types";
import * as fs from "fs-extra";
import * as path from 'path';

export class NpmProxy {
	constructor() { }

	async init() {
		return new Promise((resolve, reject) => {
			console.debug("Initializing npm config...");
			npm.load({ color: true }, (err, result) => {
				err ? reject(err) : resolve(result);
			});
		});
	}

	async view(packageName: string): Promise<any> {
		const response = await new Promise((resolve, reject) =>
			npm.commands.view([packageName],
				(err, res) => err ? reject(err) : resolve(res)));

		return Object.keys(response).map(key => {
			// strip the root level
			return response[key];
		})[0];
	}

	async viewLocal(directory: string): Promise<any> {
		const sourceExists = await fs.pathExists(directory);
		if (!sourceExists) {
			throw new Error(`Local package does not exist in ${directory}`);
		}
		return await fs.readJson(path.join(directory, 'package.json'));
	}

	async install(toDir: string): Promise<string[]> {
		const resp = await new Promise((resolve, reject) =>
			npm.commands.install([toDir],
				(err, res) => err ? reject(err) : resolve(res)));

		return (<Array<Array<string>>>resp).map(entry => {
			// strip only dependency name @ version part
			return entry[0];
		});
	}
}

export class DependenciesMapper {
	private readonly mappingPairs: t.NpmMvnPackagePair[];

	constructor(mappingRefs: string[] = [], public readonly defaultMvnGroupId) {
		this.mappingPairs = mappingRefs.map(pairRefsMap => {
			let [npmRef, mvnRef] = pairRefsMap.split("^");

			let npmPack = DependenciesMapper.parseNpmRef(npmRef);
			let mvnPack = DependenciesMapper.parseMvnRef(mvnRef, defaultMvnGroupId, npmPack.name, npmPack.version);

			console.debug(`Building a mapping pair...`);
			console.debug(`NPM: ${JSON.stringify(npmPack)}`);
			console.debug(`MVN: ${JSON.stringify(mvnPack)}`);

			return <t.NpmMvnPackagePair>{
				npm: npmPack,
				mvn: mvnPack
			};
		});
	}

	parseNpmRefs(npmRefs: string[]): t.NpmPackageRef[] {
		return npmRefs.map(npmRef => DependenciesMapper.parseNpmRef(npmRef));
	}

	parseMvnRefs(mvnRefs: string[], fallbackGroupId?: string, fallbackVersion?: string, fallbackPackaging?: string, fallbackScope?: string): t.MvnPackageRef[] {
		return mvnRefs.map(mvnRef => DependenciesMapper.parseMvnRef(mvnRef, fallbackGroupId, undefined, fallbackVersion, fallbackPackaging, fallbackScope));
	}

	guessMapNpmPack(npmRefs: t.NpmPackageRef[]): t.NpmMvnPackagePair[] {
		return npmRefs.map(npmPack => {
			return {
				npm: npmPack,
				mvn: DependenciesMapper.parseMvnRef(undefined, this.defaultMvnGroupId, npmPack.name, npmPack.version)
			};
		});
	}

	mapPackages(npmPacks: t.NpmPackageRef[]): { mapped: t.NpmMvnPackagePair[]; unmapped: t.NpmPackageRef[] } {
		return npmPacks.reduce(
			(mapping, npmPack) => {
				let pair = this.mappingPairs.filter(
					pair => npmPack.name === pair.npm.name && (!pair.npm.version || pair.npm.version === npmPack.version || !npmPack.version)
				)[0];

				if (pair) {
					mapping.mapped.push(pair);
				} else {
					mapping.unmapped.push(npmPack);
				}
				return mapping;
			},
			{ mapped: [], unmapped: [] }
		);
	}

	private static parseNpmRef(npmRef: string): t.NpmPackageRef {
		const isScoped = npmRef.includes('/');

		// e.g.: @pscoe-vmware/loging@2.3.4
		if (isScoped) {
			const [scope, nameVer] = npmRef.split('/');
			const [npmName, npmVer] = nameVer.split("@");
			return { name: `${scope}/${npmName}`, version: npmVer };
		} else {
			const [npmName, npmVer] = npmRef.split("@");
			return { name: npmName, version: npmVer };
		}
	}

	private static parseMvnRef(
		mvnRef: string = "",
		fallbackGroupId?: string,
		fallbackArtifactId?: string,
		fallbackVersion?: string,
		fallbackPackaging?: string,
		fallbackScope?: string
	): t.MvnPackageRef {
		let [groupId, artifactId, version, packaging, scope] = (mvnRef || "").split(":");

		if (!groupId) {
			if (!fallbackGroupId) {
				throw `Dependency is missing groupId and no conventionGroupId is not set: ${mvnRef}`;
			}

			console.debug(`Using ${fallbackGroupId} for groupId of dependency: ${mvnRef}`);
			groupId = fallbackGroupId;
		}

		if (!artifactId) {
			if (!fallbackArtifactId) {
				throw `Dependency is missing required artifactId: ${mvnRef}`;
			}

			console.debug(`Using ${fallbackArtifactId} for artifactId of dependency: ${mvnRef}`);
			artifactId = fallbackArtifactId;
		}

		if (!version) {
			if (fallbackVersion) {
				console.debug(`Using ${fallbackVersion} for version of dependency: ${mvnRef}`);
				version = fallbackVersion;
			} else {
				console.debug(`Using RELEASE for version of dependency: ${mvnRef}`);
				version = "RELEASE";
			}
		}

		if (!packaging) {
			if (fallbackPackaging) {
				console.debug(`Using ${fallbackPackaging} for type of dependency: ${mvnRef}`);
				packaging = fallbackPackaging;
			} else {
				console.debug(`Using 'package' for type of dependency: ${mvnRef}`);
				packaging = "package";
			}
		}

		if (!scope) {
			if (fallbackPackaging) {
				console.debug(`Using ${fallbackPackaging} for scope of dependency: ${mvnRef}`);
				scope = fallbackScope;
			}
		}

		return {
			groupId,
			artifactId,
			version,
			packaging,
			scope
		};
	}
}
