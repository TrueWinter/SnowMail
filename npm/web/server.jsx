import { render } from 'preact-render-to-string';
import ContactForm from '../react/ContactForm';

/** @param {import('.').ServerOpts} opts  */
export async function renderStatic(opts) {
  if (typeof window !== 'undefined') {
    throw new Error('This function can only be called on the server');
  }

  return render(<ContactForm url={opts.url} id={opts.id} providerProps={opts.providerProps}
    inputs={opts.inputs} />);
}
