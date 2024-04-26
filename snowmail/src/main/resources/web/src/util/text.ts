export function toTitleCase(str: string) {
  if (!str) return '';
  const stringParts = str.toLowerCase().split(' ');
  const output = [];

  stringParts.forEach((s) => {
    output.push(s.charAt(0).toUpperCase() + s.substring(1).toLowerCase());
  });

  return output.join(' ');
}
