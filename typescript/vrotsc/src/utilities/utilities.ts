/*-
 * #%L
 * vrotsc
 * %%
 * Copyright (C) 2023 - 2024 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import { system } from "../system/system";
import { FileType } from "../types";

/**
* Generates a unique id for a given file path.
* The id is generated using the uuidv3 library.
*
* uuidv3 requires a name and a namespace.
* We use the name to generate a unique id for the given namespace.
* Example:
* ````
* generateElementId(FileType.Workflow, "path/to/workflow.yaml")
* returns: 9aced742-20c5-3b79-a337-94b13ad83d72
* ````
* Every time the same path is passed, the same id will be returned.
* the defined namespaces are hardcoded and are used to generate the id.
*
* @throws {Error} if the file type is not implemented.
*/
export function generateElementId(fileType: FileType, path: string): string {
	return system.uuid(path.replace(/\\/g, "/"), getIdHashForFile());

	function getIdHashForFile(): string {
		switch (fileType) {
			case FileType.Workflow:
				return "0d79ca9f-3e6c-4194-b73c-35eb5ba9cb80";
			case FileType.PolicyTemplate:
				return "42bf5b9b-20f3-428c-bdf4-d800a7cdc265";
			case FileType.ConfigurationTS:
			case FileType.ConfigurationYAML:
				return "b93b589d-53ac-48e5-b9ca-59d83447d64c";
			case FileType.Resource:
				return "89e14cd8-f955-4634-aadf-34306c862737";
			default:
				throw new Error(`Unimplemented ID Generation type: ${fileType}`);
		}
	}
}
