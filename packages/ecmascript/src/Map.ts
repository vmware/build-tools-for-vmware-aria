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
	private size = 0;
	private items: { [name: string]: any } = {};

	constructor(values?: ReadonlyArray<[K, V]> | null) {
		if (values) {
			for (let keyValue of values) {
				this.items[keyValue[0]] = keyValue[1];
				this.size++;
			}
		}
	}

	entries(): [any, any][] {
		let entries = [];

		for (let key in this.items) {
			entries.push([key, this.items[key]]);
		}

		return entries;
	}

	keys(): any[] {
		let keys = [];

		for (let key in this.items) {
			keys.push(key);
		}

		return keys;
	}

	values(): any[] {
		let values = [];

		for (let key in this.items) {
			values.push(this.items[key]);
		}

		return values;
	}

	has(key): boolean {
		return this.items.hasOwnProperty(key);
	}

	get(key): any {
		return this.items[key];
	}

	set(key, value): any {
		let exist = this.items.hasOwnProperty(key);
		if (exist) {
			this.size++;
		}

		this.items[key] = value;
		return this;
	}

	delete(key): boolean {
		let exist = this.items.hasOwnProperty(key);
		if (exist) {
			this.size--;
			delete this.items[key];
		}

		return exist;
	}

	clear(): void {
		this.size = 0;
		this.items = {};
	}

	forEach(callbackfn: (value, key, map: Map<any, any>) => void): void {
		for (let key in this.items) {
			callbackfn(this.items[key], key, this);
		}
	}
}
