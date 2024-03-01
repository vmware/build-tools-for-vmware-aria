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
/**
 * Copies elements from a source array to a destination array.
 * @param dest - The destination array.
 * @param src - The source array.
 * @param start - The starting index from which to begin copying. Defaults to 0.
 * @param end - The ending index at which to stop copying. Defaults to the length of the source array.
 * @returns The destination array after copying the elements.
 */
export function copyArray<T>(dest: T[], src: readonly T[], start: number = 0, end: number = src.length): T[] {
	for (let i = start; i < end && i < src.length; i++) {
		if (src[i] !== undefined) {
			dest.push(src[i]);
		}
	}
	// Return the destination array
	return dest;
}
