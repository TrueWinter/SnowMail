import { render } from 'preact-render-to-string';
import { makeForm } from '.';

/** @param {import('.').ServerOpts} opts  */
export async function renderStatic(opts) {
  if (typeof window !== 'undefined') {
    throw new Error('This function can only be called on the server');
  }

  return render(makeForm(opts));
}
