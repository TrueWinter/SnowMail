/* eslint-disable no-unused-vars */
import { ReactNode } from 'react';
import { type MantineProviderProps } from '@mantine/core';
import { type MantineContactFormProps } from './MantineContactForm';

export interface ContactFormProps extends MantineContactFormProps {
  providerProps?: MantineProviderProps
}
export default function ContactForm(props: ContactFormProps): ReactNode;
