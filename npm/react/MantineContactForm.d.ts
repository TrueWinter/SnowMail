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
  defaults?: Record<string, any>
  handler?: (values: Record<string, any>) => Promise<HandlerReturnValue>
  onChange?: (values: Record<string, any>) => void
  afterSubmit?: (scroll: () => void) => void
}

export default function MantineContactForm(props: MantineContactFormProps): ReactNode;
