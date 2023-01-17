
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
