import { render as preactRender, hydrate as preactHydrate } from 'preact';
import ContactForm from '../react/ContactForm';

/**
 * @param {import('.').Opts} opts
 * @private
 */
export function makeForm(opts) {
  return (
    <ContactForm url={opts.url} id={opts.id} providerProps={opts.providerProps}
      inputs={opts.inputs} handler={opts.handler} afterSubmit={opts.afterSubmit} />
  );
}

/** @param {import('.').Opts} opts  */
export function render(opts) {
  preactRender(makeForm(opts), opts.element);
}

/** @param {import('.').ServerOpts} opts  */
export function hydrate(opts) {
  if (typeof window === 'undefined') {
    throw new Error('This function can only be called on the client');
  }

  preactHydrate(makeForm(opts), opts.element);
}
