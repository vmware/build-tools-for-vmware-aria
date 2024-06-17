export declare function tail<T, U>(arr1: T[], arr2: U[]): Array<T | U>;
type Range = [start: number, end: number];
export declare function foo(x: Range): void;
export type VerticalAlignment = "top" | "middle" | "bottom";
export type HorizontalAlignment = "left" | "center" | "right";
export declare function setAlignment(value: `${VerticalAlignment}-${HorizontalAlignment}`): void;
export {};
