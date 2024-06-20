/* eslint-disable no-unused-vars */
import { ReactNode } from 'react';
import { InputUnion } from '../types/public';

export interface HandlerReturnValue {
  success: boolean
  error?: string
}

export interface MantineContactFormProps {
  url: string
  id: string
  inputs?: InputUnion[]
  handler?: (values: Record<string, any>) => Promise<HandlerReturnValue>
  afterSubmit?: (scroll: () => void) => void
}

export default function MantineContactForm(props: MantineContactFormProps): ReactNode;
