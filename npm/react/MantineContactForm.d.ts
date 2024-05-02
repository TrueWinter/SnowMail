/* eslint-disable no-unused-vars */
import { ReactNode } from 'react';
import { InputUnion } from '../types/public';

export interface MantineContactFormProps {
  url: string
  id: string
  inputs?: InputUnion[]
}
export default function MantineContactForm(props: MantineContactFormProps): ReactNode;
