export function deepCopyObjArr<T>(arr: T[]): T[] {
  // return arr.map((o) => ({ ...o }));
  return structuredClone(arr);
}
