/* eslint-disable no-unused-vars */
import { ReactNode } from 'react';
import { UseFormReturnType } from '@mantine/form';
import * as Types from './public';

export type InputTypes = 'TEXT' | 'TEXTAREA' | 'BUTTON' | 'SCRIPT' | 'MULTIPLE' | 'CUSTOM';
export type InputType<T extends InputTypes> =
  T extends 'TEXT' ? Types.TextInput :
  T extends 'TEXTAREA' ? Types.TextAreaInput :
  T extends 'BUTTON' ? Types.ButtonInput :
  T extends 'SCRIPT' ? Types.ScriptInput :
  T extends 'MULTIPLE' ? Types.MultipleInputs :
  T extends 'CUSTOM' ? Types.CustomElementInput :
  never;

interface Props<T extends InputTypes> {
  input: InputType<T>
  form?: UseFormReturnType<any>
}
export type Component<T extends InputTypes> = (props: Props<T>) => ReactNode;
