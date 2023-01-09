function getPadString(sourceLength: number, targetLength: number, padString?: string): string {
	if (sourceLength > targetLength) {
		return "";
	}
	if (padString != null) {
		padString = "" + padString;
	}
	if (padString == null || !padString.length) {
		padString = " ";
	}
	let pad = "";
	const len = targetLength - sourceLength;
	for (let i = 0; i < len; i++) {
		pad += padString[i % padString.length];
	}

	return pad;
}

export default class Shims {
	static stringStartsWith(str: string, search: string, pos?: number): boolean {
		pos = !pos || pos < 0 ? 0 : +pos;
		return str.substring(pos, pos + search.length) === search;
	}

	static stringEndsWith(str: string, search: string, strLen?: number): boolean {
		if (strLen === undefined || strLen > this.length) {
			strLen = str.length;
		}

		return str.substring(strLen - search.length, strLen) === search;
	}

	static stringIncludes(str: string, search: string, start?: number): boolean {
		if (typeof start !== "number") {
			start = 0;
		}

		if (start + search.length > str.length) {
			return false;
		}
		else {
			return str.indexOf(search, start) !== -1
		}
	}

	static stringRepeat(str: string, count: number): string {
		if (str == null) {
			throw new TypeError(`Can't convert ${str} to object`);
		}

		let result = "";
		while (0 < count--) {
			result = result.concat(str);
		}

		return result;
	}

	static stringPadStart(str: string, targetLength: number, padString?: string): string {
		return getPadString(str.length, targetLength, padString) + str;
	}

	static stringPadEnd(str: string, targetLength: number, padString?: string): string {
		return str + getPadString(str.length, targetLength, padString);
	}

	static arrayFind = function <T>(array: T[], predicate: (value: T, index: number, obj: T[]) => boolean): T | undefined {
		for (let i = 0; i < array.length; i++) {
			if (predicate(array[i], i, array)) {
				return array[i];
			}
		}
	}

	static arrayFindIndex = function <T>(array: T[], predicate: (value: T, index: number, obj: T[]) => boolean): number {
		for (let i = 0; i < array.length; i++) {
			if (predicate(array[i], i, array)) {
				return i;
			}
		}

		return -1;
	}

	static arrayFill = function <T>(array: T[], value: T, start?: number, end?: number): T[] {
		if (end < 0) {
			end = array.length + end * -1;
		}

		for (let i = start; i < end; i++) {
			array[i] = value;
		}

		return array;
	}

	static arrayFrom(arrayLike: ArrayLike<any>, mapfn?: (v: any, k: number) => any): any[] {
		let array = <any[]>arrayLike;

		if (mapfn) {
			array = array.map(mapfn);
		}

		return array;
	}

	static arrayOf(): any[] {
		return Array.prototype.slice.call(arguments);
	}

	static objectAssign(target): any {
		if (target === null || target === undefined) {
			throw new TypeError("Cannot convert undefined or null to object");
		}

		for (var i = 1, len = arguments.length; i < len; i++) {
			var source = arguments[i];
			for (var p in source) {
				if (Object.prototype.hasOwnProperty.call(source, p)) {
					target[p] = source[p];
				}
			}
		}
		return target;
	}

	static objectValues(target): any[] {
		return Object.keys(target).map(key => target[key]);
	}

	static spreadArrays() {
		var size = 0;
		for (var i = 0, len = arguments.length; i < len; i++) {
			size += arguments[i].length;
		}
		var result = Array(size);
		for (var k = 0, i = 0; i < len; i++) {
			for (var a = arguments[i], j = 0, jl = a.length; j < jl; j++, k++) {
				result[k] = a[j];
			}
		}
		return result;
	}
}

