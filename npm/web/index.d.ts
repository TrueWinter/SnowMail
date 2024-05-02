/* eslint-disable no-unused-vars */
import { type ContactFormProps } from '../react/ContactForm';

export interface Opts extends ContactFormProps {
  element: HTMLElement
}
type OptsWithoutElement = Omit<Opts, 'element'>
export type ServerOpts = Required<Pick<OptsWithoutElement, 'inputs'>> & OptsWithoutElement

export function render(opts: Opts): void;
export function hydrate(opts: ServerOpts): void;
