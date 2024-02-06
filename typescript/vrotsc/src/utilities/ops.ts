export function noop(_?: {} | null | undefined): void { }

export function returnUndefined(): undefined { return undefined; }

export function notImplemented(): never {
	throw new Error("Not implemented");
}
