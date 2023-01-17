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
    export function noop(_?: {} | null | undefined): void { }

    export function returnUndefined(): undefined { return undefined; }

    export function notImplemented(): never {
        throw new Error("Not implemented");
    }

    export function copyArray<T>(dest: T[], src: readonly T[], start?: number, end?: number): T[] {
        start = start === undefined ? 0 : start;
        end = end === undefined ? src.length : end;
        for (let i = start; i < end && i < src.length; i++) {
            if (src[i] !== undefined) {
                dest.push(src[i]);
            }
        }
        return dest;
    }

    export function generateElementId(fileType: FileType, path: string): string {
        return system.uuid(path.replace(/\\/g, "/"), getIdHashForFile());

        function getIdHashForFile(): string {
            switch (fileType) {
                case vrotsc.FileType.Workflow:
                    return "0d79ca9f-3e6c-4194-b73c-35eb5ba9cb80";
                case vrotsc.FileType.PolicyTemplate:
                    return "42bf5b9b-20f3-428c-bdf4-d800a7cdc265";
                case vrotsc.FileType.ConfigurationTS:
                case vrotsc.FileType.ConfigurationYAML:
                    return "b93b589d-53ac-48e5-b9ca-59d83447d64c";
                case vrotsc.FileType.Resource:
                    return "89e14cd8-f955-4634-aadf-34306c862737";
                default:
                    throw new Error(`Unimplemented ID Generation type: ${fileType}`);
            }
        }
    }
}