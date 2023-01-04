export default class Set<T extends string | number> {
	private size = 0;
	private items: { [name: string]: boolean } = {};

	constructor(values?: ReadonlyArray<T> | null) {
		if (values) {
			for (let value of values) {
				this.items[value] = true;
				this.size++;
			}
		}
	}

	entries(): [any, any][] {
		let entries = [];

		for (let value in this.items) {
			entries.push([value, value]);
		}

		return entries;
	}

	keys(): any[] {
		return this.values();
	}

	values(): any[] {
		let values = [];

		for (let value in this.items) {
			values.push(value);
		}

		return values;
	}

	has(value): boolean {
		return this.items.hasOwnProperty(value);
	}

	add(value): any {
		this.items[value] = true;
		return this;
	}

	delete(value: any): boolean {
		let exist = this.items.hasOwnProperty(value);
		if (exist) {
			this.size--;
			delete this.items[value];
		}

		return exist;
	}

	clear(): void {
		this.size = 0;
		this.items = {};
	}

	forEach(callbackfn: (value, value2, set: Set<any>) => void): void {
		for (let value in this.items) {
			callbackfn(value, value, this);
		}
	}
}
