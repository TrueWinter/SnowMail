import { createRoot } from 'react-dom/client';
import ContactForm from '../react/ContactForm';

/** @param {import('.').Opts} opts  */
export function render(opts) {
  const root = createRoot(opts.element);
  root.render(<ContactForm url={opts.url} id={opts.id} />);
}
