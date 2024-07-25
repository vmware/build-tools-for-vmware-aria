/*-
 * #%L
 * ecmascript
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
export default class Set<T extends string | number> {
	private size: number = 0;
	private items: { [name: string]: boolean } = {};

	constructor(values?: ReadonlyArray<T> | null) {
		if (values) {
			for (let value of values) {
				this.items[value] = true;
				this.size++;
			}
		}
	}

	public entries(): [any, any][] {
		let entries = [];

		for (let value in this.items) {
			entries.push([value, value]);
		}

		return entries;
	}

	public keys(): any[] {
		return this.values();
	}

	public values(): any[] {
		let values = [];

		for (let value in this.items) {
			values.push(value);
		}

		return values;
	}

	public has(value: any): boolean {
		return this.items.hasOwnProperty(value);
	}

	public add(value: any): any {
		let exist = this.has(value);
		this.items[value] = true;

		if (exist) {
			return this;
		}
		this.size++;

		return this;
	}

	public delete(value: any): boolean {
		let exist = this.has(value);
		if (exist) {
			this.size--;
			delete this.items[value];
		}

		return exist;
	}

	public clear(): void {
		this.size = 0;
		this.items = {};
	}

	public forEach(callbackFunction: (value: any, value2: any, set: Set<any>) => void): void {
		for (let value in this.items) {
			callbackFunction(value, value, this);
		}
	}
}
