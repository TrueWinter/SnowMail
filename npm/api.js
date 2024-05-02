/**
 * Fetches a form definition from SnowMail
 * @param {string} url The SnowMail instance URL
 * @param {string} id The form ID
 * @example getForm('https://snowmail.example.com', '6626bd40e3fe51617ad2cb33')
 * @returns {Promise<import('./types/public').InputUnion[]>} The inputs for this form
 */
export async function getForm(url, id) {
  const resp = await fetch(`${url}/public-api/forms/${id}`);
  if (resp.status !== 200) {
    throw new Error(`Received non-200 status code from SnowMail: ${resp.status}`);
  }

  const json = await resp.json();
  return json.inputs;
}
