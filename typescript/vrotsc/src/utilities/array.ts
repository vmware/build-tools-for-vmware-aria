/**
 * Copies elements from a source array to a destination array.
 * @param dest - The destination array.
 * @param src - The source array.
 * @param start - The starting index from which to begin copying. Defaults to 0.
 * @param end - The ending index at which to stop copying. Defaults to the length of the source array.
 * @returns The destination array after copying the elements.
 */
export function copyArray<T>(dest: T[], src: readonly T[], start: number = 0, end: number = src.length): T[] {
	for (let i = start; i < end && i < src.length; i++) {
		if (src[i] !== undefined) {
			dest.push(src[i]);
		}
	}
	// Return the destination array
	return dest;
}
