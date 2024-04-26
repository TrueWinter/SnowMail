/* eslint-disable no-unused-vars */
import { type ContactFormProps } from '../react/ContactForm';

export interface Opts extends ContactFormProps {
  element: HTMLElement
}
export function render(opts: Opts): void;
