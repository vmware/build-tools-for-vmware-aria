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
 * Formats the position of a WF element for XML output
 * @param {[number, number]} position
 * @param {[number, number]} [offset = [0, 0]]
 * @returns {string} <position x="${formattedPosX}" y="${formattedPosY}" />
 * where the coordinates are X and Y rounded to .0
 */
export function formatPosition(position: [number, number], offset: [number, number] = [0,0]): string {
	const coords = position.map((num, ind) => Math.round((num || 0) + (offset?.[ind] || 0) ).toString() + ".0");
	return `<position x="${coords[0]}" y="${coords[1]}" />`;
}
