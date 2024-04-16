import { UseFormReturnType } from '@mantine/form';
import { FormEvent } from 'react';

export function onSubmit(e: FormEvent<HTMLFormElement>, form: UseFormReturnType<any, any>) {
  if (form.validate().hasErrors) {
    e.preventDefault();
  }
}
