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

export default class Map<K extends (string | number), V> {
	private size: number = 0;
	private items: { [name: string]: any } = {};

	constructor(values?: ReadonlyArray<[K, V]> | null) {
		if (values) {
			for (let keyValue of values) {
				this.items[keyValue[0]] = keyValue[1];
				this.size++;
			}
		}
	}

	public entries(): [any, any][] {
		let entries = [];

		for (let key in this.items) {
			entries.push([key, this.items[key]]);
		}

		return entries;
	}

	public keys(): any[] {
		let keys = [];

		for (let key in this.items) {
			keys.push(key);
		}

		return keys;
	}

	public values(): any[] {
		let values = [];

		for (let key in this.items) {
			values.push(this.items[key]);
		}

		return values;
	}

	public has(key: any): boolean {
		return this.items.hasOwnProperty(key);
	}

	public get(key: any): any {
		return this.items[key];
	}

	public set(key: any, value: any): any {
		this.items[key] = value;
		this.size++;

		return this;
	}

	public delete(key: any): boolean {
		let exist = this.has(key);
		if (exist) {
			this.size--;
			delete this.items[key];
		}

		return exist;
	}

	public clear(): void {
		this.size = 0;
		this.items = {};
	}

	public forEach(callbackFunction: (value: any, key: any, map: Map<any, any>) => void): void {
		for (let key in this.items) {
			callbackFunction(this.items[key], key, this);
		}
	}
}
