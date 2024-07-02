//Variadic Tuple Types
export function tail<T, U>(arr1: T[], arr2: U[]): Array<T | U>;
export function tail(arg) {
    const [_, ...result] = arg;
    return result
}
// Labeled Tuple Elements
type Range = [start: number, end: number];
export function foo(x: Range): void {
    // ...
}
// Short-Circuiting Assignment Operators
let a = undefined;
const b = 3;

a ||= b;
a &&= b;
a ??= b;

export type VerticalAlignment = "top" | "middle" | "bottom";
export type HorizontalAlignment = "left" | "center" | "right";

// Takes
//   | "top-left"    | "top-center"    | "top-right"
//   | "middle-left" | "middle-center" | "middle-right"
//   | "bottom-left" | "bottom-center" | "bottom-right"
export declare function setAlignment(value: `${VerticalAlignment}-${HorizontalAlignment}`): void;


let as = { prop: "hello" } as const;

